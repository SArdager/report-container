<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Route control page</title>
    <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
    <script>
        var w = Number(window.innerWidth);
        var h = Number(window.innerHeight);
        if (h>w) {
          $('head').append('<link rel="stylesheet" type="text/css" href="../resources/css/mobileStyle.css">');
        } else {
          $('head').append('<link rel="stylesheet" type="text/css" href="../resources/css/style.css">');
        }
    </script>
    <script type="text/javascript" src="../resources/js/controlHelper.js"></script>
    <script type="text/javascript" src="../resources/js/showNote.js"></script>
</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <strong style="margin-top: 4px; margin-right: 20px">Пользователь: ${user.userFirstname} ${user.userSurname}</strong>
            <a style="margin-top: 4px;" href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Отчет по использованию термоконтейнера</h1>
        <br>
        <a href="start-page">Вернуться</a>
        <br>
        <h2><div id="result_line"></div></h2>
        <div class="title_row">
            <span class="date_line">Вывести по термоконтейнеру №:</span>
            <select id="select_container" style="width: 200px; margin-left: 20px;">
                <c:forEach var="container" items="${containers}">
                    <option value=${container.id}>${container.containerNumber}</option>
                </c:forEach>
            </select>
        </div>
        <table style="border: none; width: 100%">
            <caption><strong>Использование термоконтейнера</strong></caption>
            <tr>
                <td class="table_title" style="text-decoration: underline;">Термоконтейнер</td>
                <td id="routeContainerNumber"></td>
            </tr>
            <tr>
                <td class="table_title" style="text-decoration: underline;">Характеристика</td>
                <td id="containerValue"></td>
            </tr>
            <tr>
                <td class="table_title" style="text-decoration: underline;">Начало эксплуатации</td>
                <td id="registrationDate"></td>
            </tr>
            <tr>
                <td class="table_title" style="text-decoration: underline;">Место нахождения</td>
                <td id="containerDepartment"></td>
            </tr>
        </table>
        <form id="export_route" action="route/report-exportExcel" method="post">
            <div class="title_row" style="margin-left: 40px">
                <span class="date_line">Вывести за период:</span>
                <input type="date" id="startDate" name="startDate"/>
                <input type="date" id="endDate" name="endDate"/>
                <span class="text_line">по</span>
                <input type="number" id="routePageSize" min="2" value="10"/>
                <span class="text_line">записей</span>
                <input type="hidden" id="totalRouteNotes" value="0"/>
                <input type="hidden" id="container_id" name="containerId" value="0" />
            </div>
        </form>
        <div class="title_row" style="justify-content: space-between;">
            <div class="title_row" style="width: 40%; justify-content: space-between; margin-right: 0.5em">
                <span id="reload_route" class ="reload_line">Показать</span>
                <img src="../resources/images/export_excel_48.png" id="btn_export_route" width="24" height="24" alt="">
            </div>
            <div id="pages_title" ></div>
        </div>
        <div class = "scroll_table">
           <table>
             <caption><strong>Записи по перемещению термоконтейнера</strong></caption>
             <thead>
               <tr>
                 <th>Номер</th>
                 <th>Время отправки</th>
                 <th>Отправитель</th>
                 <th>Время прибытия</th>
                 <th>Получатель</th>
                 <th>Статус</th>
                 <th>Запись отправителя</th>
                 <th>Запись получателя</th>
               </tr>
             </thead>
             <tbody id = "route_table_body">
             </tbody>
           </table>
        </div>
        <br>
        <div id="show_route_points" style="display: none">
            <table style="border: none; width: 100%">
                <caption><strong>Записи по промежуточным объектам</strong></caption>
                <tr>
                    <td class="table_title">Рег.№ отправления</td>
                    <td id="containerNoteId"></td>
                </tr>
            </table>
            <table border ="1">
                <thead>
                    <th>Наименование объекта</th>
                    <th>Время регистрации</th>
                    <th>Исполнитель</th>
                    <th>Запись исполнителя</th>
                </thead>
                <tbody class="table_body" id="route_points_body">
                </tbody>
            </table>
        </div>
     </div>
    <div class="buffer" style = "height: 5em;"></div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
            $('#select_container').trigger("change");
            var resultLineValue;
            window.addEventListener("click", function(){
                resultLineValue = $('#result_line').text();
                if(resultLineValue.length>0){
                    $('#result_line').text("");
                }
            });
       });
    </script>

</body>
</html>
