$(document).ready(function(){

    $('#btn_add_user').on('click', function(){
        var surname = $('#surname').val();
        var firstname = $('#firstname').val();
        var position = $('#position').val();
        var email = $('#email').val();
        var username = $('#username').val();
        var departmentId = $('#select_department').val();
        var password = $('#password').val();
        if($('#roleId').is(':checked')==false){
            $('#user_role').val("USER");
        } else {
            $('#user_role').val("ADMIN");
        }

        function checkLogin(){
            if(username.length>4){
                if(position.length > 4 && email.length > 6 && surname.length > 2){
                    return true;
                } else {
                    $('#result_line').html("Проверьте заполнение всех полей формы !!! \n Фамилия должна содержать 3 и более символов.\n \n Повторите ввод!!!");
                    return false;
                }
            } else {
                $('#result_line').html("Логин пользователя должен быть длинее 5 символов.");
                return false;
            }
        }

        function checkPassword(){
            if($("#password").val() === $("#confirm_password").val()){
                return true;
            } else {
                $('#result_line').html("Значения пароля НЕ СОВПАДАЮТ !!! Повторите ввод!!!");
                return false;
            }
        }
        function checkRights(){
            if($("#user_rights").val() == ""){
                $('#result_line').html("Выберите предоставляемые права пользователя");
                return false;
            } else {
                return true;
            }
        }

        if(checkLogin() && checkPassword() && checkRights()){
            $.ajax({
                url : '/admin/add-user/save-user',
                method: 'POST',
                dataType: 'text',
                data : {userSurname: $('#surname').val(), userFirstname: $('#firstname').val(), position: $('#position').val(),
                    email: $('#email').val(), username: $('#username').val(), curatorId: $('#curator_id').val(),
                    password: $('#password').val(), role: $('#user_role').val(), departmentId: $('#select_department').val(),
                    rights: $('#user_rights').val()},
                success : function(message) {
                    $('#result_line').html(message);
                    if(message.indexOf("добавлен")>0){
                        $('#surname').val("");
                        $('#firstname').val("");
                        $('#position').val("");
                        $('#email').val("");
                        $('#username').val("");
                        $('#curator_id').val(0);
                        $('#curator').val("");
                        $('#password').val("");
                        $('#confirm_password').val("");
                        $('#user_role').val("USER");
                        $('#user_rights').val("reader");
                        $('#select_company').val(1);
                        document.getElementById("roleId").checked = false;
                        document.getElementById("readerId").checked = true;
                        document.getElementById("editorId").checked = false;
                    }
                },
                error:  function(response) {
                    $('#result_line').html(response);
                }
            });
        }
    });

    var show_select = document.getElementById("show_select");
    var curator = document.getElementById("curator");

    curator.oninput = function(){
        var textValue = $('#curator').val().trim();
        if(textValue.length>2){
            $('#curator').readOnly = true;
            $.ajax({
                url : '/admin/search-user',
                method: 'POST',
                dataType: 'json',
                data : {text: textValue},
                success : function(users) {
                    $('#select_curator').empty();
                    show_select.style.display = "block";
                    $.each(users, function(key, user){
                        $('#select_curator').append('<option value="' + user.id + '">' +
                            user.userSurname + ' ' + user.userFirstname + ' - ' + user.username + '</option');
                    });
                    $('#curator').readOnly = false;
                },
                error:  function(response) {
                    $('#curator').readOnly = false;
                    $('#curator_id').val(0);
                }
            });
        }
        if(textValue.length<3){
            show_select.style.display = "none";
            $('#curator').readOnly = false;
            $('#curator_id').val(0);
        }
    };

    $('#select_curator').on('click', function(){
        var curatorId = $('#select_curator').val();
        var curatorName = $('#select_curator option:selected').text();
        let position = curatorName.indexOf("-");
        curatorName = curatorName.substring(0, position);
        $('#curator_id').val(curatorId);
        $('#curator').val(curatorName);
        show_select.style.display = "none";
    });

});
