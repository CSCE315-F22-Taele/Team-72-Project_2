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

    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public double getCustomerPrice(){
        return customer_price;
    }
    public double getRestockPrice(){
        return restock_price;
    }
    public double getCustomerAmount(){
        return customer_amount;
    }
    public double getRestockAmount(){
        return restock_amount;
    }
    public String getOrderUnit(){
        return order_unit;
    }
    public double getInventory(){
        return inventory;
    }
    public String getType(){
        return type;
    }
    public String getMinAmount(){
        return min_amount;
    }
    public void setID(int _id){
        id = _id;
    }
    public void setInventory(double amount){
        inventory = amount;
    }
    public void setCustomerPrice(double amount){
        customer_price = amount;
    } 

    @Override
    public int hashCode(){
        return id;
    }
    
    @Override
    public String toString(){
        String str = "id: " + getId() + ", name: " + getName() 
        + ", customer price: " + getCustomerPrice() + ", restock price: " + getRestockPrice()
        + ", customer amount: " + getCustomerAmount() + ", restock amount: " + getRestockAmount()
        + ", order unit: " + getOrderUnit() + ", inventory: " + getInventory() + ", type: " + getType() + ", min amount: " + getMinAmount();
        return str;
    }

    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
        }
        if (! (o instanceof Item)){
            return false;
        }

        Item i = (Item) o;
        return (i.getId() == getId());
    }

}