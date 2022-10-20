import java.util.Date;
import java.text.SimpleDateFormat;  

/**
 * Class that contains information about a single Customer Order
 * @author Jake Bass
 */
public class CustomerOrder{
    private long id;
    private double price;
    private Date time_of_order;
    private int employee_id;

    /**
     * CustomerOrder Constructor
     * @param _id long of customer order's ID
     * @param _price double of customer order's price
     * @param _time_of_order String of the date and time the customer order was made
     * @param _employee_id integer of employee ID who created the order
     */
    public CustomerOrder(long _id, double _price, String _time_of_order, int _employee_id) {
        id = _id;
        price = _price;
        try{
            time_of_order = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(_time_of_order);  
        }catch(Exception e){
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.out.println("Failed to create Date with: "+ _time_of_order);
        }
        employee_id = _employee_id;
    }

    /**
     * Get Customer Order's ID number
     * @return long of id number
     */
    public long getId(){
        return id;
    }

    /**
     * Get Customer Order's price
     * @return double of the price
     */
    public double getPrice(){
        return price;
    }

    /**
     * Get Customer Order's Date of placement
     * @return Date obj of time the customer order was created
     */
    public Date getTimeOfOrder(){
        return time_of_order;
    }

    /**
     * Get Customer Order's Date of placement as a string
     * @return String of time the customer order was created
     */
    public String getTimeOfOrderStr(){
        return time_of_order.toString();
    }

    /**
     * Get Employee ID of employee who placed the Customer Order
     * @return integer of employee ID
     */
    public int getEmployeeId(){
        return employee_id;
    }

    /**
     * Creates string of all attributes in customerOrder
     * @return string of all attributes of customerOrder
     */
    @Override
    public String toString(){
        String res = "id = "+id+", price = "+price+", time of order = "+time_of_order+", employee id = "+employee_id;
        return res;
    }
}