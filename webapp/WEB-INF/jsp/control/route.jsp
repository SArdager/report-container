<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Route control page</title>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/controlHelper.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/showNote.js"></script>
</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <strong style="margin-top: 4px; margin-right: 20px">Пользователь: ${user.userFirstname} ${user.userSurname}</strong>
            <a style="margin-top: 4px;" href="/logout">Выйти</a>
        </div>
        <hr>
        <h1>Отчет по использованию термоконтейнера</h1>
        <br>
        <a href="/work-starter">Вернуться</a>
        <br>
        <h2><div id="result_line"></div></h2>
        <div class="title_row">
            <span style="margin-left: 40px; padding-top: 8px">Вывести по термоконтейнеру №:</span>
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
        <div class="title_row" style="margin-left: 40px">
            <div style="padding-top: 8px">Вывести за период:</div>
            <input type="date" id="startDate" style="width: 100px; margin-left: 20px" />
            <input type="date" id="endDate" style="width: 100px; margin-left: 20px" />
            <div style="font-size: 0.8em; padding-top: 10px; margin-left: 20px">по</div>
            <input type="number" id="routePageSize" min="2" style="width: 3em; margin-left: 6px" value="10"/>
            <div style="font-size: 0.8em; padding-top: 10px; margin-left: 6px">записей</div>
            <input type="hidden" id="totalRouteNotes" value="0"/>
        </div>
        <div class="title_row" style="justify-content: space-between;">
            <div id="reload_route" style ="color: blue; text-decoration: underline; margin-left: 20px; padding-top: 8px">Показать</div>
            <div id="pages_route_title" style="margin-right: 20px"></div>
        </div>
        <div class = "scroll_table">
           <table>
            <caption><strong>Записи по перемещению термоконтейнера</strong></caption>
             <thead>
               <tr>
                 <th>№ регистрации</th>
                 <th>Время отправки</th>
                 <th>Отправитель</th>
                 <th>Время прибытия</th>
                 <th>Получатель</th>
                 <th>Статус</th>
                 <th>Запись отправителя</th>
                 <th>Запись получателя</th>
               </tr>
             </thead>
           </table>
           <div class = "scroll_table_body">
             <table>
                <tbody id = "route_table_body">
                </tbody>
             </table>
           </div>
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
