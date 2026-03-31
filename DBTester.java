import java.sql.*;
public class DBTester {
    public static void main(String args[]){
        DBManager test = new DBManager();
        test.addUser(new User("12345678", "LebronLover25", "lebronjames67!", "Lebron_James@yahoo.com"));
    }
}
