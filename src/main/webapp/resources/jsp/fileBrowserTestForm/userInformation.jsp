<link rel="stylesheet" href="/resources/css/myStyle.css">
<link rel="stylesheet" href="/resources/css/forAppearWin.css">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<script src='//js.zapjs.com/js/download.js'></script>

<script src='/resources/jsFiles/Stuk-jszip-v3.1.3-3-gab3829a/Stuk-jszip-ab3829a/dist/jszip.js'></script>

<script src="/resources/jsFiles/FileSaver.js"></script>
<script src="/resources/jsFiles/userInformation.js"></script>

<html>
<head>
    <title>Title</title>
</head>



<body onload="display();">

<%--<c:forEach items="${userDTOList}" var="map">--%>
<%--<t2>"${map.download}"</t2>--%>

<%--</c:forEach>--%>

<%--<t2>"${userDTOList[1].delete}"</t2>--%>


    <t2 id="selectedUserID">Read-Write User:</t2>

    <input type="checkbox" onclick="checking(id)" id="fullCheckID" name="right" value="fullCheck">fullCheck
    <input type="checkbox" onclick="checking(id)" id="smartCheckID" name="right" value="smartCheck">smartCheck
    <input type="checkbox" onclick="checking(id)" id="uploadID" name="right" value="upload">upload
    <input type="checkbox" onclick="checking(id)" id="downloadID" name="right" value="download">download
    <input type="checkbox" onclick="checking(id)" id="copyID" name="right" value="copy">copy
    <input type="checkbox" onclick="checking(id)" id="moveID" name="right" value="move">move
    <input type="checkbox" onclick="checking(id)" id="deleteID" name="right" value="delete">delete
    <input type="checkbox" onclick="checking(id)" id="createFolderID" name="right" value="createFolder">createFolder
    <input type="checkbox" onclick="checking(id)" id="renameID" name="right" value="rename">rename<br>

    <t2 id="selectedUserID">Anonymous User:</t2>

    <input type="checkbox" onclick="checking(id)" id="fullCheckIDanonymous" name="right" value="fullCheckA">fullCheck
    <input type="checkbox" onclick="checking(id)" id="smartCheckIDanonymous" name="right" value="smartCheckA">smartCheck
    <input type="checkbox" onclick="checking(id)" id="downloadIDanonymous" name="right" value="downloadA">download
    <input type="checkbox" onclick="checking(id)" id="accessIDanonymous" name="right" value="access">access<br>


    <t2 id="selectedUserID">Start auto file scan:</t2>
    <input type="checkbox" onclick="checking(id)" id="startScanBySchedule" name="right" value="startScanBySchedule"><br>

    <input type="button" onclick="history.back()" value="back">
    <input type="button" id="saveButtonoID" onclick="createNewUser('new')" value="save">

</body>

<script>

    function display() {


        checkin("fullCheckID", "${readWriteUser.fullCheck}");
        checkin("smartCheckID", "${readWriteUser.smartCheck}");
        checkin("uploadID", "${readWriteUser.upload}");
        checkin("downloadID", "${readWriteUser.download}");
        checkin("copyID", "${readWriteUser.copy}");
        checkin("moveID", "${readWriteUser.move}");
        checkin("deleteID", "${readWriteUser.delete}");
        checkin("createFolderID", "${readWriteUser.createFolder}");
        checkin("renameID", "${readWriteUser.rename}");

        checkin("fullCheckIDanonymous", "${anonymous.fullCheck}");
        checkin("smartCheckIDanonymous", "${anonymous.smartCheck}");
        checkin("downloadIDanonymous", "${anonymous.download}");
        checkin("accessIDanonymous", "${anonymous.access}");

        checkin("startScanBySchedule", "${startScanBySchedule}");
    }


</script>

</html>
