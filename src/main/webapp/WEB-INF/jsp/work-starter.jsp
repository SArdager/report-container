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
                <div class="color_text" id="dep_name"> ${department.departmentName},  ${department.branch.branchName}</div>
            </div>
            <div class="title_row">
                <div class="title_name">Права пользователя</div>
                <div id="userRights" class="color_text">${userRights.rights}</div>
            </div>
        </p>
        <h2>Выбор операции</h2>
        <h5><a href="user/change-department">Поменять объект</a></h5>
        <div id="container_field" style="display: block">
            <span style="margin-right: 20px; font-size: 0.85em;">Конечный пункт маршрута - </span><input type="checkbox" id="department_checkbox"/>
            <h5><a href="user/check-between" id="between_department" style="display: block">Промежуточный объект регистрации</a></h5>
            <div id="check_department" style="display: none">
                <h5><a href="user/check-in">Приемка термоконтейнера</a></h5>
                <h5><a href="user/check-out">Отгрузка термоконтейнера</a></h5>
            </div>
        </div>
        <br>
        <div id="parcel_field" style="display: none">
            <h5><a href="user/create-parcel">Создать и отправить почтовое отправление</a></h5>
            <h5><a href="user/check-parcel">Приемка и отслеживание посылки</a></h5>
        </div>
        <h5><a href="user/check-journal" id="journal_line" style="display: block">Журнал движения термоконтейнеров</a></h5>
        <h5><a href="user/check-container" id="account_line" style="display: none">Учет термоконтейнеров</a></h5>
        <h5><a href="control/start-page" id="control_line" style="display: none">Отчеты по термоконтейнерам и посылкам</a></h5>
        <h5><a href="control/edit-time-standard" id="time_line" style="display: none">Установить срок доставки</a></h5>
        <h5><a href="control/add-rights" id="rights_line" style="display: none">Изменить права</a></h5>
        <br>
        <sec:authorize access="hasRole('ADMIN')">
            <h5><a href="admin">Администрирование системы</a></h5>
        </sec:authorize>

     </div>
  </section>

     <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            let name = "${user.userFirstname}";
            document.getElementById("user_name").textContent = name.substring(0, 1) + ". ${user.userSurname}";
            let depName = $('#dep_name').html();
            let rights = $('#userRights').html();
            if(rights.indexOf("ПОСЫЛОК")>0){
                $('#parcel_field').css("display", "block");
                $('#journal_line').css("display", "none");
                if(depName.indexOf("Склад")<0){
                    $('#container_field').css("display", "none");
                } else {
                    $('#check_department').css("display", "block");
                    $('#between_department').css("display", "none");
                }
            }
            if(rights.indexOf("УЧЕТ")>-1){
                $('#account_line').css("display", "block");
                $('#control_line').css("display", "none");
                $('#journal_line').css("display", "none");
                $('#container_field').css("display", "none");
            }
            if(rights.indexOf("СМОТР")>0){
                $('#control_line').css("display", "block");
                $('#container_field').css("display", "none");
            }
            if(rights.indexOf("СРОК")>0){
                $('#time_line').css("display", "block");
            }
            if(rights.indexOf("ПРАВ")>0){
                $('#rights_line').css("display", "block");
            }
            if(rights.indexOf("ЛАБОР")>0){
                $('#control_line').css("display", "block");
                $('#time_line').css("display", "block");
                $('#rights_line').css("display", "block");
                $('#parcel_field').css("display", "block");
            }
            $('#department_checkbox').on('click', function(){
                if($('#department_checkbox').is(':checked')==true){
                    $('#check_department').css("display", "block");
                    $('#between_department').css("display", "none");
                } else {
                    $('#check_department').css("display", "none");
                    $('#between_department').css("display", "block");
                }
            });
            if(depName.indexOf("боратория")>0){
                $('#department_checkbox').trigger("click");
            }
            if(depName.indexOf("Склад")>-1){
                $('#department_checkbox').trigger("click");
                $('#department_checkbox').css("display", "none");
            }

       });
     </script>

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>