<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../../common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <link href="css/default.css" type="text/css" rel="stylesheet" media="screen">
   <script type="text/javascript" src="jslib/jquery-1.3.1.min.js"></script>
   <title>OpenRemote Beehive - File Comparison With Latest Revision</title>
   <script>
      var left_div = null;
      var right_div = null;
      var currentLeftLine = null;
      var left_div_scroll = null;
      var right_div_scroll = null;
      var tr_height = null;
      var currentRightLine = null;
      
      $().ready(function(){         
         left_div = $("#left_div");
         left_div_scroll = $("#left_div")[0];
         right_div = $("#right_div");
         right_div_scroll = $("#right_div")[0];
         
         left_div.scroll(function(){
            right_div_scroll.scrollTop = this.scrollTop;
            right_div_scroll.scrollLeft = this.scrollLeft;
         });
         right_div.scroll(function(){
            left_div_scroll.scrollTop = this.scrollTop;
            left_div_scroll.scrollLeft = this.scrollLeft;
         });
         
         currentLeftLine = left_div.find("tr:first");
         currentRightLine = right_div.find("tr:first");
         tr_height = currentLeftLine.height();
         var fileAction = $("#action").val();
         if(fileAction=="UNVERSIONED" || fileAction=="ADDED"){
        	       $("#breadcrumbs").find("a:first").nextAll("a").attr("href","#");
             }
      });
      
      function previous(){
         var lastLeftLine = currentLeftLine;
         var lastRightLine = currentRightLine;
         var finalIndex = null;
         currentLeftLine.prevAll().each(function(){
            var changeType = $(this).attr("changeType");
            currentLeftLine = $(this);
            if (changeType != 'N'){
               return false;
            }
         });
         currentRightLine.prevAll().each(function(){
            var changeType = $(this).attr("changeType");
            currentRightLine = $(this);
            if (changeType != 'N'){
               return false;
            }
         });
         
         var finalLeftIndex = currentLeftLine.attr('index');
         var finalRightIndex = currentRightLine.attr('index');
         
         if(currentLeftLine == lastLeftLine && currentRightLine == lastRightLine){
            return;
         }
         
         if(finalLeftIndex > finalRightIndex){
            currentRightLine = lastRightLine;
         }else if(finalLeftIndex < finalRightIndex){
            currentLeftLine = lastLeftLine;
         }
         finalIndex = finalLeftIndex >= finalRightIndex ? finalLeftIndex: finalRightIndex;
         
         scrollTo(finalIndex);
      }
      function next(){
         var lastLeftLine = currentLeftLine;
         var lastRightLine = currentRightLine;
         var finalIndex = null;
         currentLeftLine.nextAll().each(function(){
            var changeType = $(this).attr("changeType");
            currentLeftLine = $(this);
            if (changeType != 'N'){
               return false;
            }
         });
         currentRightLine.nextAll().each(function(){
            var changeType = $(this).attr("changeType");
            currentRightLine = $(this);
            if (changeType != 'N'){
               return false;
            }
         });
         
         var finalLeftIndex = currentLeftLine.attr('index');
         var finalRightIndex = currentRightLine.attr('index');
         
         
         if(currentLeftLine == lastLeftLine && currentRightLine == lastRightLine){
            return;
         }
   
         if (finalLeftIndex < finalRightIndex) {
            currentRightLine = lastRightLine;
         } else if (finalLeftIndex > finalRightIndex) {
            currentLeftLine = lastLeftLine;
         }
   
         finalIndex = finalLeftIndex <= finalRightIndex ? finalLeftIndex
               : finalRightIndex;
   
         scrollTo(finalIndex);
      }
       function scrollTo(index) {
          left_div_scroll.scrollTop = tr_height * parseInt(index);
      }
