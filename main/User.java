import java.util.ArrayList;
import java.util.List;

public class User{
    private String userID,      //should be 8-digit string of numbers. must be unique
    userName,                   //must be unique
    password,
    email;                      //must be unique
    private List rankings;

    //All variables except rankings must be defined in order to create a user
    public User(String userID, String userName, String password, String email){
        this.userID = userID;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.rankings = new ArrayList();
    }
    public void setID(String userID){
        this.userID = userID;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setEmail(String email){
        this.email = email;
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
    public List getRankings(){
        return rankings;
    }
    public boolean validatePassword(String password){
        if(this.password.equals(password)) return true;
        return false;
    }
    public boolean verify(String password){
        return this.password.equals(password);
    }
    @Override
    public String toString(){
        return "User{userID ='" + userID + "', userName='" + userName + "', password='" + password + "', email='" + email + "}";
    }
}
