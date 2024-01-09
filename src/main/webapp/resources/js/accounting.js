$(document).ready(function(){

    $('#btn_check_new').on('click', function(){
        let new_number = $('#new_number').val();
        let next_new_number = $('#next_new_number').val();
        if(new_number.length == 8){
            if(next_new_number.length == 8){
                let first_new = new_number.substring(0,5);
                let second_new = next_new_number.substring(0,5);
                if(first_new == second_new){
                    checkContainers($('#new_number').val(), $('#next_new_number').val(), $('#select_value').val());
                } else {
                    $('#result_line').html("Не совпадают первые пять чисел номеров термоконтейнеров");
                }
            } else if(next_new_number.length<8 && next_new_number.length>0) {
                $('#result_line').html("Второе значение из диапазона номеров термоконтейнеров содержит менее восьми знаков");
            } else {
                checkContainers($('#new_number').val(), "0", $('#select_value').val());
            }
        } else {
            $('#result_line').html("Внесите номер термоконтейнера");
            $('#new_number').focus();
        }
    });

    function checkContainers(firstContainerNumber, secondContainerNumber, containerValueId){
        $('#btn_check_new').css("display", "none");
        $.ajax({
            url: '../user/check-container/new-container',
            method: 'POST',
            dataType: 'text',
            data: {containerNumber: firstContainerNumber, nextContainerNumber: secondContainerNumber,
                valueId: containerValueId},
            success: function(message) {
                if(message.indexOf("зарегистрирован")>0){
                    var x = confirm(message);
                    if(x){
                        $.ajax({
                            url: '../user/check-container/edit-container',
                            method: 'POST',
                            dataType: 'text',
                            data: {containerNumber: $('#new_number').val(), valueId: $('#select_value').val()},
                            success: function(message) {
                                $('#result_line').html(message);
                                $('#new_number').val("");
                                $('#next_new_number').val("");
                                $('#new_number').focus();
                            },
                            error:  function(response) {
                                window.scrollTo({ top: 0, behavior: 'smooth' });
                                $('#result_line').html("Ошибка редактирования термоконтейнера. Перегрузите страницу.");
                            }
                        });
                    }
                } else {
                    $('#result_line').html(message);
                    $('#new_number').val("");
                    $('#next_new_number').val("");
                    $('#new_number').focus();
                }
                $('#btn_check_new').css("display", "block");
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка регистрации термоконтейнера. Перегрузите страницу.");
                $('#btn_check_new').css("display", "block");
            }
        });
    }

    $('#btn_value').on('click', function(){
        var valueName = $('#value_name').val();
        if(valueName.length>1){
            $('#btn_value').css("display", "none");
            $.ajax({
                url: '../user/edit-container/edit-values',
                method: 'POST',
                dataType: 'text',
                data: {id: $('#select_container_value').val(), valueName: valueName},
                success: function(message) {
                    $('#btn_value').css("display", "block");
                    $('#result_line').html(message);
                    setTimeout(() => { document.location.href = '../user/check-container';}, 800);
               },
                error:  function(response) {
                    $('#btn_value').css("display", "block");
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
        } else {
            $('#result_line').html("Напишите новое название");
        }
    });

    $('#btn_del_value').on('click', function(){
        var valueName = $('#select_container_value option:selected').text();
        $('#btn_del_value').css("display", "none");
        $.ajax({
            url: '../user/edit-container/delete-value',
            method: 'POST',
            dataType: 'text',
            data: {id: $('#select_container_value').val()},
            success: function(message) {
                $('#btn_del_value').css("display", "block");
                $('#result_line').html(message);
                setTimeout(() => { document.location.href = '../user/check-container';}, 1000);
            },
            error:  function(response) {
                $('#btn_del_value').css("display", "block");
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

    $('#select_container_value').on('change', function(){
        var btn_value = document.getElementById("btn_value");
        var btn_del_value = document.getElementById("btn_del_value");
        if($('#select_container_value').val()>0){
            btn_value.value = "Изменить";
            btn_del_value.type = "button";
        } else{
            $('#value_name').val("");
            btn_value.value = "Создать";
            btn_del_value.type = "hidden";
        }
    });

    $('#btn_check').on('click', function(){
        var write_off_number = $('#write_off_number').val();
        if(write_off_number.length == 8){
            $('#td_check').css("display", "none");
            $.ajax({
                url: '../user/check-container/check-place',
                method: 'POST',
                dataType: 'text',
                data: {containerNumber: $('#write_off_number').val()},
                success: function(message) {
                    if(message.indexOf("Принять")==0){
                        $('#result_line').html("Для списания необходимо принять термоконтейнер.");
                        $('#td_check_in').css("display", "block");
                    } else if(message.indexOf("Удалить")==0){
                        $('#result_line').html("Термоконтейнер можно удалить.");
                        $('#td_write_off').css("display", "block");
                        $('#btn_write_off').html(message);
                    } else if(message.indexOf("Списать")==0){
                        $('#result_line').html("Термоконтейнер готов к списанию.");
                        $('#td_write_off').css("display", "block");
                        $('#btn_write_off').html(message);
                    } else {
                        $('#td_check').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html(message);                    }
                },
                error:  function(response) {
                    $('#td_check').css("display", "block");
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка проверки термоконтейнера. Перегрузите страницу.");
                }
            });
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });            $('#result_line').html("Проверьте правильность ввода номера термоконтейнера");
            $('#result_line').html("Проверьте правильность ввода номера термоконтейнера");
        }
    });

    $('#btn_check_in').on('click', function(){
        var write_off_number = $('#write_off_number').val();
        if(write_off_number.length == 8){
            $('#td_check_in').css("display", "none");
            $.ajax({
                url: '../user/check-container/check-in',
                method: 'POST',
                dataType: 'text',
                data: {containerNumber: $('#write_off_number').val()},
                success: function(message) {
                    if(message.indexOf("Списать")==0){
                        $('#result_line').html("Термоконтейнер готов к списанию.");
                        $('#td_write_off').css("display", "block");
                        $('#btn_write_off').html(message);
                    } else {
                        $('#td_check_in').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html(message);
                    }                },
                error:  function(response) {
                    $('#td_check_in').css("display", "block");
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка проверки термоконтейнера. Перегрузите страницу.");
                }
            });
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });            $('#result_line').html("Проверьте правильность ввода номера термоконтейнера");
            $('#result_line').html("Проверьте правильность ввода номера термоконтейнера");
        }
    });

    $('#btn_write_off').on('click', function(){
        var write_off_number = $('#write_off_number').val();
        if(write_off_number.length == 8){
            $('#td_write_off').css("display", "none");
            $.ajax({
                url: '../user/check-container/write-off-container',
                method: 'POST',
                dataType: 'text',
                data: {containerNumber: $('#write_off_number').val()},
                success: function(message) {
                    $('#td_check').css("display", "block");
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html(message);
                    $('#write_off_number').val("");
                },
                error:  function(response) {
                    $('#td_write_off').css("display", "block");
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка списания термоконтейнера. Перегрузите страницу.");
                }
            });
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Проверьте правильность ввода номера термоконтейнера");
        }
    });

    $('#btn_send').on('click', function(){
        var send_number = $('#send_number').val();
        var next_send_number = $('#next_send_number').val();

        if(send_number.length == 8){
            var validTime = /^[0-9]+$/;
            if(validTime.test($('#time_standard').val()) && $('#time_standard').val()>0){
                if(next_send_number.length == 8){
                    let first_new = send_number.substring(0,5);
                    let second_new = next_send_number.substring(0,5);
                    if(first_new == second_new){
                        sendContainers($('#send_number').val(), $('#next_send_number').val(), $('#select_department').val(), $('#time_standard').val());
                    } else {
                        $('#result_line').html("Не совпадают первые пять чисел номеров термоконтейнеров");
                    }
                } else if(next_send_number.length<8 && next_send_number.length>0) {
                    $('#result_line').html("Второе значение из диапазона номеров термоконтейнеров содержит менее восьми знаков");
                } else {
                    sendContainers($('#send_number').val(), "0", $('#select_department').val(), $('#time_standard').val());
                }
            } else {
                $('#result_line').html("Время доставки должно состоять только из цифр (без пробелов и десятичных значений)");
            }
        } else {
            $('#result_line').html("Внесите номер термоконтейнера длиной восумь знаков");
            $('#new_number').focus();
        }
    });

    function sendContainers(firstContainerNumber, secondContainerNumber, depId, time){
        $('#btn_send').css("display", "none");
        $.ajax({
            url: '../user/check-container/send-container',
            method: 'POST',
            dataType: 'text',
            data: {containerNumber: firstContainerNumber, nextContainerNumber: secondContainerNumber,
                departmentId: depId, timeStandard: time},
            success: function(message) {
                if(message.indexOf("уже")>0){
                    var x = confirm(message);
                    if(x){
                        $.ajax({
                            url: '../user/check-container/resend-container',
                            method: 'POST',
                            dataType: 'text',
                            data: {containerNumber: $('#send_number').val(), departmentId: $('#select_department').val(),
                                 timeStandard: $('#time_standard').val()},
                            success: function(message) {
                                $('#result_line').html(message);
                                $('#send_number').val("");
                                $('#next_send_number').val("");
                                $('#send_number').focus();
                                },
                            error:  function(response) {
                                window.scrollTo({ top: 0, behavior: 'smooth' });
                                $('#result_line').html("Ошибка регистрации отгрузки термоконтейнера. Перегрузите страницу.");
                                $('#send_number').val("");
                                $('#next_send_number').val("");
                                $('#send_number').focus();
                            }
                        });
                    }
                } else {
                 $('#result_line').html(message);
                 $('#send_number').val("");
                 $('#next_send_number').val("");
                 $('#send_number').focus();
                }
                $('#btn_send').css("display", "block");
            },
            error:  function(response) {
                $('#btn_send').css("display", "block");
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка регистрации отгрузки термоконтейнера. Перегрузите страницу.");
                $('#send_number').val("");
                $('#send_number').focus();
            }
        });
    }

    $('#btn_find').on('click', function(){
        var validFindNumber = /^[0-9]+$/;
        var find_html = "";
        $('#find_table_body').html("");
        if(validFindNumber.test($('#find_number').val())){
            if($('#find_number').val().length==8){
            $('#btn_find').css("display", "none");
            $.ajax({
                url: '../user/check-container/find-container-place',
                method: 'POST',
                dataType: 'json',
                data: {findNumber: $('#find_number').val()},
                success: function(note) {
                    $('#find_table').css("display", "block");
                    let toDepartment = note.toDepartment;
                    if(toDepartment.length>0){
                        find_html = "<tr tabindex='2'><td style='color: blue; text-decoration: underline; text-align: center;'>" + note.containerNumber + "</td><td>" +
                            note.toDepartment + "</td><td>" + note.arriveTime + "</td><td>" + note.toUser + "</td><td>" + note.outDepartment + "</td>";
                        let userName = note.toUser;
                        if(userName.length>0){
                            find_html+= "<td>Находится на объекте</td></tr>";
                        } else {
                            find_html+= "<td>В дороге</td></tr>";
                        }
                    } else {
                        find_html = "<tr tabindex='2'><td style='color: blue; text-decoration: underline; text-align: center;'>" + note.containerNumber + "</td><td>" +
                            note.outDepartment + "</td><td></td><td></td><td></td><td>Находится на объекте</td></tr>";
                    }
                    $('#find_table_body').prepend(find_html);
                    $('#btn_find').css("display", "block");
                },
                error:  function(response) {
                    $('#btn_find').css("display", "block");
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка поиска по базе. Перегрузите страницу.");
                }
            });
            } else {
                $('#result_line').html("Прверьте правильности ввода номера термоконтейнера. Количество чисел должно быть равным 8.");
            }
        } else {
            $('#result_line').html("Номера должны состоять только из цифр (без пробелов и десятичных значений)");
        }
    });

    $('#btn_search').on('click', function(){
        let search_html = "";
        $('#search_table_body').html("");
        let selectedBranchId = $('#select_branch_search').val();
        $('#btn_search').css("display", "none");
        $.ajax({
            url: '../user/check-container/search-container',
            method: 'POST',
            dataType: 'json',
            data: {branchId: selectedBranchId},
            success: function(containers) {
                $('#search_table').css("display", "block");
                search_html = "";
                if(selectedBranchId>1){
                    if(containers[0]!=null){
                        search_html+= "<tr tabindex='3'><td colspan='4' style='color: blue;'><b>" + containers[0].branchName + "</b></td></tr>";
                        $.each(containers, function(key, container){
                            search_html += "<tr><td style='color: blue; text-decoration: underline; text-align: center;'>" + container.containerNumber + "</td><td>" + container.departmentName +
                                    "</td><td>" + container.value  + "</td><td>" + container.registrationDate + "</td></tr>";
                        });
                    } else {
                        search_html+= "<tr tabindex='3'><td colspan='4' '><b>Отсутствуют термоконтенеры</b></td></tr>";
                    }
                } else {
                    search_html = "<tr tabindex='3'><td colspan='4' style='color: blue;'><b>" + containers[0].branchName + "</b></td></tr>";
                    var currentId = containers[0].branchId;
                    for(var i=0; i<containers.length; i++){
                        var nextId = containers[i].branchId;
                        if(currentId===nextId){
                            search_html += "<tr><td style='color: blue; text-decoration: underline; text-align: center;'>" + containers[i].containerNumber + "</td><td>" + containers[i].departmentName +
                                "</td><td>" + containers[i].value  + "</td><td>" + containers[i].registrationDate + "</td></tr>";
                        } else {
                            currentId=nextId;
                            search_html+= "<tr><td colspan='4' style='color: blue;'><b>" + containers[i].branchName + "</b></td></tr>";
                            i--;
                        }
                    }
                }
                $('#search_table_body').prepend(search_html);
                $("tr[tabindex=3]").focus();
                $('#btn_search').css("display", "block");
            },
            error:  function(response) {
                $('#btn_search').css("display", "block");
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка поиска по базе. Перегрузите страницу.");
            }
        });
    });

    $('#btn_used').on('click', function(){
        let used_html = "";
        $('#used_table_body').html("");
        let selectedBranchId = $('#select_branch_used').val();
        $('#btn_used').css("display", "none");
        $.ajax({
            url: '../user/check-container/no-used-container',
            method: 'POST',
            dataType: 'json',
            data: {branchId: selectedBranchId, days: $('#days_used').val()},
            success: function(containers) {
                $('#used_table').css("display", "block");
                used_html = "";
                if(selectedBranchId>1){
                    if(containers[0]!=null){
                        used_html+= "<tr tabindex='3'><td colspan='4' style='color: blue;'><b>" + containers[0].branchName + "</b></td></tr>";
                        $.each(containers, function(key, container){
                            used_html += "<tr><td style='color: blue; text-decoration: underline; text-align: center;'>" + container.containerNumber + "</td><td>" +
                            container.departmentName + "</td><td>" + container.value  + "</td><td>" + container.registrationDate + "</td></tr>";
                        });
                    } else {
                        used_html+= "<tr tabindex='3'><td colspan='4' '><b>Отсутствуют термоконтенеры</b></td></tr>";
                    }
                } else {
                    used_html = "<tr tabindex='3'><td colspan='4' style='color: blue;'><b>" + containers[0].branchName + "</b></td></tr>";
                    var currentId = containers[0].branchId;
                    for(var i=0; i<containers.length; i++){
                        var nextId = containers[i].branchId;
                        if(currentId===nextId){
                            used_html += "<tr><td style='color: blue; text-decoration: underline; text-align: center;'>" + containers[i].containerNumber + "</td><td>" +
                            containers[i].departmentName + "</td><td>" + containers[i].value  + "</td><td>" + containers[i].registrationDate + "</td></tr>";
                        } else {
                            currentId=nextId;
                            used_html+= "<tr><td colspan='4' style='color: blue;'><b>" + containers[i].branchName + "</b></td></tr>";
                            i--;
                        }
                    }
                }
                $('#used_table_body').prepend(used_html);
                $("tr[tabindex=3]").focus();
                $('#btn_used').css("display", "block");
            },
            error:  function(response) {
                $('#btn_used').css("display", "block");
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка поиска по базе. Перегрузите страницу.");
            }
        });
    });

    $('#btn_print').on('click', function(){
        var validNumber = /^[0-9]+$/;
        if(validNumber.test($('#start_number').val()) && validNumber.test($('#end_number').val())){
            if($('#end_number').val()>=$('#start_number').val()){
                $('#btn_print').css("display", "none");
                $.ajax({
                    url: '../user/check-container/print-code',
                    method: 'POST',
                    dataType: 'text',
                    data: {startNumber: $('#start_number').val(), endNumber: $('#end_number').val()},
                    success: function(message) {
                        $('#btn_print').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html(message);
                    },
                    error:  function(response) {
                        $('#btn_print').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка формирования штрих-кода термоконтейнера. Перегрузите страницу.");
                    }
                });
            } else {
                $('#result_line').html("Второй номер должен быть равен или больше начального номера");
            }
        } else {
            $('#result_line').html("Номера должны состоять только из цифр (без пробелов и десятичных значений)");
        }
    });


    $('#many_new_numbers').on('click', function(){
        if($('#next_new_number').attr('type') == 'hidden'){
            $('#next_new_number').prop('type', 'text');
            $('#next_new_number').val("");
        } else {
            $('#next_new_number').prop('type', 'hidden');
            $('#next_new_number').val("");
        }
    });
    $('#many_send_numbers').on('click', function(){
        if($('#next_send_number').attr('type') == 'hidden'){
            $('#next_send_number').prop('type', 'text');
        } else {
            $('#next_send_number').prop('type', 'hidden');
        }
    });
    $('#check_new').on('click', function(){
        $('#check_new_area').css("display", "block");
        $('#new_number').focus();
    });
    $('#clean_check_new').on('click', function(){
        $('#check_new_area').css("display", "none");
    });
    $('#clean_input_new').click(function(){
        $('#new_number').val("");
        $('#next_new_number').val("");
        $('#new_number').focus();
    });

    $('#check_value').on('click', function(){
        $('#check_value_area').css("display", "block");
    });
    $('#clean_check_value').on('click', function(){
        $('#check_value_area').css("display", "none");
    });

    $('#send_container').on('click', function(){
        $('#send_area').css("display", "block");
        $('#send_number').focus();
    });
    $('#clean_send').on('click', function(){
        $('#send_area').css("display", "none");
    });
    $('#clean_input_send').click(function(){
        $('#send_number').val("");
        $('#next_send_number').val("");
        $('#send_number').focus();
    });

    $('#write_off_container').on('click', function(){
        $('#write_off_area').css("display", "block");
        $('#write_off_number').val("");
        $('#write_off_number').focus();
    });
    $('#clean_write_off').on('click', function(){
        $('#write_off_area').css("display", "none");
    });
    $('#clean_input_write_off').click(function(){
        $('#write_off_number').val("");
        $('#btn_check').css("display", "block");
        $('#write_off_number').focus();
    });

    $('#find_container').on('click', function(){
        $('#find_area').css("display", "block");
        $('#find_table_body').html("");
        $('#find_number').focus();
    });
    $('#clean_find').on('click', function(){
        $('#find_area').css("display", "none");
        $('#find_table').css("display", "none");
        $('#table_area').css("display", "none");
        $('#find_number').val("");
    });
    $('#clean_input_find').click(function(){
        $('#find_number').val("");
        $('#find_table_body').html("");
        $('#find_number').focus();
    });

    $('#search_containers').on('click', function(){
        $('#search_area').css("display", "block");
        $('#search_table_body').html("");
    });
    $('#clean_search').on('click', function(){
        $('#search_area').css("display", "none");
        $('#search_table').css("display", "none");
        $('#table_area').css("display", "none");
    });

    $('#search_table_body').on('click', function(event){
        let elem = event.target || event.srcElement;
        let num = elem.innerHTML;
        let validNumber = /^[0-9]+$/;
        if(num!=null && validNumber.test(num)){
            showTable(num);
        }
    });

    $('#used_containers').on('click', function(){
        $('#used_area').css("display", "block");
        $('#used_table_body').html("");
    });
    $('#clean_used').on('click', function(){
        $('#used_area').css("display", "none");
        $('#used_table').css("display", "none");
        $('#table_area').css("display", "none");
    });

    $('#used_table_body').on('click', function(event){
        let elem = event.target || event.srcElement;
        let num = elem.innerHTML;
        let validNumber = /^[0-9]+$/;
        if(num!=null && validNumber.test(num)){
            showTable(num);
        }
    });

    $('#find_table_body').on('click', function(event){
        let elem = event.target || event.srcElement;
        let num = elem.innerHTML;
        let validNumber = /^[0-9]+$/;
        if(num!=null && validNumber.test(num)){
            showTable(num);
        }
    });

    $('#used_table_body').on('click', function(event){
        let elem = event.target || event.srcElement;
        let num = elem.innerHTML;
        let validNumber = /^[0-9]+$/;
        if(num!=null && validNumber.test(num)){
            showTable(num);
        }
    });

    function showTable(num){
        $.ajax({
            url: '../user/load-data/find-notes',
            method: 'POST',
            dataType: 'json',
            data: {containerNumber: num},
            success: function(notes) {
                $('#table_area').css("display", "block");
                $('#container_number').html(num);

                let table_body = $('#container_table_body');
                let notes_html = "";
                table_body.html('');
                if(notes!=null && notes.length>0){
                  $.each(notes, function(key, note){
                        notes_html += "<tr><td>" + note.outDepartment + "</td><td>" + note.sendTime + "</td><td>" + note.outUser +
                        "</td><td>" + note.toDepartment + "</td><td>" + note.arriveTime + "</td><td>" + note.toUser + "</td></tr>";
                    });
                } else {
                    notes_html = "<tr><td colspan='6'>Нет записей по перемещениям термоконтейнера</td></tr>";
                }
                table_body.prepend(notes_html);
                document.getElementById("container_number").scrollIntoView();
           },
            error:  function(response) {
                $('#result_line').html("Ошибка обращения в базу даннных. Перегрузите страницу.");
                $('#show_note').css("display", "none");
            }
        });
    }

    $('#clean_table').on('click', function(){
        $('#table_area').css("display", "none");
    });

    $('#print_container').on('click', function(){
        $('#print_area').css("display", "block");
    });
    $('#clean_print').on('click', function(){
        $('#print_area').css("display", "none");
    });

    $('#select_branch').on('change', function(){
        $.ajax({
            url: '../user/change-department/select-branch',
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
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });
});
