<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>jsonengine</title>
</head>
<body>
Input your display name.
<ul>
<c:forEach var="e" items="${f:errors()}">
<li>${f:h(e)}</li>
</c:forEach>
</ul>
<form action="${f:url("updateDisplayName")}" method="POST">
<input type="text" name="displayName" value="${displayName}" /><br />
<input type="submit" /><a href="${f:url("index")}">index</a>
</form>
</body>
</html>