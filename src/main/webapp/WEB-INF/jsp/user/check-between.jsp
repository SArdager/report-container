<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Termocontainer check-between page</title>
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
        <h1>Промежуточный объект регистрации</h1>
        <br>
        <a class="link_line" href="../work-starter">Вернуться</a>
        <a class="link_line" href="check-in">Приемка термоконтейнера</a>
        <a class="link_line" href="check-out">Отгрузка термоконтейнера</a>
        <a class="link_line" href="check-journal">Журнал движения термоконтейнеров</a>
        <br>
        <h3><div id="result_line"></div></h3>
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
        <h3><div id="check_line"></div></h3>
        <table id="checking_view" style="display: none">
            <tr>
                <td class="table_title">Сканирование номера</td>
                <td><input type="text" id="number_check" maxlength="8"/></td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td id="clean_input" class="cut_line">Очистить поле ввода<br></td>
            </tr>
            <tr>
                <td class="table_title">Запись о регистрации</td>
                <td><br><textarea id="textarea_between"></textarea></td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><button id="btn_check">Зарегистрировать</button></td>
            </tr>
            <tr>
                <td class="table_title">Время регистрации</td>
                <td id="time_check">Прибытие не зарегистрировано</td>
            </tr>
            <tr>
                <td class="table_title">Статус регистрации</td>
                <td id="status_check"></td>
            </tr>
        </table>
     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            let name = "${user.userFirstname}";
            document.getElementById("user_name").textContent = name.substring(0, 1) + ". ${user.userSurname}";
            var rights = $('#userRights').html();
            if(rights.indexOf("ВНЕСЕНИЕ")>-1 || rights.indexOf("ЛАБОРАТОРИИ")>0){
                $('#checking_view').css("display", "block");
            } else {
                $('#check_line').html("Права на регистрацию термоконтейнеров в промежуточном объекте отсутствуют");
            }
            document.getElementById("number_check").focus();
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
            $('#clean_input').click(function(){
                $('#number_check').val("");
                $('#number_check').focus();
            });
       });
    </script>

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>
