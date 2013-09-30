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

import java.io.Serializable;
import java.util.Calendar;

public class TimerInitiator implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private byte week;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;
    private int stopMonth;
    private int stopDay;
    private int stopHour;
    private int stopMinute;
    private Macro startMacro;
    private Macro stopMacro;

    public static final int SIZEOF_TIMER_INITIATOR = 9;
    private static final byte SUNDAY = (byte)0x01;
    private static final byte MONDAY = (byte)0x02;
    private static final byte TUESDAY = (byte)0x04;
    private static final byte WEDNESDAY = (byte)0x08;
    private static final byte THURSDAY = (byte)0x10;
    private static final byte FRIDAY = (byte)0x20;
    private static final byte SATURDAY = (byte)0x40;

    // maximum number of days in a month. The index into the array is the
    // month, zero based. e.g., 0 for January, 1 for February, 11 for
    // December.
//    private int[] daysInMonth = {31, 29, 31, 30, 31, 30, 31, 31, 30,
//                                 31, 30, 31};

    /**
     * Create a new TimerInitiator.  Initialized with start date set to
     * January 1 and stop date set to December 31.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public TimerInitiator()
    {
        setStartDate(Calendar.JANUARY, 1);
        setStopDate(Calendar.DECEMBER, 31);
    }

    /**
     * Add a day of the week that this TimerInitiator is for.
     *
     * @param weekDay A day of the week constant from the class
     *        <CODE>java.util.Calendar</CODE>. Should be one of the following:
     *        <CODE>Calendar.SUNDAY</CODE>, <CODE>Calendar.MONDAY</CODE>,
     *        <CODE>Calendar.TUESDAY</CODE>, <CODE>Calendar.WEDNESDAY</CODE>,
     *        <CODE>Calendar.THURSDAY</CODE>, <CODE>Calendar.FRIDAY</CODE>,
     *        or <CODE>Calendar.SATURDAY</CODE>.
     * @see java.util.Calendar
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void addDayOfWeek(int weekDay)
    {
        switch (weekDay)
        {
            case Calendar.SUNDAY:
            week |= SUNDAY;
            break;

            case Calendar.MONDAY:
            week |= MONDAY;
            break;

            case Calendar.TUESDAY:
            week |= TUESDAY;
            break;

            case Calendar.WEDNESDAY:
            week |= WEDNESDAY;
            break;

            case Calendar.THURSDAY:
            week |= THURSDAY;
            break;

            case Calendar.FRIDAY:
            week |= FRIDAY;
            break;

            case Calendar.SATURDAY:
            week |= SATURDAY;
            break;
        }
    }

    /**
     * Remove a day of the week that this TimerInitiator is for.
     *
     * @param weekDay A day of the week constant from the class
     *        <CODE>java.util.Calendar</CODE>. Should be one of the following:
     *        <CODE>Calendar.SUNDAY</CODE>, <CODE>Calendar.MONDAY</CODE>,
     *        <CODE>Calendar.TUESDAY</CODE>, <CODE>Calendar.WEDNESDAY</CODE>,
     *        <CODE>Calendar.THURSDAY</CODE>, <CODE>Calendar.FRIDAY</CODE>,
     *        or <CODE>Calendar.SATURDAY</CODE>.
     * @see java.util.Calendar
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void removeDayOfWeek(int weekDay)
    {
        switch (weekDay)
        {
            case Calendar.SUNDAY:
            week &= (0xff & SUNDAY);
            break;

            case Calendar.MONDAY:
            week &= (0xff & MONDAY);
            break;

            case Calendar.TUESDAY:
            week &= (0xff & TUESDAY);
            break;

            case Calendar.WEDNESDAY:
            week &= (0xff & WEDNESDAY);
            break;

            case Calendar.THURSDAY:
            week &= (0xff & THURSDAY);
            break;

            case Calendar.FRIDAY:
            week &= (0xff & FRIDAY);
            break;

            case Calendar.SATURDAY:
            week &= (0xff & SATURDAY);
            break;
        }
    }

    /**
     * Check to see if a day of the week is set for this TimerInitiator.
     *
     * @param weekDay A day of the week constant from the class
     *        <CODE>java.util.Calendar</CODE>. Should be one of the following:
     *        <CODE>Calendar.SUNDAY</CODE>, <CODE>Calendar.MONDAY</CODE>,
     *        <CODE>Calendar.TUESDAY</CODE>, <CODE>Calendar.WEDNESDAY</CODE>,
     *        <CODE>Calendar.THURSDAY</CODE>, <CODE>Calendar.FRIDAY</CODE>,
     *        or <CODE>Calendar.SATURDAY</CODE>.
     * @see java.util.Calendar
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public boolean checkDayOfWeek(int weekDay)
    {
        switch (weekDay)
        {
            case Calendar.SUNDAY:
            return((week & SUNDAY) == SUNDAY);

            case Calendar.MONDAY:
            return((week & MONDAY) == MONDAY);

            case Calendar.TUESDAY:
            return((week & TUESDAY) == TUESDAY);

            case Calendar.WEDNESDAY:
            return((week & WEDNESDAY) == WEDNESDAY);

            case Calendar.THURSDAY:
            return((week & THURSDAY) == THURSDAY);

            case Calendar.FRIDAY:
            return((week & FRIDAY) == FRIDAY);

            case Calendar.SATURDAY:
            return((week & SATURDAY) == SATURDAY);
        }
        return(false);
    }

    /**
     * Retrieve an array of the weekdays set for this TimerInitiator.
     *
     * @return An array with the weekdays set for this TimerInitiator. The
     *         number of elements in the array indicate the number of days
     *         set. Each element is one of the following:
     *        <CODE>Calendar.SUNDAY</CODE>, <CODE>Calendar.MONDAY</CODE>,
     *        <CODE>Calendar.TUESDAY</CODE>, <CODE>Calendar.WEDNESDAY</CODE>,
     *        <CODE>Calendar.THURSDAY</CODE>, <CODE>Calendar.FRIDAY</CODE>,
     *        or <CODE>Calendar.SATURDAY</CODE>. The 
     * @see java.util.Calendar
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int[] getDaysOfWeek()
    {
        int[] setDays = new int[7];
        int result[];
        int i = 0;

        if ((week & SUNDAY) == SUNDAY)
        {
            setDays[i++] = Calendar.SUNDAY;
        }

        if ((week & MONDAY) == MONDAY)
        {
            setDays[i++] = Calendar.MONDAY;
        }

        if ((week & TUESDAY) == TUESDAY)
        {
            setDays[i++] = Calendar.TUESDAY;
        }

        if ((week & WEDNESDAY) == WEDNESDAY)
        {
            setDays[i++] = Calendar.WEDNESDAY;
        }

        if ((week & THURSDAY) == THURSDAY)
        {
            setDays[i++] = Calendar.THURSDAY;
        }

        if ((week & FRIDAY) == FRIDAY)
        {
            setDays[i++] = Calendar.FRIDAY;
        }

        if ((week & SATURDAY) == SATURDAY)
        {
            setDays[i++] = Calendar.SATURDAY;
        }

        result = new int[i];
        System.arraycopy(setDays, 0, result, 0, i);
        return(result);
    }

    /**
     * Clear the days of the week for this TimerInitiator.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void clearDaysOfWeek()
    {
        week = 0;
    }

    /**
     * Set the start date for this TimerInitiator.
     * The day of the week and day of the year are anded together, so both
     * have to match the current time before the event will trigger a macro.
     *
     * @param month Month, zero based. e.g., 0 for January, 1 for February.
     * @param day Day
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setStartDate(int month, int day)
    {
        month = CM11A.normalizeMonth(month);
        day = CM11A.normalizeDay(month, day);
        startMonth = month;
        startDay = day;
    }

    /**
     * Retrieve the start day for this TimerInitiator.
     *
     * @return The start day for this TimerInitiator.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getStartDay()
    {
        return(startDay);
    }

    /**
     * Retrieve the start month for this TimerInitiator.
     *
     * @return The start month for this TimerInitiator.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getStartMonth()
    {
        return(startMonth);
    }

    /**
     * Set the start time for this TimerInitiator. 
     * Event is triggered if current day is within window of start day and
     * stop day, and current time matches this time.
     *
     * @param hour Hour in 24 hour time, 0 - 23. Example: 14 = 2 PM.
     * @param minute Minute, 0 - 59.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setStartTime(int hour, int minute)
    {
        if ((hour < 0) || (hour > 23))
        {
            throw new IllegalArgumentException("Hour must be between 0 and 23, inclusive.");
        }

        if ((minute < 0) || (minute > 59))
        {
            throw new IllegalArgumentException("Minute must be between 0 and 59, inclusive.");
        }

        startHour = hour;
        startMinute = minute;
    }

    /**
     * Get the start hour for this TimerInitiator.
     *
     * @return Start hour for this timer initiator.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getStartHour()
    {
        return(startHour);
    }

    /**
     * Get the start minute for this TimerInitiator.
     *
     * @return Start minute for this timer initiator.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getStartMinute()
    {
        return(startMinute);
    }

    /**
     * Set the stop day for this TimerInitiator.
     * The day of the week and day of the year are anded together, so both
     * have to match the current time before the event will trigger a macro.
     *
     * @param month Month, zero based. e.g., 0 for January, 1 for February.
     * @param day Day
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setStopDate(int month, int day)
    {
        month = CM11A.normalizeMonth(month);
        day = CM11A.normalizeDay(month, day);
        stopMonth = month;
        stopDay = day;
    }

    /**
     * Retrieve the stop day for this TimerInitiator.
     *
     * @return The stop day for this TimerInitiator.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getStopDay()
    {
        return(stopDay);
    }

    /**
     * Retrieve the stop month for this TimerInitiator.
     *
     * @return The stop month for this TimerInitiator.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getStopMonth()
    {
        return(stopMonth);
    }

    /**
     * Set the stop time for this TimerInitiator. 
     * Event is triggered if current day is within window of start day and
     * stop day, and current time matches this time.
     *
     * @param hour Hour in 24 hour time, 0 - 23. Example: 14 = 2 PM.
     * @param minute Minute, 0 - 59.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setStopTime(int hour, int minute)
    {
        if ((hour < 0) || (hour > 23))
        {
            throw new IllegalArgumentException("Hour must be between 0 and 23, inclusive.");
        }

        if ((minute < 0) || (minute > 59))
        {
            throw new IllegalArgumentException("Minute must be between 0 and 59, inclusive.");
        }

        stopHour = hour;
        stopMinute = minute;
    }

    /**
     * Get the stop hour for this TimerInitiator.
     *
     * @return Stop hour for this timer initiator.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getStopHour()
    {
        return(stopHour);
    }

    /**
     * Get the stop minute for this TimerInitiator.
     *
     * @return Stop minute for this timer initiator.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public int getStopMinute()
    {
        return(stopMinute);
    }

    /**
     * Set the start macro. Triggered at the start time if current day is
     * is within the start day and stop day and the day of the week is set.
     *
     * @param macro Macro to invoke.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setStartMacro(Macro macro)
    {
        startMacro = macro;
    }

    /**
     * Get the start macro.
     *
     * @return The start macro.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public Macro getStartMacro()
    {
        return(startMacro);
    }

    /**
     * Set the stop macro. Triggered at the stop time if current day is
     * is within the start day and stop day and the day of the week is set.
     *
     * @param macro Macro to invoke.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public void setStopMacro(Macro macro)
    {
        stopMacro = macro;
    }

    /**
     * Get the stop macro.
     *
     * @return The stop macro.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public Macro getStopMacro()
    {
        return(stopMacro);
    }

    /**
     * Get the byte array representing this macro initiator.
     *
     * @param startMacroOffset Integer offset to start macro to initiate.
     * @param stopMacroOffset Integer offset to stop macro to initiate.
     * @return Byte array containing the macro initiator.
     *
     * @author Jesse Peterson <jesse@jpeterson.com>
     */
    public byte[] getBytes(int startMacroOffset, int stopMacroOffset)
    {
        byte[] element = null, startEventOffset, stopEventOffset;
        int startDayValue, stopDayValue;
        byte startMinutes, startMinutes120, stopMinutes, stopMinutes120;
        int minutes;

        element = new byte[9];

        // start time in minutes
        minutes = (startHour * 60) + startMinute;
        startMinutes = (byte)(minutes % 120);
        startMinutes120 = (byte)((minutes - startMinutes) / 120);

        // stop time in minutes
        minutes = (stopHour * 60) + stopMinute;
        stopMinutes = (byte)(minutes % 120);
        stopMinutes120 = (byte)((minutes - stopMinutes) / 120);
        if (System.getProperty("DEBUG") != null)
        {
            System.out.println("startMinutes = " + startMinutes);
            System.out.println("startMinutes120 = " + startMinutes120);
            System.out.println("stopMinutes = " + stopMinutes);
            System.out.println("stopMinutes120 = " + stopMinutes120);
        }

        // Day of week mask
        element[0] = week;

        // start day, low byte
        startDayValue = CM11A.dayOfYear(startMonth, startDay);
        element[1] = (byte)(startDayValue & 0xff);

        // stop day, low byte
        stopDayValue = CM11A.dayOfYear(stopMonth, stopDay);
        element[2] = (byte)(stopDayValue & 0xff);

        // start/stop minutes * 120
        element[3] = (byte)((startMinutes120 << 4) | stopMinutes120);

        // start day high bit, event start time
        element[4] = startMinutes;
        if (startDayValue > 0xff)
        {
            // set high bit
            element[4] |= 0x80;
        }

        // stop day high bit, event stop time
        element[5] = stopMinutes;
        if (stopDayValue > 0xff)
        {
            // set high bit
            element[5] |= 0x80;
        }

        startEventOffset = CM11A.toBytes(startMacroOffset, 2, true);
        stopEventOffset = CM11A.toBytes(stopMacroOffset, 2, true);

        // start event offset, high nibble, stop event offset, high nibble
        element[6] = (byte)((startEventOffset[0] << 4) | stopEventOffset[0]);

        // start event offset, low byte
        element[7] = startEventOffset[1];

        // stop event offset, low byte
        element[8] = stopEventOffset[1];

        return(element);
    }
}
