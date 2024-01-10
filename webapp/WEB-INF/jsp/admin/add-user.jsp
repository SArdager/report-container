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
        <h3><div id="result_line"></div></h3>
        <p>
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
                   <label>Выбор из списка</label>
                   <select id="select_position">
                        <option value="0">Кликните из списка</option>
                        <option value="1">Медицинская сестра</option>
                        <option value="2">Регистратор</option>
                        <option value="3">Старшая медицинская сестра</option>
                        <option value="4">Старший регистратор</option>
                        <option value="5">Водитель-курьер</option>
                        <option value="6">Заведующий лаборатории</option>
                        <option value="7">Администратор лаборатории</option>
                        <option value="8">Медицинский брат</option>
                        <option value="9">Регистратор ПК</option>
                        <option value="10">Заведующий складом</option>
                        <option value="10">Старший водитель-курьер</option>
                   </select>
               </div>
               <div class="field">
                   <label>Почтовый адрес</label>
                   <input type="email" id="email" name="email" size="40" required/>
               </div>
               <div class="field">
                   <label>Логин</label>
                   <input type="text" id="username" name="username" size="40" value="@kdlolymp.kz" required/>
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
                   <input type="radio" id="resetId" name="rights" value="reset" checked="checked"/>
               </div>
               <div class="field">
                   <label>Просмотр записей</label>
                   <input type="radio" id="readerId" name="rights" value="reader"/>
               </div>
               <div class="field">
                   <label>Внесение записей</label>
                   <input type="radio" id="editorId" name="rights" value="editor"/>
               </div>
               <div class="field">
                   <label>Курьер</label>
                   <input type="radio" id="editorId" name="rights" value="courier"/>
               </div>
               <div class="field">
                   <label>Просмотр записей и изменение срока доставки</label>
                   <input type="radio" id="changerId" name="rights" value="changer"/>
               </div>
               <div class="field">
                   <label>Просмотр записей и изменение прав</label>
                   <input type="radio" id="righterId" name="rights" value="righter"/>
               </div>
               <div class="field">
                   <label>Полные права по лаборатории</label>
                   <input type="radio" id="chefId" name="rights" value="chef"/>
               </div>
               <div class="field">
                   <label>Создание и отслеживание посылок</label>
                   <input type="radio" id="creatorId" name="rights" value="creator"/>
               </div>
               <div class="field">
                   <label>Учет термоконтейнеров</label>
                   <input type="radio" id="accountId" name="rights" value="account"/>
               </div>
               <br>
               <div class="field">
                   <label>Дать права администратора</label>
                   <input type="checkbox" id="roleId" />
               </div>
               <input type="hidden" id="user_role" name="role" />
            </div>
        </p>
        <br>
        <button id="btn_add_user" style="margin-left: 160px">Зарегистрировать</button>
        <br>
        <button id="btn_check_double" style="margin-left: 160px">Проверить дубли</button>
        <br>
        <div id="double_field" style="display:none;">
            <h4 id="clean_double_field" style="color: red;">Очистить поле таблицы дублей</h4>
            <table>
               <thead>
                   <tr>
                       <th>Фамилия</th>
                       <th>Имя</th>
                       <th>Id</th>
                       <th>Логин</th>
                       <th>Email</th>
                       <th>Филиал</th>
                   </tr>
               <thead>
                   <tbody id="double_users_table_body">
                   </tbody>
            </table>
        </div>
     </div>
     <div class="buffer" style = "height: 5em;"></div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
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

</body>
</html>
