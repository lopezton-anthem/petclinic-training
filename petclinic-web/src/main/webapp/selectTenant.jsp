<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.antheminc.oss.nimbus.domain.model.state.multitenancy.Tenant"%>
<html>

<head>
<title>Tenant Select</title>

<style>
body {
font-family: sans-serif;
}

.footer {
position: absolute;
bottom: 0;
right: 0;
left: 0;
min-height: 120px;
background-color: #333;
color: #fff;
font-size: 12px;
}

.loginBox {
position: absolute;
top: 165px;
left: 35%;
width: 250px;
padding: 40px;
border: 1px solid #cecece;
}

.header {
height: 95px;
left: 0;
right: 0;
top: 0;
background-color: #006bc2;
padding-top: .9rem;
position: fixed;
}

.header .logo {
display: inline-block;
width: 150px;
margin-left: 10px;
margin-top: 0;
vertical-align: top;
}
</style>
</head>

<body>
<div class="header">

</div>
<div class="loginBox">
<p>Select the tenant</p>
	<%
		Set<Tenant> tenants = (Set<Tenant>) request.getAttribute("tenants");
		List<Tenant> sortedTenants = new ArrayList<>(tenants);
		Collections.sort(sortedTenants, new Comparator<Tenant>() {
			@Override
			public int compare(Tenant t1, Tenant t2) {
				return t1.getDescription().compareTo(t2.getDescription());
			}
		});
	%>
	
	<ul>
	  <% for (Tenant tenant : tenants) { %>
		<li><a href='/petclinic/chooseTenant?tenantId=<%= tenant.getId() %>'><%= tenant.getDescription() %></a></li>
	  <% } %>
	</ul>
</div>
<div class="footer" id="footer">
</div>
</body>

</html>