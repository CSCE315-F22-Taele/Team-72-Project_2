import java.util.Date;
import java.text.SimpleDateFormat;  

public class CustomerOrder{
    private long id;
    private double price;
    private Date time_of_order;
    private int employee_id;

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

    public long getId(){
        return id;
    }
    public double getPrice(){
        return price;
    }
    public Date getTimeOfOrder(){
        return time_of_order;
    }
    public String getTimeOfOrderStr(){
        return time_of_order.toString();
    }
    public int getEmployeeId(){
        return employee_id;
    }

    /**
     * creates string of all attributes in customerOrder
     * @return String of all attributes of customerOrder
     */
    @Override
    public String toString(){
        String res = "id = "+id+", price = "+price+", time of order = "+time_of_order+", employee id = "+employee_id;
        return res;
    }
}