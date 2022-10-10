import java.sql.*;
import java.util.Scanner;  
import java.io.File;


public class ExecQuery{

    private Connection conn;
    
    public ExecQuery(){
        conn = null;
        String dbName = "csce331_906_72";
        String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;

        try{
            conn = DriverManager.getConnection(dbConnectionString, "csce331_906_krueger", "730001845");
        }catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(1);
        }
        
        System.out.println("Opened database successfully");

    }

    public ExecQuery(String user, String pswd){
        conn = null;
        String dbName = "csce331_906_72";
        String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;

        try{
            conn = DriverManager.getConnection(dbConnectionString, user, pswd);
        }catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(1);
        }
        
        System.out.println("Opened database successfully");
    }


    public String run(String query){
        try{
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);
            ResultSetMetaData rsmd = result.getMetaData();

            int columnsNumber = rsmd.getColumnCount();
    
            String res = "";

            while (result.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1){
                        res += "  ";
                    }
                    res += result.getString(i);        
                }
            }
            
            return res;

        }catch(Exception e){
            if (! e.getMessage().equals("No results were returned by the query.")){
                //e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                System.out.println("Tried Executing: "+ query);
            }
            return "";
        }  
    }


    public void close(){
        try {
            conn.close();
            System.out.println("Connection Closed.");
        } catch(Exception e) {
            System.out.println("Connection NOT Closed.");
        }
    }


}
