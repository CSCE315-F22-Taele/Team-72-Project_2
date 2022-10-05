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
       //create a statement object
       Statement stmt = conn.createStatement();

       String[] queries = {"SELECT AVG(inventory) FROM item",
       "SELECT AVG(restock_amount) FROM item",
       "SELECT * FROM item WHERE inventory<=50",
       "SELECT * FROM item WHERE inventory>=150",
       "SELECT * FROM item ORDER BY type ASC",
       "SELECT AVG(price) FROM customer_orders",
       "SELECT * FROM customer_orders ORDER BY id ASC",
       "SELECT * FROM customer_orders ORDER BY time_of_order ASC",
       "SELECT  FROM customer_orders ORDER BY employee_id DESC",
       "SELECT * FROM customer_order_items ORDER BY name ASC",
       "SELECT AVG(price) FROM customer_order_items",
       "SELECT * FROM employee ORDER BY last_name ASC",
       "SELECT * FROM employee ORDER BY last_name DESC",
       "SELECT * FROM employee ORDER BY first_name ASC",
       "SELECT * FROM employee ORDER BY first_name DESC"};

       System.out.println("--------------------Query Results--------------------");
       for(int i = 0; i < queries.length; i++){
        ResultSet result = stmt.executeQuery(queries[i]);
        while (result.next()) {
          System.out.println(result.getString(""));
        }
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
