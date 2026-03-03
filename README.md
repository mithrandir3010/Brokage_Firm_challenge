# 📈 Brokerage Firm App

A comprehensive full-stack brokerage application built with **Java/Spring Boot** and **React**. This platform allows users to manage their stock portfolios, execute buy/sell orders for Borsa Istanbul (BIST), and track their account balances in real-time.

## 🚀 Features

* **Stock Trading:** Seamless buy and sell operations with real-time balance validation.
* **Portfolio Management:** Detailed view of user assets and transaction history.
* **Role-Based Security:** Secure authentication and authorization using **JWT** with `CUSTOMER` and `ADMIN` roles.
* **Transaction Integrity:** Robust handling of financial operations to ensure data consistency.
* **Testing:** High code reliability ensured through unit tests with **Mockito** and **JUnit 5**.

## 🛠 Tech Stack

* **Backend:** Java 21, Spring Boot, Spring Data JPA, Spring Security (JWT), Hibernate.
* **Frontend:** React, TypeScript, Tailwind CSS.
* **Database:** PostgreSQL.
* **Infrastructure:** Docker, Docker Compose.
* **Testing:** JUnit 5, Mockito.

## 📦 Getting Started

### Prerequisites

* Docker & Docker Compose
* Java 21 or higher
* Node.js (v18+)

### Installation and Running

**1. Clone the repository:**

```bash
git clone https://github.com/mithrandir3010/Brokage_Firm_challenge.git
cd Brokage_Firm_challenge
```

**2. Start the Database (PostgreSQL):**

```bash
cd docker
docker compose up -d
cd ..
```

**3. Run the Backend (Spring Boot):**

```bash
./gradlew bootRun
```

**4. Run the Frontend (React):**

```bash
cd frontend
npm install
npm run dev
```

---

**GitHub:** [@mithrandir3010](https://github.com/mithrandir3010)

**Specialization:** Backend Development | Java & Spring Boot Ecosystem
