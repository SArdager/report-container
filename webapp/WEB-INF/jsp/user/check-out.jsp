<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Termocontainer check-out page</title>
  <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
  <script type="text/javascript" src="../resources/js/selectDepartment.js"></script>
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
            <strong style="margin-top: 4px; margin-right: 20px">Пользователь: ${user.userFirstname} ${user.userSurname}</strong>
            <a style="margin-top: 4px; " href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Отгрузка термоконтейнера</h1>
        <br>
        <a class="link_line" href="../work-starter">Вернуться</a>
        <a class="link_line" href="check-between">Регистрация на объекте</a>
        <a class="link_line" href="check-in">Приемка термоконтейнера</a>
        <a class="link_line" href="check-journal">Журнал движения термоконтейнеров</a>
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
        <hr>
        <h2><div id="check_line"></div></h2>
        <input type="hidden" id="departmentId" value="${department.id}" />
        <table id="checking_view" style="display: none">
            <tr>
                <td class="table_title">Получатель</td>
                <td><select id="select_branch">
                      <c:forEach var="branch" items="${branches}">
                        <option value=${branch.id}>${branch.branchName}</option>
                      </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><select id="select_department">
                    </select><br>
                </td>
            </tr>
            <tr>
                <td class="table_title">Сканирование номера</td>
                <td><input type="text" id="number_outcome" />
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td id="clean_input" class="cut_line">Очистить поле ввода</td>
            </tr>
            <tr>
                <td class="table_title">Оплата отгрузки</td>
                <td><input type="number" id="payment" style="width: 8em;" value="0" /></td>
            </tr>
            <tr>
                <td class="table_title">Запись по отгрузке</td>
                <td><textarea id="textarea_out"></textarea></td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><button id="btn_outcome" >Зарегистрировать</button></td>
            </tr>
            <tr>
                <td class="table_title">Время отгрузки</td>
                <td id="time_outcome"></td>
            </tr>
            <tr>
                <td class="table_title">Статус отгрузки</td>
                <td id="status_outcome"></td>
            </tr>
        </table>

     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
            var scan_field = document.getElementById("scan_field");
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
                $('#time_outcome').html("");
                $('#status_outcome').html("");
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
