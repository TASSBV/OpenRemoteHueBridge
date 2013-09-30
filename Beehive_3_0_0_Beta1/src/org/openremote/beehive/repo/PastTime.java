/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.beehive.repo;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * The Class PastTime.
 */
public class PastTime {
    
    /** The years. */
    protected int years = 0;
    
    /** The months. */
    protected int months = 0;
    
    /** The weeks. */
    protected int weeks = 0;
    
    /** The days. */
    protected int days = 0;
    
    /** The hours. */
    protected int hours = 0;
    
    /** The minutes. */
    protected int minutes = 0;
    
    /** The seconds. */
    protected int seconds = 0;
    
    /**
     * Instantiates a new past time.
     * 
     * @param first the first
     * @param second the second
     */
    public PastTime(Date first, Date second) {
        if (!first.equals(second)) {
            GregorianCalendar lower = new GregorianCalendar();
            GregorianCalendar greater = new GregorianCalendar();
            if (first.before(second)) {
                lower.setTime(first);
                greater.setTime(second);
            } else {
                lower.setTime(second);
                greater.setTime(first);                
            }
            
            this.processElapsedYears(lower, greater);
            this.processElapsedMonths(lower, greater);
            this.processElapsedWeeks(lower, greater);
            this.processElapsedDays(lower, greater);
            this.processElapsedHours(lower, greater);
            this.processElapsedMinutes(lower, greater);
            this.processElapsedSeconds(lower, greater);
        }
    }
    
    /**
     * Gets the years.
     * 
     * @return the years
     */
    public int getYears() {
        return this.years;
    }
    
    /**
     * Gets the months.
     * 
     * @return the months
     */
    public int getMonths() {
        return this.months;
    }
    
    /**
     * Gets the weeks.
     * 
     * @return the weeks
     */
    public int getWeeks() {
        return this.weeks;
    }
    
    /**
     * Gets the days.
     * 
     * @return the days
     */
    public int getDays() {
        return this.days;
    }
    
    /**
     * Gets the hours.
     * 
     * @return the hours
     */
    public int getHours() {
        return this.hours;
    }
    
    /**
     * Gets the minutes.
     * 
     * @return the minutes
     */
    public int getMinutes() {
        return this.minutes;
    }
    
    /**
     * Gets the seconds.
     * 
     * @return the seconds
     */
    public int getSeconds() {
        return this.seconds;
    }
    
    /**
     * Process elapsed years.
     * 
     * @param lower the lower
     * @param greater the greater
     */
    protected void processElapsedYears(GregorianCalendar lower, GregorianCalendar greater) {
        GregorianCalendar tmp = (GregorianCalendar) lower.clone();
        int dayOfMonth = tmp.get(Calendar.DAY_OF_MONTH);        
        do {
            tmp.set(Calendar.DAY_OF_MONTH, 1);            
            tmp.add(Calendar.YEAR, 1);
            if (tmp.getActualMaximum(Calendar.DAY_OF_MONTH) >= dayOfMonth) {
                tmp.set(Calendar.DAY_OF_MONTH, dayOfMonth);                
            } else {
                tmp.set(Calendar.DAY_OF_MONTH, tmp.getActualMaximum(Calendar.DAY_OF_MONTH));                
            }            
            if (tmp.equals(greater) || tmp.before(greater)) {
                lower.set(Calendar.DAY_OF_MONTH, 1);                
                lower.add(Calendar.YEAR, 1);
                if (lower.getActualMaximum(Calendar.DAY_OF_MONTH) >= dayOfMonth) {
                    lower.set(Calendar.DAY_OF_MONTH, dayOfMonth);                
                } else {
                    lower.set(Calendar.DAY_OF_MONTH, lower.getActualMaximum(Calendar.DAY_OF_MONTH));                
                }                       
                this.years++;
            }
        } while (tmp.before(greater));
    }
    
