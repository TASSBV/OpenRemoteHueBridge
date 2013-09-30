<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:useBean id="now" class="java.util.Date" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
  <head>
    <meta http-equiv = "Content-Type" content = "text/html; charset=UTF-8">
    <meta content = "openremote, open source, home automation, open source automation,
                     iphone, android, ipad, knx, insteon, x10, infrared, z-wave, isy-99,
                     russound, lutron, domintell, globalcache, irtrans, samsung,
                     samsung smart tv, insteon"
          name = "KEYWORDS"/>

    <link href = "image/OpenRemote.Logo.16x16.png" rel = "shortcut icon"/>
    <link href = "image/OpenRemote.Logo.16x16.png" type = "image/png" rel = "icon"/>

    <title>OpenRemote Designer</title>

    <style type="text/css">

      BODY
      {
        background-color: rgb(255, 255, 255);
        color: rgb(107, 92, 79);

        border-width: 0px;
        border-style: none;

        font-family: Verdana, Arial, sans-serif;
      }


      DIV.main
      {

      }


      DIV.logo
      {
        margin:       0px 0px 30px 0px;

        text-align:   center;
        overflow:     visible;

        height:       80px;
        max-height:   80px;
      }


      IMG.watermark
      {
        opacity:      0.20;

        z-index:      -10;

        position:     relative;
        top:          -60px;
        left:         -390px;
      }


      DIV.warning-notice
      {
        margin:                20px 2px;
        padding:               20px;

        box-shadow:            0px 0px 8px rgba(212, 71, 15, 0.4);
        -moz-box-shadow:       0px 0px 8px rgba(212, 71, 15, 0.4);
        -webkit-box-shadow:    0px 0px 8px rgba(212, 71, 15, 0.4);

        background-color:      rgba(212, 71, 15, 0.6);
        color:                 rgba(156, 48, 26, 0.8);

        border-width:          3px;
        border-color:          rgba(156, 48, 26, .8);
        border-style:          solid;

        border-radius:         15px;
        -moz-border-radius:    15px;
        -webkit-border-radius: 15px;

        display:               none;
      }


      DIV.box
      {
        margin:                20px 0px;
        padding:               0px 20px 0px 20px;

        box-shadow:            0px 0px 35px rgba(107, 92, 79, 0.4);
        -moz-box-shadow:       0px 0px 35px rgba(107, 92, 79, 0.4);
        -webkit-box-shadow:    0px 0px 35px rgba(107, 92, 79, 0.4);

        background-color:      rgba(245, 245, 245, 0.6);

        border-width:          3px;
        border-color:          white;
        border-style:          solid;

        border-radius:         15px;
        -moz-border-radius:    15px;
        -webkit-border-radius: 15px;

        position:              relative;
      }

      H2.box-header
      {
        font-weight:  normal;
        font-size:    15px;

        margin:	      10px 0px 20px 0px;
        color:        rgba(79, 168, 0, 1.0);
        text-shadow:  2px 2px 2px rgba(230, 219, 209, 1.0);
      }


      DIV.box P
      {
        color:        rgba(107, 92, 79, 1.0);
        margin:       10px 0px 20px 0px;

        font-family:    Verdana, Arial, sans-serif;
        letter-spacing: 0.08em
      }


      DIV.box P A,
      DIV.box P A:visited
      {
        color:        rgba(79, 168, 0, 1.0);
      }

      DIV.box P A:hover
      {
        color:        rgba(186, 235, 92, 1.0);
      }

      
      .form_label
      {
        padding-right:      10px;
      }

      .center-form
      {
        margin:             auto;
        font-size:          11px;
        color:              #4D4D4D;
        width:              500px;
      }

      a img
      {
        border:             none;
      }

      .login_submit
      {
        width:              70px;
        margin-left:        80px;
      }

      .copyright
      {
        text-align:         center;
        margin:             5px 0px 80px 0px;
      }

      p.title
      {
        text-align:         center;
        font-weight:        bold;
        font-size:          13px;
      }

      p.fail
      {
        color:              red;
        text-align:         center;
      }

      p.pass
      {
        color:              green;
        text-align:         center;
      }

      .register_btn
      {
        margin-left:        40px;
      }

      div.inner-boundary
      {
        border:             1px solid #A6A6A6;
      }

      div.inner-border
      {
        border:             1px solid #FEFEFE;
        background-color:   rgba(230, 219, 209, .7);
        padding:            20px;
      }

      p.input
      {
        text-align:         right;
        width:              240px;
      }

      a, a:hover
      {
        color:              #225E8A;
        text-decoration:    none;
      }

      .incorrect
      {
        color:              red;
        text-align:         left;
      }

      .forget_btn
      {
        margin-left:         10px;
      }

    </style>
  </head>

  <body>

    <div class = "main">

      <!-- ============ LOGO TITLE ======================================= -->

      <div class = "logo">
        <p>
          <a href = "http://www.openremote.org">
            <img src    = "http://www.openremote.org/download/attachments/11960338/OpenRemote-singleline-full-logo_400x62.png"
                 border = "0"
                 alt    = "OpenRemote Logo"
            />
          </a>
        </p>

      <!-- 'watermark' -->

      <img class  = "watermark"
           src    = "http://www.openremote.org/download/attachments/11468891/OpenRemote iTunes Icon 512x512.png"
           border = "0"
           alt    = "watermark"
      />

      </div>

        

      <div class="center-form">

        <!-- ========== OPTIONAL WARNING NOTICE ========================= -->

        <div class = "warning-notice">

        </div>

        <!-- ========== LOGIN FORM ====================================== -->

        <form method="POST" action="j_security_check">

          <div class="inner-boundary">
            <div class="inner-border">

              <p class="title">Login to OpenRemote Designer (@VERSION.NAME@)</p>

              <c:if test = "${isActivated ne null and isActivated}">
                <p class="pass"><b>${username}</b> has been activated, please login.</p>
              </c:if>

              <c:if test = "${isActivated ne null and not isActivated}">
                <p class="fail">Invalid activation credentials, activation failed.</p>
              </c:if>

              <c:if test = "${needActivation ne null}">
                <p class="pass">
                   We have sent an activation email to <b>${email}</b>,
                   please follow the instructions in the email to complete
                   your registration.
                </p>
              </c:if>

              <c:if test = "${isAccepted ne null and isAccepted}">
                <p class = "pass">You have accepted the invitation, please login.</p>
              </c:if>

              <c:if test = "${needActivation eq null}">
                <div style = "padding-left:110px">
                  <p class = "input">
                    <b class = "form_label">Username</b>
                    <input id = "username"
                           style = "width:150px"
                           type = "text"
                           name = "j_username"
                           value = "${username}">
                  </p>

                  <p class = "input">
                    <b class = "form_label">Password</b>
                    <input id = "password"
                           style = "width:150px"
                           type = "password"
                           name = "j_password"
                           value = "">
                  </p>

                  <c:if test = "${param.fail ne null }">
                    <p class = "incorrect">The username or password you entered is incorrect.</p>
                  </c:if>

                  <p>
                    <input id = "rememberme" type = "checkbox" name = "_spring_security_remember_me">
                      <label for = "rememberme">Remember Me</label>

                    <a class = "register_btn" href = "register.jsp">Create a New Account</a>
                  </p>

                  <div>
                    <input class = "login_submit" type = "submit" value = "Login">

                    <a class = "forget_btn" href = "forget.jsp">Forgot password?</a>
                  </div>
                </div>
              </c:if>

            </div>
		  </div>
	    </form>


        <!-- ========== COPYRIGHT NOTICE =================================================== -->


        <p class = "copyright">
            Copyright &copy; 2008-<fmt:formatDate value = "${now}" pattern = "yyyy"/>
            <a href="http://www.openremote.org">OpenRemote</a> -- Version @VERSION@ (@BUILD.DATE@ @VERSION.NAME@)
        </p>



        <!-- ========== CONTROLLER DOWNLOAD REMINDER ====================================== -->


        <div class = "box">

          <img style  = "position: absolute; left: -50px; bottom: 0px;"
               src    = "http://www.openremote.org/download/attachments/12845303/ControllerBox_192x144.png"
               border = "0" />
            
          <div style = "margin: 0px 0px 30px 120px;">

            <h2 class = "box-header">Update to Latest OpenRemote Controller</h2>

            <p>
              In order to use all the latest Designer features, make sure you've
              <a href = "http://download.openremote.org/2.0">installed OpenRemote Controller 2.0 or later</a>.
            </p>

<!--
            <h2 class = "box-header">Have You Installed OpenRemote Controller?</h2>

            <p>
              OpenRemote Automation requires controller installed in your local area
              network (LAN).
            </p>

            <p>
              Installation instructions are available for Windows, Linux,
              Mac OS X, Synology NAS, ReadyNAS, QNAP NAS and <a href = "http://openremote.org/display/orb/Building+OpenRemote+Hardware+Reference+Implementation">ALIX</a>.
            </p>
-->

          </div>

        </div>


      </div>
    </div>
  </body>
</html>

