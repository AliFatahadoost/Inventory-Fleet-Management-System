/* ============================================================
   IFMS — Full dbo Schema + Seed Data
   ============================================================
   Run this ONCE against a fresh database.

   The framework will auto-create these on first boot:
     • USERS_DATA_AND_PERMISSIONS schema + tables
     • INIT_DATABASE schema + stored procedures

   This script handles only the `dbo` schema used by IFMS.

   Safe to re-run — uses IF OBJECT_ID guards.
   ============================================================ */

USE IFMS_DB;  -- ← change to your database name if different
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;
GO


/* ============================================================
   SECTION 1 — CORE DOMAIN TABLES
   ============================================================ */

/* ------------------------------------------------------------
   inventory_type — categories of inventory (warehouse, store, …)
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.inventory_type', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.inventory_type (
        inventory_id         NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        inventory_type_title NVARCHAR(50)  NOT NULL,
        inventory_type_code  NUMERIC(18,0) NOT NULL,
        is_deleted           BIT           NULL DEFAULT 0,
        CONSTRAINT PK_inventory_type PRIMARY KEY (inventory_id),
        CONSTRAINT UQ_inventory_type_code UNIQUE (inventory_type_code)
    );
END;
GO

/* ------------------------------------------------------------
   warehouse — physical locations with coordinates
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.warehouse', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.warehouse (
        warehouse_id    NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        warehouse_title NVARCHAR(50)  NOT NULL,
        warehouse_code  NUMERIC(18,0) NOT NULL,
        warehouse_lat   DECIMAL(9,6)  NOT NULL,
        warehouse_long  DECIMAL(9,6)  NOT NULL,
        is_deleted      BIT           NULL DEFAULT 0,
        CONSTRAINT PK_warehouse PRIMARY KEY (warehouse_id)
    );
END;
GO

/* ------------------------------------------------------------
   inventory — an instance of a type at a warehouse
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.inventory', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.inventory (
        inventory_id      NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        inventory_type_id NUMERIC(18,0) NULL,
        warehouse_id      NUMERIC(18,0) NULL,
        is_deleted        BIT           NULL DEFAULT 0,
        CONSTRAINT PK_inventory PRIMARY KEY (inventory_id)
    );
END;
GO

/* ------------------------------------------------------------
   inventory_users — which users can operate which inventory
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.inventory_users', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.inventory_users (
        inventory_users_id NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        inventory_id       NUMERIC(18,0) NOT NULL,
        user_id            NUMERIC(18,0) NOT NULL,
        is_deleted         BIT           NULL DEFAULT 0,
        CONSTRAINT PK_inventory_users PRIMARY KEY (inventory_users_id)
    );
END;
GO

/* ------------------------------------------------------------
   product — the things we stock
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.product', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.product (
        product_id    NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        product_code  NUMERIC(18,0) NOT NULL,
        product_title NVARCHAR(50)  NULL,
        is_deleted    BIT           NULL DEFAULT 0,
        CONSTRAINT PK_product PRIMARY KEY (product_id)
    );
END;
GO

/* ------------------------------------------------------------
   product_transaction_type — DELIVERY_IN, WASTE, etc.
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.product_transaction_type', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.product_transaction_type (
        product_transaction_type_id NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        title                       NVARCHAR(50)  NULL,
        description                 NVARCHAR(500) NULL,
        is_deleted                  BIT           NULL DEFAULT 0,
        CONSTRAINT PK_product_transaction_type PRIMARY KEY (product_transaction_type_id)
    );
END;
GO

/* ------------------------------------------------------------
   inventory_stock — current stock per (inventory, product)
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.inventory_stock', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.inventory_stock (
        inventory_stock_id NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        inventory_code     NUMERIC(18,0) NOT NULL,
        stock_count        NUMERIC(18,0) NOT NULL,
        product_code       NUMERIC(18,0) NOT NULL,
        is_deleted         BIT           NULL DEFAULT 0,
        CONSTRAINT PK_inventory_stock PRIMARY KEY (inventory_stock_id)
    );
END;
GO

/* ------------------------------------------------------------
   inventory_stock_history — immutable audit trail
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.inventory_stock_history', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.inventory_stock_history (
        inventory_stock_history_id   NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        inventory_id                 NUMERIC(18,0) NOT NULL,
        product_changed_code         NUMERIC(18,0) NOT NULL,
        product_transaction_type_id  NUMERIC(18,0) NOT NULL,
        request_id                   NUMERIC(18,0) NULL,
        amount_changed               NUMERIC(18,0) NOT NULL,
        CONSTRAINT PK_inventory_stock_history PRIMARY KEY (inventory_stock_history_id)
    );
END;
GO


/* ============================================================
   SECTION 2 — PRODUCT REQUESTS (inter-inventory transfers)
   ============================================================ */

