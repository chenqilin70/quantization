<%--
  Created by IntelliJ IDEA.
  User: aierxuan
  Date: 2019-01-24
  Time: 18:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <title>Title</title>
    <link rel="stylesheet"  type="text/css"  href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.css" >
    <link rel="stylesheet"  type="text/css"  href="<%=request.getContextPath()%>/css/index.css" >
    <script src="<%=request.getContextPath()%>/js/jquery-3.3.1.min.js"  type="text/javascript"  ></script>
    <script src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.js"  type="text/javascript"  ></script>
    <script src="<%=request.getContextPath()%>/js/echarts.min.js"  type="text/javascript"  ></script>
    <script src="<%=request.getContextPath()%>/js/index.js"  type="text/javascript"  ></script>
    <meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
</head>
<body class="container-fluid">
<div id="contextPath"><%=request.getContextPath()%></div>
    <div class="row">
        <div id="searchDiv" class="col-lg-offset-3 col-lg-6 col-md-offset-2  col-md-8  col-sm-offset-2 col-sm-8 col-xs-offset-1  col-xs-10">
            <div class="input-group dropdown">
                <!--data-toggle="dropdown"-->
                <span class="input-group-addon iconfont" id="basic-addon3">&#xe632;</span>
                <input type="text" class="form-control dropdown-toggle"   id="searchInput" placeholder="">
                <%--<span class="input-group-btn">
                    <button class="btn btn-default" id="searchButton" type="button">Go!</button>
                </span>--%>
                <ul class="dropdown-menu"  aria-labelledby="dropdownMenu1"  id="searchMenu">
                </ul>
            </div>
        </div>
    </div>

    <%--<c:forEach var="i"   items="<%=new Integer[]{0,1,3,6,12}%>"   >--%>
        <div class="row">
            <div id="chartDiv" class="chartDiv col-lg-offset-3 col-lg-6 col-md-offset-2  col-md-8  col-sm-offset-2 col-sm-8 col-xs-offset-1  col-xs-10">

            </div>
            <div  class="chartNoDataDiv col-lg-offset-3 col-lg-6 col-md-offset-2  col-md-8  col-sm-offset-2 col-sm-8 col-xs-offset-1  col-xs-10">
                暂无数据
            </div>
        </div>
    <%--</c:forEach>--%>
</body>
</html>
