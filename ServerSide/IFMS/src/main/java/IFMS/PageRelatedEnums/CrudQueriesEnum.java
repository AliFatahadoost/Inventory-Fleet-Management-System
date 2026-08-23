
package IFMS.PageRelatedEnums;
import IFMS.InterFaces.CrudQueries;
import IFMS.DataBase.GenerateGenericSQLQuery;

public enum CrudQueriesEnum implements CrudQueries{
    
   
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
    
    private final int belongsToObjectCode;
    private final String readQuery;
    private final String updateQuery;
    private final String createQuery;
    private final String deleteQuery;
    
    CrudQueriesEnum(String readQuery, String updateQuery, String createQuery, String deleteQuery, int belongsToObjectCode)
    {
        this.readQuery = readQuery;
        this.updateQuery = updateQuery;
        this.createQuery = createQuery;
        this.deleteQuery = deleteQuery; 
        this.belongsToObjectCode = belongsToObjectCode;
    }
    
    CrudQueriesEnum(String readQuery, String updateQuery, String createQuery, String deleteQuery)
    {
        this.readQuery = readQuery;
        this.updateQuery = updateQuery;
        this.createQuery = createQuery;
        this.deleteQuery = deleteQuery; 
        this.belongsToObjectCode = -1;
    }
    
    @Override
    public int whoDoesItBelongTo(){return this.belongsToObjectCode;}
    @Override
    public String getReadQuery(){return this.readQuery;}
    @Override
    public String getUpdateQuery(){return this.updateQuery + "select 1 as status";}
    @Override
    public String getDeleteQuery(){return this.deleteQuery + "select 1 as status";}
    @Override
    public String getCreateQuery(){return this.createQuery + "select 1 as status";}

}
