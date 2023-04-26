<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    if (isset($_POST['image'])) {
        $encodedImage = $_POST['image'];
        $decodedImage = base64_decode($encodedImage);
        $imageName = 'image_' . uniqid() . '.jpg'; // unique filename
        //echo 'Image uploaded successfully.';
    } else {
        die('Image not found.');
    }

    // Connect to the database
    $servername = "localhost";
    $username = "root";
    $password = "";
    $dbname = "fishfinder";

    $conn = mysqli_connect($servername, $username, $password, $dbname);
    if (!$conn) {
        die("Connection failed: " . mysqli_connect_error());
    }

    $imageType = $_POST['type'];
    if ($imageType == 'profile') {
        $userToken = $_POST['token'];

        $stmt = $conn->prepare("SELECT * from users WHERE token=?");
        $stmt->bind_param("s", $userToken);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        if ($row !== null) {
            $user_id = $row["user_id"];
        } else {
            // handle the case where the token is invalid
            die('invalid token');
        }
        $imagePath = 'http://10.129.17.5/fishfinder/uploads/' . $imageName;
        // Prepare the SQL statement with placeholders for the location data
        $stmt = mysqli_prepare($conn, "UPDATE users SET profile_picture = ? WHERE user_id = ?");
        mysqli_stmt_bind_param($stmt, "ss", $imagePath, $user_id);

        // Execute the prepared statement with the location data
        if (mysqli_stmt_execute($stmt)) {
            $imagePath = 'uploads/' . $imageName;
            $file = fopen($imagePath, 'wb');
            fwrite($file, $decodedImage);
            fclose($file);
            echo 'Profile Success';
        } else {
            echo 'Error';
        }

        mysqli_stmt_close($stmt);
    }elseif ($imageType == 'location') {

        $locationName = $_POST['locationName'];
        $userToken = $_POST['token'];
        
        $stmt = $conn->prepare("SELECT * from users WHERE token=?");
        $stmt->bind_param("s", $userToken);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        if ($row !== null) {
            $user_id = $row["user_id"];
        } else {
            // handle the case where the token is invalid
            die('invalid token here');
        }
        
        $stmt = $conn->prepare("SELECT * FROM locations WHERE name=?");
        $stmt->bind_param("s",$locationName);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        if ($row !== null) {
            $location_id = $row["id"];
        } else {
            // handle the case where the token is invalid
            die('invalid location name');
        }

        $stmt = $conn->prepare("SELECT * FROM user_locations WHERE user_id=? and location_id=?");
        $stmt->bind_param("ss", $user_id,$location_id);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        if ($row == null) {
            die('This is not your location');    
        }

        $imagePath = 'http://10.129.17.5/fishfinder/uploads/' . $imageName;

        $stmt = mysqli_prepare($conn, "UPDATE locations SET thumbnail = ? WHERE name = ?");
        mysqli_stmt_bind_param($stmt, "ss", $imagePath, $locationName);
        
        if(mysqli_stmt_execute($stmt)){
            $imagePath = 'uploads/' . $imageName;
            $file = fopen($imagePath, 'wb');
            fwrite($file, $decodedImage);
            fclose($file);
            echo 'Location Success';
        } else {
            echo 'Error';
        }
    }elseif ($imageType == 'fish'){
        $locationName = $_POST['locationName'];
        $imagePath = 'http://10.129.17.5/fishfinder/uploads/' . $imageName;
        $token = $_POST['token'];

        // get the user id based on the token
        $stmt = $conn->prepare("SELECT * from users WHERE token=?");
        $stmt->bind_param("s", $token);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        if ($row !== null) {
            $user_id = $row["user_id"];
        } else {
            // handle the case where the token is invalid
            die('invalid token');
        }

        // get the location id based on the location name
        $stmt = $conn->prepare("SELECT * from locations WHERE name=?");
        $stmt->bind_param("s", $locationName);
        $stmt->execute();
        $result = $stmt->get_result();
        $row = $result->fetch_assoc();
        if ($row !== null) {
            $location_id = $row["id"];
        } else {
            // handle the case where the token is invalid
            die('invalid token');
        }

        // insert into the location images table
        $stmt = mysqli_prepare($conn, "INSERT INTO location_images (location_id, user_id, url) VALUES (?, ?, ?)");
        mysqli_stmt_bind_param($stmt, "sss", $location_id, $user_id, $imagePath);
        
        if(mysqli_stmt_execute($stmt)){
            $imagePath = 'uploads/' . $imageName;
            $file = fopen($imagePath, 'wb');
            fwrite($file, $decodedImage);
            fclose($file);
            echo 'Fish Success';
        } else {
            echo 'Error';
        }
    }

} else {
    die('Invalid request.');
}
?>
