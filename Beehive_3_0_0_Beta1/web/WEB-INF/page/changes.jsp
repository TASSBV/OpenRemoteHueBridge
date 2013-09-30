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
           if($('#updateStatus').val() == "running"){
               $('#below_message').text(" The updating is running, please commit changes later.");
               $("#commitSubmit").attr("disabled","true").addClass("disabled_button");
           }
           if($('#commitStatus').val() == "running"){
                $('#message').text(" The committing is running...");
                $("#commitSubmit").attr("disabled","true").addClass("disabled_button");
                showBlock();
                var progress = new Progress({'type':'commit', 'interval':2000, 'showMethod':showProgress, 'endMethod': endAnimation});
                progress.show();
                progress.refresh();
           }
           $('#submitForm').ajaxForm(function() {
               $("#commitSubmit").removeAttr("disabled").removeClass("disabled_button");
           });
           $("#commitSubmit").click( function() {
               if($('#diffSize').val()==0){
            	   $('#below_message').html("There is no changes to commit.").css("color","red");
               }else{
	        	      var validator = validateCheck();
	        	      if(validator.form()){
	        	    	   inputComment();
	        	      }
               }
           });
           $('#commit').click(function(){
        	      $("#commitSubmit").attr("disabled","true").addClass("disabled_button");
               $('#comment').val($('#inputComment').val());
               $.unblockUI();
        	      $('#message').text(" The committing is running...");
               $(this).attr("disabled","true").addClass("disabled_button");
               var commitAll = $('#allChangesChecked').val();
               if(commitAll == 'false'){
            	   $('#submitForm').submit();
               }else{
                  var comment = $('#comment').val();
            	   $.post("changes.htm", {method: 'commit', commitAll: 'true', comment: comment},
                    	   function (data){
		                     $("#commitSubmit").removeAttr("disabled").removeClass("disabled_button");
            	   });
               }
               showBlock();
               new Progress({'type':'commit', 'interval':2000, 'showMethod':showProgress, 'endMethod': endAnimation}).refresh();
               });
           $('#cancel').click(function(){
        	      $.unblockUI();
               });
           $("#checkall").click( function() {
               checkAll(this.checked);
           });
           $('#alterCheckAll').click(function() {
        	      if($('#allChangesChecked').val() == 'false'){
        	    	  var checkAllInfo = $('#checkAllInfo');
        	    	  $('#allChangesChecked').val('true');
        	    	  $(this).html('Clear selection');
        	    	  checkAllInfo.find('span:first').html('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;All '+$('#diffSize').val()+' changes are selected.&nbsp;');
        	      }else{
            	  $("#checkall").removeAttr('checked');
        	    	  $("#checkall").click();
        	    	  $("#checkall").removeAttr('checked');
            	}
           });
           $('input.changedNode').each(function(){
        	      var action = $(this).attr('action');
        	      if(action=='UNVERSIONED'||action=='ADDED'){
        	    	  $(this).click(checkAdd);
        	    	}else if(action=='DELETEED'){
        	    		$(this).click(checkDelete);
            	}
           });
           $('#commitSuccessBtn').click(function(){
        	     $.unblockUI();
        	     window.location='changes.htm';
           });
           
       });
       function showProgress(progress){
    	     $("#commitInfo").html("<pre>"+progress.data+"</pre>");
           var infoContainer = $("#infoContainer");
           infoContainer[0].scrollTop = infoContainer[0].scrollHeight;
       }
       function endAnimation(){
    	     $('#spinner').hide();
           $('#message').text(" Commit succeeds!");
           $("#commitSuccessBtn").removeAttr("disabled").removeClass("disabled_button");
       }
       function checkAll(checked){
    	   $("input[name='items']").attr("checked",checked);
           var checkAllInfo = $('#checkAllInfo');
           if(checked){
               $('#alterCheckAll').html("Select all "+$('#diffSize').val()+" changes in workCopy");
               checkAllInfo.find('span:first').html('&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;All 50 changes on this page are selected.&nbsp;');
               checkAllInfo.removeClass('hidden');
           }else{
               checkAllInfo.addClass('hidden');
               $('#allChangesChecked').val('false');
           }
       }
       function validateCheck(){
    	   return $("#submitForm").validate({
               rules:{
                   items:"required"
               },
               messages:{
                  items:"Please select at least one change!"
               },
               errorPlacement:function(error, element){
                   $('#below_message').html(error).css("color","red");
               }
             });
           }
       function showBlock(){
			$.blockUI({
				message: $('#commitView'),
				css: {
	               width: '30%',
	               top: '20%',
	               left: '30%',
	               height: '50%',
	               textAlign: 'left',
	               cursor: 'default'
	            }
			});
         $('#commitSuccessBtn').attr("disabled","true").addClass("disabled_button");
         $('#spinner').show();
       }
       function inputComment(){
           $.blockUI({
              message: $('#commentView'),
              css: {
                    width: '30%',
                    top: '30%',
                    left: '30%',
                    height: '30%',
                    textAlign: 'center',
                    cursor: 'default'
                 }
           });
         }
	function checkDelete() {
		var delTexts = $(this).attr("value").substring(1).split("/");
		var delText = null;
		if (delTexts.length == 1) {
			delText = $(this).attr("value");
		} else {
			delText = "/" + delTexts[0];
		}
		var parentTR = $(this).parents("tr.first");
		parentTR.prevAll().each( function() {
			var checkbox = $(this).find("input[action='DELETEED']");
			if (checkbox.val() && checkbox.val().indexOf(delText) == 0) {
				checkbox.attr("checked", this.checked);
			}
		});
		parentTR.nextAll().each( function() {
			var checkbox = $(this).find("input[action='DELETEED']");
			if (checkbox.val() && checkbox.val().indexOf(delText) == 0) {
				checkbox.attr("checked", this.checked);
			}
		});
	}
	function checkAdd() {
		var action = $(this).attr('action');
		var addText = $(this).val();
		var addPath = addText.substring(0, addText.indexOf("|"));
		var parentTR = $(this).parents("tr.first");
		if (this.checked) {
			parentTR
					.prevAll()
					.each(
							function() {
								var checkbox = $(this).find(
										"input[action='" + action + "']");
								var checkboxText = checkbox.val();
								if (checkboxText) {
									var checkboxPath = checkboxText.substring(
											0, checkboxText.indexOf("|"));
									if (addPath.indexOf(checkboxPath + "/") == 0) {
										checkbox.attr("checked", true);
										if (checkboxPath.substring(1)
												.split("/").length == 1) {
											return false;
										}
									}
								}
							});
		} else {
			parentTR.nextAll().each(
					function() {
						var checkbox = $(this).find(
								"input[action='" + action + "']");
						var checkboxText = checkbox.val();
						if (checkboxText) {
							var checkboxPath = checkboxText.substring(0,
									checkboxText.indexOf("|"));
							if (checkboxPath.indexOf(addPath + "/") == 0) {
								checkbox.attr("checked", false);
							} else {
								return false;
							}
						}
					});
		}
	}
