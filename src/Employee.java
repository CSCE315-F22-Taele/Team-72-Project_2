/**
 * Class that contains information about a single Employee
 * @author Jake Bass
 */
public class Employee{
    private int id;
    private String first_name; 
    private String last_name;
    private String username;
    private String password; 
    private String role;

    /**
     * Employee Constructor
     * @param _id integer of employee ID
     * @param _first string of first name of an employee
     * @param _last string of last name of an employee
     * @param _user string of username of an employee
     * @param _pass string of password of an employee
     * @param _role string of role of an employee
     */
    public Employee(int _id, String _first, String _last, String _user, 
    String _pass, String _role){
        id = _id;
        first_name = _first;
        last_name = _last;
        username = _user;
        password = _pass;
        role = _role;
    }

    /**
     * Get employee's ID number
     * @return integer of ID
     */
    public int getId(){
        return id;
    }

    /**
     * Get employee's login username
     * @return String of username
     */
    public String getUser(){
        return username;
    }

    /**
     * Get employee's login password
     * @return String of password
     */
    public String getPass(){
        return password;
    }

    /**
     * Get employee's first name
     * @return String of first name
     */
    public String getFirst(){
        return first_name;
    }

    /**
     * Get employee's last name
     * @return String of last name
     */
    public String getLast(){
        return last_name;
    }

    /**
     * Get employee's first name
     * @return String of last name
     */
    public String getRole(){
        return role;
    }

    /**
     * Creates string of all attributes in employee 
     * @return String of all attributes of employee
     */
    @Override
    public String toString(){
        String str = "id: " + getId() + ", First Name: " + getFirst() 
        + ", Last Name: " + getLast() + ", Username: " + getUser()
        + ", Password: " + getPass() + ", Role: " + getRole();
        return str;
    }

}