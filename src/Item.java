public class Item{
    private int id;
    private String name; 
    private double customer_price;
    private double restock_price;
    private double customer_amount; 
    private double restock_amount;
    private String order_unit;
    private int inventory;
    private String type;


    public Item(int _id, String _name, double _customer_price, double _restock_price, 
    double _customer_amount, double _restock_amount, String _order_unit, 
    int _inventory, String _type){
        id = _id;
        name = _name;
        customer_price = _customer_price;
        restock_price = _restock_price;
        customer_amount = _customer_amount;
        restock_amount = _restock_amount;
        order_unit = _order_unit;
        inventory = _inventory;
        type = _type;
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
    public int getInventory(){
        return inventory;
    }
    public String getType(){
        return type;
    }


    @Override
    public String toString(){
        String str = "id: " + getId() + ", name: " + getName() 
        + ", customer price: " + getCustomerPrice() + ", restock price: " + getRestockPrice()
        + ", customer amount: " + getCustomerAmount() + ", restock amount: " + getRestockAmount()
        + ", order unit: " + getOrderUnit() + ", inventory: " + inventory + ", type: " + getType();
        return str;
    }

}