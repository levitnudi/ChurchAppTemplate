<?php
include_once './db_functions.php';
//Create Object for DB_Functions clas
if(isset($_POST["mediaurl"]) && !empty($_POST["mediadetail"])){
$db = new DB_Functions(); 
//Store media details in db

$url = $_POST["mediaurl"];
$detail = $_POST["mediadetail"];
$res = $db->storeMedia($url, $detail);
	
	} 
?>

