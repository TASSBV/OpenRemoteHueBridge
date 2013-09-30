<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">   
   <title>OpenRemote Beehive - Changes From Update</title>
   <script type="text/javascript" src="js/progress.js"></script>
   <script type="text/javascript">
   $(document).ready(function() {	   
	   if($('#commitStatus').val() == "running"){
           $('#message').text(" The committing is running, please update later.");
           $("#updateBtn").attr("disabled","true").addClass("disabled_button");
     }
	   if($('#updateStatus').val() == "running"){
		   setAnimation();
		   var progress = new Progress({'type':'update', 'interval':5000, 'showMethod':showProgress, 'endMethod': endAnimation});
		   progress.show();
         progress.refresh();
		   $('#message').text("Updating form http://lirc.sourceforge.net/remotes ......");
	   }
	   $('#updateBtn').click(function(){
		      setAnimation();
			   $.post("sync.htm?method=update",{});
			   new Progress({'type':'update', 'interval':5000, 'showMethod':showProgress, 'endMethod': endAnimation}).refresh();
			   $('#message').text("Updating from http://lirc.sourceforge.net/remotes ......");
	      });
	 });
   
	function showProgress(progress){
		$('#updateInfo').html("<pre>"+progress.data+"</pre>");
		var infoContainer = $("#infoContainer");
		infoContainer[0].scrollTop = infoContainer[0].scrollHeight;
		var bar = $('#progressbar');
      bar.find('.progress').css("width",progress.percent);
      bar.find('.text').text(progress.percent);
	}
	function endAnimation(){
		$('#tab_2 img').attr("src","image/update_icon.gif");
      $('#updateBtn').removeAttr("disabled").removeClass("disabled_button");
      $('#spinner').hide();
      $('#message').html("Update completed, you can view and commit the <b><a href='changes.htm' style='text-decoration: underline;'>changes</a></b>");
	}	
	function setAnimation(){
		$('#tab_2 img').attr("src","image/update.gif");
      $('#updateBtn').attr("disabled","true").addClass("disabled_button");
      $('#spinner').show();
	}
</script>
</head>
	<body tabId="2">
	<table class="infopanel" width="100%" border="0" cellpadding="0"
		cellspacing="0">	
			<tr>
				<td width="100%">
				<table class="tabcontent" width="100%" border="0" cellpadding="0"
					cellspacing="0">
					   <tr>
					      <td colspan="5" style="padding-left: 20px;" class="value"><b>Last update:</b></td>
                     <td colspan="5" class="value">
                        <c:choose>
                           <c:when test="${lastUpdate ne null}">
			                     ${lastUpdate.startDate}&nbsp;(${lastUpdate.status })
                           </c:when>
                           <c:otherwise>
                              There is no sync yet.
                           </c:otherwise>
                        </c:choose>
                     </td>
                     <td></td>
                  </tr>
						<tr>
							<td class="value" style="padding-left: 20px;" colspan="5"
								width="10%"><b>Progress</b>&nbsp;:</td>
							<td class="value" colspan="5" width="80%">
								<div id="progressbar" style="float:left">
									<div class="text">0%</div>
									<div class="progress" >
									<span class="text" >0%</span>
									</div>
								</div><span id="message" style="margin-left:10px">Please click the update button to sync lirc files with lirc website.</span>
							</td>
							<td class="value" colspan="5" width="10%"><input id="updateBtn" value="Update" class="button" type="button">
							</td>
						</tr>
				</table>
				</td>	
			</tr>			
	</table>
	<div id="infoContainer" class="infoContainer">
	   <div id="updateInfo"></div>
	   <div id="spinner"><img alt="" src="image/spinner.gif" /></div>
	</div>

</body>
</html>