function checking(id) {
    if ($('#' + id).attr('checked')) {
        $('#' + id).removeAttr('checked');

    } else {
        $('#' + id).attr('checked', 'checked');
    }
}

function createNewUser(option) {

    var userRights = [];

    $("input:checkbox[name=right]").each(function () {
        if ($(this).attr('checked')) {
            userRights.push(($(this).val())+":true");
        } else {
            userRights.push(($(this).val())+":false");
        }

    });

    var rights = userRights.join(";");

    // $("input:checkbox[name=right]:checked").each(function () {
    //     userRights.push(($(this).val()));
    // });

    $.ajax({
        url: "/createNewUser",
        method: "GET",
        dataType: 'text',
        cache: false,
        data: {rights: rights},
        success: function (responce) {
                clearForm();
                window.location.reload(false);
        },

        error: function (responce) {
            alert("Error update User " + responce);
        }
    });
}

function backToMainePage() {
    document.location.href = history.back();
   // window.history.back();
}

function checkin(expected, actual) {
    if (actual=="true") {
        $('#' + expected).attr('checked', 'checked').prop("checked", true);
//            document.getElementById(expected).checked = true;
    } else {
        $('#' + expected).removeAttr('checked');
    }
}

function clearForm(){
    $("#saveButtonoID").val("Save");
    $("#selectedUserID").text(0);
    $("#loginID").val("");
    $("#passwordID").val("");
    $("#deleteUserButtonID").attr("style", "display: none");
    $("#saveButtonoID").attr("onclick", "createNewUser('new')");
    $("input:checkbox[name=right]:checked").each(function () {
        $(this).removeAttr('checked');
    });
}
