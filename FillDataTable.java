import java.sql.*;
import java.util.Scanner;  
import java.io.File;

public class FillDataTable{
    public static final String user = "csce331_906_krueger";
    public static final String pswd = "730001845";

    //Determines if word has atleast 1 non-numeric char
    public static boolean isNotNumeric(String word) {
        try{
            Double.parseDouble(word);
        }catch(Exception e){
            return true;
        }
        return false;
    }

    //Tokenize manually
    public static String tokenize(String line){
        int i = 0;
        int j = 0; 
        String vals = "(";
    
        while(i < line.length()){
            j = line.indexOf(",", i);
            if (j == -1){
                j = line.length();
            }
      
            String token = line.substring(i,j);

            if (isNotNumeric(token)){
                vals += "'" + token +"'";
            }else{
                vals += token;
            }

            if (j != line.length()){
                vals+=", ";
            }else{
                vals+=");";
            }

            i = j;
            i++;
        }
        return vals;
    }

    public static void main(String args[]) {
        String table_name;
        String path_name;

        if (args.length == 0){
            table_name = "test";
            path_name = "cool.csv";
        }else{
            path_name = args[0];
            table_name = args[1];
        }
        System.out.println("Retrieving data from " + path_name + " and inserting into " + table_name + "...");


        //Building the connection with your credentials
        Connection conn = null;
        String teamNumber = "72"; 
        String sectionNumber = "906"; 
        String dbName = "csce331_" + sectionNumber + "_" + teamNumber;
        String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
   
        try{
            //Connecting to the database
            conn = DriverManager.getConnection(dbConnectionString, user, pswd);
            System.out.println("Opened database successfully");

            //parsing a CSV file into Scanner class constructor & set delimiter pattern
            Scanner sc = new Scanner(new File(path_name));  
            
            //create a statement object
            Statement stmt = conn.createStatement();
            
            //Running multiple queries
            while (sc.hasNext()){ 
                String line = sc.nextLine(); 
                
                String sqlStatement = "INSERT INTO " + table_name +" VALUES";
                sqlStatement += tokenize(line);

                //Send Statement to DBMS
                try{
                    stmt.executeQuery(sqlStatement);
                }catch(Exception e){
                    if (! e.getMessage().equals("No results were returned by the query.")){
                        e.printStackTrace();
                        System.err.println(e.getClass().getName()+": "+e.getMessage());
                        System.out.println("Tried Executing: "+ sqlStatement);
                        System.exit(1);
                    }
                }  
            }   
            sc.close(); 
        
        }catch(Exception e){
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(1);
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


