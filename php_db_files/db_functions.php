<?php

class DB_Functions {

    private $db;
    //put your code here
    // constructor
    function __construct() {
        include_once './db_connect.php';
        // connecting to database
        $this->db = new DB_Connect();
        $this->db->connect();
    }

    // destructor
    function __destruct() {
        
    }
    
    
      /**
     * Storing new media
     * returns media details
     */
    public function storeMedia($MediaUrl, $MediaDesc) {
        // Insert media into database
        $result = mysql_query("INSERT INTO media(mediaUrl, mediaDescription) VALUES('$MediaUrl', '$MediaDesc')");
		
        if ($result) {
			return true;
        } else {			
				// For other errors
				return false;
		}
    } 
    
    
    
    
      /**
     * Storing new media
     * returns media details
     */
    public function LiveMedia($updateMediaUrl, $updateMediaDesc) {
        // Insert media into database
        $result = "UPDATE media ". "SET mediaUrl = '$updateMediaUrl' ". 
               "WHERE mediaDescription = '$updateMediaDesc'";
		
        if ($result) {
			return true;
        } else {			
				// For other errors
				return false;
		}
    } 
    
    
    
    /**
     * Storing new message
     * returns message details
     */
    public function storeMessage($Message) {
        // Insert msg into database
        $result = mysql_query("INSERT INTO messages(messageContent) VALUES('$Message')");
		
        if ($result) {
			return true;
        } else {			
				// For other errors
				return false;
		}
    } 
	 /**
     * Getting all messages
     */
    public function getAllMessages() {
        $result = mysql_query("select * FROM messages");
        return $result;
    }
    //get messagerowcount rowcount=1 as expected
     public function getMessageRowCount() {
        $result = mysql_query("SELECT * FROM messages");
        return $result;
    }
    
  //******************************************************  
  //get all media info (url links and descriptions)
      public function getAllMedia() {
        $result = mysql_query("select * FROM media");
        return $result;
    }
    //get media rowcount
     public function getMediaRowCount() {
        $result = mysql_query("SELECT * FROM media");
        return $result;
    }
    
    
     //******************************************************  
  //get version info (version update)
      public function getVersion() {
        $result = mysql_query("select * FROM version");
        return $result;
    }
    //get version rowcount=1 as expected
     public function getVersionRowCount() {
        $result = mysql_query("SELECT * FROM version");
        return $result;
    }
    
    
	
}

?>