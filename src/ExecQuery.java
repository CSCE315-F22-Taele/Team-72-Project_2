import java.sql.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.HashMap;

/** 
 * ExecQuery Class that executes sql queries on a database
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

    /**
     * Executes an sql query
     * @param query string containing the sql query
     * @return ArrayList containing an array of strings representing the results from query, empty string if no results outputted
     */
    public ArrayList<String[]> runArrayList(String query){
        try{
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);
            ResultSetMetaData rsmd = result.getMetaData();

            int columnsNumber = rsmd.getColumnCount();

            ArrayList<String[]> data = new ArrayList<>();


            while (result.next()) {
                String[] res = new String[columnsNumber];
                for (int i = 1; i <= columnsNumber; i++) {
                    res[i-1] = result.getString(i);      
                }
                data.add(res);
            }
            
            return data;

        }catch(Exception e){
            if (! e.getMessage().equals("No results were returned by the query.")){
                //e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                System.out.println("Tried Executing: "+ query);
                close();
            }
            return null;
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

    /**
     * Verifies that the current user is a manager
     * @param user string that holds the user's username
     * @param pass string that holds the user's password
     * @return boolean that is true if user is manager, else false if not
     */
    public boolean verifyManager(String user, String pass){
        
        String isManager = run("SELECT role FROM employee WHERE username = '"+user+"'");
        return verifyServer(user, pass) && isManager.equals("manager");
    }

    /**
     * Verifies that the current user is a server
     * @param user string that holds the user's username
     * @param pass string that holds the user's password
     * @return boolean that is true if user is server, else false if not
     */
    public boolean verifyServer(String user, String pass){
        String res = run("SELECT password FROM employee WHERE username = '"+user+"'");
        return res.equals(pass);
    }

    /**
     * Changes price of specified item
     * @param item current item whoms price is being altered
     * @param price double that holds new price of item
     */
    public void changeItemPrice(Item item, double price){
        run("UPDATE item SET customer_price = "+ price + " WHERE name = '" + item.getName() +"'");
        item.setCustomerPrice(price);
    }

    /**
     * Add new item entry into item table in DB
     * @param item new item to be created
     */
    public void addItem(Item item){
        int co_id = Integer.parseInt(run("SELECT COUNT(id) FROM item"))+1;
        item.setID(co_id);
        run("INSERT INTO item(id, name, customer_price, restock_price, customer_amount, restock_amount, order_unit, inventory, type, min_amount) VALUES("
        +item.getId()+", '"+item.getName()+"', "+item.getCustomerPrice()+", "+item.getRestockPrice()+", "+item.getCustomerAmount()+
        ", "+item.getRestockAmount()+", '"+item.getOrderUnit()+"', "+item.getInventory()+", '"+item.getType()+", " + item.getMinAmount()+"')");       
    }

    /**
     * Gets selected item from item table by specified name
     * @param name string of item's name to be grabbed
     * @return new the item specified by its name
     */
    public Item getItem(String name){
        String res = run("SELECT * FROM item WHERE name = '"+name+"'");
        //tokenize

        String[] attrib = new String[10];
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

        Item item = new Item(Integer.parseInt(attrib[0]), attrib[1], Double.parseDouble(attrib[2]),
        Double.parseDouble(attrib[3]), Double.parseDouble(attrib[4]), Double.parseDouble(attrib[5]), attrib[6], 
        Double.parseDouble(attrib[7]), attrib[8], Double.parseDouble(attrib[9]));
        
        return item;
    }

    /**
     * Updates inventory amount when a restock order is placed
     * @param LHM linkedhashmap that contains specified item and how many units of it to restock
     * @param employee current user, used to verify if employee is manager
     */
    public void confirmRestockOrder(LinkedHashMap <Item, Integer> LHM, Employee employee){
        if(employee.getRole() == "manager"){
            Set<Item> keys = LHM.keySet();
            for(Item key : keys){
                double oldInv = key.getInventory();
                run("UPDATE item SET inventory=inventory+" + LHM.get(key) + " where name='" + key.getName() + "'");
                key.setInventory(oldInv+LHM.get(key));
            }
        }
    }

    /**
     * Decrease inventory of specified item by 1 unit
     * @param item item to have its inventory decreased
     */
    public void decrease(Item item) throws Exception{
        //edge case Chips and Queso

        double decrementAmt = Double.parseDouble(getItemColumn(item.getName(), "customer_amount"));
        double currAmt = Double.parseDouble(getItemColumn(item.getName(), "inventory"));

        currAmt -= decrementAmt;

        run("UPDATE item SET inventory = "+ currAmt + " WHERE name = '" + item.getName() + "'");
        item.setInventory(currAmt);

        if (item.getName().length() >= 5 && item.getName().substring(0,5).equals("Chips")){

            decrementAmt = Double.parseDouble(getItemColumn("Tortilla Chips", "customer_amount"));
            currAmt = Double.parseDouble(getItemColumn(item.getName(), "inventory"));

            run("UPDATE item SET inventory = "+ currAmt + " WHERE name = 'Tortilla Chips'");
            item.setInventory(currAmt);


            String sauce = item.getName().substring(10);
            if (sauce.equals("Guac")){
                sauce = "Guacamole";
            }
            decrementAmt = Double.parseDouble(getItemColumn(sauce, "customer_amount"));
            currAmt = Double.parseDouble(getItemColumn(sauce, "inventory"));

            run("UPDATE item SET inventory = "+ currAmt + " WHERE name = '" + sauce +"'");
            item.setInventory(currAmt);
        }

    }

    /**
     * Creates a Customer Order in the Database.
     * @param items ArrayList of Items to be added to a Customer Order
     * @param employee Employee onject who created the Customer Order
     */
    public void confirmCustomerOrder(ArrayList<Item> items, Employee employee){
        double price = 0;
        int co_id = Integer.parseInt(run("SELECT COUNT(id) FROM customer_orders"))+1;
        int coi_id = Integer.parseInt(run("SELECT COUNT(id) FROM customer_order_items"))+1;

        int original_coi_id = coi_id;

        String mainTop = "";
        String mainContain= "";
        double mainPrice = 0;

        int proteinID = 0;
        int containerID = 0;


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        Date date = new Date();  
        String time_of_order = formatter.format(date).toString();
        

        run("INSERT INTO customer_order_items(id, name, price) VALUES ("+original_coi_id +", null, 0)");
        run("INSERT INTO customer_orders(id, price, time_of_order, employee_id) VALUES ("+co_id+", " + price + ", '" + time_of_order + "', " + employee.getId() + ")");


        for (Item i: items){
            price += i.getCustomerPrice();

        
            if (i.getType().equals("Protein")){
                mainTop = i.getName();
                mainPrice = i.getCustomerPrice();
                proteinID = i.getId();
                
                
            }
            else if (i.getType().equals("Entree Base")){
                mainContain = i.getName();
                containerID = i.getId();
            }

            else if (i.getType().equals("Drinks")){
                coi_id++;
                run("INSERT INTO customer_order_items(id, name, price) VALUES ("+coi_id+", '" + i.getName() + "', " + i.getCustomerPrice()+")");
                run("INSERT INTO co_to_coi(co_id, coi_id) VALUES ("+co_id+", " + coi_id +")");
                run("INSERT INTO coi_to_i(coi_id, i_id) VALUES ("+coi_id+", " + i.getId() +")");
            }

            else if (i.getType().equals("Sides")){
                coi_id++;
                run("INSERT INTO customer_order_items(id, name, price) VALUES ("+coi_id+", '" + i.getName() + "', " + i.getCustomerPrice()+")");
                run("INSERT INTO co_to_coi(co_id, coi_id) VALUES ("+co_id+", " + coi_id +")");
                run("INSERT INTO coi_to_i(coi_id, i_id) VALUES ("+coi_id+", " + i.getId() +")");
                
            }else{
                run("INSERT INTO coi_to_i(coi_id, i_id) VALUES ("+original_coi_id+", " + i.getId() +")");
            }
            
            try{
                decrease(i);
            }catch(Exception e){
                System.err.println(e.getClass().getName()+": "+e.getMessage());
            }
            

        }

        //Toppings

        run("UPDATE customer_order_items SET name = '"+ mainTop + " " + mainContain + "' WHERE id = '" + original_coi_id+"'");
        run("UPDATE customer_order_items SET price = "+ mainPrice + " WHERE id = '" + original_coi_id+"'");
        run("UPDATE customer_orders SET price = "+ price + " WHERE id = '" + co_id+"'");

        run("INSERT INTO co_to_coi(co_id, coi_id) VALUES ("+co_id+", " + original_coi_id +")");
 
        run("INSERT INTO coi_to_i(coi_id, i_id) VALUES ("+original_coi_id+", " + proteinID +")");
        run("INSERT INTO coi_to_i(coi_id, i_id) VALUES ("+original_coi_id+", " + containerID +")");
        

    }
    

    /** Gets name, type, and customer price of every item in the Item datatable
     * @return array of Items
     */
    public Item[] getItems(){
        int i = 0;
        int row_amt = Integer.parseInt(run("SELECT COUNT(name) FROM item"));
        Item items[] = new Item[row_amt];

        while (i < row_amt){
            String res = run("SELECT id, name, customer_price, restock_price, customer_amount, restock_amount, order_unit, inventory, type, min_amount FROM item OFFSET "+i+" LIMIT 1");


            //Tokenize
            String[] attrib = new String[10];
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


            Item item = new Item(Integer.parseInt(attrib[0]), attrib[1], Double.parseDouble(attrib[2]),
            Double.parseDouble(attrib[3]), Double.parseDouble(attrib[4]), Double.parseDouble(attrib[5]), 
            attrib[6], Double.parseDouble(attrib[7]), attrib[8], Double.parseDouble(attrib[9]));
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
     * @return  string of resulting query
     * @throws Exception
     */
    public String getItemColumn(String ingredient, String column) throws Exception{
        if (column.equals("name")){
            return ingredient;
        }
        String res = run("SELECT "+column+" FROM item WHERE name = '" + ingredient + "'");
        if(res==""){

             throw new Exception("Item/Column Not Found ("+ingredient + ", " + column+")");
        }
        return res;
     }




    //Reports
    /**
     * Retrieves Customer Orders for each Item between a start date and an end date
     * @param start string of the start date in the format "yyyy-MM-dd HH:mm:ss"
     * @param end string of the end date in the format "yyyy-MM-dd HH:mm:ss"
     * @return HashMap with Item's as keys and an ArrayList of CustomerOrders that contain the Item Key
     */
    HashMap<Item, ArrayList<CustomerOrder>> getSalesReport(String start, String end){
        HashMap<Item, ArrayList<CustomerOrder>> report = new HashMap<>();
        
        //ArrayList<String[]> res = runArrayList("select * from customer_orders WHERE time_of_order BETWEEN '"+start+"' AND '"+end+"'");
        Item[] items = getItems();

        //Populate hashmap
        for (Item i: items){

            ArrayList<String[]> res = runArrayList("select * from customer_orders where id in (select co_id from co_to_coi where coi_id in (select coi_id from coi_to_i where i_id = "+i.getId()+")) and time_of_order BETWEEN '"+start+"' AND '"+end+"'");
            ArrayList<CustomerOrder> cos = new ArrayList<>();
            for (String[] attrib: res){
                cos.add(new CustomerOrder(Long.parseLong(attrib[0]), Double.parseDouble(attrib[1]), attrib[2], Integer.parseInt(attrib[3])));
            }

            report.put(i, cos);
        }
        /* 
        for (String[] arr: res){
            CustomerOrder co = new CustomerOrder(Long.parseLong(arr[0]), Double.parseDouble(arr[1]), arr[2], Integer.parseInt(arr[3]));

            ArrayList<String[]> itemNames = runArrayList("SELECT name FROM item WHERE id IN (SELECT i_id from coi_to_i WHERE coi_id IN (SELECT coi_id FROM co_to_coi WHERE co_id="+co.getId()+"))");
        
            for (String[] name: itemNames){
                Item i = getItem(name[0]);
                
                ArrayList<CustomerOrder> single_report = report.get(i);
                single_report.add(co);
                report.put(i, single_report);
            }
        }*/
        
        return report;
    }

 
    /**
     * Retrieves Items between a start date and now who's inventory decreased by less than 10%
     * @param start string of the start date in the format "yyyy-MM-dd HH:mm:ss"
     * @return ArrayList of a pair of items and their old inventories to be compared with the current inventory
     */
    ArrayList<Pair<Item,Double>> getExcessReport(String start){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        Date date = new Date();  
        String end = formatter.format(date).toString();

        HashMap<Item, ArrayList<CustomerOrder>> sales = getSalesReport(start, end);

        ArrayList<Pair<Item,Double>> report = new ArrayList<>();

        for (Item i: sales.keySet()){
            int numOfSales = sales.get(i).size();
            double inventory_now = i.getInventory();
            double customer_amount = i.getCustomerAmount();

            Double inventory_before = numOfSales*customer_amount + inventory_now;

            //calc if it has less than 10 percent

            if (inventory_before * 0.90 < inventory_now){
                report.add(new Pair<Item, Double>(i, inventory_before));
            }
        }

        return report;
    }

    /**
     * Retrieves Items who's inventory is less than it's specified minimum amount  
     * @return ArrayList of Items that have too low of inventory.
     */
    ArrayList<Item> getRestockReport(){

        ArrayList<String[]> res = runArrayList("SELECT name, inventory FROM item");

        ArrayList<Item> report = new ArrayList<>();

        for (String[] arr: res){
            double inventory = Double.parseDouble(arr[1]);
            Item i = getItem(arr[0]);

            if (inventory < i.getMinAmount()){
                report.add(i);
            }
        }
        return report;
    }



    public static void main(String[] args) throws Exception{
        ExecQuery ex = new ExecQuery("krueger", "730001845");
        
        HashMap<Item, ArrayList<CustomerOrder>> hm = ex.getSalesReport("2022-10-1", "2022-10-16");

        for (Item i: hm.keySet()){
            ArrayList<CustomerOrder> arr = hm.get(i);
            
            System.out.println(i.getName()+":");
            for (CustomerOrder co: arr){
                System.out.println(co);
            }
            System.out.println();
        }
        
        ex.close();
    }


}
