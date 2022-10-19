public class Employee{
    private int id;
    private String first_name; 
    private String last_name;
    private String username;
    private String password; 
    private String role;


    public Employee(int _id, String _first, String _last, String _user, 
    String _pass, String _role){
        id = _id;
        first_name = _first;
        last_name = _last;
        username = _user;
        password = _pass;
        role = _role;
    }

    public int getId(){
        return id;
    }
    public String getUser(){
        return username;
    }
    public String getPass(){
        return password;
    }
    public String getFirst(){
        return first_name;
    }
    public String getLast(){
        return last_name;
    }
    public String getRole(){
        return role;
    }

    /**
     * creates string of all attributes in employee 
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