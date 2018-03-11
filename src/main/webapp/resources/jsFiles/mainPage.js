/**
 * Created by Andrey on 8/15/2017.
 */



var listOfFiles;
var rootFolder = "";

function uploadFirmware(el, option) {

    if (option != "no") {
        var a = document.getElementById('closeW');
        a.click();
    }
    var formData = new FormData();
    var totalFiles = document.getElementById("addFirmwareFile").files.length;
    for (var i = 0; i < totalFiles; i++) {
        var file = document.getElementById("addFirmwareFile").files[i];
        formData.append("file", file);
    }

    cancelUpload();

    startSpinLoader();

    $.ajax({
        // url: '/upload?option='+option,
        url: '/upload?optionParam=' + option + '&rootFolder=' + rootFolder,
        method: 'POST',
        data: formData,
        enctype: 'multipart/form-data',
        dataType: 'json',
        cache: false,
        processData: false,
        contentType: false,
        success: function (data) {

            if (data["name"] == "OK") {
                mySuccessAlert("Upload Finished");
                openFolder(rootFolder);
                //  cancelUpload();

            } else if (data["name"] == "error") {
                var message = "The file/s: ";
                var listFiles = data[1];
                for (var i = 0; i < listFiles.length; i++) {
                    message = message + listFiles[i] + ", ";
                }
                message = message + "can not be uploaded!";
                myWarningAlert(message);

            } else if (data["name"] == "duplicate") {
                var body = document.getElementById('listID');
                var innerTrHtml = '';
                var listFiles = data["files"];
                for (var i = 0; i < listFiles.length; i++) {
                    innerTrHtml += '<li>' + listFiles[i] + '</li>';
                }
                body.innerHTML = innerTrHtml;

                //  $("#modalWindow").attr("style", "display: none");

                chengeActionOfOptionButtons("uploadFirmware(this, 'rename')", "uploadFirmware(this, 'overwrite')", "uploadFirmware(this, 'skip')");

                var a = document.createElement('a');
                document.body.appendChild(a);
                a.style = "display: none";
                a.href = "#win1";
                $(".overlay").attr("id", "win1");

                // var body = document.getElementById('modalWindow');
                // var innerTrHtml = '';
                // innerTrHtml +='<a href="#win1" style="display: none" class="button button-green">Открыть окно 1</a>'+
                // '<a href="#x" class="overlay" id="win1"></a>'+
                //     '<div class="popup" style="background-color:#d9edf7">'+
                //     '<ul id="listID">'+
                //     '</ul>'+
                //
                //     '<input id="RenameID" type="button" value="Rename" onclick="uploadFirmware(this, "rename")">'+
                //     '<input id="OverwriteID" type="button" value="Overwrite" onclick="uploadFirmware(this, "overwrite")">'+
                //     '<input id="SkipID" type="button" value="Skip" onclick="uploadFirmware(this, "skip")">'+
                //     '<a class="close" id="closeW" title="Закрыть" href="#close"></a>'+
                //
                //     '</div>';
                // body.innerHTML = innerTrHtml;

                a.click();
                document.body.removeChild(a);
                setTimeout(function () {
                    $(".overlay").removeAttr("id")
                }, 1000);


            }
            finishSpinLoader();
        },
        error: function (data) {
            var a = data.status;
            if (a == 405 || a == 403) {
                location.reload();
            } else {
                myDangerAlert("UPLOAD Error!!!");
            }

            finishSpinLoader();
        }
    });


}

function cancelUpload() {
    $("#selectedFilesID_li").attr("style", "display: none");
    $("#selectedFilesID").text("");

    $("#uploadFileID").attr("style", "display:none");
    $("#cancelUploadFileID").attr("style", "display:none");
}

function creatingFolder(nameFodler) {


    $.ajax({
        url: "/createFolder",
        method: "POST",
        dataType: 'text',
        cache: false,
        data: {destination: rootFolder, nameFodler: nameFodler},
        success: function (data) {
            if (data == null || data == "") {

                openFolder(rootFolder);
                cancelUpload();
                showTreeFolder();

            } else {
                myWarningAlert(data);
            }
        },
        error: function (data) {
            var a = data.status;
            if (a == 405 || a == 403) {
                location.reload();
            } else {
                myDangerAlert("Creating folder error!!!");
            }
        }
    });
    showTreeFolder();
}


