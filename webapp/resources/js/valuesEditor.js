$(document).ready(function(){

    $('#btn_standard').on('click', function(){
        if($('#select_first_department').val()>0 && $('#select_second_department').val()>0 ){
            var validTime = /^[0-9]+$/;
            if(validTime.test($('#time_standard').val()) && $('#time_standard').val()>0){
                $('#btn_standard').css("display", "none");
                $.ajax({
                    url: '../user/load-data/edit-standard',
                    method: 'POST',
                    dataType: 'text',
                    data: {standardId: $('#standard_id').val(), firstPointId: $('#select_first_department').val(),
                        secondPointId: $('#select_second_department').val(), timeStandard: $('#time_standard').val()},
                    success: function(message) {
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html(message);
                        $('#btn_standard').css("display", "block");
                    },
                    error:  function(response) {
                        $('#btn_standard').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    }
                });
            } else {
                $('#result_line').html("Время доставки должно быть больше нуля, состоять только из целых чисел");
            }
        } else {
            $('#result_line').html("Выберите отделы отгрузки и доставки");
        }
    });

    $('#select_first_branch').on('change', function(){
        $.ajax({
            url: '../user/change-department/select-branch',
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
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

    $('#select_second_branch').on('change', function(){
        $.ajax({
            url: '../user/change-department/select-branch',
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
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

    $('#select_first_department').on('change', function(){
        checkAllParameters();
    });

    $('#select_second_department').on('change', function(){
        checkAllParameters();
    });

    function checkAllParameters(){
        $('#standard_id').val("0");
        $('#time_standard').val(0);
        if($('#select_first_department').val()>0 && $('#select_second_department').val()>0 ){
            $.ajax({
                url: '../user/load-data/time-standard',
                method: 'POST',
                dataType: 'json',
                data: {firstPointId: $('#select_first_department').val(),
                    secondPointId: $('#select_second_department').val()},
                success: function(standard) {
                    if(standard!=null){
                        $('#time_standard').val(standard.timeStandard);
                        $('#standard_id').val(standard.id);
                    }
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
        }
    }

});

