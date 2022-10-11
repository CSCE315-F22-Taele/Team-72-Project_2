import java.sql.*;
import java.util.Scanner;  
import java.io.File;


public class ExecQuery{

    private Connection conn;

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

    /*public double add(String ingredient){

    }

    public boolean verifyManager(String user, String pass){

    }

    public boolean verifyServer(String user, String pass){

    }*/

    public double getItemPrice(String ingredient) throws Exception{
       String price =  run("SELECT customer_price FROM item WHERE name=" + ingredient);
       if(price==""){
        throw new Exception("Item Not Found");
       }
       double res = Double.parseDouble(price);
       return res;
    }

    public static void main(String[] args) throws Exception{
        ExecQuery ex = new ExecQuery("csce331_906_bass", "330001828");
        double uno = ex.getItemPrice("Steak");
        double dos = ex.getItemPrice("Ground Beef");
        double tres = ex.getItemPrice("Bowls");
        double cuatro = ex.getItemPrice("spaghetti");

        System.out.println(uno + " " + dos + " " + tres + " " + cuatro);
    }


}