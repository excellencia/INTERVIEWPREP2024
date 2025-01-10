CREATE TABLE product_types (
    type_id VARCHAR(10) PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL,
    base_rental_rate DECIMAL(10,2) NOT NULL,
    deposit_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    product_id VARCHAR(10) PRIMARY KEY,
    type_id VARCHAR (10) REFERENCES product_types(type_id),
    serial_number VARCHAR(50) UNIQUE NOT NULL,
    bike_size VARCHAR(20), --NULL FOR SCOOTERS
    motor_type VARCHAR(20), -- NULL FOR BIKES
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_maintained_at TIMESTAMP,
    CHECK(status IN ('AVAILABLE','RENTED','DAMAGED','RETIRED'))

);

CREATE TABLE customers (
    customer_id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    address TEXT NOT NULL,
    email VARCHAR(50) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    id_proof_type VARCHAR(20) NOT NULL,
    id_proof_number VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_proof_type, id_proof_number)
);

CREATE TABLE rentals (
    rental_id VARCHAR(10) PRIMARY KEY,
    customer_id VARCHAR(10) REFERENCES customer(customer_id),
    product_id VARCHAR(10) REFERENCES product(product_id),
    start_date TIMESTAMP NOT NULL,
    due_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP,
    rental_rate DECIMAL(10,2) NOT NULL,
    deposit_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK(status IN ('ACTIVE','COMPLETED', 'CANCELED'))
);

CREATE TABLE charges (
    charge_id VARCHAR(10) PRIMARY KEY,
    rental_id VARCHAR(10) REFERENCES rentals(rental_id),
    charge_type VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(10) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK(status IN ('PENDING', 'PAID', 'REFUNDED')),
    CHECK(charge_type IN ('RENTAL', 'LATE_FEE', 'DAMAGE'))
);

--How many small bikes do you have?
SELECT count(*)
FROM products p 
JOIN product_types pt ON p.type_id = pt.type_id
WHERE pt.type_name = 'BIKE' 
AND p.bike_size = 'SMALL'
AND p.status = 'AVAILABLE';

--What products are there for rent?
SELECT p.product_id, pt.type_name, p.serial_number, p.bike_size, p.motor_type
FROM products p
JOIN product_types pt ON p.type_id = pt.type_id
WHERE p.status = 'AVAILABLE';

--Does this customer have a balance? (aka owe us money)
SELECT c.customer_id, SUM(ch.amount) AS balance
FROM customers c
JOIN rentals rn ON c.customer_id = rn.customer_id
JOIN charges ch ON rn.rental_id = ch.rental_id
WHERE ch.status = 'PENDING'
GROUP BY c.customer_id
HAVING SUM(ch.amount) > 0;

--What products are rented?
select p.product_id, pt.type_name,r.start_date, r.due_date,
c.name as rented_by
FROM rentals r
JOIN product p ON r.product_id = p.product_id
JOIN product_types pt ON p.type_id = pt.type_id
JOIN customer c on r.customer_id = c.customer_id
WHERE r.status = 'ACTIVE'

--Are there products that are overdue for return? Who has them?
select r.rental_id, p.product_id, pt.type_name, r.start_date,r.due_date,
c.name as customer_name, 
EXTRACT(hour from (CURRENT_TIMESTAMP - r.due_date)) as hours_overdue
FROM rentals r
JOIN products p on r.product_id = p.product_id
JOIN customers c on r.customer_id = c.customer_id
JOIN product_types pt on p.type_id = pt.type_id
WHERE r.status = 'ACTIVE'
AND (due_date < CURRENT_TIMESTAMP)

--What products has a customer rented?
SELECT r.rental_id, p.product_id, pt.type_name, r.start_date, r.due_date, r.return_date
FROM rentals r
JOIN products p ON r.product_id = p.product_id
JOIN product_types pt ON p.type_id = pt.type_id
WHERE r.customer_id = 'CUSTOMER_ID'