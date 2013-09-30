/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */

/**
 * Display progress on page, all the params is a json object and is required.
 * @param int params.type full string
 * @param int params.interval timeInterval of Number
 * @param String params.showMethod show the progressData in the page.
 * @param Boolean params.endMethod make the page display as progress is end. 
 *	
 * Usage: new Progress({'type':'commit', 'interval':2000, 'showMethod':showProgress, 'endMethod': endAnimation}).refresh().									 
 */
var Progress = function (params){
	var timer = 0;
	var self = this;
	function getProgress(){
		$.getJSON("progress.htm?method=getProgress",{type:params.type, r:Math.random()}, function(json) {
			params.showMethod.call(self,json);
	         if (json.status == "isEnd") {
	            clearInterval(timer);
	            timer = 0;
	            params.endMethod.call(self);
	         }
	      });
	};
	
	self.refresh = function(){
		timer = setInterval(function(){getProgress();},params.interval);
	};
	self.show = function(){
		getProgress();
	};
};


