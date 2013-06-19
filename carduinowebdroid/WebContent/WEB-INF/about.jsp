<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage="" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>CarDuinoDroid</title>

<!--JQuery Import-->
<link rel="stylesheet" href="static/jquery-ui-1.10.3.custom/css/ui-lightness/jquery-ui-1.10.3.custom.css" />
<script src="static/jquery-1.9.1.js"></script>
<script src="static/jquery-ui.js"></script>

<link rel="icon" href="static/favicon.ico" type="image/x-icon" />
<link rel="stylesheet" href="static/style.css" />

<script>
$(function() {
	$("input[type=button], button,#about_home").button();
});
</script>

</head>
<body>

<div id="about_logo">

	<img src="static/logo.png" />
       
</div>

<center>
	<div id="about_frame">
		Herzlich Willkommen bei CarDuinoDroid <br>
		<p>Die Website entstand im Rahmen des Projektes "Softwareprojekt 2013" der TU Ilmenau. Ziel des Projektes war es, 
		das vorherige gleichnamige Projekt weiter zu entwickeln, welches darin bestand ein handelsübliches Modellfahrzeug
		um ein Arduino Mikrocontrollerboard und ein Android Smartphone zu erweitern und somit in eine fernsteuerbare Drohne 
		zu verwandeln. Ein Android-Smartphone bietet eine große Menge an Interaktionsmöglichkeiten und über die Verbindung 
		mit einem Arduino-Board ist auch die digitale und analoge Ein- und Ausgabe von Android aus möglich. 
		Für unser Softwareprojekt stand somit ein voll funktionstüchtiger Aufbau und eine Desktop-Steuersoftware in Java zur 
		Verfügung.</p> 
		<p>Ziel dieses Softwareprojekts war die Entwicklung einer Webseite zur Steuerung des CarDuinoDroid innerhalb eines 
		Browsers. Die Oberfläche sollte vergleichbar zur Desktop-Software eine problemfreie Steuerung des Fahrzeugs 
		ermöglichen. Für die Darstellung des Videobilds des Android Smartphones wird ein Datenstrom mit Einzelbildern 
		übertragen. Dieser musste möglichst verzögerungsarm und fehlerunanfällig sein. Weitere für die Webseite benötigten 
		Funktionen waren unter anderem eine Warteschlange und ein Logging der Nutzeraktionen. Beobachter der Seite können 
		sich in der Warteschlange zum Steuern des Fahrzeugs einreihen, die maximale Steuerungszeit eines Nutzers sollte dabei 
		variabel von einem Administrator eingestellt werden können. Bei dem Design des gesamten Systems sollte auf 
		Verklemmungsfreiheit und Ressourcenverbrauch geachtet werden.</p> 
		
		<p>Wir hoffen, dass euch somit einen kleinen einblick in unser Projekt geben konnten. nun wünschen wir euch viel Spaß 
		mit dem CarDiunoDroid.</p>
		
		<center>
		<a href="index.jsp">
			<button value="Home" type="button" id="about_home" >Home</button></center>		
		</a>
	</div>
</center>

</body>
</html>