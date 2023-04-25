<?php

$con = mysqli_connect("localhost","root","","fishfinder");

if(!$con){
	die("connection failed: " . mysqli_connect_error());
}

# check if user with email exists
$stmt = $con->prepare("SELECT * from users WHERE email=?");
$stmt->bind_param("s",$_POST["email"]);
$stmt->execute();
$result = $stmt->get_result();

if($result->num_rows==1){
	# if user exists, verify password
	$row = $result->fetch_assoc();
	if(password_verify($_POST["password"], $row["password"])){
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
		$stmt = $con->prepare("UPDATE users SET token='$token' WHERE email=?");
		$stmt->bind_param("s",$_POST["email"]);
		$result = $stmt->execute();
		if ($result === TRUE) {
			# if the token can be saved, then the user exists and the login is also valid
			$response = array("success" => true, "message" => "User Logged in Successfully", "token" => $token);
		} else {
			# otherwise, send the error message
			$response = array("success" => false, "message" => "Error: " . $stmt . "<br>" . $con->error);
		}
	}else{
		# if password is incorrect, send error message
		$response = array("success" => false, "message" => "Incorrect password");
	}
}else{
	# if user doesn't exist, send error message
	$response = array("success" => false, "message" => "User does not exist");
}

# Close MySQL connection
$con->close();

# Send JSON response
header('Content-Type: application/json');
echo json_encode($response);
?>