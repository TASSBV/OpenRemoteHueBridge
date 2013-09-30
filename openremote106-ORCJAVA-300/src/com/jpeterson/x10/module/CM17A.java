/*
 * Copyright (C) 1999  Jesse E. Peterson
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 *
 */

package com.jpeterson.x10.module;

import java.util.*;
import gnu.io.*;
import com.jpeterson.util.Condition;
import com.jpeterson.x10.ControlEvent;
import com.jpeterson.x10.SerialGateway;
import com.jpeterson.x10.Transmitter;
import com.jpeterson.x10.TransmitterEvent;
import com.jpeterson.x10.TransmitterListener;
import com.jpeterson.x10.Gateway;
import com.jpeterson.x10.GatewayException;
import com.jpeterson.x10.GatewayListener;
import com.jpeterson.x10.GatewayStateError;
import com.jpeterson.x10.event.*;

/**
 * Gateway to X10 CM17A serial interface unit. The CM17A is purely
 * a transmitter of X10Events.  It is a very small device that connects to
 * a serial port and transmitts X10 commands via an RF link to an X10
 * RF receiver.  It is only able to send On, Off, Dim, and Bright commands.
 * The Dim and Bright commands can only be sent in multiples of 5%.
 *
 * The CM17A is controlled by toggling the UART's RTS and DTR signals. Because
 * of this fact, the CM17A device provides a passthrough capability, allowing
 * a serial device to be connected to the CM17A.
 *
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class CM17A extends SerialGateway implements
    Transmitter, Runnable
{
    private transient CommPortIdentifier portId;
    private transient SerialPort serialPort;

    private transient Thread processThread;
//    private boolean stopRequested;
    private transient Vector<CM17AStandardTransmission> transmissionQueue;
    private transient Condition shouldProcess;

    private transient AddressEvent currentAddress;

    /**
     * Construct a new CM17A object.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public CM17A()
    {
        super();
        setPortName("COM2");

        shouldProcess = new Condition(false);
        transmissionQueue = new Vector<CM17AStandardTransmission>();
        setGatewayState(Transmitter.QUEUE_EMPTY);
    }

    /**
     * Encapsulates state transition rules.  Only implements Transmitter
     * specific states.  Lets parent's stateTransition handle the generic
     * gateway states.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void stateTransition(long state)
    {
        int id = 0;
        long oldGatewayState, newGatewayState;
        boolean topOfQueueChanged = false;
        oldGatewayState = getGatewayState();
        newGatewayState = oldGatewayState;

        if (Transmitter.QUEUE_EMPTY == state)
        {
            id = TransmitterEvent.QUEUE_EMPTIED;
            newGatewayState &= ~Transmitter.QUEUE_NOT_EMPTY;
            newGatewayState |= state;
            topOfQueueChanged = false;
        }
        else if (Transmitter.QUEUE_NOT_EMPTY == state)
        {
            id = TransmitterEvent.QUEUE_UPDATED;
            newGatewayState &= ~Transmitter.QUEUE_EMPTY;
            newGatewayState |= state;
            topOfQueueChanged = true;
        }
        else
        {
            super.stateTransition(state);
            return;
        }

        if (newGatewayState != oldGatewayState)
        {
            setGatewayState(newGatewayState);
            fireControlEvent(new TransmitterEvent(this, id, topOfQueueChanged,
                             oldGatewayState,
                             newGatewayState));
        }
    }

    /**
     * Implementation of Transmitter. Other software components
     * can send X10 events to the CM17A to have sent through the X10
     * protocol to X10 devices on the power line network.
     *
     * The CM17A only provides the ability to transmit a subset of the
     * available X10 events. It is only able to transmit On, Off, Dim, and
     * Bright commands. The device is also only able to address one
     * device at a time. The object will only maintain one AddressEvent
     * at a time. Meaning, if the following sequence is transmitted, A1, A2,
     * On, the command is processed thus.  The AddressEvent A1 is stored as
     * the "current" device. The "current" device is immediately replaced
     * with A2. Then, when the On event is seen, the device transmitts the
     * command A2 ON to the X10 network, turning A2 on. The On and Off
     * commands result in the "current" device being sent as an
     * Address X10 event, followed by the associated On or Off X10
     * Function event. The Dim and Bright functions do not result in a
     * device being addressed. Therefore, they will act only on previously
     * addressed devices. This means that you will need to issue and On
     * command, which will address the device, followed by a Dim or Bright
     * command. Also, the Dim and Bright command are only sent in increments
     * of 5%, so multiple commands are issued to make up the desired amount
     * of the Dim or Bright Function event. All other events are ignored
     * by this device.
     *
     * @param evt X10 event to transmit.  Should be either an AddressEvent
     *        or an OnEvent, OffEvent, DimEvent, or BrightEvent. All other
     *        events are ignored.
     * @exception GatewayStateError if called for a transmitter in the
     *            DEALLOCATED or DEALLOCATING_RESOURCES states
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void transmit(X10Event evt) throws GatewayStateError
    {
        // If deallocating resouces, throw an error
        if (testGatewayState(Gateway.DEALLOCATING_RESOURCES |
                             Gateway.DEALLOCATED))
        {
            throw new GatewayStateError("Can not transmit.  Transmitter is currently in the DEALLOCATING_RESOURCES or DEALLOCATED state.");
        }

        try
        {
            waitGatewayState(Gateway.ALLOCATED);
        }
        catch (InterruptedException e)
        {
            throw new GatewayStateError("Caught InterruptedException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            throw new GatewayStateError("Caught IllegalArgumentException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }

        if (evt instanceof AddressEvent)
        {
            currentAddress = (AddressEvent)evt;
        }
        else if ((evt instanceof OnEvent) || (evt instanceof OffEvent))
        {
            if (currentAddress != null)
            {
                if (currentAddress.getHouseCode() ==
                    ((FunctionEvent)evt).getHouseCode())
                {
                    CM17AStandardTransmission event;
                    if (evt instanceof OnEvent)
                    {
                        event = new CM17AStandardTransmission(currentAddress,
                                (OnEvent)evt);
                    }
                    else
                    {
                        event = new CM17AStandardTransmission(currentAddress,
                                (OffEvent)evt);
                    }
                    transmissionQueue.addElement(event);
                    stateTransition(Transmitter.QUEUE_NOT_EMPTY);
                    shouldProcess.setTrue();
                }
            }
        }
        else if ((evt instanceof DimEvent) || (evt instanceof BrightEvent))
        {
            CM17AStandardTransmission event;
            if (evt instanceof DimEvent)
            {
                event = new CM17AStandardTransmission((DimEvent)evt);
            }
            else
            {
                event = new CM17AStandardTransmission((BrightEvent)evt);
            }
            transmissionQueue.addElement(event);
            stateTransition(Transmitter.QUEUE_NOT_EMPTY);
            shouldProcess.setTrue();
        }
    }

    /**
     * Indicates if the the interface to the CM11A is up.
     *
     * @return Returns true if the interface is up, false if the interface
     *         connection is down.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public boolean isRunning()
    {
        return(testGatewayState(Gateway.ALLOCATED));
    }

    /**
     * Start the serial link between the computer and the actual CM11A
     * module.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void allocate() throws GatewayException, GatewayStateError
    {
        // If deallocating resouces, throw an error
        if (testGatewayState(Gateway.DEALLOCATING_RESOURCES))
        {
            throw new GatewayStateError("CM11A can not be allocated.  It is currently in the DEALLOCATING_RESOURCES state.");
        }

        // If already being allocated or allocated, just return
        if (testGatewayState(Gateway.ALLOCATED |
                             Gateway.ALLOCATING_RESOURCES))
        {
            // already allocating
            return;
        }

        // set the new state
        stateTransition(Gateway.ALLOCATING_RESOURCES);

        if (System.getProperty("DEBUG") != null)
        {
            System.out.println("DEBUG: Starting interface");
        }
//        stopRequested = false;

        try
        {
            portId = CommPortIdentifier.getPortIdentifier(portName);
            serialPort = (SerialPort) portId.open("CM17A", 2000);
        }
        catch (NoSuchPortException e)
        {
            setGatewayState(Gateway.DEALLOCATED);
            throw new GatewayException("Requested com port " + portName +
                                       " does not exist.");
        }
        catch (PortInUseException e)
        {
            setGatewayState(Gateway.DEALLOCATED);
            throw new GatewayException("Request com port " + portName +
                                       " in use.");
        }
    
        processThread = new Thread(this);
        processThread.setDaemon(true);
        processThread.start();

        setGatewayState(Gateway.RESUMED);
        stateTransition(Gateway.ALLOCATED);

        // set CM17A to reset
        serialPort.setRTS(false);
        serialPort.setDTR(false);
        if (System.getProperty("DEBUG") != null)
        {
            System.out.println("DEBUG: Interface started");
        }
    }

    /**
     * Stop the serial link between the computer and the actual CM11A
     * module.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public synchronized void deallocate()
    {
        // If allocating resouces, throw an error
        if (testGatewayState(Gateway.ALLOCATING_RESOURCES))
        {
            throw new GatewayStateError("CM11A can not be deallocated.  It is currently in the ALLOCATING_RESOURCES state.");
        }

        // If already being deallocated or deallocated, just return
        if (testGatewayState(Gateway.DEALLOCATED |
                             Gateway.DEALLOCATING_RESOURCES))
        {
            // already deallocating
            return;
        }

        stateTransition(Gateway.DEALLOCATING_RESOURCES);

        // stop thread
        shouldProcess.setTrue();

        if (processThread != null)
        {
            try
            {
                // wait for the read thread to stop
                processThread.join(5000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            if (processThread.isAlive())
            {
                // kill it
                // processThread.stop(); // deprecated
            }
            processThread = null;
        }

        if (serialPort != null)
        {
            serialPort.close();
        }
        serialPort = null;
        portId = null;
        stateTransition(Gateway.DEALLOCATED);
        if (System.getProperty("DEBUG") != null)
        {
            System.out.println("DEBUG: Interface stopped");
        }
    }

    /**
     * Set the CM17A to reset state.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected void reset() throws GatewayStateError
    {
        // If deallocating resouces, throw an error
        if (testGatewayState(Gateway.DEALLOCATING_RESOURCES |
                             Gateway.DEALLOCATED))
        {
            throw new GatewayStateError("Can not transmit.  Transmitter is currently in the DEALLOCATING_RESOURCES or DEALLOCATED state.");
        }

        try
        {
            waitGatewayState(Gateway.ALLOCATED);
        }
        catch (InterruptedException e)
        {
            throw new GatewayStateError("Caught InterruptedException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            throw new GatewayStateError("Caught IllegalArgumentException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }

        if (serialPort != null)
        {
            serialPort.setRTS(false);
            serialPort.setDTR(false);
        }
    }

    /**
     * Set the CM17A to standby state.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected void standby() throws GatewayStateError
    {
        // If deallocating resouces, throw an error
        if (testGatewayState(Gateway.DEALLOCATING_RESOURCES |
                             Gateway.DEALLOCATED))
        {
            throw new GatewayStateError("Can not transmit.  Transmitter is currently in the DEALLOCATING_RESOURCES or DEALLOCATED state.");
        }

        try
        {
            waitGatewayState(Gateway.ALLOCATED);
        }
        catch (InterruptedException e)
        {
            throw new GatewayStateError("Caught InterruptedException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            throw new GatewayStateError("Caught IllegalArgumentException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }

        if (serialPort != null)
        {
            if (!serialPort.isRTS())
            {
                serialPort.setRTS(true);
            }
            if (!serialPort.isDTR())
            {
                serialPort.setDTR(true);
            }
        }
    }

    /**
     * Set the CM17A to logical '1' state.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected void logical1() throws GatewayStateError
    {
        // If deallocating resouces, throw an error
        if (testGatewayState(Gateway.DEALLOCATING_RESOURCES |
                             Gateway.DEALLOCATED))
        {
            throw new GatewayStateError("Can not transmit.  Transmitter is currently in the DEALLOCATING_RESOURCES or DEALLOCATED state.");
        }

        try
        {
            waitGatewayState(Gateway.ALLOCATED);
        }
        catch (InterruptedException e)
        {
            throw new GatewayStateError("Caught InterruptedException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            throw new GatewayStateError("Caught IllegalArgumentException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }

        if (serialPort != null)
        {
            if (!serialPort.isRTS())
            {
                serialPort.setRTS(true);
            }
            if (serialPort.isDTR())
            {
                serialPort.setDTR(false);
            }
        }
    }

    /**
     * Set the CM17A to logical '0' state.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    protected void logical0() throws GatewayStateError
    {
        // If deallocating resouces, throw an error
        if (testGatewayState(Gateway.DEALLOCATING_RESOURCES |
                             Gateway.DEALLOCATED))
        {
            throw new GatewayStateError("Can not transmit.  Transmitter is currently in the DEALLOCATING_RESOURCES or DEALLOCATED state.");
        }

        try
        {
            waitGatewayState(Gateway.ALLOCATED);
        }
        catch (InterruptedException e)
        {
            throw new GatewayStateError("Caught InterruptedException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            throw new GatewayStateError("Caught IllegalArgumentException while waiting for the gateway to enter an ALLOCATED state:  " + e.getMessage());
        }

        if (serialPort != null)
        {
            if (!serialPort.isDTR())
            {
                serialPort.setDTR(true);
            }
            if (serialPort.isRTS())
            {
                serialPort.setRTS(false);
            }
        }
    }

    /**
     * Calls TransmitterListener methods.  Lets parent's dispatchControlEvent
     * call the GatewayListener methods.
     *
     * @author Jesse Peterson <6/29/99>
     */
    @SuppressWarnings("unchecked")
    public void dispatchControlEvent(ControlEvent event)
    {
        if (event instanceof TransmitterEvent)
        {
            GatewayListener listener;

            // make a copy of the listener object vector so that it cannot
            // be changed while we are firing event
            Vector<CM17AStandardTransmission> v;
            synchronized(this)
            {
                v = (Vector<CM17AStandardTransmission>) gatewayListeners.clone();
            }
        
            // fire the event to all listeners
            int cnt = v.size();
            for (int i = 0; i < cnt; i++)
            {
                switch (event.getId())
                {
                case TransmitterEvent.QUEUE_EMPTIED:
                    listener = (GatewayListener)v.elementAt(i);
                    if (listener instanceof TransmitterListener)
                    {
                        ((TransmitterListener)listener).queueEmptied((TransmitterEvent)event);
                    }
                    break;

                case TransmitterEvent.QUEUE_UPDATED:
                    listener = (GatewayListener)v.elementAt(i);
                    if (listener instanceof TransmitterListener)
                    {
                        ((TransmitterListener)listener).queueUpdated((TransmitterEvent)event);
                    }
                    break;
                }
            }
        }
        else
        {
            super.dispatchControlEvent(event);
        }
    }

    /**
     * Process CM11A events.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void run()
    {
        while (true)
        {
            try
            {
                shouldProcess.waitForTrue();
                shouldProcess.setFalse();
                if (System.getProperty("DEBUG") != null)
                {
                    System.out.println("DEBUG: process thread awakened");
                }
                if (testGatewayState(Gateway.DEALLOCATING_RESOURCES |
                                     Gateway.DEALLOCATED))
                {
                    return;
                }

                processTransmissionQueue();
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    /**
     * Process the queue of events to be processed by the CM11A.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void processTransmissionQueue()
    {
        Object obj;
        CM17ATransmissionEvent transmissionEvent;

        while (!transmissionQueue.isEmpty())
        {
            try
            {
                obj = transmissionQueue.elementAt(0);

                if (obj instanceof CM17ATransmissionEvent)
                {
                    transmissionEvent = (CM17ATransmissionEvent)obj;
                    if (System.getProperty("DEBUG") != null)
                    {
                        System.out.println("DEBUG: Transmitting " + transmissionEvent);
                    }
                    transmissionEvent.transmit(serialPort);

                    // transmit complete
                    if (System.getProperty("DEBUG") != null)
                    {
                        System.out.println("DEBUG: Transmission of " + transmissionEvent + " complete.");
                    }

                    // remove from queue
                    transmissionQueue.removeElementAt(0);
                    long state = getGatewayState();
                    fireControlEvent(new TransmitterEvent(this,
                                                          TransmitterEvent.QUEUE_UPDATED,
                                                          true, state, state));
                }
                else
                {
                    // queue element is not a CM17ATransmissionEvent
                    transmissionQueue.removeElementAt(0);
                    long state = getGatewayState();
                    fireControlEvent(new TransmitterEvent(this,
                                                         TransmitterEvent.QUEUE_UPDATED,
                                                         true, state, state));
                }
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                // should not happen
                e.printStackTrace();
            }
        }

        stateTransition(Transmitter.QUEUE_EMPTY);
    }
}
