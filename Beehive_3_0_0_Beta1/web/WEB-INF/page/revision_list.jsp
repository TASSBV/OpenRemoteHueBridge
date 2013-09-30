<%@ page language="java" contentType="text/html; charset=UTF8"
    pageEncoding="UTF8"%>
<%@ include file="../../common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
<title>OpenRemote Beehive - Revision List</title>
<script type="text/javascript">
	$(document).ready(function() {
		  	var filePath = $("#filePath").val();

		  	$("a[name='rollBack']").click(function(){
		  	   var revision = $(this).attr("revision");
		  	   $("#actionInfo").text("Rollbacking......");
		  	   $.post("history.htm?method=rollBack",
		  			   {path:filePath, revision:revision},
		  			   function(){
			  			   $("#message").text("Rollback "+filePath+" to revision "+revision+" success!");
		  			   }
				  	   );
		  	});
	});
   function compareFiles(){
	   var rev1 = 0;
	   var rev2 = 0;
	   $('input[name="items"]:checked').each(function(i){
		   if(i==0){
	        rev1 = $(this).val();
		   }
		   if(i==1){
			   rev2 = $(this).val();
		   }
		   if(i==2){
			   rev2 = 0;
			   alert("Please compare two versions!");
			   return;
		   }
	    });
	    if(rev1 == 0){
		    alert("Please select two versions to compare!");
		    return;
	    }
	    if(rev1!=0 && rev2!=0){
		   var filePath = $("#filePath").val();
		   location.href="history.htm?method=compare&path="+filePath+"&rev1="+rev1+"&rev2="+rev2;
	    }
   }

</script>
</head>
<body tabId="3">
   <input id="filePath" type="hidden" value="${breadcrumbPath}"/>
	<table class="infopanel" width="100%" border="0" cellpadding="0"
		cellspacing="0">
			<tr>
				<td>
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr class="path_node">
							<td nowrap="true"><span class="pathFormat">
							Path&nbsp;:&nbsp; </span></td>
                     <td width="100%" nowrap="true">
                        <jsp:include page="breadcrumb.jsp" flush="true">
                           <jsp:param name="breadcrumbPath" value="${breadcrumbPath}" />
                           <jsp:param name="isFile" value="${isFile}" />
                        </jsp:include>
                     </td>
						</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td width="100%">
					<table class="tabcontent" width="100%" border="0" cellpadding="0"
						cellspacing="0">
							<tr nowrap="true">
								<td class="value" style="padding-left: 20px;" nowrap="true"><b>Head revision:</b>&nbsp; ${headRevision}</td>
								<td class="value" style="padding-left: 20px;" nowrap="true"><b>Displayed revisions:</b>&nbsp; ${fn:length(logMessages)}</td>
								<td id="message" style="text-align:center;font-size:11px" width="100%"></td>
							</tr>
					</table>
		       </td>
				 <c:if test="${isFile}">
				 <td>
				 <table class="tabcontent" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td style="padding-right: 7px;" align="center"><a id="compareLink"
								href="javascript:compareFiles()"> <img src="image/diff.gif"
								alt="Diff" title="Diff"
								style="margin: 0pt; padding: 0pt; cursor: pointer;" border="0">
							</a></td>
						</tr>
				 </table>
				</td>
				</c:if>
			</tr>
	</table>
	<table id="table_list_of_revision" class="list" rules="all"
		width="100%" cellpadding="0" cellspacing="0">
		<thead>
		<tr class="second">
			<th width="10%" nowrap="true"><a href="#">Revision</a></th>
			<th width="10%" nowrap="true"><a href="#">Age</a></th>
			<th width="10%" nowrap="true"><a href="#">Author</a></th>
			<th width="65%" nowrap="true"><a href="#">Comment</a></th>
			<th><a href="#">Rollback</a></th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${logMessages}" var="logMessage" varStatus="status">
			<tr class="first">
				<td>
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
						   <c:choose>
							   <c:when test="${isFile}">
									<td class="internal" style="padding-right: 5px;"><input
										name="items" type="checkbox" value="${logMessage.revision}"></td>
									<td class="internal" width="100%" nowrap="true">
									  <a href="history.htm?method=getContent&path=${breadcrumbPath}&revision=${logMessage.revision}">${logMessage.revision}</a>
									</td>
								</c:when>
								<c:otherwise>
								    <td class="internal" width="100%" nowrap="true" align="center">
                             ${logMessage.revision}
                             </td>
								</c:otherwise>
							</c:choose>
						</tr>
					</table>
				</td>
				<td align="center">${logMessage.age}<input type="hidden" value="${logMessage.date }"/></td>
				<td align="center">${logMessage.author}</td>
				<td align="center">${logMessage.comment}</td>
				<td align="center">
				  <c:if test="${status.count gt 1}">
				     <a revision="${logMessage.revision}" name="rollBack" href="javascript:void(0);"><img border="0" title="rollback to this version" alt="rollback to this version" src="image/rollback.gif"/>
                  </a>
              </c:if>
            </td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>