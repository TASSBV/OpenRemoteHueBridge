<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:useBean id="now" class="java.util.Date" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta content="openremote, knx, iphone, insteon, x10, infrared, crestron, zigbee, opensource, gpl, iknx, lirc, beehive, modeler, uicomposer" name="KEYWORDS"/>
<link href="image/OpenRemote.Logo.16x16.png" rel="shortcut icon"/>
<link href="image/OpenRemote.Logo.16x16.png" type="image/png" rel="icon"/>
<title>Register - OpenRemote Designer</title>
<style type="text/css">
    body{
        line-height:100%;
        background-color:#D2D1D0;
        font-family:'Lucida Grande',Geneva,Verdana,Arial,sans-serif;
    }
	.form_label{
	    padding-right:10px;
	}
	.center-form{
	   margin:auto;
	   font-size:11px;
	   color:#4D4D4D;
	   width:500px;
	}
	a img{
        border:none;
    }
    .register_submit{
		margin-left:84px;
		width:150px;
    }
    .copyright{
        text-align: center;
    }
    p.title {
    	text-align: center; 
    	font-weight: bold; 
    	font-size: 13px;
    }
    p.fail{
        color:red;
        text-align: center;
    }
    p.pass{
        color:green;
    }
    div.inner-boundary {
		border:1px solid #A6A6A6;
	}
	div.inner-border  {
		border:1px solid #FEFEFE;
		background-color:#E0E0E0;	
		padding:20px;
	}
	p.input {
		text-align: right;
		width: 290px;
	}
	a, a:hover {
		color:#225E8A;
		text-decoration:none;
	}
</style>
</head>
<body>

<div class="center-form">
	<form method="POST" action="account.htm?method=acceptInvition">
		<div class="inner-boundary">
			  <div class="inner-border">
	            <a href="http://www.openremote.org" ><img src="image/global.logo.png" /></a>
	            <p class="title">You have been invited to access OpenRemote Designer</p>
               <c:if test="${isChecked ne null and not isChecked}">
                  <a class="register_btn" href="register.jsp">Create a New Account</a>
               </c:if>
               <c:if test="${isChecked ne null and isChecked}">
	            <div style="padding-left:70px">
		            <p>If you already have an account, you can <a href="login.jsp">login here</a>.</p>
                  <input type="hidden" name="uid" value="${uid}">
		            <p class="input"><b class="form_label">Desired username</b><input id="username" style="width:150px" type="text" name="username" value="${username}"></p>
		            <c:if test="${success ne null and not success}">
		                <p class="fail"><b>${username}</b> is not available, choose another.</p>
	                </c:if>
		            <c:if test="${username_invalid ne null}">
		                <p class="fail">Sorry, only letters, numbers and periods are allowed.</p>
	                </c:if>
		            <c:if test="${username_length ne null}">
		                <p class="fail">Sorry, your username must be between 4 and 30 characters long.</p>
	                </c:if>
	                <c:if test="${username_blank ne null}">
		                <p class="fail">Required field cannot be left blank.</p>
	                </c:if>
		            <p class="input"><b class="form_label">Choose password</b><input id="password" style="width:150px" type="password" name="password" value="${password}"></p>
		            <c:if test="${password_error ne null}">
		                <p class="fail">Passwords do not match.</p>
	                </c:if>
	                <c:if test="${password_blank ne null}">
		                <p class="fail">Required field cannot be left blank.</p>
	                </c:if>
	                <c:if test="${password_length ne null}">
		                <p class="fail">Password must be between 6 and 16 characters long.</p>
	                </c:if>
		            <p class="input"><b class="form_label">Re-type password</b><input id="r_password" style="width:150px" type="password" name="r_password" value="${r_password}"></p>
	                <c:if test="${r_password_blank ne null}">
		                <p class="fail">Required field cannot be left blank.</p>
	                </c:if>
	                <p class="input"><b class="form_label">Email</b><input id="email" style="width:150px" type="text" name="email" value="${email}"></p>
	                <c:if test="${email_blank ne null}">
		                <p class="fail">Required field cannot be left blank.</p>
	                </c:if>
	                <c:if test="${email_invalid ne null}">
		                <p class="fail">Invalid email format.</p>
	                </c:if>
	                <p class="input"><b class="form_label">Type code below</b><input name="code" style="width:150px" /> </p>
	                <c:if test="${code_dismatch ne null}">
		                <p class="fail">The code you entered didn't match the verification.</p>
	                </c:if>
	                <p class="input"><img title="click to try a new code" src="captchaImg" onclick="refresh(this);" style="cursor:pointer" alt="loading code..."/> </p>
	                <script type="text/javascript">
	                 function refresh(that) {
	                	 that.setAttribute("src","captchaImg?" + Math.floor(Math.random()*9999999));
	                 }
	                </script>
					<div><input class="register_submit" type="submit" value="Create my account"></div>
	            </div>
              </c:if>                       
		        <p class="copyright">Copyright &copy; 2008-<fmt:formatDate value="${now}"pattern="yyyy" />
		        <a href="http://www.openremote.org">OpenRemote</a>.</p>            
			</div>
		</div>
	</form>
</div>
</body>
</html>