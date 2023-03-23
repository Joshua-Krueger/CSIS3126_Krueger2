<?php

$con = mysqli_connect("localhost","root","","fishfinder");

if($con){
	//echo "connection successful";
}else{
	die("connection failed: " . mysqli_connect_error());

}

$stmt = $con->prepare("SELECT * from users WHERE token=?");
$stmt->bind_param("s",$_POST["token"]);
$stmt->execute();
$result = $stmt->get_result();

if($result->num_rows==1){
	$row = $result->fetch_assoc();
	echo "information:".$row["name"].",".$row["trust_rating"].",".$row["profile_picture"];

}else{
	echo "user does not exist";
}
?>