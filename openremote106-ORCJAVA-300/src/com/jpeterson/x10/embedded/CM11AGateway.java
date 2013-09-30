/*
 * Copyright (C) 2000  Jesse E. Peterson
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

package com.jpeterson.x10.embedded;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.TooManyListenersException;
import java.util.Vector;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import com.jpeterson.util.Condition;
import com.jpeterson.util.Unsigned;
import com.jpeterson.x10.X10Util;
import com.jpeterson.x10.event.*;
import com.jpeterson.x10.Transmitter;

/**
 * This is the main object for the embedded X10 package.  It is based on
 * the class <code>com.ibm.tspaces.examples.x10.X10Monitor</code>, an example
 * program included in IBM's TSpaces distribution available from
 * <a href="http://alphaworks.ibm.com">http://alphaworks.ibm.com</a>, as well
 * as the full-featured X10 framework available from
 * <a href="http://www.jpeterson.com">http://www.jpeterson.com</a>.
 *
 *  <code>
 *  CM11AGateway gateway = new CM11AGateway();
 *  Thread t = new Thread(gateway);
 *  t.start();
 *  </code>
 * 
 * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
 */
public class CM11AGateway implements Runnable, SerialPortEventListener, Transmitter
{
    private boolean running;

    private SerialPort port;  // Serial port

    private InputStream inputStream;  // serial input stream
    private OutputStream outputStream;  // serial output stream.

    private boolean processingDataAvailable;

    private int expected;
    private byte expectedChecksum;
    private X10Event curEvent;
    private X10Queue eventQueue;

    static final int ANY = 0;
    static final int POLL_IGNORE = 1;
    static final int POLL = 2;
    static final int RESPONSE = 3;
    static final int CHECKSUM = 4;
    static final int READY = 5;

    public static final byte POLL_REQ = (byte)0x5a;
    public static final byte TIME_REQ = (byte)0xa5;
    public static final byte MACRO_INITIATED = (byte)0x5b;
    public static final byte CHECKSUM_OK = (byte)0x00;
    public static final byte READY_REQ = (byte)0x55;

    public static final byte POLL_ACK = (byte)0xc3;
    public static final byte TIMER_DOWNLOAD = (byte)0x9b;

    // maximum number of days in a month. The index into the array is the
    // month, zero based. e.g., 0 for January, 1 for February, 11 for
    // December.
    private static final int[] daysInMonth = {31, 29, 31, 30, 31, 30, 31, 31,
                                              30, 31, 30, 31};

    private static final int BUFFER_SIZE = 255;
    private byte[] buffer = new byte[BUFFER_SIZE];
    private int index, length, in, count;

    private Condition doneSending;

    private Vector<AddressListener> addressListeners;
    private Vector<FunctionListener> functionListeners;

