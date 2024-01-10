<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Setup Departments page</title>
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
        <h1>НАСТРОЙКА СПИСКА ПОЛУЧАТЕЛЕЙ</h1>
        <br>
        <a class="link_line" href="check-out">Вернуться</a>
        <br>
        <h3><div id="result_line"></div></h3>
        <div class="title_row">
            <div class="title_name"><b>Текущий объект:</b></div>
            <div id="department_name" class="color_text"> ${department.departmentName},  ${department.branch.branchName}</div>
        </div>
        <hr>
        <h5>Выберите отражаемые филиалы и отделы</h5>
        <div class="wrapper">
            <div class="half_page">
                <table class="choice_table">
                    <thead>
                        <tr>
                            <th class="name_cell">Филиал</th>
                            <th class="box_cell">
                                <input type="checkbox" id="all_branches" />
                            </th>
                        </tr>
                    <thead>
                        <tbody id="branches_table_body">
                        </tbody>
                </table>
            </div>
            <div class="half_page">
                <table class="choice_table">
                    <thead>
                        <tr>
                            <th class="name_cell">Объект</th>
                            <th class="box_cell">
                                <input type="checkbox" id="all_departments" />
                            </th>
                        </tr>
                    <thead>
                        <tbody id="departments_table_body">
                        </tbody>
                </table>
                <br>
                <div id="save_dep" class="cut_line" style="display: none">Сохранить отмеченные объекты (с зеленым цветом будет в верхней строке)</div>
            </div>
        </div>

        <br><button class="btn_margin" id="btn_save">Сохранить</button>
     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            let name = "${user.userFirstname}";
            document.getElementById("user_name").textContent = name.substring(0, 1) + ". ${user.userSurname}";
            let resultLineValue;
            let clickNumber = 0;
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
    <script type="text/javascript" src="../resources/js/setupEditor.js"></script>
    <div class="buffer" style = "height: 5em;"></div>
</body>
</html>