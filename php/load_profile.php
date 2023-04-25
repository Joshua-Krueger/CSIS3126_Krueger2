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

	$total_rating = 5;
	$total_rows = 1;
	while ($row2 = $result->fetch_assoc()) {
		// Prepare and execute the SQL query to get ratings for the location
		$stmt = $con->prepare("SELECT * FROM ratings WHERE location_id = ?");
		$stmt->bind_param("s", $row2["location_id"]);
		$stmt->execute();
		$result = $stmt->get_result();

		// Calculate the total rating for the location
		if ($result->num_rows == 0) {
			$current_rating = 5.0;
		} else {
			$current_rating = 5.0;
			$rows = 1;
			while ($row3 = $result->fetch_assoc()) {
			  $current_rating += $row3["rating"];
			  $rows += 1;
			}
			$current_rating /= $rows;
			$total_rating += $current_rating;
			$total_rows += 1;
		}
	}

	$total_rating /= $total_rows;

		$stmt = $con->prepare("SELECT * from location_images WHERE user_id=?");
	$stmt->bind_param("s",$id);
	$stmt->execute();
	$result = $stmt->get_result();
	$fish_num = $result->num_rows;

	$data = array(
		"name" => $row["name"],
		"trust_rating" => $total_rating,
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