<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Termocontainer accounting page</title>
  <link rel="stylesheet" type="text/css" href="${contextPath}/resources/css/style.css">
    <script type="text/javascript" src="${contextPath}/resources/js/jquery-3.6.0.min.js"></script>
    <script type="text/javascript" src="${contextPath}/resources/js/accounting.js"></script>

</head>

<body>
  <section>
     <div class="container">
        <div class="user_title">
            <strong style="margin-top: 4px; margin-right: 20px">Пользователь: ${user.userFirstname} ${user.userSurname}</strong>
            <a style="margin-top: 4px;" href="/logout">Выйти</a>
        </div>
        <hr>
        <h1>Учет термоконтейнеров</h1>
        <br>
        <a href="/work-starter">Вернуться</a>
        <br>
        <h2><div id="result_line"></div></h2>
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
        <br>
        <p>
        <h3 id="check_new"><u>Регистрация новых термоконтейнеров</u></h3>
        <div class="main_block" id="check_new_area" style="display: none">
            <div class="cut_line" id="clean_check_new">Скрыть поле регистрации новых термоконтейнеров</div>
            <div class="field">
                <label>Сканирование номера</label>
                <input type="text" id="new_number" maxlength="13"/>
                <div id="clean_input_new" style ="font-size: 0.9em; text-decoration: underline;">Очистить поле ввода</div>
            </div>
            <div class="field">
                <label>Выбрать вид</label>
                <select id="select_value" >
                    <c:forEach var="containerValue" items="${containerValues}">
                        <option value=${containerValue.id}>${containerValue.valueName}</option>
                    </c:forEach>
                </select>
           </div>
            <div class="field">
                <label></label>
                <button id="btn_check_new">Зарегистрировать</button>
           </div>
            <hr>
            <br>
        </div>
        </p>
        <p>
        <h3 id="send_container"><u>Передача термоконтейнера в филиал</u></h3>
        <div class="main_block" id="send_area" style="display: none">
            <div class="cut_line" id="clean_send">Скрыть поле передачи термоконтейнеров</div>
            <div class="field">
                <label>Сканирование номера</label>
                <input type="text" id="send_number" maxlength="13"/>
                <div id="clean_input_send" style ="font-size: 0.9em; text-decoration: underline;">Очистить поле отправки</div>
            </div>
            <div class="field">
                <label>Получатель</label>
                <select id="select_branch">
                    <c:forEach var="branch" items="${branches}">
                    <option value=${branch.id}>${branch.branchName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label></label>
                <select id="select_department">
                </select>
            </div>
            <div class="field">
                <label>Срок доставки (часов)</label>
                <input type="number" id="time_standard" value="120" />
            </div>
            <div class="field">
                <label></label>
                <button id="btn_send" >Передать</button>
            </div>
            <hr>
            <br>
        </div>
        </p>
        <p>
        <h3 id="find_container"><u>Поиск термоконтейнера по номеру</u></h3>
        <div class="main_block" id="find_area" style="display: none">
            <div class="cut_line" id="clean_find">Скрыть поле поиска термоконтейнера</div>
            <div class="field">
                <label>Номер термоконтейнера</label>
                <input type="text" id="find_number" maxlength="13"/>
                <div id="clean_input_find" style ="font-size: 0.9em; text-decoration: underline;">Очистить поле поиска</div>
                <button id="btn_find" style="margin-left: 180px">Найти</button>
           </div>
            <div id="find_table_body"></div>
            <hr>
            <br>
        </div>
        </p>
        <p>
        <h3 id="search_containers"><u>Наличие термоконтейнеров по филиалам</u></h3>
        <div class="main_block" id="search_area" style="display: none">
            <div class="cut_line" id="clean_search">Скрыть поле поиска термоконтейнеров</div>
            <div class="field">
                <label>Выбор филиала</label>
                <select id="select_branch_search">
                    <option value="1">По всем филиалам</option>
                    <c:forEach var="branch" items="${branches}">
                        <option value=${branch.id}>${branch.branchName}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="field">
                <label></label>
                <button id="btn_search">Показать</button>
            </div>
            <div id="search_table_body"></div>
            <hr>
            <br>
        </div>
        <div id="table_area" style="display: none">
            <br>
            <table border ="1">
                <caption><strong>Информация по термоконтейнерам</strong></caption>
                <thead>
                    <th>Номер термоконтейнера</th>
                    <th>Филиал нахождения</th>
                    <th>Объект нахождения</th>
                    <th>Дата прибытия</th>
                </thead>
                <tbody class="table_body" id="container_table_body">
                </tbody>
            </table>
        </div>
        </p>
        <p>
        <h3 id="print_container"><u>Печать штрих-кодов термоконтейнеров</u></h3>
        <div class="main_block" id="print_area" style="display: none">
            <div class="cut_line" id="clean_print">Скрыть поле печати штрих-кодов</div>
            <div class="field">
                <label>Начать с номера</label>
                <input type="text" id="start_number" maxlength="12"/>
            </div>
            <div class="field">
                <label>До номера (включительно)</label>
                <input type="text" id="end_number" maxlength="12"/>
            </div>
            <div class="field">
                <label></label>
                <button id="btn_print" >Вывести</button>
            </div>
            <hr>
        </div>
        </p>
     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            $("h2").css("color", "red");
            $('#select_branch').trigger("change");

            $('#select_value').change(function(){$('#new_number').focus();});
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
