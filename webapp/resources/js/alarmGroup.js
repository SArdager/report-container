$(document).ready(function(){

    $('#select_alarm_user').on('change', function(){
        let userId = $('#select_alarm_user').val();
        if(userId>0){
            let userName = $('#select_alarm_user option:selected').text();
            $('#user_id').val(userId);
            $('#alarm_user_name').val(userName);
            document.getElementById("show_select").style.display = "none";
        }
    });

    document.getElementById("alarm_user_name").oninput = function(){
        let textValue = $('#alarm_user_name').val().trim();
        if(textValue.length>2 && textValue.length<6){
            $('#user_id').val(0);
            $('#alarm_user_name').readOnly = true;
            $.ajax({
                url : 'search-user',
                method: 'POST',
                dataType: 'json',
                data : {text: textValue},
                success : function(users) {
                    $('#select_alarm_user').empty();
                    document.getElementById("show_select").style.display = "block";
                    $('#select_alarm_user').append('<option value="-1">Выберите пользователя</option>');
                    $.each(users, function(key, user){
                        $('#select_alarm_user').append('<option value="' + user.id + '">' +
                            user.userSurname + ' ' + user.userFirstname +
                            '; ' + user.position + '</option>');
                    });
                    $('#alarm_user_name').readOnly = false;
                },
                error:  function(response) {
                    $('#alarm_user_name').readOnly = false;
                }
            });
        } else {
            document.getElementById("show_select").style.display = "none";
            $('#alarm_user_name').readOnly = false;
        }
    };


    $('#select_alarm_branch').on('change', function(){
        var branchId = $('#select_alarm_branch').val();
        if(branchId > 0){
            $.ajax({
                url: 'find-department',
                method: 'POST',
                dataType: 'text',
                data: {branchId: $('#select_alarm_branch').val()},
                success: function(message) {
                    let departmentId = Number(message);
                    if(departmentId==1){
                        $('#alarmGroupName').val("Отсутствует лаборатория");
                    } else {
                        let name = $('#select_alarm_branch option:selected').text();
                        $('#alarmGroupName').val(name);
                    }
                    $('#alarm_department').val(departmentId);
                    $.ajax({
                        url : 'search-department-users',
                        method: 'POST',
                        dataType: 'json',
                        data : {departmentId: departmentId},
                        success : function(users) {
                            $('#select_alarm_user').empty();
                            document.getElementById("show_select").style.display = "block";
                            $('#select_alarm_user').append('<option value="-1">Выберите пользователя</option>');
                            $.each(users, function(key, user){
                                $('#select_alarm_user').append('<option value="' + user.id + '">' +
                                    user.userSurname + ' ' + user.userFirstname +
                                    '; ' + user.position + '</option>');
                            });
                        },
                        error:  function(response) {
                        }
                    });
                },
                error:  function(response) {
                    $('#result_line').html("Ошибка обращения в базу данных. Повторите.");
                }
            });
        } else {
            $('#alarmGroupName').val("");
            $('#alarm_department').val("1");
        }
    });

    $('#select_alarm_group').on('change', function(){
        let btn_alarm = document.getElementById("btn_alarm");
        let btn_del_alarm = document.getElementById("btn_del_alarm");
        let btn_alarm_users = document.getElementById("btn_alarm_users");
        let name_field = document.getElementById("name_field");
        let user_field = document.getElementById("user_field");
        $('#alarm_user_name').val("");
        $('#alarm_user_name').attr('placeholder', 'Первые три буквы фамилии');

        if($('#select_alarm_group').val()>0){
            btn_alarm.value = "Изменить";
            btn_del_alarm.type = "button";
            btn_alarm_users.type = "button";
            name_field.style.display = "block";
            user_field.style.display = "block";
            $('#select_alarm_branch').val(0);
            $('#alarmGroupName').val("");
        } else if($('#select_alarm_group').val()<0){
            name_field.style.display = "none";
            user_field.style.display = "none";
            btn_alarm_users.type = "hidden";
        } else {
            btn_alarm.value = "Создать";
            btn_del_alarm.type = "hidden";
            btn_alarm_users.type = "hidden";
            name_field.style.display = "block";
            user_field.style.display = "none";
            $('#select_alarm_branch').val(0);
            $('#alarmGroupName').val("");
        }
    });

    $('#btn_alarm').on('click', function(){
        var alarmGroupName = $('#alarmGroupName').val();
        if(alarmGroupName.length>1 && alarmGroupName.indexOf("лабор")<0){
            $.ajax({
                url: 'edit-alarm-group/change-group',
                method: 'POST',
                dataType: 'text',
                data: {id: $('#select_alarm_group').val(), departmentId: $('#alarm_department').val(), alarmGroupName: alarmGroupName},
                success: function(message) {
                    $('#result_line').html(message);
                    setTimeout(() => { document.location.href = '../admin/alarm-groups';}, 800);
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
            url: 'edit-alarm-group/delete-group',
            method: 'POST',
            dataType: 'text',
            data: {id: $('#select_alarm_group').val()},
            success: function(message) {
                $('#result_line').html(message);
                setTimeout(() => { document.location.href = 'alarm-groups';}, 800);
            },
            error:  function(response) {
                $('#result_line').html("Ошибка обращения в базу данных. Повторите.");
            }
        });
    });

    $('#btn_add_user').on('click', function(){
        if($('#select_alarm_group').val()>0){
            if($('#user_id').val()>0){
               $.ajax({
                    url: '../admin/edit-alarm-group/add-user',
                    method: 'POST',
                    dataType: 'text',
                    data: {userId: $('#user_id').val(), alarmGroupId: $('#select_alarm_group').val()},
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
            if($('#user_id').val()>0){
                $.ajax({
                    url: 'edit-alarm-group/remove-user',
                    method: 'POST',
                    dataType: 'text',
                    data: {userId: $('#user_id').val(), alarmGroupId: $('#select_alarm_group').val()},
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
            url: 'edit-alarm-group/get-user-group',
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
                        alarm_html += "<tr><td>" + user.userSurname + " " + user.userFirstname +
                            "</td><td>" + user.position + "</td><td>" + user.email + "</td></tr>";
                    });
                } else {
                    alarm_html = "<tr><td colspan='3'>Список оповещения пустой</td></tr>";
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
