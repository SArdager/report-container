$(document).ready(function(){

    $('#select_department').on('change', function(){
        if($('#user_id').val() > 0){
            loadUserRights();
        } else {
            $('#user_clean').trigger("click");
        }
    });

    $('#select_user').on('change', function(){
        if($('#select_user').val()>0){
            $('#user_id').val($('#select_user').val());
            $('#find_name').val($('#select_user option:selected').text());
            loadUserRights();
        } else {
            $('#user_clean').trigger("click");
        }
    });
    $('#select_short').on('change', function(){
        if($('#select_short').val()>0){
            $('#user_id').val($('#select_short').val());
            $('#find_name').val($('#select_short option:selected').text());
            loadUserRights();
        } else {
            $('#user_clean').trigger("click");
        }
    });

    function loadUserRights(){
        $.ajax({
            url: '../user/load-data/user-rights',
            method: 'POST',
            dataType: 'json',
            data: {userId: $('#user_id').val()},
            success: function(userRightsList) {
                let currentDepartmentId = $('#select_department').val();
                let userRole = "reset";
                $.each(userRightsList, function(key, userRights){
                    if(userRights.departmentId == currentDepartmentId){
                        userRole = userRights.rights;
                    }
                });
                if(userRole==="chef") {
                    $('#btn_rights').css("display", "none");
                    $('#btn_del').css("display", "none");
                    $('#result_line').html("Вы не можете поменять права этому пользователю на данном объекте");
                } else {
                    $('#btn_rights').css("display", "block");
                    $('#btn_del').css("display", "block");
                    if(userRole==="reader"){
                        document.getElementById("readerId").checked = true;
                    } else if(userRole==="editor"){
                        document.getElementById("editorId").checked = true;
                    } else if(userRole==="courier"){
                        document.getElementById("courierId").checked = true;
                    } else if(userRole==="changer"){
                        document.getElementById("changerId").checked = true;
                    } else if(userRole==="righter"){
                        document.getElementById("righterId").checked = true;
                    } else if(userRole==="creator"){
                        document.getElementById("creatorId").checked = true;
                    } else {
                        document.getElementById("resetId").checked = true;
                    }
                }
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    };

    let find_name = document.getElementById("find_name");
    find_name.oninput = function(){
        let textValue = $('#find_name').val().trim();
        if(textValue.length>0 && textValue.length<5){
            $('#select_short').empty();
            $('#user_id').val(0);
            document.getElementById("show_select").style.display = "none";
            document.getElementById("show_short_select").style.display = "block";
            $('#select_short').append("<option value='0'>Кликните пользователя</option>");
            $("#select_user option").each(function() {
                let u_val= $(this).val();
                let u_name= $(this).text();
                if(u_name.indexOf(textValue)>-1){
                    $('#select_short').append("<option value='" + u_val + "'>" + u_name + "</option>");
                }
            });
        }
    };

    $('#user_clean').on('click', function(){
        $('#user_id').val(0);
        $('#find_name').val("");
        $('#select_user').val(0);
        document.getElementById("resetId").checked = true;
        document.getElementById("show_select").style.display = "block";
        document.getElementById("show_short_select").style.display = "none";
    });

    $('#btn_rights').on('click', function(){
        let user_rights = $('input[type="radio"][name="rights"]:checked').val();
        if($('#user_id').val() > 0){
            $('#btn_rights').css("display", "none");
            $.ajax({
                url: '../user/change-rights',
                method: 'POST',
                dataType: 'text',
                data: {userId: $('#user_id').val(), departmentId: $('#select_department').val(),
                    rights: user_rights },
                success: function(message) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html(message);
                    $('#btn_rights').css("display", "block");
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    $('#btn_rights').css("display", "block");
                }
            });
        } else {
            $('#result_line').html("Выберите пользователя из списка");
        }
    });

    $('#btn_del').on('click', function(){
        if($('#user_id').val() > 0){
            var x = confirm("Вы удаляете пользователя и не сможете после выполнения отменить удаление!!\nПродолжить удаление?");
            if(x){
            $('#btn_del').css("display", "none");
                $.ajax({
                    url: '../user/del-user',
                    method: 'POST',
                    dataType: 'text',
                    data: {id: $('#user_id').val()},
                    success: function(message) {
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html(message);
                        $('#btn_del').css("display", "block");
                    },
                    error:  function(response) {
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                        $('#btn_del').css("display", "block");
                    }
                });
            }
        } else {
            $('#result_line').html("Выберите пользователя из списка");
        }
    });



});
