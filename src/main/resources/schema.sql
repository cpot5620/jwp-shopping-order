CREATE TABLE if not exists product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price INT NOT NULL,
    image_url VARCHAR(255) NOT NULL
);

CREATE TABLE if not exists member (
     id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
     email VARCHAR(255) NOT NULL UNIQUE,
     password VARCHAR(255) NOT NULL,
     point BIGINT NOT NULL
);

CREATE TABLE if not exists cart_item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

CREATE TABLE if not exists orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    payment BIGINT NOT NULL,
    discount_point BIGINT NOT NULL,
    order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id) ON DELETE CASCADE
);

CREATE TABLE if not exists order_detail (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);
