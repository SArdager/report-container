<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Working page</title>
  <script type="text/javascript" src="resources/js/jquery-3.6.0.min.js"></script>
  <script>
      var w = Number(window.innerWidth);
      var h = Number(window.innerHeight);
      if (h>w) {
        $('head').append('<link rel="stylesheet" type="text/css" href="resources/css/mobileStyle.css">');
        $('head').append('<meta name="viewport" content="width=device-width, initial-scale=1.0">');
      } else {
        $('head').append('<link rel="stylesheet" type="text/css" href="resources/css/style.css">');
      }
  </script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <span id="user_name"></span>
            <a href="logout">Выйти</a>
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
        <h2>Выбор операции</h2>
        <h4><a href="user/change-department">Поменять объект</a></h4>
        <div id="container_field" style="display: block">
            <h4><a href="user/check-between">Промежуточный объект регистрации</a></h4>
            <h4><a href="user/check-in">Приемка термоконтейнера</a></h4>
            <h4><a href="user/check-out">Отгрузка термоконтейнера</a></h4>
            <h4><a href="user/check-journal">Журнал движения термоконтейнеров</a></h4>
        </div>
        <h4><a href="user/check-courier" id="courier_line" style="display: none">Учет термоконтейнеров</a></h4>
        <div id="parcel_field" style="display: none">
            <h4><a href="user/create-parcel">Создать почтовое отправление</a></h4>
            <h4><a href="user/check-parcel">Отслеживание посылки</a></h4>
        </div>
        <br>
        <h4><a href="user/check-container" id="account_line" style="display: none">Учет термоконтейнеров</a></h4>
        <h4><a href="control/start-page" id="control_line" style="display: none">Отчеты по термоконтейнерам и посылкам</a></h4>
        <h4><a href="control/edit-time-standard" id="time_line" style="display: none">Установить срок доставки</a></h4>
        <h4><a href="control/add-rights" id="rights_line" style="display: none">Изменить права</a></h4>
        <br>
        <sec:authorize access="hasRole('ADMIN')">
            <h4><a href="admin">Администрирование системы</a></h4>
        </sec:authorize>

     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            let name = "${user.userFirstname}";
            document.getElementById("user_name").textContent = name.substring(0, 1) + ". ${user.userSurname}";
            let rights = $('#userRights').html();

            if(rights.indexOf("ПОСЫЛОК")>0){
                document.getElementById("parcel_field").style.display = "block";
                document.getElementById("container_field").style.display = "none";
            }
            if(rights.indexOf("УЧЕТ")>-1){
                document.getElementById("account_line").style.display = "block";
            }
            if(rights.indexOf("ДОСТАВКА")>-1){
                document.getElementById("courier_line").style.display = "block";
                document.getElementById("container_field").style.display = "none";
            }
            if(rights.indexOf("СМОТР")>0){
                document.getElementById("control_line").style.display = "block";
            }
            if(rights.indexOf("СРОК")>0){
                document.getElementById("time_line").style.display = "block";
            }
            if(rights.indexOf("ПРАВ")>0){
                document.getElementById("rights_line").style.display = "block";
            }
            if(rights.indexOf("ЛАБОР")>0){
                document.getElementById("control_line").style.display = "block";
                document.getElementById("time_line").style.display = "block";
                document.getElementById("parcel_field").style.display = "block";
            }
       });
    </script>

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>