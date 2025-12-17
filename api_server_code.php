<?php
// Ezt a fájlt nevezd át api.php-ra és töltsd fel a szerverre.

header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST");

// Adatbázis konfiguráció
$servername = "localhost";
$username = "root";       
$password = "";           // Ha van jelszó, írd át ide (pl. "admin")
$dbname = "plantbox";     // A megadott új adatbázis neve

// Kapcsolódás
$conn = new mysqli($servername, $username, $password, $dbname);
$conn->set_charset("utf8mb4");

if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed: " . $conn->connect_error]));
}

// Bemeneti adatok kezelése (JSON vagy Form-data)
$input = [];
$contentType = $_SERVER["CONTENT_TYPE"] ?? '';
if (strpos($contentType, "application/json") !== false) {
    $input = json_decode(file_get_contents('php://input'), true) ?? [];
} else {
    $input = $_POST;
}

// Action meghatározása
$action = $_GET['action'] ?? $input['action'] ?? '';

$response = ["success" => false, "message" => "Unknown action"];

switch ($action) {
    case 'register':
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            $response = ["success" => false, "message" => "POST method required"];
            break;
        }
        
        $name = $input['name'] ?? '';
        $email = $input['email'] ?? '';
        $password = $input['password'] ?? '';
        $username = $email; // Egyszerűsítés: email az username

        if (empty($name) || empty($email) || empty($password)) {
            $response = ["success" => false, "message" => "Missing fields"];
        } else {
            $stmt = $conn->prepare("SELECT id FROM users WHERE email = ? OR username = ?");
            $stmt->bind_param("ss", $email, $username);
            $stmt->execute();
            if ($stmt->get_result()->num_rows > 0) {
                $response = ["success" => false, "message" => "Email already exists"];
            } else {
                $hashed_password = password_hash($password, PASSWORD_DEFAULT);
                $insertStmt = $conn->prepare("INSERT INTO users (username, pass, email, name) VALUES (?, ?, ?, ?)");
                $insertStmt->bind_param("ssss", $username, $hashed_password, $email, $name);
                
                if ($insertStmt->execute()) {
                    $response = ["success" => true, "message" => "Registration successful"];
                } else {
                    $response = ["success" => false, "message" => "Registration failed: " . $conn->error];
                }
                $insertStmt->close();
            }
            $stmt->close();
        }
        break;

    case 'login':
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            $response = ["success" => false, "message" => "POST method required"];
            break;
        }

        $email = $input['email'] ?? '';
        $password = $input['password'] ?? '';

        if (empty($email) || empty($password)) {
            $response = ["success" => false, "message" => "Missing email or password"];
        } else {
            $stmt = $conn->prepare("SELECT id, name, pass FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $result = $stmt->get_result();

            if ($row = $result->fetch_assoc()) {
                if (password_verify($password, $row['pass'])) {
                    $response = [
                        "success" => true,
                        "message" => "Login successful",
                        "user" => [
                            "id" => $row['id'],
                            "name" => $row['name'],
                            "email" => $email
                        ]
                    ];
                } else {
                    $response = ["success" => false, "message" => "Invalid password"];
                }
            } else {
                $response = ["success" => false, "message" => "User not found"];
            }
            $stmt->close();
        }
        break;

    case 'get_devices':
        $userId = $_GET['user_id'] ?? $input['user_id'] ?? 0;
        
        $stmt = $conn->prepare("SELECT * FROM boxes WHERE owner_id = ?");
        $stmt->bind_param("i", $userId);
        $stmt->execute();
        $result = $stmt->get_result();
        
        $devices = [];
        while($row = $result->fetch_assoc()) {
            $devices[] = [
                "id" => (string)$row["box_id"],
                "name" => $row["name"],
                "plantName" => $row["plant"] ?? "Ismeretlen",
                "status" => "online",
                "moisture" => (int)($row["szarassag"] ?? 0),
                "light" => (int)($row["feny"] ?? 0),
                "temp" => (float)($row["ho"] ?? 0),
                "battery" => 100
            ];
        }
        
        echo json_encode($devices);
        exit;

    case 'add_device':
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            $response = ["success" => false, "message" => "POST method required"];
            break;
        }

        $userId = $input['user_id'] ?? 0;
        $name = $input['name'] ?? 'Új Eszköz';
        $plantName = $input['plant_name'] ?? 'Ismeretlen Növény';

        if (empty($userId)) {
            $response = ["success" => false, "message" => "User ID required"];
        } else {
            $stmt = $conn->prepare("INSERT INTO boxes (owner_id, name, plant, szarassag, feny, ho, para, legnyomas, vizszint) VALUES (?, ?, ?, 0, 0, 0, 0, 0, 0)");
            $stmt->bind_param("iss", $userId, $name, $plantName);
            
            if ($stmt->execute()) {
                $newId = $conn->insert_id;
                $response = [
                    "success" => true, 
                    "message" => "Device added successfully",
                    "device" => [
                        "id" => (string)$newId,
                        "name" => $name,
                        "plantName" => $plantName,
                        "status" => "online",
                        "moisture" => 0,
                        "light" => 0,
                        "temp" => 0.0,
                        "battery" => 100
                    ]
                ];
            } else {
                $response = ["success" => false, "message" => "Error adding device: " . $conn->error];
            }
            $stmt->close();
        }
        break;

    case 'water_plant':
        // Öntözés parancs
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            $response = ["success" => false, "message" => "POST method required"];
            break;
        }
        
        $deviceId = $input['device_id'] ?? '';
        $amount = $input['amount'] ?? 100; // ml, példa érték

        if (empty($deviceId)) {
            $response = ["success" => false, "message" => "Device ID required"];
        } else {
            // Itt lehetne bejegyezni egy 'commands' táblába, vagy frissíteni egy 'last_watered' mezőt.
            // Jelenleg csak szimuláljuk a sikert.
            // Opcionális: frissíthetjük a vizszintet a boxes táblában, ha lenne értelme (csökken/nő).
            
            $response = ["success" => true, "message" => "Watering command sent to device " . $deviceId];
        }
        break;

    case 'get_device_log':
        $boxId = $_GET['box_id'] ?? $input['box_id'] ?? 0;
        $limit = $_GET['limit'] ?? 100;

        if (empty($boxId)) {
            $response = ["success" => false, "message" => "Box ID required"];
            break;
        }

        // Feltételezve, hogy létezik a sensor_log tábla
        $stmt = $conn->prepare("SELECT * FROM sensor_log WHERE box_id = ? ORDER BY timestamp DESC LIMIT ?");
        $stmt->bind_param("ii", $boxId, $limit);
        $stmt->execute();
        $result = $stmt->get_result();
        $log_data = $result->fetch_all(MYSQLI_ASSOC);

        echo json_encode($log_data);
        exit;

    default:
        $response = ["success" => false, "message" => "Invalid action"];
        break;
}

echo json_encode($response);
$conn->close();
?>
