-- Create Customer table
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    location VARCHAR(255)
);

-- Create Product table
CREATE TABLE IF NOT EXISTS product (
    code VARCHAR(255) PRIMARY KEY,
    cost INT,
    status VARCHAR(50) -- 'Active' or 'Inactive'
);

-- Create Transaction table
CREATE TABLE IF NOT EXISTS transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_time TIMESTAMP,
    quantity INT,
    total_cost INT,
    customer_id BIGINT,
    product_code VARCHAR(255),
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (product_code) REFERENCES product(code)
);