</script>
</head>
<body tabId="1">
   <input type="hidden" id="action" value="${action}">
   <table class="infopanel" width="100%" border="0" cellpadding="0"
      cellspacing="0">
      <tr>
         <td width="100%">
         <table width="100%" border="0" cellpadding="0" cellspacing="0">
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
               <td class="message" style="padding-left: 20px;" width="50%"
                  nowrap="true">
               <table class="tabcontent" width="100%" border="0" cellpadding="0"
                  cellspacing="0">
                  <tr class="value" nowrap="true">
                     <td class="value" nowrap="true"><b>Revision:</b>&nbsp; <a
                        href="#"> ${repoMessage.revision}[HEAD] </a></td>                     
                     <td class="value" style="padding-left: 20px; padding-right: 20px;"
                        nowrap="true"><b>Author:</b>&nbsp; ${repoMessage.author }</td>
                     <td width="100%"></td>
                  </tr>
               </table>
               </td>
               <td class="message" style="padding-left: 20px;" width="50%"
                  nowrap="true">
               <table class="tabcontent" width="100%" border="0" cellpadding="0"
                  cellspacing="0">
                  <tr class="value" nowrap="true">
                     <td class="value" nowrap="true"><b>LIRC WebSite:</b>&nbsp;</td>
   
                  </tr>
               </table>
               </td>
            </tr>
            <tr>
               <td class="message" style="padding-left: 25px;" width="50%"><b>Comment:</b>&nbsp;
               ${repoMessage.comment }</td>
               <td class="message" style="padding-left: 25px;" width="50%">&nbsp;</td>
            </tr>
         </table>
         </td>
      </tr>
   </table>
   <table class="subinfopanel" width="100%">
      <tr>
      <td width="100%">
      <table>
         <tr class="value">
            <td>
            <table>
               <tr valign="bottom">
                  <td><img src="image/added_ico.gif"
                     style="border: 0pt none; margin: 0pt; padding: 0pt;" width="16"
                     border="0"></td>
                  <td class="value" style="padding-right: 20px;" width="100%"
                     nowrap="true"><b>Added:</b>&nbsp; ${changeCount.addedItemsCount }</td>
               </tr>
            </table>
            </td>
            <td>
            <table>
               <tr valign="bottom">
                  <td><img src="image/changed_ico.gif"
                     style="border: 0pt none; margin: 0pt; padding: 0pt;" width="16"
                     border="0"></td>
                  <td class="value" style="padding-right: 20px;" width="100%"
                     nowrap="true"><b>Changed:</b>&nbsp; ${changeCount.modifiedItemsCount }</td>
               </tr>
            </table>
            </td>
            <td>
            <table>
               <tr valign="bottom">
                  <td><img src="image/removed_ico.gif"
                     style="border: 0pt none; margin: 0pt; padding: 0pt;" width="16"
                     border="0"></td>
                  <td class="value" style="padding-right: 20px;" width="100%"
                     nowrap="true"><b>Deleted:</b>&nbsp; ${changeCount.deletedItemsCount }</td>
               </tr>
            </table>
            </td>
         </tr>
      </table>
      </td>
      <td style="padding-right: 20px;" align="right"></td>
      <!-- Next/Previous buttons -->
      <td align="right">
         <table align="right">
            <tr>
               <td><input onClick="previous()" value="Previous Change" class="button"
                  type="button"></td>
               <td><input onClick="next()" value="Next Change" class="button"
                  type="button"></td>
            </tr>
         </table>
      </td>
   </tr>
   </table>
   <table id="table_list_of_compare" rules="all" width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
         <td width="50%">
            <div id="left_div">
               <table valign="top" width="100%" cellpadding="0" cellspacing="0">
                  <c:if test="${fn:length(leftLines)==0 && fn:length(rightLines)==0}">
                     <div class="tip">This is a Vendor</div>
                  </c:if>                                    
                  <c:forEach items="${leftLines}" var="leftLine" varStatus="status">                  
                  <tr valign="middle" index="${status.index}" changeType="${leftLine.changeType }">
                     <td class="actionType"><span class="image_of_change_${leftLine.changeType }"></span></td>
                     <td class="diffLineNumber"><a class="number" name="1" href="#1">${leftLine.number } </a></td>
                     <td class="diffLine_${leftLine.changeType }" width="100%" align="left"><pre>${leftLine.line }</pre></td>
                  </tr>
                  </c:forEach>
                  <c:if test="${fn:length(leftLines)==0}">
                     <div class="tip">The file doesn't exist in history</div>
                  </c:if> 
               </table>
            </div>
          </td>
         <td width="50%">
            <div id="right_div">
               <table valign="top" width="100%" cellpadding="0" cellspacing="0">
                  <c:forEach items="${rightLines}" var="rightLine" varStatus="status">
                  <tr valign="middle" index="${status.index}" changeType="${rightLine.changeType }">
                     <td class="actionType"><span class="image_of_change_${rightLine.changeType }"></span></td>
                     <td class="diffLineNumber"><a class="number" name="1" href="#1">${rightLine.number } </a></td>
                     <td class="diffLine_${rightLine.changeType }" width="100%" align="left"><pre>${rightLine.line }</pre></td>
                  </tr>
                  </c:forEach>
               </table>
              </div>
             </td>
      </tr>
   </table>
</body>
</html>