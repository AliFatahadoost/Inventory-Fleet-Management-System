package IFMS.PageRelatedEnums;

import IFMS.InterFaces.JaliFiles;

public enum FilesEnum implements JaliFiles {

    // =========================================================
    // JALI FRAMEWORK FILES
    // =========================================================

    test(
        "/FrameWorksLib/Jali.js/test.html",
        true,
        FileTypesEnum.html
    ),

    // =========================================================
    // LEAFLET (bundled locally)
    // =========================================================

    leafletJs(
        "/FrameWorksLib/Leaflet/leaflet.js",
        true,
        FileTypesEnum.js
    ),

    leafletCss(
        "/FrameWorksLib/Leaflet/leaflet.css",
        true,
        FileTypesEnum.css
    ),

    leafletMarkerIcon(
        "/FrameWorksLib/Leaflet/images/marker-icon.png",
        true,
        FileTypesEnum.png
    ),

    leafletMarkerIcon2x(
        "/FrameWorksLib/Leaflet/images/marker-icon-2x.png",
        true,
        FileTypesEnum.png
    ),

    leafletMarkerShadow(
        "/FrameWorksLib/Leaflet/images/marker-shadow.png",
        true,
        FileTypesEnum.png
    ),

    // =========================================================
    // MAP ELEMENT
    // =========================================================

    mapBox(
        "/FrameWorksLib/Jali.js/custom_elements/mapBox.js",
        true,
        FileTypesEnum.js
    ),

