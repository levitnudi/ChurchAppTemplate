<?php
    include_once 'db_functions.php';
    $db = new DB_Functions();
    $version = $db->getVersionRowCount();
	$a = array();
	$b = array();
    if ($version != false){
        $no_of_version = mysql_num_rows($version);
		while ($row = mysql_fetch_array($version)) {		
			$b["versionId"] = $row["currentVersion"];
			$b["versionText"] = $row["versionName"];
			array_push($a,$b);
		}
		echo json_encode($a);
	}
   
?>