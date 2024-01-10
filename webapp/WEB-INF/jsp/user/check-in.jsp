<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Termocontainer check-in page</title>
  <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
  <script type="text/javascript" src="../resources/js/worker.js"></script>
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
        <hr>
        <h1>Приемка термоконтейнера</h1>
        <br>
        <a class="link_line" href="../work-starter">Вернуться</a>
        <a class="link_line" href="check-between">Регистрация на объекте</a>
        <a class="link_line" href="check-out">Отгрузка термоконтейнера</a>
        <a class="link_line" href="check-journal">Журнал движения термоконтейнеров</a>
        <br>
        <h3><div id="result_line"></div></h3>
        <p>
            <div class="title_row">
                <div class="title_name">Наименование объекта:</div>
                <div class="color_text" id="dep_name"> ${department.departmentName},  ${department.branch.branchName}</div>
            </div>
            <div class="title_row">
                <div class="title_name">Права пользователя</div>
                <div id="userRights" class="color_text">${userRights.rights}</div>
            </div>
        </p>
        <div id="reload_input" class ="cut_line">Обновить</div>
        <table class="table_shot">
            <caption><strong>Журнал ожидания термоконтейнеров</strong></caption>
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
        <div id="parcels_table" style="display: none">
            <table class="table_shot">
                <caption><strong>Журнал ожидания посылок</strong></caption>
                <thead>
                    <th>Номер посылки</th>
                    <th>Пункт назначения</th>
                    <th>Вложена в термоконтейнер (посылку)</th>
                    <th>Содержание</th>
                    <th>Габариты</th>
                </thead>
                <tbody class="table_body" id="parcels_table_body">
                </tbody>
            </table>
        </div>
        <br>
        <hr>
        <h2>Приемка термоконтейнера</h2>
        <h3><div id="check_line"></div></h3>
        <div id="checking_view" style="display: none">
            <table>
                <tr>
                    <td class="table_title">Сканирование номера</td>
                    <td><input type="text" id="number_income" maxlength="8"/><br></td>
                </tr>
                <tr>
                    <td class="table_title"></td>
                    <td id="clean_input" class="cut_line">Очистить поле ввода</td>
                </tr>
                <tr>
                    <td class="table_title">Запись о приеме</td>
                    <td><br><textarea id="textarea_in"></textarea></td>
                </tr>
                <tr>
                    <td class="table_title"></td>
                    <td><button id="btn_income">Зарегистрировать</button></td>
                </tr>
                <tr>
                    <td class="table_title">Время регистрации</td>
                    <td id="time_income">Прибытие не зарегистрировано</td>
                </tr>
                <tr>
                    <td class="table_title">Статус доставки</td>
                    <td id="status_income"></td>
                </tr>
            </table>
        </div>
     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            let name = "${user.userFirstname}";
                document.getElementById("user_name").textContent = name.substring(0, 1) + ". ${user.userSurname}";
            let depName = $('#dep_name').html();
            let rights = $('#userRights').html();
            if(rights.indexOf("ВНЕСЕНИЕ")>-1){
                $('#checking_view').css("display", "block");
            } else if(rights.indexOf("ТЕРМОКОНТЕЙНЕР")>0){
                $('#checking_view').css("display", "block");
            } else if(rights.indexOf("ЛАБОРАТОРИИ")>0){
                $('#checking_view').css("display", "block");
            } else if(depName.indexOf("Склад")>-1){
                $('#checking_view').css("display", "block");
            } else {
                $('#check_line').html("Права на регистрацию термоконтейнеров отсутствуют");
            }
            $('#reload_input').trigger("click");
            document.getElementById("number_income").focus();
            var resultLineValue;
            var clickNumber = 0;
            window.addEventListener("click", function(){
                clickNumber++;
                resultLineValue = $('#result_line').text();
                if(clickNumber==0){
                    $('#result_line').text("");
                    $('#clean_input').trigger("click");
                }
                if(resultLineValue.length>0){
                    clickNumber = -1;
                }
            });
            $('#clean_input').click(function(){
                $('#time_income').html("");
                $('#status_income').html("");
                $('#number_income').val("");
                $('#number_income').focus();
            });
       });
    </script>

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>
