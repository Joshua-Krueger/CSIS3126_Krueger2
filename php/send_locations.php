<?php

$con = mysqli_connect("localhost","root","","fishfinder");

if(!$con){
	die("connection failed: " . mysqli_connect_error());
}

$stmt = ("SELECT * FROM locations");
$result = $con->query($stmt);

if(!$result){
	die('failed to retrieve data');
}

if ($result->num_rows == 0) {
	die('no data yet');
}

$data = array();

while($row = $result->fetch_assoc()){
	$data[] = $row;
}

$con->close();

header('Content-Type: application/json');

echo json_encode($data);

?>