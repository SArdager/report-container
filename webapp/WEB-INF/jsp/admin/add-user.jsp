<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Add new user</title>
  <link rel="stylesheet" type="text/css" href="../resources/css/style.css">
    <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="../resources/js/userRegistration.js"></script>
    <script type="text/javascript" src="../resources/js/selectDepartment.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Создание нового пользователя</h1>
        <br>
        <a href="../admin">Вернуться</a>
        <br>
        <h2><div id="result_line"></div></h2>
        <div class="main_block">
           <div class="field">
               <label>Фамилия</label>
               <input type="text" id="surname" name="userSurname" size="40" required/>
           </div>
           <div class="field">
               <label>Имя</label>
               <input type="text" id="firstname" name="userFirstname" size="40" required/>
           </div>
           <div class="field">
               <label>Должность</label>
               <input type="text" id="position" name="position" size="40" required/>
           </div>
           <div class="field">
               <label>Почтовый адрес</label>
               <input type="email" id="email" name="email" size="40" required/>
           </div>
           <div class="field">
               <label>Логин</label>
               <input type="text" id="username" name="username" size="40" required/>
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
               <label>Убрать права</label>
               <input type="checkbox" id="resetId" checked />
           </div>
           <div class="field">
               <label>Просмотр записей</label>
               <input type="checkbox" id="readerId" />
           </div>
           <div class="field">
               <label>Внесение записей</label>
               <input type="checkbox" id="editorId" />
           </div>
           <div class="field">
               <label>Учет термоконтейнеров</label>
               <input type="checkbox" id="accountId" />
           </div>
           <input type="hidden" id="user_rights" name="rights" value="" />
           <br>
           <div class="field">
               <label>Дать права администратора</label>
               <input type="checkbox" id="roleId" />
           </div>
           <input type="hidden" id="user_role" name="role" />
           <br>
           <div class="field">
               <label></label>
               <button id="btn_add_user">Зарегистрировать</button>
           </div>

        </div>
     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
            $('#select_company').trigger("change");
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
