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

/**
 * The Class Age.
 * 
 * @author Tomsky
 */
public class Age {
    
    /** The Constant YEAR. */
    protected static final String YEAR = "year";
    
    /** The Constant YEARS. */
    protected static final String YEARS = "years";
    
    /** The Constant MONTH. */
    protected static final String MONTH = "month";
    
    /** The Constant MONTHS. */
    protected static final String MONTHS = "months";
    
    /** The Constant WEEK. */
    protected static final String WEEK = "week";
    
    /** The Constant WEEKS. */
    protected static final String WEEKS = "weeks";
    
    /** The Constant DAY. */
    protected static final String DAY = "day";
    
    /** The Constant DAYS. */
    protected static final String DAYS = "days";
    
    /** The Constant HOUR. */
    protected static final String HOUR = "hour";
    
    /** The Constant HOURS. */
    protected static final String HOURS= "hours";
    
    /** The Constant MINUTE. */
    protected static final String MINUTE = "minute";
    
    /** The Constant MINUTES. */
    protected static final String MINUTES = "minutes";    
    
    /** The first dimension. */
    protected String firstDimension = "";
    
    /** The second dimension. */
    protected String secondDimension = "";
    
    /**
     * Instantiates a new age.
     * 
     * @param years the years
     * @param months the months
     * @param weeks the weeks
     * @param days the days
     * @param hours the hours
     * @param minutes the minutes
     */
    public Age(int years, int months, int weeks, int days, int hours, int minutes) {
        if (years != 0) {
            this.firstDimension = Integer.toString(years);
            if (years == 1) {
                this.firstDimension += " " + Age.YEAR;
            } else {
                this.firstDimension += " " + Age.YEARS;                
            }
            if (months != 0) {
                this.secondDimension = Integer.toString(months);
                if (months == 1) {
                    this.secondDimension += " " + Age.MONTH;
                } else {
                    this.secondDimension += " " + Age.MONTHS; 
                }
            }
        } else if (months != 0) {
            this.firstDimension = Integer.toString(months);
            if (months == 1) {
                this.firstDimension += " " + Age.MONTH;
            } else {
                this.firstDimension += " " + Age.MONTHS; 
            }            
            if (weeks != 0) {
                 this.secondDimension = Integer.toString(weeks);
                if (weeks == 1) {
                    this.secondDimension += " " + Age.WEEK;
                } else {
                    this.secondDimension += " " + Age.WEEKS; 
                }
            }
        } else if (weeks != 0) {
            this.firstDimension = Integer.toString(weeks);
            if (weeks == 1) {
                this.firstDimension += " " + Age.WEEK;
            } else {
                this.firstDimension += " " + Age.WEEKS; 
            }            
            if (days != 0) {
                this.secondDimension = Integer.toString(days);
                if (days == 1) {
                    this.secondDimension += " " + Age.DAY;
                } else {
                    this.secondDimension += " " + Age.DAYS; 
                }                
            }
        } else if (days != 0) {
            this.firstDimension = Integer.toString(days);
            if (days == 1) {
                this.firstDimension += " " + Age.DAY;
            } else {
                this.firstDimension += " " + Age.DAYS; 
            }                   
            if (hours != 0) {
                this.secondDimension = Integer.toString(hours);
                if (hours == 1) {
                    this.secondDimension += " " + Age.HOUR;
                } else {
                    this.secondDimension += " " + Age.HOURS; 
                }                       
            }
        } else if (hours != 0) {
            this.firstDimension = Integer.toString(hours);
            if (hours == 1) {
                this.firstDimension += " " + Age.HOUR;
            } else {
                this.firstDimension += " " + Age.HOURS; 
            }                   
            if (minutes != 0) {
                this.secondDimension = Integer.toString(minutes);
                if (minutes == 1) {
                    this.secondDimension += " " + Age.MINUTE;
                } else {
                    this.secondDimension += " " + Age.MINUTES; 
                }                       
            }
        } else if (minutes != 0) {
            this.firstDimension = Integer.toString(minutes);
            if (minutes == 1) {
                this.firstDimension += " " + Age.MINUTE;
            } else {
                this.firstDimension += " " + Age.MINUTES; 
            }
        } else {
            this.firstDimension = "0 minutes";
        }            
    }
    
    /**
     * Gets the first dimension.
     * 
     * @return the first dimension
     */
    public String getFirstDimension() {
        return this.firstDimension;
    }
    
    /**
     * Gets the second dimension.
     * 
     * @return the second dimension
     */
    public String getSecondDimension() {
        return this.secondDimension;
    }
    
    public String toString() {
        return this.firstDimension + " " + this.secondDimension;
    }
}

