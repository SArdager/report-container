window.addEventListener("load", function(){
    $('#users_table_body').on('click', function(event){
        let elem = event.target || event.srcElement;
        let login = elem.innerHTML;
        let userName =  elem.nextElementSibling.innerHTML;
        let show_rights = "";
        let rights_html = "";
        $('#userLogin').html(userName);
        $.ajax({
             url: '../user/load-data/user-rights',
             method: 'POST',
             dataType: 'json',
             data: {username: login},
             success: function(userRightsList) {
                 document.getElementById("show_rights").style.display = "block";
                 let rights_body = $('#rights_table_body');
                 rights_body.html('');
                 $.each(userRightsList, function(key, rights){
                     rights_html += "<tr><td>" + rights.departmentName + ", " + rights.branchName +
                             "</td><td>" + rights.rights + "</td></tr>";
                 });
                 rights_body.prepend(rights_html);
                 $("thead[tabindex=0]").focus();
             },
             error:  function(response) {
                 $('#result_line').html("Ошибка обращения в базу данных.");
                 document.getElementById("show_rights").style.display = "none";
             }
        });
    });

    $('#btn_find_user').on('click', function(event){
        $('#show_rights').css("display", "none");
        $('#show_table').css("display", "none");
        $('#btn_find_user').css("display", "none");
        $.ajax({
            url: '../admin/find-user',
            method: 'POST',
            dataType: 'json',
            data: {surname: $('#search_surname').val(), firstname: $('#search_firstname').val(),
                    login: $('#search_login').val(), branchId: $('#select_branch').val(), departmentId: $('#select_department').val()},
            success: function(users) {
                $('#btn_find_user').css("display", "block");
                let new_lines_html ='';
                let body = $('#users_table_body');
                body.html('');
                $('#show_table').css("display", "block");
                $.each(users, function(key, user){
                    if(!$.isArray(users)|| !users.length){
                        $('#result_line').html("Указанный в запросе пользователь отсутствует в базе.");
                    } else {
                        new_lines_html+="<tr><td style='color: blue; text-decoration: underline'>"+ user.username + "</td><td>" +
                            user.userSurname + " " + user.userFirstname + "</td><td>" + user.position + "</td><td>" +
                            user.email + "</td><td>" + user.isEnabled + "</td><td>" + user.role + "</td></tr>";
                    }
                });
                body.prepend(new_lines_html);
            },
            error:  function(response) {
                $('#btn_find_user').css("display", "block");
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });

    $('#select_branch').on('change', function(){
        $.ajax({
            url: '../user/change-department/select-branch',
            method: 'POST',
            dataType: 'json',
            data: {branchId: $('#select_branch').val()},
            success: function(departments) {
                $('#select_department').empty();
                $('#select_department').append('<option value="1">По всем объектам</option');
                $.each(departments, function(key, department){
                    $('#select_department').append('<option value="' + department.id + '">' + department.departmentName + '</option');
                });
            },
            error:  function(response) {
                window.scrollTo({ top: 0, behavior: 'smooth' });
                $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    });


});

