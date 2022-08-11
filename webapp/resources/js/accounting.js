$(document).ready(function(){

    $('#btn_check_new').on('click', function(){
        var new_number = $('#new_number').val();
        var dateValue = new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString().slice(0, -3);
        if(new_number.length > 0){
            $.ajax({
                url: '/user/check-container/new-container',
                method: 'POST',
                dataType: 'text',
                data: {containerNumber: $('#new_number').val(),
                    valueId: $('#select_value').val(),  date: dateValue},
                success: function(message) {
                    $('#result_line').html(message);
                    $('#new_number').val("");
                    $('#new_number').focus();
                },
                error:  function(response) {
                    $('#result_line').html("Ошибка регистрации термоконтейнера. Повторите.");
                    $('#new_number').val("");
                    $('#new_number').focus();
                }
            });
        } else {
            $('#result_line').html("Внесите номер термоконтейнера");
            $('#new_number').focus();
        }
    });

    $('#btn_send').on('click', function(){
        var send_number = $('#send_number').val();
        var dateValue = new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString().slice(0, -3);

        if(send_number.length > 0){
            var validTime = /^[0-9]+$/;
            if(validTime.test($('#time_standard').val()) && $('#time_standard').val()>0){
                $.ajax({
                    url: '/user/check-container/send-container',
                    method: 'POST',
                    dataType: 'text',
                    data: {containerNumber: $('#send_number').val(), departmentId: $('#select_department').val(),
                         timeStandard: $('#time_standard').val(), date: dateValue},
                    success: function(message) {
                        if(message.indexOf("уже")>0){
                            var x = confirm(message);
                            if(x){
                                $.ajax({
                                    url: '/user/check-container/resend-container',
                                    method: 'POST',
                                    dataType: 'text',
                                    data: {containerNumber: $('#send_number').val(), departmentId: $('#select_department').val(),
                                         timeStandard: $('#time_standard').val(), date: dateValue},
                                    success: function(message) {
                                        $('#result_line').html(message);
                                        $('#send_number').val("");
                                        $('#send_number').focus();
                                        },
                                    error:  function(response) {
                                        $('#result_line').html("Ошибка регистрации отгрузки термоконтейнера. Повторите.");
                                        $('#send_number').val("");
                                        $('#send_number').focus();
                                    }
                                });
                            }
                        } else {
                         $('#result_line').html(message);
                         $('#send_number').val("");
                         $('#send_number').focus();
                        }
                    },
                    error:  function(response) {
                        $('#result_line').html("Ошибка регистрации отгрузки термоконтейнера. Повторите.");
                        $('#send_number').val("");
                        $('#send_number').focus();
                    }
                });
            } else {
                $('#result_line').html("Время доставки должно состоять только из цифр (без пробелов и десятичных значений)");
            }
        } else {
            $('#result_line').html("Внесите номер термоконтейнера");
            $('#new_number').focus();
        }
    });

    $('#btn_find').on('click', function(){
        var validFindNumber = /^[0-9]+$/;
        var find_html = "";
        $('#find_table_body').html("");
        if(validFindNumber.test($('#find_number').val())){
            $.ajax({
                url: '/user/check-container/find-container',
                method: 'POST',
                dataType: 'json',
                data: {findNumber: $('#find_number').val()},
                success: function(note) {
                    $('#result_line').html("Получены сведения о местонахождении контейнера");
                    find_html = "<table border = '1'><thead><th>Номер термоконтейнера</th><th>Объект последней регистрации</th>" +
                        "<th>Время регистрации</th><th>Принял термоконтейнер</th><th>Получен из объекта</th><th>Статус</th>" +
                        "</thead><tbody class='table_body'><tr tabindex='2'><td>" + note.containerNumber + "</td><td>" +
                        note.toDepartment + "</td><td>" + note.arriveTime + "</td><td>" + note.toUser +
                        "</td><td>" + note.outDepartment + "</td>";
                    if(note.isSend){
                        find_html+= "<td>Отправлен на другой объект</td>";
                    } else {
                        find_html+= "<td>Находится на объекте</td>";
                    }
                    find_html+= "<tr></tbody></table>";
                    $('#find_table_body').prepend(find_html);
                    $("tr[tabindex=2]").focus();
                },
                error:  function(response) {
                    $('#result_line').html("Ошибка поиска по базе. Повторите.");
                }
            });
        } else {
            $('#result_line').html("Номера должны состоять только из цифр (без пробелов и десятичных значений )");
        }
    });

    $('#btn_search').on('click', function(){
        var search_html = "";
        $('#search_table_body').html("");
        var selectedBranchId = $('#select_branch_search').val();
        $.ajax({
            url: '/user/check-container/search-container',
            method: 'POST',
            dataType: 'json',
            data: {branchId: selectedBranchId},
            success: function(containers) {
                $('#result_line').html("Получены сведения о наличии контейнеров по филиалу (филиалам)");
                search_html = "<table border = '1'><thead><th>Название объекта</th><th>Номер термоконтейнера</th>" +
                    "<th>Характеристика</th><th>Начало эксплуатации</th></thead><tbody class='table_body'>";
                search_html+= "<tr tabindex='3'><td colspan='4' style='color: blue;'><b>" + containers[0].branchName + "</b></td></tr>";
                if(selectedBranchId>1){
                    $.each(containers, function(key, container){
                        search_html += "<tr><td>" + container.departmentName + "</td><td>" + container.containerNumber + "</td><td>" +
                               container.value  + "</td><td>" + container.registrationDate + "</td></tr>";
                    });
                } else {
                    var currentId = containers[0].branchId;
                    for(var i=0; i<containers.length; i++){
                       var nextId = containers[i].branchId;
                      if(currentId===nextId){
                            search_html += "<tr><td>" + containers[i].departmentName + "</td><td>" + containers[i].containerNumber + "</td><td>" +
                               containers[i].value  + "</td><td>" + containers[i].registrationDate + "</td></tr>";
                       } else {
                            currentId=nextId;
                            search_html+= "<tr tabindex='3'><td colspan='4' style='color: blue;'><b>" + containers[i].branchName + "</b></td></tr>";
                            i--;
                        }
                    }
                }
                $('#search_table_body').prepend(search_html);
                $("tr[tabindex=3]").focus();
            },
            error:  function(response) {
                $('#result_line').html("Ошибка поиска по базе. Повторите.");
            }
        });
    });

    $('#btn_print').on('click', function(){
        var validNumber = /^[0-9]+$/;
        if(validNumber.test($('#start_number').val()) && validNumber.test($('#end_number').val())){
            if($('#end_number').val()>$('#start_number').val()){
                $.ajax({
                    url: '/user/check-container/print-code',
                    method: 'POST',
                    dataType: 'text',
                    data: {startNumber: $('#start_number').val(), endNumber: $('#end_number').val()},
                    success: function(message) {
                        $('#result_line').html(message);
                    },
                    error:  function(response) {
                        $('#result_line').html("Ошибка регистрации термоконтейнера. Повторите.");
                    }
                });
            } else {
                $('#result_line').html("Второй номер должен быть больше начального номера");
            }
        } else {
            $('#result_line').html("Номера должны состоять только из цифр (без пробелов и десятичных значений)");
        }
    });

    var check_new_area = document.getElementById("check_new_area");
    $('#check_new').on('click', function(){
        check_new_area.style.display = "block";
        $('#new_number').focus();
    });
    $('#clean_check_new').on('click', function(){
        check_new_area.style.display = "none";
    });
    $('#clean_input_new').click(function(){
        $('#new_number').val("");
        $('#new_number').focus();
    });

    var send_area = document.getElementById("send_area");
    $('#send_container').on('click', function(){
        send_area.style.display = "block";
        $('#send_number').focus();
    });
    $('#clean_send').on('click', function(){
        send_area.style.display = "none";
    });
    $('#clean_input_send').click(function(){
        $('#send_number').val("");
        $('#send_number').focus();
    });

    var find_area = document.getElementById("find_area");
    $('#find_container').on('click', function(){
        find_area.style.display = "block";
        $('#find_table_body').html("");
        $('#find_number').focus();
    });
    $('#clean_find').on('click', function(){
        find_area.style.display = "none";
    });
    $('#clean_input_find').click(function(){
        $('#find_number').val("");
        $('#find_table_body').html("");
        $('#find_number').focus();
    });

    var search_area = document.getElementById("search_area");
    $('#search_containers').on('click', function(){
        search_area.style.display = "block";
        $('#search_table_body').html("");
    });
    $('#clean_search').on('click', function(){
        search_area.style.display = "none";
    });

    var print_area = document.getElementById("print_area");

    $('#print_container').on('click', function(){
        print_area.style.display = "block";
    });
    $('#clean_print').on('click', function(){
        print_area.style.display = "none";
    });

    $('#select_branch').on('change', function(){
        $.ajax({
            url: '/user/change-department/select-branch',
            method: 'POST',
            dataType: 'json',
            data: {branchId: $('#select_branch').val()},
            success: function(departments) {
                $('#select_department').empty();
                $.each(departments, function(key, department){
                    $('#select_department').append('<option value="' + department.id + '">' + department.departmentName + '</option');
                });
                $('#send_number').focus();
            },
            error:  function(response) {
                alert("Ошибка обращения в базу данных. Повторите.");
            }
        });
    });



});
