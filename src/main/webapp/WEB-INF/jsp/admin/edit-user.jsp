<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>User editor page</title>
  <link rel="stylesheet" type="text/css" href="../resources/css/style.css">
    <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="../resources/js/userEditor.js"></script>
    <script type="text/javascript" src="../resources/js/showUserRights.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Редактирование (в т.ч. удаление или восстановление) пользователя</h1>
        <br>
        <a href="../admin">Вернуться</a>
        <br>
        <h3><div id="result_line"></div></h3>
        <div class="main_block">
           <div class="field">
               <label>Пользователь</label>
               <input type="text" id="user_name" size="40" placeholder="Первые три буквы фамилии" required/>
           </div>
           <input type="hidden" id="user_id" value="0"/>
           <div class="field" id="show_select" style="display: none; ">
               <label></label>
               <select id="select_user">
               </select>
           </div>
           <br>
           <div class="field">
               <label>Фамилия</label>
               <input type="text" id="surname" size="40" required/>
           </div>
           <div class="field">
               <label>Имя</label>
               <input type="text" id="firstname" size="40" required/>
           </div>
           <div class="field">
               <label>Должность</label>
               <input type="text" id="position" size="40" required/>
           </div>
           <div class="field">
               <label>Почтовый адрес</label>
               <input type="email" id="email" size="40" required/>
           </div>
           <div class="field">
               <label>Логин</label>
               <input type="text" id="username" size="40" required/>
           </div>
            <br>
            <div class="field">
                <label>Заблокировать пользователя</label>
                <input type="checkbox" id="isNotEnabled" />
            </div>
            <div class="field">
                <label>Разблокировать пользователя</label>
                <input type="checkbox" id="isEnabled" checked ="checked" />
            </div>
            <input type="hidden" id="is_enabled" value="true" />
            <div class="field">
                <label>Послать адрес сервера с новым паролем</label>
                <input type="checkbox" id="is_link_send" />
            </div>
        </div>
        <p>
        <br>
        <button id="btn_edit_user" style="margin-left: 130px" >Внести изменения</button>
        </p>
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

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>
