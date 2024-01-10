<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Administration page</title>
  <script type="text/javascript" src="resources/js/jquery-3.6.0.min.js"></script>
  <script>
      var w = Number(window.innerWidth);
      var h = Number(window.innerHeight);
      if (h>w) {
        $('head').append('<link rel="stylesheet" type="text/css" href="resources/css/mobileStyle.css">');
      } else {
        $('head').append('<link rel="stylesheet" type="text/css" href="resources/css/style.css">');
      }
  </script>
</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="logout">Выйти</a>
        </div>
        <hr>
        <h1>АДМИНИСТРИРОВАНИЕ СИСТЕМЫ</h1>
        <br>
        <h2>Выбор операции</h2>
        <h5><a href="admin/edit-company">Редактировать структуру предприятия</a></h5>
        <h5><a href="control/edit-time-standard">Редактирование сроков доставки</a></h5>
        <h5><a href="admin/event-log">Просмотр сохраненных событий</a></h5>
        <h5><a href="admin/alarm-groups">Редактирование групп оповещения</a></h5>
        <h5><a href="admin/add-user">Добавить пользователя</a></h5>
        <h5><a href="admin/edit-rights">Изменение прав пользователя</a></h5>
        <h5><a href="admin/reset-password">Сброс пароля пользователя</a></h5>
        <h5><a href="admin/edit-user">Редактирование (+ удаление или восстановление) пользователя</a></h5>
        <h5><a href="admin/info-users">Информация о пользователях</a></h5>
        <br>
        <a href="work-starter">Главная</a>


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



