<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>HOME PAGE</title>
</head>
<body>
<p>HOME PAGE </p>
<form action="${pageContext.request.contextPath}/test/form" method="post">
    <input name="name" type="text">
    <input type="submit" value="提交">
</form>
</body>
</html>