<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Route control page</title>
    <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="../resources/js/showNote.js"></script>
    <script>
        var w = Number(window.innerWidth);
        var h = Number(window.innerHeight);
        if (h>w) {
          $('head').append('<link rel="stylesheet" type="text/css" href="../resources/css/mobileStyle.css">');
        } else {
          $('head').append('<link rel="stylesheet" type="text/css" href="../resources/css/style.css">');
        }
    </script>
</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <span id="user_name"></span>
            <a href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Отчет по использованию термоконтейнера</h1>
        <br>
        <a href="start-page">Вернуться</a>
        <br>
        <h3><div id="result_line"></div></h3>
        <table>
            <tr><td class="table_title">№ термоконтейнера</td>
                <td><input type="number" maxlength="8" id="container_number" style="width: 160px;"/>
            </tr>
            <tr><td class="table_title"><span id="container_clean" class="cut_line">Очистить поле номера</span></td>
                <td><select id="select_container" style="width: 160px; display: none;">
                    </select>
                </td>
            </tr>
        </table>
        <table style="border: none; width: 100%">
            <strong>Использование термоконтейнера</strong>
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
            <div class="title_row" style="margin-left: 20px">
                <span class="date_line">Вывести за период:</span>
                <input type="date" id="startDate" name="startDate"/>
                <input type="date" id="endDate" name="endDate"/>
                <span class="text_line">по</span>
                <input type="number" id="pageSize" min="5" step="10" value="30"/>
                <span class="text_line">строк</span>
                <input type="hidden" id="totalNotes" value="0"/>
                <input type="hidden" id="container_id" name="containerId" value="0" />
            </div>
        </form>
        <div class="title_row">
            <div class="title_left">
                <span id="reload_route" class ="reload_line">Показать</span>
                <img src="../resources/images/export_excel_48.png" id="btn_export_route" align = "top" alt="">
            </div>
            <div id="route_pages_title" style="margin-left: 20px;"></div>
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
            let name = "${user.userFirstname}";
            document.getElementById("user_name").textContent = name.substring(0, 1) + ". ${user.userSurname}";
            let resultLineValue;
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
