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
    <link rel="stylesheet"  type="text/css"  href="<%=request.getContextPath()%>/css/index.css" >
    <script src="<%=request.getContextPath()%>/js/jquery-3.3.1.min.js"  type="text/javascript"  ></script>
    <script src="<%=request.getContextPath()%>/js/index.js"  type="text/javascript"  ></script>
    <script src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.js"  type="text/javascript"  ></script>
    <meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
</head>
<body class="container-fluid">
    <div class="row">
        <div id="searchDiv" class="col-lg-offset-3 col-lg-6 col-md-offset-2  col-md-8  col-sm-offset-2 col-sm-8 col-xs-offset-1  col-xs-10">
            <div class="input-group">
                <input type="text" class="form-control" id="searchInput" placeholder="Search for...">
                <span class="input-group-btn">
                    <button class="btn btn-default" id="searchButton" type="button">Go!</button>
                </span>
            </div>
        </div>
    </div>
</body>
</html>
