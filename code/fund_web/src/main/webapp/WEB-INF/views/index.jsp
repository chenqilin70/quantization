<%--
  Created by IntelliJ IDEA.
  User: aierxuan
  Date: 2019-01-24
  Time: 18:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet"  type="text/css"  href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.css" >

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.js"  type="text/javascript"  ></script>
</head>
<body class="container-fluid">
    <div class="row">

        <div class="col-lg-6">
            <div class="input-group">
                <input type="text" class="form-control" placeholder="Search for...">
                <span class="input-group-btn">
            <button class="btn btn-default" type="button">Go!</button>
          </span>
            </div><!-- /input-group -->
        </div><!-- /.col-lg-6 -->
    </div><!-- /.row -->
</body>
</html>
