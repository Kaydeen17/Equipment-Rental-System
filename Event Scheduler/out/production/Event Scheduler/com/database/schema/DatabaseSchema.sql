-- Create the Clients table
CREATE TABLE IF NOT EXISTS Clients (
    clientId INT PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL,
    contactInfo VARCHAR(255)
);

-- Create the Users table
CREATE TABLE IF NOT EXISTS Users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the Bookings table
CREATE TABLE IF NOT EXISTS Bookings (
    booking_id INT PRIMARY KEY NOT NULL,
    clientId INT,
    bookingDate DATE NOT NULL,
    returnDate DATE NOT NULL,
    user_id INT,
    FOREIGN KEY (clientId) REFERENCES Clients(clientId),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS Assets (
    asset_id INT PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    location VARCHAR(100) NOT NULL

);
-- Create the AssetInstances table
CREATE TABLE IF NOT EXISTS AssetInstances (
    serial_number VARCHAR(100) PRIMARY KEY NOT NULL,
    asset_id INT NOT NULL,
    status ENUM('Available', 'Booked', 'Maintenance') DEFAULT 'Available',
    booking_id INT DEFAULT NULL,
    FOREIGN KEY (asset_id) REFERENCES Assets(asset_id) ON DELETE CASCADE,
    FOREIGN KEY (booking_id) REFERENCES Bookings(booking_id) ON DELETE SET NULL
);

-- Create the Invoices table
CREATE TABLE IF NOT EXISTS Invoices (
    invoice_id INT PRIMARY KEY AUTO_INCREMENT,
    client_id INT,
    booking_id INT,
    user_id INT,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_date DATE,
    FOREIGN KEY (client_id) REFERENCES Clients(clientId),
    FOREIGN KEY (booking_id) REFERENCES Bookings(booking_id),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE SET NULL
);

-- Create the Payments table
CREATE TABLE IF NOT EXISTS Payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    invoice_id INT,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_date DATE NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES Invoices(invoice_id)
);

-- Create the Many-to-Many relationship between Bookings and Assets
CREATE TABLE IF NOT EXISTS Booking_Assets (
    booking_id INT NOT NULL,
    asset_id INT NOT NULL,
    PRIMARY KEY (booking_id, asset_id),
    FOREIGN KEY (booking_id) REFERENCES Bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (asset_id) REFERENCES Assets(asset_id) ON DELETE CASCADE
);

-- Create the Many-to-Many relationship between Bookings and Asset Instances
CREATE TABLE IF NOT EXISTS Booking_Asset_Instances (
    booking_id INT NOT NULL,
    serial_number VARCHAR(100) NOT NULL,
    PRIMARY KEY (booking_id, serial_number),
    FOREIGN KEY (booking_id) REFERENCES Bookings(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (serial_number) REFERENCES AssetInstances(serial_number) ON DELETE CASCADE
);
