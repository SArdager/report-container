<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Company editor page</title>
  <link rel="stylesheet" type="text/css" href="../resources/css/style.css">
    <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="../resources/js/showNote.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <a style="margin-top: 4px;" href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Просмотр событий</h1>
        <br>
        <a href="../admin">Вернуться</a>
        <br>
        <h3><div id="result_line"></div></h3>
        <form id="export_logs" action="../admin/load-data/logs-exportExcel" method="post">
            <div class="main_block">
                <div class="field">
                    <label>Событие</label>
                    <select id="select_event" name="eventId" >
                        <option value=-1>Выберите событие</option>
                        <option value= 1>Изменение времени доставки</option>
                        <option value= 2>Изменение прав доступа</option>
                    </select>
                </div>
            </div>
            <br>
            <div class="title_row" >
                <span class="date_line">Вывести за период:</span>
                <input type="date" id="startDate" name="startDate"/>
                <input type="date" id="endDate" name="endDate"/>
                <span class="text_line">по</span>
                <input type="number" id="pageSize" min="5" step="10" value="30"/>
                <span class="text_line">строк</span>
            </div>
            <input type="hidden" id="totalLogs" value="0"/>
            <div class="title_row">
                <span class="date_line">Вывести по филиалу:</span>
                <select id="select_branch" name="branchId">
                    <option value="0">Все филиалы</option>
                    <c:forEach var="branch" items="${branches}">
                        <option value=${branch.id}>${branch.branchName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="title_row" style="justify-content: left;">
                <div class="title_row" style="width: 30%; justify-content: space-between; margin-right: 0.5em">
                    <span id="reload_logs" class ="reload_line">Обновить</span>
                    <img src="../resources/images/export_excel_48.png" id="btn_export_log" alt="">
                </div>
                <div id="logs_pages_title" style="margin-left: 30px;"></div>
            </div>
        </form>
        <div class = "scroll_table">
           <table>
             <caption><strong>История событий: <u><span id="logs_name"></span></u></strong></caption>
             <thead>
               <tr>
                 <th>Id</th>
                 <th>Дата</th>
                 <th>Филиал</th>
                 <th>Отдел</th>
                 <th id="target"></th>
                 <th>Было</th>
                 <th>Стало</th>
                 <th>Кто внес изменения</th>
               </tr>
             </thead>
               <tbody id = "logs_table_body">
               </tbody>
           </table>
        </div>
        <br>
     </div>
     <div class="buffer" style = "height: 5em;"></div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
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
