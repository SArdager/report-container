<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Edit alarm groups page</title>
  <link rel="stylesheet" type="text/css" href="../resources/css/style.css">
    <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="../resources/js/alarmGroup.js"></script>
</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Редактирование групп оповещения</h1>
        <br>
        <a href="../admin">Вернуться</a>
        <br>
        <h3><div id="result_line"></div></h3>
        <div class="main_block">
            <div class="field">
                <label>Группа оповещения</label>
                <select id="select_alarm_group">
                    <option value="-1">Выберите группу оповещения</option>
                    <option value="0">Создайте новую группу</option>
                    <c:forEach var="alarmGroup" items="${alarmGroups}">
                        <option value=${alarmGroup.id}>${alarmGroup.alarmGroupName}</option>
                    </c:forEach>
                </select>
            </div>
            <div id="name_field" style="display: none">
                <div class="field">
                    <label>по филиалу</label>
                    <select id="select_alarm_branch">
                        <option value="0">Выберите филиал</option>
                        <c:forEach var="branch" items="${branches}">
                            <option value=${branch.id}>${branch.branchName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label>Новое название</label>
                    <input type="text" id="alarmGroupName" />
                </div>
                <input type="hidden" id="alarm_branch" value="1" />
                <div class="title_row" style="margin-left: 120px">
                    <input type="button" class ="two_in_line" id="btn_alarm" value="Создать" />
                    <input type="hidden" class ="two_in_line" id="btn_del_alarm" value="Удалить" />
                </div>
            </div>
            <br><br>
            <div id="user_field" style="display: none;">
                <div class="field">
                    <label>Пользователь</label>
                    <input type="text" id="alarm_user_name" size="40" placeholder="Первые три буквы фамилии" required/>
                </div>
                <input type="hidden" id="user_id" name="userId" value="0"/>
                <br>
                <div class="title_row" style="margin-left: 120px">
                    <input type="button" class ="two_in_line" id="btn_clean_user" value="Очистить" />
                    <input type="button" class ="two_in_line" id="btn_all_user" value="Обновить список" />
                </div>
                <br>
                <div class="field" id="show_select" style="display: none; ">
                    <label></label>
                    <select id="select_alarm_user">
                    </select>
                </div>
                <br>
                <div class="title_row" style="margin-left: 120px">
                    <input type="button" class ="two_in_line" id="btn_add_user" value="Добавить" />
                    <input type="button" class ="two_in_line" id="btn_remove_user" value="Убрать" />
                </div>
            </div>
        </div>

        <p>
        <br>
        <input type="hidden" id="btn_alarm_users" style="margin-left: 150px" value="Вывести список" />
        </p>
        <div id="show_alarm_table" style="display:none;">
            <div class="cut_line" id="line_cut_alarm" >Скрыть список оповещения</div>
            <table border ="1">
                <caption>Список лиц оповещения</caption>
                <thead>
                    <th>Фамилия, имя</th>
                    <th>Должность</th>
                    <th>Email</th>
                </thead>
                <tbody class="table_body" id="alarm_users_body">
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
