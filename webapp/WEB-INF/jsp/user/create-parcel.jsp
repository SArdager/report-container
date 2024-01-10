<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>Create parcel page</title>
  <script type="text/javascript" src="../resources/js/jquery-3.6.0.min.js"></script>
  <script type="text/javascript" src="../resources/js/parcelCreator.js"></script>
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
        <h1>Создание и отправка почтового отправления</h1>
        <br>
        <a class="link_line" href="../work-starter">Вернуться</a>
        <a class="link_line" href="check-parcel">Отслеживание почтового отправления</a>
        <br>
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
        <hr>
        <h3><div id="result_line"></div></h3>
        <input type="hidden" id="departmentId" value="${department.id}" />

        <table>
            <tr>
                <td class="table_title">Вид отправления</td>
                <td><select id="select_parcel">
                        $('#select_parcel').append('<option value="0">Выберите вид отправления</option>');
                        $('#select_parcel').append('<option value="1">Документы</option>');
                        $('#select_parcel').append('<option value="2">Реагенты</option>');
                        $('#select_parcel').append('<option value="3">Материалы</option>');
                        $('#select_parcel').append('<option value="4">Запасные части к оборудованию</option>');
                        $('#select_parcel').append('<option value="5">Компьютерное оборудование, комплектующие</option>');
                    </select>
                </td>
            </tr>
            <tr>
                <td class="table_title">Получатель</td>
                <td><select id="select_company">
                        <c:forEach var="company" items="${companies}">
                          <option value=${company.id}>${company.companyName}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><select id="select_branch">
                    </select>
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><select id="select_department">
                    </select><br>
                </td>
            </tr>
            <tr>
                <td class="table_title">Габариты (кроме документов), см</td>
                <td><input type="text" id="dimensions" /></td>
            </tr>
            <tr>
                <td class="table_title">Сопроводительная запись</td>
                <td><br><textarea id="note"></textarea></td>
            </tr>
            <tr>
                <td class="table_title">Присылать уведомления по маршруту</td>
                <td><input type="checkbox" id="information" /></td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><button id="btn_parcel">Создать</button></td>
            </tr>
        </table><br>
        <span id="reload_parcel" class ="cut_line">Обновить</span><br>
        <table border ="1" width="400">
            <caption><strong>Подготовлены к отгрузке:</strong></caption>
            <thead>
                <th>№ посылки</th>
                <th>Конечный получатель</th>
                <th>Габариты</th>
                <th>Вложена в посылку</th>
            </thead>
            <tbody class="table_body" id="parcels_table_body">
            </tbody>
        </table>
        <br><hr>
        <table>
            <tr>
                <td class="table_title">Номер посылки</td>
                <td><input type="text" id="parcel_number" value="" maxlength="8"/>
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><button id="btn_prt">Штрих-код</button></td>
            </tr>
            <tr>
                <td class="table_title">Отправитель</td>
                <td><select id="select_change_branch">
                      <c:forEach var="branch" items="${branches}">
                        <option value=${branch.id}>${branch.branchName}</option>
                      </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td class="table_title"><span id="memory_department" class="float_line">Запомнить</span></td>
                <td><select id="select_change_department">
                    </select><br>
                </td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><button id="btn_send" style="display: block;">Отгрузить</button></td>
            </tr>
            <tr>
                <td id="show_assembly" colspan='2' class ="cut_line">Комплектовать (разукомплектовать) посылку</td>
            </tr>
            <tr id="parcel_tr" style="display: none;">
                <td class="table_title">Номер посылки</td>
                <td><input type="text" id="add_number" value="" maxlength="8"/>
                </td>
            </tr>
            <tr id="add_tr" style="display: none;">
                <td class="table_title">Вложить в посылку</td>
                <td><input type="text" id="parent_number" maxlength="8" />
                </td>
            </tr>
            <tr id="costs_tr" style="display: none;">
                <td class="table_title">Доля оплаты доставки</td>
                <td><input type="number" id="costs_part" value="0" step="10" /> %</td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><button id="btn_add" style="display: none;">Вложить</button></td>
            </tr>
            <tr>
                <td class="table_title"></td>
                <td><button id="btn_remove" style="display: none;">Выложить</button></td>
            </tr>
        </table>
     </div>
  </section>

  <script>
      $(document).ready(function(){
          $("h1").css("color", "blue");
          let name = "${user.userFirstname}";
          document.getElementById("user_name").textContent = name.substring(0, 1) + ". ${user.userSurname}";
          $('#select_company').trigger("change");
          let mem_department_id = "${memoryDepartmentId}";
          if(mem_department_id>1){
              $('#select_change_branch').val("${memoryBranchId}");
              $.ajax({
                  url: '../user/change-department/select-branch',
                  method: 'POST',
                  dataType: 'json',
                  data: {branchId: "${memoryBranchId}"},
                  success: function(departments) {
                      $('#select_change_department').empty();
                      $.each(departments, function(key, department){
                          $('#select_change_department').append('<option value="' + department.id + '">' + department.departmentName + '</option');
                      });
                      $('#select_change_department').val(mem_department_id);
                  },
                  error:  function(response) {
                      $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                  }
              });
          } else {
              $('#select_change_branch').trigger("change");
          }
          let resultLineValue;
          let clickNumber = 0;
          window.addEventListener("click", function(){
              clickNumber++;
              resultLineValue = $('#result_line').text();
              if(clickNumber==0){
                  $('#result_line').text("");
                  $('#clean_input').trigger("click");
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
