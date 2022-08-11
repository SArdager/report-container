<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE HTML>
<html>
<head>
  <title>Главная</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
</head>
<body>
    <section>
    <div class="container">
        <h1>ЖУРНАЛЫ УЧЕТА ТЕРМОКОНТЕЙНЕРОВ</h1>
        <h3 style="margin-left: 40px;">${pageContext.request.userPrincipal.name}</h3>
        <sec:authorize access="!isAuthenticated()">
            <p>Вход только для авторизованных пользователей</p>
            <h4><a href="/login">Войти</a></h4>
        </sec:authorize>
        <sec:authorize access="isAuthenticated()">
            <h4><a href="/work-starter">На главную</a></h4>
            <h4><a href="/changePassword">Сменить пароль</a></h4>
            <h4><a href="/logout">Выйти</a></h4>
        </sec:authorize>
        <sec:authorize access="hasRole('ADMIN')">
            <h4><a href="/admin">Редактирование (только админ)</a></h4>
        </sec:authorize>
    </div>
    </section>
</body>
</html>


