<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Change Department page</title>
    <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/selectDepartment.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <strong style="margin-top: 4px; margin-right: 20px">Пользователь: ${user.userFirstname} ${user.userSurname}</strong>
            <a style="margin-top: 4px;" href="/logout">Выйти</a>
        </div>
        <hr>
        <h1>ВЫБОР НОВОГО ОБЪЕКТА</h1>
        <br>
        <a href="/work-starter">Вернуться</a>
        <br>
        <h2 id="error-attention">${errorMessage}</h2>
        <div class="title_row">
            <strong>Текущий объект:</strong>
            <div id="department_name" style="margin-left: 10px; color: DarkBlue"><b> ${department.departmentName},  ${department.branch.branchName}</b></div>
        </div>
        <br>
        <div class="main_block">
            <div class="field>
                <span class = "title">Предприятие</span>
                <select id="select_company" >
                    <c:forEach var="company" items="${companies}">
                        <option value=${company.id}>${company.companyName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <span class = "title">Филиал</span>
                <select id="select_branch" >
                    <c:forEach var="branch" items="${branches}">
                        <option value=${branch.id}>${branch.branchName}</option>
                    </c:forEach>
                </select>
            </div>
            <form method="post" action="/user/change-department/choose-department">
            <div class="field">
                <span class = "title">Объект</span>
                <select id="select_department" name="departmentId" >
                    <c:forEach var="department" items="${departments}">
                        <option value=${department.id}>${department.departmentName}</option>
                    </c:forEach>
                </select>
            </div>
            <br>
            <p>
                <button type="submit" style="margin-left: 104px" >Выбрать</button>
            </p>
            </form>
        </div>
     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
            $('#select_company').trigger("change");
            var resultLineValue;
            window.addEventListener("click", function(){
                resultLineValue = $('#error-attention').text();
                if(resultLineValue.length>0){
                    $('#error-attention').text("");
                }
            });
       });
    </script>

    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>