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
<title>Reset password - OpenRemote Designer</title>
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
    .reset_submit{
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
        text-align:center;
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
	<form method="POST" action="account.htm?method=changePassword">
		<div class="inner-boundary">
			  <div class="inner-border">
	            <a href="http://www.openremote.org" ><img src="image/global.logo.png" /></a>
	            <p class="title">Reset password of '${username}'</p>
               <c:if test="${hasReset eq null}">
                  <p class="pass">You can not reset password.</p>
               </c:if>
               <c:if test="${hasReset ne null and hasReset}">
                  <p class="pass">You have reset password successfully, please <a href="login.jsp">login here</a>.</p>
               </c:if>
               <c:if test="${hasReset ne null and not hasReset}">
		            <div style="padding-left:70px">
			            
			            <p class="input"><b class="form_label">New password</b>
                        <input id="password" style="width:150px" type="password" name="new_password" value="${password}">
                     </p>
			            <c:if test="${password_error ne null}">
			                <p class="fail">Passwords do not match.</p>
		                </c:if>
		                <c:if test="${password_blank ne null}">
			                <p class="fail">Required field cannot be left blank.</p>
		                </c:if>
		                <c:if test="${password_length ne null}">
			                <p class="fail">Password must be between 6 and 16 characters long.</p>
		                </c:if>
                      <input name="uid" type="hidden" value="${uid}">
                      <input name="aid" type="hidden" value="${aid}">
                      <input name="reset_username" type="hidden" value="${username}">
			            <p class="input"><b class="form_label">Re-type password</b><input id="r_password" style="width:150px" type="password" name="r_password" value="${r_password}"></p>
		                <c:if test="${r_password_blank ne null}">
			                <p class="fail">Required field cannot be left blank.</p>
		                </c:if>
	
						    <div><input class="reset_submit" type="submit" value="Reset password"></div>
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