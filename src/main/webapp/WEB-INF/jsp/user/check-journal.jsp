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
            <span id="user_name"></span>
            <a href="../logout">Выйти</a>
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
        <h3><div id="result_line"></div></h3>
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
                <span class="text_line" style="margin-left: 10px;">по</span>
                <input type="number" id="pageSize" min="5" step="10" value="30"/>
                <span class="text_line">строк</span>
            </div>
            <input type="hidden" id="totalNotes" value="0"/>
            <input type="hidden" id="exportDepartmentId" name="exportDepartmentId" value="1" />
        </form>
        <div class="title_row" >
            <span class="date_line">Вывести по термоконтейнерам:</span>
            <input type="radio" name="sample" value="all" checked="checked"/>
            <span class="text_line" >- по всем</span>
            <input type="radio" name="sample" value="route" />
            <span class="text_line" >- в дороге</span>
            <input type="radio" name="sample" value="home" />
            <span class="text_line" >- на объекте</span>
        </div>
        <div id="chose_department" class="title_row" style="display: none">
            <span class="date_line">Вывести по объекту:</span>
            <input type="checkbox" id="all_checkbox" style="margin-left: 14px; "/>
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
        <div id="show_search_field" class="reload_line">Поиск термоконтейнера по номеру</div>
        <div id="search_field" style="display: none">
            <div class="title_row" >
                <span class="date_line">Номер термоконтейнера:</span>
                <input type="text" id="container_number" maxlength="8" style="margin-left: 14px; width: 160px"/>
                <button id="btn_search" style="margin-top: 2px; margin-left: 16px; ">Найти</button>
            </div>
        </div>
        <div id="show_note" style="display: none">
            <div class="cut_line" id="line_cut_note" >Скрыть данные по термоконтейнеру</div>
            <h2>Данные по перемещению термоконтейнера</h2>
            <input type="hidden" id="outDepartmentId" />
            <input type="hidden" id="toDepartmentId" />
            <input type="hidden" id="outUserId" />
            <input type="hidden" id="toUserId" />
            <input type="hidden" id="passUserId" />
            <table>
                <tr tabindex="0">
                    <td class="table_title">Номер отправления</td>
                    <td id="containerNoteId"></td>
                </tr>
                <tr>
                    <td class="table_title" style="text-decoration: underline;">Термоконтейнер</td>
                    <td id="containerNumber"></td>
                </tr>
                <tr>
                    <td class="table_title">Номер термохрона</td>
                    <td id="thermometer"></td>
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
                    <td class="table_title">Место оплаты</td>
                    <td id="paidEnd"></td>
                </tr>
                <tr>
                    <td class="table_title">Сумма оплаты</td>
                    <td id="sendPay"></td>
                </tr>
                <tr id="changePay" style="display: none;">
                    <td class="table_change_title">Изменить сумму</td>
                    <td><input type="number" class="pay_input" id="inputPay" style="width: 8em;" step="500" value="0"/></td>
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
                <tr>
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
        <br>
        <div class="title_row">
            <div class="title_left">
                <span id="reload_journal" class ="reload_line" >Обновить</span>
                <img style="margin-left: 20px;" src="../resources/images/export_excel_48.png" id="btn_export_excel" align = "top" alt="">
            </div>
            <div id="journal_pages_title"></div>
        </div>
        <div class = "scroll_table">
           <table>
             <thead>
               <tr>
                 <th style='width: 15%'>№ термоконтейнера</th>
                 <th style='width: 10%'>Термохрон</th>
                 <th style='width: 15%'>Дата отправки</th>
                 <th style='width: 15%'>Дата приемки</th>
                 <th style='width: 15%'>Отправитель</th>
                 <th style='width: 15%'>Получатель</th>
                 <th style='width: 15%'>Статус</th>
               </tr>
             </thead>
             <tbody id = "notes_table_body">
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
            $('#select_branch').trigger("change");
            var chose_department = document.getElementById("chose_department");
            if(${department.id}==1){
                chose_department.style.display = "block";
            }
            $('#reload_journal').trigger("click");
            $('input[type=radio][name=sample]').change(function(){
                if(this.value != 'all'){
                    $('#btn_export_excel').css("display", "none");
                }
            });
            $('#show_search_field').on('click', function(){
                if($('#search_field').css("display") == "none"){
                    $('#search_field').css("display", "block");
                    $('#show_note').css("display", "none");
                    $('#show_search_field').html("Скрыть поле поиска");
                } else {
                    $('#search_field').css("display", "none");
                    $('#show_note').css("display", "none");
                    $('#show_search_field').html("Поиск термоконтейнера по номеру");
                }
            });

            $('#line_cut_note').on('click', function(){
                 $('#show_note').css("display", "none");
            });

            let resultLineValue;
            let clickNumber = 0;
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

