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

// Query the users table with the token in order to get the user id
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, TRUE);

$token = $_POST["token"];

$sql = "SELECT user_id FROM users WHERE token = ?";

$stmt = mysqli_prepare($conn, $sql);

mysqli_stmt_bind_param($stmt, "s", $token);

// Execute the prepared statement to get the user id
if (mysqli_stmt_execute($stmt)) {
     // Get the result
     $result = mysqli_stmt_get_result($stmt);

     while ($row = mysqli_fetch_assoc($result)) {
        $user_id = $row['user_id'];
     }
}else{
    die("User not found");
}

// Fetch the fish images for the user from the database
$sql = "SELECT url FROM location_images WHERE user_id = ?";
$stmt = mysqli_prepare($conn, $sql);
mysqli_stmt_bind_param($stmt, "i", $user_id);

// Execute the prepared statement to get the fish images
if (mysqli_stmt_execute($stmt)) {
     // Get the result
     $result = mysqli_stmt_get_result($stmt);

     // Build the JSON response
     $response = array();
     $response['images'] = array();

     while ($row = mysqli_fetch_assoc($result)) {
        array_push($response['images'], $row['url']);
     }

     // Return the JSON response
     header('Content-Type: application/json');
     echo json_encode($response);
} else {
     echo json_encode(array('error' => 'Failed to fetch fish images'));
}

// Close the database connection
mysqli_close($conn);

?>
