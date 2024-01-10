<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Change Department page</title>
  <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
  <script type="text/javascript" src="../resources/js/selectDepartment.js"></script>
  <script>
      var w = Number(window.innerWidth);
      var h = Number(window.innerHeight);
      if (h>w) {
        $('head').append('<link rel="stylesheet" type="text/css" href="../resources/css/mobileStyle.css">');
        $('head').append('<meta name="viewport" content="width=device-width, initial-scale=1.0">');
      } else {
        $('head').append('<link rel="stylesheet" type="text/css" href="../resources/css/style.css">');
      }
  </script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <span id="user_name"></span>
            <a href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>ВЫБОР НОВОГО ОБЪЕКТА</h1>
        <br>
        <a class="link_line" href="../work-starter">Вернуться</a>
        <br>
        <h3 id="error-attention">${errorMessage}</h3>
        <div class="title_row">
            <div class="title_name"><b>Текущий объект:</b></div>
            <div id="department_name" class="color_text"> ${department.departmentName},  ${department.branch.branchName}</div>
        </div>
        <br>
        <form method="post" action="../user/change-department/choose-department">
        <table>
            <tr>
                <td class="table_title">Предприятие</td>
                <td><select id="select_company" >
                       <c:forEach var="company" items="${companies}">
                           <option value=${company.id}>${company.companyName}</option>
                       </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="table_title">Филиал</td>
                <td><select id="select_branch" >
                       <c:forEach var="branch" items="${branches}">
                           <option value=${branch.id}>${branch.branchName}</option>
                       </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="table_title">Объект</td>
                <td><select id="select_department" name="departmentId" >
                       <c:forEach var="department" items="${departments}">
                           <option value=${department.id}>${department.departmentName}</option>
                       </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><br><button type="submit">Выбрать</button></td>
            </tr>
        </table>
        </form>
     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            let name = "${user.userFirstname}";
            document.getElementById("user_name").textContent = name.substring(0, 1) + ". ${user.userSurname}";
            $('#select_company').trigger("change");
            var resultLineValue;
            var clickNumber = 0;
            window.addEventListener("click", function(){
                clickNumber++;
                resultLineValue = $('#error-attention').text();
                if(clickNumber==0){
                    $('#error-attention').text("");
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