    /**
     * Create a new CM11AGateway object.
     *
     * @param portName Serial port name
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public CM11AGateway(String portName)
    {
        doneSending = new Condition(true);
        eventQueue = new X10Queue();
        addressListeners = new Vector<AddressListener>();
        functionListeners = new Vector<FunctionListener>();
        running = true;

        initPort(portName);
    }

    /**
     * Initialize the serial port.
     *
     * @param portName Name of serial port connected to CM11A.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    private void initPort(String portName)
    {
        try
        {
            port = (SerialPort)CommPortIdentifier.getPortIdentifier(portName).open("CM11AGateway", 2000);

            port.setSerialPortParams(4800,
                                     SerialPort.DATABITS_8,
                                     SerialPort.STOPBITS_1,
                                     SerialPort.PARITY_NONE);

            inputStream = port.getInputStream();

            outputStream = port.getOutputStream();

            port.addEventListener(this);
        }
        catch (NoSuchPortException e)
        {
            // TODO: Throw an X10Exception
        }
        catch (PortInUseException e)
        {
            // TODO: Throw an X10Exception
        }
        catch (UnsupportedCommOperationException e)
        {
            // TODO: Throw an X10Exception
        }
        catch (IOException e)
        {
            // TODO: Throw an X10Exception
        }
        catch (TooManyListenersException e)
        {
            // TODO: Throw an X10Exception
        }
    }

    /**
     * Stop the gateway.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public void stop()
    {
        running = false;
    }

    /**
     * This thread operates as the dispatch thread, dispatching X10Events
     * to the listeners.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public void run()
    {
        X10Event event;

        // express desire to be notified when data is available to be read
        port.notifyOnDataAvailable(true);

        // event dispatch
        while(running)
        {
            // read event from queue
            //System.out.println("Trying to get the next message.");  // DEBUG
            event = (X10Event)eventQueue.get();
            //System.out.println("Got the next message.");  // DEBUG

            // dispatch event
            //System.out.println("Firing event...");  // DEBUG
            fireX10Event(event);
            //System.out.println("Fired event");  // DEBUG
        }
    }

    /**
     * This method is called when a serial event occurs.
     *
     * @param event Serial port event
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public void serialEvent(SerialPortEvent event)
    {
        switch (event.getEventType())
        {
        case SerialPortEvent.DATA_AVAILABLE:
            //System.out.println("serialEvent: DATA_AVAILABLE"); // DEBUG
            if (!processingDataAvailable)
            {
                dataAvailable();
            }
            break;

        default:
            // unknown serial event
            break;
        }        
    }

    /**
     * Process available data sent from the CM11A device.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    private void dataAvailable()
    {
        if (System.getProperty("DEBUG") != null)
        {
            System.out.println("Data available.");  // DEBUG
        }

        try
        {
            processingDataAvailable = true;

            while (inputStream.available() > 0)
            {
                in = inputStream.read();

                if (in == -1)
                {
                    // end of stream
                    // this should not happen
                    return;
                }

                switch (expected)
                {
                case ANY:
                    // open state, no responses outstanding
                    switch (in)
                    {
                    case TIME_REQ:
                        // set clock
                        setClock();
                        break;

                    case POLL_REQ:
                        //System.out.println("Poll Req");  // DEBUG
                        expected = RESPONSE;
                        //System.out.println("Sending Poll ACK...");  // DEBUG
                        send(POLL_ACK);
                        //System.out.println("Sent Poll ACK");  // DEBUG
                        break;

                    case MACRO_INITIATED:
                    default:
                        // ERROR
                        // not handled
                        System.out.println("Not handled... in = " + in);  // DEBUG
                        consumeData();
                    }
                    break;

                case POLL_IGNORE:
                    break;

                case POLL:
                    if (in == POLL_REQ)
                    {
                        System.out.println("Expected poll request.");  // DEBUG
                        consumeData();
                        expected = RESPONSE;
                        send(POLL_ACK);
                    }
                    else
                    {
                        // ERROR
                        // unexpected poll request
                        System.out.println("Unexpected poll request.");  // DEBUG
                    }
                    break;

                case RESPONSE:
                    if (in == POLL_REQ)
                    {
                        // unexpected poll request
                        System.out.println("Unexpected Poll Request.");  // ERROR
                        break;
                    }

                    // Response to poll
                    // first byte is the size of the response message
                    index = 0;
                    length = in;

                    if (length > buffer.length)
                    {
                        // ERROR
                        System.out.println("Can not handle message.  Too big: " + length + " bytes.  Maximum allowable message size: " + buffer.length + " bytes.");  // DEBUG
                        consumeData();
                        expected = ANY;
                        break;
                    }
                    
                    try
                    {
                        while (index < length)
                        {
                            count = inputStream.read(buffer, index, length-index);
                            index += count;
                        }

                        processResponse(buffer, length);
                    }
                    catch (IOException e)
                    {
                        // ERROR
                        e.printStackTrace(); // DEBUG
                        System.out.println("Error reading response."); // DEBUG
                        consumeData();
                    }

                    expected = ANY;
                    break;

                case CHECKSUM:
                    if (in == POLL_REQ)
                    {
                        // interface ignored event
                        // resend event
                        transmitEvent(curEvent);
                        break;
                    }

                    if ((byte)in == expectedChecksum)
                    {
                        expected = READY;
                        send(CHECKSUM_OK);
                    }
                    else
                    {
                        // resend event
                        transmitEvent(curEvent);
                    }
                    break;

                case READY:
                    if (in == READY_REQ)
                    {
                        // send notification
                        eventQueue.put(curEvent);
                    }
                    else
                    {
                        // ERROR
                        // unexpected Ready
                        System.out.println("Unexpected ready response");  // DEBUG
                    }

                    expected = ANY;

                    doneSending.setTrue();
                    break;

                default:
                    // ERROR
                    // unsupported response
                    System.out.println("Unexpected response");  // DEBUG
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();  // TODO: enhance error handling
        }
        finally
        {
            processingDataAvailable = false;
        }
    }

    /**
     * Clear the input buffer stream.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    private void consumeData()
    {
        try
        {
            while (inputStream.available() > 0)
            {
                inputStream.read(buffer);
            }
        }
        catch (IOException e)
        {
            // ERROR
            e.printStackTrace();  // TODO: enhance error handling
        }
    }

    /**
     * Handle a Poll response message.
     *
     * @param response Poll response message.
     * @param len Poll response message length
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    private void processResponse(byte[] response, int len)
    {
        byte hiNibble, loNibble;

        X10Event evt;
        for (int i = 1; i < len; i++)
        {
            hiNibble = (byte)((response[i] >> 4) & 0x0f);
            loNibble = (byte)(response[i] & 0x0f);

            if ((response[0] & (1 << (i - 1))) != 0)
            {
                // function
                switch (loNibble)
                {
                case X10Util.X10_FUNCTION_ALL_UNITS_OFF:
                    evt = new AllUnitsOffEvent(this,
                                               X10Util.byte2houseCode(hiNibble));
                    break;

                case X10Util.X10_FUNCTION_ALL_LIGHTS_ON:
                    evt = new AllLightsOnEvent(this,
                                               X10Util.byte2houseCode(hiNibble));
                    break;

                case X10Util.X10_FUNCTION_ON:
                    evt = new OnEvent(this,
                                      X10Util.byte2houseCode(hiNibble));
                    break;

                case X10Util.X10_FUNCTION_OFF:
                    evt = new OffEvent(this,
                                       X10Util.byte2houseCode(hiNibble));
                    break;

                case X10Util.X10_FUNCTION_DIM:
                    evt = new DimEvent(this,
                                       X10Util.byte2houseCode(hiNibble),
                                       (int)Unsigned.unsigned(response[++i]),
                                       210);
                    break;

                case X10Util.X10_FUNCTION_BRIGHT:
                    evt = new BrightEvent(this,
                                          X10Util.byte2houseCode(hiNibble),
                                          (int)Unsigned.unsigned(response[++i]),
                                          210);
                    break;

                case X10Util.X10_FUNCTION_ALL_LIGHTS_OFF:
                    evt = new AllLightsOffEvent(this,
                                                X10Util.byte2houseCode(hiNibble));
                    break;

                case X10Util.X10_FUNCTION_EXTENDED_CODE:
                    evt = new ExtendedCodeEvent(this,
                                                X10Util.byte2houseCode(hiNibble),
                                                response[++i],  // data
                                                response[++i]); // command
                    break;

                case X10Util.X10_FUNCTION_HAIL_REQUEST:
                    evt = new HailRequestEvent(this,
                                               X10Util.byte2houseCode(hiNibble));
                    break;

                case X10Util.X10_FUNCTION_HAIL_ACKNOWLEDGE:
                    evt = new HailAcknowledgeEvent(this,
                                                   X10Util.byte2houseCode(hiNibble));
                    break;

                case X10Util.X10_FUNCTION_PRESET_DIM_1:
                    evt = new PresetDim1Event(this,
                                              X10Util.byte2houseCode(hiNibble));
                    break;

                case X10Util.X10_FUNCTION_PRESET_DIM_2:
                    evt = new PresetDim2Event(this,
                                              X10Util.byte2houseCode(hiNibble));
                    break;

                case X10Util.X10_FUNCTION_EXTENDED_DATA_TRANSFER:
                    evt = new ExtendedDataTransferEvent(this,
                                                        X10Util.byte2houseCode(hiNibble));
                    break;

                case X10Util.X10_FUNCTION_STATUS_ON:
                    evt = new StatusOnEvent(this,
                                            X10Util.byte2houseCode(hiNibble));
                    break;

                case X10Util.X10_FUNCTION_STATUS_OFF:
                    evt = new StatusOffEvent(this,
                                             X10Util.byte2houseCode(hiNibble));
                    break;

                case X10Util.X10_FUNCTION_STATUS_REQUEST:
                    evt = new StatusRequestEvent(this,
                                                 X10Util.byte2houseCode(hiNibble));
                    break;

                default:
                    evt = null;
                    break;
                }
            }
            else
            {
                // address
                evt = new AddressEvent(this,
                                       X10Util.byte2houseCode(hiNibble),
                                       X10Util.byte2deviceCode(loNibble));
            }
            if (evt != null)
            {
                // send notification
                //System.out.println("Putting event " + evt + " on event queue...");  // DEBUG
                eventQueue.put(evt);
                //System.out.println("Done putting event " + evt + " on event queue");  // DEBUG
            }
        }
    }

    /**
     * Issue a command.
     * <BR>
     * For example, to turn device A1 on:
     * <BR>
     * <CODE>
     * AddressEvent address = new AddressEvent(this, 'A', 1);
     * FunctionEvent function = new OnEvent(this, 'A');
     * cm11a.issueCommand(address, function);
     * </CODE>
     * 
     * @param address Address for function
     * @param function Action to take on the device addressed in the address field.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public synchronized void issueCommand(AddressEvent address,
                                          FunctionEvent function)
    {
        try
        {
            transmit(address);
            transmit(function);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Send an event to the CM11A device.
     *
     * @param event X10Event to send.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public synchronized void transmit(X10Event event) throws IOException
    {
        //System.out.println("Sending " + event + "...");  // DEBUG
        try
        {
            // check to make sure no transmission is in progress.
            // if transmission is in progress, wait for it to complete.
            doneSending.waitForTrue();
        }
        catch (InterruptedException e)
        {
            // ERROR
            e.printStackTrace();  // TODO: enhance error handling
        }

        doneSending.setFalse();

        transmitEvent(event);

        try
        {
            doneSending.waitForTrue();
        }
        catch (InterruptedException e)
        {
            // ERROR
            e.printStackTrace();  // TODO: enhance error handling
        }

        // event sent
        //System.out.println("Done sending " + event);  // DEBUG
    }

    /**
     * Transmits event.
     *
     * @param event X10 event to transmit
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    private void transmitEvent(X10Event event)
    {
        expectedChecksum = event.getChecksum();
        expected = CHECKSUM;
        curEvent = event;
        //System.out.print("Transmitting " + event + "...");  // DEBUG
        send(event.getPacket());
        //System.out.println("done.");  // DEBUG
    }

    /**
     * Send a byte array to the CM11A device over the serial port.
     *
     * @param bytes Byte array to send.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    private void send(byte[] bytes)
    {
        try
        {
            outputStream.write(bytes);
        }
        catch (IOException e)
        {
            // ERROR
            e.printStackTrace();  // TODO: enhance error handling
        }
    }

    /**
     * Send a byte to the CM11A device over the serial port.
     *
     * @param b Byte to send.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    private void send(byte b)
    {
        try
        {
            outputStream.write(b);
        }
        catch (IOException e)
        {
            e.printStackTrace();  // TODO: enhance error handling
        }
    }

    /**
     * Set the clock to the current time.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public void setClock()
    {
        Calendar now = Calendar.getInstance();
        // TODO: This is defaulting to house code 'A'.  Need to correct when
        //       user has the ability to set this.
        setClock(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE),
                 now.get(Calendar.SECOND), now.get(Calendar.MONTH),
                 now.get(Calendar.DAY_OF_MONTH),
                 now.get(Calendar.DAY_OF_WEEK), 'A', false,
                 false, false);
    }

    /**
     * Update the CM11A interface's internal clock, set the monitored house
     * code, reset battery timer, clear monitored statuses, and purge timer.
     *
     * @param hour Current time, hours. 0 - 23.
     * @param minute Current time, minutes. 0 - 59.
     * @param second Current time, seconds. 0 - 59.
     * @param month Current month, zero based. e.g., 0 for January
     *        (Calendar.JANUARY), 1 for February (Calendar.FEBRUARY).
     * @parma day Current day. 1 - 31. (Maximum day depends on month)
     * @param dayOfWeek A day of the week constant from the class
     *        <CODE>java.util.Calendar</CODE>. Should be one of the following:
     *        <CODE>Calendar.SUNDAY</CODE>, <CODE>Calendar.MONDAY</CODE>,
     *        <CODE>Calendar.TUESDAY</CODE>, <CODE>Calendar.WEDNESDAY</CODE>,
     *        <CODE>Calendar.THURSDAY</CODE>, <CODE>Calendar.FRIDAY</CODE>,
     *        or <CODE>Calendar.SATURDAY</CODE>.
     * @param houseCode the house code of the event. Valid codes are 'A'
     *        through 'P', uppercase.
     * @param clearBatteryTimer If true, the interface's battery timer will be
     *        cleared.
     * @param clearMonitoredStatus If true, the interface's monitored statuses
     *        will be cleared.
     * @param purgeTimer If true, this will purge the interfaces timer, but I
     *        don't know what that means???
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public void setClock(int hour, int minute, int second, int month, int day,
                         int dayOfWeek, char houseCode, boolean clearBatteryTimer,
                         boolean clearMonitoredStatus, boolean purgeTimer)
    {
        byte[] packet = new byte[7];
        int minutes, dayOfYear;

        if ((hour < 0) || (hour > 23))
        {
            throw new IllegalArgumentException("Hour must be between 0 and 23, inclusive.");
        }

        if ((minute < 0) || (minute > 59))
        {
            throw new IllegalArgumentException("Minute must be between 0 and 59, inclusive.");
        }

        if ((second < 0) || (second > 59))
        {
            throw new IllegalArgumentException("Second must be between 0 and 59, inclusive.");
        }

        packet[0] = TIMER_DOWNLOAD;

        packet[1] = (byte)second;

        minutes = (hour * 60) + minute;
        packet[2] = (byte)(minutes % 120);
        packet[3] = (byte)((minutes - packet[2]) / 120);

        dayOfYear = dayOfYear(month, day);
        packet[4] = (byte)(dayOfYear & 0xff);

        switch(dayOfWeek)
        {
        default:
        case Calendar.SUNDAY:
            packet[5] = (byte)0x01;
            break;

        case Calendar.MONDAY:
            packet[5] = (byte)0x02;
            break;

        case Calendar.TUESDAY:
            packet[5] = (byte)0x04;
            break;

        case Calendar.WEDNESDAY:
            packet[5] = (byte)0x08;
            break;

        case Calendar.THURSDAY:
            packet[5] = (byte)0x10;
            break;

        case Calendar.FRIDAY:
            packet[5] = (byte)0x20;
            break;

        case Calendar.SATURDAY:
            packet[5] = (byte)0x40;
            break;
        }

        if (dayOfYear > 0xff)
        {
            // set high bit
            packet[5] |= 0x80;
        }

        packet[6] = (byte)((X10Util.houseCode2byte(houseCode)) << 4);

        if (clearBatteryTimer)
        {
            packet[6] |= (byte)0x04;
        }

        if (clearMonitoredStatus)
        {
            packet[6] |= (byte)0x01;
        }

        if (purgeTimer)
        {
            packet[6] |= (byte)0x02;
        }

        expected = ANY;

        send(packet);

        /**
        // store values
        this.monitoredHouseCode = houseCode;
        this.currentDay = dayOfWeek;
        this.julianDay = dayOfYear;
        this.hours = hour;
        this.minutes = minute;
        this.seconds = second;
        if (clearBatteryTimer)
        {
            batteryUsage = 0;
        }
        */
    }

    /**
     * Add an X10 Address listener.  The CM11A will send any X10 address
     * events that it intercepts on the power line to all registered
     * address listeners.
     *
     * @param l X10 AddressListener to add.
     * @see removeAddressListener
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public synchronized void addAddressListener(AddressListener l)
    {
        if (addressListeners == null)
        {
            addressListeners = new Vector<AddressListener>();
        }
    
        // add a listener if it is not already registered
        if (!addressListeners.contains(l))
        {
            addressListeners.addElement(l);
        }
    }

    /**
     * Remove an Address listener.
     *
     * @param l AddressListener to remove.
     * @see addAddressListener
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public synchronized void removeAddressListener(AddressListener l)
    {
        // remove it if it is registered
        if (addressListeners != null)
        {
            addressListeners.removeElement(l);
        }
    }

    /**
     * Add an X10 Function listener.  The CM11A will send any X10 function
     * events that it intercepts on the power line to all registered
     * function listeners.
     *
     * @param l X10 FunctionListener to add.
     * @see removeFunctionListener
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public synchronized void addFunctionListener(FunctionListener l)
    {
        if (functionListeners == null)
        {
            functionListeners = new Vector<FunctionListener>();
        }

        // add a listener if it is not already registered
        if (!functionListeners.contains(l))
        {
            functionListeners.addElement(l);
        }
    }

    /**
     * Remove a Function listener.
     *
     * @param l FunctionListener to remove.
     * @see addFunctionListener
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    public synchronized void removeFunctionListener(FunctionListener l)
    {
        // remove it if it is registered
        if (functionListeners != null)
        {
            functionListeners.removeElement(l);
        }
    }

    /**
     * Fire an X10 Transmission. When an X10 event is received from the
     * power line, this method is called with the X10 event to send to all
     * registered X10 transmission listeners.
     *
     * @param evt The event to send to X10Transmission listeners.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    @SuppressWarnings("unchecked")
    protected void fireX10Event(X10Event evt)
    {
        // TODO: Implement Extended function features ExtendedCodeEvent and
        //       ExtendedDataTransferEvent.
        Vector<Object> v = null;

        if (evt instanceof AddressEvent)
        {
            // make a copy of the listener object vector so that it cannot
            // be changed while we are firing events
            synchronized(this)
            {
                if (addressListeners != null)
                {
                    v = (Vector<Object>) addressListeners.clone();
                }
            }
        
            if (v != null)
            {
                // fire the event to all listeners
                int cnt = v.size();
                for (int i = 0; i < cnt; i++)
                {
                    AddressListener client = (AddressListener)v.elementAt(i);
                    client.address((AddressEvent)evt);
                }
            }
        }
        else if (evt instanceof FunctionEvent)
        {
            // make a copy of the listener object vector so that it cannot
            // be changed while we are firing events
            synchronized(this)
            {
                if (functionListeners != null)
                {
                    v = (Vector<Object>) functionListeners.clone();
                }
            }
        
            if (v != null)
            {
                FunctionEvent functionEvent = (FunctionEvent)evt;
                byte functionCode = functionEvent.getFunction();

                // fire the event to all listeners
                int cnt = v.size();

                for (int i = 0; i < cnt; i++)
                {
                    FunctionListener client = (FunctionListener)v.elementAt(i);
                    switch (functionCode)
                    {
                    case X10Util.X10_FUNCTION_ALL_UNITS_OFF:
                        client.functionAllUnitsOff(functionEvent);
                        break;

                    case X10Util.X10_FUNCTION_ALL_LIGHTS_ON:
                        client.functionAllLightsOn(functionEvent);
                        break;

                    case X10Util.X10_FUNCTION_ON:
                        client.functionOn(functionEvent);
                        break;

                    case X10Util.X10_FUNCTION_OFF:
                        client.functionOff(functionEvent);
                        break;

                    case X10Util.X10_FUNCTION_DIM:
                        client.functionDim(functionEvent);
                        break;

                    case X10Util.X10_FUNCTION_BRIGHT:
                        client.functionBright(functionEvent);
                        break;

                    case X10Util.X10_FUNCTION_ALL_LIGHTS_OFF:
                        client.functionAllLightsOff(functionEvent);
                        break;

                    case X10Util.X10_FUNCTION_HAIL_REQUEST:
                        client.functionHailRequest(functionEvent);
                        break;

                    case X10Util.X10_FUNCTION_HAIL_ACKNOWLEDGE:
                        client.functionHailAcknowledge(functionEvent);
                        break;

                    case X10Util.X10_FUNCTION_PRESET_DIM_1:
                        client.functionPresetDim1(functionEvent);
                        break;

                    case X10Util.X10_FUNCTION_PRESET_DIM_2:
                        client.functionPresetDim2(functionEvent);
                        break;

                    case X10Util.X10_FUNCTION_STATUS_ON:
                        client.functionStatusOn(functionEvent);
                        break;

                    case X10Util.X10_FUNCTION_STATUS_OFF:
                        client.functionStatusOff(functionEvent);
                        break;

                    case X10Util.X10_FUNCTION_STATUS_REQUEST:
                        client.functionStatusRequest(functionEvent);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Make sure that the month is valid.  If it is greater than December(11),
     * set the month to December.  If the month is less than January(0), set
     * the month to January.
     *
     * @param month Month, zero based. e.g., 0 for January, 1 for February.
     * @return Normalized month.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    protected static int normalizeMonth(int month)
    {
        if (month > 11)
        {
            return(11);
        }
        if (month < 0)
        {
            return(0);
        }
        return(month);
    }

    /**
     * Make sure that the day is valid.  If it greater than the maximum day
     * for the specified month, set the day to the maximum day.  If it is
     * less than the minimum day for the specified month, set the day to the
     * minimum day.
     *
     * @param day Day to normalize.
     * @param month Month, zero based. e.g., 0 for January, 1 for February.
     * @reutrn Normalized day.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    protected static int normalizeDay(int month, int day)
    {
        if (day < 1)
        {
            return(1);
        }
        month = normalizeMonth(month);
        if ((month == 0) || (month == 2) || (month == 4) || (month == 6) ||
            (month == 7) || (month == 9) || (month == 11))
        {
            // max of 31 days
            if (day > 31)
            {
                return(31);
            }
            return(day);
        }
        else if ((month == 3) || (month == 5) || (month == 8) || (month == 10))
        {
            // max of 30 days
            if (day > 30)
            {
                return(30);
            }
            return(day);
        }
        else
        {
            // max of 29 days
            if (day > 29)
            {
                return(29);
            }
            return(day);
        }
    }

    /**
     * Convert a date in the format of month and day to day of year format.
     * The algorythm uses 366 days in a year, where 0 is January 1 and 365 is
     * December 31.
     *
     * @param month Month, zero based. e.g., 0 for January, 1 for February.
     * @param day Day of month
     * @return Day of year.
     *
     * @author Jesse Peterson <a href="mailto:jesse@jpeterson.com">(jesse@jpeterson.com)</a>
     */
    protected static int dayOfYear(int month, int day)
    {
        int dayOfYear = 0;

        month = normalizeMonth(month);
        day = normalizeDay(month, day);

        for (int i = 0; i < month; i++)
        {
            dayOfYear += daysInMonth[i];
        }
        dayOfYear += (day - 1);

        return(dayOfYear);
    }
}
