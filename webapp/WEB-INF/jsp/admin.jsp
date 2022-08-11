<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Administration page</title>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="/logout">Выйти</a>
        </div>
        <hr>
        <h1>АДМИНИСТРИРОВАНИЕ СИСТЕМЫ</h1>
        <br>
        <h2>Выбор операции</h2>
        <h4><a href="/admin/change-company">Редактировать структуру предприятия</a></h4>
        <h4><a href="/admin/edit-values">Редактирование видов проб и термоконтейнеров</a></h4>
        <h4><a href="/admin/edit-time-standards">Редактирование норматива доставки</a></h4>
        <h4><a href="/admin/alarm-groups">Редактирование групп оповещения</a></h4>
        <h4><a href="/admin/add-user">Добавить пользователя</a></h4>
        <h4><a href="/admin/edit-rights">Изменение прав пользователя</a></h4>
        <h4><a href="/admin/reset-password">Сброс пароля пользователя</a></h4>
        <h4><a href="/admin/edit-user">Редактирование пользователя</a></h4>
        <h4><a href="/admin/info-users">Информация о пользователях</a></h4>
        <br>
        <a href="/work-starter">Главная</a>


     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
       });
    </script>

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>