/* ------------------------------------------------------------
   inventory_product_request_type — INTERNAL / EXTERNAL
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.inventory_product_request_type', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.inventory_product_request_type (
        inventory_product_request_type_id NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        request_type_code                 NUMERIC(18,0) NOT NULL,
        request_type_title                NVARCHAR(50)  NOT NULL,
        is_deleted                        BIT           NULL DEFAULT 0,
        CONSTRAINT PK_inventory_product_request_type PRIMARY KEY (inventory_product_request_type_id)
    );
END;
GO

/* ------------------------------------------------------------
   inventory_product_request_status — PENDING / APPROVED / …
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.inventory_product_request_status', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.inventory_product_request_status (
        inventory_product_request_status NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        request_status_code              NUMERIC(18,0) NOT NULL,
        request_status_title             NVARCHAR(50)  NOT NULL,
        is_deleted                       BIT           NULL DEFAULT 0,
        CONSTRAINT PK_inventory_product_request_status PRIMARY KEY (inventory_product_request_status)
    );
END;
GO

/* ------------------------------------------------------------
   inventory_product_request_header — a transfer request
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.inventory_product_request_header', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.inventory_product_request_header (
        inventory_product_request_header_id NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        request_status_code                 NUMERIC(18,0) NOT NULL,
        request_type_code                   NUMERIC(18,0) NOT NULL,
        requesting_inventory_id             NUMERIC(18,0) NOT NULL,
        supplying_inventory_id              NUMERIC(18,0) NULL,
        request_date                        NVARCHAR(10)  NOT NULL,
        is_deleted                          BIT           NULL DEFAULT 0,
        CONSTRAINT PK_inventory_product_request_header PRIMARY KEY (inventory_product_request_header_id)
    );
END;
GO

/* Additive columns (safe if already exist) */
IF COL_LENGTH('dbo.inventory_product_request_header', 'requester_user_code') IS NULL
    ALTER TABLE dbo.inventory_product_request_header
        ADD requester_user_code NUMERIC(18,0) NULL;
GO

IF COL_LENGTH('dbo.inventory_product_request_header', 'created_at') IS NULL
    ALTER TABLE dbo.inventory_product_request_header
        ADD created_at DATETIME2 NULL DEFAULT SYSDATETIME();
GO

IF COL_LENGTH('dbo.inventory_product_request_header', 'decided_by_user_code') IS NULL
    ALTER TABLE dbo.inventory_product_request_header
        ADD decided_by_user_code NUMERIC(18,0) NULL;
GO

IF COL_LENGTH('dbo.inventory_product_request_header', 'decided_at') IS NULL
    ALTER TABLE dbo.inventory_product_request_header
        ADD decided_at DATETIME2 NULL;
GO

IF COL_LENGTH('dbo.inventory_product_request_header', 'decision_note') IS NULL
    ALTER TABLE dbo.inventory_product_request_header
        ADD decision_note NVARCHAR(500) NULL;
GO

/* ------------------------------------------------------------
   inventory_product_request_details — line items per request
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.inventory_product_request_details', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.inventory_product_request_details (
        inventory_product_request_details_id NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        inventory_product_request_header_id  NUMERIC(18,0) NOT NULL,
        product_code                         NUMERIC(18,0) NOT NULL,
        product_count                        NUMERIC(18,0) NOT NULL,
        is_deleted                           BIT           NULL DEFAULT 0,
        CONSTRAINT PK_inventory_product_request_details PRIMARY KEY (inventory_product_request_details_id)
    );
END;
GO


/* ============================================================
   SECTION 3 — FLEET
   ============================================================ */

