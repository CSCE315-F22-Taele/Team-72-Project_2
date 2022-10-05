import java.sql.*;
import java.util.Scanner;  
import java.io.File;


class OrderHistory{

  public static String tokenize(String line){
    //Tokenize manually
    int i = 0;
    int j = 0; 
    int tokenNum = 0;

    String id = "";
    String date = "";
    String type = "";
    String main_topping = "";
    String additional_toppings = "";
    String sides = "";
    String drinks = "";
    String total = "";


    while(i < line.length()){
      j = line.indexOf(",", i);

      if (tokenNum == 7){
          j = line.length();
      }
      
      String token = line.substring(i,j);
      
      //csv file will be in the form id,date,total,type,side,drink,main_topping,additonal_toppings
      switch (tokenNum){
          case 0:
              id = token;
              break;
          case 1:
              date = ", '" + token + "'";
              break;
          case 2:
              total =", " + token;
              break;
          case 3:
              type = ", '" + token + "'";
              break;
          case 4: 
              sides = ", '" + token + "'";
              break;
          case 5: 
              drinks = ", '" + token + "'";
              break;
          case 6: 
              main_topping = ", '" + token + "'";
              break;
          case 7:
              additional_toppings = ", '" + token + "'";
              break;
      }
      tokenNum++;
      
      i = j;
      i++;

    }

    return "("+id + date + type + main_topping + additional_toppings + sides + drinks + total + ");";


  }

    public static void main(String args[]) {

    //Building the connection with your credentials
    Connection conn = null;
    String teamNumber = "72"; 
    String sectionNumber = "906"; 
    String dbName = "csce331_" + sectionNumber + "_" + teamNumber;
    String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
    dbSetup myCredentials = new dbSetup(); 

   
     try{
        //Connecting to the database
        conn = DriverManager.getConnection(dbConnectionString, dbSetup.user, dbSetup.pswd);
        System.out.println("Opened database successfully");


        //parsing a CSV file into Scanner class constructor & set delimiter pattern
        Scanner sc = new Scanner(new File("order.csv"));  
        sc.useDelimiter(",");  
        
        //create a statement object
        Statement stmt = conn.createStatement();

        String table_name = "orderhistory";
        String attributes = "(id, date, type, main_topping, additional_toppings, sides, drinks, total)";
        
        //Running a query
        
        while (sc.hasNext()){ 
          String line = sc.nextLine(); 

          String sqlStatement = "INSERT INTO "+ table_name + " " + attributes + " VALUES";
        

          
          System.out.println("Executing Query");
          sqlStatement += tokenize(line);
          //send statement to DBMS
          //This executeQuery command is useful for data retrieval
          try{
            stmt.executeQuery(sqlStatement);
          }catch(Exception e){
            //e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.out.println("Tried Executing: "+ sqlStatement);
          }
          
          
        }   
        sc.close(); 
        

    }catch (Exception e){
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

    }
  }
}


