$(document).ready(function(){

    $('#btn_outcome').on('click', function(){
        var validNumber = /^[0-9]+$/;
        var number_outcome = $('#number_outcome').val();
        var dateValue = new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString().slice(0, -3);
        if($('#select_department').val() == $('#departmentId').val()){
           $('#result_line').html("Проверьте правильность выбора получателя");
        } else {
            if(number_outcome.length > 0){
                if(validNumber.test($('#payment').val())){
                    $.ajax({
                        url: '/user/check-out/send',
                        method: 'POST',
                        dataType: 'text',
                        data: {toId: $('#select_department').val(), probeId: $('#select_probe').val(),
                            containerNumber: $('#number_outcome').val(), date: dateValue, isFirstSend: "true",
                            text: $('#textarea_out').val(), payment: $('#payment').val()},
                        success: function(message) {
                            if(message.indexOf("уже")>0){
                                var x = confirm(message);
                                if(x){
                                    $.ajax({
                                        url: '/user/check-out/send',
                                        method: 'POST',
                                        dataType: 'text',
                                        data: {containerNumber: $('#number_outcome').val(), toId: $('#select_department').val(), isFirstSend: "false",
                                             probeId: $('#select_probe').val(), date: dateValue, text: $('#textarea_out').val(), payment: $('#payment').val()},
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
                            } else if(message.indexOf("Номер документа")>0){
                                $('#result_line').html(message);
                                $('#number_outcome').val("");
                                $('#number_outcome').focus();
                                $('#time_outcome').html(dateValue);
                                $('#status_outcome').html("Регистрация отправки термоконтейнера");
                            }
                            else {
                                $('#result_line').html(message);
                            }
                        },
                        error:  function(response) {
                            $('#result_line').html("Ошибка регистрации отгрузки. Повторите.");
                        }
                    });
                } else {
                    $('#result_line').html("В строке оплаты отгрузки должна указана стоимость только в числах");
                }
            } else {
                $('#result_line').html("Введите номер термоконтейнера");
            }
        }
    });

    $('#btn_income').on('click', function(){
        var number_income = $('#number_income').val();
        var dateValue = new Date().toLocaleDateString() + " " + new Date().toLocaleTimeString().slice(0, -3);
        if(number_income.length > 0){
            $.ajax({
                url: '/user/check-in/check',
                method: 'POST',
                dataType: 'text',
                data: {containerNumber: $('#number_income').val(), date: dateValue, text: $('#textarea_in').val()},
                success: function(message) {
                    if(message.indexOf("ЖЕЛАЕТЕ")>0){
                        var x = confirm(message);
                        if(x){
                            $.ajax({
                                url: '/user/check-in/check-route-off',
                                method: 'POST',
                                dataType: 'text',
                                data: {containerNumber: $('#number_income').val(), date: dateValue, text: $('#textarea_in').val()},
                                success: function(message) {
                                    $('#result_line').html(message);
                                    $('#number_income').val("");
                                    $('#number_income').focus();
                                    },
                                error:  function(response) {
                                    $('#result_line').html("Ошибка регистрации термоконтейнера. Повторите.");
                                    $('#number_income').val("");
                                    $('#number_income').focus();
                                }
                            });
                        }
                    } else if(message.indexOf("внесено")>0){
                        $('#result_line').html(message);
                        $('#time_income').html(dateValue);
                        $('#status_income').html("Регистрация приемки термоконтейнера");
                        $('#reload_input').trigger("click");
                        $('#number_income').val("");
                        $('#number_income').focus();

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
                url: '/user/check-between/check',
                method: 'POST',
                dataType: 'text',
                data: {containerNumber: $('#number_check').val(), date: dateValue, text: $('#textarea_between').val()},
                success: function(message) {
                    $('#result_line').html(message);
                    $('#number_check').val("");
                    $('#number_check').focus();
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
            url: '/user/load-data/container-notes',
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

    $('#reload_journal').on('click', function(){
        var departmentId = $('#department_id').val();
        if(departmentId==1){
            if($('#department_checkbox').is(':checked')==false && $('#select_department').val()!=null){
                departmentId = $('#select_department').val();
            }
        }
        $('#line_cut_note').click();
        var pageNumber = 0;
        var pageSize = $('#pageSize').val();
        var totalNotes;
        var totalPages;
        $.ajax({
            url: '/user/load-data/journal-totalNotes',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), departmentId: departmentId},
            success: function(totalElements) {
                totalNotes = parseInt(totalElements);
                if(totalNotes>0){
                    $('#totalNotes').val(totalNotes);
                    if(totalNotes%pageSize>0){
                        totalPages = parseInt(totalNotes/pageSize) + 1;
                    } else{
                        totalPages = parseInt(totalNotes/pageSize);
                    }
                    $.ajax({
                        url: '/user/load-data/journal-notes',
                        method: 'POST',
                        dataType: 'json',
                        data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(),
                                departmentId: departmentId, pageNumber: pageNumber, pageSize: pageSize},
                        success: function(notes) {
                            var pages_html = "";
                            var notes_html = "";
                            var pages_journal_title = $('#pages_journal_title');
                            var notes_table_body = $('#notes_table_body');
                            pages_journal_title.html('');
                            notes_table_body.html('');
                            pages_html = "<tr>";
                            if(pageNumber>2){
                                pages_html+="<td class='pages'> ( . . . )  </td>";
                            }
                            for(let i=0; i<totalPages-1; i++){
                                if(i - pageNumber<3 && pageNumber - i<3){
                                    if(i - pageNumber==0){
                                        pages_html+="<td class='pages'><b> (" + (Number(i*pageSize)+1) + "..." + (i+1)*pageSize + ")  </b></td>";
                                    } else {
                                        pages_html+="<td class='pages'> (" + (Number(i*pageSize)+1) + "..." + (i+1)*pageSize + ")  </td>";
                                    }
                                }
                            }
                            if(totalPages-pageNumber>4){
                                pages_html+="<td class='pages'> ( . . . )  </td>";
                            }
                            if(pageNumber==totalPages-1){
                                pages_html += "<td class='pages'><b> (" + (Number((totalPages-1)*pageSize)+1) + "..." + totalNotes + ")  </b></td></tr>";
                            } else {
                                pages_html += "<td class='pages'> (" + (Number((totalPages-1)*pageSize)+1) + "..." + totalNotes + ")  </td></tr>";
                            }
                            pages_journal_title.prepend(pages_html);
                            $.each(notes, function(key, note){
                                notes_html += "<tr><td style='color: blue; text-decoration: underline'>" + note.id + "</td><td>" +
                                note.sendTime + "</td><td>" + note.arriveTime + "</td><td>" + note.outDepartment  + "</td><td>" +
                                note.toDepartment  + "</td><td>" + note.status + "</td></tr>";
                            });
                            notes_table_body.prepend(notes_html);
                        },
                        error:  function(response) {
                            alert("Ошибка обращения в базу данных. Повторите.");
                        }
                    });
                }
            },
            error:  function(response) {
                alert("Ошибка обращения в базу данных. Повторите.");
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
            url: '/user/check-out/save-changes',
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

});
