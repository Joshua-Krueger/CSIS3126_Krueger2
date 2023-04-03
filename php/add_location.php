<?php

$con = mysqli_connect("localhost", "root", "", "fishfinder");

// get the JSON object from the POST request
$json_str = file_get_contents('php://input');

// convert the JSON string to a PHP array
$json_arr = json_decode($json_str, true);

// access the parameters sent from the Kotlin code
$token = $json_arr['token'];
$name = $json_arr['name'];
$state = $json_arr['state'];
$town = $json_arr['town'];
$latlng = $json_arr['latlng'];
$latitude = $json_arr['latitude'];
$longitude = $json_arr['longitude'];
$description = $json_arr['description'];
$thumbnail = NULL;

if (!$con) {
    die("connection failed: " . mysqli_connect_error());
}

$stmt = $con->prepare("SELECT * from users WHERE token=?");
$stmt->bind_param("s", $token);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();
if ($row !== null) {
    $user_id = $row["user_id"];
} else {
    // handle the case where the token is invalid
    die("invalid token");
}

$stmt = $con->prepare("INSERT into locations(user_id,name,state,town,latlng,latitude,longitude,description,thumbnail) values(?,?,?,?,?,?,?,?,?)");
$stmt->bind_param("sssssssss", $user_id, $name, $state, $town, $latlng, $latitude, $longitude, $description, $thumbnail);
$result = $stmt->execute();

$response = array();
if ($result == 200) {
    $response['status'] = 'success';
} else {
    $response['status'] = 'failed';
}

echo json_encode($response);

?>