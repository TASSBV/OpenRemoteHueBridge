<%@ page language="java" contentType="text/html; charset=UTF8" pageEncoding="UTF8" 
    import="org.openremote.beehive.exception.SVNException" %>
<%@ include file="../../../common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Exception</title>
</head>
<body>
<%
SVNException e = (SVNException)request.getAttribute("exception");
   response.setStatus(e.getErrorCode());
%>
<table class="infopanel" width="100%" border="0" cellpadding="0"
      cellspacing="0">  
         <tr>
            <td width="100%">
            <table class="tabcontent" width="100%" border="0" cellpadding="0"
               cellspacing="0">
                  <tr>
                     <td class="value" style="padding-left: 20px;" colspan="5"
                        width="10%"><b>Exception</b>&nbsp;:</td>
                     <td class="value" colspan="5" width="80%">
                        <span style="margin-left:10px; color:red;">(<%=e.getErrorCode()%>): <%=e.getMessage()%></span>
                     </td>
                     <td class="value" colspan="5" width="10%">
                     </td>
                  </tr>
            </table>
            </td> 
         </tr>       
   </table>
</body>
</html>