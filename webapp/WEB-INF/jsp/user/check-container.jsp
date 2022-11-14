<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Termocontainer accounting page</title>
  <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
  <script type="text/javascript" src="../resources/js/accounting.js"></script>
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
            <strong style="margin-top: 4px; margin-right: 20px">Пользователь: ${user.userFirstname} ${user.userSurname}</strong>
            <a style="margin-top: 4px;" href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Учет термоконтейнеров</h1>
        <br>
        <a href="../work-starter">Вернуться</a>
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
        <h3 id="check_new"><u>Регистрация (редактирование) термоконтейнеров</u></h3>
        <table id="check_new_area" style="display: none">
            <tr>
                <td colspan='2' class="cut_line" id="clean_check_new">Скрыть поле регистрации (редактирования) термоконтейнеров</td>
            </tr>
            <tr>
                <td class="table_title">Сканирование номера</td>
                <td><input type="text" id="new_number" maxlength="13"/></td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td id="clean_input_new" class="cut_line">Очистить поле ввода<br></td>
            </tr>
            <tr>
                <td class="table_title">Выбрать вид</td>
                <td><select id="select_value">
                      <c:forEach var="containerValue" items="${containerValues}">
                        <option value=${containerValue.id}>${containerValue.valueName}</option>
                      </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><br><button id="btn_check_new" >Зарегистрировать</button><br></td>
            </tr>
        </table>
        </p>
        <p>
        <h3 id="send_container"><u>Передача термоконтейнера в филиал</u></h3>
        <table id="send_area" style="display: none">
            <tr>
                <td colspan='2' class="cut_line" id="clean_send">Скрыть поле передачи термоконтейнеров</td>
            </tr>
            <tr>
                <td class="table_title">Сканирование номера</td>
                <td><input type="text" id="send_number" maxlength="13"/></td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td id="clean_input_send" class="cut_line">Очистить поле ввода<br></td>
            </tr>
            <tr>
                <td class="table_title">Получатель</td>
                <td><select id="select_branch">
                    <c:forEach var="branch" items="${branches}">
                    <option value=${branch.id}>${branch.branchName}</option>
                    </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><select id="select_department">
                    </select>
                </td>
            </tr>
            <tr>
                <td class="table_title">Срок доставки (часов)</td>
                <td><input type="number" id="time_standard" value="120" />
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><br><button id="btn_send" >Передать</button><br></td>
            </tr>
        </table>
        </p>
        <p>
        <h3 id="check_value"><u>Редактирование характеристик термоконтейнеров</u></h3>
        <table id="check_value_area" style="display: none">
            <tr>
                <td colspan='2' class="cut_line" id="clean_check_value">Скрыть редактирования характеристик термоконтейнеров</td>
            </tr>
            <tr>
                <td class="table_title">Термоконтейнеры</td>
                <td><select id="select_container_value" >
                    <option value=-1>Создать новый вид термоконтейнера</option>
                      <c:forEach var="containerValue" items="${containerValues}">
                        <option value=${containerValue.id}>${containerValue.valueName}</option>
                      </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="table_title">Новое название</td>
                <td><input type="text" id="value_name" size="40" required/>
                </td>
            </tr>
            <tr>
                <td colspan='2' class="two_in_line">
                    <input type="button" class ="two_in_line" id="btn_value" value="Создать" />
                    <input type="hidden" class ="two_in_line" id="btn_del_value" value="Удалить" />
                <br></td>
            </tr>
        </table>
        </p>
        <p>
        <h3 id="write_off_container"><u>Списание термоконтейнера</u></h3>
        <table id="write_off_area" style="display: none">
            <tr>
                <td colspan='2' class="cut_line" id="clean_write_off">Скрыть поле списания термоконтейнера</td>
            </tr>
            <tr>
                <td class="table_title">Номер термоконтейнера</td>
                <td><input type="text" id="write_off_number" maxlength="13"/>
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td id="clean_input_write_off" class="cut_line">Очистить поле ввода<br></td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><br><button id="btn_write_off" >Списать</button><br></td>
            </tr>
        </table>
        </p>
        <p>
        <h3 id="find_container"><u>Поиск термоконтейнера по номеру</u></h3>
        <table id="find_area" style="display: none">
            <tr>
                <td colspan='2' class="cut_line" id="clean_find">Скрыть поле поиска термоконтейнера</td>
            </tr>
            <tr>
                <td class="table_title">Номер термоконтейнера</td>
                <td><input type="text" id="find_number" maxlength="13"/>
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td id="clean_input_find" class="cut_line">Очистить поле ввода<br></td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><br><button id="btn_find" >Найти</button><br></td>
            </tr>
            <tr>
                <td colspan='2' id="find_table_body"></td>
            </tr>
        </table>
        </p>
        <p>
        <h3 id="search_containers"><u>Наличие термоконтейнеров по филиалам</u></h3>
        <table id="search_area" style="display: none">
            <tr>
                <td colspan='2' class="cut_line" id="clean_search">Скрыть поле поиска термоконтейнера</td>
            </tr>
            <tr>
                <td class="table_title">Выбор филиала</td>
                <td><select id="select_branch_search">
                       <option value="1">По всем филиалам</option>
                       <c:forEach var="branch" items="${branches}">
                           <option value=${branch.id}>${branch.branchName}</option>
                       </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><br><button id="btn_search" >Показать</button><br></td>
            </tr>
            <tr>
                <td colspan='2' id="search_table_body"></td>
            </tr>
        </table>
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
        <table id="print_area" style="display: none">
            <tr>
                <td colspan='2' class="cut_line" id="clean_print">Скрыть поле печати штрих-кодов</td>
            </tr>
            <tr>
                <td class="table_title">Начать с номера</td>
                <td><input type="text" id="start_number" maxlength="12"/></td>
            </tr>
            <tr>
                <td class="table_title">До номера (включительно)</td>
                <td><input type="text" id="end_number" maxlength="12"/></td>
            </tr>
            <tr>
                <td class="table_title">Путь к папке</td>
                <td><input type="text" id="path_barcode" /></td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><br><button id="btn_print" >Вывести</button><br></td>
            </tr>
        </table>
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
