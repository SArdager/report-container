<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Values editor page</title>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/valuesEditor.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="/logout">Выйти</a>
        </div>
        <hr>
        <h1>Редактирование видов проб и термоконтейнеров</h1>
        <br>
        <a href="/admin">Вернуться</a>
        <br>
        <h2><div id="result_line"></div></h2>
        <div class="main_block">
                <div class="field">
                    <label>Виды проб</label>
                    <select id="select_probe" >
                        <option value=-1>Создать новый вид пробы</option>
                        <c:forEach var="probe" items="${probes}">
                            <option value=${probe.id}>${probe.probeName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label>Новое название</label>
                    <input type="text" id="probe_name" size="40" required/>
                </div>
            <div class="title_row" style="margin-left: 105px">
                <input type="button" id="btn_probe" style="width: 110px; margin-left: 40px" value="Создать" />
                <input type="hidden" id="btn_del_probe" style="width: 110px; margin-left: 40px" value="Удалить" />
            </div>
            <br>
            <br>
                <div class="field">
                    <label>Термоконтейнеры</label>
                    <select id="select_value" >
                        <option value=-1>Создать новый вид термоконтейнера</option>
                        <c:forEach var="containerValue" items="${containerValues}">
                            <option value=${containerValue.id}>${containerValue.valueName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label>Новое название</label>
                    <input type="text" id="value_name" size="40" required/>
                </div>
            <div class="title_row" style="margin-left: 105px">
                <input type="button" id="btn_value" style="width: 110px; margin-left: 40px" value="Создать" />
                <input type="hidden" id="btn_del_value" style="width: 110px; margin-left: 40px" value="Удалить" />
            </div>
            <br>
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

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>
