package IFMS.DataBase;


public class DataBaseInit {

    
    public static void initBasicDataBaseActions()
    {
    
        initUserTableAndSchemas();
        initObjectsTable();
        initObjectUserPermissionsTable();
        InitIsAuthenticatedSP();
        InitIsAllowedSP();
        InitLoginSP();
        initMakeUser();
        InitGetUserIdFromTokenFunc();
        
        dataBaseUtils.runStaticQuery(
                "exec INIT_DATABASE.SET_UP_SYS_USERS_TABLE\n" +
                "exec INIT_DATABASE.SET_UP_SYS_OBJECTS_TABLE\n" +
                "exec INIT_DATABASE.SET_UP_OBJECT_USER_PERMISSION_TABLE"
        );
    
    }
    
    public static void InitIsAuthenticatedSP()
    {
        dataBaseUtils.runStaticQuery(
                "CREATE OR ALTER PROCEDURE USERS_DATA_AND_PERMISSIONS.IS_AUTHENTICATE\n" +
                "@TOKEN NVARCHAR(512)\n" +
                "AS\n" +
                "BEGIN\n" +
                "	IF EXISTS(SELECT 1 FROM PROJECT_ZERO.USERS_DATA_AND_PERMISSIONS.SYS_USERS WHERE SYS_LOGIN_SESSION = @TOKEN)\n" +
                "		SELECT 1 AS STATUS\n" +
                "	ELSE\n" +
                "		SELECT 0\n" +
                "END"
        );   
    }
    
    public static void InitIsAllowedSP()
    {
        dataBaseUtils.runStaticQuery(
                "CREATE OR ALTER PROCEDURE USERS_DATA_AND_PERMISSIONS.IS_ALLOWED_READ\n" +
                "\n" +
                "    @USER_CODE NUMERIC,\n" +
                "    @OBJECT_CODE NUMERIC\n" +
                "\n" +
                "AS\n" +
                "BEGIN\n" +
                "    SELECT\n" +
                "        CASE\n" +
                "            WHEN EXISTS (\n" +
                "                SELECT 1\n" +
                "                FROM PROJECT_ZERO.USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION\n" +
                "                WHERE USER_CODE = @USER_CODE\n" +
                "                  AND OBJECT_CODE = @OBJECT_CODE\n" +
                "                  AND CAN_READ = 1\n" +
                "            )\n" +
                "            THEN 1\n" +
                "            ELSE 0\n" +
                "        END AS STATUS\n" +
                "END"
        );
        dataBaseUtils.runStaticQuery(
                "CREATE OR ALTER PROCEDURE USERS_DATA_AND_PERMISSIONS.IS_ALLOWED_CREATE\n" +
                "\n" +
                "    @USER_CODE NUMERIC,\n" +
                "    @OBJECT_CODE NUMERIC\n" +
                "\n" +
                "AS\n" +
                "BEGIN\n" +
                "    SELECT\n" +
                "        CASE\n" +
                "            WHEN EXISTS (\n" +
                "                SELECT 1\n" +
                "                FROM PROJECT_ZERO.USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION\n" +
                "                WHERE USER_CODE = @USER_CODE\n" +
                "                  AND OBJECT_CODE = @OBJECT_CODE\n" +
                "                  AND CAN_CREATE = 1\n" +
                "            )\n" +
                "            THEN 1\n" +
                "            ELSE 0\n" +
                "        END AS STATUS\n" +
                "END"
        );
        dataBaseUtils.runStaticQuery(
                "CREATE OR ALTER PROCEDURE USERS_DATA_AND_PERMISSIONS.IS_ALLOWED_UPDATE\n" +
                "\n" +
                "    @USER_CODE NUMERIC,\n" +
                "    @OBJECT_CODE NUMERIC\n" +
                "\n" +
                "AS\n" +
                "BEGIN\n" +
                "    SELECT\n" +
                "        CASE\n" +
                "            WHEN EXISTS (\n" +
                "                SELECT 1\n" +
                "                FROM PROJECT_ZERO.USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION\n" +
                "                WHERE USER_CODE = @USER_CODE\n" +
                "                  AND OBJECT_CODE = @OBJECT_CODE\n" +
                "                  AND CAN_UPDATE = 1\n" +
                "            )\n" +
                "            THEN 1\n" +
                "            ELSE 0\n" +
                "        END AS STATUS\n" +
                "END"
        );
        dataBaseUtils.runStaticQuery(
                "CREATE OR ALTER PROCEDURE USERS_DATA_AND_PERMISSIONS.IS_ALLOWED_DELETE\n" +
                "\n" +
                "    @USER_CODE NUMERIC,\n" +
                "    @OBJECT_CODE NUMERIC\n" +
                "\n" +
                "AS\n" +
                "BEGIN\n" +
                "    SELECT\n" +
                "        CASE\n" +
                "            WHEN EXISTS (\n" +
                "                SELECT 1\n" +
                "                FROM PROJECT_ZERO.USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION\n" +
                "                WHERE USER_CODE = @USER_CODE\n" +
                "                  AND OBJECT_CODE = @OBJECT_CODE\n" +
                "                  AND CAN_DELETE = 1\n" +
                "            )\n" +
                "            THEN 1\n" +
                "            ELSE 0\n" +
                "        END AS STATUS\n" +
                "END"
        );
    }
    
