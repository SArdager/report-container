<%@page import="kz.kdlolymp.termocontainers.entity.User"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Pay control page</title>
    <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
    <script>
        let w = Number(window.innerWidth);
        let h = Number(window.innerHeight);
        if (h>w) {
          $('head').append('<link rel="stylesheet" type="text/css" href="../resources/css/mobileStyle.css">');
        } else {
          $('head').append('<link rel="stylesheet" type="text/css" href="../resources/css/style.css">');
        }
    </script>
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
        <hr>
        <h1>Отчет по расходу денежных средств по объекту</h1>
        <br>
        <a href="start-page">Вернуться</a>
        <br>
        <h3><div id="result_line"></div></h3>
        <p>
            <div class="title_row">
                <div class="title_name">Наименование объекта:</div>
                <div class="color_text"> ${department.departmentName},  ${department.branch.branchName}</div>
            </div>
            <div class="title_row">
                <div class="title_name">Права пользователя</div>
                <div class="color_text">${userRights.rights}</div>
                <input type="hidden" id="department_id" value="${department.id}" />
                <input type="hidden" id="branch_id" value="${department.branch.id}" />
            </div>
        </p>
        <form id="export_payment" action="" method="post">
            <div class="title_row">
                <span class="date_line">Вывести за период:</span>
                <input type="date" id="startDate" name="startDate"/>
                <input type="date" id="endDate" name="endDate"/>
                <span class="text_line">по</span>
                <input type="number" id="pageSize" min="5" step="10" value="30"/>
                <span class="text_line">строк</span>
                <input type="hidden" id="totalPayments" value="0"/>
                <input type="hidden" id="totalParcels" value="0"/>
                <input type="hidden" id="exportDepartmentId" value="${department.id}" name="departmentId"/>
                <input type="hidden" id="exportBranchId" value="${department.branch.id}" name="branchId"/>
            </div>
        </form>
        <div class="title_row" >
            <span class="date_line">Вывести по объекту:</span>
            <div class="checkbox_margin" id="chose_checkbox" style="display: none">
                <input type="checkbox" id="all_checkbox" style="margin: 0px; padding: 0px"/>
                <span class="text_line">- все объекты</span>
            </div>
            <select id="select_branch" class="select_in_line" style="display: none">
                <c:forEach var="branch" items="${branches}">
                    <option value=${branch.id}>${branch.branchName}</option>
                </c:forEach>
            </select>
            <select id="select_department" class="select_in_line">
            </select>
            <div class="checkbox_margin" >
                <input type="checkbox" id="branch_checkbox" />
                <span class="text_line">- по филиалу</span>
            </div>
        </div>
        <h4 id="show_payment">Оплата за транспортировку термоконтейнеров</h4>
        <h4 id="show_parcels">Оплата за транспортировку посылок</h4>
        <div id="payment_field" style="display: none">
            <div id="close_containers" class="cut_line">Скрыть поле оплаты за транспортировку термоконтейнеров</div>
            <div class="title_row">
               <div class="title_left">
                   <span id="reload_payment" class ="reload_line">Показать</span>
                   <img src="../resources/images/export_excel_48.png" id="btn_export_payment" alt="">
               </div>
               <div id="payment_pages_title" style="margin-left: 20px;"></div>
            </div>
            <div id="payment_table" class = "scroll_table">
               <table>
                 <caption><strong>Информация по транспортировке термоконтейнеров</strong></caption>
                 <thead>
                   <tr>
                     <th>Номер</th>
                     <th>Дата отправки</th>
                     <th>Отправитель</th>
                     <th>Получатель</th>
                     <th>Стоимость</th>
                     <th>Количество</th>
                     <th>Место оплаты</th>
                     <th>Запись отправителя</th>
                   </tr>
                 </thead>
                 <tbody id = "payment_table_body">
                 </tbody>
               </table>
            </div>
        </div>
        <br>
        <div id="parcels_field" style="display: none">
            <div id="close_parcels" class="cut_line">Скрыть поле оплаты за транспортировку посылок</div>
            <div class="title_row">
               <div class="title_left">
                   <span id="reload_parcels" class ="reload_line">Показать</span>
                   <img src="../resources/images/export_excel_48.png" id="btn_export_parcels" align = "top" alt="">
               </div>
               <div id="parcels_pages_title" style="margin-left: 20px;"></div>
            </div>
            <div id="parcels_payment_table" class = "scroll_table">
               <table>
                 <caption><strong>Информация по транспортировке посылок</strong></caption>
                 <thead>
                   <tr>
                     <th>Номер</th>
                     <th>Дата отправки</th>
                     <th>Вид отправления</th>
                     <th>Отправил</th>
                     <th>Получатель</th>
                     <th>Вложено в</th>
                     <th>Стоимость доставки</th>
                     <th>Запись отправителя</th>
                   </tr>
                 </thead>
                 <tbody id = "parcels_table_body">
                 </tbody>
               </table>
            </div>
            <br>
            <div id="parcel_info_field" style="display: none">
                <table>
                    <tr>
                        <td class="table_title">Номер посылки</td>
                        <td id="parcel_number"></td>
                    </tr>
                    <tr>
                        <td class="table_title" >Отправитель</td>
                        <td id="sender"></td>
                    </tr>
                    <tr>
                        <td class="table_title" >Получатель</td>
                        <td id="destination"></td>
                    </tr>
                    <tr>
                        <td class="table_title" >Содержимое</td>
                        <td id="parcel_type"></td>
                    </tr>
                    <tr>
                        <td class="table_title" >Габариты</td>
                        <td id="dimensions"></td>
                    </tr>
                    <tr>
                        <td class="table_title" >Отправлено</td>
                        <td id="send_date"></td>
                    </tr>
                    <tr>
                        <td class="table_title" >Статус</td>
                        <td id="status"></td>
                    </tr>
                </table>
                <table id="points_payment_table" class="table_shot">
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
            if(${department.id}==1){
                document.getElementById("chose_checkbox").style.display = "block";
                document.getElementById("select_branch").style.display = "block";
            } else {
                $('#select_branch').val(${department.branch.id});
            }
            $('#show_payment').on('click', function(){
                $('#payment_field').css("display", "block");
                $('#parcels_field').css("display", "none");
            });
            $('#show_parcels').on('click', function(){
                $('#payment_field').css("display", "none");
                $('#parcels_field').css("display", "block");
            });
            $('#close_containers').on('click', function(){
                $('#payment_field').css("display", "none");
            });
            $('#close_parcels').on('click', function(){
                $('#parcels_field').css("display", "none");
            });

            $('#select_branch').trigger("change");
            $('#all_checkbox').on('click', function(){
                if($('#all_checkbox').is(':checked')){
                    $('#branch_checkbox').prop('checked', false);
                }
            });
            let resultLineValue;
            let clickNumber = 0;
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
