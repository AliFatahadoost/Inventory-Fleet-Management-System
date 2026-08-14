
package IFMS.PageRelatedEnums;

public enum CrudQueriesEnum {
    
    
    fakeTable(
            //read Query
            "DECLARE @PAGE_ROW_COUNT INT = ? "
          + "DECLARE @WHICH_PAGE INT = ? "
          + "DECLARE @FAKE_NAME NVARCHAR(50) = ? "
          + "DECLARE @FAKE_COUNTRY NVARCHAR(50) = ? "
          + "DECLARE @FAKE_FUCKING_FUCK NVARCHAR(50) = ? "
          + "SELECT * FROM FAKE_TABLE "
          + "WHERE (@FAKE_NAME = '' OR FAKE_NAME LIKE N'%' + @FAKE_NAME + N'%') "
          + "AND (@FAKE_COUNTRY = '' OR FAKE_COUNTRY LIKE N'%' + @FAKE_COUNTRY + N'%') "
          + "AND (@FAKE_FUCKING_FUCK = '' OR FAKE_FUCKING_FUCK LIKE N'%' + @FAKE_FUCKING_FUCK + N'%') "
          + "ORDER BY FAKE_TABLE_ID "
          + "OFFSET ((@WHICH_PAGE - 1) * @PAGE_ROW_COUNT) ROWS FETCH NEXT @PAGE_ROW_COUNT ROWS ONLY ",
            
            //update Query
            "UPDATE FAKE_TABLE \n" +
            "SET FAKE_NAME = ISNULL(NULLIF(?, ''), FAKE_NAME),\n" +
            "FAKE_COUNTRY = ISNULL(NULLIF(?, ''), FAKE_COUNTRY),\n" +
            "FAKE_FUCKING_FUCK = ISNULL(NULLIF(?, ''), FAKE_FUCKING_FUCK)\n" +
            "WHERE FAKE_TABLE_ID = ?",
            
            //Create Query
            "INSERT INTO FAKE_TABLE (FAKE_NAME, FAKE_COUNTRY, FAKE_FUCKING_FUCK) VALUES (? ,? ,?)",
            
            //Delete Query
            "DELETE FROM FAKE_TABLE WHERE FAKE_TABLE_ID = ?"       
    ),
    dataComboTest
    (
            "select FAKE_TABLE_ID, FAKE_NAME from FAKE_TABLE",
            "",
            "",
            ""
    );
    
    
    private String readQuery;
    private String updateQuery;
    private String createQuery;
    private String deleteQuery;
        
    CrudQueriesEnum(String readQuery, String updateQuery, String createQuery, String deleteQuery)
    {
        this.readQuery = readQuery;
        this.updateQuery = updateQuery;
        this.createQuery = createQuery;
        this.deleteQuery = deleteQuery;   
    }
    
    public String getReadQuery(){return this.readQuery;}
    public String getUpdateQuery(){return this.updateQuery + "select 1 as status";}
    public String getDeleteQuery(){return this.deleteQuery + "select 1 as status";}
    public String getCreateQuery(){return this.createQuery + "select 1 as status";}

}
