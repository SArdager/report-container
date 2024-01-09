$(document).ready(function(){

    $('#select_user').on('click', function(){
        let userId = $('#select_user').val();
        if(userId>0){
            let userFullName = $('#select_user option:selected').text();
            let posName = userFullName.indexOf(";");
            let userName = userFullName.substring(0, posName);
            let login = userFullName.substring(posName+1);
            $('#user_id').val(userId);
            $('#user_name').val(userName);
            document.getElementById("show_select").style.display = "none";
            $.ajax({
                url : '../user/load-data/user',
                method: 'POST',
                dataType: 'json',
                data : {id: userId},
                success : function(user) {
                    $('#surname').val(user.userSurname);
                    $('#firstname').val(user.userFirstname);
                    $('#position').val(user.position);
                    $('#email').val(user.email);
                    $('#username').val(user.username);
                    if(user.isEnabled){
                        document.getElementById("isEnabled").checked = true;
                        $('#is_enabled').val("true");
                    } else {
                        document.getElementById("isEnabled").checked = false;
                        $('#is_enabled').val("false");
                    }
                },
                error:  function(response) {
                    $('#user_name').attr("readonly", false);
                }
            });
        }
    });

    $('#btn_reset').on('click', function(){
        let userId = $('#user_id').val();
        if(userId > 0){
            $('#btn_reset').css("display", "none");
            $.ajax({
                url: '../admin/reset-password',
                method: 'POST',
                dataType: 'text',
                data: {id: $('#user_id').val()},
                success: function(message) {
                    $('#btn_reset').css("display", "block");
                    $('#result_line').html(message);
                    $('#user_name').val("");
                    $('#password').val("");
                },
                error:  function(response) {
                    $('#btn_reset').css("display", "block");
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
        } else {
            $('#result_line').html("Выберите пользователя из списка");
        }
    });

    let user_name = document.getElementById("user_name");
    user_name.oninput = function(){
        let textValue = $('#user_name').val().trim();
        $('#user_id').val(0);
        if(textValue.length>2){
            $('#user_name').attr("readonly", true);
            $.ajax({
                url : '../admin/search-user',
                method: 'POST',
                dataType: 'json',
                data : {text: textValue},
                success : function(users) {
                    $('#select_user').empty();
                    document.getElementById("show_select").style.display = "block";
                    $.each(users, function(key, user){
                        $('#select_user').append('<option value="-1">Выберите пользователя</option>');
                        $('#select_user').append('<option value="' + user.id + '">' +
                            user.userSurname + ' ' + user.userFirstname + '; ' +
                            user.username + '</option');
                    });
                    $('#user_name').attr("readonly", false);
                },
                error:  function(response) {
                    $('#user_name').attr("readonly", false);
                }
            });
        } else {
            document.getElementById("show_select").style.display = "none";
            $('#user_name').attr("readonly", false);
                $('#surname').val("");
                $('#firstname').val("");
                $('#position').val("");
                $('#email').val("");
                $('#username').val("");
        }
    };

    $('#btn_edit_user').on('click', function(){
        let userId = $('#user_id').val();
        if(userId > 0){
            $('#btn_edit_user').css("display", "none");
            $.ajax({
                url: '../admin/edit-user',
                method: 'POST',
                dataType: 'text',
                data: {id: $('#user_id').val(), userSurname: $('#surname').val(),
                    userFirstname: $('#firstname').val(), position: $('#position').val(),
                    email: $('#email').val(), username: $('#username').val(),
                    isEnabled: $('#is_enabled').val(), isLinkSend: $('#is_link_send').is(':checked')},
                success: function(message) {
                    $('#btn_edit_user').css("display", "block");
                    $('#result_line').html(message);
                    $('#user_name').val("");
                    $('#password').val("");
                },
                error:  function(response) {
                    $('#btn_edit_user').css("display", "block");
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
        } else {
            $('#result_line').html("Выберите пользователя из списка");
        }
    });

    let checkbox_enabled = document.getElementById("isEnabled");
    let checkbox_not_enabled = document.getElementById("isNotEnabled");

    $('#isEnabled').change ( function(){
        if($('#isEnabled').is(':checked')==true){
            checkbox_not_enabled.checked = false;
            $('#is_enabled').val("true");
        } else {
            $('#is_enabled').val("false");
            checkbox_not_enabled.checked = true;
        }
    });

    $('#isNotEnabled').change ( function(){
        if($('#isNotEnabled').is(':checked')==true){
            checkbox_enabled.checked = false;
            $('#is_enabled').val("false");
        } else {
            checkbox_enabled.checked = true;
            $('#is_enabled').val("true");
        }
    });

});
