<%@ include file="../../common/taglibs.jsp" %>

<select id="${generateSelectTcagId}" multiple="multiple" size="8"
        onchange="showSelect(this.id,this.options[this.selectedIndex].value)">
    <option value="0">------------</option>
    <c:forEach items="${items}" var="item">
        <option value="${item.oid}">${item.name}</option>
    </c:forEach>
</select>
