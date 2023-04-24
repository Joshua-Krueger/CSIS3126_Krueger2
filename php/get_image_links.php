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

// Query the location table with the name of the location (sent with the request) in order to get the location id
$inputJSON = file_get_contents('php://input');

$input = json_decode($inputJSON, TRUE);

$location_name = $input['name'];

$sql = "SELECT id FROM locations WHERE name = ?";

$stmt = mysqli_prepare($conn, $sql);

mysqli_stmt_bind_param($stmt, "s", $location_name);

// Execute the prepared statement with the location data
if (mysqli_stmt_execute($stmt)) {
     // Get the result
     $result = mysqli_stmt_get_result($stmt);

     while ($row = mysqli_fetch_assoc($result)) {
        $location_id = $row['id'];
     }
}



// Fetch the image links from the database
$stmt = mysqli_prepare($conn, "SELECT url FROM location_images WHERE location_id = ?");
mysqli_stmt_bind_param($stmt, "s", $location_id);

// Execute the prepared statement with the location data
if (mysqli_stmt_execute($stmt)) {
     // Get the result
     $result = mysqli_stmt_get_result($stmt);

     // Build the JSON response
     $response = array();
     $response['images'] = array();

     while ($row = mysqli_fetch_assoc($result)) {
        $response['images'][] = $row['url'];
     }

     // Return the JSON response
     header('Content-Type: application/json');
     echo json_encode($response);
} else {
     echo json_encode(array('error' => 'Failed to fetch image links'));
}

// Close the database connection
mysqli_close($conn);

?>