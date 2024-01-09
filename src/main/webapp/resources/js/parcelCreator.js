$(document).ready(function(){

    $('#select_parcel').on('change', function(){
        $('#dimensions').val("");
        $('#note').val("");
        $('#information').prop('checked', false);
        if($('#select_parcel').val()==1){
            $('#dimensions').css("display", "none");
        } else {
            $('#dimensions').css("display", "block");
        }
    });
    $('#select_branch').on('change', function(){
        $('#dimensions').val("");
        $('#note').val("");
        $('#information').prop('checked', false);
    });

    let parentNumber = document.getElementById("parent_number");
    parentNumber.oninput = function(){
              let parent_number = $('#parent_number').val();
              if(parent_number.length>0){
                  $('#costs_tr').css("display", "table-row");
                  $('#btn_add').css("display", "block");
                  $('#btn_send').css("display", "none");
                  $('#btn_remove').css("display", "none");
              } else {
                  $('#costs_tr').css("display", "none");
                  $('#btn_add').css("display", "none");
                  $('#btn_send').css("display", "block");
                  $('#btn_remove').css("display", "block");
              }
          };

    $('#btn_parcel').on('click', function(){
        let type_parcel = $('#select_parcel').val();
        let dimensions = $('#dimensions').val();
        if(type_parcel>0){
            if(dimensions.length<2 && type_parcel>1){
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Внесите габариты почтового отправления.");
            } else {
                $('#btn_parcel').css("display", "none");
                $.ajax({
                    url: '../user/check-parcel/create',
                    method: 'POST',
                    dataType: 'text',
                    data: {parcelType: type_parcel, destinationId: $('#select_department').val(), outDepartmentId: $('#departmentId').val(),
                        note: $('#note').val(), dimensions: dimensions, information: $('#information').is(':checked')},
                    success: function(parcelNumber) {
                        if(parcelNumber!= null && parcelNumber.length == 8){
                            $('#parcel_number').val(parcelNumber);
                            window.scrollTo({ top: 0, behavior: 'smooth' });
                            $('#result_line').html("Номер почтового отправления: " + parcelNumber);
                            $('#reload_parcel').trigger("click");
                        } else {
                            $('#parcelNumber').val("");
                            window.scrollTo({ top: 0, behavior: 'smooth' });
                            $('#result_line').html("Ошибка создания почтового отправления. Перегрузите страницу.");
                        }
                        $('#btn_parcel').css("display", "block");
                    },
                    error:  function(response) {
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка создания почтового отправления. Перегрузите страницу.");
                        $('#btn_parcel').css("display", "block");
                    }
                });
            }
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Выберите вид почтового отправления.");
        }
    });

    $('#memory_department').on('click', function(){
        $('#memory_department').css("display", "none");
        $.ajax({
            url: '../user/check-parcel/memory-department',
            method: 'POST',
            dataType: 'text',
            data: {departmentId: $('#select_change_department').val()},
            success: function(message) {
                $('#memory_department').css("display", "block");
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html(message);
            },
            error:  function(response) {
                $('#memory_department').css("display", "block");
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

    $('#reload_parcel').on('click', function(){
        $('#reload_parcel').css("display", "none");
        $.ajax({
            url: '../user/check-parcel/load-results',
            method: 'POST',
            dataType: 'json',
            data: {departmentId: $('#departmentId').val()},
            success: function(parcels) {
                let parcels_html = "";
                let parcels_table_body = $('#parcels_table_body');
                parcels_table_body.html('');
                if(parcels!=null && parcels.length>0){
                    $.each(parcels, function(key, parcel){
                        parcels_html += "<tr><td>" + parcel.parcelNumber + "</td><td>" + parcel.destinationName +
                               "</td><td>" + parcel.dimensions  + "</td><td>" + parcel.parentNumber + "</td></tr>";
                    });
                } else {
                    parcels_html = "<tr><td colspan='4'>Отсутствуют подготовленные к отгрузке посылки</td></tr>";
                }
                parcels_table_body.prepend(parcels_html);
                $('#parcel_number').val("");
                $('#reload_parcel').css("display", "block");
                $('#btn_send').css("display", "block");
                $('#btn_remove').css("display", "block");
                $('#btn_add').css("display", "none");
            },
            error:  function(response) {
                $('#reload_parcel').css("display", "block");
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

    $('#btn_send').on('click', function(){
        let parcel_number = $('#parcel_number').val();
        if(parcel_number.length>0){
            $('#btn_send').css("display", "none");
            $.ajax({
                url: '../user/check-parcel/send',
                method: 'POST',
                dataType: 'text',
                data: {parcelNumber: parcel_number, toDepartmentId: $('#select_change_department').val()},
                success: function(message) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html(message);
                    $('#reload_parcel').trigger("click");
                    if(document.getElementById("parent_number")!=null){
                        $('#parent_number').val("");
                    }
                    if(document.getElementById("parcel_number")!=null){
                        $('#parcel_number').val("");
                    }
                    $('#btn_send').css("display", "block");
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка отгрузки почтового отправления. Перегрузите страницу.");
                    $('#btn_send').css("display", "block");
                }
            });
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Внесите номер почтового отправления.");
        }
    });

    $('#show_assembly').on('click', function(){
        if($('#parcel_tr').css('display') == 'table-row'){
           $('#parcel_tr').css("display", "none");
           $('#add_tr').css("display", "none");
           $('#btn_add').css("display", "none");
           $('#btn_remove').css("display", "none");
        } else {
           $('#parcel_tr').css("display", "table-row");
           $('#add_tr').css("display", "table-row");
           $('#btn_add').css("display", "none");
           $('#btn_remove').css("display", "block");
           $('#add_number').val("");
           $('#parent_number').val("");
           $('#costs_part').val("0");
        }
    });

    $('#btn_add').on('click', function(){
        let parcel_number = $('#add_number').val();
        let validNumber = /^[0-9]+$/;
        if(parcel_number.length>0){
            if(validNumber.test($('#costs_part').val())){
                $('#btn_add').css("display", "none");
                $.ajax({
                    url: '../user/check-parcel/add-to-parent',
                    method: 'POST',
                    dataType: 'text',
                    data: {parcelNumber: parcel_number,  parentNumber: $('#parent_number').val(),
                            costsPart: $('#costs_part').val()},
                    success: function(message) {
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html(message);
                        $('#reload_parcel').trigger("click");
                        $('#add_number').val("");
                        $('#parent_number').val("");
                        document.getElementById("costs_tr").style.display = "none";
                        $('#btn_add').css("display", "block");
                    },
                    error:  function(response) {
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка оформления почтового отправления. Перегрузите страницу.");
                        $('#btn_add').css("display", "block");
                    }
                });
            } else {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("В строке доли оплаты доставки должна указана стоимость в числах (при отсутствии оплаты - ноль)");
            }
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Внесите номер вкладываемого почтового отправления.");
        }
    });

    $('#btn_prt').on('click', function(){
        let number = $('#parcel_number').val();
        if(number.length>7){
            $('#btn_prt').css("display", "none");
            $.ajax({
                url: '../user/check-container/print-code',
                method: 'POST',
                dataType: 'text',
                data: {startNumber: number, endNumber: ""},
                success: function(message) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html(message);
                    $('#btn_prt').css("display", "block");
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка создания штрих-кода. Перегрузите страницу.");
                    $('#btn_prt').css("display", "block");
                }
            });
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Внесите корректный номер, состоящий из 8 символов.");
        }
    });

    $('#btn_remove').on('click', function(){
        let parcel_number = $('#add_number').val();
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
                    $('#add_number').val("");
                    $('#parent_number').val("");
                    $('#reload_parcel').trigger("click");
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

    $('#clean_courier').click(function(){
        $('#number_courier').val("");
        $('#textarea_courier').val("");
        $('#number_courier').focus();
    });

});
