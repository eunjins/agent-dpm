<%--
  Created by IntelliJ IDEA.
  User: ghdgh
  Date: 2021-11-23
  Time: 오후 7:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Test</title>
</head>
<body>
    <form action="/script/distribute" method="POST" enctype="multipart/form-data">
        <input type="file" name="scriptFile"/>
        <input type="submit" name="전송"/>
        <input type="hidden" name="encryptId" value="00325-96018-79885-AAOEM" />
    </form>
</body>
</html>
