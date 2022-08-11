<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Users information page</title>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/showUserRights.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="/logout">Выйти</a>
        </div>
        <hr>
        <h1>Информация о пользователях</h1>
        <br>
        <a href="/admin">Вернуться</a>
        <br>
        <h2><div id="result_line"></div></h2>
        <br>

        <div class="main_block">
           <div class="field">
               <label>Поиск по фамилии</label>
               <input type="text" id="search_surname" size="40" placeholder="Любые буквы фамилии" required/>
           </div>
           <div class="field">
               <label>Поиск по имени</label>
               <input type="text" id="search_firstname" size="40" placeholder="Любые буквы имени" required/>
           </div>
        </div>
        <p>
        <br>
        <button id="btn_find_user" style="margin-left: 150px" >Найти</button>
        </p>
        <div id="show_table" style="display:none;">
            <div class="cut_line" id="line_cut_table" >Скрыть таблицу пользователей</div>
            <table border ="1">
                <caption><strong>Результаты поиска</strong></caption>
                <span style="font-size: 0.8em; text-decoration: underline; margin-left: 20px;">Для просмотра прав пользователя кликните по его логину</span>
                <thead>
                    <th>Логин</th>
                    <th>Фамилия, имя</th>
                    <th>Должность</th>
                    <th>Email</th>
                    <th>Куратор</th>
                    <th>Активность</th>
                    <th>Роль</th>
                </thead>
                <tbody class="table_body" id="users_table_body">
                </tbody>
            </table>
            <br>
            <div id="show_rights" style="display:none;">
                <div class="cut_line" id="line_cut_rights" >Скрыть права пользователя</div>
                <table border ="1">
                <caption>
                    <div class="title_row" style="font-weight: bold; margin-left: 20px">
                        Права пользователя:
                        <div class="color_name" id="userLogin" ></div>
                    </div>
                </caption>
                    <thead>
                        <th>Объект (наименование, филиал)</th>
                        <th>Права по объекту</th>
                    </thead>
                    <tbody class="table_body" id="rights_table_body">
                    </tbody>
                </table>
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
        var line_cut_table = document.getElementById("line_cut_table");
        var show_table = document.getElementById("show_table");
        $('#line_cut_table').on('click', function(){
           show_table.style.display = "none";
        });
        var line_cut_rights = document.getElementById("line_cut_rights");
        var show_rights = document.getElementById("show_rights");
        $('#line_cut_rights').on('click', function(){
           show_rights.style.display = "none";
        });
        $('#btn_find_user').on('click', function(event){
            $.ajax({
                url: '/admin/find-user',
                method: 'POST',
                dataType: 'json',
                data: {surname: $('#search_surname').val(), firstname: $('#search_firstname').val()},
                success: function(users) {
                    var new_lines_html ='';
                    var body = $('#users_table_body');
                    body.html('');
                    show_table.style.display = "block";
                    $.each(users, function(key, user){
                        if(!$.isArray(users)|| !users.length){
                            $('#result_line').html("Указанный в запросе пользователь отсутствует в базе.");
                        } else {
                            new_lines_html+="<tr><td style='color: blue; text-decoration: underline'>"+ user.username + "</td><td>" +
                                user.userSurname + " " + user.userFirstname + "</td><td>" +
                                user.position + "</td><td>" + user.email + "</td><td>" +
                                user.curatorName + "</td><td>" + user.isEnabled + "</td><td>" +
                                user.role + "</td></tr>";
                        }
                    });
                    body.prepend(new_lines_html);
                },
                error:  function(response) {
                    $('#result_line').html("Для получения информации о правах пользователя кликните по ячейке с логином.");
                }
            });
        });
    </script>

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>
