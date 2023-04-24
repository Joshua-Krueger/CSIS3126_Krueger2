<?php
header("Content-Type: application/json");

// Decode the JSON object received in the POST request
$data = json_decode(file_get_contents('php://input'), true);

// Extract the location data from the decoded JSON object
$rating = $data["rating"];
$description = $data["ratingDescription"];
$location_name = $data["locationName"];
$token = $data["token"];


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

$stmt = $conn->prepare("SELECT * from locations WHERE name=?");
$stmt->bind_param("s", $location_name);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();
if ($row !== null) {
    $location_id = $row["id"];
} else {
    // handle the case where the token is invalid
    die(json_encode(array('status' => 'failed', 'message' => 'invalid location name')));
}

$stmt = $conn->prepare("SELECT * from ratings WHERE user_id=? and location_id=?");
$stmt->bind_param("ss", $user_id,$location_id);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();
if ($row !== null) {
    $stmt = mysqli_prepare($conn, "DELETE FROM ratings WHERE user_id=? AND location_id=?");
    mysqli_stmt_bind_param($stmt, "ss", $user_id,$location_id);
    mysqli_stmt_execute($stmt);
}

// Prepare the SQL statement with placeholders for the location data
$stmt = mysqli_prepare($conn, "INSERT INTO ratings (user_id, location_id, rating, rating_description) VALUES (?, ?, ?, ?)");
mysqli_stmt_bind_param($stmt, "ssss", $user_id,$location_id,$rating,$description);

// Execute the prepared statement with the location data
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