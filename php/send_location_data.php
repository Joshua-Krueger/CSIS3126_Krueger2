<?php

header('Content-Type: application/json');

// Get the request data as a JSON encoded string
$requestData = file_get_contents('php://input');

// Decode the JSON string into a PHP object
$requestObject = json_decode($requestData);

// Access the data in the object
$name = $requestObject->name;


// Create a MySQL connection
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "fishfinder";

$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

// Prepare and execute the SQL query
$stmt = $conn->prepare("SELECT * FROM locations WHERE name = ?");
$stmt->bind_param("s", $name);
$stmt->execute();
$result = $stmt->get_result();

// Process the query results
if ($result->num_rows > 0) {
  // location found
  $location = $result->fetch_assoc();
  // Prepare and execute the SQL query to get ratings for the location
  $stmt = $conn->prepare("SELECT * FROM ratings WHERE location_id = ?");
  $stmt->bind_param("s", $location["id"]);
  $stmt->execute();
  $result = $stmt->get_result();

  // Calculate the total rating for the location
  if ($result->num_rows == 0) {
    $total_rating = 5.0;
  } else {
    $total_rating = 5.0;
    $rows = 1;
    while ($row = $result->fetch_assoc()) {
      $total_rating += $row["rating"];
      $rows += 1;
    }
    $total_rating /= $rows;
  }

  // Build the response object
  $response = array(
    "status" => "success",
    "location" => array(
      "town" => $location["town"],
      "state" => $location["state"],
      "description" => $location["description"],
      "rating" => $total_rating,
      "thumbnail" => $location["thumbnail"]
    )
  );
  http_response_code(200);

} else {
  // location not found
  $response = array(
    "status" => "failed",
    "message" => "Location not found"
  ); 
  http_response_code(404);
}

// Send the response as a JSON encoded string
echo json_encode($response);

// Clean up
$stmt->close();
$conn->close();

?>
