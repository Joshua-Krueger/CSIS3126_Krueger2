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

if($result->num_rows==0){
	echo "registering user";
	$stmt = $con->prepare("INSERT into users (name, email, password)
	VALUES (?,?,?)");
	$stmt->bind_param("sss",$_POST["name"],$_POST["email"],$_POST["password"]);
	$stmt->execute();
	$result = $stmt->get_result();

	if ($result->num_rows==1) {
		echo "success";
	}else{
		echo "failed";
	}

}else{
	echo "user already exists";
}
?>