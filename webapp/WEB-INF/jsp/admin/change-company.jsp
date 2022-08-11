<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Company editor page</title>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/companyEditor.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="/logout">Выйти</a>
        </div>
        <hr>
        <h1>Изменение структуры компании</h1>
        <br>
        <a href="/admin">Вернуться</a>
        <br>
        <h2><div id="result_line"></div></h2>
        <div class="main_block">
            <form:form modelAttribute="companyForm" id="change_company" method="post" action="/admin/change-company/company">
                <div class="field">
                    <label>Предприятие</label>
                    <select id="select_company" name="id" >
                        <option value=-1>Создать новое предприятие</option>
                        <c:forEach var="company" items="${companies}">
                            <option value=${company.id}>${company.companyName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label>Новое название</label>
                    <input type="text" id="companyName" name="companyName" size="40" required/>
                </div>
            <div class="title_row" style="margin-left: 90px">
                <input type="button" id="btn_company" style="width: 110px; margin-left: 40px" value="Создать" />
                <input type="hidden" id="btn_del_company" style="width: 110px; margin-left: 40px" value="Удалить" />
            </div>
            </form:form>
            <br>
            <br>
                <div class="field">
                    <label>Филиал</label>
                    <select id="select_branch" name="id">
                        <option value=-1>Выберите предприятие</option>
                        <c:forEach var="branch" items="${branches}">
                            <option value=${branch.id}>${branch.branchName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label>Новое название</label>
                    <input type="text" id="branchName" name="branchName" size="40" required/>
                </div>
            <div class="title_row" style="margin-left: 90px">
                <input type="hidden" id="btn_branch" style="width: 110px; margin-left: 40px" value="Создать" />
                <input type="hidden" id="btn_del_branch" style="width: 110px; margin-left: 40px" value="Удалить" />
            </div>
            <br>
            <br>
                <div class="field">
                    <label>Объект</label>
                    <select id="select_department" name="id">
                        <option value=-1>Выберите филиал</option>
                        <c:forEach var="department" items="${departments}">
                            <option value=${department.id}>${department.departmentName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="field">
                    <label>Новое название</label>
                    <input type="text" id="departmentName" name="departmentName" size="40" required/>
                </div>
            <div class="title_row" style="margin-left: 90px">
                <input type="hidden" id="btn_department" style="width: 110px; margin-left: 40px" value="Создать" />
                <input type="hidden" id="btn_del_department" style="width: 110px; margin-left: 40px" value="Удалить" />
            </div>
        </div>

     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
            var result_line = document.getElementById('result_line');
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
