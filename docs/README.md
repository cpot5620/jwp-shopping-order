## 도메인

### 회원

- [X] 회원의 이름은 4글자 이상, 10글자 이하여야 한다.
- [X] 회원의 비밀번호는 4글자 이상, 10글자 이하여야 한다.
- [X] 회원의 이름은 중복될 수 없다.
- 
### 상품

- [X] 상품의 이름은 1글자 이상, 20글자 이하여야 한다.
- [X] 상품의 가격은 1 ~ 10,000,000원 이여야 한다.

### 장바구니

- [X] 하나의 상품을 1 ~ 1,000개 까지 담을 수 있다.

### 쿠폰

- [X] 쿠폰의 이름은 1글자 이상, 50글자 이하여야 한다.
- [X] 쿠폰의 할인율은 5 ~ 90%여야 한다.
- [X] 쿠폰의 사용 가능 기간은 1 ~ 365일이다.
- [X] 쿠폰은 최대 만료일자가 존재한다.

### 금액

- [X] 쿠폰을 적용한 금액의 소숫점은 버린다.

## API 명세

## 상품

- [X] 상품 목록 조회

### Request

```
GET /products HTTP/1.1
```

### Response

``` json
HTTP/1.1 200 OK
Content-Type: application/json

[
    {
        "id": 1,
        "name": "치킨",
        "price": 10000,
        "imageUrl": "http://example.com/chicken.jpg"
    },
    {
        "id": 2,
        "name": "피자",
        "price": 20000,
        "imageUrl": "http://example.com/pizza.jpg"
    }
]

```
---
- [X] 상품 조회

### Request

```
GET /products/{productId} HTTP/1.1
```

### Response

``` json
HTTP/1.1 200 OK
Content-Type: application/json

{
    "id": 1,
    "name": "치킨",
    "price": 10000,
    "imageUrl": "http://example.com/chicken.jpg"
}
```
---
- [X] 상품 추가

### Request

``` json
POST /products HTTP/1.1
Content-Type: application/json

{
    "name": "부리또",
    "price": 30000,
    "imageUrl": "http://example.com/burrito.jpg"
}

```

### Response

```
HTTP/1.1 201 Created
Location: /products/{productId}
```
---
- [X] 상품 수정

### Request

``` json
PUT /products/{productId} HTTP/1.1
Content-Type: application/json

{
    "name": "부리또",
    "price": 30000,
    "imageUrl": "http://example.com/burrito.jpg"
}

```

### Response

```
HTTP/1.1 200 OK
```
---
- [X] 상품 삭제

### Request

``` 
DELETE /products/{productId} HTTP/1.1
```

### Response

```
HTTP/1.1 204 No Content
```

---

## 장바구니

- [X] 장바구니 아이템 목록 조회

### Request

``` 
GET /shop-items HTTP/1.1
Authorization: Basic ${credentials}
```

### Response

``` json
HTTP/1.1 200 OK
Content-Type: application/json

[
    {
        "id": 1,
        "quantity": 5,
        "product": {
            "id": 1,
            "price": 10000,
            "name": "치킨",
            "imageUrl": "http://example.com/chicken.jpg"
        }
    },
    {
        "id": 2,
        "quantity": 1,
        "product": {
            "id": 2,
            "price": 20000,
            "name": "피자",
            "imageUrl": "http://example.com/pizza.jpg"
        }
    }
]

```
---
- [X] 장바구니 아이템 추가

### Request

```
POST /shop-items HTTP/1.1
Authorization: Basic ${credentials}
Content-Type: application/json

{
    "productId": 1
}
```

### Response

```
HTTP/1.1 201 Created
Location: /shop-items/{cartItemId}
```
---
- [X] 장바구니 아이템 수량 변경

### Request

``` json
PATCH /shop-items/{cartItemId} HTTP/1.1
Authorization: Basic ${credentials}
Content-Type: application/json

{
    "quantity": 3
}
```

