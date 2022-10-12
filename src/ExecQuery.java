import java.sql.*;

public class ExecQuery{

    private Connection conn;

    public ExecQuery(String lastname, String uin){
        conn = null;
        String dbName = "csce331_906_72";
        String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;

        try{
            conn = DriverManager.getConnection(dbConnectionString, "csce331_906_"+lastname, uin);
        }catch(Exception e){
            //e.printStackTrace();
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
                        res += "|";
                    }
                    res += result.getString(i);    
                    //System.out.println("Here: "+(String[]) (a.getArray()));    
                }

            }
            
            return res;

        }catch(Exception e){
            if (! e.getMessage().equals("No results were returned by the query.")){
                //e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                System.out.println("Tried Executing: "+ query);
                close();
            }
            return "";
        }  
    }


    public void close(){
        try {
            conn.close();
            System.out.println("Connection Closed.");
        } catch(Exception e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.out.println("Connection NOT Closed.");
        }
    }


    /*public double add(String ingredient){

    }

    public boolean verifyManager(String user, String pass){

    }

    public boolean verifyServer(String user, String pass){

    }
    */

    public Item[] getItems(){
        int i = 0;
        int row_amt = Integer.parseInt(run("SELECT COUNT(name) FROM item"));
        Item items[] = new Item[row_amt];

        while (i < row_amt){
            String res = run("SELECT name, type, customer_price FROM item OFFSET "+i+" LIMIT 1");


            //Tokenize
            String[] attributes = new String[3];
            int k = 0;
            int prev = 0;
            for( int curr = 0; curr < res.length(); curr++){
                if (Character.compare(res.charAt(curr),'|') == 0){
                    attributes[k] = res.substring(prev, curr);
                    k++;
                    prev = curr+1;
                }
            }
            attributes[k] = res.substring(prev,res.length());

            

            //String type = run("SELECT type FROM item OFFSET "+i+" LIMIT 1");
            //String price_str = run("SELECT customer_price FROM item OFFSET "+i+" LIMIT 1");
            Item item = new Item(attributes[0], attributes[1], Double.parseDouble(attributes[2]));
            items[i] = item;

            i++;
        }

        return items;
    }

    public double getItemPrice(String ingredient) throws Exception{
       String price = run("SELECT customer_price FROM item WHERE name = '" + ingredient + "'");
       if(price==""){
            throw new Exception("Item Not Found");
       }
       double res = Double.parseDouble(price);
       return res;
    }

    public static void main(String[] args) throws Exception{


        ExecQuery ex = new ExecQuery("krueger", "730001845");
        
        Item test[] = ex.getItems();


        for (Item i: test){
            System.out.println(i.getName() + " " + i.getType() + " " + i.getPrice());
        }

        /* 
        double uno = ex.getItemPrice("Steak");
        double dos = ex.getItemPrice("Ground Beef");
        double tres = ex.getItemPrice("Bowls");
        try{
            double cuatro = ex.getItemPrice("spaghetti");
        }catch (Exception e){
            System.out.println("No spaghetti...");
        }
      
        System.out.println((uno/2)*2 + " " + (dos/2)*2 + " " + (tres/2)*2);
        */

        ex.close();
    }


}