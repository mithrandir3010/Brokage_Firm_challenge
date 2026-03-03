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

## 🛠️ Tech Stack

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
├── src/main/java/broker/      # Backend source code
│   ├── controller/            # REST API endpoints
│   ├── service/               # Business logic
│   ├── repository/            # Database access (JPA)
│   ├── model/                 # Entity classes
│   ├── dto/                   # Request/Response DTOs
│   ├── exception/             # Custom exceptions
│   └── security/              # JWT & Security config
├── src/test/java/broker/      # Backend tests
├── frontend/                  # React frontend
│   ├── src/
│   │   ├── components/        # React components
│   │   ├── context/           # Auth context
│   │   ├── services/          # API calls (Axios)
│   │   └── types/             # TypeScript types
│   ├── package.json
│   └── vite.config.ts
├── docker/
│   └── docker-compose.yml     # PostgreSQL container
├── scripts/
│   └── start-all.sh           # Start all services
├── build.gradle               # Backend dependencies
└── README.md
```

## 🚦 Quick Start

### Prerequisites

- **Java 21** - [Download](https://adoptium.net/)
- **Node.js 20+** - [Download](https://nodejs.org/)
- **Docker** - [Download](https://www.docker.com/)

### Option 1: Start All Services (Recommended)

```bash
# Clone the repository
git clone https://github.com/mithrandir3010/Brokage_Firm_challenge.git
cd Brokage_Firm_challenge

# Start all services with one command
./scripts/start-all.sh
```

### Option 2: Manual Setup

#### Step 1: Start Database

```bash
# Using Docker Compose
cd docker
docker compose up -d

# Or manually
docker run -d \
  --name mypostgres \
  -e POSTGRES_USER=myuser \
  -e POSTGRES_PASSWORD=sifre123 \
  -e POSTGRES_DB=mydb \
  -p 5433:5432 \
  postgres:17
```

#### Step 2: Start Backend

```bash
# From project root
./gradlew bootRun
```

Backend will be available at: **http://localhost:8080**

#### Step 3: Start Frontend

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

Frontend will be available at: **http://localhost:3000**

## 🔧 Environment Configuration

### Backend (`application.properties`)

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5433/mydb` | Database URL |
| `spring.datasource.username` | `myuser` | Database username |
| `spring.datasource.password` | `sifre123` | Database password |
| `JWT_SECRET` | (env variable) | JWT signing secret |
| `JWT_EXPIRATION` | `86400000` | Token expiration (24h) |

### Frontend (`.env`)

Copy `.env.example` to `.env` and configure:

```bash
cd frontend
cp .env.example .env
```

| Variable | Default | Description |
|----------|---------|-------------|
| `VITE_BACKEND_URL` | `http://localhost:8080` | Backend URL for proxy |
| `VITE_API_URL` | `http://localhost:8080/api` | API URL for production |

## 🔐 Test Accounts

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| testuser | test123 | ADMIN | Full access to admin panel |
| customer1 | test123 | CUSTOMER | Standard customer account |

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
2. `size × price` amount is blocked from usableSize
3. Order is created with PENDING status

### Order Creation (SELL)
1. Customer's stock balance is checked
2. Sell quantity is blocked from usableSize
3. Order is created with PENDING status

### Order Matching (Admin only)
1. Only PENDING orders can be matched
2. BUY: Stock added to customer, blocked TRY removed
3. SELL: TRY added to customer, blocked stock removed
4. Order status changes to MATCHED

### Order Cancellation
1. Only PENDING orders can be cancelled
2. Blocked amount/quantity returned to usableSize
3. Order status changes to CANCELED

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "broker.service.OrderServiceTest"

# Run with coverage
./gradlew test jacocoTestReport
```

### Test Coverage
- ✅ Unit tests (Service layer)
- ✅ Controller tests (MockMvc)
- ✅ Integration tests (End-to-end)

## 🐳 Docker Commands

```bash
# Start PostgreSQL
docker compose -f docker/docker-compose.yml up -d

# Stop PostgreSQL
docker compose -f docker/docker-compose.yml down

# View logs
docker logs mypostgres

# Access PostgreSQL CLI
docker exec -it mypostgres psql -U myuser -d mydb
```

## 📝 Development

### Backend Hot Reload
Spring Boot DevTools is included. Changes will auto-reload.

### Frontend Hot Reload
Vite provides instant HMR (Hot Module Replacement).

### Code Style
- Backend: Standard Java conventions
- Frontend: ESLint + TypeScript strict mode

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is developed for educational purposes.

---

⭐ Star this repository if you find it helpful!
