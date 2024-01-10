$(document).ready(function(){

    $('#reload_journal').on('click', function(){
        if($('#department_id').val()==1 && $('#all_checkbox').is(':checked')==false && $('#select_branch').val()==0){
             $('#result_line').html("Выберите филиал или отметьте все объекты");
        } else {
            let departmentId = $('#department_id').val();
            $('#totalNotes').val(0);
            $('#journal_pages_title').html('');
            $('#notes_table_body').html('');
            $('#close_search').trigger('click');
            if(departmentId==1){
                if($('#all_checkbox').is(':checked')==false){
                    departmentId = $('#select_department').val();
                }
            }
            let sample = $('input[type="radio"][name="sample"]:checked').val();
            $('#line_cut_note').click();
            if(departmentId>0){
                $('#reload_journal').css("display", "none");
                if(sample=="all"){
                    $('#btn_export_excel').css("display", "block");
                    $.ajax({
                        url: '../user/load-data/journal-totalNotes',
                        method: 'POST',
                        dataType: 'json',
                        data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), departmentId: departmentId},
                        success: function(totalElements) {
                            let totalNotes = parseInt(totalElements);
                            if(totalNotes>0){
                                $('#totalNotes').val(totalNotes);
                                showNotesPage(0, departmentId);
                            } else {
                                let notes_table_body = $('#notes_table_body');
                                notes_table_body.prepend("<tr><td colspan='7'>За последний месяц термоконтейнеры не регистрировались</td></tr>");
                            }
                            $('#reload_journal').css("display", "block");
                        },
                        error:  function(response) {
                            $('#reload_journal').css("display", "block");
                            window.scrollTo({ top: 0, behavior: 'smooth' });
                            $('#result_line').html("Ошибка обращения в базу даннных. Перегрузите страницу.");
                        }
                    });
                } else if(sample == "route"){
                    $.ajax({
                        url: '../user/load-data/journal-at-route',
                        method: 'POST',
                        dataType: 'json',
                        data: {departmentId: departmentId},
                        success: function(notes) {
                            $('#reload_journal').css("display", "block");
                            let notes_html = "";
                            let notes_table_body = $('#notes_table_body');
                            if(notes.length>0){
                                $.each(notes, function(key, note){
                                    notes_html += "<tr><td style='text-align: end;'><span style='color: white'>" + note.id + "/</span><span style='color: blue; text-decoration: underline'>" +
                                    note.containerNumber + "</span></td><td style='width: 10%; text-align: end; padding-end: 20px;>'" + note.thermometer + "</td><td style='text-align: center;'>" +
                                    note.sendTime + "</td><td style='text-align: center;'>" + note.arriveTime + "</td><td>" + note.outDepartment  + "</td><td>" + note.toDepartment  + "</td><td>" + note.status + "</td></tr>";
                                });
                            } else{
                                notes_html += "<tr><td colspan='7'>Все отправленные термоконтейнеры доставлены</td></tr>";
                            }
                            notes_table_body.prepend(notes_html);
                        },
                        error:  function(response) {
                            $('#reload_journal').css("display", "block");
                            window.scrollTo({ top: 0, behavior: 'smooth' });
                            $('#result_line').html("Ошибка обращения в базу даннных. Перегрузите страницу.");
                        }
                    });
                } else {
                    if(departmentId>1){
                        $.ajax({
                            url: '../user/load-data/journal-at-home',
                            method: 'POST',
                            dataType: 'json',
                            data: {departmentId: departmentId},
                            success: function(notes) {
                                $('#reload_journal').css("display", "block");
                                let notes_html = "";
                                let notes_table_body = $('#notes_table_body');
                                if(notes.length>0){
                                    $.each(notes, function(key, note){
                                        notes_html += "<tr><td style='text-align: end;'><span style='color: white'>" + note.id + "/</span><span style='color: blue; text-decoration: underline'>" +
                                        note.containerNumber + "</span></td><td style='width: 10%; text-align: end; padding-end: 20px;'>" + note.thermometer + "</td><td style='text-align: center;'>" +
                                        note.sendTime + "</td><td style='text-align: center;'>" + note.arriveTime + "</td><td>" + note.outDepartment  + "</td><td>" + note.toDepartment  + "</td><td>" + note.status + "</td></tr>";
                                    });
                                } else{
                                    notes_html += "<tr><td colspan='7'>Отсутствуют термоконтейнеры, зарегистрированные (находящиеся) на объекте</td></tr>";
                                }
                                notes_table_body.prepend(notes_html);
                            },
                            error:  function(response) {
                                $('#reload_journal').css("display", "block");
                                window.scrollTo({ top: 0, behavior: 'smooth' });
                                $('#result_line').html("Ошибка обращения в базу даннных. Перегрузите страницу.");
                            }
                        });
                    } else {
                        $('#reload_journal').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Выберите объект, по которому следует вывести результат запроса.");
                    }
                }
            }
        }
    });

    $('#reload_payment').on('click', function(){
        if($('#department_id').val()==1 && $('#all_checkbox').is(':checked')==false && $('#select_branch').val()==0){
             $('#result_line').html("Выберите филиал или отметьте все объекты");
        } else {
            let departmentId = $('#department_id').val();
            let branchId = $('#branch_id').val();
            if(departmentId==1){
                if($('#all_checkbox').is(':checked')==false){
                    branchId = $('#select_branch').val();
                    if($('#branch_checkbox').is(':checked')==false){
                        departmentId = $('#select_department').val();
                    }
                }
            }
            $('#reload_payment').css("display", "none");
            $.ajax({
                url: '../control/payment/report-totalNotes',
                method: 'POST',
                dataType: 'json',
                data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), departmentId: departmentId, branchId: branchId},
                success: function(totalNumbers) {
                    $('#reload_payment').css("display", "block");
                    let totalPayments = parseInt(totalNumbers);
                    $('#payment_table_body').html('');
                    $('#payment_pages_title').html('');
                    if(totalPayments>0){
                        $('#totalPayments').val(totalPayments);
                        showPaymentsPage(0, departmentId, branchId);
                    } else {
                        let table_body = $('#payment_table_body');
                        table_body.prepend("<tr><td colspan='6'>Отсутствуют данные по оплате перевозки на объекте за выбранный период</td></tr>");
                    }
                },
                error:  function(response) {
                    $('#reload_payment').css("display", "block");
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
        }
    });

    $('#reload_parcels').on('click', function(){
        if($('#department_id').val()==1 && $('#all_checkbox').is(':checked')==false && $('#select_branch').val()==0){
             $('#result_line').html("Выберите филиал или отметьте все объекты");
        } else {
            let departmentId = $('#department_id').val();
            let branchId = $('#branch_id').val();
            $('#totalParcels').val(0);
            $('#parcels_pages_title').html('');
            $('#parcels_table_body').html('');
            if(departmentId==1){
                if($('#all_checkbox').is(':checked')==false){
                    if($('#branch_checkbox').is(':checked')==true){
                        departmentId = 0;
                    } else {
                        departmentId = $('#select_department').val();
                    }
                    branchId = $('#select_branch').val();
                }
            }
            $('#reload_parcels').css("display", "none");
            $.ajax({
                url: '../control/payment/parcels-totalNumbers',
                method: 'POST',
                dataType: 'json',
                data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(),
                    departmentId: departmentId, branchId: branchId},
                success: function(totalNumbers) {
                    $('#reload_parcels').css("display", "block");
                    let totalParcels = parseInt(totalNumbers);
                    if(totalParcels>0){
                        $('#totalParcels').val(totalParcels);
                        if(departmentId!=1){
                            showParcelsPaymentsPage(0, departmentId, branchId);
                        } else {
                            showAllParcelsPaymentsPage(0);
                        }
                    } else {
                        let table_body = $('#parcels_table_body');
                        table_body.prepend("<tr><td colspan='8'>Оплата почтовых отправлений не зарегистрирована</td></tr>");
                    }
                },
                error:  function(response) {
                    $('#reload_parcels').css("display", "block");
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Перегрузите страницу.");
                }
            });
        }
    });

    $('#reload_delay').on('click', function(){
        if($('#department_id').val()==1 && $('#all_checkbox').is(':checked')==false && $('#select_branch').val()==0){
             $('#result_line').html("Выберите филиал или отметьте все объекты");
        } else {
            document.getElementById("show_points").style.display = "none";
            let departmentId = $('#department_id').val();
            if(departmentId==1){
                if($('#all_checkbox').is(':checked')==false){
                    departmentId = $('#select_department').val();
                }
            }
            let way = $('input[type="radio"][name="way"]:checked').val();
            $('#reload_delay').css("display", "none");
            $.ajax({
                url: '../control/delay/report-totalNotes',
                method: 'POST',
                dataType: 'json',
                data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), way: way,
                        departmentId: departmentId, delayLimit: $('#delay_limit').val()},
                success: function(totalElements) {
                    $('#reload_delay').css("display", "block");
                    let totalNotes = parseInt(totalElements);
                    $('#delay_pages_title').html('');
                    $('#delay_table_body').html('');
                    if(totalNotes>0){
                        $('#totalNotes').val(totalNotes);
                        showDelayPage(0, departmentId);
                    } else {
                        let table_body = $('#delay_table_body');
                        table_body.prepend("<tr><td colspan='9'>Отсутствуют данные по опозданиям прибытия за выбранный период</td></tr>");
                    }
                },
                error:  function(response) {
                    $('#reload_delay').css("display", "block");
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
        }
    });

    $('#reload_route').on('click', function(){
        if($('#department_id').val()==1 && $('#all_checkbox').is(':checked')==false && $('#select_branch').val()==0){
             $('#result_line').html("Выберите филиал или отметьте все объекты");
        } else {
            $('#show_route_points').css("display", "none");
            $('#route_table_body').html('');
            $('#route_pages_title').html('');
            $('#reload_route').css("display", "none");
            if($('#container_id').val()>0){
                $.ajax({
                    url: '../control/route/report-totalNotes',
                    method: 'POST',
                    dataType: 'json',
                    data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), containerId: $('#container_id').val()},
                    success: function(totalElements) {
                        $('#reload_route').css("display", "block");
                        let totalNotes = parseInt(totalElements);
                        if(totalNotes>0){
                            $('#totalNotes').val(totalNotes);
                            showRoutePage(0);
                        } else {
                            let table_body = $('#route_table_body');
                            table_body.prepend("<tr><td colspan='8'>Отсутствуют данные по перемещениям термоконтейнера за выбранный период</td></tr>");
                        }
                    },
                    error:  function(response) {
                        $('#reload_route').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    }
                });
            } else {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Заполните номер термоконтейнера");
            }
        }
    });

    function showNotesPage(pageNumber, departmentId){
        let pageSize = $('#pageSize').val();
        let totalNotes = $('#totalNotes').val();
        $('#journal_pages_title').html('');
        $('#notes_table_body').html('');
        $.ajax({
            url: '../user/load-data/journal-notes',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(),
                    departmentId: departmentId, pageNumber: pageNumber, pageSize: pageSize},
            success: function(notes) {
                $('#reload_journal').css("display", "block");
                let notes_html = "";
                let pages_title = $('#journal_pages_title');
                let table_body = $('#notes_table_body');
                if(notes.length>0){
                    let pages_html = getPagesHtml(pageNumber, pageSize, totalNotes);
                    pages_title.prepend(pages_html);
                    $.each(notes, function(key, note){
                        notes_html += "<tr><td style='text-align: end;'><span style='color: white'>" + note.id + "/</span><span style='color: blue; text-decoration: underline'>" +
                        note.containerNumber + "</span></td><td style='width: 10%; text-align: end; padding-end: 20px;'>" + note.thermometer + "</td><td style='text-align: center;'>" +
                        note.sendTime + "</td><td style='text-align: center;'>" + note.arriveTime + "</td><td>" + note.outDepartment  + "</td><td>" + note.toDepartment  + "</td><td>" + note.status + "</td></tr>";
                    });
                    table_body.prepend(notes_html);
                }
            },
            error:  function(response) {
                $('#reload_journal').css("display", "block");
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу даннных. Перегрузите страницу.");
            }
        });
    }

    function showPaymentsPage(pageNumber, departmentId, branchId){
        let pageSize = $('#pageSize').val();
        let totalPayments = $('#totalPayments').val();
        $('#payment_table_body').html('');
        $('#payment_pages_title').html('');
        $.ajax({
            url: '../control/payment/report-notes',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), branchId: branchId,
                    departmentId: departmentId, pageNumber: pageNumber, pageSize: pageSize},
            success: function(notes) {
                let notes_html = "";
                let pages_html = getPagesHtml(pageNumber, pageSize, totalPayments);
                let pages_title = $('#payment_pages_title');
                let table_body = $('#payment_table_body');
                pages_title.prepend(pages_html);
                let depName = "";
                let sumPayment = 0;
                if(branchId == 1){
                    for(let i=0; i<notes.length; i++){
                        let note = notes[i];
                        if(depName != note.outBranch){
                            if(i > 0){
                                notes_html+= "<tr><td><b>Сумма по филиалу на странице:</b></td><td></td><td></td><td></td><td><b>" +
                                        sumPayment + " тг</b></td><td></td><td></td><td></td></tr>";
                            }
                            notes_html += "<tr><td colspan='8'><b>" + note.outBranch + "</b></td></tr>";
                            depName = note.outBranch;
                            sumPayment = 0;
                        }
                        notes_html += "<tr><td>" + note.containerNumber + "</td><td>" + note.sendDate + "</td><td>" +
                                note.outDepartment + "</td><td>" + note.toDepartment  + "</td><td>" + note.sendPay  +
                                " тг</td><td>" + note.amount + "</td><td>" + note.paidEnd + "</td><td>" + note.sendNote + "</td></tr>";
                        sumPayment += Number(note.sendPay);
                    }
                    notes_html+= "<tr><td><b>Сумма по филиалу на странице:</b></td><td></td><td></td><td></td><td><b>" +
                                sumPayment + " тг</b></td><td></td><td></td><td></td></tr>";
                } else {
                    if(departmentId == 1){
                        for(let i=0; i<notes.length; i++){
                            let note = notes[i];
                            if(i == 0){
                                if(note.paidEnd == "при получении"){
                                    notes_html += "<tr><td colspan='8'><b>" + note.toBranch + "</b></td></tr>";
                                } else {
                                    notes_html += "<tr><td colspan='8'><b>" + note.outBranch + "</b></td></tr>";
                                }
                            }
                            notes_html += "<tr><td>" + note.containerNumber + "</td><td>" + note.sendDate + "</td><td>" +
                                    note.outDepartment + "</td><td>" + note.toDepartment  + "</td><td>" + note.sendPay  +
                                    " тг</td><td>" + note.amount + "</td><td>" + note.paidEnd + "</td><td>" + note.sendNote + "</td></tr>";
                            sumPayment += Number(note.sendPay);
                        }
                        notes_html+= "<tr><td><b>Сумма по филиалу на странице:</b></td><td></td><td></td><td></td><td><b>" +
                                    sumPayment + " тг</b></td><td></td><td></td><td></td></tr>";
                    } else {
                        for(let i=0; i<notes.length; i++){
                            let note = notes[i];
                            if(i == 0){
                                if(note.paidEnd == "при получении"){
                                    notes_html += "<tr><td colspan='8'><b>" + note.toDepartment + "</b></td></tr>";
                                } else {
                                    notes_html += "<tr><td colspan='8'><b>" + note.outDepartment + "</b></td></tr>";
                                }
                            }
                            notes_html += "<tr><td>" + note.containerNumber + "</td><td>" + note.sendDate + "</td><td>" +
                                    note.outDepartment + "</td><td>" + note.toDepartment  + "</td><td>" + note.sendPay  +
                                    " тг</td><td>" + note.amount + "</td><td>" + note.paidEnd + "</td><td>" + note.sendNote + "</td></tr>";
                            sumPayment += Number(note.sendPay);
                        }
                        notes_html+= "<tr><td><b>Сумма по объекту на странице:</b></td><td></td><td></td><td></td><td><b>" +
                                    sumPayment + " тг</b></td><td></td><td></td><td></td></tr>";
                    }
                }
                table_body.prepend(notes_html);
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    }

    function showParcelsPaymentsPage(pageNumber, departmentId, branchId){
        let pageSize = $('#pageSize').val();
        let totalParcels = $('#totalParcels').val();
        $('#parcels_pages_title').html('');
        $('#parcels_table_body').html('');
        $.ajax({
            url: '../control/payment/send-parcels',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), branchId: branchId,
                    departmentId: departmentId, pageNumber: pageNumber, pageSize: pageSize},
            success: function(points) {
                let pages_title = $('#parcels_pages_title');
                let table_body = $('#parcels_table_body');
                if(points!=null && points.length>0){
                    let pages_html = getPagesHtml(pageNumber, pageSize, totalParcels);
                    pages_title.prepend(pages_html);
                    let sumPayment = 0;
                    let parcels_html = "<tr><td colspan='8'><b>" + points[0].departmentName + "</b></td></tr>";
                    $.each(points, function(key, point){
                        parcels_html += "<tr><td style='color: blue; text-decoration: underline;'>" + point.parcelNumber + "</td><td style='text-align: center;'>" +
                                point.sendTime + "</td><td>" + point.parcelType + "</td><td>" + point.outUser + "</td><td>" + point.toDepartmentName  + "</td><td>" +
                                point.parent  + "</td><td>" + point.payment + " тг</td><td>" + point.text + "</td></tr>";

                        sumPayment += Number(point.payment);
                    });
                    parcels_html += "<tr><td><b>Сумма по странице:</b></td><td></td><td></td><td></td><td></td><td></td><td><b>" + sumPayment + " тг</b></td><td></td></tr>";
                    table_body.prepend(parcels_html);
                }
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу даннных. Перегрузите страницу.");
            }
        });
    }

    function showAllParcelsPaymentsPage(pageNumber){
        let pageSize = $('#pageSize').val();
        let totalParcels = $('#totalParcels').val();
        $('#parcels_pages_title').html('');
        $('#parcels_table_body').html('');
        $.ajax({
            url: '../control/payment/send-all-parcels',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(),
                    pageNumber: pageNumber, pageSize: pageSize},
            success: function(points) {
                let pages_title = $('#parcels_pages_title');
                let table_body = $('#parcels_table_body');
                let sumPayment = 0;
                if(points!=null && points.length>0){
                    let pages_html = getPagesHtml(pageNumber, pageSize, totalParcels);
                    pages_title.prepend(pages_html);
                    let parcels_html = "";
                    let depName = "";
                    for(let i=0; i<points.length; i++){
                        let point = points[i];
                        if(depName != point.departmentName){
                            if(i>0){
                                parcels_html += "<tr><td><b>Сумма по филиалу на странице:</b></td><td></td><td></td><td></td>" +
                                        "<td></td><td></td><td><b>" + sumPayment + " тг</b></td><td></td></tr>";
                            }
                            parcels_html += "<tr><td colspan='8'><b>" + point.departmentName + "</b></td></tr>";
                            depName = point.departmentName;
                            sumPayment = 0;
                        }
                        parcels_html += "<tr><td style='color: blue; text-decoration: underline;'>" + point.parcelNumber +
                                "</td><td style='text-align: center;'>" + point.sendTime + "</td><td>" + point.parcelType + "</td><td>" + point.outUser +
                                "</td><td>" + point.toDepartmentName  + "</td><td>" + point.parent  + "</td><td>" +
                                point.payment + " тг</td><td>" + point.text + "</td></tr>";
                        sumPayment += Number(point.payment);
                    }
                    parcels_html += "<tr><td><b>Сумма по филиалу на странице:</b></td><td></td><td></td><td></td><td></td>" +
                            "<td></td><td><b>" + sumPayment + " тг</b></td><td></td></tr>";
                    table_body.prepend(parcels_html);
                }
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу даннных. Перегрузите страницу.");
            }
        });
    }

    function showDelayPage(pageNumber, departmentId){
        let pageSize = $('#pageSize').val();
        let totalNotes = $('#totalNotes').val();
        $('#delay_table_body').html('');
        $('#delay_pages_title').html('');
        let way = $('input[type="radio"][name="way"]:checked').val();
        $.ajax({
            url: '../control/delay/report-notes',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), pageNumber: pageNumber,
                    departmentId: departmentId, pageSize: pageSize, delayLimit: $('#delay_limit').val(), way: way},
            success: function(notes) {
                if(notes!=null && notes.length>0){
                    let notes_html = "";
                    let table_body = $('#delay_table_body');
                    let pages_title = $('#delay_pages_title');
                    let pages_html = getPagesHtml(pageNumber, pageSize, totalNotes);
                    pages_title.prepend(pages_html);
                    $.each(notes, function(key, note){
                        notes_html += "<tr><td style='color: blue; text-decoration: underline'>N" + note.id + "</td><td>" + note.containerNumber + "</td><td>" +
                        note.outDepartment + "</td><td style='text-align: center;'>" + note.sendTime + "</td><td>" + note.toDepartment  + "</td><td style='text-align: center;'>" +
                        note.arriveTime  + "</td><td style='text-align: center;'>" + note.delayTime + "</td><td>" + note.sendNote + "</td><td>" + note.arriveNote + "</td></tr>";
                    });
                    table_body.prepend(notes_html);
                }
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    }

    function showRoutePage(pageNumber){
        let pageSize = $('#pageSize').val();
        let totalNotes = $('#totalNotes').val();
        $('#route_pages_title').html('');
        $('#route_table_body').html('');
        $.ajax({
            url: '../control/route/report-notes',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), pageNumber: pageNumber,
                    containerId: $('#container_id').val(), pageSize: pageSize},
            success: function(notes) {
                if(notes!=null && notes.length>0){
                    let pages_title = $('#route_pages_title');
                    let pages_html = getPagesHtml(pageNumber, pageSize, totalNotes);
                    pages_title.html(pages_html);
                    let notes_html = "";
                    let table_body = $('#route_table_body');
                    $.each(notes, function(key, note){
                        notes_html += "<tr><td style='color: blue; text-decoration: underline'>N" + note.id + "</td><td>" +
                        note.sendDate + "</td><td>" + note.outDepartment + "</td><td>" + note.arriveDate + "</td><td>" + note.toDepartment  + "</td><td>" +
                        note.status  + "</td><td>" + note.sendNote + "</td><td>" + note.arriveNote + "</td></tr>";
                    });
                    table_body.prepend(notes_html);
                }
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    }

    $('#notes_table_body').on('click', function(event){
        let elem = event.target || event.srcElement;
        let prev = elem.previousElementSibling;
        if(prev!=null){
            let noteLine = prev.innerHTML;
            let pos = noteLine.indexOf("/");
            if(pos>0){
                let noteId = noteLine.substring(0, pos);
                let validNumber = /^[0-9]+$/;
                $('#changePay').css("display", "none");
                $('#changeToBranch').css("display", "none");
                $('#changeToDepartment').css("display", "none");
                $('#changeNote').css("display", "none");
                $('#changeArriveNote').css("display", "none");
                $('#changeBetweenNote').css("display", "none");
                $('#changeButton').css("display", "none");
                $('#changeArriveButton').css("display", "none");
                $('#changeBetweenButton').css("display", "none");
                $('#inputSendNote').val("");
                $('#inputPay').val("0");
                $('#inputArriveNote').val("");
                $('#inputBetweenNote').val("");
                if(validNumber.test(noteId)){
                    $.ajax({
                        url: '../user/load-data/note',
                        method: 'POST',
                        dataType: 'json',
                        data: {noteId: noteId},
                        success: function(containerNote) {
                            $('#show_note').css("display", "block");
                            $('#outDepartmentId').val(containerNote.outDepartmentId);
                            $('#toDepartmentId').val(containerNote.toDepartmentId);
                            $('#outUserId').val(containerNote.outUserId);
                            $('#toUserId').val(containerNote.toUserId);
                            $('#containerNoteId').text(containerNote.id);
                            $('#containerNumber').text(containerNote.containerNumber);
                            $('#thermometer').text(containerNote.thermometer);
                            $('#outDepartment').html(containerNote.outDepartment);
                            $('#sendTime').html(containerNote.sendTime);
                            $('#outUser').html(containerNote.outUser);
                            $('#sendNote').html(containerNote.sendNote);
                            $('#inputSendNote').val(containerNote.sendNote);
                            $('#paidEnd').html(containerNote.paidEnd);
                            $('#sendPay').html(containerNote.sendPay + "  тенге");
                            $('#inputPay').val(containerNote.sendPay);
                            $('#toDepartment').html(containerNote.toDepartment);
                            $('#arriveTime').html(containerNote.arriveTime);
                            $('#toUser').html(containerNote.toUser);
                            $('#status').html(containerNote.status);
                            $('#arriveNote').html(containerNote.arriveNote);
                            var arriveDate = containerNote.arriveTime;
                            comparingDate(arriveDate, containerNote.paidEnd);
                            var status = containerNote.status;
                            if(status.indexOf("В дороге")==0){
                                if($('#department_id').val() == $('#outDepartmentId').val()){
                                    if(containerNote.paidEnd == "при отгрузке"){
                                        $('#changePay').css("display", "table-row");
                                    }
                                    $('#changeToBranch').css("display", "table-row");
                                    $('#changeToDepartment').css("display", "table-row");
                                    $('#changeNote').css("display", "table-row");
                                    $('#changeButton').css("display", "table-row");
                                    $('#select_change_branch').val(containerNote.toBranchId);
                                    $('#select_change_branch').trigger("change");
                                }
                            }
                            let points_body = $('#points_table_body');
                            let points_html = "";
                            points_body.html('');
                            let passPoints = containerNote.betweenPoints;
                            if(passPoints!=null && passPoints.length>0){
                                let betweenDepartmentId;
                                let departmentId = $('#department_id').val();
                                $.each(passPoints, function(key, point){
                                    points_html += "<tr><td>" + point.departmentName + "</td><td style='text-align: center;'>" + point.passTime +
                                            "</td><td>" + point.passUser + "</td><td>" + point.passNote + "</td></tr>";
                                    betweenDepartmentId = point.departmentId;
                                    if(status.indexOf("В дороге")==0 && betweenDepartmentId == departmentId){
                                        $('#changeBetweenNote').css("display", "table-row");
                                        $('#changeBetweenButton').css("display", "table-row");
                                        $('#passUserId').val(point.passUserId);
                                    }
                                });
                            } else {
                                points_html = "<tr><td colspan='5'>Нет промежуточных объектов регистрации</td></tr>";
                            }
                            points_body.prepend(points_html);
                            $("tr[tabindex=0]").focus();
                       },
                        error:  function(response) {
                            $('#result_line').html("Ошибка обращения в базу даннных. Перегрузите страницу.");
                            $('#show_note').css("display", "none");
                        }
                    });
                }
            }
        }
    });

    function comparingDate(arriveTime, paidEnd){
        let point = arriveTime.indexOf(".");
        let arriveDay = arriveTime.substring(0, point);
        arriveTime = arriveTime.substring(point+1);
        point = arriveTime.indexOf(".");
        let arriveMonth = arriveTime.substring(0, point) - 1;
        arriveTime = arriveTime.substring(point+1);
        point = arriveTime.indexOf(" ");
        let arriveYear = arriveTime.substring(0, point);
        arriveTime = arriveTime.substring(point+1);
        point = arriveTime.indexOf(":");
        let arriveHour = arriveTime.substring(0, point);
        arriveTime = arriveTime.substring(point+1);
        let arriveDate = new Date(arriveYear, arriveMonth, arriveDay, arriveHour, arriveTime, "00");
        let arriveValue = arriveDate.getTime() + 86400000;
        let currentValue = new Date().getTime();
        if(arriveValue>currentValue){
            if($('#department_id').val() == $('#toDepartmentId').val()){
                $('#changeArriveNote').css("display", "table-row");
                $('#changeArriveButton').css("display", "table-row");
                if(paidEnd == "при получении"){
                    $('#changePay').css("display", "table-row");
                }
            }
        }
    };

    $('#btn_search').on('click', function(){
        let validNumber = /^[0-9]+$/;
        let containerNumber = $('#container_number').val();
        $('#line_cut_note').click();;
        $('#journal_pages_title').html('');
        $('#notes_table_body').html('');
        if(containerNumber.length>0 && validNumber.test(containerNumber)){
            $('#btn_search').css("display", "none");
            $.ajax({
                url: '../user/check-container/find-container',
                method: 'POST',
                dataType: 'json',
                data: {findNumber: containerNumber, endDate: $('#endDate').val()},
                success: function(notes) {
                    $('#btn_export_excel').css("display", "none");
                    $('#btn_search').css("display", "block");
                    let notes_html = "";
                    let table_body = $('#notes_table_body');
                    if(notes!=null && notes.length>0){
                        $.each(notes, function(key, note){
                            notes_html += "<tr><td style='text-align: end;'><span style='color: white'>" + note.id + "/</span><span style='color: blue; text-decoration: underline'>" +
                            note.containerNumber + "</span></td><td style='width: 10%; text-align: end; padding-end: 20px;'>" + note.thermometer + "</td><td style='text-align: center;'>" +
                            note.sendTime + "</td><td style='text-align: center;'>" + note.arriveTime + "</td><td>" + note.outDepartment  + "</td><td>" + note.toDepartment  + "</td><td>" + note.status + "</td></tr>";
                        });
                        table_body.prepend(notes_html);
                    } else {
                        search_html+= "<tr><td colspan='7'><b>Информация о термоконтейнере не найдена. Проверьте правильность ввода номера</b></td></tr>";
                    }
                },
                error:  function(response) {
                    $('#btn_search').css("display", "block");
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка поиска по базе. Перегрузите страницу.");
                }
            });
        } else {
            $('#result_line').html("Внесите номер термоконтейнера.");
        }
    });

    $('#delay_table_body').on('click', function(event){
        let elem = event.target || event.srcElement;
        let noteId = elem.innerHTML;
        let show_points = document.getElementById("show_points");
        if(noteId.indexOf("N")==0){
            noteId = noteId.substring(1);
            $.ajax({
                url: '../user/load-data/note',
                method: 'POST',
                dataType: 'json',
                data: {noteId: noteId},
                success: function(containerNote) {
                    show_points.style.display = "block";
                    window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
                    $('#containerNoteId').text(containerNote.id);
                    $('#containerNumber').text(containerNote.containerNumber);
                    let points_body = $('#points_body');
                    let points_html = "";
                    points_body.html('');
                    let passPoints = containerNote.betweenPoints;
                    if(passPoints!=null && passPoints.length>0){
                        $.each(passPoints, function(key, point){
                            points_html += "<tr><td>" + point.departmentName + "</td><td style='text-align: center;'>" + point.passTime +
                                    "</td><td>" + point.passUser + "</td><td>" + point.passNote + "</td></tr>";
                        });
                    } else {
                        points_html = "<tr><td colspan='5'>Нет промежуточных объектов регистрации</td></tr>";
                    }
                    points_body.prepend(points_html);
               },
                error:  function(response) {
                    $('#result_line').html("Ошибка обращения в базу даннных. <br>Для получения информации о движении термоконтейнера кликните по ячейке с номером.");
                    show_points.style.display = "none";
                }
            });
        }
    });

    $('#route_table_body').on('click', function(event){
        let elem = event.target || event.srcElement;
        let noteId = elem.innerHTML;
        let show_route_points = document.getElementById("show_route_points");
        if(noteId.indexOf("N")==0){
            noteId = noteId.substring(1);
            $.ajax({
                url: '../user/load-data/note',
                method: 'POST',
                dataType: 'json',
                data: {noteId: noteId},
                success: function(containerNote) {
                    show_route_points.style.display = "block";
                    window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
                    $('#containerNoteId').text(containerNote.id);
                    let points_body = $('#route_points_body');
                    let points_html = "";
                    points_body.html('');
                    let passPoints = containerNote.betweenPoints;
                    if(passPoints!=null && passPoints.length>0){
                        $.each(passPoints, function(key, point){
                            points_html += "<tr><td>" + point.departmentName + "</td><td  style='text-align: center;'>" + point.passTime +
                                    "</td><td>" + point.passUser + "</td><td>" + point.passNote + "</td></tr>";
                        });
                    } else {
                        points_html = "<tr><td colspan='5'>Нет промежуточных объектов регистрации</td></tr>";
                    }
                    points_body.prepend(points_html);
               },
                error:  function(response) {
                    $('#result_line').html("Ошибка обращения в базу даннных. <br>Для получения информации о движении термоконтейнера кликните по ячейке с номером.");
                    show_route_points.style.display = "none";
                }
            });
        }
    });

    $('#parcels_table_body').on('click', function(event){
        let elem = event.target || event.srcElement;
        let parcelNumber = elem.innerHTML;
        let validNumber = /^[0-9]+$/;
        if(parcelNumber.length==8 && validNumber.test(parcelNumber.substring(1))){
            $.ajax({
                url: '../user/load-data/search-parcel',
                method: 'POST',
                dataType: 'json',
                data: {findNumber: parcelNumber},
                success: function(parcel) {
                    document.getElementById("parcel_info_field").style.display = "block";
                    window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' });
                    let points_html = "";
                    let table_body = $('#points_table_body');
                    table_body.html('');
                    if(parcel!=null){
                        $('#parcel_number').text(parcel.parcelNumber);
                        $('#sender').text(parcel.outDepartment);
                        $('#destination').text(parcel.destinationName);
                        $('#parcel_type').text(parcel.parcelType);
                        $('#dimensions').text(parcel.dimensions);
                        $('#send_date').text(parcel.sendDateTime);
                        $('#status').text(parcel.status);
                        let points = parcel.parcelPoints;
                        $.each(points, function(key, point){
                            points_html += "<tr><td style='font-weight: bold;'>" + point.departmentName + "</td><td style='text-align: center;'>" +
                            point.arriveTime + "</td><td>" + point.intoUser + "</td><td style='text-align: center;'>" + point.sendTime  + "</td><td>" +
                            point.outUser  + "</td><td>" + point.parent + "</td><td>" + point.payment + " тг</td><td>" + point.text + "</td></tr>";
                        });
                        table_body.prepend(points_html);
                    } else{
                        document.getElementById("parcel_info_field").style.display = "none";
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    }
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
        }
    });

    $('#journal_pages_title').on('click', function(event){
        let elem = event.target || event.srcElement;
        let cellContent = elem.innerHTML;
        let pageSize = $('#pageSize').val();
        let totalNotes = $('#totalNotes').val();
        let currentRow = $('#journal_pages_title').text();
        let pageNumber = getPageNumber(totalNotes, pageSize, cellContent, currentRow);
        let departmentId = $('#department_id').val();
        if($('#department_id').val()==1 && $('#all_checkbox').is(':checked')==false){
            departmentId = $('#select_department').val();
        }
        showNotesPage(pageNumber, departmentId);

    });

    function getPageNumber(totalNotes, pageSize, cellContent, currentRow){
        let totalPages;
        if(totalNotes%pageSize>0){
            totalPages = parseInt(totalNotes/pageSize) + 1;
        } else{
            totalPages = parseInt(totalNotes/pageSize);
        }
        let currentNoteNumber;
        let pos1;
        let pos2;
        pos1 = cellContent.indexOf("(") + 1;
        pos2 = cellContent.indexOf("..");
        if(pos2>1){
            currentNoteNumber = cellContent.substring(pos1, pos2);
            pageNumber = parseInt(currentNoteNumber/pageSize);
        } else{
            let pos = currentRow.indexOf(". .");
            if(pos < 6) {
                currentRow = currentRow.substring(12);
                pos1 = currentRow.indexOf("(") + 1;
                pos2 = currentRow.indexOf("..");
                currentNoteNumber = currentRow.substring(pos1, pos2);
                pageNumber = parseInt(currentNoteNumber/pageSize);
                if(pageNumber>3){
                   pageNumber-=3;
                } else{
                    pageNumber--;
                }
            } else {
                currentRow = currentRow.substring(0, pos-3);
                while(currentRow.indexOf("(")>=0){
                    currentRow = currentRow.substring(currentRow.indexOf("(")+1);
                }
                pos1 = currentRow.indexOf("..") + 3;
                pos2 = currentRow.indexOf(")");
                currentNoteNumber = currentRow.substring(pos1, pos2);
                pageNumber = parseInt(currentNoteNumber/pageSize);
                if((totalPages - pageNumber)>3){
                   pageNumber+=3;
                } else{
                    pageNumber++;
                }
            }
        }
        return pageNumber;
    }

    function getPagesHtml(pageNumber, pageSize, totalNotes){
        let totalPages;
        if(totalNotes%pageSize>0){
            totalPages = parseInt(totalNotes/pageSize) + 1;
        } else{
            totalPages = parseInt(totalNotes/pageSize);
        }
        let pages_html = "<tr>";
        if(pageNumber>2){
            pages_html+="<td class='pages'> ( . . . )  </td>";
        }
        for(let i=0; i<totalPages-1; i++){
            if(i - pageNumber<3 && pageNumber - i<3){
                if(i - pageNumber==0){
                    pages_html+="<td class='pages'><b>  (" + (Number(i*pageSize)+1) + "..." + (i+1)*pageSize + ")  </b></td>";
                } else {
                    pages_html+="<td class='pages'>  (" + (Number(i*pageSize)+1) + "..." + (i+1)*pageSize + ")  </td>";
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
        return pages_html;
    }

    $('#payment_pages_title').on('click', function(event){
        let elem = event.target || event.srcElement;
        let cellContent = elem.innerHTML;
        let pageSize = $('#pageSize').val();
        let totalPayments = $('#totalPayments').val();
        let currentRow = $('#payment_pages_title').text();
        let pageNumber = getPageNumber(totalPayments, pageSize, cellContent, currentRow);
        let branchId = $('#branch_id').val();
        let departmentId = $('#department_id').val();
        if(departmentId==1){
            if($('#all_checkbox').is(':checked')==false){
                branchId = $('#select_branch').val();
                if($('#branch_checkbox').is(':checked')==false){
                    departmentId = $('#select_department').val();
                }
            }
        }
        showPaymentsPage(pageNumber, departmentId, branchId);
    });

    $('#parcels_pages_title').on('click', function(event){
         let elem = event.target || event.srcElement;
         let cellContent = elem.innerHTML;
         let pageSize = $('#pageSize').val();
         let totalParcels = $('#totalParcels').val();
         let currentRow = $('#parcels_pages_title').text();
         let pageNumber = getPageNumber(totalParcels, pageSize, cellContent, currentRow);
         let branchId = $('#branch_id').val();
         let departmentId = $('#department_id').val();
         if(departmentId == 1 && $('#all_checkbox').is(':checked')==false){
             departmentId = $('#select_department').val();
         }
         if(departmentId!=1){
            showParcelsPaymentsPage(pageNumber, departmentId, branchId);
         } else {
            showAllParcelsPaymentsPage(pageNumber);
         }
     });

    $('#delay_pages_title').on('click', function(event){
        let elem = event.target || event.srcElement;
        let cellContent = elem.innerHTML;
        let pageSize = $('#pageSize').val();
        let totalNotes = $('#totalDelayNotes').val();
        let currentRow = $('#delay_pages_title').text();
        let pageNumber = getPageNumber(totalNotes, pageSize, cellContent, currentRow);
        let departmentId = $('#department_id').val();
        if(departmentId == 1 && $('#all_checkbox').is(':checked')==false){
            departmentId = $('#select_department').val();
        }
        showDelayPage(pageNumber, departmentId)
    });

    $('#route_pages_title').on('click', function(event){
        document.getElementById("show_route_points").style.display = "none";
        let elem = event.target || event.srcElement;
        let cellContent = elem.innerHTML;
        let pageSize = $('#pageSize').val();
        let totalNotes = $('#totalRouteNotes').val();
        let currentRow = $('#route_pages_title').text();
        let pageNumber = getPageNumber(totalNotes, pageSize, cellContent, currentRow);
        showRoutePage(pageNumber);
    });

    $('#select_event').on('change', function(){
        if($('#select_event').val()==1){
            $('#logs_name').html("изменение времени доставки");
            $('#target').html("Объект назначения");
        } else if($('#select_event').val()==2){
            $('#logs_name').html("изменение прав доступа");
            $('#target').html("Пользователь");
        } else {
            $('#logs_name').html("");
            $('#target').html("");
            $('#logs_table_body').html("");
            $('#logs_pages_title').html("");
        }
    });

    $('#reload_logs').on('click', function(){
        $('#totalLogsNotes').val(0);
        $('#logs_pages_title').html('');
        $('#logs_table_body').html('');
        if($('#select_event').val()>0){
            $.ajax({
                url: '../admin/load-data/totalLogs',
                method: 'POST',
                dataType: 'json',
                data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(),
                    branchId: $('#select_branch').val(), eventId: $('#select_event').val()},
                success: function(totalElements) {
                    let totalLogs = parseInt(totalElements);
                    if(totalLogs>0){
                        $('#totalLogs').val(totalLogs);
                        showLogsPage(0, $('#select_branch').val());
                    }
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу даннных. Перегрузите страницу.");
                }
            });
        } else {
            $('#result_line').html("Выберите событие.");
        }
    });

    $('#logs_pages_title').on('click', function(event){
        let pageNumber;
        let elem = event.target || event.srcElement;
        let cellContent = elem.innerHTML;
        let pageSize = $('#pageSize').val();
        let totalLogs = $('#totalLogs').val();
        let currentRow = $('#logs_pages_title').text();
        pageNumber = getPageNumber(totalLogs, pageSize, cellContent, currentRow);
        showLogsPage(pageNumber, $('#branch_id').val());

    });

    function showLogsPage(pageNumber, branchId){
        let pageSize = $('#pageSize').val();
        let totalLogs = $('#totalLogs').val();
        $('#logs_pages_title').html('');
        $('#logs_table_body').html('');
        $.ajax({
            url: '../admin/load-data/logs-journal',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), branchId: $('#select_branch').val(),
                    eventId: $('#select_event').val(), pageNumber: pageNumber, pageSize: pageSize},
            success: function(logs) {
                let logs_html = "";
                let pages_title = $('#logs_pages_title');
                let table_body = $('#logs_table_body');
                if(logs.length>0){
                    let pages_html = getPagesHtml(pageNumber, pageSize, totalLogs);
                    pages_title.prepend(pages_html);
                    $.each(logs, function(key, log){
                        logs_html += "<tr><td style='color: blue; text-decoration: underline'>" + log.id + "</td><td style='text-align: center;'>" +
                        log.time + "</td><td>" + log.branch + "</td><td>" + log.department  + "</td><td>" + log.target  +
                        "</td><td>" + log.oldValue + "</td><td>" + log.newValue + "</td><td>" + log.editor + "</td></tr>";
                    });
                    table_body.prepend(logs_html);
                }
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    }

    $('#btn_export_excel').on('click', function(){
        if($('#all_checkbox').is(':checked')==false && $('#select_branch').val()==0){
             $('#result_line').html("Выберите филиал или отметьте все объекты");
        } else {
            let departmentId = $('#department_id').val();
            if(departmentId==1){
                if($('#all_checkbox').is(':checked')==false){
                    departmentId = $('#select_department').val();
                }
            }
            $('#exportDepartmentId').val(departmentId);
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Ожидайте завершение выборки и скачивания данных.");
            $('#export_excel').submit();
        }
    });

    $('#btn_export_log').on('click', function(){
        if($('#select_event').val()>0){
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Ожидайте завершение выборки и скачивания данных.");
            $('#export_logs').submit();
        } else{
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Выберите вид отчета.");
        }
    });

    $('#btn_export_payment').on('click', function(){
        if($('#all_checkbox').is(':checked')==false && $('#select_branch').val()==0){
             $('#result_line').html("Выберите филиал или отметьте все объекты");
        } else {
            let departmentId = $('#department_id').val();
            let branchId = $('#branch_id').val();
            if(departmentId==1){
                if($('#all_checkbox').is(':checked')==false){
                    departmentId = $('#select_department').val();
                    branchId = $('#select_branch').val();
                }
            }
            $('#exportDepartmentId').val(departmentId);
            $('#exportBranchId').val(branchId);
            $('#export_payment').attr('action', 'payment/containers-exportExcel');
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Ожидайте завершение выборки и скачивания данных.");
            $('#export_payment').submit();
        }
    });

    $('#btn_export_parcels').on('click', function(){
        if($('#all_checkbox').is(':checked')==false && $('#select_branch').val()==0){
             $('#result_line').html("Выберите филиал или отметьте все объекты");
        } else {
            let departmentId = $('#department_id').val();
            let branchId = $('#branch_id').val();
            if(departmentId==1){
                if($('#all_checkbox').is(':checked')==false){
                    departmentId = $('#select_department').val();
                    branchId = $('#select_branch').val();
                }
            }
            $('#exportDepartmentId').val(departmentId);
            $('#exportBranchId').val(branchId);
            $('#export_payment').attr('action', 'payment/parcels-exportExcel');
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Ожидайте завершение выборки и скачивания данных.");
            $('#export_payment').submit();
        }
    });

    $('#btn_export_delay').on('click', function(){
        if($('#all_checkbox').is(':checked')==false && $('#select_branch').val()==0){
             $('#result_line').html("Выберите филиал или отметьте все объекты");
        } else {
            var departmentId = $('#department_id').val();
            if(departmentId==1){
                if($('#all_checkbox').is(':checked')==false){
                    departmentId = $('#select_department').val();
                }
            }
            $('#exportDepartmentId').val(departmentId);
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Ожидайте завершение выборки и скачивания данных.");
            $('#export_delay').submit();
        }
    });

    $('#btn_export_route').on('click', function(){
        if($('#all_checkbox').is(':checked')==false && $('#select_branch').val()==0){
             $('#result_line').html("Выберите филиал или отметьте все объекты");
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            if($('#container_id').val()>0){
                $('#result_line').html("Ожидайте завершение выборки и скачивания данных.");
                $('#export_route').submit();
            } else {
                $('#result_line').html("Внесите номер термоконтейнера.");
            }
        }
    });

    $('#select_container').on('change', function(){
        $('#container_number').val($('#select_container option:selected').text());
        $('#route_pages_title').html("");
        $('#route_table_body').html("");
        $('#show_route_points').css({display: "none"});
        loadContainer($('#container_number').val());
    });

    let container_list = new Array();
    let numbers_length = 0;

    $('#container_number').on('input', function(){
        let container_number = $('#container_number').val();
        if(container_number.length<4){
            numbers_length = container_number.length;
            $('#select_container').css("display", "none");
        } else if(container_number.length==4){
            getContainers(container_number);
            numbers_length = container_number.length;
        } else if(container_number.length==8){
            loadContainer(container_number);
        } else {
            if(container_number.length < numbers_length){
                getContainers(container_number);
            } else {
                for(let i=0; i<container_list.length; i++){
                    let container = container_list[i];
                    const index = container.containerNumber.indexOf(container_number);
                    if(index<0){
                        container_list.splice(i, 1);
                        i--;
                    }
                }
                loadSelect(container_list);
            }
            numbers_length = container_number.length;
        }
    });

    function loadContainer(container_number){
            $.ajax({
                url: '../user/load-data/container',
                method: 'POST',
                dataType: 'json',
                data: {containerNumber: container_number},
                success: function(container) {
                    let containerId = 0 + container.id;
                    $('#container_id').val(containerId);
                    $('#routeContainerNumber').text(container.containerNumber);
                    $('#containerValue').text(container.value);
                    $('#registrationDate').text(container.registrationDate);
                    $('#containerDepartment').text(container.departmentName + ", " + container.branchName);
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
    }

    function getContainers(container_number){
        $.ajax({
            url: '../user/load-data/containers',
            method: 'POST',
            dataType: 'json',
            data: {containerNumber: container_number},
            success: function(containers) {
                container_list = containers;
                if(containers!=null && containers.length>0){
                    $('#select_container').css("display", "block");
                    loadSelect(container_list);
                }
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    }

    function loadSelect(container_list){
        $('#select_container').empty();
        $('#select_container').append($("<option></option>").attr("value", 0).text("Выберите из списка"));
        $.each(container_list, function(key, container){
            $('#select_container').append($("<option></option>").attr("value", key).text(container.containerNumber));
        });
//        for(let i=0; i<container_list.length; i++){
//            let container = container_list[i];
//            let key = container.id;
//            let value = container.containerNumber;
//            $('#select_container').append($("<option></option>").attr("value", key).text(value));
//        }
    }

        $('#container_clean').on('click', function(){
            $('#container_number').val("");
            $('#routeContainerNumber').text("");
            $('#containerValue').text("");
            $('#registrationDate').text("");
            $('#containerDepartment').text("");
            $('#route_pages_title').html("");
            $('#route_table_body').html("");
            $('#show_route_points').css({display: "none"});
            $('#select_container').css({display: "none"});
            $('#container_id').val(0);
        });

});




