<?php

$con = mysqli_connect("localhost","root","","fishfinder");

if($con){
	//echo "connection successful";
} else{
	die("connection failed: " . mysqli_connect_error());
}

$stmt = $con->prepare("SELECT * from users WHERE token=?");
$stmt->bind_param("s",$_POST["token"]);
$stmt->execute();
$result = $stmt->get_result();

if($result->num_rows==1){
	$row = $result->fetch_assoc();

	$id = $row["user_id"];

	$stmt = $con->prepare("SELECT * from user_locations WHERE user_id=?");
	$stmt->bind_param("s",$id);
	$stmt->execute();
	$result = $stmt->get_result();	
	$location_num = $result->num_rows;

		$stmt = $con->prepare("SELECT * from location_images WHERE user_id=?");
	$stmt->bind_param("s",$id);
	$stmt->execute();
	$result = $stmt->get_result();	
	$fish_num = $result->num_rows;

	$data = array(
		"name" => $row["name"],
		"trust_rating" => $row["trust_rating"],
		"profile_picture" => $row["profile_picture"],
		"fish_num" => $fish_num,
		"location_num" => $location_num
	);
	echo json_encode($data);
} else{
	$data = array(
		"error" => "user does not exist"
	);
	echo json_encode($data);
}
?>
