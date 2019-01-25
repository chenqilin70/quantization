<%--
  Created by IntelliJ IDEA.
  User: aierxuan
  Date: 2019-01-25
  Time: 16:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:forEach items="${requestScope.funds}" var="f">
    <li><a href="${requestScope.contextPath}/">${f.jjqc} ( ${f.fundcode} )</a></li>
</c:forEach>

