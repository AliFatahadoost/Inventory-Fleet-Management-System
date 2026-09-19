# Jali Frame + IFMS

> A custom Java full-stack framework and the inventory/fleet system built on top of it.

---

## Table of Contents

- [What is this?](#what-is-this)
- [Jali Frame](#jali-frame)
  - [Philosophy](#philosophy)
  - [Architecture](#architecture)
  - [The Enum-Driven Pattern](#the-enum-driven-pattern)
  - [The Custom Element Ecosystem](#the-custom-element-ecosystem)
  - [RBAC](#rbac)
  - [Directory Structure](#directory-structure)
  - [How to Boot](#how-to-boot)
- [IFMS — Inventory & Fleet Management System](#ifms--inventory--fleet-management-system)
  - [Modules](#modules)
  - [Routes & Object Codes](#routes--object-codes)
  - [Fleet Routing with OSRM](#fleet-routing-with-osrm)
  - [Ticket System](#ticket-system)
- [Tech Stack](#tech-stack)
- [Screenshots](#screenshots)
- [Getting Started](#getting-started)
- [Roadmap](#roadmap)
- [Known Limitations](#known-limitations)
- [License](#license)

---

## What is this?

This repository contains two things:

1. **Jali Frame** — a from-scratch full-stack Java framework for building database-backed web applications without a conventional MVC stack.
2. **IFMS (Inventory & Fleet Management System)** — the flagship application built on Jali Frame. IFMS was developed *as* the framework's proving ground: every feature was built to stress a piece of Jali, and every framework rough edge was discovered by shipping real pages through it.

The two are co-evolved. IFMS is not a demo — it's the reason Jali exists.

---

## Jali Frame

### Philosophy

Jali Frame rejects the convention of controllers, routers, ORM entities, and hand-written CRUD endpoints. Instead, it treats **application structure as data**.

Instead of writing:

```java
@GetMapping("/warehouse")
public List<Warehouse> listWarehouses() { ... }

@PostMapping("/warehouse")
public ResponseEntity<Warehouse> createWarehouse(@RequestBody Warehouse w) { ... }
```

You write:

```java
Warehouse(
    new ReadQuery().setTableName("warehouse").setColumnNames(...).getQuery(),
    new UpdateQuery().setTableName("warehouse").setColumnNames(...).getWhereQuery("warehouse_id = ?").getQuery(),
    new CreateQuery().setTableName("warehouse").setColumnNames(...).getQuery(),
    "UPDATE warehouse SET is_deleted = 1 WHERE warehouse_id = ?",
    101
),
```

…and the framework auto-generates the API, the CRUD handlers, the permission rows, the front-end table, and the modal — all from that single enum entry.

**Three principles:**

1. **Everything is an enum.** Pages, API queries, static assets, and lookups are declared in Java enums. Registering a route is one line.
2. **The framework serves the client.** The server trims HTML based on the user's permissions before it reaches the browser — no client-side auth flicker.
3. **The client is a stack of custom elements.** `<fetch-data-table>`, `<data-combo>`, `<find-object-box>`, `<date-box>`, `<map-box>`. Every form in every app is a composition of these.

### Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                        Browser (ES Modules)                    │
│                                                                │
│   <fetch-data-table>  <data-combo>  <map-box>  <date-box> ...  │
│            │               │            │          │           │
│            └───────────────┴────────────┴──────────┘           │
│                            │                                   │
│                            ▼  fetch() JSON                     │
└────────────────────────────────────────────────────────────────┘
                             │
┌────────────────────────────┼───────────────────────────────────┐
│                            ▼                                   │
│  com.sun.net.httpserver.HttpServer                             │
│                                                                │
│  ┌──────────────────┐       ┌──────────────────────────────┐   │
│  │  pageHandlerOpener│       │  apiManagement.dataApiGen    │   │
│  │  • serves HTML    │       │  • GET / POST / PUT / DELETE │   │
│  │  • filters by     │       │  • auth + permission check   │   │
│  │    data-AccessCode│       │  • generic SQL execution     │   │
│  └──────────────────┘       └──────────────────────────────┘   │
│                                                                │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Enum layer                                              │  │
│  │    FilesEnum        → static assets + HTML pages         │  │
│  │    WebPagesEnum     → route registration + object codes  │  │
│  │    CrudQueriesEnum  → SQL for every read/write/lookup    │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  DataBase layer                                          │  │
│  │    dataBaseManager  → connection pool                    │  │
│  │    dataBaseUtils    → helpers (auth, isAllowed, JSON)    │  │
│  │    DataBaseInit     → self-bootstrapping schema + SPs    │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                │
└────────────────────────────────────────────────────────────────┘
                             │
                             ▼
                       SQL Server (IFMS_DB)
```

### The Enum-Driven Pattern

Four enums define the entire application surface:

#### `FilesEnum` — static assets

```java
coreJs("/FrameWorksLib/Jali.js/core.js", true, FileTypesEnum.js),
Warehouse("/InventoryManagement/Warehouse.html", false, FileTypesEnum.html),
```

Every served file is registered here. The HTTP server mounts each at `/coreJs`, `/warehouse`, etc. The `loadedByIframe` flag tells the dashboard whether the asset is a full page or a shared library.

#### `WebPagesEnum` — routes + object codes

```java
Warehouse(101, FilesEnum.Warehouse, "/warehouse"),
```

Each page declares:
- An **object code** (used by RBAC)
- Its backing file
- Its URL route

Calling `registerRoute(server)` mounts the page **and** queues the object code for `SYS_OBJECTS` registration.

#### `CrudQueriesEnum` — every SQL query in the app

```java
Warehouse(
    new ReadQuery()
        .setTableName("warehouse")
        .setColumnNames("warehouse_id", "warehouse_title", "warehouse_code", "warehouse_lat", "warehouse_long")
        .setColumnDataTypes("numeric", "nvarchar(50)", "numeric", "decimal(9,6)", "decimal(9,6)")
        .setWhereQuery("AND isnull(is_deleted, 0) = 0")
        .getQuery(),
    new UpdateQuery().setTableName("warehouse").setColumnNames(...).getQuery(),
    new CreateQuery().setTableName("warehouse").setColumnNames(...).getQuery(),
    "UPDATE warehouse SET is_deleted = 1 WHERE warehouse_id = ?",
    101
),
```

One enum entry = one full CRUD API. The framework builds the SQL, binds parameters, serializes the response to JSON, and enforces permissions — all keyed off the same object code used in `WebPagesEnum`.

#### `FileTypesEnum` — MIME types

```java
html("text/html"),
css("text/css"),
js("text/javascript"),
png("image/png");
```

### The Custom Element Ecosystem

Every UI pattern in every Jali app is a **Web Component** loaded as an ES module. No React. No Vue. No build step.

| Element | Purpose |
|---|---|
| `<fetch-data-table>` | Renders a table with search, pagination, CRUD modals, and auto-wired API calls |
| `<data-combo>` | Dropdown that fetches options from an API endpoint |
| `<find-object-box>` | Modal picker for large lists (paginated, searchable) |
| `<date-box>` | Calendar picker with `YYYY-MM-DD` output |
| `<map-box>` | Leaflet map. **Write mode** picks lat/lng; **read mode** draws OSRM routes |
| `<jali-form>` | Standalone form (used by Login) |
| `<fleet-trip-table>` | Custom element combining table + OSRM routing (built for IFMS) |

All components:
- Load their own CSS via `<link>` inside a shadow root
- Read their config from **HTML attributes** using a small DSL
- Emit standard `change` / `submit` / custom events
- Work identically inside and outside modals

**The input DSL:**

```html
<fetch-data-table
    api="/warehouseApi"
    inputs="
        |name::warehouse_title|title::Title|type::text|value::;;
        |name::warehouse_lat,warehouse_long|title::Location|type::map-box|value::;;
    "
    columns="Title, Location"
>
```

Each `;;`-terminated block is one form field. Supported types: `text`, `number`, `password`, `checkbox`, `radio`, `data-combo`, `find-object-box`, `date-box`, `map-box`, `current-user`.

### RBAC

Jali Frame's authorization model is **object-code-based**, not role-based.

- Every page has an object code (e.g. Warehouse = 101)
- Every API request checks the user's permission for that object
- Every HTML element with `data-AccessCode="N"` is stripped server-side before reaching the browser if the user lacks READ

**Server-side HTML trimming** is the key innovation. Because the filter runs on the raw HTML response, a user without `CAN_READ` on object 101 literally never receives the Warehouse card in their Dashboard HTML. No client-side auth flicker. No hidden elements that can be unhidden with DevTools.

The permission matrix lives in `USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION`:

```
USER_CODE | OBJECT_CODE | CAN_READ | CAN_CREATE | CAN_UPDATE | CAN_DELETE
----------|-------------|----------|------------|------------|------------
1         | 101         | 1        | 1          | 1          | 1
2         | 101         | 1        | 0          | 0          | 0
```

The framework's `DataBaseInit` class auto-syncs this table on every boot:
- New objects inserted into `SYS_OBJECTS`
- Cross-joined with all users to create missing rows
- ADMIN (USER_CODE = 1) force-granted all flags

### Directory Structure

```
jali-frame/
├── backend/
│   ├── ConfigAndLauncherManager/
│   │   └── readConfig.java              # Config file, GUI/console, HTTP bootstrap
│   ├── DataBase/
│   │   ├── dataBaseManager.java          # Connection pool
│   │   ├── dataBaseUtils.java            # isAllowed, isAuthenticated, JSON helpers
│   │   ├── DataBaseInit.java             # Self-bootstrapping schema + stored procs
│   │   └── GenerateGenericSQLQuery.java  # Read/Update/Create/Delete builders
│   ├── InterFaces/
│   │   ├── CrudQueries.java
│   │   ├── JaliFiles.java
│   │   └── JaliWebPage.java
│   ├── PageRelatedEnums/
│   │   ├── FilesEnum.java                # Every static asset
│   │   ├── WebPagesEnum.java             # Every page + object code
│   │   ├── CrudQueriesEnum.java          # Every SQL query
│   │   └── FileTypesEnum.java
│   ├── WebServerHandlers/
│   │   ├── pageHandlerOpener.java        # Serves files + trims by data-AccessCode
│   │   ├── apiManagement.java            # Generic CRUD handler
│   │   └── webServerUtils.java           # JSON parser, cookie extractor
│   └── mainServerLaunch.java             # Wires APIs + routes at boot
│
└── ClientSide/
    ├── FrameWorksLib/
    │   ├── Jali.js/
    │   │   ├── core.js                   # Imports all custom elements
    │   │   └── custom_elements/
    │   │       ├── dataTable.js
    │   │       ├── dataCombo.js
    │   │       ├── findObjectBox.js
    │   │       ├── dateBox.js
    │   │       ├── mapBox.js
    │   │       ├── jaliForm.js
    │   │       └── fleetTripTable.js
    │   ├── JaliFrame.css/
    │   │   ├── readDataTable.css
    │   │   ├── dataCombo.css
    │   │   ├── FindObjectBox.css
    │   │   ├── dateBox.css
    │   │   ├── dataForm.css
    │   │   └── mapBox.css
    │   └── Leaflet/                      # Bundled Leaflet
    └── [Your app pages]/                 # Login, Dashboard, modules...
```

### How to Boot

1. **Create `config.txt`** next to the JAR (or let Jali create it on first run):

   ```properties
   BASE_FILE_ADDRESS=/absolute/path/to/ClientSide
   server=localhost
   port=1433
   databaseName=IFMS_DB
   username=sa
   password=your_password
   MAX_CONNECTION_POOL=5
   portNumber=8080
   serverIP=127.0.0.1
   queueWaitLine=10
   MAX_SESSION_TIME=86400
   ```

2. **Run the server:**

   ```bash
   java -cp jali-frame.jar IFMS.mainServerLaunch
   ```

   The launcher prompts:
   - `Y` — Swing configuration GUI
   - `n` — Console mode
   - `s` — Silent (jump straight to serving)

3. **First boot** creates the schema, stored procedures, `SYS_OBJECTS` table, and ADMIN user. Default credentials: `ADMIN` / `12`.

4. **Navigate to** `http://127.0.0.1:8080/`.

---

## IFMS — Inventory & Fleet Management System

IFMS is a real-world logistics system handling:

- **Multi-location inventory** — warehouses, stores, docks, factories
- **Stock tracking** with full historical audit trail
- **Inter-inventory transfers** — request product from another location
- **Fleet management** — teams, drivers, vehicles, dispatch
- **Route planning** — OSRM-powered distance and duration calculations
- **Approval workflows** — temp-access tickets and stock adjustments
- **Role-based access** — users see only the pages they're permitted to see

### Modules

| Module | Forms | Description |
|---|---|---|
| **Inventory** | 11 | Type, Warehouse, Inventory, Users, Product, Stock, Stock History, Request Type/Status/Header/Details |
| **Fleet** | 7 | Team, Drivers, Vehicles, Team Drivers, Team Manager, Trip, Team Transports |
| **Access Management** | 3 | Requests (tickets), Object lines, Stock lines |
| **Reports** | 4 | Low Stock, Fleet Activity, Product Movement, Ticket Summary |
| **User Management** | 2 | Users, Permissions matrix |

**Total: 27 pages, all driven by 3 enums and 7 custom elements.**

### Routes & Object Codes

Object codes are namespaced by module:

| Range | Module |
|---|---|
| 1–9 | System (Login, Dashboard, Home) |
| 10–50 | Management landing pages |
| 100–111 | Inventory forms |
| 200–209 | Fleet forms |
| 300–302 | Access ticket forms |
| 400–403 | Reports |
| 41–42 | User Management |

Every object code is used **three times**:
1. In `WebPagesEnum` for the page route
2. In `CrudQueriesEnum` for permission enforcement on its API
3. In HTML as `data-AccessCode="N"` for server-side trimming

### Fleet Routing with OSRM

The FleetTrip page demonstrates Jali's custom element strength:

```html
<fleet-trip-table api="/fleetTripApi"></fleet-trip-table>
```

Custom element that:
1. Lists trips in a table (like `<fetch-data-table>`)
2. Opens a modal with dropdowns for team, product request, and status
3. On product request selection, fetches the origin/destination warehouses
4. Calls OSRM to compute the driving route
5. Draws it on a `<map-box>` and fills distance/duration
6. Saves the encoded polyline for later display

Distance is stored as **meters**, duration as **seconds** — displayed as Km (1 decimal) and H:MM.

### Ticket System

Two ticket types share one header table and one approval workflow:

| Type | Payload Table | Meaning |
|---|---|---|
| `TEMP_ACCESS` | `access_request_object` | Request read/write permission on a form |
| `STOCK_ADJUSTMENT` | `access_request_stock` | Request write-off or correction of stock |

Both go through `PENDING` → `APPROVED` / `DENIED` → `APPLIED`. Approve/Deny buttons on the header table auto-fill `decided_by_user_code` from the logged-in session.

---

## Tech Stack

**Backend:**
- Java 17+
- `com.sun.net.httpserver.HttpServer` (built into the JDK)
- SQL Server
- No external framework dependencies (Spring, Hibernate, etc.)

**Frontend:**
- Vanilla ES modules
- Web Components (Custom Elements API + Shadow DOM)
- Leaflet for maps
- OSRM for routing

**Build:**
- Maven (or plain `javac` if preferred)

---

## Screenshots

> *Add screenshots here — Dashboard, Inventory form with modal, FleetTrip with map + route, Permission matrix.*

Recommended shots:
- `docs/dashboard.png` — main dashboard with sidebar + tabs
- `docs/warehouse-form.png` — a standard CRUD modal
- `docs/fleet-trip.png` — FleetTrip modal with OSRM route drawn
- `docs/permissions.png` — the permission matrix editor
- `docs/reports.png` — Low Stock report

---

## Getting Started

```bash
# 1. Clone
git clone https://github.com/yourname/jali-frame-ifms.git
cd jali-frame-ifms

# 2. Set up the database
#    Run the DDL scripts in /sql (dbo + USERS_DATA_AND_PERMISSIONS + INIT_DATABASE schemas)

# 3. Configure
cp config.example.txt config.txt
# edit config.txt with your SQL Server credentials

# 4. Build
mvn clean package

# 5. Run
java -cp target/jali-frame.jar IFMS.mainServerLaunch

# 6. Open http://127.0.0.1:8080/ and log in as ADMIN / 12
```

---

## Roadmap

**Jali Frame 2.0:**
- [ ] Replace positional payloads (`input0, input1, …`) with named fields
- [ ] Split overloaded element attributes (`name` → `api` + `field-name`)
- [ ] Single-pass boot sequence (no `finalizeRegistration` workaround)
- [ ] Auto-generated TypeScript definitions for custom elements
- [ ] Websocket support for live-updating tables
- [ ] Migrate to `jdk.httpserver` → `Netty` for HTTP/2

**IFMS:**
- [ ] User-facing ticket submission flow (currently admin-only pages)
- [ ] FOB / combo re-hydration on edit mode
- [ ] Password change endpoint + UI
- [ ] Toast notifications replacing `alert()`
- [ ] Multi-stop fleet routes
- [ ] Warehouse-specific inventory coordinates
- [ ] CSV / PDF export for reports
- [ ] Dark mode toggle persisted per user

---

## Known Limitations

Honestly documented so you don't discover them the hard way:

- **Positional JSON payloads.** Create/Update requests send `{input0, input1, …}` in enum-column order. Reordering form inputs without updating the enum breaks the save silently.
- **Combo/FOB display doesn't re-hydrate on edit.** The selected value is preserved on save, but the visual display resets to blank when reopening the edit modal.
- **No auto-expiry enforcement for tickets.** `expires_at` is stored but never triggers automatic permission revocation.
- **Hardcoded admin password on first boot.** Change it via direct DB update.
- **OSRM public demo endpoint** is used by default — rate-limited. Swap to a self-hosted instance for production.
- **`DataBaseInit` boot order.** Pages queue their object codes during `main()`, but the queue only flushes if `finalizeRegistration()` is called at the end. This is documented in code but easy to forget.

---

## License

MIT — do whatever you want. Attribution appreciated but not required.

---

## Acknowledgements

- **Leaflet** for map rendering
- **OSRM** for routing calculations
- **OpenStreetMap** contributors for the tile data
- Every open-source contributor who ever debugged a shadow DOM boundary

---

<p align="center">
  <em>Built with Jali Frame — because the framework is the product.</em>
</p>

---