</script>
</head>
<body tabId="1">
      <input id="diffSize" type="hidden" value="${diffSize }"/>
      <input id="allChangesChecked" type="hidden" value="false"/>
	   <table class="infopanel" width="100%" border="0" cellpadding="0" cellspacing="0">
	      <tr>
	         <td width="100%">
	         <table class="tabcontent" width="100%" border="0" cellpadding="0"
	            cellspacing="0">
	            <tbody>
	               <tr class="value" nowrap="true">
	                  <td class="value" style="padding-left: 20px;" nowrap="true"><b>Revision:</b>&nbsp;
	                     ${headMessage.revision}[HEAD]</td>
	                  <td class="value" style="padding-left: 20px;" nowrap="true"><b>Age:</b>&nbsp;
	                     ${headMessage.age}</td>
	                  <td class="value" style="padding-left: 20px;" nowrap="true"><b>Author:</b>&nbsp;
	                     ${headMessage.author}</td>
	                  <td class="value" style="padding-left: 20px;" nowrap="true"><b>Total
	                     items:</b>&nbsp; ${diffSize}</td>
	                  <td style="text-align:center;" width="100%"><span id="below_message" style="margin-left:10px; font-size:11px;"></span></td>
	               </tr>
	               <tr>
	                  <td class="value" style="padding-left: 20px;" colspan="5"
	                     width="100%"><b>Comment:</b>&nbsp; ${headMessage.comment}</td>
	               </tr>
	         </table>
	         </td>
	         <td>
	         <table class="tabcontent" border="0" cellpadding="0" cellspacing="0">
	            <tr>
	               <td width="23" align="left" style="padding-right: 7px;">
	                  <input id="commitSubmit" type="submit" class="button" value="Submit"/></td>
	            </tr>
	         </table>
	         </td>
	      </tr>
	      <tr>
	        <td style="padding:3px 0 3px 20px; font-size:12px;">
	           <c:if test="${diffSize gt 50}">
		           <c:set var="showAll" value="${showAll}"></c:set>
		           <c:if test="${showAll eq null}">
		              Only <b>50</b> changes shown, <a href="changes.htm?showAll=true" style="font-size:12px;text-decoration:underline">display <b>${diffSize-50}</b> more changes...</a>
		           </c:if>
		           <c:if test="${showAll eq true}">
		              <a href="changes.htm" style="font-size:12px;text-decoration:underline">Only <b>50</b> changes shown...</a>
		           </c:if>
	           </c:if>
	        </td>
	        <td></td>
	      </tr>
	   </table>
	   <table id="table_list_of_revisions"  class="list" rules="all" width="100%" cellpadding="0" cellspacing="0">
	      <tr class="second">
	         <th align="left" nowrap="true"><input id="checkall" name="checkall" type="checkbox" ><label id = "checkAll_label" for="checkall">Changed resources</label>
	              <c:if test="${diffSize gt 50 && showAll eq null}">
	                 <span id="checkAllInfo" class="hidden"><span></span><span id="alterCheckAll" class="cursor_span"></span></span>
	              </c:if>
	         </th>
	         <th width="5%" nowrap="true"><a href="#">Revision </a></th>
	      </tr>
	      <c:set var="noDiff" value="false" />
		   <c:if test="${diffSize eq 0}">
		       <c:set var="noDiff" value="true" />
		   </c:if>
		     <tr id="noDiff_msg_tr" class="first" style="${noDiff? '' : 'display:none'}">
		          <td>
			          <c:choose>
				          <c:when test="${isBlankSVN eq true}">
				             The repo is blank, please sync to import remote files.
				          </c:when>
				          <c:otherwise>
		                  There is no change.		          
				          </c:otherwise>
			          </c:choose>
		          </td>
		          <td></td>
		     </tr>
            <form id="submitForm" action="changes.htm?method=commit" method="post">
	           <div id="commentView" class="hidden">
	              <h2>Comment</h2>
	              <textarea id="inputComment" rows="4" cols="40"></textarea>
	              <div>
		              <input id="commit" type="button" value="OK"/>
		              <input id="cancel" type="button" value="Cancel"/>
	              </div>
	           </div>
	           <input type="hidden" id="comment" name="comment"/>
	           <c:forEach items="${diffStatus}" var="diffElement">
	            <tr class="first" >
	              <td width="50%"><table width="100%" border="0" cellpadding="0" cellspacing="0"><tr>
	                      <td class="internal" style="padding-right: 5px;"><input name="items" type="checkbox" value="${diffElement.path}|${diffElement.status}" action="${diffElement.status }" class="changedNode"/>
	                      <td class="internal" style="padding-right: 5px;"><a href="changes.htm?method=change&path=${diffElement.path}&action=${diffElement.status }"><span class="image_link ${diffElement.status }"></span></a></td>
	                      <td class="internal" width="100%" nowrap="true"><a href="changes.htm?method=change&path=${diffElement.path}&action=${diffElement.status }">${diffElement.path}</a></td>
	                    </tr>
	                </table></td>
	              <td align="center"><a href="history.htm?method=getRevisions&path=${diffElement.path}"> <img src="image/revision.gif" alt="Revision list" title="Revision list" border="0"> </a> </td>
	            </tr>
	           </c:forEach>
		      </form>
	   </table>
	   <div id="commitView" class="hidden" style="height:100%;">
	      <div id="infoContainer" style="height:90%; overflow:auto;">
	         <div id="commitInfo"></div>
	         <div id="spinner"><img alt="" src="image/spinner.gif" /></div>	      
	      </div>
         <div style="text-align:right; background-color:#F1FDE9;"><span id="message" style="margin-left:10px; font-size:11px;"></span><input id="commitSuccessBtn" type="button" value="OK" class="button"/></div>
      </div>
   </div>
<script type="text/javascript" src="jslib/jquery.blockUI.js"></script>
<script type="text/javascript" src="jslib/jquery.validate.min.js"></script>
</body>
</html>