function createImageOrFolderOnThePage(data) {
    var body1 = document.getElementById('idImage');
    var innerTrHtml1 = '';
    var body2 = document.getElementById('idFolder');
    var innerTrHtml2 = '';

    var numberImage = 0;
    var fileInfo = data[0]


    for (var i = 0; i < data.length; i++) {
        fileInfo = data[i];
        var a = fileInfo.localurl;

        // if(a.indexOf(" ")){
        //     a = a.replace(" ", \u0020);
        // }

        var shortName = cutLongName(fileInfo.name);

        if (fileInfo.name.indexOf('enotherSpecialFile') + 1 && fileInfo.image != "folder") {
            fileInfo.name = fileInfo.name.replace('enotherSpecialFile', '');
            innerTrHtml2 += '' +
                '<li><input name="localFileAddress" hidden id="pic-' + i + '"    value="' + fileInfo.localurl + '">' +
                '<label data-title="' + fileInfo.name + '" for="pic-' + i + '" class="slider photo">' +
                '<img class="greyBorder" id="' + a + '" src="data:image/jpg;base64,' + fileInfo.image + '"/>' +
                '<p value="' + a + '" name="filename" ></p>' +
                '<p name="filename" >' + shortName + '</p></label></li>';
        }

        else if (fileInfo.image != "folder") {

            numberImage++;

            innerTrHtml1 += '' +
                '<li><input  name="localFileAddress" hidden id="pic-' + i + '"   value="' + fileInfo.localurl + '">' +

                '<label data-title="' + fileInfo.name + '" for="pic-' + i + '" class="slider photo">' +

                '<img class="greyBorder" id="' + a + '" src="data:image/jpg;base64,' + fileInfo.image + '" onclick="showOriginal(id)" name="' + numberImage + '"/>' +

                '<p name="filename" onclick="openWindowForRename(\'' + fileInfo.name + '\',\'' + fileInfo.localurl + '\')">' + shortName + '</p></label></li>';
        }

        else if (fileInfo.image == "folder") {

            innerTrHtml2 += '' +
                '<li><input name="localFileAddress" hidden id="pic-' + i + '"    value="' + fileInfo.localurl + '">' +
                '<label data-title="' + fileInfo.name + '" for="pic-' + i + '" class="slider photo">' +

                '<p value="' + a + '" name="filename" onclick="openFolderFromContent(\'' + a + '\')" class="folder greyBorder"></p>' +
                '<p name="filename" onclick="openWindowForRename(\'' + fileInfo.name + '\',\'' + fileInfo.localurl + '\')">' + shortName + '</p></label></li>';
        }
    }

    var r = fileInfo.localurl.split("/");
    var a = r.slice(0, r.length - 1);
    rootFolder = a.join("/") + "/";

    body1.innerHTML = innerTrHtml1 + innerTrHtml2;

}

function cutLongName(name) {
    if (name.length > 12) {
        name = name.slice(0, 11) + "...";
    }
    return name;
}

function show2() {
    $.ajax({
        url: "/content",
        method: "get",
        contentType: "image/jpg",
        data: {folderURL: ""},
        cache: false,
        success: function (data) {
            createImageOrFolderOnThePage(data);
            showTreeFolder();
            //        add little spin on the page
            //      example "rebuild process is continued" - and relaunch page every 5 second;
            //       var status = checkSituation();
        },
        error: function (data) {
            myDangerAlert("Error show2!!!");
        }
    })
}

function checkSituation() {
    $.ajax({
        url: "/checkSituation",
        method: "get",
        contentType: "String",
        cache: false,
        success: function (data) {
            return data;
        },
        error: function (data) {
            myDangerAlert("Error checkSituation !!!");
        }
    })
}
var testData;

function showOriginal(fileURL) {

    var res = $("#selecyAllID").attr("style");

    if (res) {
        $("#idFullImage").html('<img onclick="cancelShow()" src="/originalFile?fileURL=\'' + fileURL + '\'" class="showPic" style="height: 80%; width: 80%; object-fit: contain" name="fullSize">' +
            '<span class="left-arrow">' +
            '<input type="button" class="left-arrow" name="' + fileURL + '"  onclick="navigateImage(name, -1)" style="opacity: 0.0">' +
            ' < </span>' +
            '<span class="right-arrow">' +
            '<input type="button" class="right-arrow" name="' + fileURL + '"  onclick="navigateImage(name, 1)" style="opacity: 0.0">' +
            '> </span>' +
            '<span class="close-button">' +
            '<input type="button" class="close-button" name="' + fileURL + '"  onclick="cancelShow()" style="opacity: 0.0">' +
            'X </span>');


        $("#shadeID").attr("class", "shade");
        $("#shadeID").attr('onclick', "cancelShow()");
    }

}
/*
 function fullSize(data, fileURL) {

 $("#idFullImage").html('<img src="data:image/jpg;base64,' + data + '" class="showPic" style="height: 80%; width: 80%; object-fit: contain" name="fullSize"/>' +
 '<input type="button"  class="leftButton" name="' + fileURL + '" value="left" onclick="navigateImage(name, -1)"></i>' +
 '<input type="button" class="rightButton" name="' + fileURL + '" value="rigth" onclick="navigateImage(name, 1)">' +
 '<input type="button" class="cancelButton" name="' + fileURL + '" value="cancel" onclick="cancelShow()">');

 $("#shadeID").attr("class", "shade");

 }
 */
function cancelShow() {
    $("#shadeID").removeAttr("class");
    $("#shadeID").removeAttr("onclick");
    $("#idFullImage").html('');
}


var downloadArchive = function () {

    var listOfFiles = gatheringSelectedFilesFolders();
    var zipName = 'download.zip';
    var a = document.createElement('a');
    document.body.appendChild(a);
    a.id = "archiveID";
    a.style = "display: none";
    a.href = "/download?files=" + listOfFiles;
    a.download = zipName;
    a.click();
    document.body.removeChild(a);

    selectFilesForActions();

}

