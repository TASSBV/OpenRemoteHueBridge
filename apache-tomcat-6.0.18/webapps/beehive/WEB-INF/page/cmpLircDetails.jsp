<%@ include file="../../common/taglibs.jsp" %>
<jsp:useBean id="commonUtils" class="org.apache.commons.lang.StringUtils"/>
<div>
<button onclick="window.open('lirc.html?method=export&id=${model.oid}')"
	class="buttonNonpersistent">Download LIRC configuration file</button>
</div>
<div id="lirc_details_title" class="content_head">
    LIRC File Details
</div>
<div class="lirc_entry">
    <div class="label">Selected LIRC file:</div>
    <div class="output">${model.fileName}</div>
</div>
<div class="lirc_entry">
    <div class="label">Model:</div>
    <div class="output">${model.name}</div>
</div>
<div class="lirc_entry">
    <div class="label">Supported devices:</div>
    <div class="output">UNKNOWN</div>
</div>
<div id="lirc_comment" class="more_detail_container">
    <div class="detail_title">
        Comments
    </div>
    <div class="detail_content">
        <pre>${section.comment}</pre>
    </div>
</div>

<div class="lirc_entry">
    <div class="label">Selected remote:</div>
    <div class="output">${options[0].value}</div>
</div>
<div class="lirc_entry">
    <div class="label">Flags:</div>
    <div class="output">${options[2].value}</div>
</div>
<div id="lirc_code_option" class="more_detail_container">
    <div class="detail_title">
        Infrared Code Options
    </div>
    <div class="detail_content">
        <table class="data_table">
            <tbody>
            <c:forEach items="${options}" var="option" varStatus="n">
                <c:if test="${!option.blankComment}">
                    <tr class="comment_row">
                        <td colspan="2">
                            <pre>${option.comment}</pre>
                        </td>
                    </tr>
                </c:if>
                <tr>
                    <td class="twentyPercentColumn">${option.name}</td>
                    <td class="defaultColumn">${option.value}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
<br/>

<div id="lirc_codes" class="more_detail_container">
    <div class="detail_title">
        Infrared Codes
    </div>
    <div class="detail_content">
        <table class="data_table">
            <tbody>
            <c:forEach items="${codes}" var="code">
                  <c:if test="${!code.blankComment}">
                    <tr class="comment_row">
                        <td colspan="2">
                            <pre>${code.comment}</pre>
                        </td>
                    </tr>
                </c:if>
                <tr>
                    <td class="twentyPercentColumn">${code.name}</td>
                    <td class="defaultColumn wrapWhitespace">
                        <pre>${code.value}</pre>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<div id="bottom">
    <button onclick="window.open('lirc.html?method=export&id=${model.oid}')" class="buttonNonpersistent">Download LIRC configuration file
    </button>
</div>