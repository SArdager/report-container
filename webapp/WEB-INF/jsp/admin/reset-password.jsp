<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>User password reset page</title>
  <link rel="stylesheet" type="text/css" href="../resources/css/style.css">
    <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="../resources/js/userEditor.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Сброс пароля пользователя</h1>
        <br>
        <a href="../admin">Вернуться</a>
        <br>
        <h3><div id="result_line"></div></h3>
        <div class="main_block">
           <div class="field">
               <label>Пользователь</label>
               <input type="text" id="user_name" size="40" placeholder="Первые три буквы фамилии" required/>
           </div>
           <input type="hidden" id="user_id" name="userId" value="0"/>
           <br>
           <br>
           <div class="field" id="show_select" style="display: none; ">
               <label style="color: blue;" >Кликните пользователя</label>
               <select id="select_user">
               </select>
           </div>
           <br>
           <br>
           <div class="field">
               <label></label>
               <button id="btn_reset" style="width: 200px; display: block" >Сбросить пароль</button>
           </div>
        </div>
     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
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