### Response

```
HTTP/1.1 200 OK
```
---
- [X] 장바구니 아이템 삭제

### Request

```
DELETE /shop-items/{cartItemId}
Authorization: Basic ${credentials}
```

### Response

```
HTTP/1.1 204 No Content
```
---
- [X] 회원 가입

### Request

``` json
POST /users/join HTTP/1.1
Content-Type: application/json

{
    "name": "jourzura",
    "password": "1q2w3e4r!"
}
```

``` json
HTTP/1.1 200 OK
```
---
- [X] 로그인

### Request

``` json
POST /users/login HTTP/1.1
Content-Type: application/json

{
	"name": "jourzura",
	"password": "1q2w3e4r!"
}
```

### Response

``` json
HTTP/1.1 200 OK

{
	"token": "encodedBySha256Password"
}
```
---
- [X] 내 쿠폰함 조회

### Request

```
GET /users/me/coupons HTTP/1.1
Authorization: Basic 93290uf9ewfiod
```

### Response

``` json
HTTP/1.1 200 OK
Content-Type: application/json
[
    {
        "id": 1,
        "name": "신규 가입 쿠폰",
        "discountRate": 20,
        "expiredAt": "2023-05-30T15:30:44"
    },
    {
    	"id": 2,
        "name": "첫 주문 감사 쿠폰",
        "discountRate": 15,
        "expiredAt": "2023-05-30T15:30:44"
    }
]
```
---
- [X] 상품 주문

### Request

``` json
POST /orders HTTP/1.1
Authorization: Basic 93290uf9ewfiod
Content-Type: application/json

{
    items: [
        { 
            "id": 1,
            "quantity": 5
            		
        },
        {
            "id": 2,
            "quantity": 100
        }
    ],
    couponId: 1
}
```

### Response

```
HTTP/1.1 201 OK
Location: /orders/{orderId}
```
---
- [X] 상품 주문 조회

### Request

```
GET /orders HTTP/1.1
Authorization: Basic 93290uf9ewfiod
```

### Response

``` json
HTTP/1.1 200 OK
[
    {
        "orderId": 1,
        "items": [
                    {
                        "product": {
                            "id": 1,
                            "name": "하이",
                            "price": 1000,
                            "imagaeUrl": "sample"
                        },
                        "quantity": 20
                    },
					{
                        "product": {
                            "id": 2,
                            "name": "하이2",
                            "price": 2000,
                            "imagaeUrl": "sample"
                        },
                        "quantity": 20
                    }
                ]
    },
    {
        "orderId": 2,
        "items": [
                    {
                        "product": {
                            "id": 3,
                            "name": "하이3",
                            "price": 1000,
                            "imagaeUrl": "sample"
                        },
                        "quantity": 20,
                    },
                    {
                        "product": {
                            "id": 4,
                            "name": "하이4",
                            "price": 2000,
                            "imagaeUrl": "sample"
                        },
                        "quantity": 30,
                    }
                ]
    }
]
```
---
- [ ] 단일 상품 주문 상세 보기

### Request

```
GET /orders/{orderId} HTTP/1.1
Authorization: Basic 93290uf9ewfiod
```

### Response

``` json
HTTP/1.1 200 OK

{
    "orderId": 1,
    "coupon": {
        "name": "쿠폰 이름임",
        "discountRate": 10
    },
    "items": [
        {
            "proudct": {
                "id": 1,
                "name": "하이",
                "price": 1000,
                "imageUrl": "sample"
            },
            "qunatity": 20
        },
        {
            "proudct": {
                "id": 2,
                "name": "하이2",
                "price": 1000,
                "imageUrl": "sample"
            },
            "qunatity": 20
        }
    ],
    "totalPrice": 40000,
    "discountedTotalPrice": 36000,
    "couponDiscountPrice": 4000,
    "deliveryPrice": 3000,
    "orderedAt": "2023-05-30T15:30:44"
}
```
