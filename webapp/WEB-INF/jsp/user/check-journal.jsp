<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Termocontainers journal page</title>
  <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
  <script>
      var w = Number(window.innerWidth);
      var h = Number(window.innerHeight);
      if (h>w) {
        $('head').append('<link rel="stylesheet" type="text/css" href="../resources/css/mobileStyle.css">');
        $('head').append('<meta name="viewport" content="width=device-width, initial-scale=1.0">');
      } else {
        $('head').append('<link rel="stylesheet" type="text/css" href="../resources/css/style.css">');
      }
  </script>
  <script type="text/javascript" src="../resources/js/worker.js"></script>
  <script type="text/javascript" src="../resources/js/showNote.js"></script>
  <script type="text/javascript" src="../resources/js/selectDepartment.js"></script>
</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <strong style="margin-top: 4px; margin-right: 20px">Пользователь: ${user.userFirstname} ${user.userSurname}</strong>
            <a style="margin-top: 4px;" href="../logout">Выйти</a>
        </div>
        <input type="hidden" id="userId" value="${user.id}" />
        <hr>
        <h1>Журнал движения термоконтейнеров по объекту</h1>
        <br>
        <a class="link_line" href="../work-starter">Вернуться</a>
        <a class="link_line" href="check-between">Регистрация на объекте</a>
        <a class="link_line" href="check-in">Приемка термоконтейнера</a>
        <a class="link_line" href="check-out">Отгрузка термоконтейнера</a>
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
        <form id="export_excel" action="load-data/journal-export-excel" method="post">
            <div class="title_row" >
                <span class="date_line">Вывести за период:</span>
                <input type="date" id="startDate" name="startDate" />
                <input type="date" id="endDate" name="endDate" />
            </div>
            <div class="title_row" style="justify-content: right;">
                <span class="text_line" >по</span>
                <input type="number" id="pageSize" min="2" value="10"/>
                <span class="text_line">записей</span>
                <input type="hidden" id="totalNotes" value="0"/>
                <input type="hidden" id="exportDepartmentId" name="exportDepartmentId" value="1" />
            </div>
        </form>
        <div id="chose_department" class="title_row" style="display: none">
            <span class="date_line">Вывести по объекту:</span>
            <input type="checkbox" id="department_checkbox" style="margin-left: 14px; "/>
            <span class="text_line" >- все объекты</span>
            <select id="select_branch" class="select_in_line">
                <option value="0">Выберите филиал</option>
                <c:forEach var="branch" items="${branches}">
                    <option value=${branch.id}>${branch.branchName}</option>
                </c:forEach>
            </select>
            <select id="select_department" class="select_in_line">
            </select>
        </div>
        <div class="title_row" style="justify-content: space-between;">
            <div class="title_row" style="width: 40%; justify-content: space-between; margin-right: 0.5em">
                <span id="reload_journal" class ="reload_line" >Обновить</span>
                <img src="../resources/images/export_excel_48.png" id="btn_export_excel" width="24" height="24" align = "top" alt="">
            </div>
            <div id="pages_journal_title"></div>
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
             <tbody id = "notes_table_body">
             </tbody>
           </table>
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
            <table>
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
                    <td><input type="number" class="pay_input" id="inputPay" value="0"/></td>
                </tr>
                <tr>
                    <td class="table_title">Получатель</td>
                    <td id="toDepartment"></td>
                </tr>
                <tr id="changeToBranch" style="display: none;">
                    <td class="table_change_title">Изменить получателя</td>
                    <td><select id="select_change_branch" >
                            <c:forEach var="branch" items="${branches}">
                                <option value=${branch.id}>${branch.branchName}</option>
                            </c:forEach>
                        </select></td>
                </tr>
                <tr id="changeToDepartment" style="display: none;">
                    <td class="table_title"></td>
                    <td><select id="select_change_department" >
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
                    $('#number_check').val("");
                    $('#number_check').focus();
                }
                if(resultLineValue.length>0){
                    clickNumber = -1;
                }
            });
       });
    </script>

</body>
</html>