// it also for hidden or displaying butttons
function selectFilesForActions() {

    // alert($("#selectFileforActionsID").attr("title"));
    // data-original-title
    if ($("#selectFileforActionsID").attr("data-original-title") == "select files") {

        $("#selectFileforActionsID").attr("data-original-title", "cancel");
        $("#selectFileforActionsID").attr("class", "fa fa-close fa-fw fa-lg myHover");

        $("#labelSelectFileID").attr("style", "display:none");

        $("[name=localFileAddress]").each(function () {
            $(this).attr("type", "checkbox");
        });

        $("#selecyAllID").removeAttr("style", "display:none");

        $("input:checkbox[name=localFileAddress]").each(function () {
            $(this).attr("onclick", "displayingUnSelectAll(value)");
        });
        //hidde not nidde

        $("#editeUserID").attr("style", "display:none");
        $("#createUserID").attr("style", "display:none");
        $("#createFodlerID").attr("style", "display:none");
        $("#labelSelectFileID_li").attr("style", "display:none");

    } else {

        $("input:checkbox[name=localFileAddress]:checked").each(function () {
            $(this).click();
            $(this).removeAttr('checked');

        });

        $("input:checkbox[name=localFileAddress]").each(function () {
            $(this).removeAttr("onclick");

        });


        $("[name=localFileAddress]").each(function () {
            $(this).removeAttr("type");
        });


        $("#selectFileforActionsID").attr("data-original-title", "select files");
        $("#selectFileforActionsID").attr("class", "fa fa-hand-o-up fa-fw fa-lg myHover");

        $("#labelSelectFileID").removeAttr("style");

        $("#selecyAllID").attr("style", "display:none");
//show not nidde
        $("#editeUserID").removeAttr("style");
        $("#createUserID").removeAttr("style");
        $("#createFodlerID").removeAttr("style");
        $("#labelSelectFileID_li").removeAttr("style");

        displayingUnSelectAll();
        $("#selectFileforActionsID").removeAttr("style");

    }
}

function navigateImage(id, navigate) {


    var allnumberOfTheElements = jQuery("#idImage").find("img");
    var actualImage = document.getElementById(id);
    var actualName = actualImage.getAttribute("name");
    var nextImage = document.getElementsByName(+actualName + navigate);

    if ((+actualName + navigate) > allnumberOfTheElements.length) {
        nextImage = document.getElementsByName(1);
    } else if ((+actualName + navigate) == 0) {
        nextImage = document.getElementsByName(allnumberOfTheElements.length);
    }


    var idNextImage = nextImage[0].getAttribute("id");
    showOriginal(idNextImage);

}

function openFolder(folderURL) {

    $.ajax({
        url: "/content",
        method: "GET",
        contentType: "image/jpg",
        cache: false,
        data: {folderURL: folderURL},
        success: function (data) {

            var backArr = folderURL.split("/");

            if (!data.length == 0) {
                createImageOrFolderOnThePage(data);
            } else {
                var body1 = document.getElementById('idImage');
                var innerTrHtml1 = '';
                body1.innerHTML = innerTrHtml1;

                //    folderURL = folderURL + "/";
                //     var r = folderURL.split("/");
                //     var a = r.slice(0, r.length-1, "/");
                if (folderURL.slice(-1) == "/") {
                    folderURL = folderURL + "/";
                }
                rootFolder = folderURL;
            }
            navigatingPath(folderURL);

        },
        error: function (data) {
            myDangerAlert("Error open folder!!!");
        }
    });

    var a = $("#cancelID").attr("style");

    if (a) {
        unSelectAll();
        $("#selectFileforActionsID").attr("data-original-title", "select files");
        $("#selectFileforActionsID").attr("class", "fa fa-hand-o-up fa-fw fa-lg myHover");
        $("#labelSelectFileID").removeAttr("style");
        cancelUpload();
        // labelSelectFileID
        $("#selectFileforActionsID").removeAttr("style");
        $("#labelSelectFileID_li").removeAttr("style");
        $("#createFodlerID").removeAttr("style");
        $("#editeUserID").removeAttr("style");
        $("#createUserID").removeAttr("style");
        $("#selecyAllID").attr("style", "display:none");
    }
}


function openFolderFromContent(folderURL) {

    var res = $("#selecyAllID").attr("style");

    if (res == "display:none") {
        openFolder(folderURL);
    }
}

function navigatingPath(urlPath) {
    var arrF;
    var rootURL;

    arrF = urlPath.split("/");

    $("#actualFolderID").empty();
    $("#actualFolderID").append(' <i style="color:#59c2ff" class="fa fa-home fa-fw fa-lg"></i><div class="forSlash"> : </div><a href="#" onclick=openFolder("")>root</a>');

    for (var i = 0; i < arrF.length; i++) {
        if (arrF[i] == "") {
            return;
        }
        var source = urlPath.split(arrF[i])[0];

        var folderR = source + arrF[i] + "/";
        if (arrF[i] != "") {
            $("#actualFolderID").append('<div class="forSlash"> / </div>');
            $("#actualFolderID").append('<a href="#" onclick="openFolder(\'' + folderR + '\')">' + arrF[i] + '</a>');
        }
    }
}

function rescan() {

    bootbox.confirm("Confirm rescan!", function (result) {
        rescaning((result))
    });
}

