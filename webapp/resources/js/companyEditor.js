$(document).ready(function(){

    $('#btn_company').on('click', function(){
        var companyName = $('#companyName').val();
        if(companyName.length>1){
            if($('#select_company').val()!=1){
                $('#btn_company').css("display", "none");
                $.ajax({
                    url: '../admin/edit-company/company',
                    method: 'POST',
                    dataType: 'text',
                    data: {id: $('#select_company').val(), companyName: companyName, isLabor: $('#isLabor').is(':checked')},
                    success: function(message) {
                        $('#btn_company').css("display", "block");
                        $('#result_line').html(message);
                        setTimeout(() => { document.location.href = '../admin/edit-company';}, 800);
                    },
                    error:  function(response) {
                        $('#btn_company').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    }
                });
            } else {
                $('#result_line').html("Нельзя изменять это название");
            }
        } else {
            $('#result_line').html("Напишите новое название");
        }
    });

    $('#btn_branch').on('click', function(){
        var branchName = $('#branchName').val();
        if(branchName.length>1){
            if($('#select_branch').val()!=1){
                $('#btn_branch').css("display", "none");
                $.ajax({
                    url: '../admin/edit-company/branch',
                    method: 'POST',
                    dataType: 'text',
                    data: {id: $('#select_branch').val(), branchName: branchName, companyId: $('#select_company').val()},
                    success: function(message) {
                        $('#btn_branch').css("display", "block");
                        $('#branchName').val("");
                        $('#select_company').trigger("change");
                        $('#result_line').html(message);
                    },
                    error:  function(response) {
                        $('#btn_branch').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    }
                });
            } else {
                $('#result_line').html("Нельзя изменять это название");
            }
        } else {
            $('#result_line').html("Напишите новое название");
        }
    });

    $('#btn_department').on('click', function(){
        var departmentName = $('#departmentName').val();
        if(departmentName.length>1){
            if($('#select_department').val()!=1){
                $('#btn_department').css("display", "none");
                $.ajax({
                    url: '../admin/edit-company/department',
                    method: 'POST',
                    dataType: 'text',
                    data: {id: $('#select_department').val(), departmentName: departmentName, branchId: $('#select_branch').val()},
                    success: function(message) {
                        $('#btn_department').css("display", "block");
                        $('#departmentName').val("");
                        $('#select_branch').trigger("change");
                        $('#result_line').html(message);
                    },
                    error:  function(response) {
                        $('#btn_department').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    }
                });
            } else {
                $('#result_line').html("Нельзя изменять это название");
            }
        } else {
            $('#result_line').html("Напишите новое название");
        }
    });

    $('#btn_del_company').on('click', function(){
        var companyName = $('#select_company option:selected').text();
        var x = confirm("ВНИМАНИЕ!!! <br>Из базы данных будет удалено предприятие `" + companyName + "` с соответствующими филиалами и объектами. \n" +
                          "ЭТА ОПЕРАЦИЯ НЕ ВОССТАНОВИМА!!! <br><br>Продолжить операцию удаления?");
        if(x){
            if($('#select_company').val()>1){
                $('#btn_del_company').css("display", "none");
                $.ajax({
                    url: '../admin/edit-company/delete-company',
                    method: 'POST',
                    dataType: 'text',
                    data: {id: $('#select_company').val()},
                    success: function(message) {
                        $('#btn_del_company').css("display", "block");
                        $('#result_line').html(message);
                        setTimeout(() => { document.location.href = '../admin/edit-company';}, 800);
                    },
                    error:  function(response) {
                        $('#btn_del_company').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    }
                });
            } else{
                $('#result_line').html("Нельзя удалять это название");
            }
        }
    });

    $('#btn_del_branch').on('click', function(){
        var branchName = $('#select_branch option:selected').text();
        var x = confirm("ВНИМАНИЕ!!!<br>Из базы данных будет удален филиал `" + branchName + "` с соответствующими ему объектами.\n" +
                          "ЭТА ОПЕРАЦИЯ НЕ ВОССТАНОВИМА!!!<br><br>Продолжить операцию удаления?");
        if(x){
            if($('#select_branch').val()>1){
                $('#btn_del_branch').css("display", "none");
                $.ajax({
                    url: '../admin/edit-company/delete-branch',
                    method: 'POST',
                    dataType: 'text',
                    data: {id: $('#select_branch').val()},
                    success: function(message) {
                        $('#btn_del_branch').css("display", "block");
                        $('#branchName').val("");
                        $('#select_company').trigger("change");
                        $('#result_line').html(message);
                    },
                    error:  function(response) {
                        $('#btn_del_branch').css("display", "block");
                        $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    }
                });
            } else{
                $('#result_line').html("Нельзя удалять это название");
            }
        }
    });

    $('#btn_del_department').on('click', function(){
        var departmentName = $('#select_department option:selected').text();
        var x = confirm("ВНИМАНИЕ!!! <br>Из базы данных будет удален объект `" + departmentName + "`. <br>" +
                          "ЭТА ОПЕРАЦИЯ НЕ ВОССТАНОВИМА!!! <br><br>Продолжить операцию удаления?");
        if(x){
            if($('#select_department').val()>1){
                $('#btn_del_department').css("display", "none");
                $.ajax({
                    url: '../admin/edit-company/delete-department',
                    method: 'POST',
                    dataType: 'text',
                    data: {id: $('#select_department').val()},
                    success: function(message) {
                        $('#btn_del_department').css("display", "block");
                        $('#departmentName').val("");
                        $('#select_branch').trigger("change");
                        $('#result_line').html(message);
                    },
                    error:  function(response) {
                        $('#btn_del_department').css("display", "block");
                        $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    }
                });
            } else{
                $('#result_line').html("Нельзя удалять это название");
            }
        }
    });

    $('#select_company').on('change', function(){
        var btn_company = document.getElementById("btn_company");
        var btn_del_company = document.getElementById("btn_del_company");
        if($('#select_company').val()>0){
            btn_company.value = "Изменить";
            btn_del_company.type = "button";
            $.ajax({
                url: '../user/change-department/select-company',
                method: 'POST',
                dataType: 'json',
                data: {companyId: $('#select_company').val()},
                success: function(branches) {
                    $('#select_branch').empty();
                    $('#select_department').empty();
                    $('#branchName').val("");
                    $('#departmentName').val("");
                    btn_branch.type = "button";
                    btn_branch.value = "Создать";
                    btn_del_branch.type = "hidden";
                    btn_department.type = "hidden";
                    btn_del_department.type = "hidden";
                    if(branches[0].hasLabor){
                        $('#isLabor').prop("checked", true);
                    } else {
                        $('#isLabor').prop("checked", false);
                    }
                    $('#select_branch').append('<option value= -1>Создать новый филиал</option');
                    $.each(branches, function(key, branch){
                        $('#select_branch').append('<option value="' + branch.id + '">' + branch.branchName + '</option');
                    });
                    $('#select_department').append('<option value= -1>Выберите филиал</option');
                },
                error:  function(response) {
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
        } else{
            $('#select_branch').empty();
            $('#select_department').empty();
            $('#companyName').val("");
            $('#branchName').val("");
            $('#departmentName').val("");
            $('#select_branch').append('<option value= -1>Выберите предприятие</option');
            $('#select_department').append('<option value= -1>Выберите филиал</option');
            btn_company.value = "Создать";
            btn_del_company.type = "hidden";
            btn_branch.type = "hidden";
            btn_del_branch.type = "hidden";
            btn_department.type = "hidden";
            btn_del_department.type = "hidden";
        }
    });

    $('#select_branch').on('change', function(){
        var btn_branch = document.getElementById("btn_branch");
        var btn_del_branch = document.getElementById("btn_del_branch");
        if($('#select_branch').val()>0){
            btn_branch.value = "Изменить";
            btn_branch.type = "button";
            btn_department.type = "button"
            btn_del_branch.type = "button";
            btn_department.value = "Создать";
            $.ajax({
                url: '../user/change-department/select-branch',
                method: 'POST',
                dataType: 'json',
                data: {branchId: $('#select_branch').val()},
                success: function(departments) {
                    $('#select_department').empty();
                    $('#departmentName').val("");
                    $('#select_department').append('<option value= -1>Создать новый объект</option');
                    $.each(departments, function(key, department){
                        $('#select_department').append('<option value="' + department.id + '">' + department.departmentName + '</option');
                    });
                },
                error:  function(response) {
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
        } else {
            btn_branch.type = "button";
            btn_branch.value = "Создать";
            btn_del_branch.type = "hidden";
            btn_department.type = "hidden";
            btn_del_department.type = "hidden";
            $('#branchName').val("");
            $('#departmentName').val("");
            $('#select_department').empty();
            $('#select_department').append('<option value= -1>Выберите филиал</option');
        }
    });

    $('#select_department').on('change', function(){
        var btn_department = document.getElementById("btn_department");
        var btn_del_department = document.getElementById("btn_del_department");
        if($('#select_department').val()>0){
            btn_department.value = "Изменить";
            btn_department.type = "button";
            btn_del_department.type = "button";
            $('#departmentName').val("");
       } else {
            btn_department.value = "Создать";
            btn_department.type = "button";
            btn_del_department.type = "hidden";
            $('#departmentName').val("");
        }
    });

    var btn_sql = document.getElementById("btn_sql");
    var clickNumber = 1;
    $('#request_db').on('click', function(){
        clickNumber++;
        if(clickNumber%2==0){
            btn_sql.type = "button";
       } else {
            btn_sql.type = "hidden";
        }
    });

    $('#btn_sql').on('click', function(){
        if($('#select_branch').val()>0){
            $.ajax({
                url: '../admin/branch/sql',
                method: 'POST',
                dataType: 'text',
                data: {branchId: $('#select_branch').val(), companyId: $('#select_new_company').val()},
                success: function(message) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html(message);
                    setTimeout(() => { document.location.href = '../admin/edit-company';}, 800);
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
        }
    });

});