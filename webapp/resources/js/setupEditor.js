$(document).ready(function(){

    $.ajax({
        url: '../user/choose-branch',
        method: 'POST',
        dataType: 'html',
        success: function(message) {
            let branches_table_body = $('#branches_table_body');
            branches_table_body.prepend(message);
        },
        error:  function(response) {
           window.scrollTo({ top: 0, behavior: 'smooth' });
           $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
        }
    });

    function chooseDepartments(branchId){
        $('#departments_table_body').html("");
        $.ajax({
            url: '../user/choose-department',
            method: 'POST',
            dataType: 'html',
            data: {branchId: branchId},
            success: function(message) {
                let departments_table_body = $('#departments_table_body');
                departments_table_body.prepend(message);
            },
            error:  function(response) {
               window.scrollTo({ top: 0, behavior: 'smooth' });
               $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
            }
        });
    }

    $('#all_branches').on('click', function(){
        $('#branches_table_body tr').css("background", "white");
        $('#departments_table_body').html("");
        if($('#all_branches').is(':checked')){
            $('.branch_box').each(function(){
                this.checked = true;
            });
        } else {
            $('.branch_box').each(function(){
                this.checked = false;
            });
        }
    });

    $('#all_departments').on('click', function(){
        $('#departments_table_body tr').css("background", "white");
        if($('#all_departments').is(':checked')){
            $('.dep_box').each(function(){
                this.checked = true;
            });
        } else {
            $('.dep_box').each(function(){
                this.checked = false;
            });
        }
    });

    $('#branches_table_body').on('click', function(event){
        let elem = event.target || event.srcElement;
        let trId = elem.closest('tr').id;
        $('#branches_table_body tr').css("background", "white");
        document.getElementById(trId).style.background = "blue";
        let branchId = trId.substring(3);
        let local_box = document.getElementById("br_" + branchId);
        $('#departments_table_body').html("");
        $('#save_dep').css("display", "none");
        if(local_box.checked == true){
            chooseDepartments(branchId);
        }
    });

    $('#departments_table_body').on('click', function(event){
        let elem = event.target || event.srcElement;
        let trdId = elem.closest('tr').id;
        let departmentId = trdId.substring(4);
        let local_dep_box = document.getElementById("dep_" + departmentId);
        $('#departments_table_body tr').css("background", "white");
        if(local_dep_box.checked == false){
            local_dep_box.checked == true;
        } else {
            document.getElementById(trdId).style.background = "green";
            local_dep_box.checked == false;
        }
        $('#save_dep').css("display", "block");
    });

    $('#btn_save').on("click", function(){
        let branchArray = "";
        let depArray = "";
        let branchId = "";
        let departmentId = "";
        $('.branch_box:checkbox:checked').each(function() {
            let boxId = $(this).attr('id');
            let tr = document.getElementById("tr" + boxId.substring(2));
            if(tr.style.background == "blue"){
                branchId = boxId.substring(3);
            }
            branchArray += boxId.substring(3) + ",";
        });
        $('.dep_box:checkbox:checked').each(function() {
            let depBoxId = $(this).attr('id');
            let d_tr = document.getElementById("trd" + depBoxId.substring(3));
            if(d_tr.style.background == "green"){
                departmentId = depBoxId.substring(4);
            }
            depArray += depBoxId.substring(4) + ",";
        });
        if(branchId!=null && branchId.length>0){
            $('#btn_save').css("display", "none");
            $.ajax({
                url: '../user/save-preferences',
                method: 'POST',
                dataType: 'html',
                data: {branchPref: branchArray, depPref: depArray, branchId: branchId, departmentId: departmentId},
                success: function(message) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html(message);
                    $('#btn_save').css("display", "block");
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    $('#btn_save').css("display", "block");
                }
            });
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Не отмечен филиал для показа в верхней строке.");
        }
    });

    $('#save_dep').on("click", function(){
        let branchArray = "";
        let depArray = "";
        let branchId;
        let departmentId;
        $('.branch_box:checkbox:checked').each(function() {
            let boxId = $(this).attr('id');
            let tr = document.getElementById("tr" + boxId.substring(2));
            if(tr.style.background == "blue"){
                branchId = boxId.substring(3);
            }
        });
        $('.dep_box:checkbox:checked').each(function() {
            let depBoxId = $(this).attr('id');
            let d_tr = document.getElementById("trd" + depBoxId.substring(3));
            if(d_tr.style.background == "green"){
                departmentId = depBoxId.substring(4);
            }
            depArray += depBoxId.substring(4) + ",";
        });

        if(departmentId!=null && departmentId.length>0){
            $('#save_dep').css("display", "none");
            $('#btn_save').css("display", "none");
            $.ajax({
                url: '../user/save-preferences',
                method: 'POST',
                dataType: 'html',
                data: {branchPref: branchArray, depPref: depArray, branchId: branchId, departmentId: departmentId},
                success: function(message) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html(message);
                    $('#save_dep').css("display", "block");
                    $('#btn_save').css("display", "block");
                },
                error:  function(response) {
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                    $('#result_line').html("Ошибка обращения в базу данных. Перегрузите страницу.");
                    $('#save_dep').css("display", "block");
                    $('#btn_save').css("display", "block");
                }
            });
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' });
            $('#result_line').html("Не отмечен зеленым цветом объект для показа в верхней строке.");
        }
    });

});
