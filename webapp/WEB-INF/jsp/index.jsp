<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE HTML>
<html>
<head>
  <title>Главная</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <script>
      var link = document.createElement("link");
      link.type = "text/css";
      link.rel = "stylesheet";
      var w = Number(window.innerWidth);
      var h = Number(window.innerHeight);
      if (h>w) {
        link.href="${pageContext.servletContext.contextPath}/resources/css/mobileStyle.css";
        $('head').append('<meta name="viewport" content="width=device-width, initial-scale=1.0">');
      } else {
        link.href="${pageContext.servletContext.contextPath}/resources/css/style.css";
      }
      document.getElementByTagName("head")[0].appendChild(link);
  </script>

</head>
<body>
    <section>
    <div class="container">
        <h1>ЖУРНАЛЫ УЧЕТА ТЕРМОКОНТЕЙНЕРОВ</h1>
        <h4 style="margin-left: 10%;">${pageContext.request.userPrincipal.name}</h4>
        <sec:authorize access="!isAuthenticated()">
            <p>Вход только для авторизованных пользователей</p>
            <h5><a href="login">Войти</a></h5>
        </sec:authorize>
        <sec:authorize access="isAuthenticated()">
            <h5><a href="work-starter">На главную</a></h5>
            <h5><a href="change-password">Сменить пароль</a></h5>
            <h5><a href="logout">Выйти</a></h5>
        </sec:authorize>
        <sec:authorize access="hasRole('ADMIN')">
            <h4><a href="admin">Редактирование (только админ)</a></h4>
        </sec:authorize>
    </div>
    </section>
</body>
</html>