function rescaning(res) {

    if (res) {

        clearPage();
        spinLoader();
        $.ajax({
            url: "/smartCheck",
            method: "POST",
            cache: false,
            success: function (data) {
                mySuccessAlert("rescan finished");
                spinLoader();
                show2();
            },
            error: function (data) {
                var a = data.status;
                if (a == 405) {
                    location.reload();
                } else {
                    var a = data.status;
                    if (a == 405 || a == 403) {
                        location.reload();
                    } else {
                        myDangerAlert("rescan Error!!!");
                        spinLoader();
                        show2();
                    }
                }
            }
        })
    }
}

function refreshC() {

    bootbox.confirm("Confirm rebuilt preview!", function (result) {
        refreshing((result))
    });
}

function refreshing(res) {
    if (res) {
        clearPage();
        spinLoader();
        $.ajax({
            url: "/recreatePreviews",
            method: "POST",
            cache: false,
            success: function (data) {
                mySuccessAlert("refresh finished");
                spinLoader();
                show2();
            },
            error: function (data) {
                var a = data.status;
                if (a == 405 || a == 403) {
                    location.reload();
                } else {
                    myDangerAlert("Rebuild Error!!!");
                    spinLoader();
                    show2();
                }
            }
        })
    }
}

function clearPage() {
    // clearing frame
    var body1;
    var innerTrHtml1;

    body1 = document.getElementById('idFullImage');
    innerTrHtml1 = '';
    innerTrHtml1 += '';
    body1.innerHTML = innerTrHtml1;
    body1 = document.getElementById('idImage');
    innerTrHtml1 = '';
    innerTrHtml1 += '';
    body1.innerHTML = innerTrHtml1;
    body1 = document.getElementById('treefolderid');
    innerTrHtml1 = '';
    innerTrHtml1 += '';
    body1.innerHTML = innerTrHtml1;

}

function startSpinLoader() {
    clearPage();
    spinLoader();
    $("#shadeID").attr("onclick", "finishSpinLoader()");
}

function finishSpinLoader() {
    var result = $("#shadeID").attr("onclick");
    if (result == "finishSpinLoader()") {
        spinLoader();
        openFolder(rootFolder);
        showTreeFolder();
        $("#shadeID").removeAttr("onclick");
    }
}

function spinLoader() {
    var info = $("#idImage").attr("class");

    if (info == "loader") {
        $("#shadeID").removeAttr("class");
        $("#idImage").attr("class", "photos");
    }

    if (info == "photos") {
        $("#idImage").attr("class", "loader");
        $("#shadeID").attr("class", "shade");
    }
}

function deleteFileFolder() {

    listOfFiles = gatheringSelectedFilesFolders();
    var filesURL = listOfFiles.split(";");
    var filename = "";
    for (var i = 0; i < filesURL.length; i++) {
        var fileURLarray = filesURL[i].split("/");
        filename = filename + " - " + fileURLarray[fileURLarray.length - 1] + "\n";
    }
    // var result = confirm("The file(s) will be deleted: \n" + filename);

    bootbox.confirm("The file(s) will be deleted: \n" + filename, function (result) {
        deleting((result))
    });
    //
}

function deleting(res) {

    if (res) {
        $.ajax({
            url: "/deleteFilesFolders",
            method: "GET",
            contentType: "text",
            cache: false,
            data: {files: listOfFiles},
            success: function (data) {
                selectFilesForActions();

                openFolder(rootFolder);
                showTreeFolder();
            },
            error: function (data) {
                var a = data.status;
                if (a == 405 || a == 403) {
                    location.reload();
                } else {
                    myDangerAlert("delete Error!!!");
                }
            }
        });

        showTreeFolder();
    }
}


function gatheringSelectedFilesFolders() {
    var el = $("input[name=localFileAddress]:checkbox:checked");
    var listOfFiles = [];
    for (var i = 0; i < el.length; i++) {

        listOfFiles.push(el[i].value);

    }
    listOfFiles = listOfFiles.join(';');
    return listOfFiles;
}

function displayingSelectedFileBorder() {
    // $("img[class*=else]");

    $("input[name=localFileAddress]:checkbox:checked").each(function () {
        var imgId = $(this).val();
        document.getElementById(imgId).setAttribute("class", "lihgtBorder");
    });
}

function hightliteFileOrNo(file) {
    // $("img[class*=else]");
    var actualClass = document.getElementById(file).getAttribute("class");

    //for folder
    if (actualClass == null) {
        //var name = file.split("/");
        //var n = name[name.length-1];
        actualClass = $("p[value='" + file + "']").attr("class");

        if (actualClass.indexOf("greyBorder") > -1) {
            actualClass = actualClass.replace("greyBorder", "lihgtBorder");
            $("p[value='" + file + "']").attr("class", actualClass);
        } else if (actualClass.indexOf("lihgtBorder") > -1) {
            actualClass = actualClass.replace("lihgtBorder", "greyBorder");
            $("p[value='" + file + "']").attr("class", actualClass);
        }
        return;
    }

    //for image
    if (actualClass.indexOf("greyBorder") > -1) {
        actualClass = actualClass.replace("greyBorder", "lihgtBorder");
        document.getElementById(file).setAttribute("class", actualClass);
    } else if (actualClass.indexOf("lihgtBorder") > -1) {
        actualClass = actualClass.replace("lihgtBorder", "greyBorder");
        document.getElementById(file).setAttribute("class", actualClass);
    }


    //var actualFolderClass =  $("p[value='"+file+"']").attr("class");

}

