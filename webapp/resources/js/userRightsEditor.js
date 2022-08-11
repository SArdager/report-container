$(document).ready(function(){
    var show_select = document.getElementById("show_select");
    var user_name = document.getElementById("user_name");

    $('#select_user').on('click', function(){
        var userId = $('#select_user').val();
        var userFullName = $('#select_user option:selected').text();
        let posName = userFullName.indexOf(";");
        let posDep = userFullName.indexOf("-");
        let posRole = userFullName.indexOf("*");
        var userName = userFullName.substring(0, posName);
        var login = userFullName.substring(posName+1, posDep);
        var department = userFullName.substring(posDep+1, posRole);
        var role = userFullName.substring(posRole+1);
        var departmentId = Number(department.trim());
        $('#user_id').val(userId);
        $('#user_name').val(userName);
        show_select.style.display = "none";
        $.ajax({
            url : '/user/load-data/department',
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
                    url: '/user/change-department/select-company',
                    method: 'POST',
                    dataType: 'json',
                    data: {companyId: companyId},
                    success: function(branches) {
                       $.each(branches, function(key, branch){
                           $('#select_branch').append('<option value="' + branch.id + '">' + branch.branchName + '</option');
                       });
                        $('#select_branch').val(branchId);
                        $('#select_branch').trigger("change");
                    },
                    error:  function(response) {
                       alert("Ошибка обращения в базу данных. Повторите.");
                    }
                });
            },
            error:  function(response) {
            }
        });
        if(role.indexOf("ADMIN")==0){
            document.getElementById("roleId").checked = true;
        } else {
            document.getElementById("roleId").checked = false;
        }
        $('#select_department').val(departmentId);
    });

    user_name.oninput = function(){
        var textValue = $('#user_name').val().trim();
        $('#user_id').val(0);
        if(textValue.length>2){
            $('#user_name').readOnly = true;
            $.ajax({
                url : '/admin/search-user',
                method: 'POST',
                dataType: 'json',
                data : {text: textValue},
                success : function(users) {
                    $('#select_user').empty();
                    show_select.style.display = "block";
                    $.each(users, function(key, user){
                        $('#select_user').append('<option value="' + user.id + '">' +
                            user.userSurname + ' ' + user.userFirstname + '; ' +
                            user.username + ' - ' + user.departmentId +
                            '*' + user.role +'</option');
                    });
                    $('#user_name').readOnly = false;
                },
                error:  function(response) {
                    $('#user_name').readOnly = false;
                }
            });
        } else {
            show_select.style.display = "none";
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
        function checkRights(){
            if($("#user_rights").val() == ""){
                $('#result_line').html("Выберите предоставляемые права пользователя");
                return false;
            } else {
                return true;
            }
        }
        function checkUser(){
            if(userId > 0){
                return true;
            } else {
                $('#result_line').html("Выберите пользователя из списка");
                return false;
            }
        }
        if(checkUser() && checkRights()){
            $.ajax({
                url: '/admin/edit-rights/rights',
                method: 'POST',
                dataType: 'text',
                data: {id: $('#user_id').val(), role: role, departmentId: $('#select_department').val(),
                    rights: $('#user_rights').val() },
                success: function(message) {
                    $('#result_line').html(message);
                },
                error:  function(response) {
                    alert("Ошибка обращения в базу данных. Повторите.");
                }
            });
        }
    });

    $('#select_alarm_group').on('change', function(){
        var btn_alarm = document.getElementById("btn_alarm");
        var btn_del_alarm = document.getElementById("btn_del_alarm");
        var alarm_name_row = document.getElementById("alarm_name_row");
        if($('#select_alarm_group').val()>0){
            btn_alarm.value = "Изменить";
            btn_del_alarm.type = "button";
            alarm_name_row.style.display = "none";
        } else {
            btn_alarm.value = "Создать";
            btn_del_alarm.type = "hidden";
            alarm_name_row.style.display = "block";
        }
    });

    $('#btn_alarm').on('click', function(){
        var alarmGroupName = $('#alarmGroupName').val();
        if(alarmGroupName.length>1){
            $.ajax({
                url: '/admin/edit-alarm-group/change-group',
                method: 'POST',
                dataType: 'text',
                data: {id: $('#select_alarm_group').val(), alarmGroupName: alarmGroupName},
                success: function(message) {
                    $('#result_line').html(message);
                    document.location.href = '/admin/alarm-groups';
                },
                error:  function(response) {
                    $('#result_line').html("Ошибка обращения в базу данных. Повторите.");
                }
            });
        } else {
            $('#result_line').html("Напишите название новой группы оповещения.");
        }
    });

    $('#btn_del_alarm').on('click', function(){
        $.ajax({
            url: '/admin/edit-alarm-group/delete-group',
            method: 'POST',
            dataType: 'text',
            data: {id: $('#select_alarm_group').val()},
            success: function(message) {
                $('#result_line').html(message);
                document.location.href = '/admin/alarm-groups';
            },
            error:  function(response) {
                $('#result_line').html("Ошибка обращения в базу данных. Повторите.");
            }
        });
    });

    $('#btn_add_user').on('click', function(){
        if($('#select_alarm_group').val()>0){
            if($('#select_user').val()>0){
               $.ajax({
                    url: '/admin/edit-alarm-group/add-user',
                    method: 'POST',
                    dataType: 'text',
                    data: {userId: $('#select_user').val(), alarmGroupId: $('#select_alarm_group').val()},
                    success: function(message) {
                        $('#result_line').html(message);
                        $('#select_user').val(-1);
                    },
                    error:  function(response) {
                        $('#result_line').html("Ошибка обращения в базу данных. Повторите.");
                    }
                });
            } else {
                $('#result_line').html("Выберите пользователя кликом из предлагаемого списка." +
                    "\nПри отсутствии в списке - добавьте нового пользователя");
            }
        } else {
            $('#result_line').html("Выберите группу оповещения");
        }
    });

    $('#btn_remove_user').on('click', function(){
        if($('#select_alarm_group').val()>0){
            if($('#select_user').val()>0){
                $.ajax({
                    url: '/admin/edit-alarm-group/remove-user',
                    method: 'POST',
                    dataType: 'text',
                    data: {userId: $('#select_user').val(), alarmGroupId: $('#select_alarm_group').val()},
                    success: function(message) {
                        $('#result_line').html(message);
                    },
                    error:  function(response) {
                        $('#result_line').html("Ошибка обращения в базу данных. Повторите.");
                    }
                });
            } else {
                $('#result_line').html("Выберите пользователя кликом из предлагаемого списка." +
                    "\nПри отсутствии в списке - добавьте нового пользователя");
            }
        } else {
            $('#result_line').html("Выберите группу оповещения");
        }
    });

    var show_alarm_table = document.getElementById("show_alarm_table");

    $('#btn_alarm_users').on('click', function(){
        $.ajax({
            url: '/admin/edit-alarm-group/get-user-group',
            method: 'POST',
            dataType: 'json',
            data: {alarmGroupId: $('#select_alarm_group').val()},
            success: function(users) {
                var alarm_html = "";
                var alarm_users_body = $('#alarm_users_body');
                show_alarm_table.style.display = "block";
                alarm_users_body.html('');
                if(users!=null && users.length>0){
                    $.each(users, function(key, user){
                        alarm_html += "<tr><td>" + user.username + "</td><td>" + user.userSurname + " " +
                            user.userFirstname + "</td><td>" + user.position + "</td><td>" + user.email + "</td></tr>";
                    });
                } else {
                    alarm_html = "<tr><td colspan='4'>Список оповещения пустой</td></tr>";
                }
                alarm_users_body.prepend(alarm_html);
            },
            error:  function(response) {
                $('#result_line').html("Ошибка обращения в базу данных. Повторите.");
            }
        });
    });

    $('#line_cut_alarm').on('click', function(){
       show_alarm_table.style.display = "none";
    });

});
