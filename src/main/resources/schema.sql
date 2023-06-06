DROP TABLE IF EXISTS order_product;
DROP TABLE IF EXISTS order_history;
DROP TABLE IF EXISTS cart_item;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS product;

CREATE TABLE product
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    name       VARCHAR(255) NOT NULL,
    price      INT          NOT NULL,
    image_url  VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE member
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    email      VARCHAR(255) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    point      INT       DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_item
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id  BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity   INT    NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE,
    UNIQUE KEY `member_product_id` (`member_id`, `product_id`)
);

CREATE TABLE order_history
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id    BIGINT NOT NULL,
    total_amount INT    NOT NULL,
    used_point   INT    NOT NULL,
    saved_point  INT    NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE
);

CREATE TABLE order_product
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id        BIGINT       NOT NULL,
    product_id      BIGINT,
    name            VARCHAR(255) NOT NULL,
    image_url       VARCHAR(255) NOT NULL,
    purchased_price INT          NOT NULL,
    quantity        INT          NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (order_id) REFERENCES order_history (id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE SET NULL
);
