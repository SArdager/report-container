window.addEventListener("load", function(){
    $('#notes_table_body').on('click', function(event){
        var elem = event.target || event.srcElement;
        var noteId = elem.innerHTML;
        var validNumber = /^[0-9]+$/;
        var show_note = document.getElementById("show_note");
        var changePay = document.getElementById("changePay");
        var changeToBranch = document.getElementById("changeToBranch");
        var changeToDepartment = document.getElementById("changeToDepartment");
        var changeNote = document.getElementById("changeNote");
        var changeArriveNote = document.getElementById("changeArriveNote");
        var changeBetweenNote = document.getElementById("changeBetweenNote");
        var changeButton = document.getElementById("changeButton");
        var changeArriveButton = document.getElementById("changeArriveButton");
        var changeBetweenButton = document.getElementById("changeBetweenButton");
        changePay.style.display = 'none';
        changeToBranch.style.display = 'none';
        changeToDepartment.style.display = 'none';
        changeNote.style.display = 'none';
        changeArriveNote.style.display = 'none';
        changeBetweenNote.style.display = 'none';
        changeButton.style.display = 'none';
        changeArriveButton.style.display = 'none';
        changeBetweenButton.style.display = 'none';
        $('#inputSendNote').val("");
        $('#inputPay').val("0");
        $('#inputArriveNote').val("");
        $('#inputBetweenNote').val("");
        if(validNumber.test(noteId)){
            $.ajax({
                url: '/user/load-data/note',
                method: 'POST',
                dataType: 'json',
                data: {noteId: noteId},
                success: function(containerNote) {
                    show_note.style.display = "block";
                    $('#outDepartmentId').val(containerNote.outDepartmentId);
                    $('#toDepartmentId').val(containerNote.toDepartmentId);
                    $('#outUserId').val(containerNote.outUserId);
                    $('#toUserId').val(containerNote.toUserId);
                    $('#containerNoteId').text(containerNote.id);
                    $('#containerNumber').text(containerNote.containerNumber);
                    $('#outDepartment').html(containerNote.outDepartment);
                    $('#sendTime').html(containerNote.sendTime);
                    $('#outUser').html(containerNote.outUser);
                    $('#sendNote').html(containerNote.sendNote);
                    $('#sendPay').html(containerNote.sendPay + "  тенге");
                    $('#toDepartment').html(containerNote.toDepartment);
                    $('#arriveTime').html(containerNote.arriveTime);
                    $('#toUser').html(containerNote.toUser);
                    $('#status').html(containerNote.status);
                    $('#arriveNote').html(containerNote.arriveNote);
                    var arriveDate = containerNote.arriveTime;
                    comparingDate(arriveDate);
                    var status = containerNote.status;
                    if(status.indexOf("В дороге")==0){
                        if($('#department_id').val() == $('#outDepartmentId').val()){
                            changeNote.style.display = 'table-row';
                            changePay.style.display = 'table-row';
                            changeToBranch.style.display = 'table-row';
                            changeToDepartment.style.display = 'table-row';
                            changeButton.style.display = 'table-row';
                            $('#select_change_branch').trigger("change");
                        }
                    }
                    var points_body = $('#points_table_body');
                    var points_html = "";
                    points_body.html('');
                    var passPoints = containerNote.betweenPoints;
                    if(passPoints!=null && passPoints.length>0){
                        var betweenDepartmentId;
                        var departmentId = $('#department_id').val();
                        $.each(passPoints, function(key, point){
                            points_html += "<tr><td>" + point.departmentName + "</td><td>" + point.passTime +
                                    "</td><td>" + point.passUser + "</td><td>" + point.passNote + "</td></tr>";
                            betweenDepartmentId = point.departmentId;
                            if(betweenDepartmentId == departmentId){
                                changeBetweenNote.style.display = 'table-row';
                                changeBetweenButton.style.display = 'table-row';
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
                    $('#result_line').html("Ошибка обращения в базу даннных. \nДля получения информации о движении термоконтейнера кликните по ячейке с номером.");
                    show_note.style.display = "none";
                }
            });
        }
    });

    function comparingDate(arriveTime){
        let point = arriveTime.indexOf(".");
        var arriveDay = arriveTime.substring(0, point);
        arriveTime = arriveTime.substring(point+1);
        point = arriveTime.indexOf(".");
        var arriveMonth = arriveTime.substring(0, point) - 1;
        arriveTime = arriveTime.substring(point+1);
        point = arriveTime.indexOf(" ");
        var arriveYear = arriveTime.substring(0, point);
        arriveTime = arriveTime.substring(point+1);
        point = arriveTime.indexOf(":");
        var arriveHour = arriveTime.substring(0, point);
        arriveTime = arriveTime.substring(point+1);
        var arriveDate = new Date(arriveYear, arriveMonth, arriveDay, arriveHour, arriveTime, "00");
        var arriveValue = arriveDate.getTime() + 86400000;
        var currentValue = new Date().getTime();
        if(arriveValue>currentValue){
            if($('#department_id').val() == $('#toDepartmentId').val()){
                changeArriveNote.style.display = 'table-row';
                changeArriveButton.style.display = 'table-row';
            }
        }
    };

    $('#delay_table_body').on('click', function(event){
        var elem = event.target || event.srcElement;
        var noteId = elem.innerHTML;
        var validNumber = /^[0-9]+$/;
        var show_points = document.getElementById("show_points");
        if(validNumber.test(noteId)){
            $.ajax({
                url: '/user/load-data/note',
                method: 'POST',
                dataType: 'json',
                data: {noteId: noteId},
                success: function(containerNote) {
                    show_points.style.display = "block";
                    $('#containerNoteId').text(containerNote.id);
                    $('#containerNumber').text(containerNote.containerNumber);
                    var points_body = $('#points_body');
                    var points_html = "";
                    points_body.html('');
                    var passPoints = containerNote.betweenPoints;
                    if(passPoints!=null && passPoints.length>0){
                        $.each(passPoints, function(key, point){
                            points_html += "<tr><td>" + point.departmentName + "</td><td>" + point.passTime +
                                    "</td><td>" + point.passUser + "</td><td>" + point.passNote + "</td></tr>";
                        });
                    } else {
                        points_html = "<tr><td colspan='5'>Нет промежуточных объектов регистрации</td></tr>";
                    }
                    points_body.prepend(points_html);
               },
                error:  function(response) {
                    $('#result_line').html("Ошибка обращения в базу даннных. \nДля получения информации о движении термоконтейнера кликните по ячейке с номером.");
                    show_points.style.display = "none";
                }
            });
        }
    });

    $('#route_table_body').on('click', function(event){
        var elem = event.target || event.srcElement;
        var noteId = elem.innerHTML;
        var validNumber = /^[0-9]+$/;
        var show_route_points = document.getElementById("show_route_points");
        if(validNumber.test(noteId)){
            $.ajax({
                url: '/user/load-data/note',
                method: 'POST',
                dataType: 'json',
                data: {noteId: noteId},
                success: function(containerNote) {
                    show_route_points.style.display = "block";
                    $('#containerNoteId').text(containerNote.id);
                    var points_body = $('#route_points_body');
                    var points_html = "";
                    points_body.html('');
                    var passPoints = containerNote.betweenPoints;
                    if(passPoints!=null && passPoints.length>0){
                        $.each(passPoints, function(key, point){
                            points_html += "<tr><td>" + point.departmentName + "</td><td>" + point.passTime +
                                    "</td><td>" + point.passUser + "</td><td>" + point.passNote + "</td></tr>";
                        });
                    } else {
                        points_html = "<tr><td colspan='5'>Нет промежуточных объектов регистрации</td></tr>";
                    }
                    points_body.prepend(points_html);
               },
                error:  function(response) {
                    $('#result_line').html("Ошибка обращения в базу даннных. \nДля получения информации о движении термоконтейнера кликните по ячейке с номером.");
                    show_route_points.style.display = "none";
                }
            });
        }
    });


    $('#pages_journal_title').on('click', function(event){
        var totalPages;
        var pageNumber;
        var elem = event.target || event.srcElement;
        var cellContent = elem.innerHTML;
        var pageSize = $('#pageSize').val();
        var totalNotes = $('#totalNotes').val();
        if(totalNotes%pageSize>0){
            totalPages = parseInt(totalNotes/pageSize) + 1;
        } else{
            totalPages = parseInt(totalNotes/pageSize);
        }
        pageNumber = getPageNumber(totalNotes, pageSize, cellContent, totalPages);
        var departmentId = $('#department_id').val();
        if($('#select_department').val()!=null && $('#department_checkbox').is(':checked')==false){
            departmentId = $('#select_department').val();
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
                pages_html = getPagesHtml(pageNumber, totalPages, pageSize, totalNotes);
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
    });

    function getPageNumber(totalNotes, pageSize, cellContent, totalPages){
        var currentNoteNumber;
        var currentRow;
        var pos1;
        var pos2;
        pos1 = cellContent.indexOf("(") + 1;
        pos2 = cellContent.indexOf("..");
        if(pos2>1){
            currentNoteNumber = cellContent.substring(pos1, pos2);
            pageNumber = parseInt(currentNoteNumber/pageSize);
        } else{
            currentRow = $('#pages_delay_title').text();
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

    function getPagesHtml(pageNumber, totalPages, pageSize, totalNotes){
        var pages_html = "<tr>";
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

    $('#pages_payment_title').on('click', function(event){
        var totalPages;
        var elem = event.target || event.srcElement;
        var cellContent = elem.innerHTML;
        var pageSize = $('#paymentPageSize').val();
        var totalNotes = $('#totalPaymentNotes').val();
        if(totalNotes%pageSize>0){
            totalPages = parseInt(totalNotes/pageSize) + 1;
        } else{
            totalPages = parseInt(totalNotes/pageSize);
        }
        var pageNumber = getPageNumber(totalNotes, pageSize, cellContent, totalPages);
        var departmentId = $('#department_id').val();
        if($('#select_department').val()!=null && $('#department_checkbox').is(':checked')==false){
            departmentId = $('#select_department').val();
        }
        $.ajax({
            url: '/control/payment/report-notes',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), pageNumber: pageNumber,
                   pageSize: pageSize, departmentId: departmentId},
            success: function(notes) {
                var notes_html = "";
                var pages_payment_title = $('#pages_payment_title');
                var payment_table_body = $('#payment_table_body');
                pages_payment_title.html('');
                payment_table_body.html('');
                var pages_html = getPagesHtml(pageNumber, totalPages, pageSize, totalNotes);
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
    });

    $('#pages_delay_title').on('click', function(event){
        var totalPages;
        var elem = event.target || event.srcElement;
        var cellContent = elem.innerHTML;
        var pageSize = $('#delayPageSize').val();
        var totalNotes = $('#totalDelayNotes').val();
        if(totalNotes%pageSize>0){
            totalPages = parseInt(totalNotes/pageSize) + 1;
        } else{
            totalPages = parseInt(totalNotes/pageSize);
        }
        var pageNumber = getPageNumber(totalNotes, pageSize, cellContent, totalPages);
        var departmentId = $('#department_id').val();
        if($('#select_department').val()!=null && $('#department_checkbox').is(':checked')==false){
            departmentId = $('#select_department').val();
        }
        $.ajax({
            url: '/control/delay/report-notes',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), pageNumber: pageNumber,
                   pageSize: pageSize, departmentId: departmentId, delayLimit: $('#delay_limit').val()},
            success: function(notes) {
                var pages_delay_title = $('#pages_delay_title');
                var delay_table_body = $('#delay_table_body');
                var notes_html = "";
                pages_delay_title.html('');
                delay_table_body.html('');
                var pages_delay_html = getPagesHtml(pageNumber, totalPages, pageSize, totalNotes);
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
    });
    $('#pages_route_title').on('click', function(event){
        var show_route_points = document.getElementById("show_route_points");
        show_route_points.style.display = "none";
        var totalPages;
        var elem = event.target || event.srcElement;
        var cellContent = elem.innerHTML;
        var pageSize = $('#routePageSize').val();
        var totalNotes = $('#totalRouteNotes').val();
        if(totalNotes%pageSize>0){
            totalPages = parseInt(totalNotes/pageSize) + 1;
        } else{
            totalPages = parseInt(totalNotes/pageSize);
        }
        var pageNumber = getPageNumber(totalNotes, pageSize, cellContent, totalPages);
        var containerId = $('#select_container').val();
        $.ajax({
            url: '/control/route/report-notes',
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(),
                   pageNumber: pageNumber, pageSize: pageSize, containerId: containerId},
            success: function(notes) {
                var pages_route_title = $('#pages_route_title');
                var route_table_body = $('#route_table_body');
                var notes_html = "";
                pages_route_title.html('');
                route_table_body.html('');
                var pages_route_html = getPagesHtml(pageNumber, totalPages, pageSize, totalNotes);
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
    });

});




