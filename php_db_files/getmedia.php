<?php
    include_once 'db_functions.php';
    $db = new DB_Functions();
    $media = $db->getMediaRowCount();
	$a = array();
	$b = array();
    if ($media != false){
        $no_of_media = mysql_num_rows($media);
		while ($row = mysql_fetch_array($media)) {		
			$b["mediaUrl"] = $row["mediaUrl"];
			$b["mediaText"] = $row["mediaDescription"];
			array_push($a,$b);
		}
		echo json_encode($a);
	}
   
?>