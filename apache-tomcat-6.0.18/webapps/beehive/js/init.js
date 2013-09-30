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

$(document).ready(function() {
	var SVN_COMMIT_ERROR = 430;
	var SVN_GETINFO_ERROR = 431;
	var SVN_ROLLBACK_ERROR = 436;
	var SVN_IO_ERROR = 441;
	var CRAWLER_WRITEFILE_ERROR = 445;
	var CRAWLER_NETWORK_ERROR = 446;
	$("body").ajaxError(function(event, request, settings) {
        if (request.status == SVN_COMMIT_ERROR) {
        	$.unblockUI();
        	clearInterval(timer);
            timer = 0;
            $('#spinner').hide();
            $("#commitSubmit").removeAttr("disabled").removeClass("disabled_button");
            $("#commitSuccessBtn").removeAttr("disabled").removeClass("disabled_button");
        	$.showErrorMsg("Commit changes occur exception! Please refresh the page and try it again.");
        } else if (request.status == SVN_GETINFO_ERROR){
            $.showErrorMsg("Copy file occur exception! Please refresh the page and try it again.");
        } else if (request.status == SVN_ROLLBACK_ERROR){
            $.showErrorMsg("Rollback to this version occur exception! Please refresh the page and try it again.");
        } else if (request.status == SVN_IO_ERROR){
            $.showErrorMsg("SVN IO exception! Please refresh the page and try it again.");
        } else if  (request.status == CRAWLER_WRITEFILE_ERROR){
        	$.showErrorMsg("Write file occur exception! Please refresh the page and try it again.");
        } else if  (request.status == CRAWLER_NETWORK_ERROR){
        	$.showErrorMsg("The network occur exception! Please refresh the page and try it again.");
        } else{
        	$.showErrorMsg("Server error!");
        }
    });
});