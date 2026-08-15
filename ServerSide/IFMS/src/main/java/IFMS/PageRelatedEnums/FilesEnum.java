
package IFMS.PageRelatedEnums;

public enum FilesEnum {
    
    Login(
        "/Login/Login.html",
        false,
        FileTypesEnum.html
    ),
    LoginPage(
        "/Login/Login.html",
        false,
        FileTypesEnum.html
    ),
    Dashboard(
        "/Dashboard/Dashboard.html",
        false,
        FileTypesEnum.html
    ),
    Home(
        "/Dashboard/dashboardSubSections/homeDashboard/Home.html",
        true,
        FileTypesEnum.html
    ),
    UserProfile(
        "/Dashboard/dashboardSubSections/userProfile/userProfile.html",
        true,
        FileTypesEnum.html
    ),
    UserTasks(
        "/Dashboard/dashboardSubSections/userProfile/userTasks.html",
        true,
        FileTypesEnum.html
    ),
    WorkHourReports(
        "/Dashboard/dashboardSubSections/userProfile/WorkHourReports.html",
        true,
        FileTypesEnum.html
    ),
    ActivityLog(
        "/Dashboard/dashboardSubSections/userProfile/ActivityLog.html",
        true,
        FileTypesEnum.html
    ),
    InventoryManagement(
        "/Dashboard/dashboardSubSections/inventoryManagement/inventoryManagement.html",
        true,
        FileTypesEnum.html
    ),
    InventoryUsersTasks(
        "/Dashboard/dashboardSubSections/inventoryManagement/inventoryUsersTasks.html",
        true,
        FileTypesEnum.html
    ),
    Products(
        "/Dashboard/dashboardSubSections/inventoryManagement/products.html",
        true,
        FileTypesEnum.html
    ),
    ProductsCategories(
        "/Dashboard/dashboardSubSections/inventoryManagement/productsCategories.html",
        true,
        FileTypesEnum.html
    ),
    ProductsMovementAndLog(
        "/Dashboard/dashboardSubSections/inventoryManagement/productsMovementAndLog.html",
        true,
        FileTypesEnum.html
    ),
    StockLevels(
        "/Dashboard/dashboardSubSections/inventoryManagement/stockLevels.html",
        true,
        FileTypesEnum.html
    ),
    WarehouseManagement(
        "/Dashboard/dashboardSubSections/warehouseManagement.html",
        true,
        FileTypesEnum.html
    ),
    FleetManagement(
        "/Dashboard/dashboardSubSections/fleetManagement.html",
        true,
        FileTypesEnum.html
    ),
    ReportSection(
        "/Dashboard/dashboardSubSections/reportSection.html",
        true,
        FileTypesEnum.html
    ),
    UsersManagement(
        "/Dashboard/dashboardSubSections/userManagement/usersManagement.html",
        true,
        FileTypesEnum.html
    ),
    EditUsersForm(
        "/Dashboard/dashboardSubSections/userManagement/EditUsersForm.html",
        true,
        FileTypesEnum.html
    ),
    EditRolesForm(
        "/Dashboard/dashboardSubSections/userManagement/EditRolesForm.html",
        true,
        FileTypesEnum.html
    ),
    cargoSent(
        "/Dashboard/dashboardSubSections/homeDashboard/cargoSent.html",
        true,
        FileTypesEnum.html
    ),
    setOwnerShipFleet(
        "/Dashboard/dashboardSubSections/homeDashboard/setOwnerShipFleet.html",
        true,
        FileTypesEnum.html
    ),
    setOwnerShipInventory(
        "/Dashboard/dashboardSubSections/homeDashboard/setOwnerShipInventory.html",
        true,
        FileTypesEnum.html
    ),
    giveUserTask(
        "/Dashboard/dashboardSubSections/homeDashboard/giveUserTask.html",
        true,
        FileTypesEnum.html
    ),
    setUserSalary(
        "/Dashboard/dashboardSubSections/homeDashboard/setUserSalary.html",
        true,
        FileTypesEnum.html
    ),
    productArrival(
        "/Dashboard/dashboardSubSections/homeDashboard/productArrival.html",
        true,
        FileTypesEnum.html
    ),
    inventoryOperations(
        "/Dashboard/dashboardSubSections/homeDashboard/inventoryOperations.html",
        true,
        FileTypesEnum.html
    ),
    productsPricing(
        "/Dashboard/dashboardSubSections/homeDashboard/productsPricing.html",
        true,
        FileTypesEnum.html
    ),
    vendors(
        "/Dashboard/dashboardSubSections/homeDashboard/vendors.html",
        true,
        FileTypesEnum.html
    ),
    test(
        "/FrameWorksLib/Jali.js/test.html",
        true,
        FileTypesEnum.html
    ),
    coreJs(
        "/FrameWorksLib/Jali.js/core.js",
        true,
        FileTypesEnum.js
    ),
    tableFormElement(
        "/FrameWorksLib/Jali.js/custom_elements/dataTable.js",
        true,
        FileTypesEnum.js
    ),
    dataCombo(
        "/FrameWorksLib/Jali.js/custom_elements/dataCombo.js",
        true,
        FileTypesEnum.js
    ),
    cssTableFormData(
        "/FrameWorksLib/JaliFrame.css/readDataTable.css",
        true,
        FileTypesEnum.css
    ),
    dataComboCss(
        "/FrameWorksLib/JaliFrame.css/dataCombo.css",
        true,
        FileTypesEnum.css
    ),
    findObjectBoxCss(
        "/FrameWorksLib/JaliFrame.css/FindObjectBox.css",
        true,
        FileTypesEnum.css
    ),
    cssDataForm(
        "/FrameWorksLib/JaliFrame.css/dataForm.css",
        true,
        FileTypesEnum.css
    );
    
    private final String relativeAddress;
    private final boolean loadedByIframe;
    private final FileTypesEnum fileType;//text/html or text/JavaScript for example
    
    FilesEnum(String relativeAddress, boolean loadedByIframe, FileTypesEnum fileType)
    {
        this.relativeAddress = relativeAddress;
        this.loadedByIframe = loadedByIframe;
        this.fileType = fileType;
        
    }
    
    public String relativeAddress(){return this.relativeAddress;}
    public boolean loadedByIframe(){return this.loadedByIframe;}
    public String getFileType(){return this.fileType.getTechnicalType();} 
    
    
}
