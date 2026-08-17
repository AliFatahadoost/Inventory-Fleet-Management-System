
package IFMS.PageRelatedEnums;
import IFMS.InterFaces.CrudQueries;

public enum CrudQueriesEnum implements CrudQueries{
    
    
    fakeTable(
            //read Query
            "DECLARE @PAGE_ROW_COUNT INT = ? "
          + "DECLARE @WHICH_PAGE INT = ? "
          + "DECLARE @FAKE_NAME NVARCHAR(50) = ? "
          + "DECLARE @FAKE_COUNTRY NVARCHAR(50) = ? "
          + "DECLARE @FAKE_FUCKING_FUCK NVARCHAR(50) = ? "
          + "SELECT * FROM FAKE_TABLE "
          + "WHERE (@FAKE_NAME = '' OR NAME LIKE N'%' + @FAKE_NAME + N'%') "
          + "AND (@FAKE_COUNTRY = '' OR LAST_NAME LIKE N'%' + @FAKE_COUNTRY + N'%') "
          + "AND (@FAKE_FUCKING_FUCK = '' OR PASSWORD LIKE N'%' + @FAKE_FUCKING_FUCK + N'%') "
          + "ORDER BY FAKE_TABLE_ID "
          + "OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
            
            //update Query
            "UPDATE FAKE_TABLE \n" +
            "SET NAME = ISNULL(NULLIF(?, ''), NAME),\n" +
            "LAST_NAME = ISNULL(NULLIF(?, ''), LAST_NAME),\n" +
            "PASSWORD = ISNULL(NULLIF(?, ''), PASSWORD)\n" +
            "WHERE FAKE_TABLE_ID = ?",
            
            //Create Query
            "INSERT INTO FAKE_TABLE (NAME, LAST_NAME, PASSWORD) VALUES (? ,? ,?)",
            
            //Delete Query
            "DELETE FROM FAKE_TABLE WHERE FAKE_TABLE_ID = ?"       
    ),
    dataComboTest
    (
            "select FAKE_TABLE_ID, FAKE_NAME from FAKE_TABLE",
            "",
            "",
            ""
    ),
    
    
    
    
    
    Login
            (
                "",
                "",
                "EXEC USERS_DATA_AND_PERMISSIONS.SHOULD_LOGIN ?, ?",
                ""
            ),
    createUser
            (
                "",
                "",
                "EXEC USERS_DATA_AND_PERMISSIONS.MAKE_NEW_USER ?, ?, ?",
                ""
            )
    
    
    ;
    
    
    private final String readQuery;
    private final String updateQuery;
    private final String createQuery;
    private final String deleteQuery;
        
    CrudQueriesEnum(String readQuery, String updateQuery, String createQuery, String deleteQuery)
    {
        this.readQuery = readQuery;
        this.updateQuery = updateQuery;
        this.createQuery = createQuery;
        this.deleteQuery = deleteQuery;   
    }
    
    @Override
    public String getReadQuery(){return this.readQuery;}
    @Override
    public String getUpdateQuery(){return this.updateQuery + "select 1 as status";}
    @Override
    public String getDeleteQuery(){return this.deleteQuery + "select 1 as status";}
    @Override
    public String getCreateQuery(){return this.createQuery + "select 1 as status";}

}
