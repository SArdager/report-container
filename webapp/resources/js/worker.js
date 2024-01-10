$(document).ready(function(){

    let validNumber = /^[0-9]+$/;

    $('#btn_outcome').on('click', function(){
        if($('#select_department').val() == $('#departmentId').val()){
            var x = confirm("Проверьте правильность выбора получателя. <br>Подтвердите направление термоконтейнера по круговому маршруту " +
            "или отмените отгрузку термоконтейнера (посылки)");
            if(x){
                registerOut();
            }
        } else {
            registerOut();
        }
    });

    function registerOut(){
        let number_outcome = $('#number_outcome').val();
        let firstLetter = number_outcome.substring(0, 1);
        let dateValue = new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString().slice(0, -3);
        $('#time_outcome').html("");
        $('#status_outcome').html("");
        if(number_outcome.length > 0){
            if(validNumber.test($('#amount').val())){
                if(validNumber.test($('#payment').val())){
                    if(validNumber.test(firstLetter)){
                        $('#btn_outcome').css("display", "none");
                        $.ajax({
                            url: 'check-out/send',
                            method: 'POST',
                            dataType: 'text',
                            data: {toId: $('#select_department').val(), containerNumber: $('#number_outcome').val(), text: $('#textarea_out').val(),
                                payment: $('#payment').val(), amount: $('#amount').val(), thermometer: $('#thermometer').val(), paidEnd: $('#paid').is(":checked")},
                            success: function(message) {
                                if(message.indexOf("уже оформлен")>0){
                                    let x = confirm(message);
                                    if(x){
                                        $.ajax({
                                            url: 'check-out/again-send',
                                            method: 'POST',
                                            dataType: 'text',
                                            data: {toId: $('#select_department').val(), containerNumber: $('#number_outcome').val(), text: $('#textarea_out').val(),
                                                payment: $('#payment').val(), amount: $('#amount').val(), thermometer: $('#thermometer').val(), paidEnd: $('#paid').is(":checked")},
                                            success: function(message) {
                                                $('#number_outcome').val("");
                                                $('#container_number').val("");
                                                $('#costs_part').val("0");
                                                $('#payment').val("0");
                                                $('#amount').val("1");
                                                $('#number_outcome').focus();
                                                $('#time_outcome').html(dateValue);
                                                $('#status_outcome').html("Переоформлена отгрузка термоконтейнера");
                                                window.scrollTo({ top: 0, behavior: 'smooth' });
                                                $('#result_line').html("Отгрузка термоконтейнера переоформлена на другой объект");
                                                $('#btn_outcome').css("display", "block");
                                            },
                                            error:  function(response) {
                                                window.scrollTo({ top: 0, behavior: 'smooth' });
                                                $('#result_line').html("Ошибка регистрации отгрузки термоконтейнера. Перегрузите страницу.");
                                                $('#btn_outcome').css("display", "block");
                                            }
                                        });
                                    }
                                } else if(message.indexOf("пользователь")>0){
                                    $('#number_outcome').val("");
                                   $('#payment').val("0");
                                                         $('#container_number').val("");
                                    $('#costs_part').val("0");
                                   $('#amount').val("1");
                                    $('#number_outcome').focus();
                                    $('#status_outcome').html("Нельзя оформить отправку термоконтейнера");
                                    window.scrollTo({ top: 0, behavior: 'smooth' });
                                    $('#result_line').html(message);
                                } else {
                                    $('#number_outcome').val("");
                                    $('#container_number').val("");
                                    $('#costs_part').val("0");
                                    $('#payment').val("0");
                                    $('#amount').val("1");
                                    $('#number_outcome').focus();
                                    $('#time_outcome').html(dateValue);
                                    $('#status_outcome').html("Регистрация отправки термоконтейнера");
                                    window.scrollTo({ top: 0, behavior: 'smooth' });
                                    $('#result_line').html(message);
                                }
                                $('#btn_outcome').css("display", "block");
                            },
                            error:  function(response) {
                                window.scrollTo({ top: 0, behavior: 'smooth' });
                                $('#result_line').html("Ошибка регистрации отгрузки. Перегрузите страницу.");
                                $('#btn_outcome').css("display", "block");
                            }
                        });
                        document.getElementById("payment_tr").style.display = "table-row";
                        document.getElementById("btn_add_parcel").style.display = "none";
                        document.getElementById("costs_part_tr").style.display = "none";
                        document.getElementById("container_tr").style.display = "none";
                        document.getElementById("amount_tr").style.display = "table-row";
                    } else {
                        $('#btn_outcome').css("display", "none");
                        $.ajax({
                            url: 'check-out/send-parcel',
                            method: 'POST',
                            dataType: 'text',
                            data: {toId: $('#select_department').val(), parcelNumber: $('#number_outcome').val(),
                                text: $('#textarea_out').val(), payment: $('#payment').val()},
                            success: function(message) {
                                $('#number_outcome').val("");
                                $('#container_number').val("");
                                $('#costs_part').val("0");
                                $('#payment').val("0");
                                $('#amount').val("1");
                                $('#number_outcome').focus();
                                $('#status_outcome').html("Отгрузка посылки оформлена.");
                                $('#reload_output').trigger("click");
                                $('#btn_outcome').css("display", "block");
                                $('#payment_tr').css("display", "table-row");
                                $('#btn_add_parcel').css("display", "none");
                                $('#costs_part_tr').css("display", "none");
                                $('#container_tr').css("display", "none");
                                $('#amount_tr').css("display", "table-row");
                                window.scrollTo({ top: 0, behavior: 'smooth' });
                                $('#result_line').html(message);
                            },
                            error:  function(response) {
                                $('#btn_outcome').css("display", "block");
                                window.scrollTo({ top: 0, behavior: 'smooth' });
                                $('#result_line').html("Ошибка регистрации отгрузки. Перегрузите страницу.");
                            }
                        });
                    }
                } else {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("В строке оплаты отгрузки должна указана стоимость в числах (при отсутствии оплаты - ноль)");
                }
            } else {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("В строке количества должно быть указано число");
            }
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Введите номер термоконтейнера");
        }
    }

    $('#btn_income').on('click', function(){
        let number_income = $('#number_income').val();
        let dateValue = new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString().slice(0, -3);
        $('#time_income').html("");
        $('#status_income').html("");
        if(number_income.length > 0){
            $('#btn_income').css("display", "none");
            let firstLetter = number_income.substring(0, 1);
            if(validNumber.test(firstLetter)){
                $.ajax({
                    url: '../user/check-in/check',
                    method: 'POST',
                    dataType: 'text',
                    data: {containerNumber: $('#number_income').val(), text: $('#textarea_in').val()},
                    success: function(message) {
                        if(message.indexOf("внесен")>0){
                            $('#time_income').html(dateValue);
                            $('#status_income').html(message.substring(41));
                            $('#number_income').val("");
                            $('#textarea_in').val("");
                            window.scrollTo({ top: 0, behavior: 'smooth' });
                            $('#result_line').html(message);
                        } else {
                            window.scrollTo({ top: 0, behavior: 'smooth' });
                            $('#result_line').html(message);
                        }
                        $('#reload_input').trigger("click");
                        $('#btn_income').css("display", "block");
                    },
                    error:  function(response) {
                        $('#btn_income').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка регистрации прибытия. Перегрузите страницу.");
                    }
                });
            } else {
                $.ajax({
                    url: '../user/check-in/parcel',
                    method: 'POST',
                    dataType: 'text',
                    data: {parcelNumber: $('#number_income').val(), text: $('#textarea_in').val()},
                    success: function(message) {
                        $('#btn_income').css("display", "block");
                        if(message.indexOf("Нельзя")<0 && message.indexOf("не найдена")<0){
                            $('#time_income').html(dateValue);
                            $('#status_income').html("Прибытие посылки зарегистрировано");
                            $('#reload_input').trigger("click");
                        }
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html(message);
                   },
                    error:  function(response) {
                        $('#btn_income').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка регистрации прибытия. Перегрузите страницу.");
                    }
                });
            }
        }
    });

    $('#reload_input').on('click', function(){
        $('#parcels_table').css("display", "none");
        $.ajax({
            url: '../user/load-data/container-notes',
            method: 'POST',
            dataType: 'json',
            success: function(notes) {
                let income_table_body = $('#income_table_body');
                income_table_body.html('');
                if(notes!=null && notes.length>0){
                    let notes_html = "";
                    $.each(notes, function(key, note){
                        notes_html += "<tr><td>" + note.id + "</td><td>" + note.waitTime + "</td><td>" +
                               note.containerNumber  + "</td><td>" + note.outDepartment + "</td></tr>";
                    });
                    income_table_body.prepend(notes_html);
                } else {
                    income_table_body.prepend("<tr><td colspan='4'>Нет отгруженных термоконтейнеров</td></tr>");
                }
                $.ajax({
                    url: '../user/load-data/parcels',
                    method: 'POST',
                    dataType: 'json',
                    success: function(parcels) {
                        if(parcels!=null && parcels.length>0){
                            $('#parcels_table').css("display", "block");
                            let parcels_html = "";
                            let parcels_table_body = $('#parcels_table_body');
                            parcels_table_body.html('');
                            $.each(parcels, function(key, parcel){
                                parcels_html += "<tr><td>" + parcel.parcelNumber + "</td><td>" + parcel.destinationName + "</td><td>" +
                                       parcel.parentNumber  + "</td><td>" + parcel.parcelType + "</td><td>" + parcel.dimensions + "</td></tr>";
                            });
                            parcels_table_body.prepend(parcels_html);
                        }
                    },
                    error:  function(response) {
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    }
                });
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

    $('#btn_check').on('click', function(){
        let number_check = $('#number_check').val();
        let dateValue = new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString().slice(0, -3);
        $('#time_check').html("");
        $('#status_check').html("");
        if(number_check.length > 0){
            $('#btn_check').css("display", "none");
            $.ajax({
                url: '../user/check-between/check',
                method: 'POST',
                dataType: 'text',
                data: {containerNumber: $('#number_check').val(), text: $('#textarea_between').val()},
                success: function(message) {
                    if(message.indexOf("ДРУГОГО")>0){
                        let x = confirm(message);
                        if(x){
                            $.ajax({
                                url: '../user/check-between/check-again',
                                method: 'POST',
                                dataType: 'text',
                                data: {containerNumber: $('#number_check').val(), text: $('#textarea_between').val()},
                                success: function(message) {
                                    $('#btn_check').css("display", "block");
                                    $('#time_income').html(dateValue);
                                    $('#status_income').html("Прохождение термоконтейнера зарегистрировано");
                                    $('#number_check').val("");
                                    $('#textarea_between').val("");
                                    window.scrollTo({ top: 0, behavior: 'smooth' });
                                    $('#result_line').html(message);
                                },
                                error:  function(response) {
                                    $('#btn_check').css("display", "block");
                                    window.scrollTo({ top: 0, behavior: 'smooth' });
                                    $('#result_line').html("Ошибка регистрации термоконтейнера. Перегрузите страницу.");
                                }
                            });
                        }
                    } else {
                        $('#number_check').val("");
                        $('#textarea_between').val("");
                        $('#time_check').html(dateValue);
                        $('#status_check').html("Прохождение термоконтейнера зарегистрировано");
                        $('#btn_check').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html(message);
                    }
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка регистрации термоконтейнера. Перегрузите страницу.");
                    $('#btn_check').css("display", "block");
                }
            });
        }
    });

    $('#btn_courier').on('click', function(){
        let number_courier = $('#number_courier').val();
        if(number_courier.length > 0){
        $('#btn_courier').css("display", "none");
            $.ajax({
                url: '../user/check-between/check-courier',
                method: 'POST',
                dataType: 'text',
                data: {containerNumber: $('#number_courier').val(), text: $('#textarea_courier').val()},
                success: function(message) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html(message);
                    $('#btn_courier').css("display", "block");
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка регистрации термоконтейнера. Перегрузите страницу.");
                    $('#btn_courier').css("display", "block");
                }
            });
        }
    });

    $('#reload_output').on('click', function(){
        $('#parcels_field').css("display", "block");
        $('#show_parcels').html("Скрыть поле просмотра посылок");
        $.ajax({
            url: '../user/load-data/ready_parcels',
            method: 'POST',
            dataType: 'json',
            success: function(parcels) {
                let parcels_html = "";
                let outcome_parcels_body = $('#outcome_parcels_body');
                outcome_parcels_body.html('');
                if(parcels!=null && parcels.length>0){
                    $.each(parcels, function(key, parcel){
                        parcels_html += "<tr><td>" + parcel.parcelNumber + "</td><td>" + parcel.destinationName + "</td><td>" +
                               parcel.parentNumber  + "</td><td>" + parcel.parcelType + "</td><td>" + parcel.dimensions + "</td></tr>";
                    });
                } else {
                    parcels_html = "<tr><td colspan='5'>Нет посылок на отгрузку</td></tr>"
                }
                outcome_parcels_body.prepend(parcels_html);
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

    $('#btn_change').on('click', function(){
        if($('#userId').val() == $('#outUserId').val()){
            if($('#select_change_department').val() == $('#outDepartmentId').val()){
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Проверьте правильность выбора получателя");
            } else {
                if($('#select_change_department').val() == $('#toDepartmentId').val()){
                    saveChanges(true);
               } else {
                    var x = confirm("Вы изменяете ОБЪЕКТ получателя термоконтейнера!!<br>Продолжить внесение изменений?");
                    if(x){
                        saveChanges(true);
                    }
                }
            }
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Вносить изменения может только отправитель термоконтейнера!!!");
        }
    });

    $('#btn_arrive_change').on('click', function(){
        if($('#userId').val() == $('#toUserId').val()){
            saveChanges(false);
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Вносить изменения может только получатель термоконтейнера!!!");
        }
    });

    $('#btn_between_change').on('click', function(){
        if($('#userId').val() == $('#passUserId').val()){
            saveChanges(false);
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Вносить изменения может только получатель термоконтейнера!!!");
        }
    });

    function saveChanges(isOutChange){
        $('#line_cut_parcel').click();
        $('#btn_change').css("display", "none");
        $('#btn_arrive_change').css("display", "none");
        $('#btn_between_change').css("display", "none");
        $.ajax({
            url: '../user/check-out/save-changes',
            method: 'POST',
            dataType: 'text',
            data: {noteId: $('#containerNoteId').text(), changeNote: $('#inputSendNote').val(), changeArriveNote: $('#inputArriveNote').val(), changeBetweenNote: $('#inputBetweenNote').val(),
                    changePay: $('#inputPay').val(), toDepartment: $('#select_change_department').val(), departmentId: $('#department_id').val(), isOutChange: isOutChange},
            success: function(message) {
                $('#btn_change').css("display", "block");
                $('#btn_arrive_change').css("display", "block");
                $('#btn_between_change').css("display", "block");
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html(message);
            },
            error:  function(response) {
                $('#btn_change').css("display", "block");
                $('#btn_arrive_change').css("display", "block");
                $('#btn_between_change').css("display", "block");
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка записи изменений в маршрутный лист. Перегрузите страницу.");
            }
        });
    }

    $('#all_checkbox').on('click', function(){
        if($('#all_checkbox').is(':checked')==true){
            $('#select_branch').val(0);
            $('#select_department').empty();
        }
    });

    $('#select_branch').on('click', function(){
        if($('#select_branch').val()>0){
            $('#all_checkbox').prop('checked', false);
        }
    });

    $('#btn_add_parcel').on('click', function(){
        $('#btn_add_parcel').css("display", "none");
        $.ajax({
            url: '../user/check-parcel/add-to-parent',
            method: 'POST',
            dataType: 'text',
            data: {parcelNumber: $('#number_outcome').val(),  parentNumber: $('#container_number').val(),
                costsPart: $('#costs_part').val()},
            success: function(message) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html(message);
                $('#btn_add_parcel').css("display", "block");
                $('#number_outcome').val("");
                $('#container_number').val("");
                $('#costs_part').val("0");
                $('#payment').val("0");
                $('#amount').val("1");
                $('#reload_output').trigger("click");
                $('#btn_outcome').css("display", "block");
                $('#payment_tr').css("display", "table-row");
                $('#btn_add_parcel').css("display", "none");
                $('#costs_part_tr').css("display", "none");
                $('#container_tr').css("display", "none");
                $('#amount_tr').css("display", "table-row");
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка оформления почтового отправления. Перегрузите страницу.");
                $('#btn_add_parcel').css("display", "block");
            }
        });
    });

    $('#btn_remove').on('click', function(){
        let parcel_number = $('#parcel_number').val();
        if(parcel_number.length>0){
            $('#btn_remove').css("display", "none");
            $.ajax({
                url: '../user/check-parcel/remove-from-parent',
                method: 'POST',
                dataType: 'text',
                data: {parcelNumber: parcel_number},
                success: function(message) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html(message);
                    $('#btn_remove').css("display", "block");
                    $('#parcel_number').val("");
                    if(document.getElementById("parent_number")!=null){
                        $('#parent_number').val("");
                    }
                    if(document.getElementById("reload_parcel")!=null){
                        $('#reload_parcel').trigger("click");
                    }
                    if(document.getElementById("reload_output")!=null){
                        $('#reload_output').trigger("click");
                    }
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    $('#btn_remove').css("display", "block");
                }
            });
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Внесите номер почтового отправления.");
        }
    });

    let numberOut = document.getElementById("number_outcome");
    numberOut.oninput = function(){
        let number_outcome = $('#number_outcome').val();
        if(number_outcome.length>0){
            let firstLetter = number_outcome.substring(0, 1);
            if(validNumber.test(firstLetter)){
                document.getElementById("container_tr").style.display = "none";
                document.getElementById("thermometer_tr").style.display = "table-row";
                document.getElementById("amount_tr").style.display = "table-row";
                document.getElementById("paid_tr").style.display = "table-row";
            } else {
                document.getElementById("container_tr").style.display = "table-row";
                document.getElementById("thermometer_tr").style.display = "none";
                document.getElementById("amount_tr").style.display = "none";
                document.getElementById("paid_tr").style.display = "none";
            }
        } else {
            document.getElementById("container_tr").style.display = "none";
            document.getElementById("thermometer_tr").style.display = "table-row";
            document.getElementById("amount_tr").style.display = "table-row";
            $('#costs_part').val("0");
            $('#payment').val("0");
            $('#amount').val("1");
        }
    };


});
