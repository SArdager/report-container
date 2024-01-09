<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>User rights changer page</title>
  <link rel="stylesheet" type="text/css" href="../resources/css/style.css">
    <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="../resources/js/addRightsEditor.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <span id="user_name"></span>
            <a href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Изменение прав работника лаборатории</h1>
        <br>
        <a class="link_line" href="../work-starter">Вернуться</a>
        <br>
        <h3><div id="result_line"></div></h3>
        <p>
            <div class="title_row">
                <div class="title_name">Наименование объекта:</div>
                <div class="color_text"> ${department.departmentName},  ${department.branch.branchName}</div>
            </div>
            <div class="title_row">
                <div class="title_name">Права пользователя</div>
                <div id="userRights" class="color_text">${userRights.rights}</div>
            </div>
        </p>
        <div class="main_block">
            <div class="field">
                <label>Объект</label>
                <select id="select_department" name="departmentId">
                    <c:forEach var="department" items="${departments}">
                        <option value=${department.id}>${department.departmentName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label>Пользователь</label>
                <input type="text" id="find_name" size="40" placeholder="Первые буквы фамилии" required/>
            </div>
            <div class="field">
                <label></label>
                <span id="user_clean" class="cut_line">Очистить поле ввода имени</span>
            </div>
            <input type="hidden" id="user_id" name="userId" value="0"/>
            <div class="field" id="show_select" style="display: block">
                <label style="color: blue;" >Выберите из списка</label>
                <select id="select_user">
                    <option value="0">Кликните пользователя</option>
                    <c:forEach var="user" items="${users}">
                        <option value=${user.id}>${user.userSurname}  ${user.userFirstname}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field" id="show_short_select" style="display: none">
                <label style="color: blue;" >Выберите из списка</label>
                <select id="select_short">
                </select>
            </div>
            <br>
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
                <input type="radio" id="editorId" name="rights" value="courier"/>
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
                <label>Создание и отслеживание посылок</label>
                <input type="radio" id="creatorId" name="rights" value="creator"/>
            </div>
            <br>
        </div>
        <p>
        <br>
        <button id="btn_rights" style="margin-left: 120px" >Изменить права</button>
        </p>
        <p>
        <br>
        <button id="btn_del" style="margin-left: 120px" >Удалить полностью</button>
        </p>

     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            let name = "${user.userFirstname}";
            document.getElementById("user_name").textContent = name.substring(0, 1) + ". ${user.userSurname}";
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
