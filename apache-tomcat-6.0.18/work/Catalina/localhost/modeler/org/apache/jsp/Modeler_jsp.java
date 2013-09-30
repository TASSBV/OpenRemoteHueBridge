package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class Modeler_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("<!doctype html>\n");
      out.write("<!-- The DOCTYPE declaration above will set the    -->\n");
      out.write("<!-- browser's rendering engine into               -->\n");
      out.write("<!-- \"Standards Mode\". Replacing this declaration  -->\n");
      out.write("<!-- with a \"Quirks Mode\" doctype may lead to some -->\n");
      out.write("<!-- differences in layout.                        -->\n");
      out.write("\n");
      out.write("<html>\n");
      out.write("\t\n");
      out.write("  <head>\n");
      out.write("    <meta http-equiv = \"content-type\" content = \"text/html; charset=UTF-8\">\n");
      out.write("\t<meta http-equiv = \"pragma\" content = \"no-cache\">\n");
      out.write("\t<meta http-equiv = \"cache-control\" content = \"no-cache\">\n");
      out.write("    <meta http-equiv = \"expires\" content = \"0\">\n");
      out.write("\n");
      out.write("    <meta content = \"openremote, open source, home automation, iphone, android,\n");
      out.write("                   knx, insteon, x10, infrared, z-wave, zigbee, isy-99, russound,\n");
      out.write("                   lutron, domintell, globalcache, irtrans, lirc\"\n");
      out.write("             name = \"KEYWORDS\"/>\n");
      out.write("\n");
      out.write("    <link href = \"image/OpenRemote.Logo.16x16.png\" rel = \"shortcut icon\"/>\n");
      out.write("\t<link href = \"image/OpenRemote.Logo.16x16.png\" type = \"image/png\" rel = \"icon\"/>\n");
      out.write("\n");
      out.write("    <!--                                                               -->\n");
      out.write("    <!-- Consider inlining CSS to reduce the number of requested files -->\n");
      out.write("    <!--                                                               -->\n");
      out.write("\t<link rel = \"stylesheet\" type = \"text/css\" href = \"resources/css/gxt-all.css\"/>\n");
      out.write("\n");
      out.write("    <!-- Support gray theme, if use default, just comment the next line.-->\n");
      out.write("\t<link rel = \"stylesheet\" type = \"text/css\" href = \"resources/css/gxt-gray.css\"/>\n");
      out.write("\n");
      out.write("    <link rel = \"stylesheet\" type = \"text/css\" href = \"resources/css/main.css\"/>\n");
      out.write("\t\n");
      out.write("    <!--                                           -->\n");
      out.write("    <!-- Any title is fine                         -->\n");
      out.write("    <!--                                           -->\n");
      out.write("    <title>OpenRemote Designer</title>\n");
      out.write("    \n");
      out.write("    <!--                                           -->\n");
      out.write("    <!-- This script loads your compiled module.   -->\n");
      out.write("    <!-- If you add any GWT meta tags, they must   -->\n");
      out.write("    <!-- be added before this line.                -->\n");
      out.write("    <!--                                           -->\n");
      out.write("    <script type = \"text/javascript\" language = \"javascript\" src = \"modeler/modeler.nocache.js\"></script>\n");
      out.write("\n");
      out.write("    <style type = \"text/css\">\n");
      out.write("\n");
      out.write("        BODY\n");
      out.write("        {\n");
      out.write("          background-image:    url('http://www.openremote.org/download/attachments/11960338/OpenRemote-singleline-full-logo_400x62.png');\n");
      out.write("          background-repeat:   no-repeat;\n");
      out.write("          background-position: center 20px;\n");
      out.write("        }\n");
      out.write("        \n");
      out.write("        #loading-cont\n");
      out.write("        {\n");
      out.write("          border-width:          2px;\n");
      out.write("          border-color:          rgba(173, 161, 148, 0.7);\n");
      out.write("          border-style:          solid;\n");
      out.write("\n");
      out.write("          border-radius:         5px;\n");
      out.write("          -moz-border-radius:    5px;\n");
      out.write("          -webkit-border-radius: 5px;\n");
      out.write("\n");
      out.write("          box-shadow:            0px 0px 35px rgba(107, 92, 79, 0.4);\n");
      out.write("          -moz-box-shadow:       0px 0px 35px rgba(107, 92, 79, 0.4);\n");
      out.write("          -webkit-box-shadow:    0px 0px 35px rgba(107, 92, 79, 0.4);\n");
      out.write("\n");
      out.write("          padding:               2px;\n");
      out.write("          margin-left:           -147px;\n");
      out.write("\n");
      out.write("          height:                auto;\n");
      out.write("          min-width:             300px;\n");
      out.write("\n");
      out.write("          position:              absolute;\n");
      out.write("          left:                  50%;\n");
      out.write("          top:                   40%;\n");
      out.write("\n");
      out.write("          z-index:               20001;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        #loading-cont .loading-indicator-img\n");
      out.write("        {\n");
      out.write("            background:   none repeat scroll 0 0 white;\n");
      out.write("\n");
      out.write("            height:\t      auto;\n");
      out.write("            margin:\t      0;\n");
      out.write("            padding:\t  10px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        #loading-cont .loading-indicator-img img\n");
      out.write("        {\n");
      out.write("          float:          left;\n");
      out.write("          margin-right:   8px;\n");
      out.write("          vertical-align: top;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("       \tP.loading-title\n");
      out.write("        {\n");
      out.write("          font-family:    Verdana, Arial, sans-serif;\n");
      out.write("          font-weight:    normal;\n");
      out.write("          font-size:      15px;\n");
      out.write("          text-align:     center;\n");
      out.write("\n");
      out.write("          color:          rgb(79,168,0);\n");
      out.write("          text-shadow:    2px 2px 2px rgb(230, 219, 209);\n");
      out.write("        }\n");
      out.write("\n");
      out.write("        P.loading-msg\n");
      out.write("        {\n");
      out.write("          font:           10px Verdana, Arial, sans-serif;\n");
      out.write("          color:          rgb(107, 92, 79);\n");
      out.write("\n");
      out.write("          text-align:     center;\n");
      out.write("          margin:         10px 0px 0px 0px;\n");
      out.write("        }\n");
      out.write("\n");
      out.write("    </style>\n");
      out.write("\n");
      out.write("  </head>\n");
      out.write("\n");
      out.write("  <!--                                           -->\n");
      out.write("  <!-- The body can have arbitrary html, or      -->\n");
      out.write("  <!-- you can leave the body empty if you want  -->\n");
      out.write("  <!-- to create a completely dynamic UI.        -->\n");
      out.write("  <!--                                           -->\n");
      out.write("  <body>\n");
      out.write("\n");
      out.write("    <!-- OPTIONAL: include this if you want history support -->\n");
      out.write("    <iframe src = \"javascript:''\" id = \"__gwt_historyFrame\" tabIndex = '-1'\n");
      out.write("            style = \"position: absolute; width: 0; height: 0;border: 0\">\n");
      out.write("    </iframe>\n");
      out.write("\n");
      out.write("    <!-- The loading message container div -->\n");
      out.write("        <div id = \"loading-cont\">\n");
      out.write("            <div class = \"loading-indicator-img\">\n");
      out.write("\n");
      out.write("              <img width = \"32\" height = \"32\" src = \"resources/images/large-loading.gif\"/>\n");
      out.write("\n");
      out.write("              <p class = \"loading-title\">OpenRemote Designer</p>\n");
      out.write("\n");
      out.write("              <p class = \"loading-msg\">Loading application, please wait...</p>\n");
      out.write("            </div>\n");
      out.write("        </div>\n");
      out.write("\n");
      out.write("        <div id=\"main\"></div>\n");
      out.write("\n");
      out.write("  </body>\n");
      out.write("\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try { out.clearBuffer(); } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
