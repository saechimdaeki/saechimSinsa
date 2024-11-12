# SaechimSinsa API Documentation

## 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API

### GET /store/lowest-category

- **Response**:
  - **200 OK**: 성공적으로 정보를 조회 함
  - **404 NOT_FOUND** : 상품 정보가 존재하지 않음

#### 성공 케이스

```http request
GET /store/lowest-category

Response:
Status: 200 Ok
{
    "items": [
        {
            "category": "TOP",
            "brandName": "C",
            "price": 10000
        },
        {
            "category": "OUTER",
            "brandName": "E",
            "price": 5000
        },
        {
            "category": "PANTS",
            "brandName": "D",
            "price": 3000
        },
        {
            "category": "SNEAKERS",
            "brandName": "A",
            "price": 9000
        },
        {
            "category": "BAG",
            "brandName": "A",
            "price": 2000
        },
        {
            "category": "HAT",
            "brandName": "D",
            "price": 1500
        },
        {
            "category": "SOCKS",
            "brandName": "I",
            "price": 1700
        },
        {
            "category": "ACCESSORY",
            "brandName": "F",
            "price": 1900
        }
    ],
    "totalPrice": 34100
}
```

#### 실패 케이스

```http request
GET /store/lowest-category

Response:
Status: 404 NOT_FOUND
{
    "status": 404,
    "name": "PRODUCT_NOT_FOUND",
    "code": "P001",
    "message": "Product not found check Brand name And category"
}
```

## 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을조회하는 API

### GET /store/lowest-brand

- **Response**:
  - **200 OK**: 성공적으로 정보를 조회 함
  - **404 NOT_FOUND** : 상품 정보가 존재하지 않음

#### 성공 케이스

```http request
GET /store/lowest-brand

Response:
{
    "brand": "D",
    "categories": [
        {
            "category": "TOP",
            "price": 10100
        },
        {
            "category": "OUTER",
            "price": 5100
        },
        {
            "category": "PANTS",
            "price": 3000
        },
        {
            "category": "SNEAKERS",
            "price": 9500
        },
        {
            "category": "BAG",
            "price": 2500
        },
        {
            "category": "HAT",
            "price": 1500
        },
        {
            "category": "SOCKS",
            "price": 2400
        },
        {
            "category": "ACCESSORY",
            "price": 2000
        }
    ],
    "totalPrice": 36100
}
```

#### 실패 케이스

```http request
GET /store/lowest-brand

Response:
{
    "status": 404,
    "name": "NO_BRAND_HAS_ALL_CATEGORIES",
    "code": "P006",
    "message": "No Brand has all categories check Data"
}
```

## 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API

### GET /store/category?category={category}

- **Request Parameters**:
  - `category` (String) : 카테고리 이름
- **Response**:
  - **200 OK**: 성공적으로 정보를 조회 함
  - **400 BAD_REQUEST**: 카테고리 정보 입력이 잘못 되었음
  - **404 NOT_FOUND** : 상품 정보가 존재하지 않음
#### 성공 케이스

```http request
GET /store/category?category=hat

Response:
{
    "category": "HAT",
    "lowestPrice": [
        {
            "brandName": "D",
            "price": 1500
        }
    ],
    "highestPrice": [
        {
            "brandName": "B",
            "price": 2000
        }
    ]
}
```

#### 실패 케이스

```http request
GET /store/category?category=abcd

Response:
{
    "status": 400,
    "name": "INVALID_CATEGORY",
    "code": "P003",
    "message": "Invalid category value"
}
```

## 브랜드 생성 API


### POST /store/brand
- **Request Body**:
  - `brandName` (String): 브랜드 이름 (필수)
  - `List<Product> products` (필수 X)
    - `brandName` (String): 브랜드 이름
    - `category` (String): 카테고리 (대소문자 구분 X)
    - `price` (Long) : 가격
- **Response**:
  - **201 CREATED**: 성공적으로 데이터를 저장함
  - **400 BAD_REQUEST**: 입력 포맷이 잘못되었음

#### 성공 케이스1

```http request
POST /store/brand

Request:
{
    "brandName": "A"
}

Response:
{
    "brandName": "A",
    "products": []
}
```

