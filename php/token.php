<?php

$con = mysqli_connect("localhost","root","","fishfinder");

if($con){
	//echo "connection successful";
}else{
	die("connection failed: " . mysqli_connect_error());

}
/*
//------------------goal for this section is to remove any tokens still registered under the user-------------------------//
$stmt = $con->prepare("SELECT * FROM users WHERE email=?");
$stmt->bind_param("s",$_POST["email"]);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();
$id = $row["user_id"];

$stmt = $con->prepare("SELECT * FROM tokens WHERE user_id=?");
$stmt->bind_param("s",$id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows>0) {
	$stmt = $con->prepare("DELETE * FROM tokens where user_id=?");
	$stmt->bind_param("s",$id);
	$stmt->execute();
}
//------------------------------------------------------------------------------------------------------------------------//
*/
$stmt = $con->prepare("SELECT * from tokens WHERE token=?");
$stmt->bind_param("s",$_POST["token"]);
$stmt->execute();
$result = $stmt->get_result();

if($result->num_rows==0){
	echo "success";
}else{
	echo "token in use";
}
?>