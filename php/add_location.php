<?php
header("Content-Type: application/json");

// Extract the location data from the POST request
$token = $_POST["token"];
$name = $_POST["name"];
$state = $_POST["state"];
$town = $_POST["town"];
$latitude = $_POST["latitude"];
$longitude = $_POST["longitude"];
$description = $_POST["description"];

// Add defaults for rating and thumbnail
$id = null;
$thumbnail = null;

// Connect to the database
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "fishfinder";

$conn = mysqli_connect($servername, $username, $password, $dbname);
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$stmt = $conn->prepare("SELECT * from users WHERE token=?");
$stmt->bind_param("s", $token);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();
if ($row !== null) {
    $user_id = $row["user_id"];
} else {
    // handle the case where the token is invalid
    die(json_encode(array('status' => 'failed', 'message' => 'invalid token')));
}

// Prepare the SQL statement with placeholders for the location data
$stmt = mysqli_prepare($conn, "INSERT INTO locations (id, name, state, town, latitude, longitude, description, thumbnail) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
mysqli_stmt_bind_param($stmt, "ssssssss", $id, $name, $state, $town, $latitude, $longitude, $description, $thumbnail);

// Execute the prepared statement with the location data
if (mysqli_stmt_execute($stmt)) {
    $response_array["status"] = "success";
} else {
    $response_array["status"] = "error";
}

mysqli_stmt_close($stmt);

$stmt = $conn->prepare("SELECT * from locations WHERE name=?");
$stmt->bind_param("s", $name);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();
if ($row !== null) {
    $location_id = $row["id"];
}

// for some reason, this statement is not happening, fix it
$stmt = $conn->prepare("INSERT INTO user_locations (id, user_id, location_id) VALUES (?, ?, ?)");
mysqli_stmt_bind_param($stmt, "sss", $id, $user_id, $location_id);

if (mysqli_stmt_execute($stmt)) {
    $response_array["status"] = "success";
} else {
    $response_array["status"] = "error";
}

mysqli_stmt_close($stmt);
mysqli_close($conn);

// Return the response as a JSON object
echo json_encode($response_array);

?>