#### 성공 케이스2

```http request
POST /store/brand

Request:
{
    "brandName": "A",
    "products":[
        {
            "brandName":"A",
            "category":"hat",
            "price":1000
        }
    ]
}

Response:
{
    "brandName": "A",
    "products": [
        {
            "brandName": "A",
            "category": "HAT",
            "price": 1000
        }
    ]
}
```

#### 실패 케이스

```http request
POST /store/brand

Request:
{
    "brandName": "",
    "products":[
        {
            "brandName":"A",
            "category":"hat",
            "price":1000
        }
    ]
}

{
    "status": 400,
    "name": "brand name is required",
    "code": "BindException",
    "message": "brandName"
}
```

## 상품 생성 API

### POST /store/product

- **Request Body**:
  - `brandName` (String): 브랜드 이름 (필수)
  - `category` (String) : 카테고리 이름(필수)
  - `price` (Long) : 가격 (필수)

- **Response**:
  - **201 CREATED**: 성공적으로 데이터를 저장함
  - **400 BAD_REQUEST**: 입력 포맷이 잘못되었음

#### 성공 케이스

```http request
POST /store/product

Request:
{
    "brandName": "A",
    "category":"hat",
    "price":1
}

Response:
{
    "category": "HAT",
    "brandName": "A",
    "price": 1
}
```

#### 실패 케이스

```http request
POST /store/product

Request:
{
    "brandName": "A",
    "category": "123",
    "price":1
}

Response:
{
    "status": 400,
    "name": "INVALID_CATEGORY",
    "code": "P003",
    "message": "Invalid category value"
}
```

## 상품 수정 API

### PUT /store/brand/{brandName}

- **Request Body**:
  - `brandName` (String): 브랜드 이름 (필수)
  - `category` (String) : 카테고리 이름(필수)
  - `price` (Long) : 가격 (필수)

- **Response**:
  - **200 OK**: 성공적으로 데이터를 수정함
  - **400 BAD_REQUEST**: 입력 포맷이 잘못되었음
  - **404 NOT_FOUND**: 상품이나 브랜드 정보를 찾을 수 없음

#### 성공 케이스

```http request
PUT /store/brand/A

Request:
{
    "brandName": "A",
    "category":"hat",
    "price":1
}

Response:
{
    "category": "HAT",
    "brandName": "A",
    "price": 1
}
```

#### 실패 케이스

```http request
PUT /store/brand/PP

Request:
{
    "brandName": "A",
    "category":"hat",
    "price":1
}

Response:
{
    "status": 404,
    "name": "BRAND_NOT_FOUND",
    "code": "P001",
    "message": "Brand not found check Brand name"
}
```

## 브랜드 삭제 API

### DEL /store/brand/{brandName}

- **Response**:
  - **204 OK**: 성공적으로 데이터를 삭제함
  - **400 BAD_REQUEST**: 입력 포맷이 잘못되었거나 존재하지 않는 데이터를 접근하려함
  - **404 NOT_FOUND**: 상품이나 브랜드 정보를 찾을 수 없음

#### 성공 케이스

```http request
DELETE /store/brand/A

Response: 204 no content
```

#### 실패 케이스

```http request
DELETE /store/brand/PPP

Response:
{
    "status": 400,
    "name": "NO_BRAND_DELETED",
    "code": "P005",
    "message": "No Brand deleted check Data"
}
```

## 상품 삭제 API

### DEL /store/brand/{brandName}/category/{categoryName}

- **Response**:
  - **204 OK**: 성공적으로 데이터를 삭제함
  - **400 BAD_REQUEST**: 입력 포맷이 잘못되었거나 존재하지 않는 데이터를 접근하려함
  - **404 NOT_FOUND**: 상품이나 브랜드 정보를 찾을 수 없음

#### 성공 케이스

```http request
DELETE /store/brand/B/category/hat

Response: 204 No Content
```


#### 실패 케이스

```http request
DELETE /store/brand/B/category/hat

{
    "status": 404,
    "name": "PRODUCT_NOT_FOUND",
    "code": "P001",
    "message": "Product not found check Brand name And category"
}

```