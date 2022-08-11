<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Delay control page</title>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/controlHelper.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/showNote.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/selectDepartment.js"></script>
</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <strong style="margin-top: 4px; margin-right: 20px">Пользователь: ${user.userFirstname} ${user.userSurname}</strong>
            <a style="margin-top: 4px;" href="/logout">Выйти</a>
        </div>
        <hr>
        <h1>Отчет по задержкам транспортировки</h1>
        <br>
        <a href="/control/start-page">Вернуться</a>
        <br>
        <h2><div id="result_line"></div></h2>
        <p>
            <div class="title_row">
                <div class="title_name">Наименование объекта:</div>
                <div class="color_text"> ${department.departmentName},  ${department.branch.branchName}</div>
                <input type="hidden" id="department_id" value="${department.id}" />
            </div>
            <div class="title_row">
                <div class="title_name">Права пользователя</div>
                <div class="color_text">${userRights.rights}</div>
            </div>
        </p>
        <div class="title_row" style="margin-left: 40px">
            <div style="padding-top: 8px">Вывести за период:</div>
            <input type="date" id="startDate" style="width: 100px; margin-left: 20px" />
            <input type="date" id="endDate" style="width: 100px; margin-left: 20px" />
            <div style="font-size: 0.8em; padding-top: 10px; margin-left: 20px">по</div>
            <input type="number" id="delayPageSize" min="2" style="width: 3em; margin-left: 6px" value="10"/>
            <div style="font-size: 0.8em; padding-top: 10px; margin-left: 6px">записей</div>
            <input type="hidden" id="totalDelayNotes" value="0"/>
        </div>
        <div class="title_row">
            <span style="margin-left: 40px; padding-top: 8px">Вывести по объекту:</span>
            <div class="checkbox_margin" id="chose_checkbox" style="margin-left: 14px; display: none">
                <input type="checkbox" id="department_checkbox" style="margin: 0px;"/>
                <span style="margin-left: 4px;">- все объекты</span>
            </div>
            <span id="chose_branch" style="display: none">
                <select id="select_branch" style="width: 200px; margin-left: 20px;">
                    <c:forEach var="branch" items="${branches}">
                        <option value=${branch.id}>${branch.branchName}</option>
                    </c:forEach>
                </select>
            </span>
            <select id="select_department" style="width: 200px; margin-left: 14px;">
            </select>
        </div>
        <div class="title_row" style="justify-content: space-between;">
            <div class="title_row" style="width: 50%;">
                <div id="reload_delay" style ="color: blue; text-decoration: underline; margin-left: 20px; padding-top: 8px">Показать опоздание более</div>
                <input type="number" id="delay_limit" min="0" style="margin-left: 8px; width: 4em" value="0"/>
                <span style="padding-top: 8px; margin-left: 8px;">часов</span>
            </div>
            <div id="pages_delay_title" style="margin-right: 20px"></div>
        </div>
        <div class = "scroll_table">
           <table>
             <thead>
               <tr>
                 <th>№ регистрации</th>
                 <th>№ контейнера</th>
                 <th>Отправитель</th>
                 <th>Время отправки</th>
                 <th>Получатель</th>
                 <th>Время прибытия</th>
                 <th>Опоздание</th>
                 <th>Запись отправителя</th>
                 <th>Запись получателя</th>
               </tr>
             </thead>
           </table>
           <div class = "scroll_table_body">
             <table>
                <tbody id = "delay_table_body">
                </tbody>
             </table>
           </div>
        </div>
        <br>
        <div id="show_points" style="display: none">
            <table style="border: none; width: 100%">
                <caption><strong>Записи по промежуточным объектам</strong></caption>
                <tr>
                    <td class="table_title">Номер отправления</td>
                    <td id="containerNoteId"></td>
                </tr>
                <tr>
                    <td class="table_title" style="text-decoration: underline;">Термоконтейнер</td>
                    <td id="containerNumber"></td>
                </tr>
            </table>
            <table border ="1">
                <thead>
                    <th>Наименование объекта</th>
                    <th>Время регистрации</th>
                    <th>Исполнитель</th>
                    <th>Запись исполнителя</th>
                </thead>
                <tbody class="table_body" id="points_body">
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
            var chose_checkbox = document.getElementById("chose_checkbox");
            var chose_branch = document.getElementById("chose_branch");
            if(${department.id}==1){
                chose_checkbox.style.display = "block";
                chose_branch.style.display = "block";
                $('#select_branch').trigger("change");
            } else {
                $('#select_branch').val(${department.branch.id});
                $('#select_branch').trigger("change");
            }

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
