<?php

$con = mysqli_connect("localhost","root","","fishfinder");

if(!$con){
	die("connection failed: " . mysqli_connect_error());
}
# Generate unique token
$token = uniqid();
$token_check = FALSE;
# make sure the token is not in use even though there is a miniscule chance of that happening
# TODO this infinitely loops, fix it
while(!$token_check){
	$stmt = "SELECT * FROM users WHERE token='$token'";
	# if the token is not already in use
	if ($con->query($stmt)->num_rows == 0) {
		$token_check = TRUE;
	}else{
		$token = uniqid();
	}
}
# Save token to the correct user
$stmt = $con->prepare("UPDATE users SET token='$token' WHERE email=? and password=?");
$stmt->bind_param("ss",$_POST["email"],$_POST["password"]);
$result = $stmt->execute();
if ($result === TRUE) {
	# if the token can be saved, then the user exists and the login is also valid
	$response = array("success" => true, "message" => "User Logged in Successfully", "token" => $token);
} else {
	# otherwise, send the error message
	$response = array("success" => false, "message" => "Error: " . $stmt . "<br>" . $con->error);
}

# Close MySQL connection
$con->close();

# Send JSON response
header('Content-Type: application/json');
echo json_encode($response);
?>
