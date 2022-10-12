import java.sql.*;

/** ExecQuery Class that executes sql queries on a database
 * @author Conrad Krueger
 */
public class ExecQuery{

    private Connection conn;


    /** Constructor to establish connection with database
     * @param lastname string that accepts last name for login
     * @param uin string that contains users UIN
     */
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

    /**
     * Executes an sql query
     * @param query string containing the sql query
     * @return string containing results from query, empty string if no results outputted
     */
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

    /** Closes the connection with the database
     */
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

    /** Gets name, type, and customer price of every item in the Item datatable
     * @return array of Items
     */
    public Item[] getItems(){
        int i = 0;
        int row_amt = Integer.parseInt(run("SELECT COUNT(name) FROM item"));
        Item items[] = new Item[row_amt];

        while (i < row_amt){
            String res = run("SELECT id, name, customer_price, restock_price, customer_amount, restock_amount, order_unit, inventory, type FROM item OFFSET "+i+" LIMIT 1");


            //Tokenize
            String[] attrib = new String[9];
            int k = 0;
            int prev = 0;
            for( int curr = 0; curr < res.length(); curr++){
                if (Character.compare(res.charAt(curr),'|') == 0){
                    attrib[k] = res.substring(prev, curr);
                    k++;
                    prev = curr+1;
                }
            }
            attrib[k] = res.substring(prev,res.length());

        
            //String type = run("SELECT type FROM item OFFSET "+i+" LIMIT 1");
            //String price_str = run("SELECT customer_price FROM item OFFSET "+i+" LIMIT 1");
            Item item = new Item(Integer.parseInt(attrib[0]), attrib[1], Double.parseDouble(attrib[2]),
            Double.parseDouble(attrib[3]), Double.parseDouble(attrib[4]), Double.parseDouble(attrib[5]), attrib[6], Integer.parseInt(attrib[7]), attrib[8]);
            items[i] = item;

            i++;
        }

        return items;
    }

    /**
     * Returns the price of an Item
     * @param ingredient string of item name
     * @return  price of item as a double
     * @throws Exception
     */
    public double getItemPrice(String ingredient) throws Exception{
       String price = run("SELECT customer_price FROM item WHERE name = '" + ingredient + "'");
       if(price==""){
            throw new Exception("Item Not Found");
       }
       double res = Double.parseDouble(price);
       return res;
    }

    /**
     * Returns the attribute of a particular item
     * @param ingredient string of item name
     * @param column string of the column name in the datatable
     * @return  price of item as a double
     * @throws Exception
     */
    public String getItemColumn(String ingredient, String column) throws Exception{
        if (column.equals("name")){
            return ingredient;
        }
        String res = run("SELECT customer_price FROM "+ column +" WHERE name = '" + ingredient + "'");
        if(res==""){
             throw new Exception("Item/Column Not Found");
        }
        return res;
     }

    public static void main(String[] args) throws Exception{
        ExecQuery ex = new ExecQuery("krueger", "730001845");
      
        Item test[] = ex.getItems();

        for (Item i: test){
            System.out.println(i);
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