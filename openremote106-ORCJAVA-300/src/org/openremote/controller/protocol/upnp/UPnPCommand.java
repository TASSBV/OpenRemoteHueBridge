/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.protocol.upnp;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.cybergarage.upnp.Action;
import org.cybergarage.upnp.Argument;
import org.cybergarage.upnp.ArgumentList;
import org.cybergarage.upnp.ControlPoint;
import org.cybergarage.upnp.Device;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;

/**
 * UPnP Event class
 *
 * @author Mathieu Gallissot
 */
public class UPnPCommand implements ExecutableCommand, StatusCommand {

	private String device;
	private String action;
	private Map<String, String> args;
	private ControlPoint controlPoint;
	private static Logger logger = Logger.getLogger(UPnPCommand.class.getName());

	/**
	 * Constructor of the UPnP event.
	 * 
	 * @param controlPoint
	 *            An UPnP control point already started
	 * @param device
	 *            Device id, preferably the uuid of the device, but friendly
	 *            name can be accepted too.
	 * @param action
	 *            Action to invoke on the remote device. This action must be
	 *            available on the remote device, external tools may be used in
	 *            order to discover devices capabilities.
	 * @param args
	 *            Arguments to pass onto the action. These are mandatory, and
	 *            action specific. An external tool may be used in order to
	 *            discover actions requirements.
	 */
	public UPnPCommand(ControlPoint controlPoint, String device,
                   String action, Map<String, String> args)
  {
		this.device = device;
		this.action = action;
		this.args = args;
		this.controlPoint = controlPoint;

	}

  @SuppressWarnings("unchecked")
	@Override
	public void send() {
		//First, let's grab the device corresponding to the event's id
		Device dev = this.controlPoint.getDevice(this.device);
		if (dev == null) {
			logger.warn("Device not found :" + this.device
					+ " , event aborted.");
			return;
		}

		//then, let's find the action in the device.
		Action act = dev.getAction(this.action);
		if (act == null) {
			logger.warn("Action not found :" + this.action
					+ " , event aborted.");
			return;
		}

		//Format the args list to UPnP's format
		ArgumentList argList = new ArgumentList();

		Iterator<String> i = args.keySet().iterator();
		while (i.hasNext()) {
			String argName = i.next();
			argList.add(new Argument(argName, this.args.get(argName)));
		}

		//Set args for the action
		act.setArgumentValues(argList);

		//Post the action
		act.postControlAction();
		//TODO : handle returned values ?
	}

  /**
   * {@inheritDoc}
   */
  public String read(EnumSensorType sensorType, Map<String, String> statusMap) {
     //TODO
     return null;
  }

}
