<%response.setStatus(500); %>
Exception: <%=((Exception)request.getAttribute("exception")).getMessage()%>