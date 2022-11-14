$(document).ready(function(){

    $('#btn_add_user').on('click', function(){
        var surname = $('#surname').val();
        var firstname = $('#firstname').val();
        var position = $('#position').val();
        var email = $('#email').val();
        var username = $('#username').val();
        var departmentId = $('#select_department').val();
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

        function checkRights(){
            if($("#user_rights").val() == ""){
                $('#result_line').html("Выберите предоставляемые права пользователя");
                return false;
            } else {
                return true;
            }
        }

        if(checkLogin() && checkRights()){
            $.ajax({
                url : 'add-user/save-user',
                method: 'POST',
                dataType: 'text',
                data : {userSurname: $('#surname').val(), userFirstname: $('#firstname').val(), position: $('#position').val(),
                    email: $('#email').val(), username: $('#username').val(), role: $('#user_role').val(),
                    departmentId: $('#select_department').val(), rights: $('#user_rights').val()},
                success : function(message) {
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
                        $('#user_rights').val("reader");
                        $('#select_company').val(1);
                        $('#select_company').trigger("change");
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


});
