$(document).ready(function(){

    $('#select_company').on('change', function(){
        $.ajax({
            url: '../user/change-department/select-company',
            method: 'POST',
            dataType: 'json',
            data: {companyId: $('#select_company').val()},
            success: function(branches) {
               $('#select_branch').empty();
               $('#select_department').empty();
               $.each(branches, function(key, branch){
                   $('#select_branch').append('<option value="' + branch.id + '">' + branch.branchName + '</option');
               });
                $('#select_branch').trigger("change");
            },
            error:  function(response) {
               window.scrollTo({ top: 0, behavior: 'smooth' });
               $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

    $('#select_branch').on('change', function(){
        $.ajax({
            url: '../user/change-department/select-branch',
            method: 'POST',
            dataType: 'json',
            data: {branchId: $('#select_branch').val()},
            success: function(departments) {
                $('#select_department').empty();
                $.each(departments, function(key, department){
                    $('#select_department').append('<option value="' + department.id + '">' + department.departmentName + '</option');
                });
                $('#select_department').trigger('change');
                $('#clean_input').trigger('click');
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

    $('#select_change_branch').on('change', function(){
        $.ajax({
            url: '../user/change-department/select-branch',
            method: 'POST',
            dataType: 'json',
            data: {branchId: $('#select_change_branch').val()},
            success: function(departments) {
                $('#select_change_department').empty();
                $.each(departments, function(key, department){
                    $('#select_change_department').append('<option value="' + department.id + '">' + department.departmentName + '</option');
                });
                if(document.getElementById('toDepartmentId')!=null){
                    $('#select_change_department').val($('#toDepartmentId').val());
                }
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

    $('#select_out_branch').on('change', function(){
        let url = '../user/change-department/select-pref';
        if(document.getElementById("all_company").style.display == 'table-row'){
            url = '../user/change-department/select-branch';
        }
        $.ajax({
            url: url,
            method: 'POST',
            dataType: 'json',
            data: {branchId: $('#select_out_branch').val()},
            success: function(departments) {
                $('#select_department').empty();
                $.each(departments, function(key, department){
                    $('#select_department').append('<option value="' + department.id + '">' + department.departmentName + '</option');
                });
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

    $("#other_branches").on('click', function(){
        $('#all_company').css("display", "table-row");
        $('#select_all_company').trigger("change");
        $("#other_branches").html("");
    });

    $('#select_all_company').on('change', function(){
        $.ajax({
            url: '../user/change-department/select-company',
            method: 'POST',
            dataType: 'json',
            data: {companyId: $('#select_all_company').val()},
            success: function(branches) {
               $('#select_out_branch').empty();
               $.each(branches, function(key, branch){
                   $('#select_out_branch').append('<option value="' + branch.id + '">' + branch.branchName + '</option');
               });
                $('#select_out_branch').trigger("change");
            },
            error:  function(response) {
               window.scrollTo({ top: 0, behavior: 'smooth' });
               $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

});
