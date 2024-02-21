DROP SCHEMA IF EXISTS `spring-mvc-ecommerce`;

CREATE SCHEMA `spring-mvc-ecommerce`;
USE `spring-mvc-ecommerce` ;

CREATE TABLE Categories (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE Products (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(255),
    price DOUBLE NOT NULL,
    create_date TIMESTAMP,
    available BOOLEAN,
    category_id BIGINT(20) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES Categories(id)
);
CREATE TABLE Orders (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    create_date TIMESTAMP,
    address VARCHAR(255) NOT NULL,
    status varchar(50),
    FOREIGN KEY (username) REFERENCES Account(username)
);
CREATE TABLE OrderDetails (
    id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT(20) NOT NULL,
    product_id BIGINT(20) NOT NULL,
    price DOUBLE NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(id),
    FOREIGN KEY (product_id) REFERENCES Products(id)
);
CREATE TABLE Account (	
    username VARCHAR(255) PRIMARY KEY,
    address varchar(255),
    password VARCHAR(255) NOT NULL,
    fullname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,	
    photo VARCHAR(255),
    activated BOOLEAN,
    admin BOOLEAN
);
SELECT username, password FROM account WHERE username = 'huy2' ;

SELECT username, 'ROLE_USER' as authority FROM account WHERE username='huy2'  UNION 
SELECT username, 'ROLE_ADMIN' as authority FROM account WHERE username='huy2' AND admin=true;
-- Insert Account
INSERT INTO Account (username, address, password, fullname, email, photo, activated, admin)
VALUES ('huy2','TPHCM', '123456', 'John Doe', 'johndoe@example.com', 'default_photo.jpg', true, false);
INSERT INTO Account VALUES ('huyadmin','TPHCM', '159357', 'John Doe', 'johndoe@example.com', 'default_photo.jpg', true, false);
INSERT INTO account (username, address, password, fullname, email, photo, activated, admin)
VALUES
    ('john_doe', '123 Main St', 'password123', 'John Doe', 'john.doe@example.com', NULL, true, true),
    ('jane_smith', '456 Oak St', 'pass456', 'Jane Smith', 'jane.smith@example.com', NULL, true, false),
    ('bob_jackson', '789 Pine St', 'secret789', 'Bob Jackson', 'bob.jackson@example.com', NULL, false, false);

DELETE FROM `spring-mvc-ecommerce`.Account where username like 'huy';

-- Insert sample categories
INSERT INTO Categories (name) VALUES ('Clothing');
INSERT INTO Categories (name) VALUES ('Art Books');
INSERT INTO Categories (name) VALUES ('Mugs');


-- Insert sample products


-- Insert sample products for Clothing category
INSERT INTO Products (name, image, price, create_date, available, category_id) VALUES
('Studio Ghibli T-shirt', '/images/ghibli_logo_tshirt.jpg', 29.99, NOW(), true,  (SELECT id FROM Categories WHERE name = 'Clothing')),
('Spirited Away Hoodie', '/images/spirited_away_hoodie.jpg', 39.99, NOW(), true, (SELECT id FROM Categories WHERE name = 'Clothing')),
('My Neighbor Totoro Sweatpants', '/images/totoro_sweatpants.jpg', 24.99, NOW(), true, (SELECT id FROM Categories WHERE name = 'Clothing'));

-- Insert sample products for Art Books category
INSERT INTO Products (name, image, price, create_date, available, category_id) VALUES
('The Art of Spirited Away', '/images/spirited_away_art_book.jpg', 49.99, NOW(), true, (SELECT id FROM Categories WHERE name = 'Art Books')),
("Howl's Moving Castle", '/images/howls_castle_art_book.jpg', 59.99, NOW(), true,(SELECT id FROM Categories WHERE name = 'Art Books')),
('Princess Mononoke ', '/images/mononoke_sketchbook.jpg', 34.99, NOW(), true, (SELECT id FROM Categories WHERE name = 'Art Books'));
INSERT INTO Products (name, image, price, create_date, available, category_id) VALUES
('Toroto', '/images/Toroto.jpg', 19.99, NOW(), true, (SELECT id FROM Categories WHERE name = 'Art Books'));

-- Insert sample products for Mugs category
INSERT INTO Products (name, image, price, create_date, available, category_id) VALUES
('Totoro Ceramic Mug', '/images/totoro_ceramic_mug.jpg', 14.99, NOW(), true, (SELECT id FROM Categories WHERE name = 'Mugs')),
('Spirited Away Travel Mug', '/images/spirited_away_travel_mug.jpg', 18.99, NOW(), true,  (SELECT id FROM Categories WHERE name = 'Mugs')),
('Studio Ghibli Coffee Set', '/images/ghibli_coffee_set.jpg', 29.99, NOW(), true,  (SELECT id FROM Categories WHERE name = 'Mugs'));

