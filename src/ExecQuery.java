import java.sql.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Arrays;  
import java.util.Set;
import java.util.LinkedHashMap;


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

    }*/

    public boolean verifyManager(String user, String pass){
        String isManager = run("SELECT role FROM employee WHERE username = '"+user+"'");
        return verifyServer(user, pass) && isManager.equals("manager");
    }

    public boolean verifyServer(String user, String pass){
        String res = run("SELECT password FROM employee WHERE username = '"+user+"'");
        return res.equals(pass);
    }

    public Item getItem(String name){
        String res = run("SELECT * FROM item WHERE name = '"+name+"'");
        //tokenize

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

        Item item = new Item(Integer.parseInt(attrib[0]), attrib[1], Double.parseDouble(attrib[2]),
        Double.parseDouble(attrib[3]), Double.parseDouble(attrib[4]), Double.parseDouble(attrib[5]), attrib[6], Double.parseDouble(attrib[7]), attrib[8]);
        
        return item;
    }


    public void confirmRestockOrder(LinkedHashMap <Item, Integer> LHM, Employee employee){
        if(employee.getRole() == "manager"){
            Set<Item> keys = LHM.keySet();
            for(Item key : keys){
                run("UPDATE item SET inventory=inventory+" + LHM.get(key) + " where name='" + key.getName() + "'");
            }
        }
    }

    //Decrease Inventory of item name
    public void decrease(Item item) throws Exception{
        //edge case Chips and Queso

        double decrementAmt = Double.parseDouble(getItemColumn(item.getName(), "customer_amount"));
        double currAmt = Double.parseDouble(getItemColumn(item.getName(), "inventory"));

        currAmt -= decrementAmt;

        run("UPDATE item SET inventory = "+ currAmt + " WHERE name = '" + item.getName() + "'");

        if (item.getName().length() >= 5 && item.getName().substring(0,5).equals("Chips")){

            decrementAmt = Double.parseDouble(getItemColumn("Tortilla Chips", "customer_amount"));
            currAmt = Double.parseDouble(getItemColumn(item.getName(), "inventory"));

            run("UPDATE item SET inventory = "+ currAmt + " WHERE name = 'Tortilla Chips'");

            String sauce = item.getName().substring(10);
            if (sauce.equals("Guac")){
                sauce = "Guacamole";
            }
            decrementAmt = Double.parseDouble(getItemColumn(sauce, "customer_amount"));
            currAmt = Double.parseDouble(getItemColumn(sauce, "inventory"));

            run("UPDATE item SET inventory = "+ currAmt + " WHERE name = '" + sauce +"'");
        }

    }


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


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
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
        
        System.out.println("Protein: "+ proteinID + " Type: "+ containerID);
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

            //System.out.println(attrib[0] + " :id, " + attrib[1] + " : name");
        
            //String type = run("SELECT type FROM item OFFSET "+i+" LIMIT 1");
            //String price_str = run("SELECT customer_price FROM item OFFSET "+i+" LIMIT 1");
            Item item = new Item(Integer.parseInt(attrib[0]), attrib[1], Double.parseDouble(attrib[2]),
            Double.parseDouble(attrib[3]), Double.parseDouble(attrib[4]), Double.parseDouble(attrib[5]), attrib[6], Double.parseDouble(attrib[7]), attrib[8]);
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

    public static void main(String[] args) throws Exception{
        ExecQuery ex = new ExecQuery("krueger", "730001845");
      
        Item test[] = ex.getItems();

        Employee conrad = new Employee(2,"Conrad", "Krueger", "CKrueg", "730001845", "manager");

        ArrayList<Item> items = new ArrayList<>();
        for (Item i : test)
            items.add(i);

        ex.confirmCustomerOrder(items, conrad);


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