function displayingUnSelectedFileBorder() {
    $("input[name=localFileAddress]:checkbox:checked").each(function () {
        var imgId = $(this).val();
        document.getElementById(imgId).setAttribute("class", "lihgtBorder");
    });
}

/*
 <sec:authorize access="hasRole('rename')">
 function openWindowForRename(oldName, localurl) {
 var newName = checkNewName(oldName);
 if (newName == false) {
 return;
 }

 $.ajax({
 url: "/rename",
 method: "GET",
 cache: false,
 dataType: 'text',
 data: {
 newName: newName,
 localurl: localurl
 },
 success: function (data) {
 show2();
 },
 error: function (data) {
 alert("rename Error!!!");
 }
 })
 }
 </sec:authorize>
 */

// function openWindowForRename(oldName, localurl) {
//     // only for file, not for folder
//     var onlyName;
//     var onlyNameArr = new Array;
//     if (oldName.indexOf('.') > -1) {
//         onlyNameArr = oldName.split('.');
//         onlyName = onlyNameArr[0];
//         oldName = onlyName;
//     }
//
//    // var newName = prompt("rename", oldName);
//     bootbox.prompt({
//         title: "Enter new name!",
//
//         callback: function (result) {
//
//              if(result!=null) {
//                 var newName = checkingNewName(oldName, result, onlyNameArr);
//                 if (newName != false) {
//                     rename(oldName, localurl, newName);
//                 }
//              }
//         }
//     });
// }

function checkingNewName(oldName, newName, onlyNameArr) {


    if (newName == null || newName == oldName || newName == oldName + '.' + onlyNameArr[1] || newName == "") {
        return false;
    }

    var filenames = document.getElementsByName('filename');
    for (var i = 0; i < filenames.length; i++) {
        if (filenames[i].innerText == newName) {
            myWarningAlert("file or folder with such name already exist");
            return false;
        }
    }
    return newName;
}

function createFolder() {
    // only for file, not for folder

    var onlyNameArr = new Array;

    bootbox.prompt({
        title: "Enter name!",

        callback: function (result) {
            if (result != null) {
                var newName = checkingFolderName(result);
                if (newName != false) {
                    creatingFolder(newName);
                }
            }
        }
    });
}
function checkingFolderName(newName) {
    if (newName == null || newName == "") {
        return false;
    }

    var filenames = document.getElementsByName('filename');
    for (var i = 0; i < filenames.length; i++) {
        if (filenames[i].innerText == newName) {
            myWarningAlert("file or folder with such name already exist");
            return false;
        }
    }
    return newName;
}


function sumFilesForCopy(typeC, typeM) {

    listOfFiles = gatheringSelectedFilesFolders();
//         document.getElementById('copyID').setAttribute("value", "copyHere");
//         document.getElementById('copyID').setAttribute("onclick", "copy('no')");
//
//         document.getElementById('cancelID').removeAttribute("style", "display: none");
//         document.getElementById('moveID').setAttribute("style", "display: none");

    // 'move', 'copy'
    // document.getElementById(typeC + 'ID').text(typeC + " Here");

    $("#selecyAllID").attr("style", "display:none");
    $('#copyMovePropositionID').text(typeC + " here");
    $("#copyMovePropositionID_li").removeAttr("style");
    //$('#' + typeC + 'ID').text(typeC + " here");

    document.getElementById(typeC + 'ID').setAttribute("onclick", "copy('no','" + typeC + "')");
    $('#' + typeC + 'ID').attr("class", "fa fa-chevron-down fa-fw fa-lg myHover");

    document.getElementById('cancelID').removeAttribute("style", "display: none");
    document.getElementById(typeM + 'ID_li').setAttribute("style", "display: none");

    $("#unselecyAllID").attr("style", "display: none");
    $("#downloadID").attr("style", "display: none");
    $("#deletefileFolderID").attr("style", "display: none");

    $("#selectFileforActionsID").attr("style", "display: none");

    $("input:checkbox[name=localFileAddress]").each(function () {
        $(this).removeAttr("onclick");
    });

    //  как вареант
    // $("[name=localFileAddress]").each(function () {
    //     $(this).removeAttr("type");
    //});

}

function sumFilesForMove() {

    listOfFiles = gatheringSelectedFilesFolders();
    document.getElementById('moveID').setAttribute("value", "MoveHere");
    document.getElementById('moveID').setAttribute("onclick", "move('no')");

    document.getElementById('cancelID').removeAttribute("style", "display: none");
    document.getElementById('copyID_li').setAttribute("style", "display: none");
}

