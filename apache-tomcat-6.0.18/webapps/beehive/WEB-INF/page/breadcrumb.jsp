<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../common/taglibs.jsp" %>
<div id="breadcrumbs">
<c:set var="breadcrumb" value='${fn:split(breadcrumbPath,"/")}'></c:set>
<a class="path_t" href="history.htm"><span class="path_text">Beehvie</span></a>
<span class="path_t">/</span>
<c:set var="getModelsUrl" value='history.htm?method=getModels&path='></c:set>
<c:set var="getContentUrl" value='history.htm?method=getContent&path='></c:set>

<c:choose>
	<c:when test="${param.isFile}">
		<c:forEach items='${breadcrumb}' var="name" varStatus="status">
		   <c:if test='${status.count lt fn:length(breadcrumb)}'>
		      <a class="" href="${getModelsUrl}${fn:substring(breadcrumbPath, 0,fn:indexOf(breadcrumbPath,name)+fn:length(name))} ">${name}</a>
		   </c:if>
		   <c:if test='${status.count lt fn:length(breadcrumb)}'>
		      <span class="path_t">/</span>
		   </c:if>
		   <c:if test="${status.count eq fn:length(breadcrumb)}">     
            <a class="" href="${getContentUrl}${breadcrumbPath}">${name}</a>
         </c:if>
		</c:forEach>
	</c:when>
	<c:otherwise><!-- is dir -->
	   <c:forEach items='${breadcrumb}' var="name" varStatus="status">
         <c:if test='${status.count le fn:length(breadcrumb)}'>
            <a class="noClass" href="${getModelsUrl}${fn:substring(breadcrumbPath, 0,fn:indexOf(breadcrumbPath,name)+fn:length(name))} ">${name}</a>
         </c:if>
         <c:if test='${status.count lt fn:length(breadcrumb)}'>
            <span class="path_t">/</span>
         </c:if>
      </c:forEach>
	</c:otherwise>
</c:choose>
</div>