    public static void InitLoginSP()
    {
        dataBaseUtils.runStaticQuery(
                "CREATE OR ALTER PROCEDURE USERS_DATA_AND_PERMISSIONS.SHOULD_LOGIN\n" +
                "@USER_NAME NVARCHAR(50),\n" +
                "@USER_PASSWORD NVARCHAR(50)\n" +
                "AS\n" +
                "BEGIN\n" +
                "SET NOCOUNT ON;\n" +
                "IF NOT EXISTS(SELECT 1 FROM USERS_DATA_AND_PERMISSIONS.SYS_USERS WHERE UPPER(SYS_USER_CODE) = UPPER(@USER_NAME)\n" +
                "																 AND SYS_PASSWORD = HASHBYTES( 'SHA2_512', CONCAT(@USER_PASSWORD, SYS_PASSWORD_SALT)))\n" +
                "BEGIN\n" +
                "	DECLARE @TOKEN VARCHAR(128) = CONVERT(VARCHAR(128),HASHBYTES('SHA2_512',CONVERT(VARCHAR(128), CONCAT('DATA_','_', NEWID()))), 2)\n" +
                "	UPDATE USERS_DATA_AND_PERMISSIONS.SYS_USERS SET SYS_LOGIN_SESSION = @TOKEN WHERE SYS_USERNAME = @USER_NAME\n" +
                "	SELECT @TOKEN AS STATUS\n" +
                "END\n" +
                "ELSE\n" +
                "	SELECT 0 AS STATUS\n" +
                "                		\n" +
                "END"
        );
    }
    
    public static void initMakeUser()
    {
    
        dataBaseUtils.runStaticQuery(
                "CREATE OR ALTER PROCEDURE USERS_DATA_AND_PERMISSIONS.MAKE_NEW_USER\n" +
                "@USER_NAME NVARCHAR(50),\n" +
                "@USER_PASSWORD NVARCHAR(50),\n" +
                "@USER_CODE NUMERIC\n" +
                "AS\n" +
                "BEGIN\n" +
                "SET NOCOUNT ON;\n" +
                "DECLARE @SALT NVARCHAR(23) = CONVERT(NVARCHAR(23), GETDATE(), 121)\n" +
                "IF NOT EXISTS(SELECT 1 FROM USERS_DATA_AND_PERMISSIONS.SYS_USERS WHERE UPPER(SYS_USER_CODE) = UPPER(@USER_NAME))\n" +
                "	BEGIN\n" +
                "	INSERT INTO USERS_DATA_AND_PERMISSIONS.SYS_USERS \n" +
                "	                		( \n" +
                "	                			SYS_USER_CODE, \n" +
                "	                			SYS_USERNAME, \n" +
                "	                			SYS_PASSWORD, \n" +
                "	                			SYS_PASSWORD_SALT \n" +
                "	                		)  \n" +
                "	                		VALUES \n" +
                "	                		( \n" +
                "	                			@USER_CODE, \n" +
                "	                			@USER_NAME, \n" +
                "	                			HASHBYTES( \n" +
                "	                				'SHA2_512', \n" +
                "	                				CONCAT(@USER_PASSWORD, @SALT) \n" +
                "	                			), \n" +
                "	                			@SALT \n" +
                "	                		) \n" +
                "	END                		\n" +
                "                		\n" +
                "END"
        );
    
    }
    
    public static void InitGetUserIdFromTokenFunc()
    {
        dataBaseUtils.runStaticQuery(
                "CREATE OR ALTER PROCEDURE USERS_DATA_AND_PERMISSIONS.GET_USER_ID_FROM_TOKEN\n" +
                "    @TOKEN NVARCHAR(512)\n" +
                "AS\n" +
                "BEGIN\n" +
                "    SELECT ISNULL(\n" +
                "        (\n" +
                "            SELECT SYS_USERS_ID\n" +
                "            FROM PROJECT_ZERO.USERS_DATA_AND_PERMISSIONS.SYS_USERS\n" +
                "            WHERE SYS_LOGIN_SESSION = @TOKEN\n" +
                "        ),\n" +
                "        -1\n" +
                "    ) AS SYS_USER_ID;\n" +
                "END"
        );
    }
    
    
    
