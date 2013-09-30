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

package com.jpeterson.x10.module.event;

import java.util.BitSet;
import com.jpeterson.x10.ControlEvent;
import com.jpeterson.x10.module.CM11A;

/**
 * @author Jesse Peterson <jesse@jpeterson.com>
 */
public class CM11AStatusEvent extends ControlEvent
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private char monitoredHouseCode;
    private BitSet onOffStatus;
    private BitSet dimStatus;
    private BitSet lastAddressedDevice;
    private int currentDay;
    private int julianDay;
    private int hours, minutes, seconds;
    private int batteryUsage;

    /**
     * The CM11A received a status event.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public static final int STATUS = 23;

    /**
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public CM11AStatusEvent(CM11A source, int batteryUsage, int seconds, int minutes, int hours, int julianDay, int currentDay, char monitoredHouseCode, BitSet lastAddressedDevice, BitSet onOffStatus, BitSet dimStatus)
    {
        super(source, STATUS);
        this.batteryUsage = batteryUsage;
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
        this.julianDay = julianDay;
        this.currentDay = currentDay;
        this.monitoredHouseCode = monitoredHouseCode;
        this.lastAddressedDevice = lastAddressedDevice;
        this.onOffStatus = onOffStatus;
        this.dimStatus = dimStatus;
    }

    /**
     * The CM11A's idea of what the current battery usage is.
     *
     * @return The CM11A's idea of what the current battery usage is.
     *         Expressed in minutes.
     *
     * @author <a href="mailto:jesse@jpeterson.com">Jesse Peterson</a>
     */
    public int getBatteryUsage()
    {
        return(batteryUsage);
    }

    /**
     * The CM11A's idea of what the current second is.
     *
     * @return The CM11A's idea of what the current second is.
     *
     * @author <a href="mailto:jesse@jpeterson.com">Jesse Peterson</a>
     */
    public int getSeconds()
    {
        return(seconds);
    }

    /**
     * The CM11A's idea of what the current minute is.
     *
     * @return The CM11A's idea of what the current minute is.
     *
     * @author <a href="mailto:jesse@jpeterson.com">Jesse Peterson</a>
     */
    public int getMinutes()
    {
        return(minutes);
    }

    /**
     * The CM11A's idea of what the current hour is.
     *
     * @return The CM11A's idea of what the current hour is.
     *
     * @author <a href="mailto:jesse@jpeterson.com">Jesse Peterson</a>
     */
    public int getHours()
    {
        return(hours);
    }

    /**
     * Retrieve the CM11A's idea of the current day of the year. The value
     * is zero based; 0 for January 1, 31 for February 1, ... The value is
     * initialized after a call to <code>updateStatus()</code>.
     * <p>
     * The utility methods <code>CM11A.extractMonth()</code> and
     * <code>CM11A.extractDay()</code> have been provided to convert this
     * value into month and day representations.
     * <p>
     * As far as I can tell, the CM11A has no way of determining
     * leap years. It therefore always uses 366 days in a year. The
     * caller is responsible for determining if the current day
     * has been corrected for a non-leap year.
     *
     * @return Day of year.
     *
     * @author <a href="mailto:jesse@jpeterson.com">Jesse Peterson</a>
     */
    public int getJulianDay()
    {
        return(julianDay);
    }

    /**
     * Retrieve the CM11A's idea of what the current day is.
     *
     * @return The CM11A's idea of what the current day is. Will be one of
     *         Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
     *         Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY,
     *         Calendar.SATURDAY.
     *
     * @author <a href="mailto:jesse@jpeterson.com">Jesse Peterson</a>
     */
    public int getCurrentDay()
    {
        return(currentDay);
    }

    /**
     * Get the house code that the CM11A is configured to monitor. The
     * CM11A device will record changes to devices on the monitored house
     * code. It record the on/off status and if a device is dimmed or not.
     * The data can be retrieved from the methods getOnOffStatus,
     * getDimStatus, get last addressed device.
     *
     * @return The character from 'A' through 'P' that indicates the
     *         house code that is being monitored.
     *
     * @author <a href="mailto:jesse@jpeterson.com">Jesse Peterson</a>
     */
    public char getMonitoredHouseCode()
    {
        return(monitoredHouseCode);
    }

    /**
     * Indication of the last addressed device as the CM11A sees it for the
     * monitored house code.
     *
     * @return The bit is set (true) if the device was addressed, not set (false)
     *         if the device was not addressed. The bit index indicates the device:
     *         bit index 0 = device 1, bit index 1 = device 2, ...,
     *         bit index 15 = device 16.
     *
     * @author <a href="mailto:jesse@jpeterson.com">Jesse Peterson</a>
     */
    public BitSet getLastAddressedDevice()
    {
        return(lastAddressedDevice);
    }

    /**
     * Indication of the On/Off status as the CM11A sees it for the
     * monitored house code.
     *
     * @return The bit is set (true) if the device is on, not set (false)
     *         if the device is off. The bit index indicates the device:
     *         bit index 0 = device 1, bit index 1 = device 2, ...,
     *         bit index 15 = device 16.
     *
     * @author <a href="mailto:jesse@jpeterson.com">Jesse Peterson</a>
     */
    public BitSet getOnOffStatus()
    {
        return(onOffStatus);
    }

    /**
     * Indication of the dim status as the CM11A sees it for the
     * monitored house code.
     *
     * @return The bit is set (true) if the device is dimmed, not set (false)
     *         if the device is not dimmed. The bit index indicates the device:
     *         bit index 0 = device 1, bit index 1 = device 2, ...,
     *         bit index 15 = device 16.
     * @see updateStatus
     *
     * @author <a href="mailto:jesse@jpeterson.com">Jesse Peterson</a>
     */
    public BitSet getDimStatus()
    {
        return(dimStatus);
    }

    /**
     * Returns a parameter string identifying the event. This method is
     * useful for event-logging and for debugging.
     *
     * @return a string identifying the event
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public String paramString()
    {
        return("CM11AStatusEvent - source = [" + getSource() + "] ID = [" + getId() + "] BatteryUsage = [" + getBatteryUsage() + "] Hours = [" + getHours() + "] Minutes = [" + getMinutes() + "] Seconds = [" + getSeconds() + "] Julian Day = [" + getJulianDay() + "] Current Day = [" + getCurrentDay() + "] Monitored House Code = [" + getMonitoredHouseCode() + "] Last Addressed Device = [" + getLastAddressedDevice() + "] On/Off Status = [" + getOnOffStatus() + "] Dim Status = [" + getDimStatus() + "]");
    }
}
