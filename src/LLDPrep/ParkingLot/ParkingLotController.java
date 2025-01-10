@RestController
@RequestMapping("/api/v1/parking")
public class ParkingLotController {
    @PostMapping("/park")
    public ResponseEntity<ParkResponse> parkVehicle(@RequestBody ParkRequest request) {
        // Implementation
    }
    
    @PostMapping("/retrieve")
    public ResponseEntity<RetrieveResponse> retrieveVehicle(@RequestBody RetrieveRequest request) {
        // Implementation
    }
}

// Request/Response DTOs
public class ParkRequest {
    @NotNull
    private String licensePlate;
    @NotNull
    private VehicleType vehicleType;
}

public class ParkResponse {
    private String ticketId;
    private String licensePlate;
    private LocalDateTime entryTime;
    private SpotLocationDTO spotLocation;
    private String status;
}

public class RetrieveRequest {
    @NotNull
    private String ticketId;
}

public class RetrieveResponse {
    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private String status;
    private PaymentDetails paymentDetails;
}

public class SpotLocationDTO {
    private int level;
    private int position;
}

public class ErrorResponse {
    private String errorCode;
    private String message;
    private LocalDateTime timestamp;
}