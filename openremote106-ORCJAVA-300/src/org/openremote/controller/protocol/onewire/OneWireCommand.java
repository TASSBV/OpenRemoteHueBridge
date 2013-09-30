/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.onewire;

import java.io.IOException;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.utils.Logger;
import org.owfs.jowfsclient.Enums.*;
import org.owfs.jowfsclient.OwfsClient;
import org.owfs.jowfsclient.OwfsClientFactory;
import org.owfs.jowfsclient.OwfsException;

/**
 * 1-wire protocol implementation for OpenRemote. Program accesses 1-wire network (owserver) via
 * library jowfsclient.
 *
 * Parameters used:
 *
 * hostname        hostname of owserver
 * port            port where owserver is running, by default it is 4304
 * deviceAddress   address of the 1-wire device, such as /1F.E9E803000000/main/28.25E9E3010000
 * filename        or sensor attribute - is filename in owfs that holds values, such as
 *                 "temperature", "temperature9", "humidity" or similar
 * refreshTime     time interval in seconds between two calls to owserver; any request between
 *                 these two calls will use cached value. In fact, it is cache timeout value.
 *
 * @author <a href="mailto:jmisura@gmail.com">Jaroslav Misura</a>
 * @author Marcus
 */
public class OneWireCommand implements ExecutableCommand, EventListener, Runnable
{

  // Class Members --------------------------------------------------------------------------------

  private final static Logger logger = Logger.getLogger(OneWireCommandBuilder.ONEWIRE_PROTOCOL_LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  private String hostname;
  private int port = 4304;
  private String deviceAddress;
  private String filename;
  private int pollingInterval;
  private TemperatureScale tempScale;
  private String data;

  /** The thread that is used to peridically update the sensor */
  private Thread pollingThread;
  
  /** The sensor which is updated */
  private Sensor sensor;
  
  /** Boolean to indicate if polling thread should run */
  boolean doPoll = false;

  // Constructors ---------------------------------------------------------------------------------

  public OneWireCommand(String hostname, int port, String deviceAddress, String filename, int pollingInterval, TemperatureScale tempScale, String data)
  {
    this.hostname = hostname;
    this.port = port;
    this.deviceAddress = deviceAddress;
    this.filename = filename;
    this.pollingInterval = pollingInterval;
    this.tempScale = tempScale;
    this.data = data;

    logger.debug("OneWireCommand created with values hostname=" + hostname +
               "; port=" + port + "; deviceAddress=" + deviceAddress + "; filename=" +filename +
               "; pollingInterval=" + pollingInterval);
  }


  // Implements StatusCommand ---------------------------------------------------------------------

  /**
   * Access value from 1-wire sensor. This method uses caching to avoid overloading 1-wire network with
   * too many accesses. After value is read it is stored together with timestamp. This value is returned in all
   * succeeding calls of read() until current time > timestamp + refresh timeout. If read fails (read from 1-wire
   * returns null or exception is thrown by jowfs library) last available value is returned. In this case, timestamp
   * is not updated, which means that next read() call will retry to get value from 1-wire.
   * <p>
   * Values from 1-wire sensors are just passed as string the way they are into sensor.update(). The sensor
   * has to map the result to it's internal data type.
   *
   * @return string value independent from sensor type
   */
  private String read()
  {
    logger.debug("1-Wire sensor " + deviceAddress + "/" + filename + " value is going to be read.");
    OwfsClient client = OwfsClientFactory.newOwfsClient(hostname, port, true);

    client.setDeviceDisplayFormat(OwDeviceDisplayFormat.OWNET_DDF_F_DOT_I);
    client.setBusReturn(OwBusReturn.OWNET_BUSRETURN_ON);
    client.setPersistence(OwPersistence.OWNET_PERSISTENCE_ON);
    switch (tempScale) {
      case Celsius:
        client.setTemperatureScale(OwTemperatureScale.OWNET_TS_CELSIUS);  
        break;
      case Fahrenheit:
         client.setTemperatureScale(OwTemperatureScale.OWNET_TS_FAHRENHEIT);  
         break;
      case Kelvin:
         client.setTemperatureScale(OwTemperatureScale.OWNET_TS_KELVIN);  
         break;
      case Rankine:
         client.setTemperatureScale(OwTemperatureScale.OWNET_TS_RANKINE);  
         break;
   }
    
    String value = null;

    logger.debug("Client created, before call");

    try
    {
      value = client.read(deviceAddress+"/"+filename);
    }
    catch (Exception e)
    {
      logger.error("Unable to read from OWSERVER.", e);
      value = null;
    }
    logger.debug("After client call, value = '"+value+"'");

    if (null == value)
    {
      return "N/A";
    }

    value = value.trim();
    logger.debug("Sensor " + deviceAddress + "/" + filename + " returns value '"+ value + "'");
    return value;
  }


  // Implements ExecutableCommand -----------------------------------------------------------------

  /**
   * Takes the given 'data' and writes it to the given attribute
   *
   */
  public void send()
  {

    logger.debug("1-Wire sensor " + deviceAddress + "/" + filename + " value is going to be changed to: '" + data);

    OwfsClient client = OwfsClientFactory.newOwfsClient(hostname, port, true);

    client.setDeviceDisplayFormat(OwDeviceDisplayFormat.OWNET_DDF_F_DOT_I);
    client.setBusReturn(OwBusReturn.OWNET_BUSRETURN_ON);
    client.setPersistence(OwPersistence.OWNET_PERSISTENCE_ON);
    try
    {
      client.write(deviceAddress+"/"+filename, data);
    }
    catch (OwfsException e)
    {
      logger.error("OneWire error, unable to write to OWSERVER.", e);
    }
    catch (IOException e)
    {
      logger.error("OneWire IO error, unable to write to OWSERVER.", e);
    }
  }


  @Override
  public void setSensor(Sensor sensor)
  {
    logger.debug("*** setSensor called as part of EventListener init *** sensor is: " + sensor);
    this.sensor = sensor;
    this.doPoll = true;
    pollingThread = new Thread(this);
    pollingThread.setName("Polling thread for sensor: " + sensor.getName());
    pollingThread.start();
  }


  @Override
  public void stop(Sensor sensor)
  {
    this.doPoll = false;
  }


  @Override
  public void run()
  {
    logger.debug("Sensor thread started for sensor: " + sensor);
    while (doPoll) {
       String readValue = this.read();
       sensor.update(readValue);
       try {
          Thread.sleep(pollingInterval); // We wait for the given pollingInterval before requesting URL again
       } catch (InterruptedException e) {
          doPoll = false;
          pollingThread.interrupt();
       }
    }
    logger.debug("*** Out of run method: " + sensor);
  }


}
