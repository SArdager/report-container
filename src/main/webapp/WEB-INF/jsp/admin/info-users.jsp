<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Users information page</title>
  <link rel="stylesheet" type="text/css" href="../resources/css/style.css">
    <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="../resources/js/showUserRights.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Информация о пользователях</h1>
        <br>
        <a href="../admin">Вернуться</a>
        <br>
        <h3><div id="result_line"></div></h3>
        <p>
        <div class="main_block">
           <div class="field">
               <label>Поиск по фамилии</label>
               <input type="text" id="search_surname" size="40" placeholder="Любые буквы фамилии" />
           </div>
           <div class="field">
               <label>Поиск по имени</label>
               <input type="text" id="search_firstname" size="40" placeholder="Любые буквы имени" />
           </div>
           <div class="field">
               <label>Поиск по логину</label>
               <input type="text" id="search_login" size="40" placeholder="Любые буквы логина" />
           </div>
           <div class="field">
               <label>Поиск по филиалу</label>
               <select id="select_branch">
                    <option value="1">По всем филиалам</option>
                    <c:forEach var="branch" items="${branches}">
                        <option value=${branch.id}>${branch.branchName}</option>
                    </c:forEach>
                </select>
           </div>
           <div class="field">
               <label>Поиск по объекту</label>
               <select id="select_department">
                    <option value="1">По всем объектам</option>
                </select>
           </div>
        <br>
           <div class="field">
               <label></label>
                <button id="btn_find_user" >Найти</button>
           </div>
        </div>
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
                    <div class="title_row" style="font-weight: bold; margin-left: 20px;">
                        Права пользователя:
                        <div class="color_name" id="userLogin" style="margin-left: 20px;"></div>
                    </div>
                </caption>
                    <thead tabindex="0">
                        <th>Объект (наименование, филиал)</th>
                        <th>Права по объекту</th>
                    </thead>
                    <tbody class="table_body" id="rights_table_body">
                    </tbody>
                </table>
            </div>
        </div>
     </div>
     <div class="buffer" style = "height: 5em;"></div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
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
            $('#line_cut_table').on('click', function(){
               document.getElementById("show_table").style.display = "none";
            });
            $('#line_cut_rights').on('click', function(){
               document.getElementById("show_rights").style.display = "none";
            });
        });
    </script>

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>
