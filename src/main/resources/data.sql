INSERT INTO product (name, price, discount_price, image_url)
VALUES ('뤀치킨', 10000, 1000,
        'https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80');
INSERT INTO product (name, price, discount_price, image_url)
VALUES ('뤀샐러드', 20000, 1000,
        'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80');
INSERT INTO product (name, price, discount_price, image_url)
VALUES ('뤀피자', 13000, 3000,
        'https://images.unsplash.com/photo-1595854341625-f33ee10dbf94?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80');

INSERT INTO member (email, password)
VALUES ('a@a.com', '1234');
INSERT INTO member (email, password)
VALUES ('b@b.com', '1234');

INSERT INTO coupon (id, name, discount_policy_name, discount_value, category)
VALUES (1,'5000원 할인 쿠폰','price',5000, 'single');
INSERT INTO coupon (id, name, discount_policy_name, discount_value, category)
VALUES (2, '전체 1000원 할인 쿠폰', 'price', 1000, 'all');
INSERT INTO coupon (id, name, discount_policy_name, discount_value, category)
VALUES (3, '전체 10퍼센트 할인 쿠폰', 'percent', 10, 'all');

INSERT INTO coupon_box (member_id, coupon_id)
VALUES (1, 1);
INSERT INTO coupon_box (member_id, coupon_id)
VALUES (1, 2);
INSERT INTO coupon_box (member_id, coupon_id)
VALUES (1, 3);

INSERT INTO cart_item (member_id, product_id, quantity)
VALUES (1, 1, 1);
INSERT INTO cart_item (member_id, product_id, quantity)
VALUES (1, 2, 1);
INSERT INTO cart_item (member_id, product_id, quantity)
VALUES (1, 3, 1);