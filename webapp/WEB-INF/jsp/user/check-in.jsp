<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Termocontainer check-in page</title>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
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
        <h1>Приемка термоконтейнера</h1>
        <br>
        <a href="/work-starter">Вернуться</a>
        <a style="margin-left: 20px;" href="/user/check-between">Регистрация на объекте</a>
        <a style="margin-left: 20px;" href="/user/check-out">Отгрузка термоконтейнера</a>
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
        <table border ="1">
            <caption><strong>Журнал ожидания термоконтейнеров</strong></caption>
            <div id="reload_input" style ="font-size: 0.9em; text-decoration: underline;">Обновить</div>
            <thead>
                <th>№ отправления</th>
                <th>Срок прибытия</th>
                <th>Номер термоконтейнера</th>
                <th>Отправитель</th>
            </thead>
            <tbody class="table_body" id="income_table_body">
            </tbody>
        </table>
        <br>
        <hr>
        <h3>Приемка термоконтейнера</h3>
        <h2><div id="check_line"></div></h2>
        <div class="main_block" id="checking_view" style="display: none">
            <div class="field">
                <label>Сканирование номера</label>
                <input type="text" id="number_income" />
                <div id="clean_input" style ="font-size: 0.9em; text-decoration: underline;">Очистить поле ввода</div>
            </div>
            <div class="field">
                <label>Запись о приеме</label>
                <textarea id="textarea_in"></textarea><br>
                <button id="btn_income" >Зарегистрировать</button>
            </div>
            <div class="field">
                <label>Время регистрации</label>
                <div id="time_income">Прибытие не зарегистрировано</div>
            </div>
            <div class="field">
                <label>Статус доставки</label>
                <div id="status_income"></div>
            </div>
        </div>

     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
            var rights = $('#userRights').html();
            var checking_view = document.getElementById("checking_view");

            if(rights.indexOf("ВНЕСЕНИЕ")>-1){
                checking_view.style.display = "block";
            } else {
                $('#check_line').html("Права на регистрацию термоконтейнеров отсутствуют");
            }
            $('#reload_input').trigger("click");
            document.getElementById("number_income").focus();
            var resultLineValue;
            window.addEventListener("click", function(){
                resultLineValue = $('#result_line').text();
                if(resultLineValue.length>0){
                    $('#result_line').text("");
                }
            });
            $('#clean_input').click(function(){
                $('#number_income').val("");
                $('#number_income').focus();
            });
       });
    </script>

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>
