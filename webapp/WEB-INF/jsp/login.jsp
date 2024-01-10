<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Log-in page with your account</title>
  <script type="text/javascript" src="resources/js/jquery-3.6.0.min.js"></script>
  <script type="text/javascript" src="resources/js/passwordChanger.js"></script>
  <script>
      var w = Number(window.innerWidth);
      var h = Number(window.innerHeight);
      if (h>w) {
        $('head').append('<link rel="stylesheet" type="text/css" href="resources/css/mobileStyle.css">');
        $('head').append('<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">');
      } else {
        $('head').append('<link rel="stylesheet" type="text/css" href="resources/css/style.css">');
      }
  </script>
</head>

<body>
  <section>
      <div class="container">
        <sec:authorize access="isAuthenticated()">
            <% response.sendRedirect("/"); %>
        </sec:authorize>
        <form id="user_auth" method="post" action="login">
            <h1>ЖУРНАЛЫ УЧЕТА ТЕРМОКОНТЕЙНЕРОВ</h1>
            <p>Для продолжения работы необходима авторизация пользователя</p>
            <br>
            <h2>Вход в систему</h2>
            <h3><div id="result_line"></div></h3>
            <table>
                <tr>
                    <td class="table_title" style="padding-left: 5%">Логин</td>
                    <td><input type="text" id="login" name="username" size="40" autofocus="true" required/></td>
                </tr>
                <tr>
                    <td class="table_title" style="padding-left: 5%">Пароль</td>
                    <td><input type="password" id="password" name="password" size="40" required/></td>
                </tr>
                <tr>
                    <td class="table_title"></td>
                    <td><button type="submit">Войти</button></td>
                </tr>
            </table>
        </form>
        <br>
        <br>
        <p >
            <h4 id="forget_password" style="margin-left: 16px;">Забыли пароль?</h4>
            <div id="show_forget" style="display: none; margin-left: 16px;">
                <b>Пароль не восстанавливается!</b> <br>Если Вы забыли пароль, то потребуется его сбросить. <br>
                Временный пароль будет отправлен на Ваш адрес электронной почты, указанный при регистрации.<br>
                После авторизации по временному паролю система потребует ввода нового пароля.
                <br>
                <button id="btn_forget_password" style="margin-left: 150px;">Сбросить пароль</button>
            </div>
        </p>
      </div>
  </section>
    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h4").css("color", "blue");
            var forgetNumber = 0;
            var show_forget = document.getElementById("show_forget");
            $('#forget_password').on('click', function(){
                if(forgetNumber==0){
                    show_forget.style.display = "block";
                    forgetNumber++;
                } else{
                    show_forget.style.display = "none";
                    forgetNumber=0;
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