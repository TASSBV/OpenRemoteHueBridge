<!doctype html>
<!-- The DOCTYPE declaration above will set the    -->
<!-- browser's rendering engine into               -->
<!-- "Standards Mode". Replacing this declaration  -->
<!-- with a "Quirks Mode" doctype may lead to some -->
<!-- differences in layout.                        -->

<html>
	
  <head>
    <meta http-equiv = "content-type" content = "text/html; charset=UTF-8">
	<meta http-equiv = "pragma" content = "no-cache">
	<meta http-equiv = "cache-control" content = "no-cache">
    <meta http-equiv = "expires" content = "0">

    <meta content = "openremote, open source, home automation, iphone, android,
                   knx, insteon, x10, infrared, z-wave, zigbee, isy-99, russound,
                   lutron, domintell, globalcache, irtrans, lirc"
             name = "KEYWORDS"/>

    <link href = "image/OpenRemote.Logo.16x16.png" rel = "shortcut icon"/>
	<link href = "image/OpenRemote.Logo.16x16.png" type = "image/png" rel = "icon"/>

    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
	<link rel = "stylesheet" type = "text/css" href = "resources/css/gxt-all.css"/>

    <!-- Support gray theme, if use default, just comment the next line.-->
	<link rel = "stylesheet" type = "text/css" href = "resources/css/gxt-gray.css"/>

    <link rel = "stylesheet" type = "text/css" href = "resources/css/main.css"/>
	
    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>OpenRemote Designer</title>
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type = "text/javascript" language = "javascript" src = "modeler/modeler.nocache.js"></script>

    <style type = "text/css">

        BODY
        {
          background-image:    url('http://www.openremote.org/download/attachments/11960338/OpenRemote-singleline-full-logo_400x62.png');
          background-repeat:   no-repeat;
          background-position: center 20px;
        }
        
        #loading-cont
        {
          border-width:          2px;
          border-color:          rgba(173, 161, 148, 0.7);
          border-style:          solid;

          border-radius:         5px;
          -moz-border-radius:    5px;
          -webkit-border-radius: 5px;

          box-shadow:            0px 0px 35px rgba(107, 92, 79, 0.4);
          -moz-box-shadow:       0px 0px 35px rgba(107, 92, 79, 0.4);
          -webkit-box-shadow:    0px 0px 35px rgba(107, 92, 79, 0.4);

          padding:               2px;
          margin-left:           -147px;

          height:                auto;
          min-width:             300px;

          position:              absolute;
          left:                  50%;
          top:                   40%;

          z-index:               20001;
        }

        #loading-cont .loading-indicator-img
        {
            background:   none repeat scroll 0 0 white;

            height:	      auto;
            margin:	      0;
            padding:	  10px;
        }

        #loading-cont .loading-indicator-img img
        {
          float:          left;
          margin-right:   8px;
          vertical-align: top;
        }

       	P.loading-title
        {
          font-family:    Verdana, Arial, sans-serif;
          font-weight:    normal;
          font-size:      15px;
          text-align:     center;

          color:          rgb(79,168,0);
          text-shadow:    2px 2px 2px rgb(230, 219, 209);
        }

        P.loading-msg
        {
          font:           10px Verdana, Arial, sans-serif;
          color:          rgb(107, 92, 79);

          text-align:     center;
          margin:         10px 0px 0px 0px;
        }

    </style>

  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body>

    <!-- OPTIONAL: include this if you want history support -->
    <iframe src = "javascript:''" id = "__gwt_historyFrame" tabIndex = '-1'
            style = "position: absolute; width: 0; height: 0;border: 0">
    </iframe>

    <!-- The loading message container div -->
        <div id = "loading-cont">
            <div class = "loading-indicator-img">

              <img width = "32" height = "32" src = "resources/images/large-loading.gif"/>

              <p class = "loading-title">OpenRemote Designer</p>

              <p class = "loading-msg">Loading application, please wait...</p>
            </div>
        </div>

        <div id="main"></div>

  </body>

</html>
