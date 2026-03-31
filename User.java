import java.util.List;

public class User{
    private String userID,
    userName,
    password,
    email;
    private EverythingList rankings;
    
    //All variables except rankings must be defined in order to create a user
    public User(String userID, String userName, String password, String email){
        userID = this.userID;
        userName = this.userName;
        password = this.password;
        email = this.email;
    }
    public void setID(String userID){
        userID = this.userID;
    }
    public void setUserName(String userName){
        userName = this.userName;
    }
    public void setPassword(String password){
        password = this.password;
    }
    public void setEmail(String email){
        email = this.email;
    }
    public String getID(){
        return userID;
    }
    public String getUserName(){
        return userName;
    }
    public String getPassword(){
        return password;
    }
    public String getEmail(){
        return email;
    }
}

