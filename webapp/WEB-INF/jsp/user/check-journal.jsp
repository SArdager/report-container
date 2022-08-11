<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Termocontainers journal page</title>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/worker.js"></script>
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
        <input type="hidden" id="userId" value="${user.id}" />
        <hr>
        <h1>Журнал движения термоконтейнеров по объекту</h1>
        <br>
        <a href="/work-starter">Вернуться</a>
        <a style="margin-left: 20px;" href="/user/check-between">Регистрация на объекте</a>
        <a style="margin-left: 20px;" href="/user/check-in">Приемка термоконтейнера</a>
        <a style="margin-left: 20px;" href="/user/check-out">Отгрузка термоконтейнера</a>
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
            <input type="number" id="pageSize" min="2" style="width: 3em; margin-left: 6px" value="10"/>
            <div style="font-size: 0.8em; padding-top: 10px; margin-left: 6px">записей</div>
            <input type="hidden" id="totalNotes" value="0"/>
        </div>
        <div id="chose_department" class="title_row" style="margin-left: 40px; display: none">
            <span>Вывести по объекту:</span>
            <input type="checkbox" id="department_checkbox" style="margin-left: 14px; "/>
            <span style="margin-left: 4px; ">- все объекты</span>
            <select id="select_branch" style="width: 200px; margin-left: 20px;">
                <c:forEach var="branch" items="${branches}">
                    <option value=${branch.id}>${branch.branchName}</option>
                </c:forEach>
            </select>
            <select id="select_department" style="width: 200px; margin-left: 20px;">
            </select>
        </div>
        <div class="title_row" style="justify-content: space-between;">
            <div id="reload_journal" style ="color: blue; text-decoration: underline; margin-left: 20px;">Обновить</div>
            <div id="pages_journal_title" style="margin-right: 20px"></div>
        </div>
        <div class = "scroll_table">
           <table>
             <thead>
               <tr>
                 <th>Номер</th>
                 <th>Дата отправки</th>
                 <th>Дата приемки</th>
                 <th>Отправитель</th>
                 <th>Получатель</th>
                 <th>Статус</th>
               </tr>
             </thead>
           </table>
           <div class = "scroll_table_body">
             <table>
                <tbody id = "notes_table_body">
                </tbody>
             </table>
           </div>
        </div>
        <br>
        <div id="show_note" style="display: none">
            <div class="cut_line" id="line_cut_note" >Скрыть данные по термоконтейнеру</div>
            <h3>Данные по перемещению термоконтейнера</h3>
            <input type="hidden" id="outDepartmentId" />
            <input type="hidden" id="toDepartmentId" />
            <input type="hidden" id="outUserId" />
            <input type="hidden" id="toUserId" />
            <input type="hidden" id="passUserId" />
            <table style="border: none; width: 100%">
                <tr>
                    <td class="table_title">Номер отправления</td>
                    <td id="containerNoteId"></td>
                </tr>
                <tr>
                    <td class="table_title" style="text-decoration: underline;">Термоконтейнер</td>
                    <td id="containerNumber"></td>
                </tr>
                <tr>
                    <td class="table_title">Отправитель</td>
                    <td id="outDepartment"></td>
                </tr>
                <tr>
                    <td class="table_title">Дата, время отправки</td>
                    <td id="sendTime"></td>
                </tr>
                <tr>
                    <td class="table_title">Отправил</td>
                    <td id="outUser"></th>
                </tr>
                <tr>
                    <td class="table_title">Запись отправителя</td>
                    <td id="sendNote"></td>
                </tr>
                <tr id="changeNote" style="display: none;">
                    <td class="table_change_title">Изменить запись</td>
                    <td><input type="text" id="inputSendNote"/></td>
                </tr>
                <tr>
                    <td class="table_title">Сумма оплаты</td>
                    <td id="sendPay"></td>
                </tr>
                <tr id="changePay" style="display: none;">
                    <td class="table_change_title">Изменить сумму</td>
                    <td><input type="number" id="inputPay" value="0"/></td>
                </tr>
                <tr>
                    <td class="table_title">Получатель</td>
                    <td id="toDepartment"></td>
                </tr>
                <tr id="changeToBranch" style="display: none;">
                    <td class="table_change_title">Изменить получателя</td>
                    <td><select id="select_change_branch" style="width: 260px;">
                            <c:forEach var="branch" items="${branches}">
                                <option value=${branch.id}>${branch.branchName}</option>
                            </c:forEach>
                        </select></td>
                </tr>
                <tr id="changeToDepartment" style="display: none;">
                    <td class="table_title"></td>
                    <td><select id="select_change_department" style="width: 260px;">
                        </select></td>
                    </td>
                </tr>
                <tr>
                    <td class="table_title">Дата, время приемки</td>
                    <td id="arriveTime"></td>
                </tr>
                <tr>
                    <td class="table_title">Получил</td>
                    <td id="toUser"></td>
                </tr>
                <tr tabindex="0">
                    <td class="table_title">Статус</td>
                    <td id="status"></td>
                </tr>
                <tr>
                    <td class="table_title">Запись получателя</td>
                    <td id="arriveNote"></td>
                </tr>
                <tr id="changeArriveNote" style="display: none;">
                    <td class="table_change_title">Изменить запись</td>
                    <td><input type="text" id="inputArriveNote"/></td>
                </tr>
                <tr id="changeBetweenNote" style="display: none;">
                    <td class="table_change_title">Изменить запись по объекту</td>
                    <td><input type="text" id="inputBetweenNote"/></td>
                </tr>
                <tr id="changeButton" style="display: none;">
                    <td class="table_title"></td>
                    <td><button id="btn_change">Внести изменения</button></td>
                </tr>
                <tr id="changeArriveButton" style="display: none;">
                    <td class="table_title"></td>
                    <td><button id="btn_arrive_change">Внести изменения</button></td>
                </tr>
                <tr id="changeBetweenButton" style="display: none;">
                    <td class="table_title"></td>
                    <td><button id="btn_between_change">Внести изменения</button></td>
                </tr>
            </table>
            <br>
            <table border ="1">
                <caption><strong>Записи по промежуточным объектам</strong></caption>
                <thead>
                    <th>Наименование объекта</th>
                    <th>Время регистрации</th>
                    <th>Исполнитель</th>
                    <th>Запись исполнителя</th>
                </thead>
                <tbody class="table_body" id="points_table_body">
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
            $('#select_branch').trigger("change");
            var chose_department = document.getElementById("chose_department");
            if(${department.id}==1){
                chose_department.style.display = "block";
            }
            $('#reload_journal').trigger("click");
            var line_cut_note = document.getElementById("line_cut_note");
            var show_note = document.getElementById("show_note");
            $('#line_cut_note').on('click', function(){
                show_note.style.display = "none";
            });
            var resultLineValue;
            var clickNumber = 0;
            window.addEventListener("click", function(){
                clickNumber++;
                resultLineValue = $('#result_line').text();
                if(clickNumber==0){
                    $('#result_line').text("");
                }
                if(resultLineValue.length>0){
                    clickNumber = -1;
                }
            });
       });
    </script>

</body>
</html>
