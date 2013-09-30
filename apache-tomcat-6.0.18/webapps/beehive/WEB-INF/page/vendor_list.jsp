<%@ page language="java" contentType="text/html; charset=UTF8"
    pageEncoding="UTF8"%>
<%@ include file="../../common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
<title>OpenRemote Beehive - Vendor List</title>
<script type="text/javascript">
    $(document).ready(function(){        
        $.tablesorter.addParser({ 
            // set a unique id 
            id: 'age', 
            is: function(s) { 
                return false; 
            }, 
            format: function(s) { 
                var time = $("<td>"+s+"</td>").find("input").val();
                return $.tablesorter.formatFloat(new Date(time).getTime()); 
            }, 
            type: 'numeric'
        });        
        $.tablesorter.addParser({ 
            // set a unique id 
            id: 'size', 
            is: function(s) { 
                return false; 
            }, 
            format: function(s) { 
                return $.tablesorter.formatFloat(s.replace(new RegExp(/k/),"")); 
            }, 
            type: 'numeric'
        });        
        $("#table_list_of_vendor").tablesorter({ 
            headers: { 
                2:{
                    sorter:'size'
                },
                3: { 
                    sorter:'age' 
                }                 
            } 
        });
        $("#table_list_of_vendor").addClass("sorterTable");
    });
</script>
</head>
<body tabId="3">
	<table class="infopanel" width="100%" border="0" cellpadding="0"
		cellspacing="0">
			<tr>
				<td>
				<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr class="path_node">
							<td nowrap="true"><span class="pathFormat">
							Path&nbsp;:&nbsp; </span></td>
							<td width="100%" nowrap="true">
							<table border="0" cellpadding="0" cellspacing="0">
									<tr>
										<td nowrap="true"><a class="path_t" href="history.htm"><span
											class="path_text">Beehive</span></a></td>
									</tr>
							</table>
							</td>
						</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td width="100%">
				<table class="tabcontent" width="100%" border="0" cellpadding="0"
					cellspacing="0">
						<tr class="value" nowrap="true">
							<td class="value" style="padding-left: 20px;" nowrap="true"><b>Revision:</b>&nbsp;
								<a	href="#">
								${headMessage.revision} </a></td>							
							<td class="value" style="padding-left: 20px;" nowrap="true"><b>Author:</b>&nbsp;
							    ${headMessage.author}</td>
							<td class="value" style="padding-left: 20px;" nowrap="true"><b>Total items in dir:</b>&nbsp;
							    ${fn:length(vendorEntries)}</td>
							<td width="100%"></td>
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
							<td width="23" align="left" style="padding-right: 7px;"><a
								href="history.htm?method=getRevisions&path="><img src="image/revision.gif"
								alt="All Revision list" title="All Revision list" border="0"></a>
							</td>							
						</tr>
				</table>
				</td>
			</tr>
	</table>

	<table id="table_list_of_vendor" class="list" rules="all"
		width="100%" cellpadding="0" cellspacing="0">
		<thead>
		<tr class="second">
			<th width="30%" nowrap="true"><a href="#">Name</th>
			<th width="10%" nowrap="true"><a href="#">Revision</a></th>
			<th width="10%" nowrap="true"><a href="#">Size</a></th>
			<th width="30%" nowrap="true"><a href="#">Age</a></th>
			<th width="20%" nowrap="true"><a href="#">Author</a></th>
		</tr>
		</thead>
		<tbody>
		<c:if test="${fn:length(vendorEntries) eq 0}">
		    <tr class="first">
		       <td>There is no vendor.</td>
		       <td></td>
		       <td></td>
		       <td></td>
		       <td></td>
		    </tr>
		</c:if>
		<c:forEach items="${vendorEntries}" var="vendorEntry">
			<tr class="first">
				<td>
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td class="internal" style="padding-left: 15px; padding-right: 5px;">
							  <a href="history.htm?method=getModels&path=${vendorEntry.path}">
							     <span	class="list_of_history_image_link ${vendorEntry.file}"></span>
							  </a>
							</td>
							<td class="internal" width="100%" nowrap="true">
							  <a href="history.htm?method=getModels&path=${vendorEntry.path}">${vendorEntry.path}</a>
							</td>
						</tr>
					</table>
				</td>
				<td align="right">
				  <c:if test="${headMessage.revision eq vendorEntry.version}">
				     <img src="image/head_revision.gif" alt="Head revision" title="Head revision" />
				  </c:if>
				  ${vendorEntry.version}
				  <a href="history.htm?method=getRevisions&path=${vendorEntry.path}"> 
				     <img src="image/revision.gif" alt="Revision list" title="Revision list" /> 
				  </a>
				</td>
				<td align="center">
					  <c:choose>
						<c:when test="${vendorEntry.file eq true}">
	                         ${vendorEntry.sizeString}
	                      </c:when>
						<c:otherwise>-</c:otherwise>
					 </c:choose>
            </td>
				<td align="center">${vendorEntry.age}<input type="hidden" value="${vendorEntry.date }"/></td>
				<td align="center">${vendorEntry.author}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>

</body>
</html>