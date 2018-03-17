<?php
    include_once 'db_functions.php';
    $db = new DB_Functions();
    $messages = $db->getMessageRowCount();
	$a = array();
	$b = array();
    if ($messages != false){
        $no_of_users = mysql_num_rows($messages);
		while ($row = mysql_fetch_array($messages)) {		
			$b["messageId"] = $row["messageId"];
			$b["messageText"] = $row["messageContent"];
			array_push($a,$b);
		}
		echo json_encode($a);
	}
   
?>