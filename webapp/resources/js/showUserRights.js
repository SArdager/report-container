window.addEventListener("load", function(){
    $('#users_table_body').on('click', function(event){
        var elem = event.target || event.srcElement;
        var login = elem.innerHTML;
        var userName =  elem.nextElementSibling.innerHTML;
        var show_rights = document.getElementById("show_rights");
        var rights_html = "";
        $('#userLogin').html(userName);
           $.ajax({
                url: '/user/load-data/user-rights',
                method: 'POST',
                dataType: 'json',
                data: {username: login},
                success: function(userRightsList) {
                    show_rights.style.display = "block";
                    var rights_body = $('#rights_table_body');
                    rights_body.html('');
                    $.each(userRightsList, function(key, rights){
                        rights_html += "<tr><td>" + rights.departmentName + ", " + rights.branchName +
                                "</td><td>" + rights.rights + "</td></tr>";
                    });
                    rights_body.prepend(rights_html);
                },
                error:  function(response) {
                    $('#result_line').html("Для получения информации о правах пользователя кликните по ячейке с логином.");
                    show_rights.style.display = "none";
                }
            });
    });
});

