<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Parcels journal page</title>
  <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
  <script type="text/javascript" src="../resources/js/showParcels.js"></script>
  <script type="text/javascript" src="../resources/js/selectDepartment.js"></script>
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
        <h1>Мониторинг почтовых отправлений</h1>
        <br>
        <a class="link_line" href="../work-starter">Вернуться</a>
        <a id="create_link" class="link_line" href="create-parcel">Создание и отправка почтового отправления</a>
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
                <div id="userRights" class="color_text">${userRights.rights}</div>
            </div>
        </p>
        <hr>
        <h4 id="show_check_in">Приемка почтовых отправлений</h4>
        <h4 id="show_check_parcels">Журнал движения посылок по объекту</h4>
        <div id= "check_in_field" style="display: none">
            <div id="close_check_in" class="cut_line">Скрыть поле приемки почтовых отправлений</div>

            <div id="reload_input_parcels" class ="reload_line">Обновить</div>
            <table class="table_shot">
                <caption><strong>Журнал ожидания посылок</strong></caption>
                <thead>
                    <th>Номер посылки</th>
                    <th>Содержание</th>
                    <th>Откуда</th>
                    <th>Дата отправки</th>
                    <th>Габариты</th>
                </thead>
                <tbody class="table_body" id="input_parcels_table_body">
                </tbody>
            </table>
            <br>
            <hr>
            <h2>Регистрация почтового отправления</h2>
            <table>
                <tr>
                    <td class="table_title">Сканирование номера</td>
                    <td><input type="text" id="parcel_number_income" maxlength="8"/><br></td>
                </tr>
                <tr>
                    <td class="table_title"></td>
                    <td id="clean_input_parcel" class="cut_line">Очистить поле ввода</td>
                </tr>
                <tr>
                    <td class="table_title">Запись о приеме</td>
                    <td><br><textarea id="parcel_textarea_in"></textarea></td>
                </tr>
                <tr>
                    <td class="table_title"></td>
                    <td><button id="btn_income_parcel">Зарегистрировать</button></td>
                </tr>
            </table>
        </div>
        <div id= "check_parcels_field" style="display: none">
            <div id="close_check_parcels" class="cut_line">Скрыть поле журнала движения посылок</div>
            <form id="export_excel" action="load-data/parcels-export-excel" method="post">
               <div class="title_row" >
                   <span class="date_line">Вывести за период:</span>
                   <input type="date" id="startDate" name="startDate" />
                   <input type="date" id="endDate" name="endDate" />
                   <span class="text_line" style="margin-left: 10px;">по</span>
                   <input type="number" id="pageSize" min="5" step="10" value="30"/>
                   <span class="text_line">строк</span>
               </div>
               <input type="hidden" id="totalParcels" value="0"/>
               <input type="hidden" id="exportDepartmentId" name="exportDepartmentId" value="1" />
            </form>
            <div class="title_row" >
               <span class="date_line">Вывести по отправлениям:</span>
               <input type="radio" name="sample" value="all" checked="checked"/>
               <span class="text_line" >- по всем</span>
               <input type="radio" name="sample" value="route" />
               <span class="text_line" >- в пути</span>
               <input type="radio" name="sample" value="out" />
               <span class="text_line" >- отправленые</span>
               <input type="radio" name="sample" value="to" />
               <span class="text_line" >- доставленые</span>
            </div>
            <div id="chose_department" class="title_row" style="display: none">
               <span class="date_line">Вывести по объекту:</span>
               <input type="checkbox" id="all_checkbox" checked="checked" style="margin-left: 14px;" />
               <span class="text_line" >- все объекты</span>
               <select id="select_company" class="select_in_line">
                   <c:forEach var="company" items="${companies}">
                       <option value=${company.id}>${company.companyName}</option>
                   </c:forEach>
               </select>
               <select id="select_branch" class="select_in_line">
               </select>
               <select id="select_department" class="select_in_line">
               </select>
            </div>
            <div id="show_search_field" class="reload_line">Поиск посылки по номеру</div>
            <div id="search_field" style="display: none">
               <div class="title_row" >
                   <span id="close_search" class="cut_line" style="margin-right: 20px; ">Скрыть таблицу поиска</span>
                   <span class="date_line">Номер отправления:</span>
                   <input type="text" id="parcel_number" maxlength="8" style="margin-left: 14px; width: 160px"/>
                   <button id="btn_search_parcel" style="margin-top: 2px; margin-left: 16px; ">Найти</button>
               </div>
            </div>
            <div id="reload_table_line" style="display: block">
               <div class="title_row">
                   <div class="title_left"">
                       <span id="reload_parcels" class ="reload_line" >Обновить</span>
                       <img src="../resources/images/export_excel_48.png" id="btn_parcel_excel" align = "top" alt="">
                   </div>
                   <div id="parcels_pages_title"></div>
               </div>
            </div>
            <div id="parcels_table" class = "scroll_table">
              <table>
                <thead>
                  <tr>
                    <th>Номер</th>
                    <th>Содержимое</th>
                    <th>Габариты</th>
                    <th>Отправитель</th>
                    <th>Дата отправки</th>
                    <th>Получатель</th>
                    <th>Дата доставки</th>
                    <th>Стоимость доставки</th>
                    <th>Статус</th>
                    <th>Примечание</th>
                  </tr>
                </thead>
                <tbody id = "parcels_table_body">
                </tbody>
              </table>
            </div>
            <br>
            <div id="parcel_points_field" style="display: none">
               <table id="points_table" class="table_shot">
                   <caption><strong>Информация по маршруту почтового отправления</strong></caption>
                   <thead>
                       <th>Филиал</th>
                       <th>Дата прибытия</th>
                       <th>Получил</th>
                       <th>Дата отправки</th>
                       <th>Отправил</th>
                       <th>Вложено в</th>
                       <th>Стоимость</th>
                       <th>Примечание</th>
                   </thead>
                   <tbody class="table_body" id="points_table_body">
                   </tbody>
               </table>
            </div>
        </div>
     </div>
    <div class="buffer" style = "height: 5em;"></div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            let name = "${user.userFirstname}";
            document.getElementById("user_name").textContent = name.substring(0, 1) + ". ${user.userSurname}";
            let rights = $('#userRights').html();
            if(rights.indexOf("ПОСЫЛОК")<0){
                $('#create_link').css("display", "none");
                $('#show_check_in').css("display", "none");
                $('#close_check_parcels').css("display", "none");
                $('#show_check_parcels').trigger("click");
            }
            $('#select_company').trigger("change");
            if(${department.id}==1){
                document.getElementById("chose_department").style.display = "block";
            }
            $('#all_checkbox').change (function(){
                if($('#all_checkbox').is(':checked')==true){
                    $('#select_branch').val("0");
                }
            });
            $('#select_branch').change (function(){
                if($('#select_branch').val()>0){
                    document.getElementById("all_checkbox").checked = false;
                } else {
                    document.getElementById("all_checkbox").checked = true;
                }
            });

            var resultLineValue;
            var clickNumber = 0;
            window.addEventListener("click", function(){
                clickNumber++;
                resultLineValue = $('#result_line').text();
                if(clickNumber==0){
                    $('#result_line').text("");
                    $('#parcel_number_income').val("");
                    $('#parcel_number_income').focus();
                }
                if(resultLineValue.length>0){
                    clickNumber = -1;
                }
            });
       });
    </script>

</body>
</html>

