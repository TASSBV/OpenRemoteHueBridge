package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class login_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List _jspx_dependants;

  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fc_005fif_0026_005ftest;
  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005ffmt_005fformatDate_0026_005fvalue_005fpattern_005fnobody;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.AnnotationProcessor _jsp_annotationprocessor;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _005fjspx_005ftagPool_005fc_005fif_0026_005ftest = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _005fjspx_005ftagPool_005ffmt_005fformatDate_0026_005fvalue_005fpattern_005fnobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_annotationprocessor = (org.apache.AnnotationProcessor) getServletConfig().getServletContext().getAttribute(org.apache.AnnotationProcessor.class.getName());
  }

  public void _jspDestroy() {
    _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.release();
    _005fjspx_005ftagPool_005ffmt_005fformatDate_0026_005fvalue_005fpattern_005fnobody.release();
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

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      java.util.Date now = null;
      synchronized (_jspx_page_context) {
        now = (java.util.Date) _jspx_page_context.getAttribute("now", PageContext.PAGE_SCOPE);
        if (now == null){
          now = new java.util.Date();
          _jspx_page_context.setAttribute("now", now, PageContext.PAGE_SCOPE);
        }
      }
      out.write("\r\n");
      out.write("\r\n");
      out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\r\n");
      out.write("\r\n");
      out.write("<html>\r\n");
      out.write("  <head>\r\n");
      out.write("    <meta http-equiv = \"Content-Type\" content = \"text/html; charset=UTF-8\">\r\n");
      out.write("    <meta content = \"openremote, open source, home automation, open source automation,\r\n");
      out.write("                     iphone, android, ipad, knx, insteon, x10, infrared, z-wave, isy-99,\r\n");
      out.write("                     russound, lutron, domintell, globalcache, irtrans, samsung,\r\n");
      out.write("                     samsung smart tv, insteon\"\r\n");
      out.write("          name = \"KEYWORDS\"/>\r\n");
      out.write("\r\n");
      out.write("    <link href = \"image/OpenRemote.Logo.16x16.png\" rel = \"shortcut icon\"/>\r\n");
      out.write("    <link href = \"image/OpenRemote.Logo.16x16.png\" type = \"image/png\" rel = \"icon\"/>\r\n");
      out.write("\r\n");
      out.write("    <title>OpenRemote Designer</title>\r\n");
      out.write("\r\n");
      out.write("    <style type=\"text/css\">\r\n");
      out.write("\r\n");
      out.write("      BODY\r\n");
      out.write("      {\r\n");
      out.write("        background-color: rgb(255, 255, 255);\r\n");
      out.write("        color: rgb(107, 92, 79);\r\n");
      out.write("\r\n");
      out.write("        border-width: 0px;\r\n");
      out.write("        border-style: none;\r\n");
      out.write("\r\n");
      out.write("        font-family: Verdana, Arial, sans-serif;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("      DIV.main\r\n");
      out.write("      {\r\n");
      out.write("\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("      DIV.logo\r\n");
      out.write("      {\r\n");
      out.write("        margin:       0px 0px 30px 0px;\r\n");
      out.write("\r\n");
      out.write("        text-align:   center;\r\n");
      out.write("        overflow:     visible;\r\n");
      out.write("\r\n");
      out.write("        height:       80px;\r\n");
      out.write("        max-height:   80px;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("      IMG.watermark\r\n");
      out.write("      {\r\n");
      out.write("        opacity:      0.20;\r\n");
      out.write("\r\n");
      out.write("        z-index:      -10;\r\n");
      out.write("\r\n");
      out.write("        position:     relative;\r\n");
      out.write("        top:          -60px;\r\n");
      out.write("        left:         -390px;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("      DIV.warning-notice\r\n");
      out.write("      {\r\n");
      out.write("        margin:                20px 2px;\r\n");
      out.write("        padding:               20px;\r\n");
      out.write("\r\n");
      out.write("        box-shadow:            0px 0px 8px rgba(212, 71, 15, 0.4);\r\n");
      out.write("        -moz-box-shadow:       0px 0px 8px rgba(212, 71, 15, 0.4);\r\n");
      out.write("        -webkit-box-shadow:    0px 0px 8px rgba(212, 71, 15, 0.4);\r\n");
      out.write("\r\n");
      out.write("        background-color:      rgba(212, 71, 15, 0.6);\r\n");
      out.write("        color:                 rgba(156, 48, 26, 0.8);\r\n");
      out.write("\r\n");
      out.write("        border-width:          3px;\r\n");
      out.write("        border-color:          rgba(156, 48, 26, .8);\r\n");
      out.write("        border-style:          solid;\r\n");
      out.write("\r\n");
      out.write("        border-radius:         15px;\r\n");
      out.write("        -moz-border-radius:    15px;\r\n");
      out.write("        -webkit-border-radius: 15px;\r\n");
      out.write("\r\n");
      out.write("        display:               none;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("      DIV.box\r\n");
      out.write("      {\r\n");
      out.write("        margin:                20px 0px;\r\n");
      out.write("        padding:               0px 20px 0px 20px;\r\n");
      out.write("\r\n");
      out.write("        box-shadow:            0px 0px 35px rgba(107, 92, 79, 0.4);\r\n");
      out.write("        -moz-box-shadow:       0px 0px 35px rgba(107, 92, 79, 0.4);\r\n");
      out.write("        -webkit-box-shadow:    0px 0px 35px rgba(107, 92, 79, 0.4);\r\n");
      out.write("\r\n");
      out.write("        background-color:      rgba(245, 245, 245, 0.6);\r\n");
      out.write("\r\n");
      out.write("        border-width:          3px;\r\n");
      out.write("        border-color:          white;\r\n");
      out.write("        border-style:          solid;\r\n");
      out.write("\r\n");
      out.write("        border-radius:         15px;\r\n");
      out.write("        -moz-border-radius:    15px;\r\n");
      out.write("        -webkit-border-radius: 15px;\r\n");
      out.write("\r\n");
      out.write("        position:              relative;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      H2.box-header\r\n");
      out.write("      {\r\n");
      out.write("        font-weight:  normal;\r\n");
      out.write("        font-size:    15px;\r\n");
      out.write("\r\n");
      out.write("        margin:\t      10px 0px 20px 0px;\r\n");
      out.write("        color:        rgba(79, 168, 0, 1.0);\r\n");
      out.write("        text-shadow:  2px 2px 2px rgba(230, 219, 209, 1.0);\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("      DIV.box P\r\n");
      out.write("      {\r\n");
      out.write("        color:        rgba(107, 92, 79, 1.0);\r\n");
      out.write("        margin:       10px 0px 20px 0px;\r\n");
      out.write("\r\n");
      out.write("        font-family:    Verdana, Arial, sans-serif;\r\n");
      out.write("        letter-spacing: 0.08em\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("      DIV.box P A,\r\n");
      out.write("      DIV.box P A:visited\r\n");
      out.write("      {\r\n");
      out.write("        color:        rgba(79, 168, 0, 1.0);\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      DIV.box P A:hover\r\n");
      out.write("      {\r\n");
      out.write("        color:        rgba(186, 235, 92, 1.0);\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      \r\n");
      out.write("      .form_label\r\n");
      out.write("      {\r\n");
      out.write("        padding-right:      10px;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      .center-form\r\n");
      out.write("      {\r\n");
      out.write("        margin:             auto;\r\n");
      out.write("        font-size:          11px;\r\n");
      out.write("        color:              #4D4D4D;\r\n");
      out.write("        width:              500px;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      a img\r\n");
      out.write("      {\r\n");
      out.write("        border:             none;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      .login_submit\r\n");
      out.write("      {\r\n");
      out.write("        width:              70px;\r\n");
      out.write("        margin-left:        80px;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      .copyright\r\n");
      out.write("      {\r\n");
      out.write("        text-align:         center;\r\n");
      out.write("        margin:             5px 0px 80px 0px;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      p.title\r\n");
      out.write("      {\r\n");
      out.write("        text-align:         center;\r\n");
      out.write("        font-weight:        bold;\r\n");
      out.write("        font-size:          13px;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      p.fail\r\n");
      out.write("      {\r\n");
      out.write("        color:              red;\r\n");
      out.write("        text-align:         center;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      p.pass\r\n");
      out.write("      {\r\n");
      out.write("        color:              green;\r\n");
      out.write("        text-align:         center;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      .register_btn\r\n");
      out.write("      {\r\n");
      out.write("        margin-left:        40px;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      div.inner-boundary\r\n");
      out.write("      {\r\n");
      out.write("        border:             1px solid #A6A6A6;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      div.inner-border\r\n");
      out.write("      {\r\n");
      out.write("        border:             1px solid #FEFEFE;\r\n");
      out.write("        background-color:   rgba(230, 219, 209, .7);\r\n");
      out.write("        padding:            20px;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      p.input\r\n");
      out.write("      {\r\n");
      out.write("        text-align:         right;\r\n");
      out.write("        width:              240px;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      a, a:hover\r\n");
      out.write("      {\r\n");
      out.write("        color:              #225E8A;\r\n");
      out.write("        text-decoration:    none;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      .incorrect\r\n");
      out.write("      {\r\n");
      out.write("        color:              red;\r\n");
      out.write("        text-align:         left;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("      .forget_btn\r\n");
      out.write("      {\r\n");
      out.write("        margin-left:         10px;\r\n");
      out.write("      }\r\n");
      out.write("\r\n");
      out.write("    </style>\r\n");
      out.write("  </head>\r\n");
      out.write("\r\n");
      out.write("  <body>\r\n");
      out.write("\r\n");
      out.write("    <div class = \"main\">\r\n");
      out.write("\r\n");
      out.write("      <!-- ============ LOGO TITLE ======================================= -->\r\n");
      out.write("\r\n");
      out.write("      <div class = \"logo\">\r\n");
      out.write("        <p>\r\n");
      out.write("          <a href = \"http://www.openremote.org\">\r\n");
      out.write("            <img src    = \"http://www.openremote.org/download/attachments/11960338/OpenRemote-singleline-full-logo_400x62.png\"\r\n");
      out.write("                 border = \"0\"\r\n");
      out.write("                 alt    = \"OpenRemote Logo\"\r\n");
      out.write("            />\r\n");
      out.write("          </a>\r\n");
      out.write("        </p>\r\n");
      out.write("\r\n");
      out.write("      <!-- 'watermark' -->\r\n");
      out.write("\r\n");
      out.write("      <img class  = \"watermark\"\r\n");
      out.write("           src    = \"http://www.openremote.org/download/attachments/11468891/OpenRemote iTunes Icon 512x512.png\"\r\n");
      out.write("           border = \"0\"\r\n");
      out.write("           alt    = \"watermark\"\r\n");
      out.write("      />\r\n");
      out.write("\r\n");
      out.write("      </div>\r\n");
      out.write("\r\n");
      out.write("        \r\n");
      out.write("\r\n");
      out.write("      <div class=\"center-form\">\r\n");
      out.write("\r\n");
      out.write("        <!-- ========== OPTIONAL WARNING NOTICE ========================= -->\r\n");
      out.write("\r\n");
      out.write("        <div class = \"warning-notice\">\r\n");
      out.write("\r\n");
      out.write("        </div>\r\n");
      out.write("\r\n");
      out.write("        <!-- ========== LOGIN FORM ====================================== -->\r\n");
      out.write("\r\n");
      out.write("        <form method=\"POST\" action=\"j_security_check\">\r\n");
      out.write("\r\n");
      out.write("          <div class=\"inner-boundary\">\r\n");
      out.write("            <div class=\"inner-border\">\r\n");
      out.write("\r\n");
      out.write("              <p class=\"title\">Login to OpenRemote Designer (Rosemary)</p>\r\n");
      out.write("\r\n");
      out.write("              ");
      if (_jspx_meth_c_005fif_005f0(_jspx_page_context))
        return;
      out.write("\r\n");
      out.write("\r\n");
      out.write("              ");
      if (_jspx_meth_c_005fif_005f1(_jspx_page_context))
        return;
      out.write("\r\n");
      out.write("\r\n");
      out.write("              ");
      if (_jspx_meth_c_005fif_005f2(_jspx_page_context))
        return;
      out.write("\r\n");
      out.write("\r\n");
      out.write("              ");
      if (_jspx_meth_c_005fif_005f3(_jspx_page_context))
        return;
      out.write("\r\n");
      out.write("\r\n");
      out.write("              ");
      if (_jspx_meth_c_005fif_005f4(_jspx_page_context))
        return;
      out.write("\r\n");
      out.write("\r\n");
      out.write("            </div>\r\n");
      out.write("\t\t  </div>\r\n");
      out.write("\t    </form>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("        <!-- ========== COPYRIGHT NOTICE =================================================== -->\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("        <p class = \"copyright\">\r\n");
      out.write("            Copyright &copy; 2008-");
      if (_jspx_meth_fmt_005fformatDate_005f0(_jspx_page_context))
        return;
      out.write("\r\n");
      out.write("            <a href=\"http://www.openremote.org\">OpenRemote</a> -- Version 2.13.9 (2013-03-30 Rosemary)\r\n");
      out.write("        </p>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("        <!-- ========== CONTROLLER DOWNLOAD REMINDER ====================================== -->\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("        <div class = \"box\">\r\n");
      out.write("\r\n");
      out.write("          <img style  = \"position: absolute; left: -50px; bottom: 0px;\"\r\n");
      out.write("               src    = \"http://www.openremote.org/download/attachments/12845303/ControllerBox_192x144.png\"\r\n");
      out.write("               border = \"0\" />\r\n");
      out.write("            \r\n");
      out.write("          <div style = \"margin: 0px 0px 30px 120px;\">\r\n");
      out.write("\r\n");
      out.write("            <h2 class = \"box-header\">Update to Latest OpenRemote Controller</h2>\r\n");
      out.write("\r\n");
      out.write("            <p>\r\n");
      out.write("              In order to use all the latest Designer features, make sure you've\r\n");
      out.write("              <a href = \"http://download.openremote.org/2.0\">installed OpenRemote Controller 2.0 or later</a>.\r\n");
      out.write("            </p>\r\n");
      out.write("\r\n");
      out.write("<!--\r\n");
      out.write("            <h2 class = \"box-header\">Have You Installed OpenRemote Controller?</h2>\r\n");
      out.write("\r\n");
      out.write("            <p>\r\n");
      out.write("              OpenRemote Automation requires controller installed in your local area\r\n");
      out.write("              network (LAN).\r\n");
      out.write("            </p>\r\n");
      out.write("\r\n");
      out.write("            <p>\r\n");
      out.write("              Installation instructions are available for Windows, Linux,\r\n");
      out.write("              Mac OS X, Synology NAS, ReadyNAS, QNAP NAS and <a href = \"http://openremote.org/display/orb/Building+OpenRemote+Hardware+Reference+Implementation\">ALIX</a>.\r\n");
      out.write("            </p>\r\n");
      out.write("-->\r\n");
      out.write("\r\n");
      out.write("          </div>\r\n");
      out.write("\r\n");
      out.write("        </div>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("      </div>\r\n");
      out.write("    </div>\r\n");
      out.write("  </body>\r\n");
      out.write("</html>\r\n");
      out.write("\r\n");
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

  private boolean _jspx_meth_c_005fif_005f0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:if
    org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_005fif_005f0 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
    _jspx_th_c_005fif_005f0.setPageContext(_jspx_page_context);
    _jspx_th_c_005fif_005f0.setParent(null);
    // /login.jsp(282,14) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
    _jspx_th_c_005fif_005f0.setTest(((java.lang.Boolean) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${isActivated ne null and isActivated}", java.lang.Boolean.class, (PageContext)_jspx_page_context, null, false)).booleanValue());
    int _jspx_eval_c_005fif_005f0 = _jspx_th_c_005fif_005f0.doStartTag();
    if (_jspx_eval_c_005fif_005f0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\r\n");
        out.write("                <p class=\"pass\"><b>");
        out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${username}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
        out.write("</b> has been activated, please login.</p>\r\n");
        out.write("              ");
        int evalDoAfterBody = _jspx_th_c_005fif_005f0.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_c_005fif_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f0);
      return true;
    }
    _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f0);
    return false;
  }

  private boolean _jspx_meth_c_005fif_005f1(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:if
    org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_005fif_005f1 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
    _jspx_th_c_005fif_005f1.setPageContext(_jspx_page_context);
    _jspx_th_c_005fif_005f1.setParent(null);
    // /login.jsp(286,14) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
    _jspx_th_c_005fif_005f1.setTest(((java.lang.Boolean) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${isActivated ne null and not isActivated}", java.lang.Boolean.class, (PageContext)_jspx_page_context, null, false)).booleanValue());
    int _jspx_eval_c_005fif_005f1 = _jspx_th_c_005fif_005f1.doStartTag();
    if (_jspx_eval_c_005fif_005f1 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\r\n");
        out.write("                <p class=\"fail\">Invalid activation credentials, activation failed.</p>\r\n");
        out.write("              ");
        int evalDoAfterBody = _jspx_th_c_005fif_005f1.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_c_005fif_005f1.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f1);
      return true;
    }
    _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f1);
    return false;
  }

  private boolean _jspx_meth_c_005fif_005f2(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:if
    org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_005fif_005f2 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
    _jspx_th_c_005fif_005f2.setPageContext(_jspx_page_context);
    _jspx_th_c_005fif_005f2.setParent(null);
    // /login.jsp(290,14) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
    _jspx_th_c_005fif_005f2.setTest(((java.lang.Boolean) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${needActivation ne null}", java.lang.Boolean.class, (PageContext)_jspx_page_context, null, false)).booleanValue());
    int _jspx_eval_c_005fif_005f2 = _jspx_th_c_005fif_005f2.doStartTag();
    if (_jspx_eval_c_005fif_005f2 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\r\n");
        out.write("                <p class=\"pass\">\r\n");
        out.write("                   We have sent an activation email to <b>");
        out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${email}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
        out.write("</b>,\r\n");
        out.write("                   please follow the instructions in the email to complete\r\n");
        out.write("                   your registration.\r\n");
        out.write("                </p>\r\n");
        out.write("              ");
        int evalDoAfterBody = _jspx_th_c_005fif_005f2.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_c_005fif_005f2.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f2);
      return true;
    }
    _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f2);
    return false;
  }

  private boolean _jspx_meth_c_005fif_005f3(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:if
    org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_005fif_005f3 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
    _jspx_th_c_005fif_005f3.setPageContext(_jspx_page_context);
    _jspx_th_c_005fif_005f3.setParent(null);
    // /login.jsp(298,14) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
    _jspx_th_c_005fif_005f3.setTest(((java.lang.Boolean) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${isAccepted ne null and isAccepted}", java.lang.Boolean.class, (PageContext)_jspx_page_context, null, false)).booleanValue());
    int _jspx_eval_c_005fif_005f3 = _jspx_th_c_005fif_005f3.doStartTag();
    if (_jspx_eval_c_005fif_005f3 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\r\n");
        out.write("                <p class = \"pass\">You have accepted the invitation, please login.</p>\r\n");
        out.write("              ");
        int evalDoAfterBody = _jspx_th_c_005fif_005f3.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_c_005fif_005f3.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f3);
      return true;
    }
    _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f3);
    return false;
  }

  private boolean _jspx_meth_c_005fif_005f4(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:if
    org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_005fif_005f4 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
    _jspx_th_c_005fif_005f4.setPageContext(_jspx_page_context);
    _jspx_th_c_005fif_005f4.setParent(null);
    // /login.jsp(302,14) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
    _jspx_th_c_005fif_005f4.setTest(((java.lang.Boolean) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${needActivation eq null}", java.lang.Boolean.class, (PageContext)_jspx_page_context, null, false)).booleanValue());
    int _jspx_eval_c_005fif_005f4 = _jspx_th_c_005fif_005f4.doStartTag();
    if (_jspx_eval_c_005fif_005f4 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\r\n");
        out.write("                <div style = \"padding-left:110px\">\r\n");
        out.write("                  <p class = \"input\">\r\n");
        out.write("                    <b class = \"form_label\">Username</b>\r\n");
        out.write("                    <input id = \"username\"\r\n");
        out.write("                           style = \"width:150px\"\r\n");
        out.write("                           type = \"text\"\r\n");
        out.write("                           name = \"j_username\"\r\n");
        out.write("                           value = \"");
        out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${username}", java.lang.String.class, (PageContext)_jspx_page_context, null, false));
        out.write("\">\r\n");
        out.write("                  </p>\r\n");
        out.write("\r\n");
        out.write("                  <p class = \"input\">\r\n");
        out.write("                    <b class = \"form_label\">Password</b>\r\n");
        out.write("                    <input id = \"password\"\r\n");
        out.write("                           style = \"width:150px\"\r\n");
        out.write("                           type = \"password\"\r\n");
        out.write("                           name = \"j_password\"\r\n");
        out.write("                           value = \"\">\r\n");
        out.write("                  </p>\r\n");
        out.write("\r\n");
        out.write("                  ");
        if (_jspx_meth_c_005fif_005f5(_jspx_th_c_005fif_005f4, _jspx_page_context))
          return true;
        out.write("\r\n");
        out.write("\r\n");
        out.write("                  <p>\r\n");
        out.write("                    <input id = \"rememberme\" type = \"checkbox\" name = \"_spring_security_remember_me\">\r\n");
        out.write("                      <label for = \"rememberme\">Remember Me</label>\r\n");
        out.write("\r\n");
        out.write("                    <a class = \"register_btn\" href = \"register.jsp\">Create a New Account</a>\r\n");
        out.write("                  </p>\r\n");
        out.write("\r\n");
        out.write("                  <div>\r\n");
        out.write("                    <input class = \"login_submit\" type = \"submit\" value = \"Login\">\r\n");
        out.write("\r\n");
        out.write("                    <a class = \"forget_btn\" href = \"forget.jsp\">Forgot password?</a>\r\n");
        out.write("                  </div>\r\n");
        out.write("                </div>\r\n");
        out.write("              ");
        int evalDoAfterBody = _jspx_th_c_005fif_005f4.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_c_005fif_005f4.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f4);
      return true;
    }
    _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f4);
    return false;
  }

  private boolean _jspx_meth_c_005fif_005f5(javax.servlet.jsp.tagext.JspTag _jspx_th_c_005fif_005f4, PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  c:if
    org.apache.taglibs.standard.tag.rt.core.IfTag _jspx_th_c_005fif_005f5 = (org.apache.taglibs.standard.tag.rt.core.IfTag) _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.get(org.apache.taglibs.standard.tag.rt.core.IfTag.class);
    _jspx_th_c_005fif_005f5.setPageContext(_jspx_page_context);
    _jspx_th_c_005fif_005f5.setParent((javax.servlet.jsp.tagext.Tag) _jspx_th_c_005fif_005f4);
    // /login.jsp(322,18) name = test type = boolean reqTime = true required = true fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
    _jspx_th_c_005fif_005f5.setTest(((java.lang.Boolean) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${param.fail ne null }", java.lang.Boolean.class, (PageContext)_jspx_page_context, null, false)).booleanValue());
    int _jspx_eval_c_005fif_005f5 = _jspx_th_c_005fif_005f5.doStartTag();
    if (_jspx_eval_c_005fif_005f5 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
      do {
        out.write("\r\n");
        out.write("                    <p class = \"incorrect\">The username or password you entered is incorrect.</p>\r\n");
        out.write("                  ");
        int evalDoAfterBody = _jspx_th_c_005fif_005f5.doAfterBody();
        if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
          break;
      } while (true);
    }
    if (_jspx_th_c_005fif_005f5.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f5);
      return true;
    }
    _005fjspx_005ftagPool_005fc_005fif_0026_005ftest.reuse(_jspx_th_c_005fif_005f5);
    return false;
  }

  private boolean _jspx_meth_fmt_005fformatDate_005f0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  fmt:formatDate
    org.apache.taglibs.standard.tag.rt.fmt.FormatDateTag _jspx_th_fmt_005fformatDate_005f0 = (org.apache.taglibs.standard.tag.rt.fmt.FormatDateTag) _005fjspx_005ftagPool_005ffmt_005fformatDate_0026_005fvalue_005fpattern_005fnobody.get(org.apache.taglibs.standard.tag.rt.fmt.FormatDateTag.class);
    _jspx_th_fmt_005fformatDate_005f0.setPageContext(_jspx_page_context);
    _jspx_th_fmt_005fformatDate_005f0.setParent(null);
    // /login.jsp(350,34) name = value type = null reqTime = true required = true fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
    _jspx_th_fmt_005fformatDate_005f0.setValue((java.util.Date) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${now}", java.util.Date.class, (PageContext)_jspx_page_context, null, false));
    // /login.jsp(350,34) name = pattern type = null reqTime = true required = false fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
    _jspx_th_fmt_005fformatDate_005f0.setPattern("yyyy");
    int _jspx_eval_fmt_005fformatDate_005f0 = _jspx_th_fmt_005fformatDate_005f0.doStartTag();
    if (_jspx_th_fmt_005fformatDate_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _005fjspx_005ftagPool_005ffmt_005fformatDate_0026_005fvalue_005fpattern_005fnobody.reuse(_jspx_th_fmt_005fformatDate_005f0);
      return true;
    }
    _005fjspx_005ftagPool_005ffmt_005fformatDate_0026_005fvalue_005fpattern_005fnobody.reuse(_jspx_th_fmt_005fformatDate_005f0);
    return false;
  }
}
