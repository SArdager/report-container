$(document).ready(function(){

    $('#select_company').on('change', function(){
        $.ajax({
            url: '/user/change-department/select-company',
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
            url: '/user/change-department/select-branch',
            method: 'POST',
            dataType: 'json',
            data: {branchId: $('#select_branch').val()},
            success: function(departments) {
                $('#select_department').empty();
                $.each(departments, function(key, department){
                    $('#select_department').append('<option value="' + department.id + '">' + department.departmentName + '</option');
                });
                $('#clean_input').trigger('click');
            },
            error:  function(response) {
                alert("Ошибка обращения в базу данных. Повторите.");
            }
        });
    });
    $('#select_change_branch').on('change', function(){
        $.ajax({
            url: '/user/change-department/select-branch',
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

    var checkbox_reader = document.getElementById("readerId");
    var checkbox_editor = document.getElementById("editorId");
    var checkbox_account = document.getElementById("accountId");
    $('#readerId').change ( function(){
        if($('#readerId').is(':checked')==true){
            checkbox_editor.checked = false;
            checkbox_account.checked = false;
            $('#user_rights').val("reader");
        } else {
            $('#user_rights').val("");
        }
    });
    $('#editorId').change ( function(){
        if($('#editorId').is(':checked')==true){
            checkbox_reader.checked = false;
            checkbox_account.checked = false;
            $('#user_rights').val("editor");
        } else {
            $('#user_rights').val("");
        }
    });
    $('#accountId').change ( function(){
        if($('#accountId').is(':checked')==true){
            checkbox_reader.checked = false;
            checkbox_editor.checked = false;
            $('#user_rights').val("account");
        } else {
            $('#user_rights').val("");
        }
    });


//    var line_between = document.getElementById("line_between");
//    var number_between = 1;
//    line_between.onclick = function(){
//        if(window.getComputedStyle(between_area).display === 'none' && number_between%2!=0){
//            number_between++;
//        }
//        if(number_between%2==0){
//
//        } else {
//        }
//        number_between++;
//    };
//


//    $('#btn_choose').on('click', function(){
//        $.ajax({
//            url: '/user/change-department/choose-department',
//            method: 'POST',
//            dataType: 'text',
//            data: {departmentId: $('#select_department').val()},
//            success: function(message) {
//                $('#result_line').html(message);
//            },
//            error:  function(response) {
//                alert("Ошибка обращения в базу данных. Повторите.");
//            }
//        });
//    });

});
