<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Log-in page with your account</title>
    <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/passwordChanger.js"></script>
</head>

<body>
  <section>
      <div class="container">
        <sec:authorize access="isAuthenticated()">
            <% response.sendRedirect("/"); %>
        </sec:authorize>
        <form id="user_auth" method="post" action="/login">
            <h1>ЖУРНАЛЫ УЧЕТА ТЕРМОКОНТЕЙНЕРОВ</h1>
            <p>Для продолжения работы необходима авторизация пользователя</p>
            <br>
            <h2>Вход в систему</h2>
            <h3><div id="result_line"></div></h3>
            <div class="main_block">
                <div class="field">
                    <label>Логин</label>
                    <input type="text" id="login" name="username" size="40" autofocus="true" required/>
                </div>
                <br>
                <div class="field">
                    <label>Пароль</label>
                    <input type="password" id="password" name="password" size="40" required/>
                    <br>
                </div>
                <div class="field">
                    <label></label>
                    <button type="submit">Войти</button>
                </div>
            </div>
        </form>
        <br>
        <br>
        <p >
            <h3 id="forget_password" style="margin-left: 16px;">Забыли пароль?</h3>
            <div id="show_forget" style="display: none; margin-left: 16px;">
            <b>Пароль не восстанавливается!</b> <br>Если Вы забыли пароль, то потребуется его сбросить. <br>
            Временный пароль будет отправлен на Ваш адрес корпоративной электронной почты.<br>
            После авторизации по временному паролю система потребует ввода нового пароля.
            <br>
            <button id="btn_forget_password" style="margin-left: 150px;">Сбросить пароль</button>
            </div>
        </p>
      </div>
  </section>
    <script>
        $(document).ready(function(){
            $("h3").css("color", "blue");
            $("h3").css("text-decoration", "underline");
            var clickNumber = 0;
            var show_forget = document.getElementById("show_forget");
            $('#forget_password').on('click', function(){
                if(clickNumber==0){
                    show_forget.style.display = "block";
                    clickNumber++;
                } else{
                    show_forget.style.display = "none";
                    clickNumber=0;
                }
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