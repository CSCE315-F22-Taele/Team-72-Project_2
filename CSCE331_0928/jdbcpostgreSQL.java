import java.sql.*;

/*
CSCE 331
9-28-2022 Lab
 */
public class jdbcpostgreSQL{

  //Commands to run this script
  //This will compile all java files in this directory
  //javac *.java
  //This command tells the file where to find the postgres jar which it needs to execute postgres commands, then executes the code

  /* DON"T COPY PASTE WRITE THE COMMANDS IN YOUR TERMINAL MANUALLY*/

  //Windows: java -cp ".;postgresql-42.2.8.jar" jdbcpostgreSQL
  //Mac/Linux: java -cp ".:postgresql-42.2.8.jar" jdbcpostgreSQL

  //MAKE SURE YOU ARE ON VPN or TAMU WIFI TO ACCESS DATABASE
  public static void main(String args[]) {

    //Building the connection with your credentials
    Connection conn = null;
    String teamNumber = "72"; // Your team number
    String sectionNumber = "906"; // Your section number
    String dbName = "csce331_" + sectionNumber + "_" + teamNumber;
    String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
    dbSetup myCredentials = new dbSetup(); 

    //Connecting to the database
    try {
        conn = DriverManager.getConnection(dbConnectionString, dbSetup.user, dbSetup.pswd);
     } catch (Exception e) {
        e.printStackTrace();
        System.err.println(e.getClass().getName()+": "+e.getMessage());
        System.exit(0);
     }

     System.out.println("Opened database successfully");

     try{
       Statement stmt = conn.createStatement();
       String[] queries = {"SELECT AVG(inventory) FROM item",
       "SELECT AVG(restock_amount) FROM item",
       "SELECT id, name, inventory FROM item WHERE inventory<=50 LIMIT 10",
       "SELECT * FROM item WHERE inventory>=150 LIMIT 10",
       "SELECT * FROM item ORDER BY type ASC LIMIT 10",
       "SELECT AVG(price) FROM customer_orders",
       "SELECT * FROM customer_orders ORDER BY id ASC LIMIT 10",
       "SELECT * FROM customer_orders ORDER BY time_of_order ASC LIMIT 10",
       "SELECT * FROM customer_orders ORDER BY employee_id DESC LIMIT 10",
       "SELECT * FROM customer_order_items ORDER BY name ASC LIMIT 10",
       "SELECT AVG(price) FROM customer_order_items",
       "SELECT * FROM employee ORDER BY last_name ASC",
       "SELECT * FROM employee ORDER BY last_name DESC",
       "SELECT * FROM employee ORDER BY first_name ASC",
       "SELECT * FROM employee ORDER BY first_name DESC"};

       System.out.println("--------------------Query Results--------------------");
       for(int i = 0; i < queries.length; i++){ //loops through query array
        ResultSet result = stmt.executeQuery(queries[i]);
        ResultSetMetaData rsmd = result.getMetaData();

        System.out.println(queries[i]); //prints current query
        System.out.println("----------------------------------------------------");

        int columnsNumber = rsmd.getColumnCount();
        
        //formats and prints ResultSet of current query
        while (result.next()) {
          for (int j = 1; j <= columnsNumber; j++) {
           if (j > 1) System.out.print(",  ");
            String columnValue = result.getString(j);
            System.out.print(columnValue);
          }
          System.out.println("");
        }
        System.out.println();
       }

   } catch (Exception e){
       e.printStackTrace();
       System.err.println(e.getClass().getName()+": "+e.getMessage());
       System.exit(0);
   }

    //closing the connection
    try {
      conn.close();
      System.out.println("Connection Closed.");
    } catch(Exception e) {
      System.out.println("Connection NOT Closed.");
    }//end try catch
  }//end main
}//end Class
