package IFMS;

import IFMS.PageRelatedEnums.WebPagesEnum;
import IFMS.ConfigAndLauncherManager.readConfig;
import IFMS.PageRelatedEnums.CrudQueriesEnum;
import IFMS.WebServerHandlers.apiManagement;
import com.sun.net.httpserver.HttpServer;
import IFMS.DataBase.DataBaseInit;


import java.io.IOException;

public class mainServerLaunch {

    public static void main(String[] args) throws IOException {

        HttpServer server = readConfig.initiate();

        // =====================================================
        // AUTH APIs
        // =====================================================

        server.createContext(
            "/LoginApi",
            new apiManagement.dataApiGen.builder()
                .shouldSetCookie(true)
                .shouldAuthenticate(false)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.Login)
                .setRedirectLocation("/Dashboard")
                .build()
        );

        server.createContext(
            "/getUsername",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(true)
                .setQuery(CrudQueriesEnum.getUsernameWithToken)
                .build()
        );

        server.createContext(
            "/currentUserApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(true)
                .setQuery(CrudQueriesEnum.CurrentUser)
                .build()
        );

        // =====================================================
        // INVENTORY CRUD APIs
        // =====================================================

        server.createContext(
            "/inventoryTypeApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.InventoryType)
                .build()
        );

        server.createContext(
            "/warehouseApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.Warehouse)
                .build()
        );

        server.createContext(
            "/inventoryApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.Inventory)
                .build()
        );

        server.createContext(
            "/inventoryUsersApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.InventoryUsers)
                .build()
        );

        server.createContext(
            "/productApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.Product)
                .build()
        );

        server.createContext(
            "/inventoryStockApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.InventoryStock)
                .build()
        );

        server.createContext(
            "/productTransactionTypeApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.ProductTransactionType)
                .build()
        );

        server.createContext(
            "/inventoryStockHistoryApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.InventoryStockHistory)
                .build()
        );

        server.createContext(
            "/inventoryProductRequestTypeApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.InventoryProductRequestType)
                .build()
        );

        server.createContext(
            "/inventoryProductRequestStatusApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.InventoryProductRequestStatus)
                .build()
        );

        server.createContext(
            "/inventoryProductRequestHeaderApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.InventoryProductRequestHeader)
                .build()
        );

        server.createContext(
            "/inventoryProductRequestDetailsApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.InventoryProductRequestDetails)
                .build()
        );

        // =====================================================
        // ACCESS REQUEST APIs
        // =====================================================

        server.createContext(
            "/accessRequestHeaderApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.AccessRequestHeader)
                .build()
        );

        server.createContext(
            "/accessRequestObjectApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.AccessRequestObject)
                .build()
        );

