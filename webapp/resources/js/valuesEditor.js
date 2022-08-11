$(document).ready(function(){

    $('#btn_probe').on('click', function(){
        var probeName = $('#probe_name').val();
        if(probeName.length>1){
            $.ajax({
                url: '/admin/edit-values/edit-probe',
                method: 'POST',
                dataType: 'text',
                data: {id: $('#select_probe').val(), probeName: probeName},
                success: function(message) {
                    document.location.href = '/admin/edit-values';
                    $('#result_line').html(message);
                },
                error:  function(response) {
                    alert("Ошибка обращения в базу данных. Повторите.");
                }
            });
        } else {
            $('#result_line').html("Напишите новое название");
        }
    });

    $('#btn_value').on('click', function(){
        var valueName = $('#value_name').val();
        if(valueName.length>1){
            $.ajax({
                url: '/admin/edit-values/edit-container',
                method: 'POST',
                dataType: 'text',
                data: {id: $('#select_value').val(), valueName: valueName},
                success: function(message) {
                    document.location.href = '/admin/edit-values';
                    $('#result_line').html(message);
                },
                error:  function(response) {
                    alert("Ошибка обращения в базу данных. Повторите.");
                }
            });
        } else {
            $('#result_line').html("Напишите новое название");
        }
    });

    $('#btn_standard').on('click', function(){
        if($('#select_first_department').val()>0 &&
                       $('#select_second_department').val()>0 ){
            var validTime = /^[0-9]+$/;
            if(validTime.test($('#time_standard').val()) && $('#time_standard').val()>0){
                $.ajax({
                    url: '/admin/edit-values/edit-standard',
                    method: 'POST',
                    dataType: 'text',
                    data: {standardId: $('#standard_id').val(), firstPointId: $('#select_first_department').val(),
                        secondPointId: $('#select_second_department').val(), probeId: $('#select_time_probe').val(),
                        timeStandard: $('#time_standard').val()},
                    success: function(message) {
                        $('#result_line').html(message);
                    },
                    error:  function(response) {
                        alert("Ошибка обращения в базу данных. Повторите.");
                    }
                });
            } else {
                $('#result_line').html("Время доставки должно состоять только из цифр (без десятичных значений)");
            }
        } else {
            $('#result_line').html("Выберите отделы отгрузки и доставки");
        }
    });

    $('#btn_del_probe').on('click', function(){
        var probeName = $('#select_probe option:selected').text();
        var x = confirm("ВНИМАНИЕ!!!\nИз базы данных будет удален вид пробы: `" + probeName + "`, а также стандарты времени по доставке термоконтейнеров, содержащие данный вид пробы.\n" +
                          "ЭТА ОПЕРАЦИЯ НЕ ВОССТАНОВИМА!!!\n\nПродолжить операцию удаления?");
        if(x){
            $.ajax({
                url: '/admin/edit-values/delete-probe',
                method: 'POST',
                dataType: 'text',
                data: {id: $('#select_probe').val()},
                success: function(message) {
                document.location.href = '/admin/edit-values';
                    $('#result_line').html(message);
                },
                error:  function(response) {
                    alert("Ошибка обращения в базу данных. Повторите.");
                }
            });
        }
    });
    $('#btn_del_value').on('click', function(){
        var valueName = $('#select_value option:selected').text();
        var x = confirm("ВНИМАНИЕ!!!\nИз базы данных будет удален вид пробы: `" + valueName + "`, а также соответствующие стандарты времени по доставке термоконтейнеров.\n" +
                          "ЭТА ОПЕРАЦИЯ НЕ ВОССТАНОВИМА!!!\n\nПродолжить операцию удаления?");
        if(x){
            $.ajax({
                url: '/admin/edit-values/delete-container-value',
                method: 'POST',
                dataType: 'text',
                data: {id: $('#select_value').val()},
                success: function(message) {
                document.location.href = '/admin/edit-values';
                    $('#result_line').html(message);
                },
                error:  function(response) {
                    alert("Ошибка обращения в базу данных. Повторите.");
                }
            });
        }
    });

    $('#select_probe').on('change', function(){
        var btn_probe = document.getElementById("btn_probe");
        var btn_del_probe = document.getElementById("btn_del_probe");
        if($('#select_probe').val()>0){
            btn_probe.value = "Изменить";
            btn_del_probe.type = "button";
        } else{
            $('#probe_name').val("");
            btn_probe.value = "Создать";
            btn_del_probe.type = "hidden";
        }
    });

    $('#select_value').on('change', function(){
        var btn_value = document.getElementById("btn_value");
        var btn_del_value = document.getElementById("btn_del_value");
        if($('#select_value').val()>0){
            btn_value.value = "Изменить";
            btn_del_value.type = "button";
        } else{
            $('#probe_name').val("");
            btn_value.value = "Создать";
            btn_del_value.type = "hidden";
        }
    });

    $('#select_first_branch').on('change', function(){
        $.ajax({
            url: '/user/change-department/select-branch',
            method: 'POST',
            dataType: 'json',
            data: {branchId: $('#select_first_branch').val()},
            success: function(departments) {
                $('#select_first_department').empty();
                $.each(departments, function(key, department){
                    $('#select_first_department').append('<option value="' + department.id + '">' + department.departmentName + '</option');
                });
                checkAllParameters();
            },
            error:  function(response) {
                alert("Ошибка обращения в базу данных. Повторите.");
            }
        });
    });
    $('#select_second_branch').on('change', function(){
        $.ajax({
            url: '/user/change-department/select-branch',
            method: 'POST',
            dataType: 'json',
            data: {branchId: $('#select_second_branch').val()},
            success: function(departments) {
                $('#select_second_department').empty();
                $.each(departments, function(key, department){
                    $('#select_second_department').append('<option value="' + department.id + '">' + department.departmentName + '</option');
                });
                checkAllParameters();
            },
            error:  function(response) {
                alert("Ошибка обращения в базу данных. Повторите.");
            }
        });
    });
    $('#select_first_department').on('change', function(){
        checkAllParameters();
    });
    $('#select_second_department').on('change', function(){
        checkAllParameters();
    });
    $('#select_time_probe').on('change', function(){
        checkAllParameters();
    });

    function checkAllParameters(){
        $('#standard_id').val(0);
        $('#time_standard').val(0);
        if($('#select_first_department').val()>0 &&
            $('#select_second_department').val()>0 &&
            $('#select_time_probe').val()>0){
            if($('#select_first_department').val()!=$('#select_second_department').val()){
                $.ajax({
                    url: '/user/load-data/time-standard',
                    method: 'POST',
                    dataType: 'json',
                    data: {firstPointId: $('#select_first_department').val(),
                        secondPointId: $('#select_second_department').val(), probeId: $('#select_time_probe').val()},
                    success: function(standard) {
                        if(standard!=null && standard.id!=null){
                            $('#time_standard').val(standard.timeStandard);
                            $('#standard_id').val(standard.id);
                        }
                    },
                    error:  function(response) {
                        alert("Ошибка обращения в базу данных. Повторите.");
                    }
                });
            }
        }
    }

});