/* ------------------------------------------------------------
   fleet_team — collection of drivers + vehicles
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.fleet_team', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.fleet_team (
        fleet_team_id   NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        fleet_team_code NUMERIC(18,0) NOT NULL,
        fleet_team_name NVARCHAR(50)  NULL,
        is_deleted      BIT           NULL DEFAULT 0,
        CONSTRAINT PK_fleet_team PRIMARY KEY (fleet_team_id)
    );
END;
GO

/* ------------------------------------------------------------
   drivers
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.drivers', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.drivers (
        drivers_id              NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        drivers_code            NUMERIC(18,0) NOT NULL,
        drivers_first_name      NVARCHAR(50)  NOT NULL,
        drivers_last_name       NVARCHAR(50)  NOT NULL,
        drivers_national_code   NVARCHAR(50)  NULL,
        drivers_phone_number    NVARCHAR(50)  NULL,
        drivers_age             NUMERIC(18,0) NULL,
        is_deleted              BIT           NULL DEFAULT 0,
        CONSTRAINT PK_drivers PRIMARY KEY (drivers_id)
    );
END;
GO

/* ------------------------------------------------------------
   vehicles
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.vehicles', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.vehicles (
        vehicle_id            NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        vehicle_code          NUMERIC(18,0) NOT NULL,
        vehicle_name          NVARCHAR(50)  NOT NULL,
        vehicle_licence_plate NVARCHAR(50)  NOT NULL,
        is_deleted            BIT           NULL DEFAULT 0,
        CONSTRAINT PK_vehicles PRIMARY KEY (vehicle_id)
    );
END;
GO

/* ------------------------------------------------------------
   fleet_team_drivers — driver + vehicle per team
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.fleet_team_drivers', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.fleet_team_drivers (
        fleet_team_drivers_id NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        fleet_team_id         NUMERIC(18,0) NOT NULL,
        driver_code           NUMERIC(18,0) NOT NULL,
        vehicle_code          NUMERIC(18,0) NOT NULL,
        is_deleted            BIT           NULL DEFAULT 0,
        CONSTRAINT PK_fleet_team_drivers PRIMARY KEY (fleet_team_drivers_id)
    );
END;
GO

/* ------------------------------------------------------------
   fleet_team_manager — user assigned to a team
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.fleet_team_manager', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.fleet_team_manager (
        fleet_team_manager_id   NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        fleet_team_manager_code NUMERIC(18,0) NOT NULL,
        user_id                 NUMERIC(18,0) NOT NULL,
        fleet_team_code         NUMERIC(18,0) NOT NULL,
        is_deleted              BIT           NULL DEFAULT 0,
        CONSTRAINT PK_fleet_team_manager PRIMARY KEY (fleet_team_manager_id)
    );
END;
GO

/* ------------------------------------------------------------
   fleet_team_transports — assign driver+vehicle to a request
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.fleet_team_transports', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.fleet_team_transports (
        fleet_team_transports_id            NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        inventory_product_request_header_id NUMERIC(18,0) NOT NULL,
        assigned_driver_code                NUMERIC(18,0) NOT NULL,
        assigned_vehicle_code               NUMERIC(18,0) NOT NULL,
        is_deleted                          BIT           NULL DEFAULT 0,
        CONSTRAINT PK_fleet_team_transports PRIMARY KEY (fleet_team_transports_id)
    );
END;
GO

/* ------------------------------------------------------------
   fleet_trip_status — PLANNED / EN_ROUTE / ARRIVED / …
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.fleet_trip_status', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.fleet_trip_status (
        fleet_trip_status_id    NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        fleet_trip_status_code  NUMERIC(18,0) NOT NULL,
        fleet_trip_status_title NVARCHAR(50)  NOT NULL,
        is_deleted              BIT           NULL DEFAULT 0,
        CONSTRAINT PK_fleet_trip_status PRIMARY KEY (fleet_trip_status_id),
        CONSTRAINT UQ_fleet_trip_status_code UNIQUE (fleet_trip_status_code)
    );
END;
GO

/* ------------------------------------------------------------
   fleet_trip — an actual planned/executed route
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.fleet_trip', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.fleet_trip (
        fleet_trip_id                       NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        fleet_team_id                       NUMERIC(18,0) NOT NULL,
        inventory_product_request_header_id NUMERIC(18,0) NULL,
        origin_warehouse_id                 NUMERIC(18,0) NOT NULL,
        destination_warehouse_id            NUMERIC(18,0) NOT NULL,
        distance_meters                     NUMERIC(18,0) NULL,
        duration_seconds                    NUMERIC(18,0) NULL,
        route_geometry                      NVARCHAR(MAX) NULL,
        planned_departure                   DATETIME2     NULL,
        actual_departure                    DATETIME2     NULL,
        actual_arrival                      DATETIME2     NULL,
        fleet_trip_status_code              NUMERIC(18,0) NOT NULL,
        is_deleted                          BIT           NULL DEFAULT 0,
        CONSTRAINT PK_fleet_trip PRIMARY KEY (fleet_trip_id)
    );
END;
GO

/* ------------------------------------------------------------
   fleet_aproval_by_inventory — arrival confirmation
   ------------------------------------------------------------ */
