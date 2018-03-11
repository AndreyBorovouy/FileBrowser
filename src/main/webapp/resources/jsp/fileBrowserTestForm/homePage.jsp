<%@page pageEncoding="UTF-8"%>
<%request.setCharacterEncoding("UTF-8");%>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<html xmlns="http://www.w3.org/1999/xhtml">

<link rel="stylesheet" href="/resources/css/myStyle.css">
<link rel="stylesheet" href="/resources/css/forAppearWin.css">


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<script src='//js.zapjs.com/js/download.js'></script>

<script src='/resources/jsFiles/Stuk-jszip-v3.1.3-3-gab3829a/Stuk-jszip-ab3829a/dist/jszip.js'></script>


<script src="/resources/jsFiles/FileSaver.js"></script>
<script src="/resources/jsFiles/mainPage.js"></script>


<head>
    <!--   <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    -->
    <link rel="stylesheet" type='text/css'  href="/resources/bootstrap-3.3.7-dist/bootstrap-3.3.7-dist/css/bootstrap.min.css">



    <script type="text/javascript" src="/resources/jsFiles/bootbox.min.js"></script>

    <link href='/resources/css/css_for_glif/font-awesome-4.7.0/css/font-awesome.css' rel='stylesheet' type='text/css'>
    <link href='/resources/css/scrollcss.css' rel='stylesheet' type='text/css'>


    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width; initial-scale=1;">
    <link href='https://fonts.googleapis.com/css?family=Jaldi:400,700' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="/resources/photocloud8/css/reset.css">
    <link rel="stylesheet" href="/resources/photocloud8/css/all.css" type="text/css"/>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">

    <script src="/resources/photocloud8/js/modernizr.js"></script> <!-- Modernizr -->
    <script type="text/javascript" src="/resources/photocloud8/js/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="/resources/photocloud8/js/main.js"></script>
    <title>PhotoCloud</title>
</head>

<body>

<div id="shadeID"></div>

<%--user login and Logaut button--%>
<c:url value="/j_spring_security_logout" var="logoutUrl"/>
<form action="${logoutUrl}" method="post" id="logoutForm">
    <input type="hidden" name="${_csrf.parameterName}"
           value="${_csrf.token}"/>
</form>

<script>
    function formSubmit() {
        document.getElementById("logoutForm").submit();
    }
</script>

<%--<c:if test="${pageContext.request.userPrincipal.name != null}">--%>
<%--<h2 style="margin-left: 90%; position: absolute">--%>
<%--<a href="javascript:formSubmit()">Logout</a>--%>
<%--</h2>--%>
<%--</c:if>--%>

<div id="modalWindow">
    <a href="#win1" style="display: none" class="button button-green">Открыть окно 1</a>

    <!-- Модальное окно №1 -->
    <a href="#x" class="overlay" id=""></a>

    <div class="popup" style="background-color:#d9edf7;height:auto">

        <ul id="listID">
        </ul>

        <div class="fileEd">
            <input id="RenameID" class="btn btn-info btn-xs" type="button" value="Rename" onclick="uploadFirmware(this, 'rename')">
            <input id="OverwriteID" class="btn btn-info btn-xs" type="button" value="Overwrite" onclick="uploadFirmware(this, 'overwrite')">
            <input id="SkipID" class="btn btn-info btn-xs" type="button" value="Skip >>"
                   onclick="uploadFirmware(this, 'skip')">
        </div>
        <a class="close" id="closeW" title="Закрыть" href="#close"></a>


    </div>
</div>

<%--<div class="container myContainer">--%>

<%--</div>--%>

