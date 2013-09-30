<%@ page import="org.openremote.controller.action.BridgeFinder" %>
<%@ page import="org.openremote.controller.action.Bridge" %>
<%@ page import="java.util.ArrayList" %>
<%--
 * This page shows the bridges to which can be connected
 * and the user can select a bridge to connect to.
 * @author TASS Technology Solutions - www.tass.nl
 *
 *
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<html>
<head>
    <TITLE>OpenRemote Controller</TITLE>

    <link href="image/OpenRemote_Logo16x16.png" rel="shortcut icon"/>
    <link href="image/OpenRemote_Logo16x16.png" type="image/png" rel="icon"/>
    <link type="text/css" href="css/index.css" rel="stylesheet"/>
</head>
<BODY style="TABLE-LAYOUT: fixed; WORD-BREAK: break-all" topMargin=10
      marginwidth="10" marginheight="10">

<script type="text/javascript">
    <%
        String username = (String) session.getAttribute("Bridgeusername");
        String ip = (String) session.getAttribute("Bridgeip");
        if (username != null){
            if(username !="failed") {
                out.print("alert('The key for the bridge ("+ip+") is set to: "+ username +"');");
            }else{
                out.print("alert('Failed to get the key. Don't forget to push the button on the bridge first.');");
            }
            session.removeAttribute("Bridgeip");
            session.removeAttribute("Bridgeusername");
        }
    %>
</script>

<TABLE height="95%" cellSpacing=0 cellPadding=0 width="100%"
       align=center border=0>
    <TBODY>
    <TR vAlign="center" align="middle">
        <TD>
            <TABLE cellSpacing=0 cellPadding=0 width=468 bgColor=#ffffff border=0>
                <TBODY>
                <TR>
                    <TD width=20 background="image/rbox_1.gif" height=20></TD>
                    <TD width=108 background="image/rbox_2.gif" height=20></TD>
                    <TD width=56><IMG height=20 src="image/rbox_2.gif" width=56></TD>
                    <TD width=100 background="image/rbox_2.gif"></TD>
                    <TD width=56><IMG height=20 src="image/rbox_2.gif" width=56></TD>
                    <TD width=108 background="image/rbox_2.gif"></TD>
                    <TD width=20 background="image/rbox_3.gif" height=20></TD>
                </TR>
                <TR>
                    <TD align=left background="image/rbox_4.gif" rowSpan=1></TD>
                    <TD style="border-bottom: 1px solid #ccc" colSpan=5 height=50>
                        <a href="http://www.openremote.org/"><img alt=""
                                                                  src="image/global.logo.gif"></a></TD>
                    <TD align=left background="image/rbox_6.gif" rowSpan=1></TD>
                </TR>
                <TR>
                    <TD align=left background="image/rbox_4.gif" rowSpan=2></TD>
                    <TD style="border-bottom: 1px solid #ccc" colSpan=5 height=50>
                        <p class="welcome">Bridge connector</p>

                    </TD>
                    <TD align=left background="image/rbox_6.gif" rowSpan=2></TD>
                </TR>
                <TR>
                    <TD align=left colSpan=5 height=80>
                        <p>&nbsp;</p>
                        The controller needs permission to talk to the Hue bridge.  </br>
                        To get this permission do the following:
                        <li> Push the button on the Hue bridge.</li>
                        <li> Click the connect button behind the bridge on the this page.</li>
                        <p>&nbsp;</p>
                        <% BridgeFinder finder = new BridgeFinder();%>
                        <% Bridge savedBridge = finder.getSavedBridge();
                            if (savedBridge != null) {
                                out.print("Last connected bridge: </br><table><tr><td style=\"padding-right: 25px;\"><b>Ip</b></td><td>" + savedBridge.getInternalip() + "</td></tr><tr><td><b>Key</b></td><td>" + savedBridge.getUsername() + "</td></tr></table>");
                            }
                        %>
                        <% ArrayList<Bridge> bridges = finder.getBridges();%>
                        <br>
                        <table style="border-spacing: 5px;">
                            <thead>
                            <tr>
                                <td><b>ID</b></td>
                                <td><b>internal IP</b></td>
                                <td><b> Mac Address</b></td>
                            </tr>
                            </thead>
                            <%
                                for (int i = 0; i < bridges.size(); i++) { %>
                            <tr>
                                <td style="margin-right: 14px;"><%=bridges.get(i).getId()%>
                                </td>
                                <td style="margin-right: 14px;"><%=bridges.get(i).getInternalip()%>
                                </td>
                                <td style="margin-right: 14px;"><%=bridges.get(i).getMacAddress()%>
                                </td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/BridgeServlet" method="post">
                                        <input type="hidden" name="ip" value="<%= bridges.get(i).getInternalip()%>"/>
                                        <input type="hidden" name="macaddress"
                                               value="<%= bridges.get(i).getMacAddress()%>"/>
                                        <input type="hidden" name="id" value="<%= bridges.get(i).getId()%>"/>
                                        <input type="submit" name="button" value="Connect"/>
                                    </form>
                                </td>
                            </tr>
                            <% } %>
                        </table>
                        <div id="online-cont">
                            <a href="/controller/">Home</a>
                        </div>
                    </TD>
                </TR>
                <TR>
                    <TD align=left background="image/rbox_7.gif" height=20></TD>
                    <TD align=left background="image/rbox_8.gif" colSpan=5 height=20></TD>
                    <TD align=left background="image/rbox_9.gif" height=20></TD>
                </TR>
                </TBODY>
            </TABLE>
			<span class="copyright">Copyright &copy; 2008-2012 OpenRemote.
			Licensed under Affero General Public License.</span>
    </TR>
    </TBODY>
</TABLE>

</body>
</html>