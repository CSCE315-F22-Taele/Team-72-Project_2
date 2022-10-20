/**
 * Class that contains information about a single Item
 * @author Jake Bass
 */
public class Item{
    private int id;
    private String name; 
    private double customer_price;
    private double restock_price;
    private double customer_amount; 
    private double restock_amount;
    private String order_unit;
    private double inventory;
    private String type;
    private double min_amount;


    /**
     * Item Constructor
     * @param _id integer of item ID number
     * @param _name string of item name
     * @param _customer_price double of item price to customer
     * @param _restock_price double of item price to restock
     * @param _customer_amount double of how much quantity of the item is given to a customer per customer order
     * @param _restock_amount double of how much quantity of the item can be reordered at once
     * @param _order_unit String of the quantity unit
     * @param _inventory double current quantity of the item
     * @param _type String of a category the item falls into
     * @param _min_amount double of the minimum amount of quantity the restuarant should have of this item at any time
     */
    public Item(int _id, String _name, double _customer_price, double _restock_price, 
    double _customer_amount, double _restock_amount, String _order_unit, 
    double _inventory, String _type, double _min_amount){
        id = _id;
        name = _name;
        customer_price = _customer_price;
        restock_price = _restock_price;
        customer_amount = _customer_amount;
        restock_amount = _restock_amount;
        order_unit = _order_unit;
        inventory = _inventory;
        type = _type;
        min_amount = _min_amount;
    }

    /**
     * Get Item's ID number
     * @return integer of ID
     */
    public int getId(){
        return id;
    }
    /**
     * Get Item's name
     * @return String of name
     */
    public String getName(){
        return name;
    }

    /**
     * Get Item's customer price
     * @return double of price
     */
    public double getCustomerPrice(){
        return customer_price;
    }

    /**
     * Get Item's restock price
     * @return double of price
     */
    public double getRestockPrice(){
        return restock_price;
    }

    /**
     * Get Item's customer amount per order
     * @return double of customer amount
     */
    public double getCustomerAmount(){
        return customer_amount;
    }

    /**
     * Get Item's restock amount
     * @return double of restock amount
     */
    public double getRestockAmount(){
        return restock_amount;
    }

    /**
     * Get Item's order unit
     * @return String of order unit
     */
    public String getOrderUnit(){
        return order_unit;
    }

    /**
     * Get Item's current inventory amount
     * @return double of inventory
     */
    public double getInventory(){
        return inventory;
    }

    /**
     * Get Item's type
     * @return String of type
     */
    public String getType(){
        return type;
    }

    /**
     * Get Item's minimum amount
     * @return double of minimum amount
     */
    public double getMinAmount(){
        return min_amount;
    }

    /**
     * Set current Item's ID to a different ID
     * @param _id integer of new ID
     */
    public void setID(int _id){
        id = _id;
    }

    /**
     * Set current Item's inventory to a different amount
     * @param amount double of the new inventory amount
     */
    public void setInventory(double amount){
        inventory = amount;
    }

    /**
     * Set current Item's customer price to a different amount
     * @param amount double of the new customer price
     */
    public void setCustomerPrice(double amount){
        customer_price = amount;
    } 

    /**
     * Relies on Item's unique ID for any hashing usage
     */
    @Override
    public int hashCode(){
        return id;
    }

    /**
     * Overidden equals operator for Item class 
     * @return boolean that represents if item objects are equal
     */
    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
        }

        if (!(o instanceof Item)){
            return false;
        }
        Item i = (Item) o;

        return (i.getId() == getId());
    }

    /**
     * creates string of all attributes in item 
     * @return String of all attributes of item
     */

    @Override
    public String toString(){
        String str = "id: " + getId() + ", name: " + getName() 
        + ", customer price: " + getCustomerPrice() + ", restock price: " + getRestockPrice()
        + ", customer amount: " + getCustomerAmount() + ", restock amount: " + getRestockAmount()
        + ", order unit: " + getOrderUnit() + ", inventory: " + getInventory() + ", type: " + getType() + ", min amount: " + getMinAmount();
        return str;
    }

}