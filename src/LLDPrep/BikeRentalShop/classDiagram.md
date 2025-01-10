```mermaid
classDiagram
    %% Product hierarchy
    class Product {
        <<interface>>
        +getId(): String
        +isAvailable(): boolean
        +setAvailable(boolean)
        +getRentalRate(): double
    }
    
    class AbstractProduct {
        <<abstract>>
        -id: String
        -available: boolean
        -rentalRate: double
        +AbstractProduct(id: String, rate: double)
    }
    
    class Bike {
        -size: Size
        -serialNumber: String
        +getSize(): Size
        +getSerialNumber(): String
    }
    
    class Scooter {
        -motorType: MotorType
        -registrationNumber: String
        +getMotorType(): MotorType
        +getRegistrationNumber(): String
    }
    
    %% Factory pattern
    class ProductFactory {
        <<interface>>
        +createProduct(id: String, specs: Map): Product
    }
    
    class BikeFactory {
        +createProduct(id: String, specs: Map): Product
    }
    
    class ScooterFactory {
        +createProduct(id: String, specs: Map): Product
    }
    
    %% Strategy pattern for fee calculation
    class RentalFeeStrategy {
        <<interface>>
        +calculateFee(Product, startTime: DateTime, endTime: DateTime): double
    }
    
    class HourlyRentalStrategy {
        +calculateFee(Product, startTime: DateTime, endTime: DateTime): double
    }
    
    class HolidayRentalStrategy {
        -holidayMultiplier: double
        +calculateFee(Product, startTime: DateTime, endTime: DateTime): double
    }
    
    %% Observer pattern
    class RentalObserver {
        <<interface>>
        +onRentalStart(RentalEvent)
        +onRentalEnd(RentalEvent)
    }
    
    class InventoryManager {
        +onRentalStart(RentalEvent)
        +onRentalEnd(RentalEvent)
    }
    
    class CustomerHistoryTracker {
        -customerHistory: Map
        +onRentalStart(RentalEvent)
        +onRentalEnd(RentalEvent)
        +getCustomerHistory(customerId: String): List
    }
    
    %% Repository interfaces
    class ProductRepository {
        <<interface>>
        +addProduct(Product)
        +removeProduct(productId: String)
        +findAvailableProducts(): List
        +findByType(type: String): List
        +findById(id: String): Product
        +countBikesBySize(size: Size): long
    }
    
    class RentalRepository {
        <<interface>>
        +addRental(RentalRecord)
        +updateRental(RentalRecord)
        +findActiveRentals(): List
        +findOverdueRentals(): List
        +findCustomerRentals(customerId: String): List
    }
    
    %% Core domain classes
    class RentalRecord {
        -customerId: String
        -product: Product
        -startTime: DateTime
        -dueTime: DateTime
        -actualReturnTime: DateTime
        -charges: double
        +isOverdue(): boolean
    }
    
    class CustomerAccount {
        -customerId: String
        -balance: double
        -rentalHistory: List
        +addCharge(amount: double)
        +makePayment(amount: double)
        +getBalance(): double
        +addRentalRecord(RentalRecord)
    }
    
    class RentalService {
        -productRepository: ProductRepository
        -rentalRepository: RentalRepository
        -customerAccounts: Map
        -feeStrategy: RentalFeeStrategy
        +createRental(customerId: String, productId: String, startTime: DateTime, dueTime: DateTime)
        +getCustomerBalance(customerId: String): double
        +findOverdueRentals(): List
        +getCustomerRentals(customerId: String): List
    }
    
    %% Relationships
    Product <|.. AbstractProduct : implements
    AbstractProduct <|-- Bike : extends
    AbstractProduct <|-- Scooter : extends
    ProductFactory <|.. BikeFactory : implements
    ProductFactory <|.. ScooterFactory : implements
    RentalFeeStrategy <|.. HourlyRentalStrategy : implements
    RentalFeeStrategy <|.. HolidayRentalStrategy : implements
    RentalObserver <|.. InventoryManager : implements
    RentalObserver <|.. CustomerHistoryTracker : implements
    RentalService *-- ProductRepository : has
    RentalService *-- RentalRepository : has
    RentalService *-- RentalFeeStrategy : has
    RentalService o-- RentalObserver : observes
    RentalRecord o-- Product : contains
    CustomerAccount o-- RentalRecord : contains