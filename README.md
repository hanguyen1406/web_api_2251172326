# API cURL Commands

Replace `<TOKEN>` with the JWT token received from the `/api/auth/login` response.
Base URL: `http://localhost:8080`

## 1. Authentication

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@gmail.com",
    "password": "123456",
    "fullName": "New User",
    "phoneNumber": "0987654321",
    "address": "123 Test Street",
    "city": "Test City",
    "postalCode": "10000"
  }'
```

### Login (Success -> Copy Token)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@gmail.com",
    "password": "123456"
  }'
```
*(Note check `App.java` seeding: Admin email is `admin@gmail.com`, users are `user1@gmail.com` etc. Password seeded is `123456`)*

### Get Current Customer
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <TOKEN>"
```

## 2. Customer Management

### List Customers (Admin only)
```bash
curl -X GET http://localhost:8080/api/customers \
  -H "Authorization: Bearer <TOKEN>"
```

### Get Customer by ID
```bash
curl -X GET http://localhost:8080/api/customers/1 \
  -H "Authorization: Bearer <TOKEN>"
```

### Update Customer
```bash
curl -X PUT http://localhost:8080/api/customers/2 \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Updated Name",
    "phoneNumber": "0999999999",
    "address": "Updated Address",
    "city": "Updated City",
    "postalCode": "99999"
  }'
```

### Get Customer Orders
```bash
curl -X GET "http://localhost:8080/api/customers/2/orders?page=0&size=10" \
  -H "Authorization: Bearer <TOKEN>"
```

## 3. Product Management

### List Products (Public)
```bash
curl -X GET "http://localhost:8080/api/products?page=0&size=10"
```

### Filter Products (Category & Price)
```bash
curl -X GET "http://localhost:8080/api/products?category=Electronics&minPrice=1000000&maxPrice=50000000"
```

### Search Products
```bash
curl -X GET "http://localhost:8080/api/products?search=iPhone"
```

### Get Product by ID
```bash
curl -X GET http://localhost:8080/api/products/1
```

### Create Product (Admin only)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Laptop",
    "description": "Powerful gaming laptop",
    "price": 25000000.0,
    "category": "Electronics",
    "brand": "Asus",
    "stock": 10,
    "imageUrl": "http://example.com/laptop.jpg"
  }'
```

### Update Product (Admin only)
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 13 Pro",
    "description": "Updated description",
    "price": 24000000.0,
    "category": "Electronics",
    "brand": "Apple",
    "stock": 45,
    "imageUrl": "url1",
    "available": true
  }'
```

### Delete Product (Admin only)
```bash
curl -X DELETE http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer <TOKEN>"
```

### Advanced Search
```bash
curl -X GET "http://localhost:8080/api/products/search?keyword=Samsung"
```

## 4. Order Management

### Create Order (User)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
        {
            "product": {"id": 2},
            "quantity": 1
        },
        {
            "product": {"id": 6},
            "quantity": 2
        }
    ],
    "paymentMethod": "cod",
    "shippingAddress": "123 New Address, City",
    "notes": "Deliver during office hours"
  }'
```

### Get Order by ID
```bash
curl -X GET http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer <TOKEN>"
```

### Update Order Status (Admin or User cancel)
```bash
curl -X PUT http://localhost:8080/api/orders/1/status \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "cancelled"
  }'
```

### Pay Order
```bash
curl -X POST http://localhost:8080/api/orders/1/pay \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "payment_method": "card"
  }'
```

### List All Orders by Status (Admin)
```bash
curl -X GET "http://localhost:8080/api/orders?status=pending" \
  -H "Authorization: Bearer <TOKEN>"
```
