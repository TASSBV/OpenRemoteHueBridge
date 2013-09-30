Sunrise/SunsetLib - Java
============

Description
-----------
Java library to compute the local sunrise and sunset at a given latitude/longitue and date combination.

Dependencies
------------
None

Installation
------------
Download the jar or clone the repo and run $ mvn clean install to build from source.

Usage
-----
Create a SunriseSunsetCalculator with a location and time zone identifier:

    Location location = new Location("39.9522222", "-75.1641667");
    SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "America/New_York");

Then call the method for the type of sunrise/sunset you want to calculate:

    String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
    Calendar officialSunset = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());

Author
------
Mike Reedell: http://mikereedell.github.com/2009/01/27/Sunrise-Sunset-Java-Library.html

[Donate with Pledgie](http://www.pledgie.com/campaigns/15328)

License
-------
Apache License, Version 2.0 
[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)