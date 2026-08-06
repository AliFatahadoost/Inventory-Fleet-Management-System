# Inventory & Fleet Management System

<div align="center">

![Version](https://img.shields.io/badge/version-1.0-blue.svg)
![Java](https://img.shields.io/badge/Java-1.8+-orange.svg)
![SQL Server](https://img.shields.io/badge/SQL%20Server-2019+-red.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)
![Build](https://img.shields.io/badge/build-Maven-brightgreen.svg)
![Status](https://img.shields.io/badge/status-active-brightgreen.svg)

**A full‑stack, zero‑dependency inventory and fleet management platform built from the ground up.**

[Key Features](#-key-features) •
[Tech Stack](#-tech-stack) •
[Quick Start](#-quick-start) •
[Architecture](#-architecture) •
[Documentation](#-documentation)

</div>

---

## 📖 Overview

The **Inventory & Fleet Management System (IFMS)** is a complete business management solution designed for companies that need to track inventory across multiple warehouses, manage vehicle fleets, assign tasks to drivers, and monitor user activity — all in one place.


Built primarily with core Java and minimal dependencies to better understand HTTP serving, authentication, authorization, connection pooling, and business application architecture. (except the SQL Server JDBC driver), this project demonstrates pure, hand‑crafted software engineering. No Spring, no React, no Angular, no Bootstrap — just raw **HTML**, **CSS**, **JavaScript**, **Java**, and **SQL**. allthough i will add Frame Works in the Later Versions To shows i can use industry Standard tools

> ⚡ **Philosophy**: Write everything yourself. Understand every layer. Build something that works.

---

## ✨ Key Features

### 📦 Inventory Management
- **Product catalog** — Create, edit, delete, and search products with category assignment
- **Product categories** — Organise items with custom categories
- **Stock levels** — Real‑time visibility of inventory across multiple warehouse locations
- **Low‑stock alerts** — Dashboard shows products below threshold (5 units)
- **Stock requests** — Request stock transfers between locations, with approval workflow
- **Live dashboard** — Key metrics at a glance: total products, categories, locations, active movements

### 🚚 Fleet Management
- **Vehicle management** — Add, edit, and delete vehicles with licence plates
- **Driver management** — Manage driver profiles with contact details
- **Trip management** — Create and track product movement trips between locations
- **Route tracking** — Assign drivers and vehicles to trips with estimated arrival times

### 👥 User & Access Control
- **User management** — Create user accounts, change passwords, assign roles
- **Role‑based access control (RBAC)** — Granular permissions: `Can Read` / `Can Write` per page
- **Access codes** — Every page has a unique `data-AccessCode` attribute for fine‑grained control
- **Activity logging** — Full audit trail of all user actions

### 📊 Reporting & Analytics
- **Activity Log** — View all user actions with filters (date, user, remarks)
- **Work Hours Report** — Track time spent on tasks, calculate earnings
- **Tasks Report** — View and manage user tasks with Start / Finish actions
- **Login History** — See when users logged in, with time‑of‑day breakdown
- **Role Assignment History** — Track when roles were assigned to users

### 🎨 User Experience
- **Dark / Light mode** — Toggle themes with persistent preference
- **Tab‑based interface** — Multi‑tab browsing within the dashboard
- **Responsive sidebar** — Collapsible navigation with hover reveal
- **Real‑time clock** — Live date/time display in the header

---

## 🧰 Tech Stack

| Layer          | Technology                                                                 |
|----------------|----------------------------------------------------------------------------|
| **Frontend**   | HTML5, CSS3, Vanilla JavaScript (no frameworks)                            |
| **Backend**    | Java 1.8+ (no frameworks — pure `com.sun.net.httpserver`)                  |
| **Database**   | Microsoft SQL Server 2019+                                                 |
| **Build Tool** | Maven                                                                      |
| **JDBC**       | `mssql-jdbc` (the only external dependency)                               |
| **Auth**       | Token‑based session management (HTTP‑only cookies)                         |
| **Architecture** | Monolithic with modular separation (page handlers + API handlers)        |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CLIENT (Browser)                            │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │   Login.html  →  Dashboard.html  →  IFrame‑based pages       │  │
│  └───────────────────────────────────────────────────────────────┘  │
└─────────────────────────────┬───────────────────────────────────────┘
                              │ HTTP (REST‑style)
┌─────────────────────────────▼───────────────────────────────────────┐
│                      HTTP SERVER (Java)                             │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │              readConfig.launchHttpServer()                     │  │
│  │  • Context: /          → Login.html                           │  │
│  │  • Context: /Dashboard → Dashboard.html                       │  │
│  │  • Context: /API/*     → Various API handlers                 │  │
│  └───────────────────────────────────────────────────────────────┘  │
└─────────────────────────────┬───────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────────────┐
│                      APPLICATION LAYER                              │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │  pageHandlerOpener    → Serves HTML, filters by access rights │  │
│  │  apiManagement        → Handles API requests (POST only)      │  │
│  │  webServerUtils       → Cookie parsing, JSON parsing, etc.    │  │
│  └───────────────────────────────────────────────────────────────┘  │
└─────────────────────────────┬───────────────────────────────────────┘
                              │
┌─────────────────────────────▼───────────────────────────────────────┐
│                      DATA ACCESS LAYER                              │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │  dataBaseManager    → Connection pooling (hand‑rolled)        │  │
│  │  dataBaseUtils      → Executes queries, returns JSON          │  │
│  └───────────────────────────────────────────────────────────────┘  │
└─────────────────────────────┬───────────────────────────────────────┘
                              │ JDBC
┌─────────────────────────────▼───────────────────────────────────────┐
│                   MICROSOFT SQL SERVER                              │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │  Stored Procedures: LOGIN_VALID, SIGNUP_NEWUSER,             │  │
│  │  IS_AUTHENTICATED, IS_ALLOWED_READ, TASKS_REPORT, etc.      │  │
│  └───────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
```

### 🔐 Authentication & Authorisation Flow

1. **Login** → User submits credentials → `LOGIN_VALID` SP returns a session token
2. **Token** → Stored as `HttpOnly` cookie (valid for `MAX_SESSION_TIME` seconds)
3. **Page Access** → `pageHandlerOpener` checks `IS_AUTHENTICATED(token)` and `IS_ALLOWED_READ(token, accessCode)`
4. **API Access** → All API endpoints validate the token before executing queries
5. **Filtering** → HTML elements with `data-AccessCode` are filtered out if the user lacks permission

---

## 📁 Project Structure

```
Inventory-Fleet-Management-System/
│
├── ClientSide/                           # All front‑end files
│   ├── Login/
│   │   └── Login.html                    # Authentication page
│   │
│   ├── Dashboard/
│   │   ├── Dashboard.html                # Main application shell
│   │   ├── dashboardSubSections/
│   │   │   ├── Home.html                 # Landing page
│   │   │   ├── userProfile/              # User profile, tasks, work hours, activity log
│   │   │   ├── inventoryManagement/      # Products, categories, stock levels, requests
│   │   │   ├── userManagement/           # Users, roles, permissions
│   │   │   ├── fleetManagement.html      # Fleet management (WIP)
│   │   │   ├── warehouseManagement.html  # Warehouse management (WIP)
│   │   │   └── reportSection.html        # Reports (WIP)
│   │   └── [assets/]                     # (optional)
│   │
│   └── [config.txt]                      # Generated on first run
│
├── src/
│   └── main/
│       └── java/
│           └── IFMS/                     # All Java source files
│               ├── mainServerLaunch.java         # Entry point
│               ├── readConfig.java               # Configuration + HTTP server
│               ├── pageHandlerOpener.java        # Page serving + access filtering
│               ├── apiManagement.java            # API endpoint handlers
│               ├── webServerUtils.java           # Utilities (cookie, JSON, etc.)
│               ├── dataBaseManager.java          # Connection pooling
│               ├── dataBaseUtils.java            # Query execution → JSON
│               └── [IFMS.class]                  # (compiled)
│
├── pom.xml                               # Maven configuration
└── README.md                             # This file
```

---

## 🚀 Quick Start

### Prerequisites

| Requirement | Minimum Version |
|-------------|-----------------|
| Java JDK    | 1.8             |
| Maven       | 3.6+            |
| SQL Server  | 2019+ (or Express) |
| OS          | Windows / Linux / macOS |

### 1. Clone the Repository

```bash
git clone https://github.com/AliFatahadoost/Inventory-Fleet-Management-System.git
cd Inventory-Fleet-Management-System
```

### 2. Set Up the Database

Run the `dataBaseScript.sql` script (provided in the repository) on your SQL Server instance. This creates:

- All tables (`SYS_USERS`, `PRODUCTS`, `INVENTORY_LOCATION`, `VEHICLES`, `TASKS`, etc.)
- All stored procedures (`LOGIN_VALID`, `IS_AUTHENTICATED`, `TASKS_REPORT`, etc.)
- Default roles and permissions --> will be added soon

### 3. Configure the Application

Instead of forcing you to manually edit a config.txt file (or hardcode values), the server gives you three ways to start up when you run the JAR:
🚀 The Launcher Modes (in readConfig.initiate())

When you run java -jar IFMS-1.0-SNAPSHOT.jar, you see this prompt:
text

Launch GUI server configuration? (Y/n/s S stands for start with no questions):

1. GUI Launcher (Press Y or just Enter)

A Swing-based configuration window pops up. It looks like a desktop app and lets you:

    Edit all server settings visually (DB host, port, username, password, HTTP port, server IP, file paths).

    Test the database connection with a single click.

    Save the config to config.txt.

    Launch the HTTP server directly from the GUI.

    It even has a live output console to show logs and errors.

This is fully cross-platform because Swing runs on Windows, macOS, and Linux without any extra dependencies.
2. Console Launcher (Press n)

If you're on a headless server (no GUI), you get a command-line interface:
text

> help
Commands:
  get <property>        - show a property value
  set <property> <val>  - change a property
  load                  - reload from config.txt
  save                  - save to config.txt
  testdb                - test database connection
  launch                - start the HTTP server
  end / exit            - shut down server and exit

You can configure everything via CLI, test the DB, and then launch the server — all without ever touching a text editor.
3. Silent Launch (Press s)

Skips all questions and immediately launches the HTTP server using the existing config.txt (or defaults). Perfect for production scripts or Docker containers.
🔧 Why This Design Matters
Feature	Benefit
No manual config editing	The GUI/CLI writes config.txt for you, avoiding syntax errors.
Cross‑platform	Pure Java (Swing + Scanner) works everywhere.
Production‑ready	The "silent" mode makes it easy to integrate with systemd or Docker.
Runtime safety	The launcher disables config changes while the server is running (prevents corruption).
Self‑contained	No need for a separate installer or setup script — the JAR is the launcher.
> **Note:** The `BASE_FILE_ADDRESS` is relative to the JAR's location. Adjust accordingly.

### 4. Build & Run

```bash
# Build the JAR file
mvn clean package

# Run the application
java -jar target/IFMS-1.0-SNAPSHOT.jar
```

### 5. Launch the Application

Open your browser and go to:

```
http://127.0.0.1:55952 --> this is default so if you had something on 8080 it wouldn't fail
```

Default credentials (after running the database script):
- **Username:** `admin`
- **Password:** `12`

> **Important:** Change the default password immediately after first login.

---

## 🧩 Backend API Endpoints

All endpoints are `POST`-only and require a valid session token (sent via `Cookie: token=...`).

| Endpoint                                | Description                                  |
|-----------------------------------------|----------------------------------------------|
| `/Login`                                | Authentication (login / signup)              |
| `/selectUserLoginLogAPI`                | Get login history for the current user       |
| `/selectUserRolesLogAPI`                | Get role assignment history                  |
| `/changeUserName`                       | Change the current user's username           |
| `/changePassword`                       | Change the current user's password           |
| `/tasksReportAPI`                       | Get all tasks assigned to the user           |
| `/changeTaskStatus`                     | Start or finish a task                       |
| `/workHourReportAPI`                    | Get work hour reports with earnings          |
| `/auditLogAPI`                          | Get full activity log (admin only)           |
| `/userListAPI`                          | List all users (admin only)                  |
| `/usersRoleListAPI`                     | List users with their roles                  |
| `/roleListAPI`                          | List all available roles                     |
| `/createNewUserAPI`                     | Create a new user (admin only)               |
| `/addRevokeRolesAPI`                    | Assign or revoke a role (admin only)         |
| `/updateUserCredByAdmin`                | Update another user's credentials (admin)    |
| `/rolesAndPermissionsListAPI`           | List all roles and permissions               |
| `/featuresListAPI`                      | List all system features (pages)             |
| `/createUpdateDeleteRolesAPI`           | Create, update, or delete a role (admin)     |
| `/manageProductsMovementTripsAPI`       | Create/update/delete movement trips          |
| `/manageInventoryInfstructureAPI`       | Manage products, categories, locations       |
| `/createUpdateDeleteVehicleAPI`         | Manage vehicles                              |
| `/createUpdateDeleteInventoryLocationAPI`| Manage warehouses/locations                  |
| `/createUpdateDeleteDriverAPI`          | Manage drivers                               |
| `/productsCountAPI`                     | Total product count                          |
| `/productsCategoryCountAPI`             | Total category count                         |
| `/inventoryLocationCountAPI`            | Total location count                         |
| `/activeMovementsCountAPI`              | Active movement trips count                  |
| `/allMovementsCountAPI`                 | All movement trips count                     |
| `/lowStockProductsAPI`                  | Products with stock below 5 units            |
| `/productsCategoryListAPI`              | List all product categories                  |
| `/productsWithCategoryAPI`              | List products with category names            |
| `/handleInventoryRequestAPI`            | Create or accept stock transfer requests     |
| `/inventoryLocationsWithTypeAPI`        | List all inventory locations with types      |
| `/inventoryStockRequestsAPI`            | List all stock requests                      |
| `/productStocksAPI`                     | List all product stock levels                |

---

## 🔒 Access Control & Permissions

### How It Works

1. Every HTML element that should be protected has a `data-AccessCode` attribute:
   ```html
   <div data-AccessCode="5" data-direction="userProfile">
       <p>User Profile</p>
   </div>
   ```

2. The `pageHandlerOpener.filterHtmlByAccess()` method scans the HTML and checks `IS_ALLOWED_READ(token, accessCode)`.

3. If the user lacks permission, the element (and its children) are **removed** from the response.

### Pre‑defined Access Codes

| Code | Page / Feature                      |
|------|-------------------------------------|
| 1    | Login page                          |
| 2    | Login page (redirect)               |
| 3    | Dashboard shell                     |
| 4    | Home page                           |
| 5    | User Profile                        |
| 6    | Tasks                               |
| 7    | Work Hour Reports                   |
| 8    | Activity Log                        |
| 9    | Inventory Management                |
| 10   | Warehouse Management                |
| 11   | Fleet Management                    |
| 12   | Reports Section                     |
| 13   | Users Management                    |
| 14   | Edit Users Form                     |
| 15   | Edit Roles Form                     |
| 16   | Products                            |
| 17   | Product Categories                  |
| 18   | Stock Levels                        |
| 19   | Inventory Users Tasks               |
| 20   | Products Movement & Log             |

---

## 🛠️ Development

### Building from Source

```bash
mvn clean compile
mvn package
```

### Running in Development Mode

```bash
mvn exec:java -Dexec.mainClass="IFMS.mainServerLaunch"
```

### Adding a New Page

1. Create an HTML file in the appropriate `ClientSide/Dashboard/dashboardSubSections/` folder.
2. Add a new context mapping in `readConfig.launchHttpServer()`:
   ```java
   server.createContext("/Dashboard/yourPage", new pageHandlerOpener(
       BASE_FILE_ADDRESS + "/Dashboard/dashboardSubSections/yourPage.html", true, accessCode));
   ```
3. Add the page to your sidebar with the matching `data-AccessCode`.

### Adding a New API Endpoint

1. In `apiManagement.java`, create a static inner class that implements `HttpHandler`.
2. In `readConfig.launchHttpServer()`, add a context mapping to your new handler.
3. Write the corresponding stored procedure in SQL Server.

---

## 📊 Database Schema (Key Tables)

```
SYS_USERS
├── SYS_USER_ID (PK)
├── USERNAME (unique)
├── PASSWORD_HASH
├── BASE_SALARY
└── IS_ACTIVE

PRODUCTS
├── PRODUCTS_ID (PK)
├── PRODUCTS_NAME
├── PRODUCTS_CATEGORY_ID (FK → PRODUCTS_CATEGORY)
└── IS_DELETED

PRODUCTS_CATEGORY
├── PRODUCTS_CATEGORY_ID (PK)
├── PRODUCTS_CATEGORY_NAME
└── IS_DELETED

INVENTORY_LOCATION
├── INVENTORY_LOCATION_ID (PK)
├── INVENTORY_LOCATION_NAME
├── INVENTORY_LOCATION_ADDRESS
├── INVENTORY_LOCATION_LAT
├── INVENTORY_LOCATION_LONG
├── LT_INVENTORY_LOCATION_TYPE_ID (FK)
└── IS_DELETED

PRODUCTS_STOCKS
├── PRODUCTS_STOCKS_ID (PK)
├── PRODUCTS_ID (FK → PRODUCTS)
├── INVENTORY_LOCATION_ID (FK → INVENTORY_LOCATION)
└── PRODUCT_COUNT_IN_STOCK

VEHICLES
├── VEHICLE_ID (PK)
├── VEHICLE_NAME
├── VEHICLE_LICENCE_PLATE
└── IS_DELETED

DRIVERS
├── DRIVER_ID (PK)
├── DRIVER_NAME
├── DRIVER_LAST_NAME
├── DRIVERS_NATIONAL_CODE
├── DRIVERS_PHONE_NUMBER
└── IS_DELETED

TASKS
├── TASKS_ID (PK)
├── TASK_NAME
├── TASK_DESCRIPTION
├── START_DATE_TIME
├── END_DATE_TIME
├── EST_HOURS_IT_WILL_TAKE
├── HOURLY_RATE_FOR_TASK
├── TASK_CREATED_BY (FK → SYS_USERS)
└── TASK_STATUS (0=pending, 1=in progress, 2=finished)
```

---

## 🤖 AI Usage Transparency

This project is **mostly written by me** (Ali FatahDoost). However, AI tools (ChatGPT, etc.) were used in specific areas:

- **CSS styling** — AI assisted with modern UI design, dark mode, and responsive layouts.
- **repetitive work** - AI has done things that i have done my self when it got repetitive. mostly didn't happen cause i tried my best on the abstractions but it still happened sometimes.
All core business logic, authentication, database connection pooling, HTTP server, and access control were **written entirely by hand**.

---

## 🧪 Testing

Currently, the project includes minimal automated testing. Manual testing is recommended (though JUnit is on the road map to be added later)

- ✅ Login / Signup
- ✅ Session expiration
- ✅ Role-based access control
- ✅ CRUD operations (products, categories, locations, vehicles, drivers)
- ✅ Stock request workflow
- ✅ Task start / finish
- ✅ Activity logging
- ✅ Dark / Light mode persistence

---

## 📝 Roadmap

### ✅ Completed
- [x] User authentication & session management
- [x] Role-based access control (RBAC)
- [x] Product & category management
- [x] Inventory location management
- [x] Stock level tracking
- [x] Stock transfer requests
- [x] Vehicle management
- [x] Driver management
- [x] Task management (start / finish)
- [x] Work hour reporting
- [x] Activity logging
- [x] Dark / Light mode
- [x] Tab-based dashboard interface

### 🚧 In Progress
- [ ] Fleet management (trip planning & tracking)
- [ ] Warehouse management (advanced location features)
- [ ] Comprehensive reports section

### 📅 Planned
- [ ] Unit tests (JUnit)
- [ ] Docker containerisation
- [ ] CI/CD pipeline (most likely i will use Github Actions instead of jenkins to learn something new)
- [ ] after all of that i'm most likely going to add SpringBoot for modern Java ecosystem
- [ ] hibernate JPA again i'm going to add these for modern Java ecosystem and learning experience 

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

### Contribution Guidelines

- Follow the existing code style (no external libraries for core features at least for now)
- Document new endpoints in this README
- Test your changes thoroughly
- Update the roadmap if adding new features
- your code needs to be read-able so later maintaining it would become a problem

---

## 📄 License

no Licenses.

---

## 👨‍💻 Author

**Ali FatahDoost**

- GitHub: [@AliFatahadoost](https://github.com/AliFatahadoost)

If you use this project, please mention my name somewhere visible (the Home page already does 😄).

<div align="center">

**[⬆ Back to top](#inventory--fleet-management-system)**

</div>