        server.createContext(
            "/accessRequestStockApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true)
                .sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.AccessRequestStock)
                .build()
        );

        // =====================================================
        // FLEET APIs
        // =====================================================

        server.createContext(
            "/fleetTeamApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.FleetTeam)
                .build()
        );

        server.createContext(
            "/driversApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.Drivers)
                .build()
        );

        server.createContext(
            "/vehiclesApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.Vehicles)
                .build()
        );

        server.createContext(
            "/fleetTeamDriversApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.FleetTeamDrivers)
                .build()
        );

        server.createContext(
            "/fleetTeamManagerApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.FleetTeamManager)
                .build()
        );

        server.createContext(
            "/fleetTeamTransportsApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.FleetTeamTransports)
                .build()
        );

        server.createContext(
            "/fleetTripApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.FleetTrip)
                .build()
        );


        server.createContext("/productRequestRouteInfoApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.ProductRequestRouteInfo)
                .build());
        
        server.createContext("/approveAccessRequestApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.ApproveAccessRequest)
                .build());

        server.createContext("/accessRequestUserStatusLookupApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.AccessRequestUserStatusLookup)
                .build());
        
        server.createContext("/denyAccessRequestApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.DenyAccessRequest)
                .build());
        server.createContext("/accessRequestHeaderTempPickerApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.AccessRequestHeaderTempPicker)
                .build());

        server.createContext("/accessRequestHeaderStockPickerApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.AccessRequestHeaderStockPicker)
                .build());

        server.createContext("/sysObjectsPickerApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.SysObjectsPicker)
                .build());
        
        server.createContext("/reportLowStockApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.ReportLowStock)
                .build());

        server.createContext("/reportFleetActivityApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.ReportFleetActivity)
                .build());

        server.createContext("/reportProductMovementApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.ReportProductMovement)
                .build());

        server.createContext("/reportTicketsApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.ReportTickets)
                .build());
        
        // =====================================================
        // LOOKUP APIs (for data-combo / find-object-box)
        // =====================================================

        server.createContext("/inventoryTypeLookupApi",  new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.InventoryTypeLookup).build());
        server.createContext("/warehouseLookupApi",      new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.WarehouseLookup).build());
        server.createContext("/inventoryLookupApi",      new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.InventoryLookup).build());
        server.createContext("/productLookupApi",        new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.ProductLookup).build());
        server.createContext("/productTransactionTypeLookupApi", new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.ProductTransactionTypeLookup).build());
        server.createContext("/inventoryProductRequestTypeLookupApi",   new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.InventoryProductRequestTypeLookup).build());
        server.createContext("/inventoryProductRequestStatusLookupApi", new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.InventoryProductRequestStatusLookup).build());
        server.createContext("/fleetTeamLookupApi",      new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.FleetTeamLookup).build());
        server.createContext("/driversLookupApi",        new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.DriversLookup).build());
        server.createContext("/vehiclesLookupApi",       new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.VehiclesLookup).build());
        server.createContext("/fleetTripStatusLookupApi",new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.FleetTripStatusLookup).build());
        server.createContext("/accessRequestTypeLookupApi",   new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.AccessRequestTypeLookup).build());
        server.createContext("/accessRequestStatusLookupApi", new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.AccessRequestStatusLookup).build());
        server.createContext("/usersLookupApi",          new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.UsersLookup).build());
        server.createContext("/sysObjectsLookupApi",     new apiManagement.dataApiGen.builder().shouldAuthenticate(true).sendTokenToDB(false).setQuery(CrudQueriesEnum.SysObjectsLookup).build());
                server.createContext("/inventoryProductRequestHeaderLookupApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.InventoryProductRequestHeaderLookup)
                .build());
                server.createContext("/fleetTeamPickerApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.FleetTeamPicker)
                .build());

        server.createContext("/usersPickerApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.UsersPicker)
                .build());
        server.createContext("/productRequestHeaderPickerApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.ProductRequestHeaderPicker)
                .build());

        server.createContext("/driversPickerApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.DriversPicker)
                .build());

        server.createContext("/vehiclesPickerApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.VehiclesPicker)
                .build());
        server.createContext("/inventoryPickerApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.InventoryPicker)
                .build());

        server.createContext("/productPickerApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.ProductPicker)
                .build());
        server.createContext("/warehousePickerApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.WarehousePicker)
                .build());
        
        server.createContext("/usersListApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.UsersList)
                .build());

        server.createContext("/permissionsListApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.PermissionsList)
                .build());
        server.createContext("/createUserApi",
            new apiManagement.dataApiGen.builder()
                .shouldAuthenticate(true).sendTokenToDB(false)
                .setQuery(CrudQueriesEnum.createUser)
                .build());
        // =====================================================
        // WEB PAGES
        // =====================================================

        WebPagesEnum.Login.shouldNotAuthenticate();

        WebPagesEnum.Login.registerRoute(server);
        WebPagesEnum.Dashboard.registerRoute(server);
        WebPagesEnum.Home.registerRoute(server);

        // Inventory section
        WebPagesEnum.InventoryManagement.registerRoute(server);
        WebPagesEnum.InventoryType.registerRoute(server);
        WebPagesEnum.Warehouse.registerRoute(server);
        WebPagesEnum.Inventory.registerRoute(server);
        WebPagesEnum.InventoryUsers.registerRoute(server);
        WebPagesEnum.Product.registerRoute(server);
        WebPagesEnum.InventoryStock.registerRoute(server);
        WebPagesEnum.ProductTransactionType.registerRoute(server);
        WebPagesEnum.InventoryStockHistory.registerRoute(server);
        WebPagesEnum.InventoryProductRequestType.registerRoute(server);
        WebPagesEnum.InventoryProductRequestStatus.registerRoute(server);
        WebPagesEnum.InventoryProductRequest.registerRoute(server);
        WebPagesEnum.InventoryProductRequestDetails.registerRoute(server);

        // Other sections
        WebPagesEnum.FleetManagement.registerRoute(server);
        WebPagesEnum.Reports.registerRoute(server);
        WebPagesEnum.ReportLowStock.registerRoute(server);
        WebPagesEnum.ReportFleetActivity.registerRoute(server);
        WebPagesEnum.ReportProductMovement.registerRoute(server);
        WebPagesEnum.ReportTickets.registerRoute(server);
        WebPagesEnum.AccessManagement.registerRoute(server);

        // Fleet forms
        WebPagesEnum.FleetTeam.registerRoute(server);
        WebPagesEnum.Drivers.registerRoute(server);
        WebPagesEnum.Vehicles.registerRoute(server);
        WebPagesEnum.FleetTeamDrivers.registerRoute(server);
        WebPagesEnum.FleetTeamManager.registerRoute(server);
        WebPagesEnum.FleetTrip.registerRoute(server);
        WebPagesEnum.FleetTeamTransports.registerRoute(server);

        // Access request forms
        WebPagesEnum.AccessRequests.registerRoute(server);
        WebPagesEnum.AccessRequestObject.registerRoute(server);
        WebPagesEnum.AccessRequestStock.registerRoute(server);
        WebPagesEnum.UserManagement.registerRoute(server);
        WebPagesEnum.Users.registerRoute(server);
        WebPagesEnum.Permissions.registerRoute(server);
        
        // =====================================================
        // FLUSH OBJECT QUEUE + SYNC PERMISSIONS
        // =====================================================
        // Page routes queue their object-registration SQL. We flush
        // it here and immediately re-sync the permission matrix.
        DataBaseInit.finalizeRegistration();
    }
}