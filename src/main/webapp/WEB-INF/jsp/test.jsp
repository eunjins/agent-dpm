<%--
  Created by IntelliJ IDEA.
  User: ghdgh
  Date: 2021-11-23
  Time: 오후 7:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <form action="/script/distribute" method="POST" enctype="multipart/form-data">
        <input type="file" name="file"/>
        <input type="submit" name="전송"/>
    </form>
</body>
</html>
