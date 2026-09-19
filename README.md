# Jali Frame + IFMS

A from-scratch Java full-stack framework and the inventory/fleet management system built with it.

**Jali Frame** provides the framework: HTTP serving, generic CRUD APIs, database integration, access control, static-file serving, and a reusable Web Component UI layer.

**IFMS** is the reference application: a 27-page inventory, fleet, ticketing, and reporting system built entirely on Jali Frame.

The goal of the project is simple:

> **Define application structure once, reuse it everywhere, and keep the framework small enough to understand.**

---

## Table of Contents

* [Overview](#overview)
* [Jali Frame](#jali-frame)

  * [Design Philosophy](#design-philosophy)
  * [Architecture](#architecture)
  * [Enum-Driven Application Model](#enum-driven-application-model)
  * [Custom Element Ecosystem](#custom-element-ecosystem)
  * [Access Control](#access-control)
  * [Directory Structure](#directory-structure)
* [IFMS](#ifms)

  * [Modules](#modules)
  * [Routes and Object Codes](#routes-and-object-codes)
  * [Fleet Routing](#fleet-routing)
  * [Ticket System](#ticket-system)
* [Technology](#technology)
* [Getting Started](#getting-started)
* [Screenshots](#screenshots)
* [Known Limitations](#known-limitations)
* [Roadmap](#roadmap)
* [License](#license)

---

# Overview

Jali Frame is a dependency-light Java web framework designed around **declarative application definitions** rather than a conventional MVC/ORM architecture.

Instead of creating separate controllers, route declarations, CRUD handlers, and UI logic for every database entity, Jali centralizes application metadata in Java enums and provides reusable client-side Web Components.

A typical entity can be described through a single `CrudQueriesEnum` entry:

```java
Warehouse(
    new ReadQuery()
        .setTableName("warehouse")
        .setColumnNames(
            "warehouse_id",
            "warehouse_title",
            "warehouse_code",
            "warehouse_lat",
            "warehouse_long"
        )
        .getQuery(),

    new UpdateQuery()
        .setTableName("warehouse")
        .setColumnNames(...)
        .getQuery(),

    new CreateQuery()
        .setTableName("warehouse")
        .setColumnNames(...)
        .getQuery(),

    "UPDATE warehouse SET is_deleted = 1 WHERE warehouse_id = ?",

    101
),
```

From that definition, Jali can provide the API layer, database operations, permission checks, and the information required by the front-end components.

IFMS was built to exercise those abstractions against a real application instead of a toy example.

---

# Jali Frame

## Design Philosophy

Jali Frame is built around three ideas.

### 1. Application structure is data

Pages, static files, object codes, and CRUD queries are declared through Java enums.

The framework reads those definitions and uses them to construct the application's runtime surface.

### 2. The server owns authorization

Authorization is enforced on the server for both API requests and rendered HTML.

Pages can declare access requirements such as:

```html
data-AccessCode="101"
```

When the current user does not have READ permission for that object, Jali can remove the protected HTML before sending the response.

This means unauthorized UI is not merely hidden with client-side JavaScript.

### 3. The browser is built from reusable Web Components

Instead of writing form and table logic repeatedly for every page, Jali provides reusable custom elements:

```html
<fetch-data-table>
<data-combo>
<find-object-box>
<date-box>
<map-box>
<jali-form>
```

Applications compose these components through HTML attributes rather than implementing their own CRUD UI from scratch.

---

# Architecture

```text
┌─────────────────────────────────────────────────────────────────┐
│                         Browser                                 │
│                                                                 │
│  <fetch-data-table>  <data-combo>  <find-object-box>            │
│  <date-box>          <map-box>   <jali-form>                    │
│                                                                 │
│                         fetch() / JSON                           │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│                     Jali HTTP Server                            │
│                                                                 │
│  com.sun.net.httpserver.HttpServer                              │
│                                                                 │
│  ┌─────────────────────┐    ┌────────────────────────────────┐  │
│  │ Page Handler        │    │ Generic API Handler             │  │
│  │                     │    │                                │  │
│  │ • serves files      │    │ • GET / POST / PUT / DELETE   │  │
│  │ • checks access     │    │ • authentication              │  │
│  │ • trims HTML        │    │ • permission checks            │  │
│  └─────────────────────┘    │ • generic SQL execution        │  │
│                             └────────────────────────────────┘  │
│                                                                 │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ Enum Definitions                                           │ │
│  │                                                            │ │
│  │ FilesEnum       → static files and pages                   │ │
│  │ WebPagesEnum    → routes and object codes                  │ │
│  │ CrudQueriesEnum → CRUD and lookup queries                  │ │
│  │ FileTypesEnum   → MIME types                               │ │
│  └────────────────────────────────────────────────────────────┘ │
│                                                                 │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │ Database Layer                                             │ │
│  │                                                            │ │
│  │ dataBaseManager → connection pool                          │ │
│  │ dataBaseUtils   → database/authentication helpers          │ │
│  │ DataBaseInit    → initialization and permission sync       │ │
│  │ Query Builders  → generic SQL generation                   │ │
│  └────────────────────────────────────────────────────────────┘ │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
                        SQL Server
```

The important property of this architecture is that **the same object definition is reused across multiple layers**.

For example, object code `101` can identify the same feature in:

```text
WebPagesEnum
       ↓
page route

CrudQueriesEnum
       ↓
API authorization

HTML data-AccessCode
       ↓
server-side UI filtering
```

---

# Enum-Driven Application Model

Jali's application surface is primarily described through four enums.

## `FilesEnum`

Defines files that the server can serve.

```java
coreJs(
    "/FrameWorksLib/Jali.js/core.js",
    true,
    FileTypesEnum.js
),

Warehouse(
    "/InventoryManagement/Warehouse.html",
    false,
    FileTypesEnum.html
),
```

The framework uses these definitions when serving static assets and application pages.

---

## `WebPagesEnum`

Defines application routes and their object codes.

```java
Warehouse(
    101,
    FilesEnum.Warehouse,
    "/warehouse"
),
```

Each page contains:

* an object code
* its backing file
* its URL route

The object code is later used by the access-control system.

---

## `CrudQueriesEnum`

Defines the database operations associated with an application object.

```java
Warehouse(
    new ReadQuery()
        .setTableName("warehouse")
        .setColumnNames(
            "warehouse_id",
            "warehouse_title",
            "warehouse_code"
        )
        .setColumnDataTypes(
            "numeric",
            "nvarchar(50)",
            "numeric"
        )
        .setWhereQuery(
            "AND ISNULL(is_deleted, 0) = 0"
        )
        .getQuery(),

    new UpdateQuery()
        .setTableName("warehouse")
        .setColumnNames(...)
        .getQuery(),

    new CreateQuery()
        .setTableName("warehouse")
        .setColumnNames(...)
        .getQuery(),

    "UPDATE warehouse SET is_deleted = 1 WHERE warehouse_id = ?",

    101
),
```

The framework uses this information to provide generic CRUD operations while binding the API to the same object code used by the page and permission system.

---

## `FileTypesEnum`

Defines MIME types for files served by the framework.

```java
html("text/html"),
css("text/css"),
js("text/javascript"),
png("image/png");
```

---

# Custom Element Ecosystem

The client-side UI is based on standard Web Components and ES modules.

No React.

No Vue.

No front-end build pipeline is required for the framework itself.

| Component            | Purpose                                              |
| -------------------- | ---------------------------------------------------- |
| `<fetch-data-table>` | Searchable, paginated CRUD table with modal forms    |
| `<data-combo>`       | API-backed dropdown                                  |
| `<find-object-box>`  | Searchable picker for large datasets                 |
| `<date-box>`         | Date selection with `YYYY-MM-DD` output              |
| `<map-box>`          | Leaflet map for location selection and route display |
| `<jali-form>`        | Generic standalone form component                    |
| `<fleet-trip-table>` | IFMS-specific table with routing support             |

Components use attributes as a small declarative configuration language.

For example:

```html
<fetch-data-table
    api="/warehouseApi"
    inputs="
        |name::warehouse_title
        |title::Title
        |type::text
        |value::;;
        |name::warehouse_lat,warehouse_long
        |title::Location
        |type::map-box
        |value::;;
    "
    columns="Title,Location">
</fetch-data-table>
```

Supported input types include:

```text
text
number
password
checkbox
radio
data-combo
find-object-box
date-box
map-box
current-user
```

The purpose of this layer is to make common application UI reusable across pages instead of repeatedly implementing the same form, search, pagination, and modal behavior.

---

# Access Control

Jali uses **object-level permissions**.

Every protected application object has an object code such as:

```text
101 = Warehouse
```

Permissions are stored per user and object:

```text
USER_CODE | OBJECT_CODE | CAN_READ | CAN_CREATE | CAN_UPDATE | CAN_DELETE
----------|-------------|----------|------------|------------|-----------
1         | 101         |    1     |     1      |     1      |     1
2         | 101         |    1     |     0      |     0      |     0
```

The same object code can therefore be used by:

```text
WebPagesEnum
       ↓
route registration

CrudQueriesEnum
       ↓
API permission checking

HTML
       ↓
data-AccessCode
       ↓
server-side filtering
```

### Server-side UI filtering

A protected element can declare:

```html
<div data-AccessCode="101">
    Warehouse
</div>
```

Jali checks the user's permission before the HTML reaches the browser.

This provides a server-side alternative to simply rendering everything and hiding unauthorized controls with JavaScript.

### Permission initialization

`DataBaseInit` is responsible for synchronizing permission data during startup.

It can:

* register newly discovered system objects
* create missing user/object permission rows
* grant the administrator full permissions

---

# Directory Structure

```text
jali-frame/
├── backend/
│   ├── ConfigAndLauncherManager/
│   │   └── readConfig.java
│   │
│   ├── DataBase/
│   │   ├── dataBaseManager.java
│   │   ├── dataBaseUtils.java
│   │   ├── DataBaseInit.java
│   │   └── GenerateGenericSQLQuery.java
│   │
│   ├── InterFaces/
│   │   ├── CrudQueries.java
│   │   ├── JaliFiles.java
│   │   └── JaliWebPage.java
│   │
│   ├── PageRelatedEnums/
│   │   ├── FilesEnum.java
│   │   ├── WebPagesEnum.java
│   │   ├── CrudQueriesEnum.java
│   │   └── FileTypesEnum.java
│   │
│   ├── WebServerHandlers/
│   │   ├── pageHandlerOpener.java
│   │   ├── apiManagement.java
│   │   └── webServerUtils.java
│   │
│   └── mainServerLaunch.java
│
└── ClientSide/
    ├── FrameWorksLib/
    │   ├── Jali.js/
    │   │   ├── core.js
    │   │   └── custom_elements/
    │   │       ├── dataTable.js
    │   │       ├── dataCombo.js
    │   │       ├── findObjectBox.js
    │   │       ├── dateBox.js
    │   │       ├── mapBox.js
    │   │       ├── jaliForm.js
    │   │       └── fleetTripTable.js
    │   │
    │   ├── JaliFrame.css/
    │   └── Leaflet/
    │
    └── [Application Pages]
```

---

# IFMS

## Inventory & Fleet Management System

IFMS is the reference application built on top of Jali Frame.

It demonstrates the framework across multiple areas of a real application:

* multi-location inventory
* stock tracking and history
* inter-inventory requests
* fleet management
* trip planning
* routing
* approval workflows
* access management
* reports
* per-user permissions

**27 application pages are implemented using the Jali architecture.**

---

## Modules

| Module            | Pages | Description                                                                 |
| ----------------- | ----: | --------------------------------------------------------------------------- |
| Inventory         |    11 | Warehouses, products, stock, requests, history, and inventory configuration |
| Fleet             |     7 | Teams, drivers, vehicles, transports, and trips                             |
| Access Management |     3 | Temporary access and stock adjustment requests                              |
| Reports           |     4 | Inventory and fleet reporting                                               |
| User Management   |     2 | Users and permission administration                                         |

---

# Routes and Object Codes

Object codes identify application features and connect routing, permissions, and APIs.

| Range   | Module            |
| ------- | ----------------- |
| 1–9     | System            |
| 10–50   | Management        |
| 100–111 | Inventory         |
| 200–209 | Fleet             |
| 300–302 | Access Management |
| 400–403 | Reports           |
| 41–42   | User Management   |

For example:

```text
Object 101
    ↓
WebPagesEnum
    ↓
/warehouse

Object 101
    ↓
CrudQueriesEnum
    ↓
warehouse API

Object 101
    ↓
data-AccessCode="101"
    ↓
server-side UI authorization
```

---

# Fleet Routing

The IFMS Fleet Trip page demonstrates how application-specific components can be built on top of Jali's generic component system.

```html
<fleet-trip-table
    api="/fleetTripApi">
</fleet-trip-table>
```

The component can:

1. display fleet trips
2. open the trip form
3. load teams, requests, and statuses
4. resolve origin and destination warehouses
5. request a route from OSRM
6. display the route on a Leaflet map
7. calculate distance and duration
8. store the resulting route information

Distance is stored in meters and duration in seconds, then converted for display.

---

# Ticket System

IFMS contains a shared approval workflow for two types of requests.

| Ticket Type        | Payload                 | Purpose                                           |
| ------------------ | ----------------------- | ------------------------------------------------- |
| `TEMP_ACCESS`      | `access_request_object` | Request temporary access to an application object |
| `STOCK_ADJUSTMENT` | `access_request_stock`  | Request a stock correction or write-off           |

Both use the same lifecycle:

```text
PENDING
   │
   ├── APPROVED
   │      │
   │      └── APPLIED
   │
   └── DENIED
```

The decision is associated with the authenticated user through the current session.

---

# Technology

## Backend

* Java 17+
* `com.sun.net.httpserver.HttpServer`
* SQL Server
* JDBC
* Custom connection pooling
* No Spring
* No Hibernate
* No ORM framework

## Frontend

* Vanilla JavaScript
* ES modules
* Web Components
* Shadow DOM
* Leaflet

## Routing

* OSRM
* OpenStreetMap data

## Build

* Maven
* Plain `javac` can also be used where appropriate

---

# Getting Started

## Requirements

You'll need:

* Java 17 or newer
* Maven
* SQL Server
* a SQL Server account with permission to create and modify the IFMS database

---

## 1. Clone the repository

```bash
git clone https://github.com/yourname/jali-frame-ifms.git
cd jali-frame-ifms
```

---

## 2. Configure the application

Create `config.txt` next to the application JAR:

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

---

## 3. Build

```bash
mvn clean package
```

---

## 4. Start the server

```bash
java -cp target/jali-frame.jar IFMS.mainServerLaunch
```

The launcher supports:

```text
Y → configuration GUI
n → console mode
s → silent startup
```

---

## 5. Initialize the database

On first startup, Jali initializes the required application objects and database structures used by IFMS.

Check the project SQL/bootstrap configuration for anything that must be prepared manually before first startup.

---

## 6. Open the application

```text
http://127.0.0.1:8080/
```

> ⚠️ Development installations may use default administrator credentials. Change the administrator password before using the system outside a local development environment.

---

# Screenshots

Screenshots will be added here as the project documentation is expanded.

Recommended examples:

```text
docs/
├── dashboard.png
├── warehouse-form.png
├── fleet-trip.png
├── permissions.png
└── reports.png
```

Useful screenshots include:

* Dashboard
* CRUD modal
* Fleet trip with route
* Permission matrix
* Report pages

---

# Known Limitations

Jali Frame and IFMS are complete as a 1.0 development cycle, but several known limitations remain.

### Positional request payloads

Create and Update requests currently use positional fields such as:

```text
input0
input1
input2
...
```

The positions must match the expected enum column order.

Changing the UI field order without updating the corresponding backend definition can break requests.

### Combo / FOB edit rehydration

Some `data-combo` and `find-object-box` values are preserved correctly during save but do not always reconstruct their visual state when an edit modal is reopened.

### Ticket expiration

`expires_at` is stored, but automatic expiration and permission revocation are not currently enforced.

### Default administrator credentials

The initial administrator credentials are intended for development and must be changed before a real deployment.

### Public OSRM instance

The default configuration uses the public OSRM service.

That service is appropriate for experimentation and development, but production deployments should use a dedicated routing service or a self-hosted OSRM instance.

### Startup registration

Page registration and database object registration are currently coupled to the startup sequence. The implementation works, but this area is a target for simplification in Jali 2.0.

---

# Roadmap

## Jali Frame 2.0

* [ ] Replace positional payloads with named fields
* [ ] Replace overloaded attributes with explicit `api`, `field`, and `value` attributes
* [ ] Simplify startup into a single deterministic registration phase
* [ ] Generate TypeScript definitions for custom elements
* [ ] Add WebSocket support for live-updating components
* [ ] Evaluate a more advanced HTTP server implementation for HTTP/2 support

## IFMS

* [ ] User-facing ticket submission flow
* [ ] Improve combo / FOB edit hydration
* [ ] Password change endpoint and UI
* [ ] Replace browser `alert()` with application notifications
* [ ] Multi-stop fleet routing
* [ ] Warehouse-specific inventory coordinates
* [ ] CSV / PDF report export
* [ ] Persisted per-user theme preferences

---

# Why This Project Exists

Jali Frame was not designed independently and then given a demo application afterward.

**IFMS was the proving ground.**

The framework was developed around the problems encountered while building the application, and IFMS was repeatedly used to test whether Jali's abstractions were actually useful.

That means the project contains two related artifacts:

```text
Jali Frame
    ↓
framework and reusable abstractions
    ↓
IFMS
    ↓
real application built with those abstractions
```

The result is intended to be useful in both directions:

* **Jali Frame** demonstrates the architecture and reusable tooling.
* **IFMS** demonstrates how that architecture behaves in a substantial application.

---

# License

MIT License.

Use it, modify it, distribute it, and build on it.

Attribution is appreciated but not required.

---

# Acknowledgements

* [Leaflet](https://leafletjs.com/) for map rendering
* [OSRM](https://project-osrm.org/) for routing
* [OpenStreetMap](https://www.openstreetmap.org/) contributors for map data
* The broader open-source ecosystem

---

<p align="center">
  <em>Built with Jali Frame — because the framework is the product.</em>
</p>