IF OBJECT_ID('dbo.fleet_aproval_by_inventory_header', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.fleet_aproval_by_inventory_header (
        fleet_aproval_by_inventory_id       NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        inventory_product_request_header_id NUMERIC(18,0) NOT NULL,
        description                         NVARCHAR(500) NULL,
        did_arive                           BIT           NULL,
        CONSTRAINT PK_fleet_aproval_by_inventory_header PRIMARY KEY (fleet_aproval_by_inventory_id)
    );
END;
GO

IF OBJECT_ID('dbo.fleet_aproval_by_inventory_details', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.fleet_aproval_by_inventory_details (
        fleet_aproval_by_inventory_details_id NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        fleet_aproval_by_inventory_id         NUMERIC(18,0) NOT NULL,
        product_code                          NUMERIC(18,0) NOT NULL,
        how_many_arived                       NUMERIC(18,0) NOT NULL,
        CONSTRAINT PK_fleet_aproval_by_inventory_details PRIMARY KEY (fleet_aproval_by_inventory_details_id)
    );
END;
GO


/* ============================================================
   SECTION 4 — ACCESS REQUESTS (TICKETS)
   ============================================================ */

IF OBJECT_ID('dbo.access_request_type', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.access_request_type (
        access_request_type_id    NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        access_request_type_code  NUMERIC(18,0) NOT NULL,
        access_request_type_title NVARCHAR(50)  NOT NULL,
        is_deleted                BIT           NULL DEFAULT 0,
        CONSTRAINT PK_access_request_type PRIMARY KEY (access_request_type_id),
        CONSTRAINT UQ_access_request_type_code UNIQUE (access_request_type_code)
    );
END;
GO

IF OBJECT_ID('dbo.access_request_status', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.access_request_status (
        access_request_status_id    NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        access_request_status_code  NUMERIC(18,0) NOT NULL,
        access_request_status_title NVARCHAR(50)  NOT NULL,
        is_deleted                  BIT           NULL DEFAULT 0,
        CONSTRAINT PK_access_request_status PRIMARY KEY (access_request_status_id),
        CONSTRAINT UQ_access_request_status_code UNIQUE (access_request_status_code)
    );
END;
GO

IF OBJECT_ID('dbo.access_request_header', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.access_request_header (
        access_request_header_id   NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        access_request_type_code   NUMERIC(18,0) NOT NULL,
        access_request_status_code NUMERIC(18,0) NOT NULL,
        requester_user_code        NUMERIC(18,0) NOT NULL,
        decided_by_user_code       NUMERIC(18,0) NULL,
        target_inventory_id        NUMERIC(18,0) NULL,
        reason                     NVARCHAR(500) NOT NULL,
        decision_note              NVARCHAR(500) NULL,
        requested_at               DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
        decided_at                 DATETIME2     NULL,
        expires_at                 DATETIME2     NULL,
        applied_at                 DATETIME2     NULL,
        applied_by_user_code       NUMERIC(18,0) NULL,
        is_deleted                 BIT           NULL DEFAULT 0,
        CONSTRAINT PK_access_request_header PRIMARY KEY (access_request_header_id)
    );

    CREATE INDEX IX_arh_requester        ON dbo.access_request_header (requester_user_code);
    CREATE INDEX IX_arh_status           ON dbo.access_request_header (access_request_status_code);
    CREATE INDEX IX_arh_target_inventory ON dbo.access_request_header (target_inventory_id);
    CREATE INDEX IX_arh_type             ON dbo.access_request_header (access_request_type_code);
END;
GO

IF OBJECT_ID('dbo.access_request_object', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.access_request_object (
        access_request_object_id NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        access_request_header_id NUMERIC(18,0) NOT NULL,
        object_code              NUMERIC(18,0) NOT NULL,
        can_read                 BIT NOT NULL DEFAULT 0,
        can_create               BIT NOT NULL DEFAULT 0,
        can_update               BIT NOT NULL DEFAULT 0,
        can_delete               BIT NOT NULL DEFAULT 0,
        is_deleted               BIT NULL DEFAULT 0,
        CONSTRAINT PK_access_request_object PRIMARY KEY (access_request_object_id)
    );

    CREATE INDEX IX_aro_header ON dbo.access_request_object (access_request_header_id);
    CREATE INDEX IX_aro_object ON dbo.access_request_object (object_code);
END;
GO

IF OBJECT_ID('dbo.access_request_stock', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.access_request_stock (
        access_request_stock_id  NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        access_request_header_id NUMERIC(18,0) NOT NULL,
        product_code             NUMERIC(18,0) NOT NULL,
        quantity_change          NUMERIC(18,0) NOT NULL,
        note                     NVARCHAR(500) NULL,
        is_deleted               BIT NULL DEFAULT 0,
        CONSTRAINT PK_access_request_stock PRIMARY KEY (access_request_stock_id)
    );

    CREATE INDEX IX_ars_header  ON dbo.access_request_stock (access_request_header_id);
    CREATE INDEX IX_ars_product ON dbo.access_request_stock (product_code);
END;
GO


/* ============================================================
   SECTION 5 — TRANSACTION LOG
   ============================================================ */

IF OBJECT_ID('dbo.users_data_transaction_log', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.users_data_transaction_log (
        users_data_transaction_log_id NUMERIC(18,0) IDENTITY(1,1) NOT NULL,
        title                         NVARCHAR(50)  NULL,
        user_id                       NUMERIC(18,0) NOT NULL,
        description                   NVARCHAR(500) NULL,
        [date]                        NVARCHAR(10)  NULL,
        CONSTRAINT PK_users_data_transaction_log PRIMARY KEY (users_data_transaction_log_id)
    );
END;
GO


/* ============================================================
   SECTION 6 — CONSTRAINTS
   ============================================================ */

/* Prevent duplicate (inventory, product) rows in stock */
IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = 'UQ_inventory_stock_inv_prod'
      AND object_id = OBJECT_ID('dbo.inventory_stock')
)
AND NOT EXISTS (
    SELECT inventory_code, product_code
    FROM dbo.inventory_stock
    GROUP BY inventory_code, product_code
    HAVING COUNT(*) > 1
)
BEGIN
    ALTER TABLE dbo.inventory_stock
        ADD CONSTRAINT UQ_inventory_stock_inv_prod
        UNIQUE (inventory_code, product_code);
END;
GO


/* ============================================================
   SECTION 7 — SEED LOOKUP DATA
   ============================================================ */

/* Access request types */
IF NOT EXISTS (SELECT 1 FROM dbo.access_request_type WHERE access_request_type_code = 1)
BEGIN
    INSERT INTO dbo.access_request_type (access_request_type_code, access_request_type_title)
    VALUES
        (1, N'TEMP_ACCESS'),
        (2, N'STOCK_ADJUSTMENT');
END;
GO

/* Access request statuses */
IF NOT EXISTS (SELECT 1 FROM dbo.access_request_status WHERE access_request_status_code = 1)
BEGIN
    INSERT INTO dbo.access_request_status (access_request_status_code, access_request_status_title)
    VALUES
        (1, N'PENDING'),
        (2, N'APPROVED'),
        (3, N'DENIED'),
        (4, N'EXPIRED'),
        (5, N'CANCELLED'),
        (6, N'APPLIED');
END;
GO

/* Fleet trip statuses */
IF NOT EXISTS (SELECT 1 FROM dbo.fleet_trip_status WHERE fleet_trip_status_code = 1)
BEGIN
    INSERT INTO dbo.fleet_trip_status (fleet_trip_status_code, fleet_trip_status_title)
    VALUES
        (1, N'PLANNED'),
        (2, N'EN_ROUTE'),
        (3, N'ARRIVED'),
        (4, N'COMPLETED'),
        (5, N'CANCELLED');
END;
GO

/* Product transaction types */
IF NOT EXISTS (
    SELECT 1 FROM dbo.product_transaction_type
    WHERE is_deleted = 0 OR is_deleted IS NULL
)
BEGIN
    INSERT INTO dbo.product_transaction_type (title, description, is_deleted)
    VALUES
        (N'DELIVERY_OUT',          N'Stock left this inventory via internal transfer',      0),
        (N'DELIVERY_IN',           N'Stock arrived at this inventory via internal transfer', 0),
        (N'EXTERNAL_RESUPPLY',     N'Stock purchased from an external supplier',             0),
        (N'STOCK_ADJUSTMENT_UP',   N'Manual upward correction approved by admin',            0),
        (N'STOCK_ADJUSTMENT_DOWN', N'Manual downward correction approved by admin',          0),
        (N'WASTE',                 N'Stock written off as spoilage',                         0),
        (N'LOSS',                  N'Stock written off as lost/damaged',                     0);
END;
GO

/* Product request types */
IF NOT EXISTS (
    SELECT 1 FROM dbo.inventory_product_request_type
    WHERE is_deleted = 0 OR is_deleted IS NULL
)
BEGIN
    INSERT INTO dbo.inventory_product_request_type
        (request_type_code, request_type_title, is_deleted)
    VALUES
        (1, N'INTERNAL_TRANSFER', 0),
        (2, N'EXTERNAL_RESUPPLY', 0);
END;
GO

/* Product request statuses */
IF NOT EXISTS (
    SELECT 1 FROM dbo.inventory_product_request_status
    WHERE is_deleted = 0 OR is_deleted IS NULL
)
BEGIN
    INSERT INTO dbo.inventory_product_request_status
        (request_status_code, request_status_title, is_deleted)
    VALUES
        (1, N'PENDING',    0),
        (2, N'APPROVED',   0),
        (3, N'DENIED',     0),
        (4, N'IN_TRANSIT', 0),
        (5, N'DELIVERED',  0),
        (6, N'CANCELLED',  0);
END;
GO


/* ============================================================
   SECTION 8 — OPTIONAL: SAMPLE DATA FOR TESTING
   ============================================================
   Uncomment if you want a few rows to play with immediately.
   ============================================================ */

/*
-- Inventory types
INSERT INTO dbo.inventory_type (inventory_type_title, inventory_type_code)
VALUES
    (N'Warehouse', 1),
    (N'Store',     2),
    (N'Dock',      3),
    (N'Factory',   4);

-- Warehouses (real Iranian coordinates so OSRM works)
INSERT INTO dbo.warehouse (warehouse_title, warehouse_code, warehouse_lat, warehouse_long)
VALUES
    (N'Tehran Central',  1001, 35.6892, 51.3890),
    (N'Isfahan Hub',     1002, 32.6546, 51.6680),
    (N'Mashhad Depot',   1003, 36.2970, 59.6062);

-- Inventories
INSERT INTO dbo.inventory (inventory_type_id, warehouse_id)
VALUES
    (1, 1),  -- Warehouse @ Tehran
    (1, 2),  -- Warehouse @ Isfahan
    (2, 3);  -- Store @ Mashhad

-- Products
INSERT INTO dbo.product (product_code, product_title)
VALUES
    (1001, N'Steel Bolts'),
    (1002, N'Copper Wire'),
    (1003, N'Cement Bags'),
    (1004, N'Fresh Produce');

-- Initial stock
INSERT INTO dbo.inventory_stock (inventory_code, product_code, stock_count)
VALUES
    (1, 1001, 500),
    (1, 1002, 200),
    (2, 1001, 300),
    (2, 1003, 150),
    (3, 1004, 80);

-- Fleet teams
INSERT INTO dbo.fleet_team (fleet_team_code, fleet_team_name)
VALUES
    (1, N'Alpha Team'),
    (2, N'Bravo Team');

-- Drivers
INSERT INTO dbo.drivers (drivers_code, drivers_first_name, drivers_last_name, drivers_phone_number, drivers_age)
VALUES
    (1, N'Ali',   N'Ahmadi', N'09120000001', 35),
    (2, N'Reza',  N'Karimi', N'09120000002', 42);

-- Vehicles
INSERT INTO dbo.vehicles (vehicle_code, vehicle_name, vehicle_licence_plate)
VALUES
    (1, N'Truck-01', N'IR-1234'),
    (2, N'Van-02',   N'IR-5678');
*/

PRINT 'IFMS dbo schema + seed data applied successfully.';
GO
