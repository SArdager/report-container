<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Pay control page</title>
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
    <script type="text/javascript" src="../resources/js/selectDepartment.js"></script>
</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <strong style="margin-top: 4px; margin-right: 20px">Пользователь: ${user.userFirstname} ${user.userSurname}</strong>
            <a style="margin-top: 4px;" href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Отчет по расходу денежных средств по объекту</h1>
        <br>
        <a href="start-page">Вернуться</a>
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
        <form id="export_pay" action="payment/report-exportExcel" method="post">
            <div class="title_row" style="margin-left: 40px">
                <span class="date_line">Вывести за период:</span>
                <input type="date" id="startDate" name="startDate"/>
                <input type="date" id="endDate" name="endDate"/>
                <span class="text_line">по</span>
                <input type="number" id="paymentPageSize" min="2" value="10"/>
                <span class="text_line">записей</span>
                <input type="hidden" id="totalPaymentNotes" value="0"/>
                <input type="hidden" id="paymentDepartmentId" value="1" name="departmentId"/>
            </div>
        </form>
        <div class="title_row">
            <span class="date_line">Вывести по объекту:</span>
            <div class="checkbox_margin" id="chose_checkbox" style="margin-left: 14px; display: none">
                <input type="checkbox" id="department_checkbox" style="margin: 0px; padding: 0px"/>
                <span class="text_line">- все объекты</span>
            </div>
            <select id="select_branch" class="select_in_line" style="display: none">
                <c:forEach var="branch" items="${branches}">
                    <option value=${branch.id}>${branch.branchName}</option>
                </c:forEach>
            </select>
            <select id="select_department" class="select_in_line">
            </select>
        </div>

        <div class="title_row" style="justify-content: space-between;">
            <div class="title_row" style="width: 40%; justify-content: space-between; margin-right: 0.5em"">
                <span id="reload_payment" class ="reload_line">Показать</span>
                <img src="../resources/images/export_excel_48.png" id="btn_export_pay" width="24" height="24" alt="">
            </div>
            <div id="pages_payment_title" ></div>
        </div>
        <div class = "scroll_table">
           <table>
             <thead>
               <tr>
                 <th>Номер</th>
                 <th>Дата отправки</th>
                 <th>Отправитель</th>
                 <th>Получатель</th>
                 <th>Стоимость</th>
                 <th>Запись отправителя</th>
               </tr>
             </thead>
             <tbody id = "payment_table_body">
             </tbody>
           </table>
        </div>
        <br>
     </div>
    <div class="buffer" style = "height: 5em;"></div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
            var chose_checkbox = document.getElementById("chose_checkbox");
            var select_branch = document.getElementById("select_branch");
            if(${department.id}==1){
                chose_checkbox.style.display = "block";
                select_branch.style.display = "block";
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
