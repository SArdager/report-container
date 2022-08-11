<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Termocontainer check-out page</title>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/selectDepartment.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/worker.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <strong style="margin-top: 4px; margin-right: 20px">Пользователь: ${user.userFirstname} ${user.userSurname}</strong>
            <a style="margin-top: 4px;" href="/logout">Выйти</a>
        </div>
        <hr>
        <h1>Отгрузка термоконтейнера</h1>
        <br>
        <a href="/work-starter">Вернуться</a>
        <a style="margin-left: 20px;" href="/user/check-between">Регистрация на объекте</a>
        <a style="margin-left: 20px;" href="/user/check-in">Приемка термоконтейнера</a>
        <a style="margin-left: 20px;" href="/user/check-journal">Журнал движения термоконтейнеров</a>
        <br>
        <h2><div id="result_line"></div></h2>
        <p>
            <div class="title_row">
                <div class="title_name">Наименование объекта:</div>
                <div class="color_text"> ${department.departmentName},  ${department.branch.branchName}</div>
            </div>
            <div class="title_row">
                <div class="title_name">Права пользователя</div>
                <div id="userRights" class="color_text">${userRights.rights}</div>
            </div>
        </p>
        <br>
        <hr>
        <h2><div id="check_line"></div></h2>
        <input type="hidden" id="departmentId" value="${department.id}" />
        <div class="main_block" id="checking_view" style="display: none">
            <div class="field">
                <label>Получатель</label>
                <select id="select_branch">
                    <c:forEach var="branch" items="${branches}">
                    <option value=${branch.id}>${branch.branchName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label></label>
                <select id="select_department">
                </select>
            </div>
            <br>
            <div class="field">
                <label>Вид проб</label>
                <select id="select_probe">
                    <c:forEach var="probe" items="${probes}">
                        <option value=${probe.id}>${probe.probeName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Сканирование номера</label>
                <input type="text" id="number_outcome" />
                <div id="clean_input" style ="font-size: 0.9em; text-decoration: underline;">Очистить поле ввода</div>
            </div>
            <div class="field">
                <label>Оплата отгрузки</label>
                <input type="number" id="payment" value=0 />
            </div>
            <div class="field">
                <label>Запись по отгрузке</label>
                <textarea id="textarea_out"></textarea><br>
                <button id="btn_outcome" >Зарегистрировать</button>
            </div>
            <div class="field">
                <label>Время отгрузки</label>
                <div id="time_outcome"></div>
            </div>
            <div class="field">
                <label>Статус отгрузки</label>
                <div id="status_outcome"></div>
            </div>
        </div>

     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
            $('#select_branch').trigger("change");
            document.getElementById("number_outcome").focus();
            var rights = $('#userRights').html();
            var checking_view = document.getElementById("checking_view");

            if(rights.indexOf("ВНЕСЕНИЕ")>-1){
                checking_view.style.display = "block";
            } else {
                $('#check_line').html("Права на регистрацию термоконтейнеров отсутствуют");
            }
            $('#number_outcome').focus();
            $('#clean_input').click(function(){
                $('#number_outcome').val("");
                $('#number_outcome').focus();
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

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>
