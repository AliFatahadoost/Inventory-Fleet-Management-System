package IFMS.PageRelatedEnums;

import IFMS.InterFaces.CrudQueries;
import IFMS.DataBase.GenerateGenericSQLQuery;

public enum CrudQueriesEnum implements CrudQueries
{

    // =========================================================
    // =========================================================
    // SECTION 1 — AUTH / USERS
    // =========================================================
    // =========================================================

    Login
    (
        "",
        "",
        "EXEC USERS_DATA_AND_PERMISSIONS.SHOULD_LOGIN ?, ?;",
        "",
        1
    ),
    
        UsersList
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @Code NVARCHAR(50)     = NULLIF(?, N'') \n" +
        " DECLARE @Username NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   SYS_USERS_ID \n" +
        "  ,SYS_USER_CODE \n" +
        "  ,SYS_USERNAME \n" +
        " FROM IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_USERS \n" +
        " WHERE 1 = 1 \n" +
        " AND CAST(SYS_USER_CODE AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Code,     N'')+N'%' \n" +
        " AND CAST(SYS_USERNAME  AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Username, N'')+N'%' \n" +
        " ORDER BY SYS_USER_CODE \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "",
        "",
        "",
        41
    ),

    PermissionsList
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @Username NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @Object   NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @Read     NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @Create   NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @Update   NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @Delete   NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   P.OBJECT_USER_PERMISSION \n" +
        "  ,ISNULL(U.SYS_USERNAME, N'(unknown)') AS username \n" +
        "  ,ISNULL(O.OBJECT_TITLE, N'(unknown)') AS object_title \n" +
        "  ,P.CAN_READ \n" +
        "  ,P.CAN_CREATE \n" +
        "  ,P.CAN_UPDATE \n" +
        "  ,P.CAN_DELETE \n" +
        " FROM IFMS_DB.USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION P \n" +
        " LEFT JOIN IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_USERS   U ON U.SYS_USER_CODE = P.USER_CODE \n" +
        " LEFT JOIN IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS O ON O.OBJECT_CODE   = P.OBJECT_CODE \n" +
        " WHERE 1 = 1 \n" +
        " AND ISNULL(U.SYS_USERNAME, N'') LIKE N'%'+ISNULL(@Username, N'')+N'%' \n" +
        " AND ISNULL(O.OBJECT_TITLE, N'') LIKE N'%'+ISNULL(@Object,   N'')+N'%' \n" +
        " AND CAST(P.CAN_READ   AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Read,   N'')+N'%' \n" +
        " AND CAST(P.CAN_CREATE AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Create, N'')+N'%' \n" +
        " AND CAST(P.CAN_UPDATE AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Update, N'')+N'%' \n" +
        " AND CAST(P.CAN_DELETE AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Delete, N'')+N'%' \n" +
        " ORDER BY U.SYS_USER_CODE, O.OBJECT_CODE \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "",
        "",
        "",
        42
    ),

    createUser
    (
        "",
        "",
        "EXEC USERS_DATA_AND_PERMISSIONS.MAKE_NEW_USER ?, ?, ?;",
        ""
    ),

    getUsernameWithToken
    (
        "SELECT SYS_USERNAME " +
        "FROM IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_USERS " +
        "WHERE SYS_LOGIN_SESSION = ?",
        "",
        "",
        ""
    ),

    // Returns the current user's code + name based on the session token.
    // Used by front-end to attribute tickets and log entries.
    // Requires sendTokenToDB(true) in apiManagement wiring.
    CurrentUser
    (
        "SELECT SYS_USER_CODE AS user_code, SYS_USERNAME AS username " +
        "FROM IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_USERS " +
        "WHERE SYS_LOGIN_SESSION = ?",
        "",
        "",
        ""
    ),

    // All users for the UserManagement page.
    AllUsers
    (
        "SELECT SYS_USERS_ID, SYS_USER_CODE, SYS_USERNAME " +
        "FROM IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_USERS " +
        "ORDER BY SYS_USER_CODE",
        "",
        "",
        ""
    ),

    // Full permission matrix view (for UserManagement page).
    AllUserObjectPermissions
    (
        "SELECT P.OBJECT_USER_PERMISSION, P.USER_CODE, " +
        "       O.OBJECT_CODE, O.OBJECT_TITLE, " +
        "       P.CAN_READ, P.CAN_CREATE, P.CAN_UPDATE, P.CAN_DELETE " +
        "FROM IFMS_DB.USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION P " +
        "INNER JOIN IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS O " +
        "    ON O.OBJECT_CODE = P.OBJECT_CODE " +
        "ORDER BY P.USER_CODE, O.OBJECT_CODE",
        "",
        "",
        ""
    ),

    // Toggle a single permission row.
    UpdateUserObjectPermission
    (
        "",
        "UPDATE IFMS_DB.USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION " +
        "SET CAN_READ   = ISNULL(NULLIF(?, ''), CAN_READ), " +
        "    CAN_CREATE = ISNULL(NULLIF(?, ''), CAN_CREATE), " +
        "    CAN_UPDATE = ISNULL(NULLIF(?, ''), CAN_UPDATE), " +
        "    CAN_DELETE = ISNULL(NULLIF(?, ''), CAN_DELETE) " +
        "WHERE OBJECT_USER_PERMISSION = ?",
        "",
        ""
    ),


    // =========================================================
    // =========================================================
    // SECTION 2 — INVENTORY MANAGEMENT
    // =========================================================
    // =========================================================

    // ---------------------------------------------------------
    // INVENTORY TYPE  (object 100)
    // ---------------------------------------------------------

    InventoryType
    (
        new GenerateGenericSQLQuery.ReadQuery()
            .setTableName("inventory_type")
            .setColumnNames(
                "inventory_id",
                "inventory_type_title",
                "inventory_type_code"
            )
            .setColumnDataTypes(
                "numeric",
                "nvarchar(50)",
                "numeric"
            )
            .setWhereQuery("AND isnull(is_deleted, 0) = 0")
            .getQuery(),

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("inventory_type")
            .setColumnNames(
                "inventory_type_title",
                "inventory_type_code"
            )
            .setWhereQuery("inventory_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("inventory_type")
            .setColumnNames(
                "inventory_type_title",
                "inventory_type_code"
            )
            .getQuery(),

        "UPDATE inventory_type SET is_deleted = 1 WHERE inventory_id = ?",

        100
    ),


    // ---------------------------------------------------------
    // WAREHOUSE  (object 101)
    // ---------------------------------------------------------

    Warehouse
    (
        new GenerateGenericSQLQuery.ReadQuery()
            .setTableName("warehouse")
            .setColumnNames(
                "warehouse_id",
                "warehouse_title",
                "warehouse_code",
                "warehouse_lat",
                "warehouse_long"
            )
            .setColumnDataTypes(
                "numeric",
                "nvarchar(50)",
                "numeric",
                "decimal(9,6)",
                "decimal(9,6)"
            )
            .setWhereQuery("AND isnull(is_deleted, 0) = 0")
            .getQuery(),

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("warehouse")
            .setColumnNames(
                "warehouse_title",
                "warehouse_code",
                "warehouse_lat",
                "warehouse_long"
            )
            .setWhereQuery("warehouse_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("warehouse")
            .setColumnNames(
                "warehouse_title",
                "warehouse_code",
                "warehouse_lat",
                "warehouse_long"
            )
            .getQuery(),

        "UPDATE warehouse SET is_deleted = 1 WHERE warehouse_id = ?",

        101
    ),


    // ---------------------------------------------------------
    // INVENTORY  (object 102)
    // ---------------------------------------------------------

    Inventory
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @inventory_type_title NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @warehouse_title NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   I.inventory_id \n" +
        "  ,IT.inventory_type_title \n" +
        "  ,W.warehouse_title \n" +
        " FROM dbo.inventory I \n" +
        " INNER JOIN dbo.inventory_type IT ON IT.inventory_id = I.inventory_type_id \n" +
        " INNER JOIN dbo.warehouse      W  ON W.warehouse_id  = I.warehouse_id \n" +
        " WHERE 1 = 1 \n" +
        " AND CAST(IT.inventory_type_title AS NVARCHAR(50)) LIKE N'%'+ISNULL(@inventory_type_title, N'')+N'%' \n" +
        " AND CAST(W.warehouse_title      AS NVARCHAR(50)) LIKE N'%'+ISNULL(@warehouse_title,      N'')+N'%' \n" +
        " AND isnull(I.is_deleted, 0) = 0 \n" +
        " ORDER BY I.inventory_id \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("inventory")
            .setColumnNames(
                "inventory_type_id",
                "warehouse_id"
            )
            .setWhereQuery("inventory_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("inventory")
            .setColumnNames(
                "inventory_type_id",
                "warehouse_id"
            )
            .getQuery(),

        "UPDATE inventory SET is_deleted = 1 WHERE inventory_id = ?",

        102
    ),


    // ---------------------------------------------------------
    // INVENTORY USERS  (object 103)
    // ---------------------------------------------------------

            InventoryUsers
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @inventory_display NVARCHAR(200) = NULLIF(?, N'') \n" +
        " DECLARE @username NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   IU.inventory_users_id \n" +
        "  ,ISNULL(IT.inventory_type_title, N'') + N' — ' + ISNULL(W.warehouse_title, N'') AS inventory_display \n" +
        "  ,ISNULL(U.SYS_USERNAME, N'(unknown)') AS username \n" +
        " FROM dbo.inventory_users IU \n" +
        " LEFT JOIN dbo.inventory      I  ON I.inventory_id      = IU.inventory_id \n" +
        " LEFT JOIN dbo.inventory_type IT ON IT.inventory_id     = I.inventory_type_id \n" +
        " LEFT JOIN dbo.warehouse      W  ON W.warehouse_id      = I.warehouse_id \n" +
        " LEFT JOIN IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_USERS U ON U.SYS_USER_CODE = IU.user_id \n" +
        " WHERE 1 = 1 \n" +
        " AND CAST(ISNULL(IT.inventory_type_title, N'') + N' — ' + ISNULL(W.warehouse_title, N'') AS NVARCHAR(200)) LIKE N'%'+ISNULL(@inventory_display, N'')+N'%' \n" +
        " AND CAST(U.SYS_USERNAME AS NVARCHAR(50)) LIKE N'%'+ISNULL(@username, N'')+N'%' \n" +
        " AND isnull(IU.is_deleted, 0) = 0 \n" +
        " ORDER BY IU.inventory_users_id \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("inventory_users")
            .setColumnNames(
                "inventory_id",
                "user_id"
            )
            .setWhereQuery("inventory_users_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("inventory_users")
            .setColumnNames(
                "inventory_id",
                "user_id"
            )
            .getQuery(),

        "UPDATE inventory_users SET is_deleted = 1 WHERE inventory_users_id = ?",

        103
    ),


    // ---------------------------------------------------------
    // PRODUCT  (object 104)
    // ---------------------------------------------------------

    Product
    (
        new GenerateGenericSQLQuery.ReadQuery()
            .setTableName("product")
            .setColumnNames(
                "product_id",
                "product_code",
                "product_title"
            )
            .setColumnDataTypes(
                "numeric",
                "numeric",
                "nvarchar(50)"
            )
            .setWhereQuery("AND isnull(is_deleted, 0) = 0")
            .getQuery(),

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("product")
            .setColumnNames(
                "product_code",
                "product_title"
            )
            .setWhereQuery("product_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("product")
            .setColumnNames(
                "product_code",
                "product_title"
            )
            .getQuery(),

        "UPDATE product SET is_deleted = 1 WHERE product_id = ?",

        104
    ),


    // ---------------------------------------------------------
    // INVENTORY STOCK  (object 105)
    // ---------------------------------------------------------

        InventoryStock
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @inventory_display NVARCHAR(200) = NULLIF(?, N'') \n" +
        " DECLARE @product_title NVARCHAR(50)       = NULLIF(?, N'') \n" +
        " DECLARE @stock_count NVARCHAR(50)         = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   IS_.inventory_stock_id \n" +
        "  ,ISNULL(IT.inventory_type_title, N'') + N' — ' + ISNULL(W.warehouse_title, N'') AS inventory_display \n" +
        "  ,ISNULL(P.product_title, N'') AS product_title \n" +
        "  ,IS_.stock_count \n" +
        "  ,IS_.inventory_code \n" +
        "  ,IS_.product_code \n" +
        " FROM dbo.inventory_stock IS_ \n" +
        " LEFT JOIN dbo.inventory      I  ON I.inventory_id     = IS_.inventory_code \n" +
        " LEFT JOIN dbo.inventory_type IT ON IT.inventory_id    = I.inventory_type_id \n" +
        " LEFT JOIN dbo.warehouse      W  ON W.warehouse_id     = I.warehouse_id \n" +
        " LEFT JOIN dbo.product        P  ON P.product_code     = IS_.product_code \n" +
        " WHERE 1 = 1 \n" +
        " AND CAST(ISNULL(IT.inventory_type_title, N'') + N' — ' + ISNULL(W.warehouse_title, N'') AS NVARCHAR(200)) LIKE N'%'+ISNULL(@inventory_display, N'')+N'%' \n" +
        " AND CAST(ISNULL(P.product_title, N'') AS NVARCHAR(50)) LIKE N'%'+ISNULL(@product_title, N'')+N'%' \n" +
        " AND CAST(IS_.stock_count AS NVARCHAR(50)) LIKE N'%'+ISNULL(@stock_count, N'')+N'%' \n" +
        " AND isnull(IS_.is_deleted, 0) = 0 \n" +
        " ORDER BY IS_.inventory_stock_id \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("inventory_stock")
            .setColumnNames(
                "inventory_code",
                "product_code",
                "stock_count"
            )
            .setWhereQuery("inventory_stock_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("inventory_stock")
            .setColumnNames(
                "inventory_code",
                "product_code",
                "stock_count"
            )
            .getQuery(),

        "UPDATE inventory_stock SET is_deleted = 1 WHERE inventory_stock_id = ?",

        105
    ),


    // ---------------------------------------------------------
    // PRODUCT TRANSACTION TYPE  (object 106)
    // ---------------------------------------------------------

    ProductTransactionType
    (
        new GenerateGenericSQLQuery.ReadQuery()
            .setTableName("product_transaction_type")
            .setColumnNames(
                "product_transaction_type_id",
                "title",
                "description"
            )
            .setColumnDataTypes(
                "numeric",
                "nvarchar(50)",
                "nvarchar(500)"
            )
            .setWhereQuery("AND isnull(is_deleted, 0) = 0")
            .getQuery(),

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("product_transaction_type")
            .setColumnNames(
                "title",
                "description"
            )
            .setWhereQuery("product_transaction_type_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("product_transaction_type")
            .setColumnNames(
                "title",
                "description"
            )
            .getQuery(),

        "UPDATE product_transaction_type SET is_deleted = 1 WHERE product_transaction_type_id = ?",

        106
    ),


    // ---------------------------------------------------------
    // INVENTORY STOCK HISTORY  (object 107)
    // Read-only audit trail; no create/update/delete from UI.
    // ---------------------------------------------------------

            InventoryStockHistory
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @product_title NVARCHAR(50)   = NULLIF(?, N'') \n" +
        " DECLARE @inventory_title NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @txn_title NVARCHAR(50)       = NULLIF(?, N'') \n" +
        " DECLARE @amount NVARCHAR(50)          = NULLIF(?, N'') \n" +
        " DECLARE @request_id NVARCHAR(50)      = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   ISH.inventory_stock_history_id \n" +
        "  ,ISNULL(P.product_title, N'(unknown)') AS product_title \n" +
        "  ,ISNULL(IT.inventory_type_title, N'') + N' — ' + ISNULL(W.warehouse_title, N'') AS inventory_display \n" +
        "  ,ISNULL(PTT.title, N'(unknown)') AS transaction_type \n" +
        "  ,ISH.amount_changed \n" +
        "  ,ISH.request_id \n" +
        " FROM dbo.inventory_stock_history ISH \n" +
        " LEFT JOIN dbo.product                  P   ON P.product_code              = ISH.product_changed_code \n" +
        " LEFT JOIN dbo.inventory                I   ON I.inventory_id              = ISH.inventory_id \n" +
        " LEFT JOIN dbo.inventory_type           IT  ON IT.inventory_id             = I.inventory_type_id \n" +
        " LEFT JOIN dbo.warehouse                W   ON W.warehouse_id              = I.warehouse_id \n" +
        " LEFT JOIN dbo.product_transaction_type PTT ON PTT.product_transaction_type_id = ISH.product_transaction_type_id \n" +
        " WHERE 1 = 1 \n" +
        " AND ISNULL(P.product_title, N'') LIKE N'%'+ISNULL(@product_title, N'')+N'%' \n" +
        " AND ISNULL(IT.inventory_type_title, N'') + N' — ' + ISNULL(W.warehouse_title, N'') LIKE N'%'+ISNULL(@inventory_title, N'')+N'%' \n" +
        " AND ISNULL(PTT.title, N'') LIKE N'%'+ISNULL(@txn_title, N'')+N'%' \n" +
        " AND ISNULL(CAST(ISH.amount_changed AS NVARCHAR(50)), N'') LIKE N'%'+ISNULL(@amount, N'')+N'%' \n" +
        " AND ISNULL(CAST(ISH.request_id     AS NVARCHAR(50)), N'') LIKE N'%'+ISNULL(@request_id, N'')+N'%' \n" +
        " ORDER BY ISH.inventory_stock_history_id DESC \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",

        "",
        "",
        "DELETE FROM inventory_stock_history WHERE inventory_stock_history_id = ?",

        107
    ),


    // ---------------------------------------------------------
    // INVENTORY PRODUCT REQUEST TYPE  (object 108)
    // ---------------------------------------------------------

    InventoryProductRequestType
    (
        new GenerateGenericSQLQuery.ReadQuery()
            .setTableName("inventory_product_request_type")
            .setColumnNames(
                "inventory_product_request_type_id",
                "request_type_code",
                "request_type_title"
            )
            .setColumnDataTypes(
                "numeric",
                "numeric",
                "nvarchar(50)"
            )
            .setWhereQuery("AND isnull(is_deleted, 0) = 0")
            .getQuery(),

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("inventory_product_request_type")
            .setColumnNames(
                "request_type_code",
                "request_type_title"
            )
            .setWhereQuery("inventory_product_request_type_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("inventory_product_request_type")
            .setColumnNames(
                "request_type_code",
                "request_type_title"
            )
            .getQuery(),

        "UPDATE inventory_product_request_type SET is_deleted = 1 WHERE inventory_product_request_type_id = ?",

        108
    ),


    // ---------------------------------------------------------
    // INVENTORY PRODUCT REQUEST STATUS  (object 109)
    // ---------------------------------------------------------

    InventoryProductRequestStatus
    (
        new GenerateGenericSQLQuery.ReadQuery()
            .setTableName("inventory_product_request_status")
            .setColumnNames(
                "inventory_product_request_status",
                "request_status_code",
                "request_status_title"
            )
            .setColumnDataTypes(
                "numeric",
                "numeric",
                "nvarchar(50)"
            )
            .setWhereQuery("AND isnull(is_deleted, 0) = 0")
            .getQuery(),

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("inventory_product_request_status")
            .setColumnNames(
                "request_status_code",
                "request_status_title"
            )
            .setWhereQuery("inventory_product_request_status = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("inventory_product_request_status")
            .setColumnNames(
                "request_status_code",
                "request_status_title"
            )
            .getQuery(),

        "UPDATE inventory_product_request_status SET is_deleted = 1 WHERE inventory_product_request_status = ?",

        109
    ),


    // ---------------------------------------------------------
    // INVENTORY PRODUCT REQUEST HEADER  (object 110)
    // ---------------------------------------------------------

    InventoryProductRequestHeader
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @type_title NVARCHAR(50)     = NULLIF(?, N'') \n" +
        " DECLARE @status_title NVARCHAR(50)   = NULLIF(?, N'') \n" +
        " DECLARE @req_inv NVARCHAR(50)        = NULLIF(?, N'') \n" +
        " DECLARE @sup_inv NVARCHAR(50)        = NULLIF(?, N'') \n" +
        " DECLARE @req_date NVARCHAR(50)       = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   IPRH.inventory_product_request_header_id \n" +
        "  ,IPRT.request_type_title \n" +
        "  ,IPRS.request_status_title \n" +
        "  ,RIW.warehouse_title  AS requesting_from \n" +
        "  ,SIW.warehouse_title  AS supplying_to \n" +
        "  ,IPRH.request_date \n" +
        "  ,IPRH.requester_user_code \n" +
        " FROM dbo.inventory_product_request_header IPRH \n" +
        " INNER JOIN dbo.inventory_product_request_type   IPRT ON IPRT.request_type_code   = IPRH.request_type_code \n" +
        " INNER JOIN dbo.inventory_product_request_status IPRS ON IPRS.request_status_code = IPRH.request_status_code \n" +
        " LEFT  JOIN dbo.inventory RI  ON RI.inventory_id  = IPRH.requesting_inventory_id \n" +
        " LEFT  JOIN dbo.warehouse RIW ON RIW.warehouse_id = RI.warehouse_id \n" +
        " LEFT  JOIN dbo.inventory SI  ON SI.inventory_id  = IPRH.supplying_inventory_id \n" +
        " LEFT  JOIN dbo.warehouse SIW ON SIW.warehouse_id = SI.warehouse_id \n" +
        " WHERE 1 = 1 \n" +
        " AND CAST(IPRT.request_type_title   AS NVARCHAR(50)) LIKE N'%'+ISNULL(@type_title,   N'')+N'%' \n" +
        " AND CAST(IPRS.request_status_title AS NVARCHAR(50)) LIKE N'%'+ISNULL(@status_title, N'')+N'%' \n" +
        " AND CAST(RIW.warehouse_title       AS NVARCHAR(50)) LIKE N'%'+ISNULL(@req_inv,      N'')+N'%' \n" +
        " AND CAST(SIW.warehouse_title       AS NVARCHAR(50)) LIKE N'%'+ISNULL(@sup_inv,      N'')+N'%' \n" +
        " AND CAST(IPRH.request_date         AS NVARCHAR(50)) LIKE N'%'+ISNULL(@req_date,     N'')+N'%' \n" +
        " AND isnull(IPRH.is_deleted, 0) = 0 \n" +
        " ORDER BY IPRH.inventory_product_request_header_id DESC \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("inventory_product_request_header")
            .setColumnNames(
                "request_status_code",
                "request_type_code",
                "requesting_inventory_id",
                "supplying_inventory_id",
                "request_date",
                "decided_by_user_code",
                "decision_note"
            )
            .setWhereQuery("inventory_product_request_header_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("inventory_product_request_header")
            .setColumnNames(
                "request_status_code",
                "request_type_code",
                "requesting_inventory_id",
                "supplying_inventory_id",
                "request_date",
                "requester_user_code"
            )
            .getQuery(),

        "UPDATE inventory_product_request_header SET is_deleted = 1 WHERE inventory_product_request_header_id = ?",

        110
    ),


    // ---------------------------------------------------------
    // INVENTORY PRODUCT REQUEST DETAILS  (object 111)
    // ---------------------------------------------------------

    InventoryProductRequestDetails
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @product_title NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @count NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @header NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   IPRD.inventory_product_request_details_id \n" +
        "  ,IPRD.inventory_product_request_header_id \n" +
        "  ,P.product_title \n" +
        "  ,IPRD.product_count \n" +
        " FROM dbo.inventory_product_request_details IPRD \n" +
        " LEFT JOIN dbo.product P ON P.product_code = IPRD.product_code \n" +
        " WHERE 1 = 1 \n" +
        " AND CAST(P.product_title                       AS NVARCHAR(50)) LIKE N'%'+ISNULL(@product_title, N'')+N'%' \n" +
        " AND CAST(IPRD.product_count                    AS NVARCHAR(50)) LIKE N'%'+ISNULL(@count,         N'')+N'%' \n" +
        " AND CAST(IPRD.inventory_product_request_header_id AS NVARCHAR(50)) LIKE N'%'+ISNULL(@header, N'')+N'%' \n" +
        " AND isnull(IPRD.is_deleted, 0) = 0 \n" +
        " ORDER BY IPRD.inventory_product_request_details_id \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("inventory_product_request_details")
            .setColumnNames(
                "inventory_product_request_header_id",
                "product_code",
                "product_count"
            )
            .setWhereQuery("inventory_product_request_details_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("inventory_product_request_details")
            .setColumnNames(
                "inventory_product_request_header_id",
                "product_code",
                "product_count"
            )
            .getQuery(),

        "UPDATE inventory_product_request_details SET is_deleted = 1 WHERE inventory_product_request_details_id = ?",

        111
    ),


    // =========================================================
    // =========================================================
    // SECTION 3 — FLEET MANAGEMENT
    // =========================================================
    // =========================================================

    // ---------------------------------------------------------
    // FLEET TEAM  (object 200)
    // ---------------------------------------------------------

    FleetTeam
    (
        new GenerateGenericSQLQuery.ReadQuery()
            .setTableName("fleet_team")
            .setColumnNames(
                "fleet_team_id",
                "fleet_team_code",
                "fleet_team_name"
            )
            .setColumnDataTypes(
                "numeric",
                "numeric",
                "nvarchar(50)"
            )
            .setWhereQuery("AND isnull(is_deleted, 0) = 0")
            .getQuery(),

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("fleet_team")
            .setColumnNames(
                "fleet_team_code",
                "fleet_team_name"
            )
            .setWhereQuery("fleet_team_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("fleet_team")
            .setColumnNames(
                "fleet_team_code",
                "fleet_team_name"
            )
            .getQuery(),

        "UPDATE fleet_team SET is_deleted = 1 WHERE fleet_team_id = ?",

        200
    ),


    // ---------------------------------------------------------
    // DRIVERS  (object 201)
    // ---------------------------------------------------------

    Drivers
    (
        new GenerateGenericSQLQuery.ReadQuery()
            .setTableName("drivers")
            .setColumnNames(
                "drivers_id",
                "drivers_code",
                "drivers_first_name",
                "drivers_last_name",
                "drivers_national_code",
                "drivers_phone_number",
                "drivers_age"
            )
            .setColumnDataTypes(
                "numeric",
                "numeric",
                "nvarchar(50)",
                "nvarchar(50)",
                "nvarchar(50)",
                "nvarchar(50)",
                "numeric"
            )
            .setWhereQuery("AND isnull(is_deleted, 0) = 0")
            .getQuery(),

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("drivers")
            .setColumnNames(
                "drivers_code",
                "drivers_first_name",
                "drivers_last_name",
                "drivers_national_code",
                "drivers_phone_number",
                "drivers_age"
            )
            .setWhereQuery("drivers_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("drivers")
            .setColumnNames(
                "drivers_code",
                "drivers_first_name",
                "drivers_last_name",
                "drivers_national_code",
                "drivers_phone_number",
                "drivers_age"
            )
            .getQuery(),

        "UPDATE drivers SET is_deleted = 1 WHERE drivers_id = ?",

        201
    ),


    // ---------------------------------------------------------
    // VEHICLES  (object 202)
    // ---------------------------------------------------------

    Vehicles
    (
        new GenerateGenericSQLQuery.ReadQuery()
            .setTableName("vehicles")
            .setColumnNames(
                "vehicle_id",
                "vehicle_code",
                "vehicle_name",
                "vehicle_licence_plate"
            )
            .setColumnDataTypes(
                "numeric",
                "numeric",
                "nvarchar(50)",
                "nvarchar(50)"
            )
            .setWhereQuery("AND isnull(is_deleted, 0) = 0")
            .getQuery(),

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("vehicles")
            .setColumnNames(
                "vehicle_code",
                "vehicle_name",
                "vehicle_licence_plate"
            )
            .setWhereQuery("vehicle_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("vehicles")
            .setColumnNames(
                "vehicle_code",
                "vehicle_name",
                "vehicle_licence_plate"
            )
            .getQuery(),

        "UPDATE vehicles SET is_deleted = 1 WHERE vehicle_id = ?",

        202
    ),


    // ---------------------------------------------------------
    // FLEET TEAM DRIVERS  (object 203)
    // ---------------------------------------------------------

        FleetTeamDrivers
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @team NVARCHAR(50)   = NULLIF(?, N'') \n" +
        " DECLARE @driver NVARCHAR(100) = NULLIF(?, N'') \n" +
        " DECLARE @vehicle NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   FTD.fleet_team_drivers_id \n" +
        "  ,ISNULL(FT.fleet_team_name, N'(unknown)') AS team_name \n" +
        "  ,ISNULL(D.drivers_first_name + N' ' + ISNULL(D.drivers_last_name, N''), N'(unknown)') AS driver_name \n" +
        "  ,ISNULL(V.vehicle_name, N'(unknown)') AS vehicle_name \n" +
        " FROM dbo.fleet_team_drivers FTD \n" +
        " LEFT JOIN dbo.fleet_team FT ON FT.fleet_team_code = FTD.fleet_team_id \n" +
        " LEFT JOIN dbo.drivers    D  ON D.drivers_code     = FTD.driver_code \n" +
        " LEFT JOIN dbo.vehicles   V  ON V.vehicle_code     = FTD.vehicle_code \n" +
        " WHERE 1 = 1 \n" +
        " AND ISNULL(FT.fleet_team_name, N'') LIKE N'%'+ISNULL(@team, N'')+N'%' \n" +
        " AND ISNULL(D.drivers_first_name + N' ' + ISNULL(D.drivers_last_name, N''), N'') LIKE N'%'+ISNULL(@driver, N'')+N'%' \n" +
        " AND ISNULL(V.vehicle_name, N'') LIKE N'%'+ISNULL(@vehicle, N'')+N'%' \n" +
        " AND isnull(FTD.is_deleted, 0) = 0 \n" +
        " ORDER BY FTD.fleet_team_drivers_id \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("fleet_team_drivers")
            .setColumnNames(
                "fleet_team_id",
                "driver_code",
                "vehicle_code"
            )
            .setWhereQuery("fleet_team_drivers_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("fleet_team_drivers")
            .setColumnNames(
                "fleet_team_id",
                "driver_code",
                "vehicle_code"
            )
            .getQuery(),

        "UPDATE fleet_team_drivers SET is_deleted = 1 WHERE fleet_team_drivers_id = ?",

        203
    ),


    // ---------------------------------------------------------
    // FLEET TEAM MANAGER  (object 204)
    // ---------------------------------------------------------

        FleetTeamManager
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @code NVARCHAR(50)   = NULLIF(?, N'') \n" +
        " DECLARE @username NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @team NVARCHAR(50)     = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   FTM.fleet_team_manager_id \n" +
        "  ,FTM.fleet_team_manager_code \n" +
        "  ,ISNULL(U.SYS_USERNAME, N'(unknown)') AS username \n" +
        "  ,ISNULL(FT.fleet_team_name, N'(unknown)') AS team_name \n" +
        " FROM dbo.fleet_team_manager FTM \n" +
        " LEFT JOIN IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_USERS U ON U.SYS_USER_CODE = FTM.user_id \n" +
        " LEFT JOIN dbo.fleet_team FT ON FT.fleet_team_code = FTM.fleet_team_code \n" +
        " WHERE 1 = 1 \n" +
        " AND CAST(FTM.fleet_team_manager_code AS NVARCHAR(50)) LIKE N'%'+ISNULL(@code, N'')+N'%' \n" +
        " AND ISNULL(U.SYS_USERNAME, N'') LIKE N'%'+ISNULL(@username, N'')+N'%' \n" +
        " AND ISNULL(FT.fleet_team_name, N'') LIKE N'%'+ISNULL(@team, N'')+N'%' \n" +
        " AND isnull(FTM.is_deleted, 0) = 0 \n" +
        " ORDER BY FTM.fleet_team_manager_id \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("fleet_team_manager")
            .setColumnNames(
                "fleet_team_manager_code",
                "user_id",
                "fleet_team_code"
            )
            .setWhereQuery("fleet_team_manager_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("fleet_team_manager")
            .setColumnNames(
                "fleet_team_manager_code",
                "user_id",
                "fleet_team_code"
            )
            .getQuery(),

        "UPDATE fleet_team_manager SET is_deleted = 1 WHERE fleet_team_manager_id = ?",

        204
    ),


    // ---------------------------------------------------------
    // FLEET TRIP  (object 205)
    // ---------------------------------------------------------

                FleetTrip
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @team NVARCHAR(50)        = NULLIF(?, N'') \n" +
        " DECLARE @origin NVARCHAR(50)      = NULLIF(?, N'') \n" +
        " DECLARE @destination NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @distance NVARCHAR(50)    = NULLIF(?, N'') \n" +
        " DECLARE @duration NVARCHAR(50)    = NULLIF(?, N'') \n" +
        " DECLARE @status NVARCHAR(50)      = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   FT.fleet_trip_id \n" +
        "  ,ISNULL(FTM.fleet_team_name, N'(unknown)')         AS team_name \n" +
        "  ,ISNULL(OW.warehouse_title,  N'(unknown)')         AS origin_title \n" +
        "  ,ISNULL(DW.warehouse_title,  N'(unknown)')         AS destination_title \n" +
        "  ,FT.distance_meters \n" +
        "  ,FT.duration_seconds \n" +
        "  ,ISNULL(FTS.fleet_trip_status_title, N'(unknown)') AS status_title \n" +
        " FROM dbo.fleet_trip FT \n" +
        " LEFT JOIN dbo.fleet_team        FTM ON FTM.fleet_team_id          = FT.fleet_team_id \n" +
        " LEFT JOIN dbo.warehouse         OW  ON OW.warehouse_id            = FT.origin_warehouse_id \n" +
        " LEFT JOIN dbo.warehouse         DW  ON DW.warehouse_id            = FT.destination_warehouse_id \n" +
        " LEFT JOIN dbo.fleet_trip_status FTS ON FTS.fleet_trip_status_code = FT.fleet_trip_status_code \n" +
        " WHERE 1 = 1 \n" +
        " AND ISNULL(FTM.fleet_team_name, N'')         LIKE N'%'+ISNULL(@team,        N'')+N'%' \n" +
        " AND ISNULL(OW.warehouse_title,  N'')         LIKE N'%'+ISNULL(@origin,      N'')+N'%' \n" +
        " AND ISNULL(DW.warehouse_title,  N'')         LIKE N'%'+ISNULL(@destination, N'')+N'%' \n" +
        " AND ISNULL(CAST(FT.distance_meters  AS NVARCHAR(50)), N'') LIKE N'%'+ISNULL(@distance, N'')+N'%' \n" +
        " AND ISNULL(CAST(FT.duration_seconds AS NVARCHAR(50)), N'') LIKE N'%'+ISNULL(@duration, N'')+N'%' \n" +
        " AND ISNULL(FTS.fleet_trip_status_title, N'') LIKE N'%'+ISNULL(@status,      N'')+N'%' \n" +
        " AND isnull(FT.is_deleted, 0) = 0 \n" +
        " ORDER BY FT.fleet_trip_id DESC \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("fleet_trip")
            .setColumnNames(
                "fleet_team_id",
                "inventory_product_request_header_id",
                "origin_warehouse_id",
                "destination_warehouse_id",
                "distance_meters",
                "duration_seconds",
                "route_geometry",
                "fleet_trip_status_code"
            )
            .setWhereQuery("fleet_trip_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("fleet_trip")
            .setColumnNames(
                "fleet_team_id",
                "inventory_product_request_header_id",
                "origin_warehouse_id",
                "destination_warehouse_id",
                "distance_meters",
                "duration_seconds",
                "route_geometry",
                "fleet_trip_status_code"
            )
            .getQuery(),

        "UPDATE fleet_trip SET is_deleted = 1 WHERE fleet_trip_id = ?",

        205
    ),


    // ---------------------------------------------------------
    // FLEET TEAM TRANSPORTS  (object 207)
    // ---------------------------------------------------------

            FleetTeamTransports
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @request NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @driver NVARCHAR(100) = NULLIF(?, N'') \n" +
        " DECLARE @vehicle NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   FTT.fleet_team_transports_id \n" +
        "  ,CAST(FTT.inventory_product_request_header_id AS NVARCHAR(50)) AS request_ref \n" +
        "  ,ISNULL(D.drivers_first_name + N' ' + ISNULL(D.drivers_last_name, N''), N'(unknown)') AS driver_name \n" +
        "  ,ISNULL(V.vehicle_name, N'(unknown)') AS vehicle_name \n" +
        " FROM dbo.fleet_team_transports FTT \n" +
        " LEFT JOIN dbo.drivers  D ON D.drivers_code = FTT.assigned_driver_code \n" +
        " LEFT JOIN dbo.vehicles V ON V.vehicle_code = FTT.assigned_vehicle_code \n" +
        " WHERE 1 = 1 \n" +
        " AND CAST(FTT.inventory_product_request_header_id AS NVARCHAR(50)) LIKE N'%'+ISNULL(@request, N'')+N'%' \n" +
        " AND ISNULL(D.drivers_first_name + N' ' + ISNULL(D.drivers_last_name, N''), N'') LIKE N'%'+ISNULL(@driver, N'')+N'%' \n" +
        " AND ISNULL(V.vehicle_name, N'') LIKE N'%'+ISNULL(@vehicle, N'')+N'%' \n" +
        " AND isnull(FTT.is_deleted, 0) = 0 \n" +
        " ORDER BY FTT.fleet_team_transports_id \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("fleet_team_transports")
            .setColumnNames(
                "inventory_product_request_header_id",
                "assigned_driver_code",
                "assigned_vehicle_code"
            )
            .setWhereQuery("fleet_team_transports_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("fleet_team_transports")
            .setColumnNames(
                "inventory_product_request_header_id",
                "assigned_driver_code",
                "assigned_vehicle_code"
            )
            .getQuery(),

        "UPDATE fleet_team_transports SET is_deleted = 1 WHERE fleet_team_transports_id = ?",

        207
    ),


    // ---------------------------------------------------------
    // FLEET APPROVAL BY INVENTORY — HEADER  (object 208)
    // No is_deleted column → hard delete
    // ---------------------------------------------------------

    FleetApprovalByInventoryHeader
    (
        new GenerateGenericSQLQuery.ReadQuery()
            .setTableName("fleet_aproval_by_inventory_header")
            .setColumnNames(
                "fleet_aproval_by_inventory_id",
                "inventory_product_request_header_id",
                "description",
                "did_arive"
            )
            .setColumnDataTypes(
                "numeric",
                "numeric",
                "nvarchar(500)",
                "bit"
            )
            .setWhereQuery("1 = 1")
            .getQuery(),

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("fleet_aproval_by_inventory_header")
            .setColumnNames(
                "inventory_product_request_header_id",
                "description",
                "did_arive"
            )
            .setWhereQuery("fleet_aproval_by_inventory_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("fleet_aproval_by_inventory_header")
            .setColumnNames(
                "inventory_product_request_header_id",
                "description",
                "did_arive"
            )
            .getQuery(),

        "DELETE FROM fleet_aproval_by_inventory_header WHERE fleet_aproval_by_inventory_id = ?",

        208
    ),


    // ---------------------------------------------------------
    // FLEET APPROVAL BY INVENTORY — DETAILS  (object 209)
    // No is_deleted column → hard delete
    // ---------------------------------------------------------

    FleetApprovalByInventoryDetails
    (
        new GenerateGenericSQLQuery.ReadQuery()
            .setTableName("fleet_aproval_by_inventory_details")
            .setColumnNames(
                "fleet_aproval_by_inventory_details_id",
                "fleet_aproval_by_inventory_id",
                "product_code",
                "how_many_arived"
            )
            .setColumnDataTypes(
                "numeric",
                "numeric",
                "numeric",
                "numeric"
            )
            .setWhereQuery("1 = 1")
            .getQuery(),

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("fleet_aproval_by_inventory_details")
            .setColumnNames(
                "fleet_aproval_by_inventory_id",
                "product_code",
                "how_many_arived"
            )
            .setWhereQuery("fleet_aproval_by_inventory_details_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("fleet_aproval_by_inventory_details")
            .setColumnNames(
                "fleet_aproval_by_inventory_id",
                "product_code",
                "how_many_arived"
            )
            .getQuery(),

        "DELETE FROM fleet_aproval_by_inventory_details WHERE fleet_aproval_by_inventory_details_id = ?",

        209
    ),


    // =========================================================
    // =========================================================
    // SECTION 4 — ACCESS REQUESTS / TICKETS
    // =========================================================
    // =========================================================

    // ---------------------------------------------------------
    // ACCESS REQUEST HEADER  (object 300)
    // ---------------------------------------------------------

    AccessRequestHeader
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @type_title NVARCHAR(50)     = NULLIF(?, N'') \n" +
        " DECLARE @status_title NVARCHAR(50)   = NULLIF(?, N'') \n" +
        " DECLARE @requester NVARCHAR(50)      = NULLIF(?, N'') \n" +
        " DECLARE @reason NVARCHAR(500)        = NULLIF(?, N'') \n" +
        " DECLARE @requested NVARCHAR(50)      = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   ARH.access_request_header_id \n" +
        "  ,ART.access_request_type_title \n" +
        "  ,ARS.access_request_status_title \n" +
        "  ,ARH.requester_user_code \n" +
        "  ,ARH.target_inventory_id \n" +
        "  ,ARH.reason \n" +
        "  ,ARH.requested_at \n" +
        " FROM dbo.access_request_header ARH \n" +
        " INNER JOIN dbo.access_request_type   ART ON ART.access_request_type_code   = ARH.access_request_type_code \n" +
        " INNER JOIN dbo.access_request_status ARS ON ARS.access_request_status_code = ARH.access_request_status_code \n" +
        " WHERE 1 = 1 \n" +
        " AND CAST(ART.access_request_type_title   AS NVARCHAR(50))  LIKE N'%'+ISNULL(@type_title, N'')+N'%' \n" +
        " AND CAST(ARS.access_request_status_title AS NVARCHAR(50))  LIKE N'%'+ISNULL(@status_title,N'')+N'%' \n" +
        " AND CAST(ARH.requester_user_code         AS NVARCHAR(50))  LIKE N'%'+ISNULL(@requester,  N'')+N'%' \n" +
        " AND CAST(ARH.reason                      AS NVARCHAR(500)) LIKE N'%'+ISNULL(@reason,     N'')+N'%' \n" +
        " AND CAST(ARH.requested_at                AS NVARCHAR(50))  LIKE N'%'+ISNULL(@requested,  N'')+N'%' \n" +
        " AND isnull(ARH.is_deleted, 0) = 0 \n" +
        " ORDER BY ARH.access_request_header_id DESC \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("access_request_header")
            .setColumnNames(
                "access_request_status_code",
                "decided_by_user_code",
                "decision_note",
                "expires_at",
                "applied_at",
                "applied_by_user_code"
            )
            .setWhereQuery("access_request_header_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("access_request_header")
            .setColumnNames(
                "access_request_type_code",
                "access_request_status_code",
                "requester_user_code",
                "target_inventory_id",
                "reason",
                "expires_at"
            )
            .getQuery(),

        "UPDATE access_request_header SET is_deleted = 1 WHERE access_request_header_id = ?",

        300
    ),


        ApproveAccessRequest
    (
        "",
        "UPDATE access_request_header " +
        "SET access_request_status_code = 2, " +
        "    decided_by_user_code = ?, " +
        "    decided_at = SYSDATETIME() " +
        "WHERE access_request_header_id = ?",
        "",
        "",
        300
    ),

    DenyAccessRequest
    (
        "",
        "UPDATE access_request_header " +
        "SET access_request_status_code = 3, " +
        "    decided_by_user_code = ?, " +
        "    decided_at = SYSDATETIME() " +
        "WHERE access_request_header_id = ?",
        "",
        "",
        300
    ),
    
        AccessRequestUserStatusLookup
    (
        " SELECT \n" +
        "   access_request_status_code \n" +
        "  ,access_request_status_title \n" +
        " FROM dbo.access_request_status \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        "   AND access_request_status_code IN (1, 5) \n" +  
        " ORDER BY access_request_status_code ",
        "", "", ""
    ),
    
    // ---------------------------------------------------------
    // ACCESS REQUEST OBJECT (temp-access payload)  (object 301)
    // ---------------------------------------------------------

        AccessRequestObject
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @request NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @object NVARCHAR(50)  = NULLIF(?, N'') \n" +
        " DECLARE @read NVARCHAR(50)    = NULLIF(?, N'') \n" +
        " DECLARE @create NVARCHAR(50)  = NULLIF(?, N'') \n" +
        " DECLARE @update NVARCHAR(50)  = NULLIF(?, N'') \n" +
        " DECLARE @delete NVARCHAR(50)  = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   ARO.access_request_object_id \n" +
        "  ,CAST(ARO.access_request_header_id AS NVARCHAR(50)) AS request_ref \n" +
        "  ,ISNULL(SO.OBJECT_TITLE, N'(unknown)') AS object_title \n" +
        "  ,ARO.can_read \n" +
        "  ,ARO.can_create \n" +
        "  ,ARO.can_update \n" +
        "  ,ARO.can_delete \n" +
        " FROM dbo.access_request_object ARO \n" +
        " LEFT JOIN IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS SO ON SO.OBJECT_CODE = ARO.object_code \n" +
        " WHERE isnull(ARO.is_deleted, 0) = 0 \n" +
        " AND CAST(ARO.access_request_header_id AS NVARCHAR(50)) LIKE N'%'+ISNULL(@request, N'')+N'%' \n" +
        " AND ISNULL(SO.OBJECT_TITLE, N'') LIKE N'%'+ISNULL(@object, N'')+N'%' \n" +
        " AND CAST(ARO.can_read   AS NVARCHAR(50)) LIKE N'%'+ISNULL(@read,   N'')+N'%' \n" +
        " AND CAST(ARO.can_create AS NVARCHAR(50)) LIKE N'%'+ISNULL(@create, N'')+N'%' \n" +
        " AND CAST(ARO.can_update AS NVARCHAR(50)) LIKE N'%'+ISNULL(@update, N'')+N'%' \n" +
        " AND CAST(ARO.can_delete AS NVARCHAR(50)) LIKE N'%'+ISNULL(@delete, N'')+N'%' \n" +
        " ORDER BY ARO.access_request_object_id DESC \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("access_request_object")
            .setColumnNames(
                "object_code",
                "can_read",
                "can_create",
                "can_update",
                "can_delete"
            )
            .setWhereQuery("access_request_object_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("access_request_object")
            .setColumnNames(
                "access_request_header_id",
                "object_code",
                "can_read",
                "can_create",
                "can_update",
                "can_delete"
            )
            .getQuery(),

        "UPDATE access_request_object SET is_deleted = 1 WHERE access_request_object_id = ?",

        301
    ),


    // ---------------------------------------------------------
    // ACCESS REQUEST STOCK (stock-adjustment payload)  (object 302)
    // ---------------------------------------------------------

        AccessRequestStock
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @request NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @product NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @qty NVARCHAR(50)     = NULLIF(?, N'') \n" +
        " DECLARE @note NVARCHAR(500)   = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   ARS.access_request_stock_id \n" +
        "  ,CAST(ARS.access_request_header_id AS NVARCHAR(50)) AS request_ref \n" +
        "  ,ISNULL(P.product_title, N'(unknown)') AS product_title \n" +
        "  ,ARS.quantity_change \n" +
        "  ,ISNULL(ARS.note, N'') AS note \n" +
        " FROM dbo.access_request_stock ARS \n" +
        " LEFT JOIN dbo.product P ON P.product_code = ARS.product_code \n" +
        " WHERE isnull(ARS.is_deleted, 0) = 0 \n" +
        " AND CAST(ARS.access_request_header_id AS NVARCHAR(50)) LIKE N'%'+ISNULL(@request, N'')+N'%' \n" +
        " AND ISNULL(P.product_title, N'') LIKE N'%'+ISNULL(@product, N'')+N'%' \n" +
        " AND CAST(ARS.quantity_change AS NVARCHAR(50)) LIKE N'%'+ISNULL(@qty, N'')+N'%' \n" +
        " AND ISNULL(ARS.note, N'') LIKE N'%'+ISNULL(@note, N'')+N'%' \n" +
        " ORDER BY ARS.access_request_stock_id DESC \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",

        new GenerateGenericSQLQuery.UpdateQuery()
            .setTableName("access_request_stock")
            .setColumnNames(
                "product_code",
                "quantity_change",
                "note"
            )
            .setWhereQuery("access_request_stock_id = ?")
            .getQuery(),

        new GenerateGenericSQLQuery.CreateQuery()
            .setTableName("access_request_stock")
            .setColumnNames(
                "access_request_header_id",
                "product_code",
                "quantity_change",
                "note"
            )
            .getQuery(),

        "UPDATE access_request_stock SET is_deleted = 1 WHERE access_request_stock_id = ?",

        302
    ),


    // =========================================================
    // =========================================================
    // SECTION 5 — LOOKUPS (used by data-combo / find-object-box)
    // =========================================================
    // All lookups use the 4-arg constructor (objectCode = -1)
    // so they never trip the permission gate.
    // =========================================================

        // ---------------------------------------------------------
    // FOB-SHAPED PICKERS
    // ---------------------------------------------------------
    // These use hand-rolled SQL with pagination + search
    // placeholders so they work inside <find-object-box>.
    //
    // The first returned column is the value FOB stores;
    // the rest are the display columns.
    // ---------------------------------------------------------

    // ---------------------------------------------------------
    // FOB PICKERS — Access Request payloads
    // ---------------------------------------------------------

    AccessRequestHeaderTempPicker
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @Requester NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @Reason NVARCHAR(500)   = NULLIF(?, N'') \n" +
        " DECLARE @Status NVARCHAR(50)    = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   ARH.access_request_header_id \n" +
        "  ,ARH.requester_user_code \n" +
        "  ,ARH.reason \n" +
        "  ,ARS.access_request_status_title \n" +
        "  ,ARH.requested_at \n" +
        " FROM dbo.access_request_header ARH \n" +
        " INNER JOIN dbo.access_request_status ARS ON ARS.access_request_status_code = ARH.access_request_status_code \n" +
        " WHERE isnull(ARH.is_deleted, 0) = 0 \n" +
        "   AND ARH.access_request_type_code = 1 \n" +
        " AND CAST(ARH.requester_user_code AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Requester, N'')+N'%' \n" +
        " AND ISNULL(ARH.reason, N'')                       LIKE N'%'+ISNULL(@Reason,    N'')+N'%' \n" +
        " AND ISNULL(ARS.access_request_status_title, N'')  LIKE N'%'+ISNULL(@Status,    N'')+N'%' \n" +
        " ORDER BY ARH.access_request_header_id DESC \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", ""
    ),

    AccessRequestHeaderStockPicker
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @Requester NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @Reason NVARCHAR(500)   = NULLIF(?, N'') \n" +
        " DECLARE @Status NVARCHAR(50)    = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   ARH.access_request_header_id \n" +
        "  ,ARH.requester_user_code \n" +
        "  ,ARH.reason \n" +
        "  ,ARS.access_request_status_title \n" +
        "  ,ARH.requested_at \n" +
        " FROM dbo.access_request_header ARH \n" +
        " INNER JOIN dbo.access_request_status ARS ON ARS.access_request_status_code = ARH.access_request_status_code \n" +
        " WHERE isnull(ARH.is_deleted, 0) = 0 \n" +
        "   AND ARH.access_request_type_code = 2 \n" +
        " AND CAST(ARH.requester_user_code AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Requester, N'')+N'%' \n" +
        " AND ISNULL(ARH.reason, N'')                       LIKE N'%'+ISNULL(@Reason,    N'')+N'%' \n" +
        " AND ISNULL(ARS.access_request_status_title, N'')  LIKE N'%'+ISNULL(@Status,    N'')+N'%' \n" +
        " ORDER BY ARH.access_request_header_id DESC \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", ""
    ),

    SysObjectsPicker
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @Title NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   OBJECT_CODE \n" +
        "  ,OBJECT_TITLE \n" +
        " FROM IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS \n" +
        " WHERE IS_DELETED = 0 \n" +
        " AND CAST(OBJECT_TITLE AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Title, N'')+N'%' \n" +
        " ORDER BY OBJECT_CODE \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", ""
    ),
        
        ProductRequestRouteInfo
    (
        " SELECT \n" +
        "   IPRH.inventory_product_request_header_id \n" +
        "  ,SI.warehouse_id     AS origin_warehouse_id \n" +
        "  ,OW.warehouse_title  AS origin_warehouse_title \n" +
        "  ,OW.warehouse_lat    AS origin_lat \n" +
        "  ,OW.warehouse_long   AS origin_lng \n" +
        "  ,RI.warehouse_id     AS destination_warehouse_id \n" +
        "  ,DW.warehouse_title  AS destination_warehouse_title \n" +
        "  ,DW.warehouse_lat    AS destination_lat \n" +
        "  ,DW.warehouse_long   AS destination_lng \n" +
        " FROM dbo.inventory_product_request_header IPRH \n" +
        " LEFT JOIN dbo.inventory SI ON SI.inventory_id = IPRH.supplying_inventory_id \n" +
        " LEFT JOIN dbo.warehouse OW ON OW.warehouse_id = SI.warehouse_id \n" +
        " LEFT JOIN dbo.inventory RI ON RI.inventory_id = IPRH.requesting_inventory_id \n" +
        " LEFT JOIN dbo.warehouse DW ON DW.warehouse_id = RI.warehouse_id \n" +
        " WHERE IPRH.inventory_product_request_header_id = ? ",
        "", "", ""
    ),
        WarehousePicker
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @Code  NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @Title NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   warehouse_id \n" +
        "  ,warehouse_code \n" +
        "  ,warehouse_title \n" +
        "  ,warehouse_lat \n" +
        "  ,warehouse_long \n" +
        " FROM dbo.warehouse \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " AND CAST(warehouse_code  AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Code,  N'')+N'%' \n" +
        " AND CAST(warehouse_title AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Title, N'')+N'%' \n" +
        " ORDER BY warehouse_code \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", ""
    ),
        FleetTeamPicker
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @Code NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @Name NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   fleet_team_code AS fleet_team_id \n" +
        "  ,fleet_team_code AS fleet_team_code_display \n" +
        "  ,fleet_team_name \n" +
        " FROM dbo.fleet_team \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " AND CAST(fleet_team_code AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Code, N'')+N'%' \n" +
        " AND CAST(fleet_team_name AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Name, N'')+N'%' \n" +
        " ORDER BY fleet_team_code \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", ""
    ),

    UsersPicker
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @username NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   SYS_USER_CODE \n" +
        "  ,SYS_USERNAME \n" +
        " FROM IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_USERS \n" +
        " WHERE CAST(SYS_USERNAME AS NVARCHAR(50)) LIKE N'%'+ISNULL(@username, N'')+N'%' \n" +
        " ORDER BY SYS_USER_CODE \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", ""
    ),
    
        // ---------------------------------------------------------
    // FOB PICKERS — FleetTeamTransports
    // ---------------------------------------------------------
    // These use pagination + search placeholders so they work
    // inside <find-object-box>.
    // ---------------------------------------------------------

    ProductRequestHeaderPicker
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @type NVARCHAR(50)   = NULLIF(?, N'') \n" +
        " DECLARE @status NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @date NVARCHAR(50)   = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   IPRH.inventory_product_request_header_id \n" +
        "  ,IPRT.request_type_title \n" +
        "  ,IPRS.request_status_title \n" +
        "  ,IPRH.request_date \n" +
        " FROM dbo.inventory_product_request_header IPRH \n" +
        " INNER JOIN dbo.inventory_product_request_type   IPRT ON IPRT.request_type_code   = IPRH.request_type_code \n" +
        " INNER JOIN dbo.inventory_product_request_status IPRS ON IPRS.request_status_code = IPRH.request_status_code \n" +
        " WHERE isnull(IPRH.is_deleted, 0) = 0 \n" +
        " AND CAST(IPRT.request_type_title   AS NVARCHAR(50)) LIKE N'%'+ISNULL(@type,   N'')+N'%' \n" +
        " AND CAST(IPRS.request_status_title AS NVARCHAR(50)) LIKE N'%'+ISNULL(@status, N'')+N'%' \n" +
        " AND CAST(IPRH.request_date         AS NVARCHAR(50)) LIKE N'%'+ISNULL(@date,   N'')+N'%' \n" +
        " ORDER BY IPRH.inventory_product_request_header_id DESC \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", ""
    ),

    DriversPicker
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @Name NVARCHAR(100) = NULLIF(?, N'') \n" +
        " DECLARE @Phone NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   drivers_code \n" +
        "  ,drivers_first_name + N' ' + ISNULL(drivers_last_name, N'') AS driver_name \n" +
        "  ,drivers_phone_number \n" +
        " FROM dbo.drivers \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " AND CAST(drivers_first_name + N' ' + ISNULL(drivers_last_name, N'') AS NVARCHAR(100)) LIKE N'%'+ISNULL(@Name, N'')+N'%' \n" +
        " AND CAST(drivers_phone_number AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Phone, N'')+N'%' \n" +
        " ORDER BY drivers_code \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", ""
    ),

    VehiclesPicker
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @Name NVARCHAR(50)  = NULLIF(?, N'') \n" +
        " DECLARE @Plate NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   vehicle_code \n" +
        "  ,vehicle_name \n" +
        "  ,vehicle_licence_plate \n" +
        " FROM dbo.vehicles \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " AND CAST(vehicle_name          AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Name,  N'')+N'%' \n" +
        " AND CAST(vehicle_licence_plate AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Plate, N'')+N'%' \n" +
        " ORDER BY vehicle_code \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", ""
    ),
    
        // ---------------------------------------------------------
    // FOB PICKERS — Inventory & Product
    // ---------------------------------------------------------

    InventoryPicker
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @Type NVARCHAR(50)      = NULLIF(?, N'') \n" +
        " DECLARE @Warehouse NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   I.inventory_id \n" +
        "  ,ISNULL(IT.inventory_type_title, N'(unknown)') AS type_title \n" +
        "  ,ISNULL(W.warehouse_title,      N'(unknown)') AS warehouse_title \n" +
        " FROM dbo.inventory I \n" +
        " LEFT JOIN dbo.inventory_type IT ON IT.inventory_id = I.inventory_type_id \n" +
        " LEFT JOIN dbo.warehouse      W  ON W.warehouse_id  = I.warehouse_id \n" +
        " WHERE isnull(I.is_deleted, 0) = 0 \n" +
        " AND ISNULL(IT.inventory_type_title, N'') LIKE N'%'+ISNULL(@Type,      N'')+N'%' \n" +
        " AND ISNULL(W.warehouse_title,       N'') LIKE N'%'+ISNULL(@Warehouse, N'')+N'%' \n" +
        " ORDER BY I.inventory_id \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", ""
    ),

    ProductPicker
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @Code  NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @Title NVARCHAR(50) = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   product_code AS product_id \n" +
        "  ,product_code AS product_code_display \n" +
        "  ,product_title \n" +
        " FROM dbo.product \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " AND CAST(product_code  AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Code,  N'')+N'%' \n" +
        " AND CAST(product_title AS NVARCHAR(50)) LIKE N'%'+ISNULL(@Title, N'')+N'%' \n" +
        " ORDER BY product_code \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", ""
    ),
    
        InventoryProductRequestHeaderLookup
    (
        " SELECT \n" +
        "   IPRH.inventory_product_request_header_id \n" +
        "  ,N'#' + CAST(IPRH.inventory_product_request_header_id AS NVARCHAR(20)) \n" +
        "   + N' - ' + IPRT.request_type_title + N' (' + IPRS.request_status_title + N')' \n" +
        " FROM dbo.inventory_product_request_header IPRH \n" +
        " INNER JOIN dbo.inventory_product_request_type   IPRT ON IPRT.request_type_code   = IPRH.request_type_code \n" +
        " INNER JOIN dbo.inventory_product_request_status IPRS ON IPRS.request_status_code = IPRH.request_status_code \n" +
        " WHERE isnull(IPRH.is_deleted, 0) = 0 \n" +
        " ORDER BY IPRH.inventory_product_request_header_id DESC ",
        "", "", ""
    ),
        InventoryTypeLookup
    (
        new GenerateGenericSQLQuery.ReadQuery()
            .setTableName("inventory_type")
            .setColumnNames(
                "inventory_id",
                "inventory_type_code",
                "inventory_type_title"
            )
            .setColumnDataTypes(
                "numeric",
                "numeric",
                "nvarchar(50)"
            )
            .setWhereQuery("AND isnull(is_deleted, 0) = 0")
            .getQuery(),
        "",
        "",
        ""
    ),

    WarehouseLookup
    (
        new GenerateGenericSQLQuery.ReadQuery()
            .setTableName("warehouse")
            .setColumnNames(
                "warehouse_id",
                "warehouse_code",
                "warehouse_title"
            )
            .setColumnDataTypes(
                "numeric",
                "numeric",
                "nvarchar(50)"
            )
            .setWhereQuery("AND isnull(is_deleted, 0) = 0")
            .getQuery(),
        "",
        "",
        ""
    ),

    InventoryLookup
    (
        " SELECT \n" +
        "   I.inventory_id \n" +
        "  ,ISNULL(IT.inventory_type_title, N'') + N' — ' + ISNULL(W.warehouse_title, N'') \n" +
        " FROM dbo.inventory I \n" +
        " LEFT JOIN dbo.inventory_type IT ON IT.inventory_id = I.inventory_type_id \n" +
        " LEFT JOIN dbo.warehouse      W  ON W.warehouse_id  = I.warehouse_id \n" +
        " WHERE isnull(I.is_deleted, 0) = 0 \n" +
        " ORDER BY I.inventory_id ",
        "", "", ""
    ),

    ProductLookup
    (
        " SELECT product_code, product_title \n" +
        " FROM dbo.product \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " ORDER BY product_code ",
        "", "", ""
    ),

    ProductTransactionTypeLookup
    (
        " SELECT product_transaction_type_id, title \n" +
        " FROM dbo.product_transaction_type \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " ORDER BY product_transaction_type_id ",
        "", "", ""
    ),

    InventoryProductRequestTypeLookup
    (
        " SELECT request_type_code, request_type_title \n" +
        " FROM dbo.inventory_product_request_type \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " ORDER BY request_type_code ",
        "", "", ""
    ),

    InventoryProductRequestStatusLookup
    (
        " SELECT request_status_code, request_status_title \n" +
        " FROM dbo.inventory_product_request_status \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " ORDER BY request_status_code ",
        "", "", ""
    ),

    FleetTeamLookup
    (
        " SELECT fleet_team_code, fleet_team_name \n" +
        " FROM dbo.fleet_team \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " ORDER BY fleet_team_code ",
        "", "", ""
    ),

    DriversLookup
    (
        " SELECT \n" +
        "   drivers_code \n" +
        "  ,drivers_first_name + N' ' + ISNULL(drivers_last_name, N'') \n" +
        " FROM dbo.drivers \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " ORDER BY drivers_code ",
        "", "", ""
    ),

    VehiclesLookup
    (
        " SELECT vehicle_code, vehicle_name \n" +
        " FROM dbo.vehicles \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " ORDER BY vehicle_code ",
        "", "", ""
    ),

    FleetTripStatusLookup
    (
        " SELECT fleet_trip_status_code, fleet_trip_status_title \n" +
        " FROM dbo.fleet_trip_status \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " ORDER BY fleet_trip_status_code ",
        "", "", ""
    ),

    AccessRequestTypeLookup
    (
        " SELECT access_request_type_code, access_request_type_title \n" +
        " FROM dbo.access_request_type \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " ORDER BY access_request_type_code ",
        "", "", ""
    ),

    AccessRequestStatusLookup
    (
        " SELECT access_request_status_code, access_request_status_title \n" +
        " FROM dbo.access_request_status \n" +
        " WHERE isnull(is_deleted, 0) = 0 \n" +
        " ORDER BY access_request_status_code ",
        "", "", ""
    ),

    UsersLookup
    (
        " SELECT \n" +
        "   SYS_USER_CODE \n" +
        "  ,SYS_USERNAME \n" +
        " FROM IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_USERS \n" +
        " ORDER BY SYS_USER_CODE ",
        "", "", ""
    ),

    SysObjectsLookup
    (
        " SELECT \n" +
        "   OBJECT_CODE \n" +
        "  ,OBJECT_TITLE \n" +
        " FROM IFMS_DB.USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS \n" +
        " WHERE IS_DELETED = 0 \n" +
        " ORDER BY OBJECT_CODE ",
        "", "", ""
    ),
    
        // =========================================================
    // =========================================================
    // SECTION 7 — REPORTS (read-only aggregate views)
    // =========================================================
    // =========================================================

    ReportLowStock
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @warehouse NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @product NVARCHAR(50)   = NULLIF(?, N'') \n" +
        " DECLARE @stock NVARCHAR(50)     = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   IS_.inventory_stock_id \n" +
        "  ,ISNULL(W.warehouse_title, N'(unknown)') AS warehouse_title \n" +
        "  ,ISNULL(P.product_title, N'(unknown)')   AS product_title \n" +
        "  ,IS_.stock_count \n" +
        " FROM dbo.inventory_stock IS_ \n" +
        " LEFT JOIN dbo.inventory I ON I.inventory_id = IS_.inventory_code \n" +
        " LEFT JOIN dbo.warehouse W ON W.warehouse_id = I.warehouse_id \n" +
        " LEFT JOIN dbo.product   P ON P.product_code = IS_.product_code \n" +
        " WHERE isnull(IS_.is_deleted, 0) = 0 \n" +
        " AND ISNULL(W.warehouse_title, N'') LIKE N'%'+ISNULL(@warehouse, N'')+N'%' \n" +
        " AND ISNULL(P.product_title,   N'') LIKE N'%'+ISNULL(@product,   N'')+N'%' \n" +
        " AND CAST(IS_.stock_count AS NVARCHAR(50)) LIKE N'%'+ISNULL(@stock, N'')+N'%' \n" +
        " ORDER BY IS_.stock_count ASC \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", "", 400
    ),

    ReportFleetActivity
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @team NVARCHAR(50)  = NULLIF(?, N'') \n" +
        " DECLARE @trips NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @dist NVARCHAR(50)  = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   ROW_NUMBER() OVER (ORDER BY FTM.fleet_team_name) AS report_row_id \n" +
        "  ,ISNULL(FTM.fleet_team_name, N'(unknown)') AS team_name \n" +
        "  ,COUNT(FT.fleet_trip_id) AS trip_count \n" +
        "  ,ISNULL(SUM(FT.distance_meters), 0) AS total_distance_meters \n" +
        " FROM dbo.fleet_team FTM \n" +
        " LEFT JOIN dbo.fleet_trip FT ON FT.fleet_team_id = FTM.fleet_team_id \n" +
        "      AND isnull(FT.is_deleted, 0) = 0 \n" +
        " WHERE isnull(FTM.is_deleted, 0) = 0 \n" +
        " GROUP BY FTM.fleet_team_name \n" +
        " HAVING ISNULL(FTM.fleet_team_name, N'') LIKE N'%'+ISNULL(@team, N'')+N'%' \n" +
        "    AND CAST(COUNT(FT.fleet_trip_id) AS NVARCHAR(50)) LIKE N'%'+ISNULL(@trips, N'')+N'%' \n" +
        "    AND CAST(ISNULL(SUM(FT.distance_meters), 0) AS NVARCHAR(50)) LIKE N'%'+ISNULL(@dist, N'')+N'%' \n" +
        " ORDER BY FTM.fleet_team_name \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", "", 401
    ),

    ReportProductMovement
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @product NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @txn NVARCHAR(50)     = NULLIF(?, N'') \n" +
        " DECLARE @total NVARCHAR(50)   = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   ROW_NUMBER() OVER (ORDER BY P.product_title, PTT.title) AS report_row_id \n" +
        "  ,ISNULL(P.product_title, N'(unknown)')   AS product_title \n" +
        "  ,ISNULL(PTT.title,       N'(unknown)')   AS transaction_type \n" +
        "  ,SUM(ISH.amount_changed) AS total_change \n" +
        "  ,COUNT(ISH.inventory_stock_history_id) AS event_count \n" +
        " FROM dbo.inventory_stock_history ISH \n" +
        " LEFT JOIN dbo.product                  P   ON P.product_code = ISH.product_changed_code \n" +
        " LEFT JOIN dbo.product_transaction_type PTT ON PTT.product_transaction_type_id = ISH.product_transaction_type_id \n" +
        " GROUP BY P.product_title, PTT.title \n" +
        " HAVING ISNULL(P.product_title, N'') LIKE N'%'+ISNULL(@product, N'')+N'%' \n" +
        "    AND ISNULL(PTT.title,       N'') LIKE N'%'+ISNULL(@txn,     N'')+N'%' \n" +
        "    AND CAST(SUM(ISH.amount_changed) AS NVARCHAR(50)) LIKE N'%'+ISNULL(@total, N'')+N'%' \n" +
        " ORDER BY P.product_title, PTT.title \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", "", 402
    ),

    ReportTickets
    (
        " DECLARE @PAGE_ROW_COUNT INT = ? \n" +
        " DECLARE @WHICH_PAGE INT = ? \n" +
        " DECLARE @type NVARCHAR(50)   = NULLIF(?, N'') \n" +
        " DECLARE @status NVARCHAR(50) = NULLIF(?, N'') \n" +
        " DECLARE @count NVARCHAR(50)  = NULLIF(?, N'') \n" +
        " SELECT \n" +
        "   ROW_NUMBER() OVER (ORDER BY ART.access_request_type_title, ARS.access_request_status_title) AS report_row_id \n" +
        "  ,ISNULL(ART.access_request_type_title,   N'(unknown)') AS type_title \n" +
        "  ,ISNULL(ARS.access_request_status_title, N'(unknown)') AS status_title \n" +
        "  ,COUNT(ARH.access_request_header_id) AS ticket_count \n" +
        " FROM dbo.access_request_header ARH \n" +
        " LEFT JOIN dbo.access_request_type   ART ON ART.access_request_type_code   = ARH.access_request_type_code \n" +
        " LEFT JOIN dbo.access_request_status ARS ON ARS.access_request_status_code = ARH.access_request_status_code \n" +
        " WHERE isnull(ARH.is_deleted, 0) = 0 \n" +
        " GROUP BY ART.access_request_type_title, ARS.access_request_status_title \n" +
        " HAVING ISNULL(ART.access_request_type_title,   N'') LIKE N'%'+ISNULL(@type,   N'')+N'%' \n" +
        "    AND ISNULL(ARS.access_request_status_title, N'') LIKE N'%'+ISNULL(@status, N'')+N'%' \n" +
        "    AND CAST(COUNT(ARH.access_request_header_id) AS NVARCHAR(50)) LIKE N'%'+ISNULL(@count, N'')+N'%' \n" +
        " ORDER BY ART.access_request_type_title, ARS.access_request_status_title \n" +
        " OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
        "", "", "", 403
    ),


    // =========================================================
    // =========================================================
    // SECTION 6 — TRANSACTION LOG
    // =========================================================
    // =========================================================

        UsersDataTransactionLog
        (
            new GenerateGenericSQLQuery.ReadQuery()
                .setTableName("users_data_transaction_log")
                .setColumnNames(
                    "users_data_transaction_log_id",
                    "title",
                    "user_id",
                    "description",
                    "date"
                )
                .setColumnDataTypes(
                    "numeric",
                    "nvarchar(50)",
                    "numeric",
                    "nvarchar(500)",
                    "nvarchar(10)"
                )
                .setWhereQuery("1 = 1")
                .getQuery(),

            "",

            new GenerateGenericSQLQuery.CreateQuery()
                .setTableName("users_data_transaction_log")
                .setColumnNames(
                    "title",
                    "user_id",
                    "description",
                    "date"
                )
                .getQuery(),

            "DELETE FROM users_data_transaction_log WHERE users_data_transaction_log_id = ?"
        );


    // =========================================================
    // =========================================================
    // ENUM FIELDS
    // =========================================================
    // =========================================================

    private final int belongsToObjectCode;
    private final String readQuery;
    private final String updateQuery;
    private final String createQuery;
    private final String deleteQuery;


    // =========================================================
    // CONSTRUCTORS
    // =========================================================

    CrudQueriesEnum(
        String readQuery,
        String updateQuery,
        String createQuery,
        String deleteQuery,
        int belongsToObjectCode
    )
    {
        this.readQuery = readQuery;
        this.updateQuery = updateQuery;
        this.createQuery = createQuery;
        this.deleteQuery = deleteQuery;
        this.belongsToObjectCode = belongsToObjectCode;
    }


    CrudQueriesEnum(
        String readQuery,
        String updateQuery,
        String createQuery,
        String deleteQuery
    )
    {
        this.readQuery = readQuery;
        this.updateQuery = updateQuery;
        this.createQuery = createQuery;
        this.deleteQuery = deleteQuery;
        this.belongsToObjectCode = -1;
    }


    // =========================================================
    // CRUD METHODS
    // =========================================================

    @Override
    public int whoDoesItBelongTo()
    {
        return this.belongsToObjectCode;
    }


    @Override
    public String getReadQuery()
    {
        return this.readQuery;
    }


    @Override
    public String getUpdateQuery()
    {
        return this.updateQuery + " select 1 as status";
    }


    @Override
    public String getDeleteQuery()
    {
        return this.deleteQuery + " select 1 as status";
    }


    @Override
    public String getCreateQuery()
    {
        return this.createQuery + " select 1 as status";
    }

}