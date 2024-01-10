<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Courier page</title>
  <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
  <script type="text/javascript" src="../resources/js/worker.js"></script>
  <script>
     $('head').append('<link rel="stylesheet" type="text/css" href="../resources/css/mobileStyle.css">');
     $('head').append('<meta name="viewport" content="width=device-width, initial-scale=1.0">');
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
        <h1>Доставка термоконтейнеров</h1>
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
        <hr>
        <h2>Регистрация термоконтейнера</h2>
        <h3><div id="result_line"></div></h3>
            <table>
                <tr>
                    <td class="table_title">Сканирование номера</td>
                    <td><input type="text" id="number_courier" maxlength="8"/><br></td>
                </tr>
                <tr>
                    <td class="table_title"><span id="clean_courier">Очистить номер</span></td>
                    <td><span>Откройте стандартное приложение сканирования штрих-кодов, отсканируйте номер, сохраните и внесите его в поле ввода <br>или внесите номер вручную</span></td>
                </tr>
                <tr>
                    <td class="table_title">Запись о приеме</td>
                    <td><br><textarea id="textarea_courier"></textarea></td>
                </tr>
                <tr>
                    <td class="table_title"></td>
                    <td><button id="btn_courier">Зарегистрировать</button></td>
                </tr>
            </table>

        <sec:authorize access="hasRole('ADMIN')">
        <h5><a href="change-department">Поменять объект</a></h5>
        </sec:authorize>
     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            let name = "${user.userFirstname}";
            document.getElementById("user_name").textContent = name.substring(0, 1) + ". ${user.userSurname}";

            document.getElementById("number_check").focus();
            $('#clean_courier').click(function(){
                $('#number_check').val("");
                $('#textarea_courier').val("");
                $('#number_check').focus();
            });

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

       });
    </script>

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>
