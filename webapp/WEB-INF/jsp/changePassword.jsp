<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Change password page</title>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/passwordChanger.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <h1>Смена пароля</h1>
        <h2><div id="result_line"></div></h2>
        <br>
        <div class="main_block">
          <form:form id="change_password" action="/changePassword/change" method="post">
            <div class="field">
                <label for="login">Новый пароль</label>
                <input type="password" id="password" name="password" size="40" autofocus="true" required/>
            </div>
            <br>
          </form:form>
            <div class="field">
                <label for="login">Повторите пароль</label>
                <input type="password" id="confirm_password" size="40" autofocus="true" required/>
                <br>
                <br>
                <button id="btn_change_password">Сменить пароль</button>
            </div>
        </div>
     </div>
  </section>
    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
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