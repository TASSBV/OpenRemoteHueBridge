<%@ page import="org.springframework.security.context.SecurityContextHolder" %>
<%@ page import="org.springframework.security.Authentication" %>
<%@ page import="org.springframework.security.ui.AccessDeniedHandlerImpl" %>

<h1>Sorry, access is denied</h1>
<p>

<%		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) { %>
			You have not got the authority to access the resource<BR><BR>
			<a href="./j_security_logout">Logout</a>
<%      } %>
