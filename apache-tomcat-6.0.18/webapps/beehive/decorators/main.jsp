<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html> 
   <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
      <title><decorator:title default="OpenRemote Beehive" /></title>
      <link href="image/OpenRemote_Logo16x16.png" rel="shortcut icon"/>
      <link href="image/OpenRemote_Logo16x16.png" type="image/png" rel="icon"/>
      <link href="css/default.css" type="text/css" rel="stylesheet" media="screen">
      <link href="css/table.css" type="text/css" rel="stylesheet" media="screen">
      <script type="text/javascript" src="jslib/jquery-1.3.1.min.js"></script>
      <script type="text/javascript" src="js/myJQueryFn.js"></script>
      <script type="text/javascript" src="js/init.js"></script>
      <script type="text/javascript">
            $(document).ready(function(){
                $('#tab_<decorator:getProperty property="body.tabId"/>').addClass('activetab').removeClass("inactivetab");
                if($('#updateStatus').val() == "running"){
                	$('#tab_2 img').attr("src","image/update.gif");
                }else{
                	$('#tab_2 img').attr("src","image/update_icon.gif");
                }
            });
        </script>
      <decorator:head />
   </head>
   <body>
      <input type="hidden" name="updateStatus" id="updateStatus" value="${update}"/>
      <input type="hidden" name="commitStatus" id="commitStatus" value="${commit}"/>
      <table width="100%" cellpadding="0" cellspacing="0" height="100%">
          <tr>
            <td><table width="100%" cellpadding="0" cellspacing="0" height="100%">
                  <tr>
                    <td><table class="actionbar_head" width="100%" cellpadding="0" cellspacing="0">
                          <tr>
                            <td style="padding-left: 8px; padding-right: 7px;"><a href="http://www.openremote.org/"><img src="image/global.logo.gif" alt="openremote.org" title="openremote.org" style="margin: 0pt; padding: 0pt; vertical-align: middle;" border="0"></a> </td>
                            <td class="title" nowrap="nowrap">Beehive Administration Client</td>
                            <td width="100%"></td>
                            <td style="padding-left: 8px; padding-right: 7px;" class="title"><a href="./j_security_logout">Logout</a></td>
                          </tr>
                      </table></td>
                  </tr>
              </table></td>
          </tr>
          <tr>
            <td style="padding: 2px 5px 0pt;"><table width="100%" cellpadding="0" cellspacing="0" height="100%">
                  <tr>
                    <td><table border="0" cellpadding="0" cellspacing="0">
                        <tr>
                          <td id="tab_1" colspan="2" class="inactivetab" onClick="window.location='changes.htm'"><img style="vertical-align: middle; margin-right: 3px;" title="Changes" alt="Changes" src="image/changes.gif"/>Changes </td>
                          <td id="tab_2" colspan="2" class="inactivetab" onClick="window.location='sync.htm'"><img style="vertical-align: middle; margin-right: 3px;" title="Update" alt="Update" src="image/update_icon.gif"/>Sync </td>
                          <td id="tab_3" colspan="2" class="inactivetab" onClick="window.location='history.htm'"><img style="vertical-align: middle; margin-right: 3px;" title="History" alt="History" src="image/history.gif"/>History </td>
                        </tr>
                    </table></td>
                  </tr>
              </table></td>
          </tr>
          <tr>
             <td style="padding: 0px 5px 0pt;" width="100%" height="100%" valign="top">
                <decorator:body />
             </td>
          </tr>
         <tr>
            <td valign="bottom"><table class="footer" style="border-width: 1px 0pt 0pt; border-top: 1px solid black;" width="100%" cellpadding="0" cellspacing="0">
                  <tr>
                    <td style="padding-left: 10px; padding-right: 5px;" align="left" nowrap="true"><a class="footer" href="http://www.openremote.org/" target="_blank">© OpenRemote Inc. 2008-2010</a> </td>
                    <td style="padding-left: 5px; padding-right: 5px;" width="100%" align="center" nowrap="true"><a class="footer" href="http://www.openremote.org/" target="_blank">Powered by OpenRemote Beehive</a> </td>
                    <td style="padding-left: 5px; padding-right: 10px;" align="right" nowrap="true">Version: <%@ include file="../common/version.jsp" %> </td>
                  </tr>
              </table></td>
          </tr>
      </table>
      
      <script type="text/javascript" src="jslib/jquery.form-2.24.js"></script>
      <script type="text/javascript" src="jslib/jquery.tablesorter.min.js"></script>
   </body>
</html>