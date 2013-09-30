<%@ page language="java" contentType="text/html; charset=UTF8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href="image/OpenRemote_Logo16x16.png" rel="shortcut icon"/>
<link href="image/OpenRemote_Logo16x16.png" type="image/png" rel="icon"/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Sensor/Polling Debug Monitor</title>
<style type="text/css">
body {
	font-family:tahoma,arial,verdana,sans-serif;
	font-size:12px;
	color: #00FF00;
	background-color: black;
}
table, tr, td {
	padding: 5px;
	border-collapse:collapse;
	border: 2px dashed #00FF00;
}
</style>
</head>
<body>

<h4>controller.xml/panel.xml changed: ${xmlChanged}</h4>
<h4>loaded sensors:</h4>
<table>
	<tr>
		<td>sensorID</td>
		<td>sensorType</td>
		<td>stateMap</td>
	</tr>
	<c:forEach items="${sensors}" var="sensor">
		<tr>
			<td>${sensor.sensorID}</td>
			<td>${sensor.sensorType}</td>
			<td>${sensor.stateMap}</td>
		</tr>
	</c:forEach>
</table>

<h4>polling threads via status command:</h4>
<table>
	<tr>
		<td>sensorID</td>
		<td>lastStatus</td>
	</tr>
	<c:forEach items="${threads}" var="thread">
		<tr>
			<td>${thread.sensor.sensorID}</td>
			<td>${thread.lastStatus}</td>
		</tr>
	</c:forEach>
</table>

<h4>panel status cache table:</h4>
<table>
	<tr>
		<td>deviceID</td>
		<td>pollingSensorIDs</td>
		<td>statusChangedSensorIDs</td>
	</tr>
	<c:forEach items="${records}" var="record">
		<tr>
			<td>${record.deviceID}</td>
			<td>${record.pollingSensorIDs}</td>
			<td>${record.statusChangedSensorIDs}</td>
		</tr>
	</c:forEach>
</table>

</body>
</html>