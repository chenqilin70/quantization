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
    <c:if test="${f.fundtype == '股票型' || f.fundtype == '股票指数'}">
        <li ><a href="#" fundcode="${f.fundcode}" fundtype="${f.fundtype}" class="searchItem" jjqc="${f.jjqc}">${f.jjqc} ( ${f.fundcode} )</a></li>
    </c:if>
    <c:if test="${f.fundtype != '股票型' && f.fundtype != '股票指数'}">
        <li  class="disabled"><a href="#" fundcode="${f.fundcode}" fundtype="${f.fundtype}"  jjqc="${f.jjqc}">${f.jjqc} ( ${f.fundcode} )</a></li>
    </c:if>
</c:forEach>


