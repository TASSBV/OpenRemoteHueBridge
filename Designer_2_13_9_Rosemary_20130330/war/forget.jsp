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
<title>Forget Password - OpenRemote Designer</title>
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
    .send_submit{
        width:100px;
        margin-left: 80px;
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
        text-align: center;
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
		width: 240px;
	}
	a, a:hover {
		color:#225E8A;
		text-decoration:none;
	}
	.incorrect {
		color:red;
		text-align: left;
	}
</style>
</head>
<body>

<div class="center-form">
	<form method="POST" action="account.htm?method=forgetPassword">
		<div class="inner-boundary">
		  <div class="inner-border">
            <a href="http://www.openremote.org" ><img src="image/global.logo.png" /></a>
            <p class="title">Forget your password?</p>
            <c:if test="${isUserAvailable ne null and not isUserAvailable}">
                <p class="fail">Invalid username, get password failed.</p>
            </c:if>
            
            <c:if test="${needReset ne null}">
                <p class="pass">We have sent you an email to <b>${email}</b>,
                 please follow the instructions in the email to reset your password.</p>
            </c:if>

            <c:if test="${needReset eq null}">
	            <div style="padding-left:110px">
		            <p class="input"><b class="form_label">Username</b>
		            <input id="username" style="width:150px" type="text" name="username" value="${username}"></p>
                  <c:if test="${username_blank ne null}">
                      <p class="fail">Username cannot be left blank.</p>
                   </c:if>
		            <div>
                     <input class="send_submit" type="submit" value="Submit">
                  </div>
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