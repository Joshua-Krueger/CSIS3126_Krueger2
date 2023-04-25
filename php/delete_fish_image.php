<?php

// Connect to the database
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "fishfinder";

$conn = mysqli_connect($servername, $username, $password, $dbname);

// Check connection
if (!$conn) {
    die("Connection failed: " . mysqli_connect_error());
}

$token = $_POST["token"];
$imageUrl = $_POST["imageUrl"];

$sql = "SELECT user_id FROM users WHERE token = ?";

$stmt = mysqli_prepare($conn, $sql);

mysqli_stmt_bind_param($stmt, "s", $token);

// Execute the prepared statement with the location data
if (mysqli_stmt_execute($stmt)) {
     // Get the result
     $result = mysqli_stmt_get_result($stmt);

     while ($row = mysqli_fetch_assoc($result)) {
        $user_id = $row['user_id'];
     }
}else{
    die("User not found");
}

// Fetch the image links from the database
$stmt = mysqli_prepare($conn, "DELETE FROM location_images WHERE user_id = ? AND url = ?");
mysqli_stmt_bind_param($stmt, "ss", $user_id,$imageUrl);

// Execute the prepared statement with the location data
if (mysqli_stmt_execute($stmt)) {
     // Return the JSON response
     header('Content-Type: application/json');
     echo json_encode(array('success'=> 'deleted image'));
} else {
     echo json_encode(array('error' => 'Failed to delete image'));
}

if(mysqli_stmt_errno($stmt) !== 0) {
    echo json_encode(array('error' => mysqli_stmt_error($stmt)));
    exit;
}

// Close the database connection
mysqli_close($conn);

?>