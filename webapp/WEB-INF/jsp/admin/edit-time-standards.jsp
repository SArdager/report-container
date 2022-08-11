<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Time standards editor page</title>
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
        <h1>Редактирование норматива доставки</h1>
        <br>
        <a href="/admin">Вернуться</a>
        <br>
        <h2><div id="result_line"></div></h2>
        <div class="main_block">
                <div class="field">
                    <label><b>Пункт отправки</b></label>
                    <select id="select_first_branch" >
                            <c:forEach var="branch" items="${branches}">
                            <option value=${branch.id}>${branch.branchName}</option>
                            </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label>Выберите объект</label>
                        <select id="select_first_department" >
                            <c:forEach var="department" items="${firstDepartments}">
                            <option value=${department.id}>${department.departmentName}</option>
                            </c:forEach>
                        </select>
                </div>
                <div class="field">
                    <label><b>Пункт доставки</b></label>
                    <select id="select_second_branch" >
                            <c:forEach var="branch" items="${branches}">
                            <option value=${branch.id}>${branch.branchName}</option>
                            </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label>Выберите объект</label>
                        <select id="select_second_department" >
                            <c:forEach var="department" items="${secondDepartments}">
                            <option value=${department.id}>${department.departmentName}</option>
                            </c:forEach>
                        </select>
                </div>
                <div class="field">
                    <label><b>Вид пробы</b></label>
                    <select id="select_time_probe" >
                            <c:forEach var="probe" items="${probes}">
                                <option value=${probe.id}>${probe.probeName}</option>
                            </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label><b>Время доставки</b> (часов):</label>
                    <input type="text" id="time_standard" size="40" value=0 required/>
                </div>
            <p>
                <input type="hidden" id="standard_id" />
                <input type="button" id="btn_standard" style="width: 110px; margin-left: 120px" value="Внести" />
            </p>
        </div>

     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
            $('#select_first_branch').trigger("change");
            $('#select_second_branch').trigger("change");
            $('#standard_id').val(0);
            var resultLineValue;
            window.addEventListener("click", function(){
                resultLineValue = $('#result_line').text();
                if(resultLineValue.length>0){
                    $('#result_line').text("");
                }
            });
       });
    </script>

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>
