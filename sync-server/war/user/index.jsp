<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>jsonengine</title>
</head>
<body>
Welcome!<br />
${user.email}<br />
${displayName}&nbsp;<a href="${f:url("displayName")}" />Edit</a><br />
</body>
</html>