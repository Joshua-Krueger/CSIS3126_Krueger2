<?php

$con = mysqli_connect("localhost","root","","fishfinder");

if($con){
	//echo "connection successful";
}else{
	die("connection failed: " . mysqli_connect_error());
}

$stmt = $con->prepare("SELECT * from users WHERE email=? AND password=?");
$stmt->bind_param("ss",$_POST["email"],$_POST["password"]);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();
$id = $row["user_id"];

if ($result->num_rows==0) {
	die("user does not exist");
}else{
	//echo "success";
}

$stmt = $con->prepare("UPDATE users 
SET token=? 
WHERE email=? AND password=?");
$stmt->bind_param("sss",$_POST["token"],$_POST["email"],$_POST["password"]);
$result = $stmt->execute();
//$result = $stmt->get_result();

if($result==true){
	//echo "success";
}else{
	echo "failed";
}

$stmt = $con->prepare("INSERT into tokens (token, user_id) VALUES (?,?)");
$stmt->bind_param("ss",$_POST["token"],$id);
$result = $stmt->execute();
//$result = $stmt->get_result();

if ($result==true) {
	echo "success";
}else{
	echo "failed";
}
?>