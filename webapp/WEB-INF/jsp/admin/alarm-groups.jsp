<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>User rights changer page</title>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/userRightsEditor.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="/logout">Выйти</a>
        </div>
        <hr>
        <h1>Редактирование групп оповещения</h1>
        <br>
        <a href="/admin">Вернуться</a>
        <br>
        <h2><div id="result_line"></div></h2>
        <div class="main_block">
            <div class="field">
                <label>Группа оповещения</label>
                <select id="select_alarm_group">
                    <option value=-1>Выберите группу оповещения</option>
                    <c:forEach var="alarmGroup" items="${alarmGroups}">
                        <option value=${alarmGroup.id}>${alarmGroup.alarmGroupName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field" id="alarm_name_row" style="display: block">
                <label>Новое название</label>
                <input type="text" id="alarmGroupName" size="40" required/>
            </div>
            <div class="title_row" style="margin-left: 120px">
                <input type="button" id="btn_alarm" style="width: 110px; margin-left: 40px" value="Создать" />
                <input type="hidden" id="btn_del_alarm" style="width: 110px; margin-left: 40px" value="Удалить" />
            </div>
            <br>

            <div class="field">
                <label>Пользователь</label>
                <input type="text" id="user_name" size="40" placeholder="Первые три буквы фамилии" required/>
            </div>
            <input type="hidden" id="user_id" name="userId" value="0"/>
            <div class="field" id="show_select" style="display: none; ">
                <label style="color: blue;" >Кликните пользователя</label>
                <select id="select_user">
                </select>
            </div>
            <br>
                <div class="title_row" style="margin-left: 120px">
                    <input type="button" id="btn_add_user" style="width: 110px; margin-left: 40px" value="Добавить" />
                    <input type="button" id="btn_remove_user" style="width: 110px; margin-left: 40px" value="Убрать" />
                </div>
        </div>

        <p>
        <br>
        <button id="btn_alarm_users" style="margin-left: 120px" >Вывести список</button>
        </p>
        <div id="show_alarm_table" style="display:none;">
            <div class="cut_line" id="line_cut_alarm" >Скрыть список оповещения</div>
            <table border ="1">
                <caption><strong>Список лиц оповещения</strong></caption>
                <thead>
                    <th>Логин</th>
                    <th>Фамилия, имя</th>
                    <th>Должность</th>
                    <th>Email</th>
                </thead>
                <tbody class="table_body" id="alarm_users_body">
                </tbody>
            </table>
        </div>
     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
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
