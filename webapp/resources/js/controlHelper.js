$(document).ready(function(){

    $('#reload_payment').on('click', function(){
        var departmentId = $('#department_id').val();
        if(departmentId==1){
            if($('#department_checkbox').is(':checked')==false && $('#select_department').val()!=null){
                departmentId = $('#select_department').val();
            }
        }
        var pageNumber = 0;
        var pageSize = $('#paymentPageSize').val();
        var totalNotes;
        var totalPages;
        $.ajax({
            url: '/control/payment/report-totalNotes',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), departmentId: departmentId},
            success: function(totalElements) {
                totalNotes = parseInt(totalElements);
                var notes_html = "";
                var payment_table_body = $('#payment_table_body');
                var pages_html = "";
                var pages_payment_title = $('#pages_payment_title');
                if(totalNotes>0){
                    $('#totalPaymentNotes').val(totalNotes);
                    if(totalNotes%pageSize>0){
                        totalPages = parseInt(totalNotes/pageSize) + 1;
                    } else{
                        totalPages = parseInt(totalNotes/pageSize);
                    }
                    $.ajax({
                        url: '/control/payment/report-notes',
                        method: 'POST',
                        dataType: 'json',
                        data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(),
                                departmentId: departmentId, pageNumber: pageNumber, pageSize: pageSize},
                        success: function(notes) {
                            pages_payment_title.html('');
                            payment_table_body.html('');
                            pages_html = getPagesHtml(pageNumber, totalPages, pageSize, totalNotes);
                            pages_payment_title.prepend(pages_html);
                            var sumPayment = 0;
                            $.each(notes, function(key, note){
                                notes_html += "<tr><td>" + note.id + "</td><td>" +
                                note.sendDate + "</td><td>" + note.outDepartment + "</td><td>" + note.toDepartment  + "</td><td>" +
                                note.sendPay  + "</td><td>" + note.sendNote + "</td></tr>";
                                sumPayment += Number(note.sendPay);
                            });
                            notes_html+= "<tr><td colspan='4'>Сумма по странице:</td><td>" + sumPayment + "</td><td></td></tr>";
                            payment_table_body.prepend(notes_html);
                        },
                        error:  function(response) {
                            alert("Ошибка обращения в базу данных. Повторите.");
                        }
                    });
                } else {
                    payment_table_body.html('');
                    pages_payment_title.html('');
                    notes_html = "<tr><td colspan='6'>Отсутствуют данные по оплате перевозки на объекте за выбранный период</td></tr>";
                    payment_table_body.prepend(notes_html);
                }
            },
            error:  function(response) {
                alert("Ошибка обращения в базу данных. Повторите.");
            }
        });
    });

    $('#reload_delay').on('click', function(){
        var departmentId = $('#department_id').val();
        if(departmentId==1){
            if($('#department_checkbox').is(':checked')==false && $('#select_department').val()!=null){
                departmentId = $('#select_department').val();
            }
        }
        var pageNumber = 0;
        var pageSize = $('#delayPageSize').val();
        var totalNotes;
        var totalPages;
        $.ajax({
            url: '/control/delay/report-totalNotes',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(),
                    departmentId: departmentId, delayLimit: $('#delay_limit').val()},
            success: function(totalElements) {
                totalNotes = parseInt(totalElements);
                var notes_html = "";
                var delay_table_body = $('#delay_table_body');
                var pages_delay_html = "";
                var pages_delay_title = $('#pages_delay_title');
                if(totalNotes>0){
                    $('#totalDelayNotes').val(totalNotes);
                    if(totalNotes%pageSize>0){
                        totalPages = parseInt(totalNotes/pageSize) + 1;
                    } else{
                        totalPages = parseInt(totalNotes/pageSize);
                    }
                    $.ajax({
                        url: '/control/delay/report-notes',
                        method: 'POST',
                        dataType: 'json',
                        data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), pageNumber: pageNumber,
                                departmentId: departmentId, pageSize: pageSize, delayLimit: $('#delay_limit').val()},
                        success: function(notes) {
                            pages_delay_title.html('');
                            delay_table_body.html('');
                            pages_delay_html = getPagesHtml(pageNumber, totalPages, pageSize, totalNotes);
                            pages_delay_title.prepend(pages_delay_html);
                            $.each(notes, function(key, note){
                                notes_html += "<tr><td style='color: blue; text-decoration: underline'>" + note.id + "</td><td>" +
                                note.containerNumber + "</td><td>" + note.outDepartment + "</td><td>" + note.sendTime + "</td><td>" + note.toDepartment  + "</td><td>" +
                                note.arriveTime  + "</td><td>" + note.delayTime + "</td><td>" + note.sendNote + "</td><td>" + note.arriveNote + "</td></tr>";
                            });
                            delay_table_body.prepend(notes_html);
                        },
                        error:  function(response) {
                            alert("Ошибка обращения в базу данных. Повторите.");
                        }
                    });
                } else {
                    delay_table_body.html('');
                    pages_delay_title.html('');
                    notes_html = "<tr><td colspan='9'>Отсутствуют данные по опозданиям прибытия за выбранный период</td></tr>";
                    delay_table_body.prepend(notes_html);
                }
            },
            error:  function(response) {
                alert("Ошибка обращения в базу данных. Повторите.");
            }
        });
    });

    $('#select_container').on('change', function(){
        $('#route_table_body').html('');
        var show_route_points = document.getElementById("show_route_points");
        show_route_points.style.display = "none";
        $.ajax({
            url: '/user/load-data/container',
            method: 'POST',
            dataType: 'json',
            data: {containerId: $('#select_container').val()},
            success: function(container) {
                $('#routeContainerNumber').text(container.containerNumber);
                $('#containerValue').text(container.value);
                $('#registrationDate').text(container.registrationDate);
                $('#containerDepartment').text(container.departmentName + ", " + container.branchName);
            },
            error:  function(response) {
               alert("Ошибка обращения в базу данных. Повторите.");
            }
        });
    });

    $('#reload_route').on('click', function(){
        var show_route_points = document.getElementById("show_route_points");
        show_route_points.style.display = "none";
        var pageNumber = 0;
        var pageSize = $('#routePageSize').val();
        var totalNotes;
        var totalPages;
        $.ajax({
            url: '/control/route/report-totalNotes',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), containerId: $('#select_container').val()},
            success: function(totalElements) {
                totalNotes = parseInt(totalElements);
                var notes_html = "";
                var route_table_body = $('#route_table_body');
                var pages_route_html = "";
                var pages_route_title = $('#pages_route_title');
                if(totalNotes>0){
                    $('#totalRouteNotes').val(totalNotes);
                    if(totalNotes%pageSize>0){
                        totalPages = parseInt(totalNotes/pageSize) + 1;
                    } else{
                        totalPages = parseInt(totalNotes/pageSize);
                    }
                    $.ajax({
                        url: '/control/route/report-notes',
                        method: 'POST',
                        dataType: 'json',
                        data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), pageNumber: pageNumber,
                                containerId: $('#select_container').val(), pageSize: pageSize},
                        success: function(notes) {
                            pages_route_title.html('');
                            route_table_body.html('');
                            pages_route_html = getPagesHtml(pageNumber, totalPages, pageSize, totalNotes);
                            pages_route_title.prepend(pages_route_html);
                            $.each(notes, function(key, note){
                                notes_html += "<tr><td style='color: blue; text-decoration: underline'>" + note.id + "</td><td>" +
                                note.sendDate + "</td><td>" + note.outDepartment + "</td><td>" + note.arriveDate + "</td><td>" + note.toDepartment  + "</td><td>" +
                                note.status  + "</td><td>" + note.sendNote + "</td><td>" + note.arriveNote + "</td></tr>";
                            });
                            route_table_body.prepend(notes_html);
                        },
                        error:  function(response) {
                            alert("Ошибка обращения в базу данных. Повторите.");
                        }
                    });
                } else {
                    route_table_body.html('');
                    pages_route_title.html('');
                    notes_html = "<tr><td colspan='8'>Отсутствуют данные по перемещениям термоконтейнера за выбранный период</td></tr>";
                    route_table_body.prepend(notes_html);
                }
            },
            error:  function(response) {
                alert("Ошибка обращения в базу данных. Повторите.");
            }
        });
    });

    function getPagesHtml(pageNumber, totalPages, pageSize, totalNotes){
        var pages_html = "<tr>";
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
        return pages_html;
    };


});
