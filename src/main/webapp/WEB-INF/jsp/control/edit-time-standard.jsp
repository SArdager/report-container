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
            <span id="user_name"></span>
            <a href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Редактирование времени доставки термоконтейнеров</h1>
        <br>
        <a class="link_line" href="../work-starter">Вернуться</a>
        <br>
        <h3><div id="result_line"></div></h3>
        <p>
        <div class="main_block">
            <input type="hidden" id="role" value="${user.role}"/>
            <input type="hidden" id="userBranchId" value="${userBranch.id}"/>
            <input type="hidden" id="userBranchName" value="${userBranch.branchName}"/>
            <div class="field">
                <label><b>Филиал отправки</b></label>
                <select id="select_first_branch" >
                    <c:forEach var="branch" items="${branches}">
                        <option value=${branch.id}>${branch.branchName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Выберите объект</label>
                <select id="select_first_department" >
                </select>
            </div>
            <div class="field">
                <label><b>Филиал получения</b></label>
                <select id="select_second_branch" >
                    <c:forEach var="branch" items="${branches}">
                        <option value=${branch.id}>${branch.branchName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Выберите объект</label>
                <select id="select_second_department" >
                </select>
            </div>
            <div class="field">
                <label><b>Время доставки</b> (часов):</label>
                <input type="number" id="time_standard" size="40" value="0" required/>
            </div>
            <p>
                <input type="hidden" id="standard_id" value="0"/>
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
            let name = "${user.userFirstname}";
            document.getElementById("user_name").textContent = name.substring(0, 1) + ". ${user.userSurname}";
            if($('#role').val()!="ADMIN"){
                $('#select_first_branch').empty();
                $('#select_first_branch').append('<option value="' + $('#userBranchId').val() + '">' + $('#userBranchName').val() + '</option>');
            } else {
                $('#select_first_branch').val($('#userBranchId').val());
            }
            $.ajax({
                url: '../user/change-department/select-branch',
                method: 'POST',
                dataType: 'json',
                data: {branchId: $('#select_first_branch').val()},
                success: function(departments) {
                    $('#select_first_department').empty();
                    $.each(departments, function(key, department){
                        $('#select_first_department').append('<option value="' + department.id + '">' + department.departmentName + '</option>');
                    });
                },
                error:  function(response) {
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
            $('#select_second_branch').val($('#userBranchId').val());
            $('#select_second_branch').trigger("change");
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

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>
