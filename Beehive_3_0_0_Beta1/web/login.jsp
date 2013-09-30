<%@ page language="java" contentType="text/html; charset=UTF8"	pageEncoding="UTF8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF8">
<link rel="stylesheet" href="css/login.css" type="text/css" />
<title>Login - Beehive</title>
</head>
<body>
<div align="left" style="margin: 20px;">
   <a rel="" href="http://openremote.org">
      <img border="0" alt="global logo" src="image/global.logo.gif" title="Click this logo to the openremote home page"/>
   </a>
</div>

<form method="POST" action="<c:url value='j_security_check'/>" name="loginform" style="vertical-align: middle;">
   <div align="center">
      <div style="margin-top: 20px; border-top:1px solid #CCC; padding-top:10px">
         <c:if test="${not empty param.login_error}">
			      <font color="red">
			        Your login attempt was not successful, try again.<br/>
			      </font>
			</c:if>
      </div>
      <div style="width: 500px; border:1px solid #CCC">
         <div style="text-align: left;">
            <div class="form-header">
               <p>Enter your account details below to login to Beehive.</p>
            </div>
            <div class="form-block">
	            <p>
	               <div class="steplabel" style="width: 150px;"><u>U</u>sername:</div>
	               <input type="text" name="j_username" tabindex="1" accesskey="U" size="30"><br />
	            </p>
					<p>
					    <div class="steplabel" style="width: 150px;"><u>P</u>assword:</div>
					    <input type="password" name="j_password" tabindex="2" accesskey="P" size="30"><br />
					</p>					
            </div>
				<div class="form-buttons">
                    <input id="loginButton" name="login"	type="submit" value="Log In" tabindex="4" />
				</div>								
				<div class="copyright">Powered by <a href="http://openremote.org">OpenRemote</a>
				</div>            
         </div>
      </div>
   </div>
</form>

</body>
</html>