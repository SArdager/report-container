$(document).ready(function(){

    $('#select_user').on('change', function(){
        var userId = $('#select_user').val();
        if(userId>0){
            var userFullName = $('#select_user option:selected').text();
            let posName = userFullName.indexOf(";");
            let posDep = userFullName.indexOf("-");
            let posRole = userFullName.indexOf("*");
            var userName = userFullName.substring(0, posName);
            var login = userFullName.substring(posName+1, posDep).trim();
            var departmentIdString = userFullName.substring(posDep+1, posRole);
            var role = userFullName.substring(posRole+1);
            var departmentId = Number(departmentIdString.trim());
            $('#user_id').val(userId);
            $('#user_name').val(userName);
            $('#username').val(login);
            document.getElementById("show_select").style.display = "none";
            $.ajax({
                url : '../user/load-data/department',
                method: 'POST',
                dataType: 'json',
                data : {departmentId: departmentId},
                success : function(branch) {
                    var branchId = branch.id;
                    var companyId = branch.companyId;
                    $('#select_company').val(companyId);
                    $('#select_branch').empty();
                    $('#select_department').empty();
                    $.ajax({
                        url: '../user/change-department/select-company',
                        method: 'POST',
                        dataType: 'json',
                        data: {companyId: companyId},
                        success: function(branches) {
                           $.each(branches, function(key, branch){
                               $('#select_branch').append('<option value="' + branch.id + '">' + branch.branchName + '</option>');
                           });
                            $('#select_branch').val(branchId);
                            $.ajax({
                                url: '../user/change-department/select-branch',
                                method: 'POST',
                                dataType: 'json',
                                data: {branchId: branchId},
                                success: function(departments) {
                                    $('#select_department').empty();
                                    $.each(departments, function(key, department){
                                        $('#select_department').append('<option value="' + department.id + '">' + department.departmentName + '</option>');
                                    });
                                    $('#select_department').val(departmentId);
                                    $('#select_department').trigger('change');
                                },
                                error:  function(response) {
                                    $('#result_line').html("Ошибка обращения в базу данных. Повторите.");
                                }
                            });
                        },
                        error:  function(response) {
                           $('#result_line').html("Ошибка обращения в базу данных. Повторите.");
                        }
                    });
                },
                error:  function(response) {}
            });
            if(role.indexOf("ADMIN")==0){
                document.getElementById("roleId").checked = true;
            } else {
                document.getElementById("roleId").checked = false;
            }
        }
    });


    let user_name = document.getElementById("user_name");
    user_name.oninput = function(){
        let textValue = $('#user_name').val().trim();
        if(textValue.length>2 ){
            $('#user_id').val(0);
            $('#user_name').readOnly = true;
            $.ajax({
                url : 'search-user',
                method: 'POST',
                dataType: 'json',
                data : {text: textValue},
                success : function(users) {
                    $('#select_user').empty();
                    document.getElementById("show_select").style.display = "block";
                    $('#select_user').append('<option value="-1">Выберите пользователя</option>');
                    $.each(users, function(key, user){
                        $('#select_user').append('<option value="' + user.id + '">' +
                            user.userSurname + ' ' + user.userFirstname + '; ' +
                            user.username + ' - ' + user.departmentId +
                            '*' + user.role +'</option>');
                    });
                    $('#user_name').readOnly = false;
                },
                error:  function(response) {
                    $('#user_name').readOnly = false;
                }
            });
        } else {
            document.getElementById("show_select").style.display = "none";
            $('#user_name').readOnly = false;
        }
    };

    $('#btn_rights').on('click', function(){
        var role;
        var userId = $('#user_id').val();
        if($('#roleId').is(':checked')==false){
            role = "USER";
        } else {
            role = "ADMIN";
        }
        if(userId > 0){
            $.ajax({
                url: 'edit-rights/rights',
                method: 'POST',
                dataType: 'text',
                data: {id: $('#user_id').val(), role: role, departmentId: $('#select_department').val(),
                    rights: $('#user_rights').val() },
                success: function(message) {
                    $('#result_line').html(message);
                },
                error:  function(response) {
                    $('#result_line').html("Ошибка обращения в базу данных. Повторите.");
                }
            });
        } else {
            $('#result_line').html("Выберите пользователя из списка");
        }
    });

    $('#select_department').on('change', function(){
        if($('#user_id').val()>0){
            $.ajax({
                url: '../user/load-data/user-rights',
                method: 'POST',
                dataType: 'json',
                data: {userId: $('#user_id').val(), username: $('#username').val()},
                success: function(userRightsList) {
                    var currentDepartmentId = $('#select_department').val();
                    var userRole = "";
                    $.each(userRightsList, function(key, userRights){
                        if(userRights.departmentId == currentDepartmentId){
                            userRole = userRights.rights;
                        }
                    });
                    if(userRole==="reader"){
                        document.getElementById("readerId").checked = true;
                        $('#readerId').trigger('change');
                    } else if(userRole==="editor"){
                        document.getElementById("editorId").checked = true;
                        $('#editorId').trigger('change');
                    } else if(userRole==="account"){
                        document.getElementById("accountId").checked = true;
                        $('#accountId').trigger('change');
                    } else {
                        document.getElementById("resetId").checked = true;
                        $('#resetId').trigger('change');
                    }
                },
                error:  function(response) {
                    $('#result_line').html("Ошибка обращения в базу данных. Повторите.");
                }
            });
        }
    });


});
