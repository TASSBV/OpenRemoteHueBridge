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

package com.jpeterson.x10server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;
import com.jpeterson.x10.Transmitter;
import com.jpeterson.x10.event.AddressEvent;
import com.jpeterson.x10.event.AddressListener;
import com.jpeterson.x10.event.AllLightsOffEvent;
import com.jpeterson.x10.event.AllLightsOnEvent;
import com.jpeterson.x10.event.AllUnitsOffEvent;
import com.jpeterson.x10.event.BrightEvent;
import com.jpeterson.x10.event.DimEvent;
import com.jpeterson.x10.event.FunctionEvent;
import com.jpeterson.x10.event.FunctionListener;
import com.jpeterson.x10.event.HailAcknowledgeEvent;
import com.jpeterson.x10.event.HailRequestEvent;
import com.jpeterson.x10.event.OffEvent;
import com.jpeterson.x10.event.OnEvent;
import com.jpeterson.x10.event.PresetDim1Event;
import com.jpeterson.x10.event.PresetDim2Event;
import com.jpeterson.x10.event.StatusOffEvent;
import com.jpeterson.x10.event.StatusOnEvent;
import com.jpeterson.x10.event.StatusRequestEvent;
import com.jpeterson.x10.event.X10Event;

/**
 * This class implements an X10 service as a network controllable device by
 * utilizing a CM11A device and the X10 Java interface. It
 * implements a TCP service that is controlled through simple text commands.
 * This provides a rudimentary human interface as well as a programmable
 * interface. Because it provides a TCP interface, totally seperate systems
 * running on independent boxes can be enabled with X10 control as long as
 * the controlling system is able to make a TCP connection.
 * <P>
 * This class also provides a mechanism to receive X10 events. The events
 * are broadcast via UDP multicast broadcasts. This allows the service to
 * efficiently notify multiple clients at once.
 * <P>
 * X10 commands are sent to the service via a TCP connection. A client
 * connects to the host running the service on the configured port. The
 * client will be prompted with the prompt ">". This indicates that the
 * service is ready to receive commands. Commands should be entered
 * followed by a "\n". This signifies completion of the command. The
 * service will then attempt to execute the commands. Upon an error,
 * an error message will be displayed, followed by the prompt ">".
 * When an error occurs in a line, none of the line is executed. If
 * the command completes, the prompt ">" will be displayed, waiting for
 * the next commands. Commands take the following form.
 * <P>
 * <CODE><PRE>
 * commands   = helpcmd | quitcmd | +( "(" command ")" )
 * helpcmd    = "help"
 * quitcmd    = "quit"
 * command    = house-code key-code [ *extra-data ]
 * house-code = "A"
 *            | "B"
 *            | "C"
 *            | "D"
 *            | "E"
 *            | "F"
 *            | "G"
 *            | "H"
 *            | "I"
 *            | "J"
 *            | "K"
 *            | "L"
 *            | "M"
 *            | "N"
 *            | "O"
 *            | "P"
 * key-code   = "01"  ; Unit 1
 *            | "02"  ; Unit 2
 *            | "03"  ; Unit 3
 *            | "04"  ; Unit 4
 *            | "05"  ; Unit 5
 *            | "06"  ; Unit 6
 *            | "07"  ; Unit 7
 *            | "08"  ; Unit 8
 *            | "09"  ; Unit 9
 *            | "10"  ; Unit 10
 *            | "11"  ; Unit 11
 *            | "12"  ; Unit 12
 *            | "13"  ; Unit 13
 *            | "14"  ; Unit 14
 *            | "15"  ; Unit 15
 *            | "16"  ; Unit 16
 *            | "A0"  ; All Units Off
 *            | "L1"  ; All Lights On
 *            | "ON"  ; On
 *            | "OF"  ; Off
 *            | "DI"  ; Dim
 *            | "BR"  ; Bright
 *            | "L0"  ; All Lights Off
 *            | "HR"  ; Hail Request
 *            | "HA"  ; Hail Acknowledge
 *            | "D1"  ; Preset Dim 1
 *            | "D2"  ; Preset Dim 2
 *            | "EX"  ; Extended Data
 *            | "S1"  ; Status On
 *            | "S0"  ; Status Off
 *            | "SR"  ; Status Request
 * extra-data  = &lt;Extra data is key code specific.  Presently, 'DI'
 *               and 'BR' use extra data.  The extra data is a number
 *               from 1 - 22 to indicate the number of dims.>
 * </PRE></CODE>
 * <P>
 * Example:
 * <TABLE BORDER='1' CELLPADDING='0' CELLSPACING='3'>
 * <TR><TD>Turn on device A3</TD><TD>(A03)(AON)</TD></TR>
 * <TR><TD>Turn off all lights for house code E</TD><TD>EL0</TD></TR>
 * <TR><TD>Dim B13 by 11 dims</TD><TD>(B13)(BDI11)</TD></TR>
 * </TABLE>
 * <P>
 * The UDP status messages will take the following form:
 * <BLOCKQUOTE><CODE>h=E&amp;k=DI&amp;dim=11&amp;max=22</CODE></BLOCKQUOTE>
 * The format is similar to a HTTP URL encoded query string. The
 * data is represented as key/value pairs, where the key and value
 * are seperated by the equals ('=') character, and the pairs are
 * seperated by the ampersand ('&amp;') character. Parsing of this
 * format should be straight forward as well as providing flexibility to
 * extend the messages in the future.
 * <P>
 * The key 'h' will contain the house code. This will be an upper case
 * letter from 'A' through 'P', inclusive. The key 'k' indicates the key
 * code. This will be the number 1 through 16 to indicate an address
 * function for the device number, or one of the two character key codes
 * described above in the command section. For a Dim or Bright function,
 * the key 'dim' will be present and will indicate the number of dims, and the key 'max'
 * will be present and indicates the total number of dims for that command type.
 *
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public abstract class X10ServerStub implements AddressListener, FunctionListener
{
    /**
     * Exit value of '1' indicates and invalid argument.
     */
    public static final int EXIT_INVALID_ARGUMENT = 1;

    /**
     * Exit value of '2' indicates and invalid listent port.
     */
    public static final int EXIT_INVALID_LISTEN_PORT = 2;

    /**
     * Exit value of '3' indicates and invalid status address.
     */
    public static final int EXIT_INVALID_STATUS_ADDRESS = 3;

    /**
     * Exit value of '4' indicates and invalid status port.
     */
    public static final int EXIT_INVALID_STATUS_PORT = 4;

    /**
     * Default listen port: 4377
     */
    public static final int DEFAULT_LISTEN_PORT = 4377;

    /**
     * Default UDP status broadcast address: 239.6.20.71
     */
    public static final String DEFAULT_STATUS_ADDRESS = "239.6.20.71";

    /**
     * Default UDP status broadcast port: 4378
     */
    public static final int DEFAULT_STATUS_PORT = 4378;

    /**
     * Default serial port: COM2
     */
    public static final String DEFAULT_COM_PORT = "COM2";

    /**
     * Default command prompt: '>'
     */
    public static final String PROMPT = ">";

    /**
     * Command to get help on possible commands. "help"
     */
    public static final String COMMAND_HELP = "help";

    /**
     * Command to quit, logging out of the server. "quit"
     */
    public static final String COMMAND_QUIT = "quit";

    /** All Units Off Key Code - "A0" */
    public static final String ALL_UNITS_OFF_KEY_CODE = "A0";

    /** All Lights On Key Code - "L1" */
    public static final String ALL_LIGHTS_ON_KEY_CODE = "L1";

    /** On Key Code - "ON" */
    public static final String ON_KEY_CODE = "ON";

    /** Off Key Code - "OF" */
    public static final String OFF_KEY_CODE = "OF";

    /** Dim Key Code - "DI" */
    public static final String DIM_KEY_CODE = "DI";

    /** Bright Key Code - "BR" */
    public static final String BRIGHT_KEY_CODE = "BR";

    /** All Lights Off Key Code - "L0" */
    public static final String ALL_LIGHTS_OFF_KEY_CODE = "L0";

    /** Hail Request Key Code - "HR" */
    public static final String HAIL_REQUEST_KEY_CODE = "HR";

    /** Hail Acknowledge Key Code - "HR" */
    public static final String HAIL_ACKNOWLEDGE_KEY_CODE = "HA";

    /** Preset Dim 1 Key Code - "D1" */
    public static final String PRESET_DIM_1_KEY_CODE = "D1";

    /** Preset Dim 2 Key Code - "D2" */
    public static final String PRESET_DIM_2_KEY_CODE = "D2";

    /** Extended Data Key Code - "EX" */
    public static final String EXTENDED_DATA_KEY_CODE = "EX";

    /** Status On Key Code - "S1" */
    public static final String STATUS_ON_KEY_CODE = "S1";

    /** Status Off Key Code - "S0" */
    public static final String STATUS_OFF_KEY_CODE = "S0";

    /** Status Request Key Code - "SR" */
    public static final String STATUS_REQUEST_KEY_CODE = "SR";

    /**
     * TCP port to listen for client connections on.
     */
    protected int listenPort;

    /**
     * IP Address for UDP status broadcasts.
     */
    protected InetAddress statusAddress;

    /**
     * UDP port for status broadcasts.
     */
    protected int statusPort;

    /**
     * Serial port to talk to CM11A to.
     */
    protected String comPort;

    /**
     * Where worker threads stand idle.
     */
    protected Vector<Worker> threads = new Vector<Worker>();

    /**
     * max # worker threads.
     */
    protected int workers = 5;

    /**
     * Inactivity timeout.
     */
    protected int timeout = 0;

    /**
     * Status broadcast socket.
     */
    protected MulticastSocket broadcastSocket;

    /**
     * X10 gateway.
     */
    protected Transmitter transmitter;

    /**
     * Variable used to indicate that the server is running.
     */
    protected boolean run;


    /**
     * Create a new X10ServerStub
     *
     * @param listenPort TCP port to listen for client connections on.
     * @param statusAddress UDP multicast address to send status messages
     *        to.
     * @param statusPort UDP port to send status messages to.
     * @param comPort Serial port to talk to Transmitter to.
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public X10ServerStub(int listenPort, InetAddress statusAddress,
                         int statusPort, String comPort)
    {
        this.listenPort = listenPort;
        this.statusAddress = statusAddress;
        this.statusPort = statusPort;
        this.comPort = comPort;
    }

    /**
     * Subclasses must implement this method, creating an instance
     * of the <code>Transmitter</code> interface.  The transmitter
     * should be initialized to for <code>X10Event</code> generation
     * if appropriate.  i.e., call <code>addAddressListener(this)</code>
     * and <code>addFunctionListener(this)</code>.
     */
    protected abstract Transmitter createTransmitter();

    /**
     * Start listening for client connections.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void start() throws IOException
    {
        transmitter = createTransmitter();

        broadcastSocket = new MulticastSocket();

        for (int i = 0; i < workers; ++i)
        {
            Worker w = new Worker();
            (new Thread(w, "worker #"+i)).start();
            threads.addElement(w);
        }

        ServerSocket serverSocket = new ServerSocket(listenPort);

        run = true;

        while (run)
        {
            Socket s = serverSocket.accept();

            Worker w = null;
            synchronized (threads)
            {
                if (threads.isEmpty())
                {
                    Worker ws = new Worker();
                    ws.setSocket(s);
                    (new Thread(ws, "additional worker")).start();
                }
                else
                {
                    w = (Worker)threads.elementAt(0);
                    threads.removeElementAt(0);
                    w.setSocket(s);
                }
            }
        }

        // cleanup
    }

    /**
     * Stop the running server.
     */
    public void stop()
    {
        run = false;
    }

    /**
     * Send a UDP broadcast message.
     *
     * @param msg Message to send
     */
    public void broadcast(String msg) throws IOException
    {
        byte[] buf;

        buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length,
                                                   statusAddress,
                                                   statusPort);
        broadcastSocket.send(packet);
    }

    /**
     * Listener for AddressEvents.
     *
     * @param e AddressEvent.
     */
    public void address(AddressEvent e)
    {
        // send address event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + e.getDeviceCode());
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for All Units Off function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionAllUnitsOff(FunctionEvent e)
    {
        // send all units off event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + ALL_UNITS_OFF_KEY_CODE);
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for All Lights On function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionAllLightsOn(FunctionEvent e)
    {
        // send all lights on event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + ALL_LIGHTS_ON_KEY_CODE);
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for On function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionOn(FunctionEvent e)
    {
        // send on event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + ON_KEY_CODE);
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for Off function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionOff(FunctionEvent e)
    {
        // send on event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + OFF_KEY_CODE);
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for Dim function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionDim(FunctionEvent e)
    {
        // send dim event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + DIM_KEY_CODE + "&dim=" +
                          e.getDims() + "&max=" + e.getDimMax());
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for Bright function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionBright(FunctionEvent e)
    {
        // send bright event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + BRIGHT_KEY_CODE + "&dim=" +
                          e.getDims() + "&max=" + e.getDimMax());
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for All Lights Off function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionAllLightsOff(FunctionEvent e)
    {
        // send all lights off event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + ALL_LIGHTS_OFF_KEY_CODE);
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for Hail Request function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionHailRequest(FunctionEvent e)
    {
        // send hail request event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + HAIL_REQUEST_KEY_CODE);
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for Hail Acknowledge function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionHailAcknowledge(FunctionEvent e)
    {
        // send hail acknowledge event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + HAIL_ACKNOWLEDGE_KEY_CODE);
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for Preset Dim 1 function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionPresetDim1(FunctionEvent e)
    {
        // send preset dim 1 event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + PRESET_DIM_1_KEY_CODE);
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for Preset Dim 2 function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionPresetDim2(FunctionEvent e)
    {
        // send preset dim 2 event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + PRESET_DIM_2_KEY_CODE);
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for Status On function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionStatusOn(FunctionEvent e)
    {
        // send status on event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + STATUS_ON_KEY_CODE);
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for Status Off function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionStatusOff(FunctionEvent e)
    {
        // send status off event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + STATUS_OFF_KEY_CODE);
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Listener for Status Request function.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void functionStatusRequest(FunctionEvent e)
    {
        // send status request event
        synchronized (this)
        {
            try
            {
                broadcast("h=" + e.getHouseCode() + "&k=" + STATUS_REQUEST_KEY_CODE);
            }
            catch (IOException ex)
            {
            }
        }
    }

    /**
     * Class that is responsible for handling an individual connection
     * to the server.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    class Worker implements Runnable
    {
        private Socket s;

        /**
         * Create a worker.
         *
         * @author Jesse Peterson <jesse@jpeterson.com>
         */
        public Worker()
        {
            s = null;
        }

        /**
         * Called to pass the client connection socket to the worker.
         * This will start the worker thread to handle the client.
         *
         * @author Jesse Peterson <jesse@jpeterson.com>
         */
        public synchronized void setSocket(Socket s)
        {
            this.s = s;
            notify();
        }

        /**
         * Handle the user interface for the client.  Prompt for commands and
         * then process those commands.
         *
         * @author Jesse Peterson <jesse@jpeterson.com>
         */
        public synchronized void run()
        {
            while(true)
            {
                if (s == null)
                {
                    // nothing to do
                    try
                    {
                        wait();
                    }
                    catch (InterruptedException e)
                    {
                        // should not happen
                        continue;
                    }
                }
                try
                {
                    handleClient();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                // go back in wait queue if there's fewer
                // than numHandler connections.
                s = null;
                Vector<Worker> pool = threads;
                synchronized (pool)
                {
                    if (pool.size() >= workers)
                    {
                        // too many thread.
                        return;
                    }
                    else
                    {
                        pool.addElement(this);
                    }
                }
            }
        }

        /**
         * Implement server protocol.  Provides prompts, recieves commands,
         * and informs of errors.
         *
         * @exception IOException Unable to read or write to the socket.
         * @author Jesse Peterson <jesse@jpeterson.com>
         */
        protected void handleClient() throws IOException
        {
            boolean done = false;
            String line;
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream());
            // We will only block in readline for this many milliseconds
            // before we fail with java.io.InterruptedIOException,
            // at which point we will abandon the connection
            s.setSoTimeout(timeout);
            s.setTcpNoDelay(true);

            try
            {
                while (!done)
                {
                    // print prompt
                    out.print(PROMPT);
                    out.flush();

                    // get command
                    line = in.readLine();

                    if (line == null)
                    {
                        done = true;
                    }
                    else if (line.equals(COMMAND_HELP))
                    {
                        // write out help
                        out.println("Available Commands");
                        out.println("help - this help information");
                        out.println("quit - log out from the server");
                        out.flush();
                    }
                    else if (line.equals(COMMAND_QUIT))
                    {
                        // quit
                        done = true;
                    }
                    else
                    {
                        // process command
                        processCommand(line, out);
                    }
                }
            }
            finally
            {
                s.close();
            }
        }

        /**
         * Process a command line.
         *
         * @param line Command line
         * @param out PrintWriter to send error messages to
         * @author Jesse Peterson <jesse@jpeterson.com>
         */
        protected void processCommand(String line, PrintWriter out)
        {
            Vector<String> commands = new Vector<String>();
            X10Event events[] = null;

            line = line.trim();
            if (line.startsWith("("))
            {
                // transaction
                String token;
                int state;  // 0 = expecting '('
                            // 1 = expecting command
                            // 2 = expecting ')'
                StringTokenizer st = new StringTokenizer(line, "()", true);

                // initial state
                state = 0;

                while (st.hasMoreTokens())
                {
                    token = st.nextToken();

                    if (token.equals("("))
                    {
                        if (state != 0)
                        {
                            out.print("Error: Syntax error, expected '(', received '" + token + "' in command line \"" + line + "\"\r\n");
                            out.flush();
                            return;
                        }

                        // transition to next state
                        state = 1;
                    }
                    else if (token.equals(")"))
                    {
                        if (state != 2)
                        {
                            out.print("Error: Syntax error, expected ')', received '" + token + "' in command line \"" + line + "\"\r\n");
                            out.flush();
                            return;
                        }

                        // transition to next state.
                        state = 0;
                    }
                    else
                    {
                        token = token.trim();

                        if (state != 1)
                        {
                            out.print("Error: Syntax error, expected command, received '" + token + "' in command line \"" + line + "\"\r\n");
                            out.flush();
                            return;
                        }

                        commands.addElement(token.trim());

                        // transition to next state.
                        state = 2;
                    }
                }
            }
            else
            {
                commands.addElement(line);
            }

            // process commands
            events = new X10Event[commands.size()];
            for (int i = 0; i < events.length; i++)
            {
                try
                {
                    events[i] = createX10Event((String)commands.elementAt(i));
                }
                catch (IllegalArgumentException e)
                {
                    out.print("Error: " + e.getMessage() + " in command line \"" + line + "\"\r\n");
                    out.flush();
                    return;
                }
            }

            // send X10 Events to gateway.
            synchronized (transmitter)
            {
                for (int j = 0; j < events.length; j++)
                {
                    if (System.getProperty("DEBUG") != null)
                    {
                        System.out.println("Transmitting: " + events[j]);           
                    }
                    try
                    {
                        transmitter.transmit(events[j]);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * Convert command to X10Event.
         *
         * @param command X10 command
         * @return X10Event for the command
         * @author Jesse Peterson <jesse@jpeterson.com>
         */
        protected X10Event createX10Event(String command)
        {
            char houseCode;
            String keyCode;

            if (command.length() < 3)
            {
                throw new IllegalArgumentException("Command " + command + " is less than 3 characters long");
            }

            // get house code
            houseCode = command.charAt(0);
            keyCode = command.substring(1, 3);

            if (keyCode.equals("01"))
            {
                return new AddressEvent(this, houseCode, 1);
            }
            else if (keyCode.equals("02"))
            {
                return new AddressEvent(this, houseCode, 2);
            }
            else if (keyCode.equals("03"))
            {
                return new AddressEvent(this, houseCode, 3);
            }
            else if (keyCode.equals("04"))
            {
                return new AddressEvent(this, houseCode, 4);
            }
            else if (keyCode.equals("05"))
            {
                return new AddressEvent(this, houseCode, 5);
            }
            else if (keyCode.equals("06"))
            {
                return new AddressEvent(this, houseCode, 6);
            }
            else if (keyCode.equals("07"))
            {
                return new AddressEvent(this, houseCode, 7);
            }
            else if (keyCode.equals("08"))
            {
                return new AddressEvent(this, houseCode, 8);
            }
            else if (keyCode.equals("09"))
            {
                return new AddressEvent(this, houseCode, 9);
            }
            else if (keyCode.equals("10"))
            {
                return new AddressEvent(this, houseCode, 10);
            }
            else if (keyCode.equals("11"))
            {
                return new AddressEvent(this, houseCode, 11);
            }
            else if (keyCode.equals("12"))
            {
                return new AddressEvent(this, houseCode, 12);
            }
            else if (keyCode.equals("13"))
            {
                return new AddressEvent(this, houseCode, 13);
            }
            else if (keyCode.equals("14"))
            {
                return new AddressEvent(this, houseCode, 14);
            }
            else if (keyCode.equals("15"))
            {
                return new AddressEvent(this, houseCode, 15);
            }
            else if (keyCode.equals("16"))
            {
                return new AddressEvent(this, houseCode, 16);
            }
            else if (keyCode.equals(ALL_UNITS_OFF_KEY_CODE))
            {
                // all units off
                return new AllUnitsOffEvent(this, houseCode);
            }
            else if (keyCode.equals(ALL_LIGHTS_ON_KEY_CODE))
            {
                // all lights on
                return new AllLightsOnEvent(this, houseCode);
            }
            else if (keyCode.equals(ON_KEY_CODE))
            {
                // on
                return new OnEvent(this, houseCode);
            }
            else if (keyCode.equals(OFF_KEY_CODE))
            {
                // off
                return new OffEvent(this, houseCode);
            }
            else if (keyCode.equals(DIM_KEY_CODE))
            {
                // dim
                if (command.length() < 4)
                {
                    return new DimEvent(this, houseCode, 5, 22);
                }

                String temp = command.substring(3);
                try
                {
                    return new DimEvent(this, houseCode,
                                        Integer.parseInt(temp), 22);
                }
                catch (NumberFormatException e)
                {
                    throw new IllegalArgumentException("Unable to convert " + temp + " to a number");
                }
            }
            else if (keyCode.equals(BRIGHT_KEY_CODE))
            {
                // bright
                if (command.length() < 4)
                {
                    return new BrightEvent(this, houseCode, 5, 22);
                }

                String temp = command.substring(3);
                try
                {
                    return new BrightEvent(this, houseCode,
                                           Integer.parseInt(temp), 22);
                }
                catch (NumberFormatException e)
                {
                    throw new IllegalArgumentException("Unable to convert " + temp + " to a number");
                }
            }
            else if (keyCode.equals(ALL_LIGHTS_OFF_KEY_CODE))
            {
                // all lights off
                return new AllLightsOffEvent(this, houseCode);
            }
            else if (keyCode.equals(HAIL_REQUEST_KEY_CODE))
            {
                // hail request
                return new HailRequestEvent(this, houseCode);
            }
            else if (keyCode.equals(HAIL_ACKNOWLEDGE_KEY_CODE))
            {
                // hail acknowledge
                return new HailAcknowledgeEvent(this, houseCode);
            }
            else if (keyCode.equals(PRESET_DIM_1_KEY_CODE))
            {
                // preset dim 1
                return new PresetDim1Event(this, houseCode);
            }
            else if (keyCode.equals(PRESET_DIM_2_KEY_CODE))
            {
                // preset dim 2
                return new PresetDim2Event(this, houseCode);
            }
            else if (keyCode.equals(EXTENDED_DATA_KEY_CODE))
            {
                // extended data
                throw new IllegalArgumentException("Extended data event not yet implemented");
            }
            else if (keyCode.equals(STATUS_ON_KEY_CODE))
            {
                // status on
                return new StatusOnEvent(this, houseCode);
            }
            else if (keyCode.equals(STATUS_OFF_KEY_CODE))
            {
                // status off
                return new StatusOffEvent(this, houseCode);
            }
            else if (keyCode.equals(STATUS_REQUEST_KEY_CODE))
            {
                // status request
                return new StatusRequestEvent(this, houseCode);
            }
            else
            {
                throw new IllegalArgumentException("Unknown command " + command);
            }
        }
    }
}
