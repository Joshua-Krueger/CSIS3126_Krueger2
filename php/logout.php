<?php

$con = mysqli_connect("localhost","root","","fishfinder");

if($con){
	//echo "connection successful";
}else{
	die("connection failed: " . mysqli_connect_error());

}

$stmt = $con->prepare("UPDATE users SET token = NULL WHERE token = ?");
$stmt->bind_param("s",$_POST["token"]);
$stmt->execute();
$result = $stmt->get_result();

if($result==0){
	echo "success";

}else{
	echo "failed";
}
?>