    public static void initObjectUserPermissionsTable()
    {
    
        dataBaseUtils.runStaticQuery(
                "CREATE OR ALTER PROCEDURE INIT_DATABASE.SET_UP_OBJECT_USER_PERMISSION_TABLE\n" +
                "AS\n" +
                "BEGIN\n" +
                "\n" +
                "    DECLARE @DOES_TABLE_EXIST BIT = 0;\n" +
                "    DECLARE @DOES_ID_EXIST BIT = 0;\n" +
                "    DECLARE @DOES_USER_CODE_EXIST BIT = 0;\n" +
                "    DECLARE @DOES_OBJECT_CODE_EXIST BIT = 0;\n" +
                "    DECLARE @DOES_CAN_READ_EXIST BIT = 0;\n" +
                "    DECLARE @DOES_CAN_CREATE_EXIST BIT = 0;\n" +
                "    DECLARE @DOES_CAN_UPDATE_EXIST BIT = 0;\n" +
                "    DECLARE @DOES_CAN_DELETE_EXIST BIT = 0;\n" +
                "\n" +
                "\n" +
                "    IF OBJECT_ID(\n" +
                "        'USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION',\n" +
                "        'U'\n" +
                "    ) IS NOT NULL\n" +
                "    BEGIN\n" +
                "\n" +
                "        SET @DOES_TABLE_EXIST = 1;\n" +
                "\n" +
                "\n" +
                "        IF EXISTS (\n" +
                "            SELECT 1\n" +
                "            FROM sys.columns c\n" +
                "            JOIN sys.tables t\n" +
                "                ON c.object_id = t.object_id\n" +
                "            JOIN sys.schemas s\n" +
                "                ON t.schema_id = s.schema_id\n" +
                "            JOIN sys.types ty\n" +
                "                ON c.user_type_id = ty.user_type_id\n" +
                "            WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "              AND t.name = 'OBJECT_USER_PERMISSION'\n" +
                "              AND LOWER(c.name) = LOWER('OBJECT_USER_PERMISSION')\n" +
                "              AND (UPPER(ty.name) = 'NUMERIC'\n" +
                "                   OR UPPER(ty.name) = 'INT')\n" +
                "              AND c.is_identity = 1\n" +
                "              AND c.is_nullable = 0\n" +
                "        )\n" +
                "        BEGIN\n" +
                "            SET @DOES_ID_EXIST = 1;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF EXISTS (\n" +
                "            SELECT 1\n" +
                "            FROM sys.columns c\n" +
                "            JOIN sys.tables t\n" +
                "                ON c.object_id = t.object_id\n" +
                "            JOIN sys.schemas s\n" +
                "                ON t.schema_id = s.schema_id\n" +
                "            JOIN sys.types ty\n" +
                "                ON c.user_type_id = ty.user_type_id\n" +
                "            WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "              AND t.name = 'OBJECT_USER_PERMISSION'\n" +
                "              AND LOWER(c.name) = LOWER('USER_CODE')\n" +
                "              AND (UPPER(ty.name) = 'NUMERIC'\n" +
                "                   OR UPPER(ty.name) = 'INT')\n" +
                "              AND c.is_nullable = 0\n" +
                "        )\n" +
                "        BEGIN\n" +
                "            SET @DOES_USER_CODE_EXIST = 1;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF EXISTS (\n" +
                "            SELECT 1\n" +
                "            FROM sys.columns c\n" +
                "            JOIN sys.tables t\n" +
                "                ON c.object_id = t.object_id\n" +
                "            JOIN sys.schemas s\n" +
                "                ON t.schema_id = s.schema_id\n" +
                "            JOIN sys.types ty\n" +
                "                ON c.user_type_id = ty.user_type_id\n" +
                "            WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "              AND t.name = 'OBJECT_USER_PERMISSION'\n" +
                "              AND LOWER(c.name) = LOWER('OBJECT_CODE')\n" +
                "              AND (UPPER(ty.name) = 'NUMERIC'\n" +
                "                   OR UPPER(ty.name) = 'INT')\n" +
                "              AND c.is_nullable = 0\n" +
                "        )\n" +
                "        BEGIN\n" +
                "            SET @DOES_OBJECT_CODE_EXIST = 1;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF EXISTS (\n" +
                "            SELECT 1\n" +
                "            FROM sys.columns c\n" +
                "            JOIN sys.tables t\n" +
                "                ON c.object_id = t.object_id\n" +
                "            JOIN sys.schemas s\n" +
                "                ON t.schema_id = s.schema_id\n" +
                "            JOIN sys.types ty\n" +
                "                ON c.user_type_id = ty.user_type_id\n" +
                "            WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "              AND t.name = 'OBJECT_USER_PERMISSION'\n" +
                "              AND LOWER(c.name) = LOWER('CAN_READ')\n" +
                "              AND UPPER(ty.name) = 'BIT'\n" +
                "              AND c.is_nullable = 0\n" +
                "        )\n" +
                "        BEGIN\n" +
                "            SET @DOES_CAN_READ_EXIST = 1;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF EXISTS (\n" +
                "            SELECT 1\n" +
                "            FROM sys.columns c\n" +
                "            JOIN sys.tables t\n" +
                "                ON c.object_id = t.object_id\n" +
                "            JOIN sys.schemas s\n" +
                "                ON t.schema_id = s.schema_id\n" +
                "            JOIN sys.types ty\n" +
                "                ON c.user_type_id = ty.user_type_id\n" +
                "            WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "              AND t.name = 'OBJECT_USER_PERMISSION'\n" +
                "              AND LOWER(c.name) = LOWER('CAN_CREATE')\n" +
                "              AND UPPER(ty.name) = 'BIT'\n" +
                "              AND c.is_nullable = 0\n" +
                "        )\n" +
                "        BEGIN\n" +
                "            SET @DOES_CAN_CREATE_EXIST = 1;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF EXISTS (\n" +
                "            SELECT 1\n" +
                "            FROM sys.columns c\n" +
                "            JOIN sys.tables t\n" +
                "                ON c.object_id = t.object_id\n" +
                "            JOIN sys.schemas s\n" +
                "                ON t.schema_id = s.schema_id\n" +
                "            JOIN sys.types ty\n" +
                "                ON c.user_type_id = ty.user_type_id\n" +
                "            WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "              AND t.name = 'OBJECT_USER_PERMISSION'\n" +
                "              AND LOWER(c.name) = LOWER('CAN_UPDATE')\n" +
                "              AND UPPER(ty.name) = 'BIT'\n" +
                "              AND c.is_nullable = 0\n" +
                "        )\n" +
                "        BEGIN\n" +
                "            SET @DOES_CAN_UPDATE_EXIST = 1;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF EXISTS (\n" +
                "            SELECT 1\n" +
                "            FROM sys.columns c\n" +
                "            JOIN sys.tables t\n" +
                "                ON c.object_id = t.object_id\n" +
                "            JOIN sys.schemas s\n" +
                "                ON t.schema_id = s.schema_id\n" +
                "            JOIN sys.types ty\n" +
                "                ON c.user_type_id = ty.user_type_id\n" +
                "            WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "              AND t.name = 'OBJECT_USER_PERMISSION'\n" +
                "              AND LOWER(c.name) = LOWER('CAN_DELETE')\n" +
                "              AND UPPER(ty.name) = 'BIT'\n" +
                "              AND c.is_nullable = 0\n" +
                "        )\n" +
                "        BEGIN\n" +
                "            SET @DOES_CAN_DELETE_EXIST = 1;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF @DOES_ID_EXIST = 0\n" +
                "           OR @DOES_USER_CODE_EXIST = 0\n" +
                "           OR @DOES_OBJECT_CODE_EXIST = 0\n" +
                "           OR @DOES_CAN_READ_EXIST = 0\n" +
                "           OR @DOES_CAN_CREATE_EXIST = 0\n" +
                "           OR @DOES_CAN_UPDATE_EXIST = 0\n" +
                "           OR @DOES_CAN_DELETE_EXIST = 0\n" +
                "        BEGIN\n" +
                "            DROP TABLE USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION;\n" +
                "            SET @DOES_TABLE_EXIST = 0;\n" +
                "        END\n" +
                "\n" +
                "    END\n" +
                "\n" +
                "\n" +
                "    IF @DOES_TABLE_EXIST = 0\n" +
                "    BEGIN\n" +
                "\n" +
                "        CREATE TABLE USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION\n" +
                "        (\n" +
                "            OBJECT_USER_PERMISSION NUMERIC IDENTITY(1,1) PRIMARY KEY,\n" +
                "\n" +
                "            USER_CODE NUMERIC NOT NULL,\n" +
                "\n" +
                "            OBJECT_CODE NUMERIC NOT NULL,\n" +
                "\n" +
                "            CAN_READ BIT NOT NULL DEFAULT 0,\n" +
                "\n" +
                "            CAN_CREATE BIT NOT NULL DEFAULT 0,\n" +
                "\n" +
                "            CAN_UPDATE BIT NOT NULL DEFAULT 0,\n" +
                "\n" +
                "            CAN_DELETE BIT NOT NULL DEFAULT 0,\n" +
                "\n" +
                "            CONSTRAINT UQ_OBJECT_USER_PERMISSION\n" +
                "                UNIQUE (USER_CODE, OBJECT_CODE)\n" +
                "        );\n" +
                "\n" +
                "    END\n" +
                "\n" +
                "\n" +
                "    /*\n" +
                "        Create a permission row for every\n" +
                "        USER_CODE × OBJECT_CODE combination\n" +
                "        that does not already exist.\n" +
                "    */\n" +
                "\n" +
                "    INSERT INTO USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION\n" +
                "    (\n" +
                "        USER_CODE,\n" +
                "        OBJECT_CODE\n" +
                "    )\n" +
                "    SELECT\n" +
                "        U.SYS_USER_CODE,\n" +
                "        O.OBJECT_CODE\n" +
                "    FROM USERS_DATA_AND_PERMISSIONS.SYS_USERS U\n" +
                "    CROSS JOIN USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS O\n" +
                "    WHERE NOT EXISTS\n" +
                "    (\n" +
                "        SELECT 1\n" +
                "        FROM USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION P\n" +
                "        WHERE P.USER_CODE = U.SYS_USER_CODE\n" +
                "          AND P.OBJECT_CODE = O.OBJECT_CODE\n" +
                "    );\n" +
                "\n" +
                "\n" +
                "    /*\n" +
                "        ADMIN is USER_CODE = 1.\n" +
                "        ADMIN gets every permission for every object.\n" +
                "    */\n" +
                "\n" +
                "    UPDATE P\n" +
                "    SET\n" +
                "        CAN_READ = 1,\n" +
                "        CAN_CREATE = 1,\n" +
                "        CAN_UPDATE = 1,\n" +
                "        CAN_DELETE = 1\n" +
                "    FROM USERS_DATA_AND_PERMISSIONS.OBJECT_USER_PERMISSION P\n" +
                "    INNER JOIN USERS_DATA_AND_PERMISSIONS.SYS_USERS U\n" +
                "        ON P.USER_CODE = U.SYS_USER_CODE\n" +
                "    WHERE U.SYS_USER_CODE = 1;\n" +
                "\n" +
                "END"
        );
    
    }
    
    
    public static void initObjectsTable() //each Page is considered object all of the inner Queries and stuff included
    {
    
        dataBaseUtils.runStaticQuery(
                "CREATE OR ALTER PROCEDURE INIT_DATABASE.SET_UP_SYS_OBJECTS_TABLE\n" +
                "AS\n" +
                "BEGIN\n" +
                "\n" +
                "    DECLARE @DOES_TABLE_EXIST BIT = 0;\n" +
                "    DECLARE @DOES_OBJECT_CODE_EXIST BIT = 0;\n" +
                "    DECLARE @DOES_OBJECT_TITLE_EXIST BIT = 0;\n" +
                "    DECLARE @DOES_IS_DELETED_EXIST BIT = 0;\n" +
                "    DECLARE @DOES_OBJECT_ID_EXIST BIT = 0;\n" +
                "\n" +
                "    IF OBJECT_ID('USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS', 'U') IS NOT NULL\n" +
                "    BEGIN\n" +
                "        SET @DOES_TABLE_EXIST = 1;\n" +
                "\n" +
                "        IF EXISTS (\n" +
                "            SELECT 1\n" +
                "            FROM sys.columns c\n" +
                "            JOIN sys.tables t\n" +
                "                ON c.object_id = t.object_id\n" +
                "            JOIN sys.schemas s\n" +
                "                ON t.schema_id = s.schema_id\n" +
                "            JOIN sys.types ty\n" +
                "                ON c.user_type_id = ty.user_type_id\n" +
                "            WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "              AND t.name = 'SYS_OBJECTS'\n" +
                "              AND LOWER(c.name) = LOWER('OBJECT_CODE')\n" +
                "              AND (UPPER(ty.name) = 'NUMERIC' OR UPPER(ty.name) = 'INT')\n" +
                "              AND c.is_nullable = 0\n" +
                "        )\n" +
                "        BEGIN\n" +
                "            SET @DOES_OBJECT_CODE_EXIST = 1;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF EXISTS (\n" +
                "            SELECT 1\n" +
                "            FROM sys.columns c\n" +
                "            JOIN sys.tables t\n" +
                "                ON c.object_id = t.object_id\n" +
                "            JOIN sys.schemas s\n" +
                "                ON t.schema_id = s.schema_id\n" +
                "            JOIN sys.types ty\n" +
                "                ON c.user_type_id = ty.user_type_id\n" +
                "            WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "              AND t.name = 'SYS_OBJECTS'\n" +
                "              AND LOWER(c.name) = LOWER('OBJECT_TITLE')\n" +
                "              AND (\n" +
                "                    (UPPER(ty.name) = 'NVARCHAR' AND c.max_length >= 100)\n" +
                "                    OR\n" +
                "                    (UPPER(ty.name) = 'VARCHAR' AND c.max_length >= 50)\n" +
                "                  )\n" +
                "              AND c.is_nullable = 0\n" +
                "        )\n" +
                "        BEGIN\n" +
                "            SET @DOES_OBJECT_TITLE_EXIST = 1;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF EXISTS (\n" +
                "            SELECT 1\n" +
                "            FROM sys.columns c\n" +
                "            JOIN sys.tables t\n" +
                "                ON c.object_id = t.object_id\n" +
                "            JOIN sys.schemas s\n" +
                "                ON t.schema_id = s.schema_id\n" +
                "            JOIN sys.types ty\n" +
                "                ON c.user_type_id = ty.user_type_id\n" +
                "            WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "              AND t.name = 'SYS_OBJECTS'\n" +
                "              AND LOWER(c.name) = LOWER('IS_DELETED')\n" +
                "              AND (\n" +
                "                    UPPER(ty.name) = 'BIT'\n" +
                "                  )\n" +
                "              AND c.is_nullable = 0\n" +
                "        )\n" +
                "        BEGIN\n" +
                "            SET @DOES_IS_DELETED_EXIST = 1;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF EXISTS (\n" +
                "            SELECT 1\n" +
                "            FROM sys.columns c\n" +
                "            JOIN sys.tables t\n" +
                "                ON c.object_id = t.object_id\n" +
                "            JOIN sys.schemas s\n" +
                "                ON t.schema_id = s.schema_id\n" +
                "            JOIN sys.types ty\n" +
                "                ON c.user_type_id = ty.user_type_id\n" +
                "            WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "              AND t.name = 'SYS_OBJECTS'\n" +
                "              AND LOWER(c.name) = LOWER('OBJECT_ID')\n" +
                "              AND (UPPER(ty.name) = 'NUMERIC' OR UPPER(ty.name) = 'INT')\n" +
                "              AND c.is_nullable = 0\n" +
                "        )\n" +
                "        BEGIN\n" +
                "            SET @DOES_OBJECT_ID_EXIST = 1;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF @DOES_IS_DELETED_EXIST = 0\n" +
                "        BEGIN\n" +
                "            DROP TABLE USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS;\n" +
                "            SET @DOES_TABLE_EXIST = 0;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF @DOES_OBJECT_CODE_EXIST = 0\n" +
                "        BEGIN\n" +
                "            DROP TABLE USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS;\n" +
                "            SET @DOES_TABLE_EXIST = 0;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF @DOES_OBJECT_TITLE_EXIST = 0\n" +
                "        BEGIN\n" +
                "            DROP TABLE USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS;\n" +
                "            SET @DOES_TABLE_EXIST = 0;\n" +
                "        END\n" +
                "\n" +
                "\n" +
                "        IF @DOES_OBJECT_ID_EXIST = 0 AND @DOES_TABLE_EXIST = 1\n" +
                "        BEGIN\n" +
                "            DROP TABLE USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS;\n" +
                "            SET @DOES_TABLE_EXIST = 0;\n" +
                "        END\n" +
                "\n" +
                "    END\n" +
                "\n" +
                "\n" +
                "    IF @DOES_TABLE_EXIST = 0\n" +
                "    BEGIN\n" +
                "        CREATE TABLE USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS\n" +
                "        (\n" +
                "            OBJECT_ID NUMERIC IDENTITY(1,1) PRIMARY KEY,\n" +
                "            OBJECT_CODE NUMERIC NOT NULL UNIQUE,\n" +
                "            OBJECT_TITLE NVARCHAR(50) NOT NULL UNIQUE,\n" +
                "            IS_DELETED BIT NOT NULL\n" +
                "        )\n" +
                "    END\n" +
                "    \n" +
                "    IF NOT EXISTS (\n" +
                "    SELECT 1\n" +
                "    FROM USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS\n" +
                "    WHERE OBJECT_CODE = 1\n" +
                "	)\n" +
                "	BEGIN\n" +
                "	    INSERT INTO USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS\n" +
                "	        (OBJECT_CODE, OBJECT_TITLE, IS_DELETED)\n" +
                "	    VALUES\n" +
                "	        (1, N'LOGIN', 0);\n" +
                "	END\n" +
                "	ELSE\n" +
                "	BEGIN\n" +
                "	    UPDATE USERS_DATA_AND_PERMISSIONS.SYS_OBJECTS\n" +
                "	    SET IS_DELETED = 0\n" +
                "	    WHERE OBJECT_CODE = 1\n" +
                "	      AND IS_DELETED = 1;\n" +
                "	END\n" +
                "\n" +
                "END"
        );
        
    }
    
    
    public static void initUserTableAndSchemas()
    {
    
        dataBaseUtils.runStaticQuery(
                "IF NOT EXISTS (\n" +
                "    SELECT 1\n" +
                "    FROM sys.schemas\n" +
                "    WHERE name = 'INIT_DATABASE'\n" +
                ")\n" +
                "BEGIN\n" +
                "    EXEC('CREATE SCHEMA INIT_DATABASE');\n" +
                "END;\n" +
                "\n" +
                "IF NOT EXISTS (\n" +
                "    SELECT 1\n" +
                "    FROM sys.schemas\n" +
                "    WHERE name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                ")\n" +
                "BEGIN\n" +
                "    EXEC('CREATE SCHEMA USERS_DATA_AND_PERMISSIONS');\n" +
                "END;" +
                "\n" +
                "CREATE OR ALTER PROCEDURE INIT_DATABASE.SET_UP_SYS_USERS_TABLE\n" +
                "AS\n" +
                "BEGIN\n" +
                "	\n" +
                "	DECLARE @DOES_TABLE_EXIST BIT = 0;\n" +
                "	DECLARE @DOES_ID_EXIST BIT = 0;\n" +
                "	DECLARE @DOES_CODE_EXIST BIT = 0;\n" +
                "	DECLARE @DOES_USERNAME_EXIST BIT = 0;\n" +
                "	DECLARE @DOES_PASSWORD_EXIST BIT = 0;\n" +
                "	DECLARE @DOES_SESSION_EXIST BIT = 0;\n" +
                "	DECLARE @DOES_PASSWORD_SALT_EXIST BIT = 0;\n" +
                "	\n" +
                "	IF OBJECT_ID('USERS_DATA_AND_PERMISSIONS.SYS_USERS', 'U') IS NOT NULL\n" +
                "	BEGIN\n" +
                "		SET @DOES_TABLE_EXIST = 1;\n" +
                "		\n" +
                "		IF EXISTS (\n" +
                "			SELECT 1\n" +
                "			FROM sys.columns c\n" +
                "			JOIN sys.tables t\n" +
                "				ON c.object_id = t.object_id\n" +
                "			JOIN sys.schemas s\n" +
                "				ON t.schema_id = s.schema_id\n" +
                "			JOIN sys.types ty\n" +
                "				ON c.user_type_id = ty.user_type_id\n" +
                "			WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "			  AND t.name = 'SYS_USERS'\n" +
                "			  AND LOWER(c.name) = LOWER('SYS_USERS_ID')\n" +
                "			  AND (UPPER(ty.name) = 'NUMERIC' OR UPPER(ty.name) = 'INT') \n" +
                "			  AND c.is_identity = 1\n" +
                "			  AND c.is_nullable = 0\n" +
                "		)\n" +
                "		BEGIN\n" +
                "			SET @DOES_ID_EXIST = 1\n" +
                "		END\n" +
                "		\n" +
                "		\n" +
                "		IF EXISTS (\n" +
                "			SELECT 1\n" +
                "			FROM sys.columns c\n" +
                "			JOIN sys.tables t\n" +
                "				ON c.object_id = t.object_id\n" +
                "			JOIN sys.schemas s\n" +
                "				ON t.schema_id = s.schema_id\n" +
                "			JOIN sys.types ty\n" +
                "				ON c.user_type_id = ty.user_type_id\n" +
                "			WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "			  AND t.name = 'SYS_USERS'\n" +
                "			  AND LOWER(c.name) = LOWER('SYS_USER_CODE')\n" +
                "			  AND (UPPER(ty.name) = 'NUMERIC' OR UPPER(ty.name) = 'INT')\n" +
                "			  AND c.is_nullable = 0\n" +
                "		)\n" +
                "		BEGIN\n" +
                "			SET @DOES_CODE_EXIST = 1\n" +
                "		END\n" +
                "		\n" +
                "		\n" +
                "		IF EXISTS (\n" +
                "			SELECT 1\n" +
                "			FROM sys.columns c\n" +
                "			JOIN sys.tables t\n" +
                "				ON c.object_id = t.object_id\n" +
                "			JOIN sys.schemas s\n" +
                "				ON t.schema_id = s.schema_id\n" +
                "			JOIN sys.types ty\n" +
                "				ON c.user_type_id = ty.user_type_id\n" +
                "			WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "			  AND t.name = 'SYS_USERS'\n" +
                "			  AND LOWER(c.name) = LOWER('SYS_USERNAME')\n" +
                "			  AND (\n" +
                "					(UPPER(ty.name) = 'NVARCHAR' AND c.max_length >= 100)\n" +
                "					OR\n" +
                "					(UPPER(ty.name) = 'VARCHAR' AND c.max_length >= 50)\n" +
                "				  )\n" +
                "			  AND c.is_nullable = 0\n" +
                "		)\n" +
                "		BEGIN\n" +
                "			SET @DOES_USERNAME_EXIST = 1\n" +
                "		END\n" +
                "		\n" +
                "		\n" +
                "		IF EXISTS (\n" +
                "			SELECT 1\n" +
                "			FROM sys.columns c\n" +
                "			JOIN sys.tables t\n" +
                "				ON c.object_id = t.object_id\n" +
                "			JOIN sys.schemas s\n" +
                "				ON t.schema_id = s.schema_id\n" +
                "			JOIN sys.types ty\n" +
                "				ON c.user_type_id = ty.user_type_id\n" +
                "			WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "			  AND t.name = 'SYS_USERS'\n" +
                "			  AND LOWER(c.name) = LOWER('SYS_PASSWORD')\n" +
                "			  AND UPPER(ty.name) = 'VARBINARY'\n" +
                "			  AND c.is_nullable = 0\n" +
                "			  AND c.max_length >= 512\n" +
                "		)\n" +
                "		BEGIN\n" +
                "			SET @DOES_PASSWORD_EXIST = 1\n" +
                "		END\n" +
                "		\n" +
                "		\n" +
                "		IF EXISTS (\n" +
                "			SELECT 1\n" +
                "			FROM sys.columns c\n" +
                "			JOIN sys.tables t\n" +
                "				ON c.object_id = t.object_id\n" +
                "			JOIN sys.schemas s\n" +
                "				ON t.schema_id = s.schema_id\n" +
                "			JOIN sys.types ty\n" +
                "				ON c.user_type_id = ty.user_type_id\n" +
                "			WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "			  AND t.name = 'SYS_USERS'\n" +
                "			  AND LOWER(c.name) = LOWER('SYS_PASSWORD_SALT')\n" +
                "			  AND UPPER(ty.name) = 'NVARCHAR'\n" +
                "			  AND c.is_nullable = 0\n" +
                "			  AND c.max_length >= 46\n" +
                "		)\n" +
                "		BEGIN\n" +
                "			SET @DOES_PASSWORD_SALT_EXIST = 1\n" +
                "		END\n" +
                "		\n" +
                "		\n" +
                "		IF EXISTS (\n" +
                "			SELECT 1\n" +
                "			FROM sys.columns c\n" +
                "			JOIN sys.tables t\n" +
                "				ON c.object_id = t.object_id\n" +
                "			JOIN sys.schemas s\n" +
                "				ON t.schema_id = s.schema_id\n" +
                "			JOIN sys.types ty\n" +
                "				ON c.user_type_id = ty.user_type_id\n" +
                "			WHERE s.name = 'USERS_DATA_AND_PERMISSIONS'\n" +
                "			  AND t.name = 'SYS_USERS'\n" +
                "			  AND LOWER(c.name) = LOWER('SYS_LOGIN_SESSION')\n" +
                "			  AND UPPER(ty.name) = 'NVARCHAR'\n" +
                "			  AND c.is_nullable = 1\n" +
                "			  AND c.max_length >= 1024\n" +
                "		)\n" +
                "		BEGIN\n" +
                "			SET @DOES_SESSION_EXIST = 1\n" +
                "		END\n" +
                "		\n" +
                "		\n" +
                "		IF @DOES_SESSION_EXIST = 0\n" +
                "		BEGIN\n" +
                "			DROP TABLE USERS_DATA_AND_PERMISSIONS.SYS_USERS\n" +
                "			SET @DOES_TABLE_EXIST = 0\n" +
                "		END\n" +
                "		\n" +
                "		\n" +
                "		IF @DOES_PASSWORD_EXIST = 0\n" +
                "		BEGIN\n" +
                "			DROP TABLE USERS_DATA_AND_PERMISSIONS.SYS_USERS\n" +
                "			SET @DOES_TABLE_EXIST = 0\n" +
                "		END\n" +
                "		\n" +
                "		\n" +
                "		IF @DOES_USERNAME_EXIST = 0\n" +
                "		BEGIN\n" +
                "			DROP TABLE USERS_DATA_AND_PERMISSIONS.SYS_USERS\n" +
                "			SET @DOES_TABLE_EXIST = 0\n" +
                "		END\n" +
                "		\n" +
                "		\n" +
                "		IF @DOES_PASSWORD_SALT_EXIST = 0\n" +
                "		BEGIN\n" +
                "			DROP TABLE USERS_DATA_AND_PERMISSIONS.SYS_USERS\n" +
                "			SET @DOES_TABLE_EXIST = 0\n" +
                "		END\n" +
                "		\n" +
                "		\n" +
                "		IF @DOES_CODE_EXIST = 0\n" +
                "		BEGIN\n" +
                "			DROP TABLE USERS_DATA_AND_PERMISSIONS.SYS_USERS\n" +
                "			SET @DOES_TABLE_EXIST = 0\n" +
                "		END\n" +
                "		\n" +
                "		\n" +
                "		IF @DOES_ID_EXIST = 0 AND @DOES_TABLE_EXIST = 1\n" +
                "		BEGIN\n" +
                "			DROP TABLE USERS_DATA_AND_PERMISSIONS.SYS_USERS\n" +
                "			SET @DOES_TABLE_EXIST = 0\n" +
                "		END\n" +
                "		\n" +
                "	END\n" +
                "	\n" +
                "	\n" +
                "	IF @DOES_TABLE_EXIST = 0\n" +
                "	BEGIN\n" +
                "		CREATE TABLE USERS_DATA_AND_PERMISSIONS.SYS_USERS\n" +
                "		(\n" +
                "			SYS_USERS_ID NUMERIC IDENTITY(1,1) PRIMARY KEY,\n" +
                "			SYS_USER_CODE NUMERIC NOT NULL UNIQUE,\n" +
                "			SYS_USERNAME NVARCHAR(50) NOT NULL UNIQUE,\n" +
                "			SYS_PASSWORD VARBINARY(512) NOT NULL,\n" +
                "			SYS_PASSWORD_SALT NVARCHAR(23) NOT NULL\n" +
                "				DEFAULT CONVERT(NVARCHAR(23), GETDATE(), 121),\n" +
                "			SYS_LOGIN_SESSION NVARCHAR(512)\n" +
                "		)\n" +
                "	END\n" +
                "	\n" +
                "	\n" +
                "	IF NOT EXISTS\n" +
                "	(\n" +
                "		SELECT 1\n" +
                "		FROM USERS_DATA_AND_PERMISSIONS.SYS_USERS\n" +
                "		WHERE SYS_USER_CODE = 1\n" +
                "		  AND SYS_USERNAME = N'ADMIN'\n" +
                "	)\n" +
                "	BEGIN\n" +
                "		\n" +
                "		DECLARE @SALT NVARCHAR(23) =\n" +
                "			CONVERT(NVARCHAR(23), GETDATE(), 121)\n" +
                "		\n" +
                "		DELETE FROM USERS_DATA_AND_PERMISSIONS.SYS_USERS\n" +
                "		WHERE SYS_USER_CODE = 1\n" +
                "		   OR SYS_USERNAME = N'ADMIN'\n" +
                "		\n" +
                "		INSERT INTO USERS_DATA_AND_PERMISSIONS.SYS_USERS\n" +
                "		(\n" +
                "			SYS_USER_CODE,\n" +
                "			SYS_USERNAME,\n" +
                "			SYS_PASSWORD,\n" +
                "			SYS_PASSWORD_SALT\n" +
                "		) \n" +
                "		VALUES\n" +
                "		(\n" +
                "			1,\n" +
                "			N'ADMIN',\n" +
                "			HASHBYTES(\n" +
                "				'SHA2_512',\n" +
                "				CONCAT(N'12', @SALT)\n" +
                "			),\n" +
                "			@SALT\n" +
                "		)\n" +
                "		\n" +
                "	END\n" +
                "	\n" +
                "END"
        );
    }
}
