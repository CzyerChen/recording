<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>表单提交结果</title>
</head>
<body>

<c:choose>
    <c:when test="${name != null}">
        姓名是：${name}
    </c:when>
</c:choose>
</body>
</html>
