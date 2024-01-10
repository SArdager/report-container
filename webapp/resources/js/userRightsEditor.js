$(document).ready(function(){

    $('#select_user').on('change', function(){
        let userId = $('#select_user').val();
        if(userId>0){
            let userFullName = $('#select_user option:selected').text();
            let names = userFullName.split("; ");
            let userName = names[0];
            let login = names[1];
            let departmentId = names[2];
            let role = names[3];
            $('#user_id').val(userId);
            $('#user_name').val(userName);
            $('#username').val(login);
            document.getElementById("show_select").style.display = "none";
            if(role.indexOf("ADMIN")!=-1){
                document.getElementById("roleId").checked = true;
            } else {
                document.getElementById("roleId").checked = false;
            }
            $.ajax({
                url : '../user/load-data/department',
                method: 'POST',
                dataType: 'json',
                data : {departmentId: departmentId},
                success : function(branch) {
                    let branchId = branch.id;
                    let companyId = branch.companyId;
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
                                    window.scrollTo({ top: 0, behavior: 'smooth' });
                                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                                }
                            });
                        },
                        error:  function(response) {
                            window.scrollTo({ top: 0, behavior: 'smooth' });
                            $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                        }
                    });
                },
                error:  function(response) {}
            });
        }
    });

    let user_name = document.getElementById("user_name");
    user_name.oninput = function(){
        $('#line_cut_rights').trigger("click");
        $('#line_cut_table').trigger("click");
        let textValue = $('#user_name').val().trim();
        if(textValue.length>2 && textValue.length<6){
            $('#user_id').val(0);
            $('#username').val("");
            $('#user_name').attr("readonly", true);
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
                            user.username + '; ' + user.departmentId +
                            '; ' + user.role + '</option>');
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
        }
    };

    $('#btn_rights').on('click', function(){
        if($('#user_id').val()>0 || $('#username').val().length>1){
            if($('#group_change_checkbox').is(':checked')){
                let checkedId = [];
                $('input:checkbox:checked').each(function(){
                    if(this.name == "department_boxes"){
                        checkedId.push(this.id.substring(4));
                    }
                });
                let rightsString = checkedId.join(' ');
                $.ajax({
                    url: 'edit-rights/add-group-rights',
                    method: 'POST',
                    dataType: 'text',
                    data: {id: $('#user_id').val(), rights: rightsString},
                    success: function(message) {
                        $('#btn_rights').css("display", "block");
                        $('#result_line').html(message);
                    },
                    error:  function(response) {
                        $('#btn_rights').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    }
                });
            } else {
                let role;
                if($('#roleId').is(':checked')==false){
                    role = "USER";
                } else {
                    role = "ADMIN";
                }
                let user_rights = $('input[type="radio"][name="rights"]:checked').val();
                $('#btn_rights').css("display", "none");
                $.ajax({
                    url: 'edit-rights/rights',
                    method: 'POST',
                    dataType: 'text',
                    data: {id: $('#user_id').val(), username: $('#username').val(), role: role,
                        departmentId: $('#select_department').val(), rights: user_rights },
                    success: function(message) {
                        $('#btn_rights').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html(message);
                    },
                    error:  function(response) {
                        $('#btn_rights').css("display", "block");
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                        $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    }
                });
            }
        } else {
            $('#result_line').html("Выберите пользователя из списка");
        }
    });

    $('#select_department').on('change', function(){
        if($('#user_id').val()>0 || $('#username').val().length>1){
            $.ajax({
                url: '../user/load-data/user-rights',
                method: 'POST',
                dataType: 'json',
                data: {userId: $('#user_id').val(), username: $('#username').val()},
                success: function(userRightsList) {
                    if(userRightsList!=null && userRightsList.length>0){
                        let currentDepartmentId = $('#select_department').val();
                        let userRole = "reset";
                        let rights_html = "";
                        let rights_body = $('#rights_table_body');
                        rights_body.html('');
                        $('#userLogin').val($('#username').val());
                        $.each(userRightsList, function(key, userRights){
                            rights_html += "<tr><td>" + userRights.departmentName + ", " + userRights.branchName +
                                     "</td><td>" + userRights.rights + "</td></tr>";
                            if(userRights.departmentId == currentDepartmentId){
                                userRole = userRights.rightsShort;
                            }
                        });
                        rights_body.prepend(rights_html);
                        if(userRole==="reader"){
                            document.getElementById("readerId").checked = true;
                        } else if(userRole==="courier"){
                            document.getElementById("courierId").checked = true;
                        } else if(userRole==="editor"){
                            document.getElementById("editorId").checked = true;
                        } else if(userRole==="changer"){
                            document.getElementById("changerId").checked = true;
                        } else if(userRole==="righter"){
                            document.getElementById("righterId").checked = true;
                        } else if(userRole==="chef"){
                            document.getElementById("chefId").checked = true;
                        } else if(userRole==="account"){
                            document.getElementById("accountId").checked = true;
                        } else if(userRole==="creator"){
                            document.getElementById("creatorId").checked = true;
                        } else {
                            document.getElementById("resetId").checked = true;
                        }
                    } else {
                        document.getElementById("resetId").checked = true;
                        $('#result_line').html("Данный пользователь не найден или у него отсутствуют права по объектам.");
                    }
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
        } else {
            $('#result_line').html("Выберите пользователя из списка");
        }
    });

    $('#user_clean').on("click", function(){
        $('#user_id').val('0');
        $('#username').val("");
        $('#user_name').val("");
        document.getElementById("resetId").checked = true;
        document.getElementById("show_rights").style.display = "none";
        document.getElementById("show_table").style.display = "none";
    });
    $('#btn_show_dep').on('click', function(){
        document.getElementById("show_rights").style.display = "block";
    });
    $('#line_cut_rights').on("click", function(){
        document.getElementById("show_rights").style.display = "none";
    });
    $('#line_cut_table').on("click", function(){
        document.getElementById("show_table").style.display = "none";
    });
});

window.addEventListener("load", function(){
    $('#btn_show_users').on('click', function(event){
        $('#btn_show_users').css("display", "none");
        $.ajax({
            url: '../admin/find-user',
            method: 'POST',
            dataType: 'json',
            data: {branchId: $('#select_branch').val()},
            success: function(users) {
                $('#btn_show_users').css("display", "block");
                let new_lines_html ='';
                let body = $('#users_table_body');
                body.html('');
                document.getElementById("show_table").style.display = "block";
                $.each(users, function(key, user){
                    if(!$.isArray(users) || !users.length){
                        $('#result_line').html("По филиалу пользователи отсутствуют в базе.");
                    } else {
                        new_lines_html+="<tr><td style='color: blue; text-decoration: underline'>"+ user.username + "</td><td>" +
                            user.userSurname + " " + user.userFirstname + "</td><td>" +
                            user.position + "</td><td>" + user.email + "</td><td>" +
                            user.isEnabled + "</td><td>" + user.role + "</td></tr>";
                    }
                });
                body.prepend(new_lines_html);
            },
            error:  function(response) {
                $('#btn_show_users').css("display", "block");
                $('#result_line').html("Для получения информации о правах пользователя кликните по ячейке с логином.");
            }
        });
    });

     $('#users_table_body').on('click', function(event){
         let elem = event.target || event.srcElement;
         let login = elem.innerHTML;
         let userName =  elem.nextElementSibling.innerHTML;
         $('#username').val(login);
         $('#user_name').val(userName);
         $('#select_department').trigger("change");
         let row = elem.closest('tr').innerHTML;
         if(row.indexOf("ADMIN")>0){
             document.getElementById("roleId").checked = true;
         }
         if(row.indexOf("USER")>0){
             document.getElementById("roleId").checked = false;
         }
     });

    $('#group_change_checkbox').on('click', function(){
        if($('#group_change_checkbox').is(':checked')==true){
            $('#group_change_field').css("display", "block");
            $('#departments_table_body').html("");
            $.ajax({
                url: '../user/load-departments',
                method: 'POST',
                dataType: 'html',
                data: {userId: $('#user_id').val(), branchId: $('#select_branch').val()},
                success: function(message) {
                    let departments_table_body = $('#departments_table_body');
                    departments_table_body.prepend(message);
                },
                error:  function(response) {
                   window.scrollTo({ top: 0, behavior: 'smooth' });
                   $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                }
            });
        } else {
            $('#group_change_field').css("display", "none");
        }
    });

});
