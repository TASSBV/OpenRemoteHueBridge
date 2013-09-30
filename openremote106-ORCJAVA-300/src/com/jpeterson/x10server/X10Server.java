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

package com.jpeterson.x10server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.jpeterson.x10.Transmitter;
import com.jpeterson.x10.module.CM11A;

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
public class X10Server extends X10ServerStub 
{
    /**
     * Serialized CM11A.
     */
    protected String serialFile;

    /**
     * X10 gateway.
     */
    protected CM11A cm11A;

    /**
     * Run a X10Server.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public static void main(String[] args)
    {
        int listenPort;
        InetAddress statusAddress = null;
        int statusPort;
        String comPort;
        X10Server server;
        String serialFile = null;

        // set default values
        listenPort = DEFAULT_LISTEN_PORT;
        try
        {
            statusAddress = InetAddress.getByName(DEFAULT_STATUS_ADDRESS);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            System.exit(EXIT_INVALID_LISTEN_PORT);
        }
        statusPort = DEFAULT_STATUS_PORT;
        comPort = DEFAULT_COM_PORT;

        try
        {
            for (int i = 0; i < args.length; i++)
            {
                if (args[i].equals("-listenport"))
                {
                    try
                    {
                        listenPort = Integer.parseInt(args[++i]);
                    }
                    catch (NumberFormatException e)
                    {
                        System.err.println("Invalid listen port number.");
                        usage();
                        System.exit(EXIT_INVALID_LISTEN_PORT);
                    }
                }
                else if (args[i].equals("-statusaddress"))
                {
                    try
                    {
                        statusAddress = InetAddress.getByName(args[++i]);
                    }
                    catch (UnknownHostException e)
                    {
                        System.err.println("Invalid status address.");
                        usage();
                        System.exit(EXIT_INVALID_STATUS_ADDRESS);
                    }
                }
                else if (args[i].equals("-statusport"))
                {
                    try
                    {
                        statusPort = Integer.parseInt(args[++i]);
                    }
                    catch (NumberFormatException e)
                    {
                        System.err.println("Invalid listen port number.");
                        usage();
                        System.exit(EXIT_INVALID_LISTEN_PORT);
                    }
                }
                else if (args[i].equals("-port"))
                {
                    comPort = args[++i];
                }
                else if (args[i].equals("-serialFile"))
                {
                    // CM11A Serialized file
                    serialFile = args[++i];
                }
                else
                {
                    usage();
                    System.exit(EXIT_INVALID_ARGUMENT);
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            usage();
            System.exit(EXIT_INVALID_ARGUMENT);
        }

        server = new X10Server(serialFile, listenPort, statusAddress, statusPort, comPort);
        try
        {
            server.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Print out usage information.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public static void usage()
    {
        System.out.println("X10Server [-serialFile filename] [-listenport port] [-statusaddress IPAddress] [-statusport port] [-com comport]");
        System.out.println("where:");
        System.out.println("  -serialFile filename Once the macros are downloaded, serialize the CM11A");
        System.out.println("                       object out to the file specified by filename.");
        System.out.println();
        System.out.println("  -listenport port");
        System.out.println("     Set the port that the server listens for control to connect to.");
        System.out.println("     Default: " + DEFAULT_LISTEN_PORT);
        System.out.println();
        System.out.println("  -statusaddress IPAddress");
        System.out.println("     Set the status broadcast address that status messages will be sent on.");
        System.out.println("     Must be a valid multicast address from 224.0.0.1 through 239.255.255.255,");
        System.out.println("     inclusive.");
        System.out.println("     Default: " + DEFAULT_STATUS_ADDRESS);
        System.out.println();
        System.out.println(" -statusport port");
        System.out.println("     Set the port that the status messages will be sent to.");
        System.out.println("     Default: " + DEFAULT_STATUS_PORT);
        System.out.println();
        System.out.println(" -port comport");
        System.out.println("     Set the com port that the CM11A can be contacted on.");
        System.out.println("     Default: " + DEFAULT_COM_PORT);
    }

    /**
     * Create a new X10Server.
     *
     * @param serialFile Filename of serialized CM11A.
     * @param listenPort TCP port to listen for client connections on.
     * @param statusAddress UDP multicast address to send status messages
     *        to.
     * @param statusPort UDP port to send status messages to.
     * @param comPort Serial port to talk to CM11A to.
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public X10Server(String serialFile, int listenPort,
                     InetAddress statusAddress, int statusPort,
                     String comPort)
    {
        super(listenPort, statusAddress, statusPort, comPort);
        this.serialFile = serialFile;
    }

    /**
     * Create an instance of an <code>CM11A</code> object, which
     * implements the <code>Transmitter</code> interface.
     */
    protected Transmitter createTransmitter()
    {
        cm11A = null;

        if (serialFile != null)
        {
            // try to deserialize CM11A
            try
            {
                ObjectInputStream istream = new ObjectInputStream(
                    new FileInputStream(serialFile));
                cm11A = (CM11A)istream.readObject();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.err.println("Unable to deserialize CM11A from " + serialFile + ", creating new one...");
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
                System.err.println("Unable to deserialize CM11A from " + serialFile + ", creating new one...");
            }
        }

        if (cm11A == null)
        {
            cm11A = new CM11A();
        }

        cm11A.setPortName(comPort);
        cm11A.addAddressListener(this);
        cm11A.addFunctionListener(this);
        try {
            cm11A.allocate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return(cm11A);
    }
}