<div class="wrapper">
    <div id="header">
        <div id="layout">
            <h1 class="logo">PhotoCloud</h1>

            <div id="header-menu">

                <div class="blockButton">
                    <div id="nav">
                        <ul>
                            <li>
                                <i data-toggle="tooltip" data-placement="bottom" title="refresh" class="fa fa-refresh fa-fw fa-lg myHover" type="button" onclick="show2()" value="refresh"></i>
                            </li>

                            <sec:authorize access="hasRole('fullCheck')">
                                <li>
                                    <i data-toggle="tooltip" data-placement="bottom" title="rebuilt preview" data-title="Rebuilt preview" class="fa fa-rotate-left fa-fw fa-lg myHover" type="button" value="RebuildContent" onclick="refreshC()" style="color: red"></i>
                                </li>
                            </sec:authorize>

                            <sec:authorize access="hasRole('smartCheck')">
                                <li>
                                    <i data-toggle="tooltip" data-placement="bottom" title="rescan preview" class="fa fa-eye fa-fw fa-lg myHover" data-title="Rescan preview" type="button" value="SCAN" onclick="rescan()"></i>
                                </li>
                            </sec:authorize>

                            <sec:authorize access="hasAnyRole('download', 'delete', 'copy', 'move')">
                                <li id="selecyAllID" style="display:none">
                                    <i data-toggle="tooltip" data-placement="bottom" title="select all" class="fa fa-check-square-o fa-fw fa-lg myHover" data-title="Select all" type="button" value="Select All" onclick="SelectAll()"></i>
                                </li>
                                <li>
                                    <i data-toggle="tooltip" data-placement="bottom" title="select files" class="fa fa-hand-o-up fa-fw fa-lg myHover" data-title="Select files" id="selectFileforActionsID" onclick="selectFilesForActions()"></i>
                                </li>
                            </sec:authorize>
                            <li id="selectedFilesID_li" style="display: none"><p id="selectedFilesID"></p></li>
                            <sec:authorize access="hasAnyRole('download', 'delete', 'copy', 'move')">
                                <li id="unselecyAllID" style="display: none">
                                    <i data-toggle="tooltip" data-placement="bottom" title="deselect all" class="fa fa-square-o fa-fw fa-lg myHover" data-title="Unselect all" type="button" value="un select All" onclick="unSelectAll()"></i>
                                </li>
                            </sec:authorize>

                            <sec:authorize access="hasRole('upload')">
                                <li style="display:none">
                                    <input id="addFirmwareFile" type="file" class="form-control" name="file" multiple accept="image/*" onchange=handleFiles(this.files)>
                                </li>

                                <i data-toggle="tooltip" data-placement="bottom" title="Select files for upload" class="fa fa-upload fa-fw fa-lg myHover" data-title="Select files for upload"  id="labelSelectFileID" onclick="selectFilesForUpload()"></i>

                                <li id="uploadFileID" style="display:none">
                                    <i data-toggle="tooltip" data-placement="bottom" title="upload file(s)" class="fa fa-chevron-down fa-fw fa-lg myHover" data-title="Upload file(s)"
                                       onclick="uploadFirmware(this, 'no')"></i>
                                </li>
                                <li data-toggle="tooltip" data-placement="bottom" title="cancel upload" id="cancelUploadFileID" style="display:none; margin-top: 30px"><i class="fa fa-close fa-fw fa-lg myHover" data-title="Cancel upload" onclick="cancelUpload()"></i>
                                </li>
                            </sec:authorize>

                            <sec:authorize access="hasRole('download')">
                                <li id="downloadID" style="display:none">
                                    <i data-toggle="tooltip" data-placement="bottom" title="download"  class="fa fa-download fa-fw fa-lg myHover" data-title="Download" onclick="downloadArchive()"></i>
                                </li>
                            </sec:authorize>

                            <sec:authorize access="hasRole('createFolder')">
                                <li id="createFodlerID">
                                    <i data-toggle="tooltip" data-placement="bottom" title="create folder" class="fa material-icons fa-fw fa-lg myHover" style="vertical-align: -6px" data-title="Create folder" onclick="createFolder()">create_new_folder</i>
                                </li>
                            </sec:authorize>
                            <li id="copyMovePropositionID_li" style="display: none">
                                <p id="copyMovePropositionID"></p>
                            </li>
                            <sec:authorize access="hasRole('copy')">
                                <li id="copyID_li" style="display:none">
                                    <i data-toggle="tooltip" data-placement="bottom" title="copy" class="fa fa-copy fa-fw fa-lg myHover" data-title="Copy" type="button" id="copyID" value="copy" onclick="sumFilesForCopy('copy', 'move')"></i>
                                </li>
                            </sec:authorize>

                            <sec:authorize access="hasRole('move')">
                                <li style="display:none" id="moveID_li">
                                    <i data-toggle="tooltip" data-placement="bottom" title="move" class="fa  fa-share-square-o fa-fw fa-lg myHover" data-title="Move" type="button" id="moveID" value="move"
                                       onclick="sumFilesForCopy('move', 'copy')"></i>
                                </li>
                            </sec:authorize>

                            <sec:authorize access="hasAnyRole('move', 'copy')">
                                <li style="display: none" id="cancelID">
                                    <i data-toggle="tooltip" data-placement="bottom" title="cancel this action" class="fa fa-close fa-fw fa-lg myHover" data-title="Cancel this action" type="button" value="cancel" onclick="cancelAct()"></i>
                                </li>
                            </sec:authorize>

                            <sec:authorize access="hasRole('editUser')">
                                <li id="editeUserID">
                                    <form method="get" action="/editUsers">
                                        <label for="navigate_to_users_page_id" data-toggle="tooltip" data-placement="bottom" title="edit user" class="fa fa-address-card-o fa-fw fa-lg myHover" data-title="Edit user"> <button id="navigate_to_users_page_id" style="display: none" type="submit"></button>
                                        </label>
                                    </form>
                                </li>

                            </sec:authorize>


                            <sec:authorize access="hasRole('delete')">
                                <li style="display:none" id="deletefileFolderID">
                                    <i label for="navigate_to_users_page_id" data-toggle="tooltip" data-placement="bottom" title="delete" style="color: red" data-title="Delete"
                                       class="fa fa-trash fa-fw fa-lg myHover" onclick="deleteFileFolder()"></i>
                                </li>
                            </sec:authorize>


                            <li>
                                <c:if test="${pageContext.request.userPrincipal.name != null}"><c:if test="${pageContext.request.userPrincipal.name != ''}">
                                    <c:if test="${pageContext.request.userPrincipal.name.toString() != 'guest'}">
                                                                           <h2 style="right: 10%; position: fixed">

                                        <a href="javascript:formSubmit()">Logout</a>

                                    </h2>
                                </c:if>
                                </c:if>
                                </c:if>
                                <%--<c:if test="${pageContext.request.userPrincipal.name == null}">--%>
                                    <c:if test="${pageContext.request.userPrincipal.name.toString() == 'guest'}">
                                        <h2 style="right: 10%; position: fixed">

                                            <a href="/resources/jsp/fileBrowserTestForm/userLogin.jsp">Login</a>
                                        </h2>
                                    </c:if>
                                <%--<c:if test="${pageContext.request.userPrincipal.name == null}">--%>
                                    <%--<h2 style="right: 10%; position: fixed">--%>
                                        <%--<a href="/resources/jsp/fileBrowserTestForm/userLogin.jsp">Login</a>--%>
                                    <%--</h2>--%>
                                <%--</c:if>--%>
                                <%--</c:if>--%>
                            </li>

                        </ul>
                    </div>
                    <div id="actualFolderID">
                        <i style="color:#59c2ff" class="fa fa-home fa-fw fa-lg"></i>
                        <div class="forSlash"> :</div>
                        <a href="#" onclick=openFolder("")>root</a>
                    </div>
                </div>
            </div>
        </div>
    </div>


    <div id="sidebar">
        <div id="showHideTreesfolders" class="fa fa-arrow-left fa-lg myHover"
             style="margin:10px 0 0 200px; position: absolute" onclick="showHideTreeFolderMenu()">
        </div>

        <ul id="treefolderid" class="cd-accordion-menu animated">

        </ul> <!-- cd-accordion-menu -->
    </div>


    <%--SPIN--%>
    <div id="info">
    </div>

    <div id="idFullImage">
    </div>


    <div id="content">
        <%--Rename--%>


        <div class="bs-example">
            <!-- Button HTML (to Trigger Modal) -->
            <div id="buttonRenameIDdiv">

            </div>


            <div class="container">

            </div>

            <ul class="photos" id="idImage">

            </ul>

        </div>
    </div>

    <script type="text/javascript" src="/resources/bootstrap-3.3.7-dist/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>

    <script type="text/javascript" src="/resources/jsFiles/bootbox.min.js"></script>

