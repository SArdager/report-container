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
               alert("Ошибка обращения в базу данных. Повторите.");
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
                alert("Ошибка обращения в базу данных. Повторите.");
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
            },
            error:  function(response) {
                alert("Ошибка обращения в базу данных. Повторите.");
            }
        });
    });

    var checkbox_reset = document.getElementById("resetId");
    var checkbox_reader = document.getElementById("readerId");
    var checkbox_editor = document.getElementById("editorId");
    var checkbox_account = document.getElementById("accountId");

    $('#resetId').change ( function(){
        if($('#resetId').is(':checked')==true){
            checkbox_reader.checked = false;
            checkbox_editor.checked = false;
            checkbox_account.checked = false;
            $('#user_rights').val("");
        } else {
            checkbox_reader.checked = true;
            $('#user_rights').val("reader");
        }
    });

    $('#readerId').change ( function(){
        if($('#readerId').is(':checked')==true){
            checkbox_reset.checked = false;
            checkbox_editor.checked = false;
            checkbox_account.checked = false;
            $('#user_rights').val("reader");
        } else {
            checkbox_reset.checked = true;
            $('#user_rights').val("");
        }
    });

    $('#editorId').change ( function(){
        if($('#editorId').is(':checked')==true){
            checkbox_reset.checked = false;
            checkbox_reader.checked = false;
            checkbox_account.checked = false;
            $('#user_rights').val("editor");
        } else {
            checkbox_reset.checked = true;
            $('#user_rights').val("");
        }
    });

    $('#accountId').change ( function(){
        if($('#accountId').is(':checked')==true){
            checkbox_reset.checked = false;
            checkbox_reader.checked = false;
            checkbox_editor.checked = false;
            $('#user_rights').val("account");
        } else {
            checkbox_reset.checked = true;
            $('#user_rights').val("");
        }
    });

});