    /**
     * Process elapsed months.
     * 
     * @param lower the lower
     * @param greater the greater
     */
    protected void processElapsedMonths(GregorianCalendar lower, GregorianCalendar greater) {
        GregorianCalendar tmp = (GregorianCalendar) lower.clone();
        int dayOfMonth = tmp.get(Calendar.DAY_OF_MONTH);
        do {
            tmp.set(Calendar.DAY_OF_MONTH, 1);
            tmp.add(Calendar.MONTH, 1);
            if (tmp.getActualMaximum(Calendar.DAY_OF_MONTH) >= dayOfMonth) {
                tmp.set(Calendar.DAY_OF_MONTH, dayOfMonth);                
            } else {
                tmp.set(Calendar.DAY_OF_MONTH, tmp.getActualMaximum(Calendar.DAY_OF_MONTH));                
            }
            if (tmp.equals(greater) || tmp.before(greater)) {
                lower.set(Calendar.DAY_OF_MONTH, 1);                
                lower.add(Calendar.MONTH, 1);
                if (lower.getActualMaximum(Calendar.DAY_OF_MONTH) >= dayOfMonth) {
                    lower.set(Calendar.DAY_OF_MONTH, dayOfMonth);                
                } else {
                    lower.set(Calendar.DAY_OF_MONTH, lower.getActualMaximum(Calendar.DAY_OF_MONTH));                
                }                
                this.months++;
            }
        } while (tmp.before(greater));        
    }
    
    /**
     * Process elapsed weeks.
     * 
     * @param lower the lower
     * @param greater the greater
     */
    protected void processElapsedWeeks(GregorianCalendar lower, GregorianCalendar greater) {
        GregorianCalendar tmp = (GregorianCalendar) lower.clone();
        do {
            tmp.add(Calendar.WEEK_OF_YEAR, 1);
            if (tmp.equals(greater) || tmp.before(greater)) {
                lower.add(Calendar.WEEK_OF_YEAR, 1);
                this.weeks++;
            }
        } while (tmp.before(greater));        
    }   
    
    /**
     * Process elapsed days.
     * 
     * @param lower the lower
     * @param greater the greater
     */
    protected void processElapsedDays(GregorianCalendar lower, GregorianCalendar greater) {
        GregorianCalendar tmp = (GregorianCalendar) lower.clone();
        do {
            tmp.add(Calendar.DAY_OF_YEAR, 1);
            if (tmp.equals(greater) || tmp.before(greater)) {
                lower.add(Calendar.DAY_OF_YEAR, 1);
                this.days++;
            }
        } while (tmp.before(greater));        
    }   
    
    /**
     * Process elapsed hours.
     * 
     * @param lower the lower
     * @param greater the greater
     */
    protected void processElapsedHours(GregorianCalendar lower, GregorianCalendar greater) {
        GregorianCalendar tmp = (GregorianCalendar) lower.clone();
        do {
            tmp.add(Calendar.HOUR_OF_DAY, 1);
            if (tmp.equals(greater) || tmp.before(greater)) {
                lower.add(Calendar.HOUR_OF_DAY, 1);
                this.hours++;
            }
        } while (tmp.before(greater));        
    }   
    
    /**
     * Process elapsed minutes.
     * 
     * @param lower the lower
     * @param greater the greater
     */
    protected void processElapsedMinutes(GregorianCalendar lower, GregorianCalendar greater) {
        GregorianCalendar tmp = (GregorianCalendar) lower.clone();
        do {
            tmp.add(Calendar.MINUTE, 1);
            if (tmp.equals(greater) || tmp.before(greater)) {
                lower.add(Calendar.MINUTE, 1);
                this.minutes++;
            }
        } while (tmp.before(greater));        
    }    
    
    /**
     * Process elapsed seconds.
     * 
     * @param lower the lower
     * @param greater the greater
     */
    protected void processElapsedSeconds(GregorianCalendar lower, GregorianCalendar greater) {
        GregorianCalendar tmp = (GregorianCalendar) lower.clone();
        do {
            tmp.add(Calendar.SECOND, 1);
            if (tmp.equals(greater) || tmp.before(greater)) {
                lower.add(Calendar.SECOND, 1);
                this.seconds++;
            }
        } while (tmp.before(greater));        
    }
}
