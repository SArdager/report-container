$(document).ready(function(){

    $('#btn_outcome').on('click', function(){
        if($('#select_department').val() == $('#departmentId').val()){
            var x = confirm("Проверьте правильность выбора получателя. \nПодтвердите направление термоконтейнера по круговому маршруту " +
            "или отмените отгрузку термоконтейнера");
            if(x){
                registerOut();
            }
        } else {
            registerOut();
        }
    });

    function registerOut(){
        var validNumber = /^[0-9]+$/;
        var number_outcome = $('#number_outcome').val();
        var dateValue = new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString().slice(0, -3);
        if(number_outcome.length > 0){
            if(validNumber.test($('#payment').val())){
                $.ajax({
                    url: 'check-out/send',
                    method: 'POST',
                    dataType: 'text',
                    data: {toId: $('#select_department').val(), containerNumber: $('#number_outcome').val(), date: dateValue,
                        isFirstSend: "true", text: $('#textarea_out').val(), payment: $('#payment').val()},
                    success: function(message) {
                        if(message.indexOf("уже")>0){
                            var x = confirm(message);
                            if(x){
                                $.ajax({
                                    url: 'check-out/send',
                                    method: 'POST',
                                    dataType: 'text',
                                    data: {toId: $('#select_department').val(), containerNumber: $('#number_outcome').val(), date: dateValue,
                                    isFirstSend: "false", text: $('#textarea_out').val(), payment: $('#payment').val()},
                                    success: function(message) {
                                        $('#result_line').html(message);
                                        $('#number_outcome').val("");
                                        $('#number_outcome').focus();
                                        $('#time_outcome').html(dateValue);
                                        $('#status_outcome').html("Переоформлена отгрузка термоконтейнера");
                                        },
                                    error:  function(response) {
                                        $('#result_line').html("Ошибка регистрации отгрузки термоконтейнера. Повторите.");
                                    }
                                });
                            }
                        } else {
                            $('#result_line').html(message);
                            $('#number_outcome').val("");
                            $('#number_outcome').focus();
                            $('#time_outcome').html(dateValue);
                            $('#status_outcome').html("Регистрация отправки термоконтейнера");
                        }
                    },
                    error:  function(response) {
                        $('#result_line').html("Ошибка регистрации отгрузки. Повторите.");
                    }
                });
            } else {
                $('#result_line').html("В строке оплаты отгрузки должна указана стоимость в числах или 0");
            }
        } else {
            $('#result_line').html("Введите номер термоконтейнера");
        }

    }

    $('#btn_income').on('click', function(){
        var number_income = $('#number_income').val();
        var dateValue = new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString().slice(0, -3);
        if(number_income.length > 0){
            $.ajax({
                url: '../user/check-in/check',
                method: 'POST',
                dataType: 'text',
                data: {containerNumber: $('#number_income').val(), date: dateValue, text: $('#textarea_in').val()},
                success: function(message) {
                    if(message.indexOf("ЖЕЛАЕТЕ")>0){
                        var x = confirm(message);
                        if(x){
                            $.ajax({
                                url: '../user/check-in/check-route-off',
                                method: 'POST',
                                dataType: 'text',
                                data: {containerNumber: $('#number_income').val(), date: dateValue, text: $('#textarea_in').val()},
                                success: function(message) {
                                    $('#result_line').html(message);
                                    },
                                error:  function(response) {
                                    $('#result_line').html("Ошибка регистрации термоконтейнера. Повторите.");
                                }
                            });
                        }
                    } else if(message.indexOf("внесено")>0){
                        $('#result_line').html(message);
                        $('#time_income').html(dateValue);
                        $('#status_income').html(message.substring(41));
                        $('#reload_input').trigger("click");
                    } else {
                        $('#result_line').html(message);
                    }
                },
                error:  function(response) {
                    $('#result_line').html("Ошибка регистрации прибытия. Повторите.");
                }
            });
        }
    });

    $('#btn_check').on('click', function(){
        var number_check = $('#number_check').val();
        var dateValue = new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString().slice(0, -3);
        if(number_check.length > 0){
            $.ajax({
                url: '../user/check-between/check',
                method: 'POST',
                dataType: 'text',
                data: {containerNumber: $('#number_check').val(), date: dateValue, text: $('#textarea_between').val()},
                success: function(message) {
                    $('#result_line').html(message);
                    $('#time_check').html(dateValue);
                    $('#status_check').html("Прохождение термоконтейнера зарегистрировано");
                },
                error:  function(response) {
                    $('#result_line').html("Ошибка регистрации термоконтейнера. Повторите.");
                }
            });
        }
    });

    $('#reload_input').on('click', function(){
        $.ajax({
            url: '../user/load-data/container-notes',
            method: 'POST',
            dataType: 'json',
            success: function(notes) {
                    var notes_html = "";
                    var income_table_body = $('#income_table_body');
                    income_table_body.html('');
                    $.each(notes, function(key, note){
                        notes_html += "<tr><td>" + note.id + "</td><td>" + note.waitTime + "</td><td>" +
                               note.containerNumber  + "</td><td>" + note.outDepartment + "</td></tr>";
                    });
                    income_table_body.prepend(notes_html);
            },
            error:  function(response) {
                alert("Ошибка обращения в базу данных. Повторите.");
            }
        });
    });

    $('#btn_export_excel').on('click', function(){
        var departmentId = $('#department_id').val();
        if(departmentId==1){
            if($('#department_checkbox').is(':checked')==false && $('#select_department').val()!=null){
                departmentId = $('#select_department').val();
            }
        }
        $('#exportDepartmentId').val(departmentId);
        $('#export_excel').submit();
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
                    var x = confirm("Вы изменяете ОБЪЕКТ получателя термоконтейнера!!\n Продолжить внесение изменений?");
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
        $('#line_cut_note').click();
        $.ajax({
            url: '../user/check-out/save-changes',
            method: 'POST',
            dataType: 'text',
            data: {noteId: $('#containerNoteId').text(), changeNote: $('#inputSendNote').val(), changeArriveNote: $('#inputArriveNote').val(), changeBetweenNote: $('#inputBetweenNote').val(),
                    changePay: $('#inputPay').val(), toDepartment: $('#select_change_department').val(), departmentId: $('#department_id').val(), isOutChange: isOutChange},
            success: function(message) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html(message);
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка записи изменений в маршрутный лист. Повторите.");
            }
        });
    }

    $('#department_checkbox').on('click', function(){
        if($('#department_checkbox').is(':checked')==true){
            $('#select_branch').val(0);
            $('#select_department').empty();
        }
    });

    $('#select_branch').on('click', function(){
        if($('#select_branch').val()>0){
            $('#department_checkbox').prop('checked', false);
        }
    });
});
