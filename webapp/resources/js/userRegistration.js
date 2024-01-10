$(document).ready(function(){

    $('#btn_add_user').on('click', function(){
        let surname = $('#surname').val();
        let firstname = $('#firstname').val();
        let position = $('#position').val();
        let email = $('#email').val();
        let username = $('#username').val();
        let departmentId = $('#select_department').val();
        let user_rights;
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
                    $('#result_line').html("Проверьте заполнение всех полей формы !!! <br> Фамилия должна содержать 3 и более символов.\n \n Перегрузите страницу ввод!!!");
                    return false;
                }
            } else {
                $('#result_line').html("Логин пользователя должен быть длинее 5 символов.");
                return false;
            }
        }

        function checkRights(){
            user_rights = $('input[type="radio"][name="rights"]:checked').val();
            if(user_rights != "reset"){
                return true;
            } else {
                $('#result_line').html("Выберите предоставляемые права пользователю");
                return false;
            }
        }

        if(checkLogin() && checkRights()){
            $('#btn_add_user').css("display", "none");
            $.ajax({
                url : 'add-user/save-user',
                method: 'POST',
                dataType: 'text',
                data : {userSurname: $('#surname').val(), userFirstname: $('#firstname').val(), position: $('#position').val(),
                    email: $('#email').val(), username: $('#username').val(), role: $('#user_role').val(),
                    departmentId: $('#select_department').val(), rights: user_rights},
                success : function(message) {
                    $('#btn_add_user').css("display", "block");
                    $('#result_line').html(message);
                    if(message.indexOf("добавлен")>0){
                        $('#surname').val("");
                        $('#firstname').val("");
                        $('#position').val("");
                        $('#email').val("");
                        $('#username').val("");
                        $('#password').val("");
                        $('#confirm_password').val("");
                        $('#user_role').val("USER");
                        $('#select_company').val(1);
                        $('#select_company').trigger("change");
                        document.getElementById("roleId").checked = false;
                        document.getElementById("resetId").checked = true;
                    }
                },
                error:  function(response) {
                    $('#btn_add_user').css("display", "block");
                    $('#result_line').html(response);
                }
            });
        }
    });

    $('#btn_check_double').on('click', function(){
         $('#btn_check_double').css("display", "none");
         $.ajax({
             url : 'add-user/check-double-user',
             method: 'GET',
             dataType: 'text',
             success : function(message) {
                 $('#btn_check_double').css("display", "block");
                 $('#double_field').css("display", "block");
                 let double_users_table_body = $('#double_users_table_body');
                 double_users_table_body.html("");
                 double_users_table_body.prepend(message);
             },
             error:  function(response) {
                 $('#btn_check_double').css("display", "block");
                 $('#result_line').html(response);
             }
         });
    });

    $('#clean_double_field').click(function(){
        $('#double_field').css("display", "none");
    });

    $('#select_position').on('change', function(){
        $('#position').val($('#select_position option:selected').text());
        $('#select_position').val("0");
    });

});
