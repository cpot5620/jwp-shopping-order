-- purchase_order
INSERT INTO purchase_order (member_id, order_at, payment, used_point, status) VALUES (2, '2023-05-20 12:12:12', 10000, 1000, 'Pending');
INSERT INTO purchase_order (member_id, order_at, payment, used_point, status) VALUES (2, '2023-05-25 14:13:12', 12000, 4000, 'Canceled');
INSERT INTO purchase_order (member_id, order_at, payment, used_point, status) VALUES (2, '2023-05-31 17:11:12', 21000, 2000, 'Pending');
INSERT INTO purchase_order (member_id, order_at, payment, used_point, status) VALUES (2, '2023-05-31 17:11:12', 21000, 2000, 'Pending');
INSERT INTO purchase_order (member_id, order_at, payment, used_point, status) VALUES (2, '2023-06-01 17:11:12', 0, 0, 'Canceled');

-- purchase_order_item
INSERT INTO purchase_order_item (order_id, product_id, name, price, image_url, quantity) VALUES (4, 1, '치킨', 10000, 'testImage1', 2);
INSERT INTO purchase_order_item (order_id, product_id, name, price, image_url, quantity) VALUES (4, 2, '샐러드', 20000, 'testImage2', 4);

INSERT INTO purchase_order_item (order_id, product_id, name, price, image_url, quantity) VALUES (5, 1, '치킨', 10000, 'testImage1', 5);
INSERT INTO purchase_order_item (order_id, product_id, name, price, image_url, quantity) VALUES (5, 3, '피자', 13000, 'testImage3', 3);

INSERT INTO purchase_order_item (order_id, product_id, name, price, image_url, quantity) VALUES (6, 3, '피자', 13000, 'testImage3', 5);
INSERT INTO purchase_order_item (order_id, product_id, name, price, image_url, quantity) VALUES (6, 2, '샐러드', 20000, 'testImage2', 7);

INSERT INTO purchase_order_item (order_id, product_id, name, price, image_url, quantity) VALUES (7, 3, '피자', 13000, 'testImage3', 3);
INSERT INTO purchase_order_item (order_id, product_id, name, price, image_url, quantity) VALUES (7, 2, '샐러드', 20000, 'testImage2', 6);

INSERT INTO purchase_order_item (order_id, product_id, name, price, image_url, quantity) VALUES (8, 3, '피자', 13000, 'testImage3', 3);
INSERT INTO purchase_order_item (order_id, product_id, name, price, image_url, quantity) VALUES (8, 2, '샐러드', 20000, 'testImage2', 6);

-- member_reward_point
INSERT INTO member_reward_point (member_id, point, created_at, expired_at, reward_order_id) VALUES (2, 500, '2023-05-20 12:12:12', '2023-05-25 12:12:12', 4);
INSERT INTO member_reward_point (member_id, point, created_at, expired_at, reward_order_id) VALUES (2, 700, '2023-05-18 12:12:12', '2023-05-29 12:12:12', 5);
INSERT INTO member_reward_point (member_id, point, created_at, expired_at, reward_order_id) VALUES (2, 1000, '2023-05-15 12:12:12', '2023-05-30 12:12:12', 6);
INSERT INTO member_reward_point (member_id, point, created_at, expired_at, reward_order_id) VALUES (2, 1200, '2023-05-15 12:12:12', '2023-06-15 12:12:12', 7);

-- order_member_used_point
INSERT INTO order_member_used_point (order_id, used_reward_point_id, used_point) VALUES (7, 4, 500);
INSERT INTO order_member_used_point (order_id, used_reward_point_id, used_point) VALUES (7, 5, 400);
INSERT INTO order_member_used_point (order_id, used_reward_point_id, used_point) VALUES (7, 6, 1100);

