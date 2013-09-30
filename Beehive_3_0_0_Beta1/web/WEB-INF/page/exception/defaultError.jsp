<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../../../common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.Enumeration,java.util.Iterator"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Error</title>
</head>
<body>
<c:set value="${exception}" var="ee" />
<jsp:useBean id="ee" type="java.lang.Exception" />
<table class="infopanel" width="100%" border="0" cellpadding="0"
      cellspacing="0">  
         <tr>
            <td width="100%">
            <table class="tabcontent" width="100%" border="0" cellpadding="0"
               cellspacing="0">
                  <tr>
                     <td class="value" style="padding-left: 20px;" colspan="5"
                        width="15%"><b>Oops, an Exception occurred</b>&nbsp;:</td>
                     <td class="value" colspan="5" width="80%">
                        <span style="margin-left:10px; color:red;"> <%=ee.getMessage()%></span>
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