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

$(document).ready(function() {

    // Form View -- Online Sync or Manual Upload ------------------------------

	var checkedMode = $("input[name='mode']:checked").val();

	if (checkedMode == 'offline') {
		showOffline();
	}

    else {
		showOnline();
	}
	
	$('#online').click(function() {
		showOnline();
	});

	$('#offline').click(function() {
		showOffline();
	});


    // Manual Upload ----------------------------------------------------------

    var uploadFormOptions = {

        dataType: "html",
        
        error: function(response) {
            error(response.responseText);
        },

        success: function(textStatus) {
            message(textStatus);
        }
    };

    $('#uploadForm').ajaxForm(uploadFormOptions);

    $('#uploadSubmit').click(function() {

        clearMessage();
        showUpdateIndicator();

        var zipPath = $('#zip').val();

        if (zipPath == '') {
            error("Please select a zip first");
            return false;
        }

        else if(!/.+?\.zip/.test(zipPath)) {
            error("Only zip is allowed");
            return false;
        }
    });



    // Online Sync ------------------------------------------------------------

    var syncFormOptions = {

        error: function(response) {
            error(response.responseText);
        },

        success: function() {
            message("Sync Complete.");
        }
    };

    $('#syncForm').ajaxForm(syncFormOptions);


    $('#syncSubmit').click(function() {
    	clearMessage();
    	showUpdateIndicator();
    });



    // Refresh Button ---------------------------------------------------------
    
	$('#refresh').click(function() {

		clearMessage();
		showRefreshIndicator();

        // TODO :
        //   the target for this GET request should be an admin REST interface in the
        //   controller -- See ORCJAVA-173
        
        $.get("config.htm?method=refreshController",
			function(msg) {

				if (msg == 'OK') {
					message("Finished reloading configuration.");
				}

                else {
					error("Failed to reload configuration and clear cache! " + msg);
				}
			}
		 );
	});

	$("#version").append(getVersionLabel());

	getControllerLinkedStatus();
});



// Functions ------------------------------------------------------------------

function getControllerLinkedStatus() {
    $.get("config.htm?method=getControllerLinkedStatus",
    	function(msg) {
    		if (msg.substring(0, 2) == "no") {
    			return;
    		} else if (msg.substring(0, 1) == "-") {
    			showNotLinked();
    			$("#macAddress").append(msg.substring(1,msg.length));
    		} else {
    			$("#accountId").append(msg);
    			showLinked();
    		}
		}
    );
}

function showOnline() {
	$('#online-cont').show();
	$('#offline-cont').hide();
	clearMessage();
}

function showOffline() {
	$('#offline-cont').show();
	$('#online-cont').hide();
	clearMessage();
}

function showLinked() {
	$('#linked').show();
	$('#not-linked').hide();
}

function showNotLinked() {
	$('#not-linked').show();
	$('#linked').hide();
}

function message(msg) {

	hideUpdateIndicator();
	hideRefreshIndicator();

	$('#errMsg').text("").hide();

	if (msg == '') {
		$('#msg').hide().text(msg);
	}

    else {
		$('#msg').hide().show().text(msg);
	}
}

function error(msg) {

    hideUpdateIndicator();
	hideRefreshIndicator();

	$('#msg').text("").hide();

	if (msg == '') {
		$('#errMsg').hide().text(msg);
	}

    else {
		$('#errMsg').hide().show().text(msg);
	}
}

function clearMessage() {
	message("");
	error("");
}

function showUpdateIndicator() {
	$('#update_indicator').show();
}

function hideUpdateIndicator() {
	$('#update_indicator').hide();
}

function showRefreshIndicator() {
	$('#refresh_indicator').show();
}

function hideRefreshIndicator() {
	$('#refresh_indicator').hide();
}

function getVersionLabel() {

    var headUrl = "$HeadURL: http://svn.code.sf.net/p/openremote/code/patches/individual/Pro-Controller_1_0_6_ORCJAVA-300/web/js/index.js $";
    var revision = "$Revision: 6648 $";
    var result = "Untagged";
    var verStr = "";
    var tagStart = -1;

    if ((tagStart = headUrl.indexOf("tags")) >= 0) {

        var tagsEnd = headUrl.indexOf("/", tagStart + 5);

        if (tagsEnd >= 0) {
          verStr = headUrl.substring(tagStart + 5, tagsEnd);
        }
    }

    else if ((tagStart = headUrl.indexOf("branches")) >= 0) {

        var tagsEnd = headUrl.indexOf("/", tagStart + 9);

        if (tagsEnd >= 0) {
          verStr = headUrl.substring(tagStart + 9, tagsEnd);
          verStr = "Branch: " + verStr;
        }
    }

    else if (revision != null) {

        tagStart = revision.indexOf("$Revision:");

        var tagsEnd = revision.indexOf("$", tagStart + 10);

        if (tagsEnd >= 0) {
          verStr = " r" + $.trim(revision.substring(tagStart + 10, tagsEnd));
        }
    }

    if (verStr.length != 0) {
       result = verStr.replace('_', '.');
    }

    return result;
}