    mapBoxCss(
        "/FrameWorksLib/JaliFrame.css/mapBox.css",
        true,
        FileTypesEnum.css
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

    findObjectBox(
        "/FrameWorksLib/Jali.js/custom_elements/findObjectBox.js",
        true,
        FileTypesEnum.js
    ),

    dateBox(
        "/FrameWorksLib/Jali.js/custom_elements/dateBox.js",
        true,
        FileTypesEnum.js
    ),
    
    fleetTripTable(
        "/FrameWorksLib/Jali.js/custom_elements/fleetTripTable.js",
        true,
        FileTypesEnum.js
    ),

    jaliForm(
        "/FrameWorksLib/Jali.js/custom_elements/jaliForm.js",
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

    dateBoxCss(
        "/FrameWorksLib/JaliFrame.css/dateBox.css",
        true,
        FileTypesEnum.css
    ),

    cssDataForm(
        "/FrameWorksLib/JaliFrame.css/dataForm.css",
        true,
        FileTypesEnum.css
    ),

    // =========================================================
    // SYSTEM PAGES
    // =========================================================

    Login(
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
        "/Home/Home.html",
        false,
        FileTypesEnum.html
    ),

    // =========================================================
    // MANAGEMENT PAGES
    // =========================================================

    InventoryManagement(
        "/InventoryManagement/InventoryManagement.html",
        false,
        FileTypesEnum.html
    ),

    FleetManagement(
        "/FleetManagement/FleetManagement.html",
        false,
        FileTypesEnum.html
    ),

    Reports(
        "/Reports/Reports.html",
        false,
        FileTypesEnum.html
    ),

    UserManagement(
        "/UserManagement/UserManagement.html",
        false,
        FileTypesEnum.html
    ),

    AccessManagement(
        "/AccessManagement/AccessManagement.html",
        false,
        FileTypesEnum.html
    ),
    
    ReportLowStock(
        "/Reports/ReportLowStock.html",
        false,
        FileTypesEnum.html
    ),

    ReportFleetActivity(
        "/Reports/ReportFleetActivity.html",
        false,
        FileTypesEnum.html
    ),

    ReportProductMovement(
        "/Reports/ReportProductMovement.html",
        false,
        FileTypesEnum.html
    ),

    ReportTickets(
        "/Reports/ReportTickets.html",
        false,
        FileTypesEnum.html
    ),

    // =========================================================
    // INVENTORY FORMS
    // =========================================================

    InventoryType(
        "/InventoryManagement/InventoryType.html",
        false,
        FileTypesEnum.html
    ),

    Warehouse(
        "/InventoryManagement/Warehouse.html",
        false,
        FileTypesEnum.html
    ),

    Inventory(
        "/InventoryManagement/Inventory.html",
        false,
        FileTypesEnum.html
    ),

    InventoryUsers(
        "/InventoryManagement/InventoryUsers.html",
        false,
        FileTypesEnum.html
    ),

    Product(
        "/InventoryManagement/Product.html",
        false,
        FileTypesEnum.html
    ),

    InventoryStock(
        "/InventoryManagement/InventoryStock.html",
        false,
        FileTypesEnum.html
    ),

    ProductTransactionType(
        "/InventoryManagement/ProductTransactionType.html",
        false,
        FileTypesEnum.html
    ),

    InventoryStockHistory(
        "/InventoryManagement/InventoryStockHistory.html",
        false,
        FileTypesEnum.html
    ),

    InventoryProductRequestType(
        "/InventoryManagement/InventoryProductRequestType.html",
        false,
        FileTypesEnum.html
    ),

    InventoryProductRequestStatus(
        "/InventoryManagement/InventoryProductRequestStatus.html",
        false,
        FileTypesEnum.html
    ),

    InventoryProductRequest(
        "/InventoryManagement/InventoryProductRequest.html",
        false,
        FileTypesEnum.html
    ),

    InventoryProductRequestDetails(
        "/InventoryManagement/InventoryProductRequestDetails.html",
        false,
        FileTypesEnum.html
    ),

    // =========================================================
    // FLEET FORMS
    // =========================================================

    FleetTeam(
        "/FleetManagement/FleetTeam.html",
        false,
        FileTypesEnum.html
    ),

    Drivers(
        "/FleetManagement/Drivers.html",
        false,
        FileTypesEnum.html
    ),

    Vehicles(
        "/FleetManagement/Vehicles.html",
        false,
        FileTypesEnum.html
    ),

    FleetTeamDrivers(
        "/FleetManagement/FleetTeamDrivers.html",
        false,
        FileTypesEnum.html
    ),

    FleetTeamManager(
        "/FleetManagement/FleetTeamManager.html",
        false,
        FileTypesEnum.html
    ),

    FleetTrip(
        "/FleetManagement/FleetTrip.html",
        false,
        FileTypesEnum.html
    ),

    FleetTeamTransports(
        "/FleetManagement/FleetTeamTransports.html",
        false,
        FileTypesEnum.html
    ),
    
    Users(
        "/UserManagement/Users.html",
        false,
        FileTypesEnum.html
    ),

    Permissions(
        "/UserManagement/Permissions.html",
        false,
        FileTypesEnum.html
    ),

    // =========================================================
    // ACCESS REQUEST FORMS
    // =========================================================

    AccessRequests(
        "/AccessManagement/AccessRequests.html",
        false,
        FileTypesEnum.html
    ),

    AccessRequestObject(
        "/AccessManagement/AccessRequestObject.html",
        false,
        FileTypesEnum.html
    ),

    AccessRequestStock(
        "/AccessManagement/AccessRequestStock.html",
        false,
        FileTypesEnum.html
    );

    private final String relativeAddress;
    private final boolean loadedByIframe;
    private final FileTypesEnum fileType;

    FilesEnum(
        String relativeAddress,
        boolean loadedByIframe,
        FileTypesEnum fileType
    )
    {
        this.relativeAddress = relativeAddress;
        this.loadedByIframe = loadedByIframe;
        this.fileType = fileType;
    }

    @Override
    public String relativeAddress()
    {
        return this.relativeAddress;
    }

    @Override
    public boolean loadedByIframe()
    {
        return this.loadedByIframe;
    }

    @Override
    public String getFileType()
    {
        return this.fileType.getTechnicalType();
    }
}