import java.sql.*;
import java.util.Scanner;  
import java.io.File;


class JavaToDatabase{



  static boolean hasAlphabet(String word) {
    for (char c: word.toCharArray()){
      if (Character.isLetter(c)){
        return true;
      }
    }
    return false;
  }

    public static void main(String args[]) {

    //Building the connection with your credentials
    Connection conn = null;
    String teamNumber = "72"; 
    String sectionNumber = "906"; 
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

        //parsing a CSV file into Scanner class constructor & set delimiter pattern
        Scanner sc = new Scanner(new File("item.csv"));  
        sc.useDelimiter(",");  
        
        //create a statement object
        Statement stmt = conn.createStatement();
        
        //Running a query
        
        while (sc.hasNext()){ 
          String line = sc.nextLine(); 

          String sqlStatement = "INSERT INTO Item (id, name, customer_price, restock_price, customer_amount, restock_amount, order_unit, inventory, type) VALUES(";
        

          //Tokenize manually
          int i = 0;
          int j = 0; 

          while(i < line.length()){
            j = line.indexOf(",", i);

            if (j == -1){
              j = line.length();
            }
            String token = line.substring(i,j);
            //System.out.println("Token: " + token);
          
            if (i != 0){
              sqlStatement += ",";
            }
            i = j;
            i++;

            //if it has >= 1 alphabetical letters, put single commas around it
            if (hasAlphabet(token)){  
              sqlStatement += (" \'" + token + "\'");  
            }else{
              sqlStatement += (" " + token); 
            }
            


          }
          System.out.println("Executing Query");
          
          //send statement to DBMS
          //This executeQuery command is useful for data retrieval
          try{
            stmt.executeQuery(sqlStatement +");");
          }catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.out.println(sqlStatement +");");
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

