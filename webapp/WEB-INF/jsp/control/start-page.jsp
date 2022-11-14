<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Control start page</title>
    <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
    <script>
        var w = Number(window.innerWidth);
        var h = Number(window.innerHeight);
        if (h>w) {
          $('head').append('<link rel="stylesheet" type="text/css" href="../resources/css/mobileStyle.css">');
        } else {
          $('head').append('<link rel="stylesheet" type="text/css" href="../resources/css/style.css">');
        }
    </script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>ПОЛУЧЕНИЕ ОТЧЕТОВ</h1>
        <br>
        <h2>Выбор операции</h2>
        <h4><a href="pay">Отчет по оплатам за перевозку</a></h4>
        <h4><a href="delay">Отчет по задержкам транспортировки</a></h4>
        <h4><a href="route">Отчет по использованию термоконтейнера</a></h4>
        <br>
        <a href="../work-starter">Главная</a>


     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
       });
    </script>

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>



