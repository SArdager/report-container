<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>User rights changer page</title>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/userRightsEditor.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/selectDepartment.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="/logout">Выйти</a>
        </div>
        <hr>
        <h1>Изменение прав пользователя</h1>
        <br>
        <a href="/admin">Вернуться</a>
        <br>
        <h2><div id="result_line"></div></h2>
        <div class="main_block">
                <div class="field">
                    <label>Пользователь</label>
                    <input type="text" id="user_name" size="40" placeholder="Первые три буквы фамилии" required/>
                </div>
                <input type="hidden" id="user_id" name="userId" value="0"/>
                <div class="field" id="show_select" style="display: none; ">
                    <label style="color: blue;" >Кликните пользователя</label>
                    <select id="select_user">
                    </select>
                </div>
                <br>
                <div class="field">
                    <label>Предприятие</label>
                    <select id="select_company">
                        <c:forEach var="company" items="${companies}">
                            <option value=${company.id}>${company.companyName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label>Филиал</label>
                    <select id="select_branch">
                        <c:forEach var="branch" items="${branches}">
                            <option value=${branch.id}>${branch.branchName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label>Объект</label>
                    <select id="select_department" name="departmentId">
                        <c:forEach var="department" items="${departments}">
                            <option value=${department.id}>${department.departmentName}</option>
                        </c:forEach>
                    </select>
                </div>
                <u>Права доступа</u>
                <div class="field">
                    <label>Просмотр записей</label>
                    <input type="checkbox" id="readerId" checked />
                </div>
                <div class="field">
                    <label>Внесение записей</label>
                    <input type="checkbox" id="editorId" />
                </div>
                <div class="field">
                    <label>Учет термоконтейнеров</label>
                    <input type="checkbox" id="accountId" />
                </div>
                <input type="hidden" id="user_rights" name="rights" value="reader" />
                <br>
                <div class="field">
                    <label>Дать/снять права администратора</label>
                    <input type="checkbox" id="roleId" />
                </div>
                <input type="hidden" id="user_role" name="role" />
        </div>
        <p>
        <br>
        <button id="btn_rights" style="margin-left: 120px" >Изменить права</button>
        </p>

     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
            $('#select_company').trigger("change");
            var result_line = document.getElementById('result_line');
            var resultLineValue;
            var clickNumber = 0;
            window.addEventListener("click", function(){
                clickNumber++;
                resultLineValue = $('#result_line').text();
                if(clickNumber==0){
                    $('#result_line').html("");
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