function cancelAct() {

    // $('#copyID').text("Copy");
    //   $('#moveID').text("Move");

    document.getElementById('copyID').setAttribute("value", "copy");
    document.getElementById('moveID').setAttribute("value", "move");

    document.getElementById('copyID').setAttribute("onclick", "sumFilesForCopy('copy', 'move')");
    document.getElementById('moveID').setAttribute("onclick", "sumFilesForCopy('move', 'copy')");

    $("#copyID").attr("class", "fa fa-copy fa-fw fa-lg myHover");
    $("#moveID").attr("class", "fa  fa-share-square-o fa-fw fa-lg myHover");

    $("#copyMovePropositionID").text("");
    $("#copyMovePropositionID_li").attr("style", "display: none");

    document.getElementById('moveID_li').removeAttribute("style", "display: none");
    document.getElementById('copyID_li').removeAttribute("style", "display: none");
    document.getElementById('cancelID').setAttribute("style", "display: none");
    //listOfFiles = null;

    $("#selecyAllID").removeAttr("style");

    $("#unselecyAllID").removeAttr("style");
    $("#downloadID").removeAttr("style");
    $("#deletefileFolderID").removeAttr("style");

    $("#selectFileforActionsID").removeAttr("style");


    $("input:checkbox[name=localFileAddress]").each(function () {
        $(this).attr("onclick", "displayingUnSelectAll(value)");
    });

    var i = new Array;
    i = $("input:checkbox[name=localFileAddress]");
    if (i.length == 0) {
        $("#labelSelectFileID_li").removeAttr("style");
        $("#selectFileforActionsID").attr("data-original-title", "select files");
        $("#selectFileforActionsID").attr("class", "fa fa-hand-o-up fa-fw fa-lg myHover");
        $("#selectFileforActionsID").removeAttr("style");

        $("#createFodlerID").removeAttr("style");
        $("#editeUserID").removeAttr("style");
        $("#createUserID").removeAttr("style");
        $("#selecyAllID").attr("style", "display:none");


        $("#selectedFilesID_li").attr("style", "display: none");
        $("#selectedFilesID").text("");

        $("#unselecyAllID").attr("style", "display: none");

        $("#downloadID").attr("style", "display:none");

        $("#deletefileFolderID").attr("style", "display:none");
        $("#copyID_li").attr("style", "display:none");
        $("#moveID_li").attr("style", "display:none");

    }

}

function copy(option, type) {

    if (option != "no") {
        var a = document.getElementById('closeW');
        a.click();
    }

    if (type == "move") {
        if (checkWay(listOfFiles, rootFolder)) {
            myWarningAlert("You cannot move folder to itself");
            return;
        }
    }
    $.ajax({
        // url: '/upload?option='+option,
        url: "/copy",
        method: "GET",
        cache: false,
        dataType: 'json',
        data: {
            listOfFiles: listOfFiles,
            destURL: rootFolder,
            optionParam: option,
            typeAction: type
        },
        success: function (data) {

            if (data["name"] == "OK") {
                mySuccessAlert(type + " Finished");

                openFolder(rootFolder);
                showTreeFolder();
                /*
                 cancelUpload();
                 cancelAct();
                 selectFilesForActions();
                 */

            } else if (data["name"] == "error") {
                var message = "The file/s: ";
                var listFiles = data[1];
                for (var i = 0; i < listFiles.length; i++) {
                    message = message + listFiles[i] + ", ";
                }
                message = message + "can not be copy or move!";
                myWarningAlert(message);
            } else {
                var body = document.getElementById('listID');
                var innerTrHtml = '';
                var data = data["files"];
                for (var i = 0; i < data.length; i++) {
                    innerTrHtml += '<li>' + data[i] + '</li>';
                }
                chengeActionOfOptionButtons("copy('rename','" + type + "')", "copy('overwrite', '" + type + "')", "copy('skip', '" + type + "')");

                body.innerHTML = innerTrHtml;
                var a = document.createElement('a');
                document.body.appendChild(a);
                a.style = "display: none";
                a.href = "#win1";
                $(".overlay").attr("id", "win1");
                a.click();
                document.body.removeChild(a);
                setTimeout(function () {
                    $(".overlay").removeAttr("id")
                }, 1000);

            }

        },
        error: function (data) {
            var a = data.status;
            if (a == 405 || a == 403) {
                location.reload();
            } else {
                myDangerAlert("Copy Error!!!");
            }

        }
    });
    // cancelAct();
    //selectFilesForActions();

    cancelAct();

    $("input:checkbox[name=localFileAddress]").each(function () {
        $(this).removeAttr("onclick");

    });
    $("input:checkbox[name=localFileAddress]:checked").each(function () {
        $(this).removeAttr('checked');
        $(this).click();
    });

    $("[name=localFileAddress]").each(function () {
        $(this).removeAttr("type");
    });

    $("#selectFileforActionsID").attr("data-original-title", "select files");
    $("#selectFileforActionsID").attr("class", "fa fa-hand-o-up fa-fw fa-lg myHover");


    $("#selecyAllID").attr("style", "display:none");
//show not nidde
    $("#editeUserID").removeAttr("style");
    $("#createUserID").removeAttr("style");
    $("#createFodlerID").removeAttr("style");
    $("#labelSelectFileID_li").removeAttr("style");

    displayingUnSelectAll();
    $("#selectFileforActionsID").removeAttr("style");


    openFolder(rootFolder);
    showTreeFolder();
    /*
     $("#selectFileforActionsID").text("Select files");
     $("#selectFileforActionsID").removeAttr("style");
     $("#labelSelectFileID_li").removeAttr("style");
     $("#createFodlerID").removeAttr("style");
     $("#editeUserID").removeAttr("style");
     $("#createUserID").removeAttr("style");
     $("#selecyAllID").attr("style", "display:none");
     */
    //
    // $('#moveID_li').attr("style", "display: none");
    // $('#copyID_li').attr("style", "display: none");
    //
    // $("#unselecyAllID").attr("style");
    // $("#downloadID").attr("style");
    // $("#deletefileFolderID").attr("style");

}

