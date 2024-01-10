<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>User rights changer page</title>
  <link rel="stylesheet" type="text/css" href="../resources/css/style.css">
    <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="../resources/js/userRightsEditor.js"></script>
    <script type="text/javascript" src="../resources/js/selectDepartment.js"></script>
</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Изменение прав пользователя</h1>
        <br>
        <a href="../admin">Вернуться</a>
        <br>
        <h3><div id="result_line"></div></h3>
        <div class="main_block">
            <div class="field">
                <label>Пользователь</label>
                <input type="text" id="user_name" size="40" placeholder="Первые три буквы фамилии" required/>
            </div>
            <span id="user_clean" class="cut_line">Очистить поле</span>
            <input type="hidden" id="user_id" name="userId" value="0"/>
            <input type="hidden" id="username"  value=""/>
            <div class="field" id="show_select" style="display: none; ">
                <label style="color: blue;" >Кликните пользователя</label>
                <select id="select_user">
                </select>
            </div>
            <br>
            <div class="field">
                <label>Предприятие</label>
                <select id="select_company">
                    <c:forEach var="company" items="${companies}">
                        <option value=${company.id}>${company.companyName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Филиал</label>
                <select id="select_branch">
                    <c:forEach var="branch" items="${branches}">
                        <option value=${branch.id}>${branch.branchName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Объект</label>
                <select id="select_department" name="departmentId">
                    <c:forEach var="department" items="${departments}">
                        <option value=${department.id}>${department.departmentName}</option>
                    </c:forEach>
                </select>
            </div>
            <u>Права доступа</u>
            <div class="field">
                <label>Нет прав (убрать права)</label>
                <input type="radio" id="resetId" name="rights" value="reset" checked="checked"/>
            </div>
            <div class="field">
                <label>Просмотр записей</label>
                <input type="radio" id="readerId" name="rights" value="reader"/>
            </div>
            <div class="field">
                <label>Внесение записей</label>
                <input type="radio" id="editorId" name="rights" value="editor"/>
            </div>
            <div class="field">
                <label>Курьер</label>
                <input type="radio" id="courierId" name="rights" value="courier"/>
            </div>
            <div class="field">
                <label>Просмотр записей и изменение срока доставки</label>
                <input type="radio" id="changerId" name="rights" value="changer"/>
            </div>
            <div class="field">
                <label>Просмотр записей и изменение прав</label>
                <input type="radio" id="righterId" name="rights" value="righter"/>
            </div>
            <div class="field">
                <label>Полные права по лаборатории</label>
                <input type="radio" id="chefId" name="rights" value="chef"/>
            </div>
            <div class="field">
                <label>Создание и отслеживание посылок</label>
                <input type="radio" id="creatorId" name="rights" value="creator"/>
            </div>
            <div class="field">
                <label>Учет термоконтейнеров</label>
                <input type="radio" id="accountId" name="rights" value="account"/>
            </div>
            <br>
            <div class="field">
                <label>Дать/снять права администратора</label>
                <input type="checkbox" id="roleId" />
            </div>
            <input type="hidden" id="user_role" name="role" />
            <br>
            <div class="field">
                <label>Групповое изменение права редактирования</label>
                <input type="checkbox" id="group_change_checkbox" />
            </div>
        </div>
        <div id="group_change_field" style="display:none;">
                <table style="width:320dp">
                    <thead>
                        <tr>
                            <th style="width:300dp">Объект</th>
                            <th style="width:20dp"></th>
                        </tr>
                    <thead>
                        <tbody id="departments_table_body">
                        </tbody>
                </table>
        </div>
        <p>
        <br>
        <button id="btn_rights" style="margin-left: 120px" >Изменить права</button>
        </p>
        <button id="btn_show_dep" style="margin-left: 120px" >Показать объекты</button>
        <div id="show_rights" style="display:none;">
            <div class="cut_line" id="line_cut_rights" >Скрыть таблицу</div>
            <table border ="1">
            <caption>
                <div class="title_row" style="font-weight: bold; margin-left: 20px;">
                    Права пользователя:
                    <div class="color_name" id="userLogin" style="margin-left: 20px;"></div>
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
        <br>
        <button id="btn_show_users" style="margin-left: 90px; width: 240px;" >Пользователи по филиалу</button>
        <div id="show_table" style="display:none;">
            <div class="cut_line" id="line_cut_table" >Скрыть таблицу пользователей</div>
            <table border ="1">
                <caption><strong>Результаты поиска</strong></caption>
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
