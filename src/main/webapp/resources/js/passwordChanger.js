$(document).ready(function(){

    $('#btn_change_password').on('click', function(){
        var strPassword = $('#password').val();
        var strConfirmPassword = $('#confirm_password').val();
        var m_strUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var m_strLowerCase = "abcdefghijklmnopqrstuvwxyz";
        var m_strNumber = "0123456789";

        if(checkPassword() && checkConfirmPassword()) {
            $('#result_line').html("Отправлен запрос на смену пароля. Ожидайте.");
            $('#change_password').submit();
        }

        function checkConfirmPassword(){
            if(strPassword === strConfirmPassword){
                return true;
            } else {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Введенные пароли НЕ СОВПАДАЮТ !!!");
                return false;
            }
        }

        function checkPassword(){
            if(strPassword.length > 5){
               if(checkContain(strPassword, m_strLowerCase)>0){
                 if(checkContain(strPassword, m_strUpperCase)>0){
                       if(checkContain(strPassword, m_strNumber)>0){
                           return true;
                       } else {
                            $('#result_line').html("Пароль должен содержать цифру.");
                            return false;
                       }
                   } else {
                       $('#result_line').html("Пароль должен содержать латинскую букву в верхнем регистре.");
                       return false;
                   }
               } else {
                    $('#result_line').html("Пароль должен содержать латинскую букву в нижнем регистре.");
                    return false;
               }
            } else {
               $('#result_line').html("Пароль должен содержать от 6 символов и более.");
               return false;
            }
        }

       function checkContain(strPassword, strCheck){
           var nCount = 0;
           for (i = 0; i < strPassword.length; i++)
           {
               if (strCheck.indexOf(strPassword.charAt(i)) > -1)
               {
                   nCount++;
               }
           }
           return nCount;
       }
    });

    $('#btn_forget_password').on('click', function(){
        var login = $('#login').val();
        if(login.length>0){
            $('#result_line').html("Направлен запрос на сброс пароля. Ожидайте ответа.");
            $('#btn_forget_password').css("display", "none");
            $.ajax({
                url: 'forget-password',
                method: 'POST',
                dataType: 'text',
                data: {username: login},
                success: function(message) {
                    $('#btn_forget_password').css("display", "block");
                    if(message.length>0){
                        $('#result_line').html(message);
                    }
                },
                error:  function(response) {
                    $('#btn_forget_password').css("display", "block");
                }
            });
        } else {
           $('#result_line').html("Введите логин");
        }
    });

});
