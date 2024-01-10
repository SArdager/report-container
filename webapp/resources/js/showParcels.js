$(document).ready(function(){

    $('#reload_parcels').on('click', function(){
        let departmentId = $('#department_id').val();
        $('#totalParcels').val(0);
        $('#parcels_pages_title').html('');
        $('#parcels_table_body').html('');
        if(departmentId==1){
            if($('#all_checkbox').is(':checked')==false){
                departmentId = $('#select_department').val();
            }
        }
        let sample = $('input[type="radio"][name="sample"]:checked').val();
        $('#line_cut_parcel').click();
        let urlSource;
        if(departmentId>0){
            if(sample=="all"){
                urlSource = '../user/load-data/parcels-totalNumbers';
                getTotalParcels(urlSource, departmentId);
            } else if(sample == "out"){
                if(departmentId>1){
                    urlSource = '../user/load-data/parcels-totalOutNumbers';
                    getTotalParcels(urlSource, departmentId);
                } else {
                    $('#result_line').html("Выберите объект");
                }
            } else if(sample == "to"){
                if(departmentId>1){
                    urlSource = '../user/load-data/parcels-totalToNumbers';
                    getTotalParcels(urlSource, departmentId);
                } else {
                    $('#result_line').html("Выберите объект");
                }
            } else {
                $.ajax({
                    url: '../user/load-data/parcels-at-route',
                    method: 'POST',
                    dataType: 'json',
                    data: {departmentId: departmentId},
                    success: function(parcels) {
                        let parcels_html = "";
                        let parcels_table_body = $('#parcels_table_body');
                        if(parcels.length>0){
                            parcels_html = getParcelsHtml(parcels);
                        } else{
                            parcels_html += "<tr><td colspan=6>Все почтовые отправления доставлены</td></tr>";
                        }
                        parcels_table_body.prepend(parcels_html);
                    },
                    error:  function(response) {
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка обращения в базу даннных. Перегрузите страницу.");
                    }
                });
            }
        }
    });

    function getTotalParcels(urlSource, departmentId){
        $.ajax({
            url: urlSource,
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(), departmentId: departmentId},
            success: function(totalNumbers) {
                let totalParcels = parseInt(totalNumbers);
                if(totalParcels>0){
                    $('#totalParcels').val(totalParcels);
                    getPageParcels(0, departmentId);
                } else {
                    let parcels_html = "";
                    let parcels_table_body = $('#parcels_table_body');
                    parcels_html += "<tr><td colspan=9>Почтовые отправления не зарегистрированы</td></tr>";
                    parcels_table_body.prepend(parcels_html);
                }
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу даннных. Перегрузите страницу.");
            }
        });
    }

    function showParcelsPage(urlSource, pageNumber, departmentId){
        let pageSize = $('#pageSize').val();
        let totalParcels = $('#totalParcels').val();
        $.ajax({
            url: urlSource,
            method: 'POST',
            dataType: 'json',
            data: {startDate: $('#startDate').val(), endDate: $('#endDate').val(),
                    departmentId: departmentId, pageNumber: pageNumber, pageSize: pageSize},
            success: function(parcels) {
                $('#parcels_table').attr("class", "scroll_table");
                let pages_html = "";
                let parcels_html = "";
                let parcels_pages_title = $('#parcels_pages_title');
                let parcels_table_body = $('#parcels_table_body');
                parcels_pages_title.html('');
                parcels_table_body.html('');
                if(parcels!=null && parcels.length>0){
                    pages_html = getPagesHtml(pageNumber, pageSize, totalParcels);
                    parcels_pages_title.prepend(pages_html);
                    parcels_html = getParcelsHtml(parcels);
                    parcels_table_body.prepend(parcels_html);
                }
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу даннных. Перегрузите страницу.");
            }
        });
    }

    function getPageParcels(pageNumber, departmentId){
        let urlSource;
        let sample = $('input[type="radio"][name="sample"]:checked').val();
        if(sample=="all"){
            urlSource = '../user/load-data/all-parcels';
            showParcelsPage(urlSource, pageNumber, departmentId);
        } else if(sample == "out"){
            urlSource = '../user/load-data/out-parcels';
            showParcelsPage(urlSource, pageNumber, departmentId);
        } else if(sample == "to"){
            urlSource = '../user/load-data/to-parcels';
            showParcelsPage(urlSource, pageNumber, departmentId);
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Ошибка обращения в базу даннных. Перегрузите страницу.");
        }
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

    function getParcelsHtml(parcels){
        let parcels_html = "";
        $.each(parcels, function(key, parcel){
            parcels_html += "<tr><td style='color: blue; text-decoration: underline'>" + parcel.parcelNumber + "</td><td>" +
            parcel.parcelType + "</td><td>" + parcel.dimensions + "</td><td>" + parcel.outDepartment  + "</td><td style='text-align: center;'>" +
            parcel.sendDateTime  + "</td><td>" + parcel.destinationName + "</td><td style='text-align: center;'>" + parcel.deliveryDateTime +
            "</td><td>" + parcel.payment + " тг</td><td>" + parcel.status +
            "</td><td>" + parcel.note + "</td></tr>";
        });
        return parcels_html;
    }

    $('#parcels_pages_title').on('click', function(event){
        let pageNumber;
        let elem = event.target || event.srcElement;
        let cellContent = elem.innerHTML;
        let pageSize = $('#pageSize').val();
        let totalParcels = $('#totalParcels').val();
        let currentRow = $('#parcels_pages_title').text();
        pageNumber = getPageNumber(totalParcels, pageSize, cellContent, currentRow);
        let departmentId = $('#department_id').val();
        if($('#all_checkbox').is(':checked')==false){
            departmentId = $('#select_department').val();
        }
        getPageParcels(pageNumber, departmentId);
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

    $('#show_search_field').on('click', function(){
        $('#search_field').css("display", "block");
        $('#show_search_field').html("");
        $('#parcel_number').val("");
    });

    $('#close_search').on('click', function(){
        $('#show_search_field').html("Поиск посылки по номеру");
        $('#search_field').css("display", "none");
        $('#parcel_points_field').css("display", "none");
        $('#reload_table_line').css("display", "block");
        $('#parcels_pages_title').html('');
        $('#parcels_table_body').html('');
    });

    $('#btn_search_parcel').on('click', function(){
        $('#btn_search_parcel').css("display", "none");
        $.ajax({
            url: '../user/load-data/search-parcel',
            method: 'POST',
            dataType: 'json',
            data: {findNumber: $('#parcel_number').val()},
            success: function(parcel) {
                $('#btn_search_parcel').css("display", "block");
                $('#parcels_table').attr("class", "table_shot");
                document.getElementById('parcel_points_field').style.display = "block";
                document.getElementById('reload_table_line').style.display = "none";
                let parcel_html = "";
                let points_html = "";
                let parcels_table_body = $('#parcels_table_body');
                let points_table_body = $('#points_table_body');
                parcels_table_body.html('');
                points_table_body.html('');
                if(parcel!=null && parcel.parcelNumber!=null){
                    parcel_html = "<tr><td style='font-weight: bold;'>" + parcel.parcelNumber + "</td><td>" +
                        parcel.parcelType + "</td><td>" + parcel.dimensions + "</td><td>" + parcel.outDepartment  + "</td><td style='text-align: center;'>" +
                        parcel.sendDateTime  + "</td><td>" + parcel.destinationName + "</td><td style='text-align: center;'>" + parcel.deliveryDateTime +
                        "</td><td>" + parcel.payment + " тг</td><td>" + parcel.status +
                        "</td><td>" + parcel.note + "</td></tr>";
                    parcels_table_body.prepend(parcel_html);
                    let points = parcel.parcelPoints;
                    $.each(points, function(key, point){
                        points_html += "<tr><td style='font-weight: bold;'>" + point.departmentName + "</td><td style='text-align: center;'>" +
                        point.arriveTime + "</td><td>" + point.intoUser + "</td><td style='text-align: center;'>" + point.sendTime  + "</td><td>" +
                        point.outUser  + "</td><td>" + point.parent + "</td><td>" + point.payment + " тг</td><td>" + point.text + "</td></tr>";
                    });
                    points_table_body.prepend(points_html);
                } else{
                    parcel_html = "<tr><td colspan='9' style='font-weight: bold;'>Указанный номер почтового отправления не зарегистрирован в базе данных</td></tr>";
                    parcels_table_body.prepend(parcel_html);
                    document.getElementById('parcel_points_field').style.display = "none";
                    $('#result_line').html("Указанный номер почтового отправления не зарегистрирован в базе данных.");
                }
            },
            error:  function(response) {
                $('#btn_search_parcel').css("display", "block");
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

    $('#parcels_table_body').on('click', function(event){
        let elem = event.target || event.srcElement;
        let parcelNumber = elem.innerHTML;
        let validNumber = /^[0-9]+$/;
        if(parcelNumber.length == 8){
            let parcelNumbers = parcelNumber.substring(1);
            if(validNumber.test(parcelNumbers)){
                $('#search_field').css("display", "block");
                $('#parcel_number').val(parcelNumber);
                $('#btn_search_parcel').trigger('click');
                $('#show_search_field').html("");
            }
        }
    });

    $('#btn_parcel_excel').on('click', function(){
        let departmentId = $('#department_id').val();
        if(departmentId==1){
            if($('#all_checkbox').is(':checked')==false){
                departmentId = $('#select_department').val();
            }
        }
        $('#exportDepartmentId').val(departmentId);
        $('#export_excel').submit();
    });

    $('#show_check_in').on('click', function(){
        $('#check_in_field').css("display", "block");
        $('#check_parcels_field').css("display", "none");
        $('#reload_input_parcels').trigger("click");
    });

    $('#close_check_in').on('click', function(){
        $('#check_in_field').css("display", "none");
    });

    $('#show_check_parcels').on('click', function(){
        $('#check_parcels_field').css("display", "block");
        $('#check_in_field').css("display", "none");
    });

    $('#close_check_parcels').on('click', function(){
        $('#check_parcels_field').css("display", "none");
    });

    $('#show_check_containers').on('click', function(){
        $('#check_container_field').css("display", "block");
        $('#check_parcels_field').css("display", "none");
        $('#check_in_field').css("display", "none");
        $('#reload_input_containers').trigger("click");
    });

    $('#close_check_container').on('click', function(){
        $('#check_container_field').css("display", "none");
    });


    $('#clean_input_parcel').on('click', function(){
        $('#parcel_number_income').val("");
        $('#parcel_textarea_in').val("");
    });

    $('#reload_input_parcels').on('click', function(){
        $('#reload_input_parcels').css("display", "none");
        $.ajax({
            url: '../user/load-data/parcels',
            method: 'POST',
            dataType: 'json',
            success: function(parcels) {
                let parcels_table_body = $('#input_parcels_table_body');
                parcels_table_body.html('');
                if(parcels!=null && parcels.length>0){
                    let parcels_html = "";
                    parcels_table_body.html('');
                    $.each(parcels, function(key, parcel){
                        parcels_html += "<tr><td>" + parcel.parcelNumber + "</td><td>" + parcel.parcelType + "</td><td>" +
                               parcel.outDepartment  + "</td><td style='text-align: center;'>" + parcel.sendDateTime + "</td><td>" + parcel.dimensions + "</td></tr>";
                    });
                    parcels_table_body.prepend(parcels_html);
                } else {
                    parcels_table_body.prepend("<tr><td colspan='5'><b>Прибытие почтового отправления не ожидается</b></td></tr>");
                }
                $('#reload_input_parcels').css("display", "block");
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                $('#reload_input_parcels').css("display", "block");
            }
        });

    });

    $('#btn_income_parcel').on('click', function(){
        $('#btn_income_parcel').css("display", "none");
        $.ajax({
            url: '../user/check-in/parcel',
            method: 'POST',
            dataType: 'text',
            data: {parcelNumber: $('#parcel_number_income').val(), text: $('#parcel_textarea_in').val()},
            success: function(message) {
                $('#btn_income_parcel').css("display", "block");
                if(message.indexOf("Нельзя")<0 && message.indexOf("не найдена")<0){
                    $('#reload_input_parcels').trigger("click");
                    $('#clean_input_parcel').trigger("click");
                }
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html(message);
            },
            error:  function(response) {
                $('#btn_income_parcel').css("display", "block");
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка регистрации прибытия. Перегрузите страницу.");
            }
        });
    });



});