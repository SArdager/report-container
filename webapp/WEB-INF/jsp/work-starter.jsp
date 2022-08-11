<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Working page</title>
    <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <strong style="margin-top: 4px; margin-right: 20px">Пользователь: ${user.userFirstname} ${user.userSurname}</strong>
            <a style="margin-top: 4px;" href="/logout">Выйти</a>
        </div>
        <hr>
        <h1>ЖУРНАЛЫ УЧЕТА ТЕРМОКОНТЕЙНЕРОВ</h1>
        <br>
        <p>
            <div class="title_row">
                <div class="title_name">Наименование объекта:</div>
                <div class="color_text"> ${department.departmentName},  ${department.branch.branchName}</div>
            </div>
            <div class="title_row">
                <div class="title_name">Права пользователя</div>
                <div id="userRights" class="color_text">${userRights.rights}</div>
            </div>
        </p>
        <br>
        <h2>Выбор операции</h2>
        <h4><a href="/user/change-department">Поменять объект</a></h4>
        <h4><a href="/user/check-between">Промежуточный объект регистрации</a></h4>
        <h4><a href="/user/check-in">Приемка термоконтейнера</a></h4>
        <h4><a href="/user/check-out">Отгрузка термоконтейнера</a></h4>
        <h4><a href="/user/check-journal">Журнал движения термоконтейнеров</a></h4>
        <br>
        <h4><a href="/user/check-container" id="account_line" style="display: none">Учет термоконтейнеров</a></h4>
        <br>
        <h4><a href="/control/start-page" id="control_line" style="display: none">Отчеты по термоконтейнерам</a></h4>
        <br>
        <sec:authorize access="hasRole('ADMIN')">
            <h4><a href="/admin">Администрирование системы</a></h4>
        </sec:authorize>

     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            var rights = $('#userRights').html();
            var account_line = document.getElementById("account_line");
            if(rights.indexOf("УЧЕТ")>-1){
                account_line.style.display = "block";
            }
            if(rights.indexOf("СМОТР")>0){
                control_line.style.display = "block";
            }
       });
    </script>

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>