<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Termocontainer accounting page</title>
  <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
  <script type="text/javascript" src="../resources/js/valuesEditor.js"></script>
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
            <a style="margin-top: 4px;" href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Редактирование времени доставки термоконтейнеров</h1>
        <br>
        <a class="link_line" href="../admin">Вернуться</a>
        <br>
        <h2><div id="result_line"></div></h2>
        <p>
        <div class="main_block">
            <div class="field">
                <label><b>Пункт отправки</b></label>
                <select id="select_first_branch" >
                        <c:forEach var="branch" items="${branches}">
                        <option value=${branch.id}>${branch.branchName}</option>
                        </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Выберите объект</label>
                    <select id="select_first_department" >
                        <c:forEach var="department" items="${firstDepartments}">
                        <option value=${department.id}>${department.departmentName}</option>
                        </c:forEach>
                    </select>
            </div>
            <div class="field">
                <label><b>Пункт доставки</b></label>
                <select id="select_second_branch" >
                        <c:forEach var="branch" items="${branches}">
                        <option value=${branch.id}>${branch.branchName}</option>
                        </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Выберите объект</label>
                    <select id="select_second_department" >
                        <c:forEach var="department" items="${secondDepartments}">
                        <option value=${department.id}>${department.departmentName}</option>
                        </c:forEach>
                    </select>
            </div>
            <div class="field">
                <label><b>Время доставки</b> (часов):</label>
                <input type="number" id="time_standard" size="40" value="0" required/>
            </div>
            <p>
                <input type="hidden" id="standard_id" />
                <div class="field">
                    <label></label>
                    <input type="button" id="btn_standard" style="width: 110px; " value="Внести" />
                </div>
            </p>
        </div>
        </p>
     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
            $('#select_first_branch').trigger("change");
            $('#select_second_branch').trigger("change");
            $('#standard_id').val("0");
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
