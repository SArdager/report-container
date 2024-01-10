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
            <span id="user_name"></span>
            <a href="../logout">Выйти</a>
        </div>
        <hr>
        <h1>Учет термоконтейнеров</h1>
        <br>
        <a href="../work-starter">Вернуться</a>
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
                <input type="hidden" id="userId" value=${user.id} />
            </div>
        </p>
        <br>
        <p>
        <h4 id="check_new">Регистрация (редактирование) термоконтейнеров</h4>
        <table id="check_new_area" style="display: none">
            <tr>
                <td colspan='2' class="cut_line" id="clean_check_new">Скрыть поле регистрации (редактирования) термоконтейнеров</td>
            </tr>
            <tr>
                <td class="table_title">Сканирование номера</td>
                <td><input type="text" id="new_number" maxlength="8"/></td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td id="clean_input_new" class="cut_line">Очистить поле ввода<br></td>
            </tr>
            <tr>
                <td class="table_title" id="many_new_numbers" style="text-decoration: underline;">Диапазон номеров</td>
                <td><input type="hidden" id="next_new_number" maxlength="8"/></td>
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
        <h4 id="send_container">Передача термоконтейнера в филиал</h4>
        <table id="send_area" style="display: none">
            <tr>
                <td colspan='2' class="cut_line" id="clean_send">Скрыть поле передачи термоконтейнеров</td>
            </tr>
            <tr>
                <td class="table_title">Номер термоконтейнера</td>
                <td><input type="text" id="send_number" maxlength="8"/></td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td id="clean_input_send" class="cut_line">Очистить поле ввода<br></td>
            </tr>
                <td class="table_title" id="many_send_numbers" style="text-decoration: underline;">Диапазон номеров</td>
                <td><input type="hidden" id="next_send_number" maxlength="8"/></td>
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
        <h4 id="check_value">Редактирование характеристик термоконтейнеров</h4>
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
        <h4 id="write_off_container">Списание термоконтейнера</h4>
        <table id="write_off_area" style="display: none">
            <tr>
                <td colspan='2' class="cut_line" id="clean_write_off">Скрыть поле списания термоконтейнера</td>
            </tr>
            <tr>
                <td class="table_title">Номер термоконтейнера</td>
                <td><input type="text" id="write_off_number" maxlength="8"/>
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td id="clean_input_write_off" class="cut_line">Очистить поле ввода<br></td>
            </tr>
            <tr >
                <td class="table_title"></td>
                <td id="td_check" style="display: table-row"><br><button id="btn_check" >Проверить</button><br></td>
                <td id="td_check_in" style="display: none"><br><button id="btn_check_in" >Принять</button><br></td>
                <td id="td_write_off" style="display: none"><br><button id="btn_write_off" >Списать</button><br></td>
            </tr>
        </table>
        </p>
        <p>
        <h4 id="find_container">Поиск термоконтейнера по номеру</h4>
        <div id="find_area" style="display: none">
            <table>
                <tr>
                    <td colspan='2' class="cut_line" id="clean_find">Скрыть поле поиска термоконтейнера</td>
                </tr>
                <tr>
                    <td class="table_title">Номер термоконтейнера</td>
                    <td><input type="text" id="find_number" maxlength="8"/>
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
            </table>
            <div id="find_table" class="table_body" style="display: none">
               <table>
                   <thead>
                       <tr>
                           <th>Номер</th>
                           <th>Место нахождения</th>
                           <th>Время регистрации</th>
                           <th>Принял термоконтейнер</th>
                           <th>Получен из объекта</th>
                           <th>Статус</th>
                       </tr>
                   </thead>
                   <tbody id="find_table_body">
                   </tbody>
               </table>
            </div>
        </div>
        </p>
        <p>
        <h4 id="search_containers">Нахождение термоконтейнеров по филиалам</h4>
        <div id="search_area" style="display: none">
            <table>
                <tr>
                    <td colspan='2' class="cut_line" id="clean_search">Скрыть поле поиска термоконтейнера</td>
                </tr>
                <tr>
                    <td class="table_title">Выбор филиала</td>
                    <td><select id="select_branch_search">
                           <option value="1">По всем филиалам</option>
                           <option value=${department.branch.id}>${department.branch.branchName}</option>
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
            </table>
            <div id="search_table" class="scroll_table" style="display: none">
               <table>
                   <thead>
                       <tr>
                           <th>Номер термоконтейнера</th>
                           <th>Название объекта</th>
                           <th>Характеристика</th>
                           <th>Начало эксплуатации</th>
                       </tr>
                   </thead>
                   <tbody id="search_table_body">
                   </tbody>
               </table>
            </div>
        </div>
        </p>
        <h4 id="used_containers">Неиспользуемые термоконтейнеры по филиалам</h4>
        <div id="used_area" style="display: none">
            <table>
                <tr>
                    <td colspan='2' class="cut_line" id="clean_used">Скрыть поле поиска термоконтейнера</td>
                </tr>
                <tr>
                    <td class="table_title">Выбор филиала</td>
                    <td><select id="select_branch_used">
                           <option value="1">По всем филиалам</option>
                           <option value=${department.branch.id}>${department.branch.branchName}</option>
                           <c:forEach var="branch" items="${branches}">
                               <option value=${branch.id}>${branch.branchName}</option>
                           </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class="table_title">Не используемые более</td>
                    <td><input id="days_used" type="number" style="width: 6em" value="45" min="30" step="15"/>
                        <span style="margin-left: 10dp">дней</span>
                    </td>
                </tr>
                <tr>
                    <td class="table_title"></td>
                    <td><br><button id="btn_used" >Показать</button><br></td>
                </tr>
            </table>
            <div id="used_table" class="scroll_table" style="display: none">
               <table>
                   <thead>
                       <tr>
                           <th>Номер термоконтейнера</th>
                           <th>Название объекта</th>
                           <th>Характеристика</th>
                           <th>Начало эксплуатации</th>
                       </tr>
                   </thead>
                   <tbody id="used_table_body">
                   </tbody>
               </table>
            </div>
        </div>
        </p>
        <div id="table_area" class = "scroll_table" style="display: none">
            <br>
            <div class="cut_line" id="clean_table">Скрыть поле перемещений термоконтейнера</div>
            <table>
                <caption><strong>Последние перемещения термоконтейнера № <u><span id="container_number"></span></u></strong></caption>
                <thead>
                  <tr>
                    <th>Отправитель</th>
                    <th>Дата отправки</th>
                    <th>Оформил отправку</th>
                    <th>Получатель</th>
                    <th>Дата приемки</th>
                    <th>Оформил приемку</th>
                  </tr>
                </thead>
                <tbody id = "container_table_body">
                </tbody>
            </table>
            <br>
        </div>
        <p>
        <h4 id="print_container">Создание штрих-кодов термоконтейнеров</h4>
        <table id="print_area" style="display: none">
            <tr>
                <td colspan='2' class="cut_line" id="clean_print">Скрыть поле печати штрих-кодов</td>
            </tr>
            <tr>
                <td colspan='2'>Рисунки штрих-кодов будут направлены на почтовый адрес</td>
            </tr>
            <tr>
                <td class="table_title">Начать с номера</td>
                <td><input type="text" id="start_number" maxlength="8"/></td>
            </tr>
            <tr>
                <td class="table_title">До номера (включительно)</td>
                <td><input type="text" id="end_number" maxlength="8"/></td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><br><button id="btn_print" >Создать</button></td>
            </tr>
        </table>
        </p>
     </div>
  </section>

    <script>
        $(document).ready(function(){
            $("h1").css("color", "blue");
            let name = "${user.userFirstname}";
            document.getElementById("user_name").textContent = name.substring(0, 1) + ". ${user.userSurname}";
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
