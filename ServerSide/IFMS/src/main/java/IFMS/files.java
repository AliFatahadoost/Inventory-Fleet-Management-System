
package IFMS;

public enum files {
    
    Login(
        "/Login/Login.html",
        false,
        1
    ),
    LoginPage(
        "/Login/Login.html",
        false,
        2
    ),
    Dashboard(
        "/Dashboard/Dashboard.html",
        false,
        3
    ),
    Home(
        "/Dashboard/dashboardSubSections/homeDashboard/Home.html",
        true,
        4
    ),
    UserProfile(
        "/Dashboard/dashboardSubSections/userProfile/userProfile.html",
        true,
        5
    ),
    UserTasks(
        "/Dashboard/dashboardSubSections/userProfile/userTasks.html",
        true,
        6
    ),
    WorkHourReports(
        "/Dashboard/dashboardSubSections/userProfile/WorkHourReports.html",
        true,
        7
    ),
    ActivityLog(
        "/Dashboard/dashboardSubSections/userProfile/ActivityLog.html",
        true,
        8
    ),
    InventoryManagement(
        "/Dashboard/dashboardSubSections/inventoryManagement/inventoryManagement.html",
        true,
        9
    ),
    InventoryUsersTasks(
        "/Dashboard/dashboardSubSections/inventoryManagement/inventoryUsersTasks.html",
        true,
        19
    ),
    Products(
        "/Dashboard/dashboardSubSections/inventoryManagement/products.html",
        true,
        16
    ),
    ProductsCategories(
        "/Dashboard/dashboardSubSections/inventoryManagement/productsCategories.html",
        true,
        17
    ),
    ProductsMovementAndLog(
        "/Dashboard/dashboardSubSections/inventoryManagement/productsMovementAndLog.html",
        true,
        20
    ),
    StockLevels(
        "/Dashboard/dashboardSubSections/inventoryManagement/stockLevels.html",
        true,
        18
    ),
    WarehouseManagement(
        "/Dashboard/dashboardSubSections/warehouseManagement.html",
        true,
        10
    ),
    FleetManagement(
        "/Dashboard/dashboardSubSections/fleetManagement.html",
        true,
        11
    ),
    ReportSection(
        "/Dashboard/dashboardSubSections/reportSection.html",
        true,
        12
    ),
    UsersManagement(
        "/Dashboard/dashboardSubSections/userManagement/usersManagement.html",
        true,
        13
    ),
    EditUsersForm(
        "/Dashboard/dashboardSubSections/userManagement/EditUsersForm.html",
        true,
        14
    ),
    EditRolesForm(
        "/Dashboard/dashboardSubSections/userManagement/EditRolesForm.html",
        true,
        15
    ),
    cargoSent(
        "/Dashboard/dashboardSubSections/homeDashboard/cargoSent.html",
        true,
        26
    ),
    setOwnerShipFleet(
        "/Dashboard/dashboardSubSections/homeDashboard/setOwnerShipFleet.html",
        true,
        22
    ),
    setOwnerShipInventory(
        "/Dashboard/dashboardSubSections/homeDashboard/setOwnerShipInventory.html",
        true,
        21
    ),
    giveUserTask(
        "/Dashboard/dashboardSubSections/homeDashboard/giveUserTask.html",
        true,
        23
    ),
    setUserSalary(
        "/Dashboard/dashboardSubSections/homeDashboard/setUserSalary.html",
        true,
        24
    ),
    productArrival(
        "/Dashboard/dashboardSubSections/homeDashboard/productArrival.html",
        true,
        25
    ),
    inventoryOperations(
        "/Dashboard/dashboardSubSections/homeDashboard/inventoryOperations.html",
        true,
        27
    ),
    productsPricing(
        "/Dashboard/dashboardSubSections/homeDashboard/productsPricing.html",
        true,
        28
    ),
    vendors(
        "/Dashboard/dashboardSubSections/homeDashboard/vendors.html",
        true,
        29
    );
    
    private final String relativeAddress;
    private final boolean loadedByIframe;
    private final int accessCode;
    
    files(String relativeAddress, boolean loadedByIframe, final int accessCode)
    {
        this.relativeAddress = relativeAddress;
        this.loadedByIframe = loadedByIframe;
        this.accessCode = accessCode;
    }
    
    public String relativeAddress(){return this.relativeAddress;}
    public boolean loadedByIframe(){return this.loadedByIframe;}
    public int accessCode(){return this.accessCode;}
    
}
