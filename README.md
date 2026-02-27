# BrokerApp - Brokerage Firm Application

A full-stack brokerage application for managing stock buy/sell orders on Borsa Istanbul (BIST).

## 🚀 Features

### Customer Operations
- ✅ User registration and JWT-based authentication
- ✅ TRY balance management (deposit/withdraw)
- ✅ Create stock buy/sell orders
- ✅ View order list and history
- ✅ Cancel pending orders
- ✅ Portfolio and asset tracking

### Admin Operations
- ✅ List and manage all customers
- ✅ Deposit money to customer accounts
- ✅ Match pending orders
- ✅ System-wide order monitoring

### Security
- ✅ JWT (JSON Web Token) authentication
- ✅ BCrypt password hashing
- ✅ Role-based authorization (CUSTOMER/ADMIN)
- ✅ Pessimistic locking for data consistency
- ✅ BigDecimal for precise financial calculations

## 🛠️ Technologies

### Backend
| Technology | Version |
|------------|---------|
| Java | 21 |
| Spring Boot | 4.0.0 |
| Spring Security | 6.x |
| Spring Data JPA | 3.x |
| PostgreSQL | 17 |
| JWT (jjwt) | 0.12.5 |
| Lombok | - |
| Gradle | 8.x |

### Frontend
| Technology | Version |
|------------|---------|
| React | 19.x |
| TypeScript | 5.x |
| Vite | 7.x |
| Tailwind CSS | 4.x |
| Axios | 1.x |
| React Router | 7.x |

## 📁 Project Structure

```
Brokage_Firm_challenge/
├── src/main/java/broker/
│   ├── controller/          # REST API endpoints
│   │   ├── AuthController.java
│   │   ├── CustomerController.java
│   │   ├── OrderController.java
│   │   └── AssetController.java
│   ├── service/             # Business logic
│   │   ├── CustomerService.java
│   │   ├── OrderService.java
│   │   └── AssetService.java
│   ├── repository/          # Database access
│   ├── model/               # Entity classes
│   │   ├── Customer.java
│   │   ├── Order.java
│   │   └── Asset.java
│   ├── dto/                 # Request/Response DTOs
│   ├── exception/           # Custom exception classes
│   └── security/            # JWT and security configuration
├── frontend/
│   ├── src/
│   │   ├── components/      # React components
│   │   ├── context/         # Auth context
│   │   ├── services/        # API calls
│   │   └── types/           # TypeScript types
│   └── ...
├── docker/
│   └── docker-compose.yml   # PostgreSQL container
└── build.gradle
```

## 🚦 Installation & Setup

### Prerequisites
- Java 21
- Node.js 20+
- Docker

### 1. Start Database

```bash
cd docker
docker compose up -d
```

Or manually:

```bash
docker run -d \
  --name mypostgres \
  -e POSTGRES_USER=myuser \
  -e POSTGRES_PASSWORD=sifre123 \
  -e POSTGRES_DB=mydb \
  -p 5433:5432 \
  postgres:17
```

### 2. Start Backend

```bash
./gradlew bootRun
```

Backend will run at http://localhost:8080

### 3. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend will run at http://localhost:3000

## 🔐 Test Accounts

| Username | Password | Role |
|----------|----------|------|
| testuser | test123 | ADMIN |
| customer1 | test123 | CUSTOMER |

## 📡 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Orders
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/orders` | List orders | USER |
| POST | `/api/orders` | Create new order | USER |
| DELETE | `/api/orders/{id}` | Cancel order | USER |
| POST | `/api/orders/{id}/match` | Match order | ADMIN |

### Assets
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/assets` | List assets | USER |
| POST | `/api/assets/deposit` | Deposit money | ADMIN |
| POST | `/api/assets/withdraw` | Withdraw money | USER |

### Customers
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/customers` | List all customers | ADMIN |
| GET | `/api/customers/{id}` | Get customer details | USER |
| DELETE | `/api/customers/{id}` | Delete customer | ADMIN |

## 💼 Business Rules

### Order Creation (BUY)
1. Customer's TRY balance is checked
2. `size × price` amount is deducted from usableSize (blocked)
3. Order is created with PENDING status

### Order Creation (SELL)
1. Customer's stock balance is checked
2. Sell quantity is deducted from usableSize (blocked)
3. Order is created with PENDING status

### Order Matching
1. Only PENDING orders can be matched
2. BUY: Stock is added to customer, blocked TRY is removed
3. SELL: TRY is added to customer, blocked stock is removed
4. Order status changes to MATCHED

### Order Cancellation
1. Only PENDING orders can be cancelled
2. Blocked amount/quantity is added back to usableSize
3. Order status changes to CANCELED

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "broker.service.OrderServiceTest"
```

### Test Coverage
- ✅ Unit tests (Service layer)
- ✅ Controller tests
- ✅ Integration tests (End-to-end scenarios)

## 📸 Screenshots

### Home Page
Modern and professional design with stock market themed background.

### Dashboard
- TRY balance summary
- Total asset count
- Pending order count
- Recent transactions

### Order Creation
- Popular stock selection
- Buy/Sell toggle
- Real-time total calculation

## 📝 License

This project is developed for educational purposes.

---

⭐ Star this repository if you find it helpful!
