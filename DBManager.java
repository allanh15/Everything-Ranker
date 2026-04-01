import java.sql.*;

public class DBManager {
    Connection c = null;
    Statement stmt = null;

    public DBManager(){
        try{
            //Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            stmt = c.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS USERS " + 
                            "(ID INT PRIMARY KEY    NOT NULL," +
                            " USERNAME       TEXT   NOT NULL," +
                            " PASSWORD       TEXT   NOT NULL," +
                            " EMAIL          TEXT)";
            stmt.executeUpdate(sql);
        } catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
    public int addUser(User user){
        try{
            stmt = c.createStatement();
            String sql = "INSERT INTO USERS (ID,USERNAME,PASSWORD,EMAIL) " +
                         "VALUES (" +
                         "'" + user.getID() + "',  " +
                         "'" + user.getUserName() + "',  " +
                         "'" + user.getPassword() + "',  " +
                         "'" + user.getEmail() + "');";
            stmt.executeUpdate(sql);
        } catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return 0;
        }
        return 1;
    }
    
    public User getUser(String userName){
        try{
            stmt = c.createStatement();
            //ResultSet rs = stmt.executeQuery("SELECT * FROM USERS WHERE USERNAME LIKE '" + userName + "%';");
            ResultSet rs = stmt.executeQuery("SELECT * FROM USERS WHERE USERNAME = '" + userName + "';");
            String id = rs.getString("ID");
            String retUserName = rs.getString("USERNAME");
            String password = rs.getString("PASSWORD");
            String email = rs.getString("EMAIL");
            rs.close();
            return new User(id,retUserName,password,email);
        }catch(Exception e){
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }
}
