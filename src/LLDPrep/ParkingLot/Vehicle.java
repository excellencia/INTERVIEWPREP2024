package LLDPrep.ParkingLot;

public class Vehicle {
    private String licensePlate;
    private VehicleType type;
    private long entryTime;
    
    // Constructors
    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.entryTime = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getLicensePlate() { return licensePlate; }
    public VehicleType getType() { return type; }
    public long getEntryTime() { return entryTime; }
}

public class ParkingSpot {
    private int spotId;
    private boolean isOccupied;
    private Vehicle parkedVehicle;
    private SpotLocation location;
    
    // Constructors
    public ParkingSpot(int spotId, SpotLocation location) {
        this.spotId = spotId;
        this.location = location;
        this.isOccupied = false;
        this.parkedVehicle = null;
    }
    
    // Getters and Setters
    public int getSpotId() { return spotId; }
    public boolean isOccupied() { return isOccupied; }
    public Vehicle getParkedVehicle() { return parkedVehicle; }
    public SpotLocation getLocation() { return location; }
    
    public void setOccupied(boolean occupied) { this.isOccupied = occupied; }
    public void setParkedVehicle(Vehicle vehicle) { this.parkedVehicle = vehicle; }
}

public class ParkingTicket {
    private String ticketId;
    private String licensePlate;
    private long entryTime;
    private SpotLocation spotLocation;
    private boolean isActive;
    
    // Constructors, Getters, and Setters
    // ...
    
    public void deactivate() {
        this.isActive = false;
    }
}

public class AutomatedParkingLot {
    private final Map<String, ParkingSpot> occupiedSpots;
    private final Queue<ParkingSpot> availableSpots;
    private final int totalCapacity;
    private final int totalLevels;
    
    // Constructor
    public AutomatedParkingLot(int totalCapacity, int totalLevels) {
        this.totalCapacity = totalCapacity;
        this.totalLevels = totalLevels;
        this.occupiedSpots = new ConcurrentHashMap<>();
        this.availableSpots = new ConcurrentLinkedQueue<>();
        initializeSpots();
    }
    
    private void initializeSpots() {
        // Initialize parking spots
    }
    
    public synchronized ParkingTicket parkVehicle(Vehicle vehicle) throws ParkingFullException {
        // Implementation
    }
    
    public synchronized Vehicle retrieveVehicle(String ticketId) throws InvalidTicketException {
        // Implementation
    }
    
    private ParkingTicket generateTicket(Vehicle vehicle, ParkingSpot spot) {
        // Implementation
    }
}