function checkWay(fileList, destination) {

    fileList = fileList.split(";");
    for (var i = 0; i < fileList.length; i++) {

        var fileName = fileList[i];

        if (destination.indexOf(fileName) > -1) {
            return true;
        }
    }
    return false;
}

function chengeActionOfOptionButtons(rename, overwrite, skip) {
    document.getElementById('RenameID').setAttribute("onclick", rename);
    document.getElementById('OverwriteID').setAttribute("onclick", overwrite);
    document.getElementById('SkipID').setAttribute("onclick", skip);
}
/*
 function move(option) {
 if (option != "no") {
 var a = document.getElementById('closeW');
 a.click();
 }
 $.ajax({
 // url: '/upload?option='+option,
 url: "/move",
 method: "GET",
 cache: false,
 dataType: 'json',
 data: {
 listOfFiles: listOfFiles,
 destURL: rootFolder,
 option: option
 },
 success: function (data) {

 if (data.length == 0) {
 alert("move Finished");


 openFolder(rootFolder);
 cancelUpload();

 return;
 }
 var body = document.getElementById('listID');
 var innerTrHtml = '';

 for (var i = 0; i < data.length; i++) {
 innerTrHtml += '<li>' + data[i] + '</li>';
 }
 chengeActionOfOptionButtons("copy('rename')", "copy('overwrite')", "copy('skip')");
 body.innerHTML = innerTrHtml;
 var a = document.createElement('a');
 document.body.appendChild(a);
 a.style = "display: none";
 a.href = "#win1";
 a.click();
 document.body.removeChild(a);
 },
 error: function (data) {
 alert("UPLOAD Error!!!");

 }
 })

 }
 */
function chengeActionOfOptionButtons(rename, overwrite, skip) {
    document.getElementById('RenameID').setAttribute("onclick", rename);
    document.getElementById('OverwriteID').setAttribute("onclick", overwrite);
    document.getElementById('SkipID').setAttribute("onclick", skip);
}

function showTreeFolder() {

    //  var localURL = "H:/workspace/SharedLocal/";

    $.ajax({
        url: "/folders",
        method: "GET",
        dataType: 'json',
        cache: false,
        success: function (responce) {

            var folderTree = sortAndParce(responce);
            var openedFolderList = new Array();
            openedFolderList = getAllOpenedFoldersFromTreeFolder();
            var body = document.getElementById('treefolderid');
            var innerTrHtml = '';
            innerTrHtml += bealdTreeFolder(folderTree, innerTrHtml, "group-", "", openedFolderList);
            body.innerHTML = innerTrHtml;
        },

        error: function (responce) {
            myDangerAlert("Error folderTree " + responce);
        }
    });
}

function getAllOpenedFoldersFromTreeFolder() {
    var openedFolderList = new Array();
    var idSelected = new Array();
    $("ul#treefolderid input:checkbox:checked").each(function () {
        idSelected.push($(this).attr('id'));
    });


    for (var i = 0; i < idSelected.length; i++) {
        var ids = idSelected[i];
        openedFolderList.push($('label[for=' + ids + ']').attr('id'));
    }

    return openedFolderList;
}

function sortAndParce(responce) {

    var folderTree = {};

    for (var i = 0; i < responce.length; i++) {
// devide address for know deeping
        var a = new Array();
        var str = responce[i];
        a = str.toString().split('/');


        // if no deeping and
        if (a.length == 1 && typeof(folderTree[a[0]]) !== "object") {
            folderTree[a[0]] = a[0];
        } else if (a.length > 1 && typeof(folderTree[a[0]]) !== "object") {
            var arr = new Array;
            var arr2 = new Array;
            arr2 = responce.slice(i, responce.length);
            for (var ii = 0; ii < arr2.length; ii++) {

                var b = arr2[ii].split(a[0] + '/');
                var bb = arr2[ii].replace(a[0] + '/', "");

                if (b.length > 1) {
                    arr.push(bb);
                }

            }
            folderTree[a[0]] = sortAndParce(arr);
        }
    }
    return folderTree;
}

function connectingFolderNavigation(url) {

    openFolder(url);
    $('label, #url').click();
}

