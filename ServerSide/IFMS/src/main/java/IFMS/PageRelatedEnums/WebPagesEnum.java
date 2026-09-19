package IFMS.PageRelatedEnums;

import com.sun.net.httpserver.HttpServer;
import IFMS.ConfigAndLauncherManager.readConfig;
import IFMS.WebServerHandlers.pageHandlerOpener;
import IFMS.InterFaces.JaliWebPage;
import IFMS.DataBase.DataBaseInit;

public enum WebPagesEnum implements JaliWebPage {

    // =========================================================
    // SYSTEM
    // =========================================================

    Login(
        1,
        FilesEnum.Login,
        "/"
    ),

    Dashboard(
        2,
        FilesEnum.Dashboard,
        "/Dashboard"
    ),

    Home(
        3,
        FilesEnum.Home,
        "/home"
    ),

    // =========================================================
    // MANAGEMENT SECTIONS
    // =========================================================

    InventoryManagement(
        10,
        FilesEnum.InventoryManagement,
        "/inventoryManagement"
    ),

    FleetManagement(
        20,
        FilesEnum.FleetManagement,
        "/fleetManagement"
    ),

    Reports(
        30,
        FilesEnum.Reports,
        "/reports"
    ),

    UserManagement(
        40,
        FilesEnum.UserManagement,
        "/userManagement"
    ),

    AccessManagement(
        50,
        FilesEnum.AccessManagement,
        "/accessManagement"
    ),

    // =========================================================
    // INVENTORY FORMS
    // =========================================================

    InventoryType( 100, FilesEnum.InventoryType, "/inventoryType" ),
    Warehouse( 101, FilesEnum.Warehouse, "/warehouse" ),
    Inventory( 102, FilesEnum.Inventory, "/inventory" ),
    InventoryUsers( 103, FilesEnum.InventoryUsers, "/inventoryUsers" ),
    Product( 104, FilesEnum.Product, "/product" ),
    InventoryStock( 105, FilesEnum.InventoryStock, "/inventoryStock" ),
    ProductTransactionType( 106, FilesEnum.ProductTransactionType, "/productTransactionType" ),
    InventoryStockHistory( 107, FilesEnum.InventoryStockHistory, "/inventoryStockHistory" ),
    InventoryProductRequestType( 108, FilesEnum.InventoryProductRequestType, "/inventoryProductRequestType" ),
    InventoryProductRequestStatus( 109, FilesEnum.InventoryProductRequestStatus, "/inventoryProductRequestStatus" ),
    InventoryProductRequest( 110, FilesEnum.InventoryProductRequest, "/inventoryProductRequest" ),
    InventoryProductRequestDetails( 111, FilesEnum.InventoryProductRequestDetails, "/inventoryProductRequestDetails" ),
    ReportLowStock( 400, FilesEnum.ReportLowStock, "/reportLowStock" ),
    ReportFleetActivity( 401, FilesEnum.ReportFleetActivity, "/reportFleetActivity" ),
    ReportProductMovement( 402, FilesEnum.ReportProductMovement, "/reportProductMovement" ),
    ReportTickets( 403, FilesEnum.ReportTickets, "/reportTickets" ),

    // =========================================================
    // FLEET FORMS
    // =========================================================

    FleetTeam( 200, FilesEnum.FleetTeam, "/fleetTeam" ),
    Drivers( 201, FilesEnum.Drivers, "/drivers" ),
    Vehicles( 202, FilesEnum.Vehicles, "/vehicles" ),
    FleetTeamDrivers( 203, FilesEnum.FleetTeamDrivers, "/fleetTeamDrivers" ),
    FleetTeamManager( 204, FilesEnum.FleetTeamManager, "/fleetTeamManager" ),
    FleetTrip( 205, FilesEnum.FleetTrip, "/fleetTrip" ),
    FleetTeamTransports( 207, FilesEnum.FleetTeamTransports, "/fleetTeamTransports" ),
    Users( 41, FilesEnum.Users, "/users" ),
    Permissions( 42, FilesEnum.Permissions, "/permissions" ),

    // =========================================================
    // ACCESS REQUEST FORMS
    // =========================================================

    AccessRequests( 300, FilesEnum.AccessRequests, "/accessRequests" ),
    AccessRequestObject( 301, FilesEnum.AccessRequestObject, "/accessRequestObject" ),
    AccessRequestStock( 302, FilesEnum.AccessRequestStock, "/accessRequestStock" );

    private final int objectId;
    private final FilesEnum pageFile;
    private final String route;

    private boolean shouldAuth = true;

    WebPagesEnum(
        int objectId,
        FilesEnum pageFile,
        String route
    )
    {
        this.objectId = objectId;
        this.pageFile = pageFile;
        this.route = route;
    }

    public void shouldNotAuthenticate()
    {
        this.shouldAuth = false;
    }

    @Override
    public int getObjectId()
    {
        return this.objectId;
    }

    @Override
    public FilesEnum getFile()
    {
        return this.pageFile;
    }

    @Override
    public void registerRoute(HttpServer server)
    {
        DataBaseInit.loadObjectIntoObjectList(
            objectId,
            this.name()
        );

        server.createContext(
            this.route,
            new pageHandlerOpener(
                readConfig.BASE_FILE_ADDRESS,
                this.pageFile,
                this.shouldAuth
            )
        );
    }
}