</body>



<script>
    window.onload = show2;

    function selectFilesForUpload() {
        $("#addFirmwareFile").click();
    }

    function handleFiles(arr) {

        $("#selectedFilesID_li").removeAttr("style");
        $("#selectedFilesID").text(arr.length + " was(were) selected");

        $("#uploadFileID").removeAttr("style");
        $("#cancelUploadFileID").removeAttr("style");

    }

    $(function () {
        $("[data-toggle='tooltip']").tooltip();
    });
</script>
    <sec:authorize access="hasRole('rename')">
<script>

    function openWindowForRename(oldName, localurl) {
        // only for file, not for folder
        var onlyName;
        var onlyNameArr = new Array;
        if (oldName.indexOf('.') > -1) {
            onlyNameArr = oldName.split('.');
            onlyName = onlyNameArr[0];
            oldName = onlyName;
        }

        // var newName = prompt("rename", oldName);
        bootbox.prompt({
            title: "Enter new name!",

            callback: function (result) {

                if(result!=null) {
                    var newName = checkingNewName(oldName, result, onlyNameArr);
                    if (newName != false) {
                        rename(oldName, localurl, newName);
                    }
                }
            }
        });
    }

    function rename(oldName, localurl, newName) {
//        var newName = checkNewName(oldName);
//        alert(newName);
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
                if (data == null || data == "") {

                    openFolder(rootFolder);
                    cancelUpload();
                    showTreeFolder();
                    openFolder(rootFolder);
                } else {
//                    alert(data);
                    myWarningAlert(data);
                }

            },
            error: function (data) {
//                alert("rename Error!!!");
                var a = data.status;
                if(a==405){
                    location.reload();
                }else {
                    myDangerAlert("rename Error!!!");
                }
            }
        });
        openFolder(rootFolder);
        showTreeFolder();
    }


    </sec:authorize>


</script>
</html>