var calc = 0;
function bealdTreeFolder(treefolder, innerTrHtml, group, localUrl, openedFolderList) {

    for (var key in treefolder) {
        calc++;

        if (typeof(treefolder[key]) != "object") {
            var r = localUrl + treefolder[key];
            innerTrHtml += '<li class="has-children">';
            if (openedFolderList != undefined && openedFolderList.length > 0 && (openedFolderList.indexOf(r) > -1)) {
                innerTrHtml += '<input  type="checkbox" checked="true" name ="' + group + calc + '" id="' + group + calc + '">';
            } else {
                innerTrHtml += '<input  type="checkbox" name ="' + group + calc + '" id="' + group + calc + '">';
            }
            innerTrHtml += '<label id="' + r + '" onclick="openFolder(id)" for="' + group + calc + '">' + treefolder[key] + '</label></li>';
        } else {
            innerTrHtml += '<li class="has-children">';
            if (openedFolderList != undefined && openedFolderList.length > 0 && (openedFolderList.indexOf(localUrl + key) > -1)) {
                innerTrHtml += '<input type="checkbox" checked="true" name ="' + group + calc + '" id="' + group + calc + '">';
            } else {
                innerTrHtml += '<input type="checkbox" name ="' + group + calc + '" id="' + group + calc + '">';
            }
            innerTrHtml += '<label id="' + localUrl + key + '" onclick="openFolder(id)" for="' + group + calc + '">' + key + '</label><ul>';
            var innerTrHtml2 = '';
            innerTrHtml += bealdTreeFolder(treefolder[key], innerTrHtml2, 'sub-group-', localUrl + key + '/', openedFolderList);
            innerTrHtml += '</ul></li>';
        }
    }

    return innerTrHtml;
}

function unSelectAll() {
    $("input:checkbox[name=localFileAddress]:checked").each(function () {
        $(this).click();
    });

    $("[class*=lihgtBorder]").each(function () {
        var cl = $(this).attr("class");
        cl = cl.replace("lihgtBorder", "greyBorder");
        $(this).attr("class", cl);
    });

    $("#unselecyAllID").attr("style", "display: none");
    $("#selectedFilesID_li").attr("style", "display: none");
    $("#selectedFilesID").text("");

}

function SelectAll() {
    $("#selectedFilesID_li").removeAttr("style");
    var i = 0;
    $("input:checkbox[name=localFileAddress]:not(:checked)").each(function () {
        $(this).click();

        i++;
    });

    i = $("input:checkbox[name=localFileAddress]:checked").length;

    $("#selectedFilesID").text(i + " file(s)/folder(s) selected");
    $("#unselecyAllID").removeAttr("style");

}

function displayingUnSelectAll(file) {

    if (file != null) {
        hightliteFileOrNo(file);
    }
    //displayingSelectedFileBorder();
    var selected = $("input:checkbox[name=localFileAddress]:checked");
    if (selected.length > 0) {
        if ($("#unselecyAllID").attr("style")) {

            $("#unselecyAllID").removeAttr("style");
        }


        $("#selectedFilesID_li").removeAttr("style");
        $("#selectedFilesID").text(selected.length + " file(s)/folder(s) selected");

        $("#downloadID").removeAttr("style", "display:none");

        $("#deletefileFolderID").removeAttr("style", "display:none");
        $("#copyID_li").removeAttr("style", "display:none");
        $("#moveID_li").removeAttr("style", "display:none");
    } else {


        $("#selectedFilesID_li").attr("style", "display: none");
        $("#selectedFilesID").text("");

        $("#unselecyAllID").attr("style", "display: none");

        $("#downloadID").attr("style", "display:none");

        $("#deletefileFolderID").attr("style", "display:none");
        $("#copyID_li").attr("style", "display:none");
        $("#moveID_li").attr("style", "display:none");

    }
}


function showHideTreeFolderMenu() {
    var state = $("#treefolderid").attr("style");
    if (state) {
        $("#treefolderid").removeAttr("style");
        $("#content").removeAttr("style");
        $("#sidebar").removeAttr("style");
        $("#showHideTreesfolders").attr("style", "margin:10px 0 0 200px");
        $("#showHideTreesfolders").attr("class", "fa fa-arrow-left fa-lg myHover");
    } else {
        $("#sidebar").attr("style", "width: 30px");
        $("#treefolderid").attr("style", "display:none");
        $("#content").attr("style", "margin:15px; padding-right:20px");
        $("#showHideTreesfolders").attr("style", "margin:10px 0 0 0");
        $("#showHideTreesfolders").attr("class", "fa fa-arrow-right fa-lg myHover");

    }

}
function mySuccessAlert(message) {

    $(".container").html('<div class="alert alert-success alert-dismissable myContainer">' +
        '<a href="#" class="close" data-dismiss="alert" aria-label="close">×</a>' +
        '<strong>Success!</strong> ' + message + '</div>');

}

function myInfoAlert(message) {
    $(".container").html('<div class="alert alert-info alert-dismissable myContainer">' +
        '<a href="#" class="close" data-dismiss="alert" aria-label="close">×</a>' +
        '<strong>Info!</strong> ' + message + '</div>');
}


function myWarningAlert(message) {

    $(".container").html('<div class="alert alert-warning alert-dismissable myContainer">' +
        '<a href="#" class="close" data-dismiss="alert" aria-label="close">×</a>' +
        '<strong>Warning!</strong> ' + message + '</div>');

}

function myDangerAlert(message) {
    $(".container").html('<div class="alert alert-danger alert-dismissable myContainer">' +
        '<a href="#" class="close" data-dismiss="alert" aria-label="close">×</a>' +
        '<strong>Warning!</strong> ' + message + '</div>');


}
