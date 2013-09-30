<%--
  User: allenwei
  Date: 2009-2-11
  Time: 17:36:57
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" >
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Beehive Database</title>
    <link href="image/OpenRemote_Logo16x16.png" rel="shortcut icon"/>
    <link href="image/OpenRemote_Logo16x16.png" type="image/png" rel="icon"/>
    <link type="text/css" href="css/openremote_base.css" rel="stylesheet"/>
    <link type="text/css" href="css/beehiveDatabase.css" rel="stylesheet"/>
    <script type="text/javascript" src="jslib/jquery-1.3.1.min.js"></script>
    <script type="text/javascript" src="js/beehiveDatabase.js"></script>
</head>
<body>
<div id="main">
    <div id="title">
        <h1>Beehive Database</h1>
    </div>
    <div id="undecoratedLink">
        [
        <a class="regularLink" target="" href="changes.htm" title="Administration">
            Administration
        </a>
        ]
    </div>
    <div id="content">
        <div class="content_head">
            <h2>Browse LIRC database</h2>
        </div>
        <div id="content_body">
            <div id="lirc_select">
                <div id="vendor_select_container" class="lirc_select_container">
                    <div class="select_container_title">
                        <h3>Select device vendor:</h3>
                    </div>
                    <div class="select_container_head">
                        <input class="filter_input" type="text" value="Filter..." tabindex="1"/>
                        <button class="buttonNonpersistent resetBtn">Reset</button>
                    </div>
                    <div class="select_wrapper">
                        <select id="vendor_select" multiple="multiple" size="8"
                                onchange="showSelect(this.id,this.options[this.selectedIndex].value)">
                            <option value="0">------------</option>
                            <c:forEach items="${vendors}" var="vendor">
                                <option value="${vendor.oid}">${vendor.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div id="model_select_container" class="lirc_select_container" style="display:none">
                    <div class="select_container_title">
                        <h3>Select device model:</h3>
                    </div>
                    <div class="select_container_head">
                        <input type="text" value="Filter..." class="filter_input" tabindex="1"/>
                        <button class="buttonNonpersistent resetBtn">Reset</button>
                    </div>
                    <div class="select_wrapper">
                    </div>
                </div>
                <div id="section_select_container" class="lirc_select_container" style="display:none">
                    <div class="select_container_title">
                        <h3>Select device section:</h3>
                    </div>
                    <div class="select_container_head">
                        <input type="text" value="Filter..." class="filter_input" tabindex="1"/>
                        <button class="buttonNonpersistent resetBtn">Reset</button>
                    </div>
                    <div class="select_wrapper">
                    </div>
                </div>

            </div>
            <div class="clear"></div>
            <div id="lirc_details_container" style="display:none;"></div>
        </div>
    </div>

</div>

<div id="waiting_div" style="display: none;">
	<img src="image/statusindicator.gif" alt="wating" />
</div>
</body>
</html>

