<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <link href="css/default.css" type="text/css" rel="stylesheet" media="screen">
   <script type="text/javascript" src="jslib/jquery-1.3.1.min.js"></script>
   <title>OpenRemote Beehive - File Comparison With Latest Revision</title>
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
                        <jsp:include page="breadcrumb.jsp" flush="true">
                           <jsp:param name="breadcrumbPath" value="${breadcrumbPath}" />
                           <jsp:param name="isFile" value="true" />
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
                     <tr class="value" nowrap="true">
                        <td class="value" style="padding-left: 20px;" nowrap="true"><b>Revision:</b>&nbsp;
                           <a href="#">
                           ${modelMessage.revision} </a></td>                   
                        <td class="value" style="padding-left: 20px;" nowrap="true"><b>Author:</b>&nbsp;
                            ${modelMessage.author}</td>
                        <td width="100%"></td>
                     </tr>
                     <tr>
                        <td class="value" style="padding-left: 20px;" colspan="5"
                           width="100%"><b>Comment:</b>&nbsp; ${modelMessage.comment}</td>
                     </tr>
               </table>
            </td>
            <td>
            <table class="tabcontent" border="0" cellpadding="0" cellspacing="0">
                  <tr>
                     <td width="23" align="left" style="padding-right: 7px;"><a
                        href="history.htm?method=getRevisions&path=${breadcrumbPath}"><img src="image/revision.gif"
                        alt="File Revision list" title="File Revision list" border="0"></a>
                     </td>
                  </tr>
            </table>
            </td>
         </tr>
   </table>
   <table id="table_list_of_compare" rules="all" width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>         
         <td width="50%">
            <div id="right_div" style="height:500px">
               <table valign="top" width="100%" cellpadding="0" cellspacing="0">
                  <c:forEach items="${lines}" var="line" varStatus="status">
                  <tr valign="middle">
                     <td class="diffLineNumber"><a class="number" name="1" href="#1">${status.count } </a></td>
                     <td class="diffLine_N" width="100%" align="left" nowrap="true" "><pre>${line}</pre></td>
                  </tr>
                  </c:forEach>
               </table>
              </div>
             </td>
      </tr>
   </table>
</body>
</html>