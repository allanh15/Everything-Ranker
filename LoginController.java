public class LoginController {
    User user;
    public static final int MIN_LENGTH_USERNAME = 8;
    public static final int MAX_LENGTH_USERNAME = 16;

    public static final int MIN_PASSWORD_LENGTH = 12;
    public static final int MAX_PASSWORD_LENGTH = 20;
    
     /*
     Username rules: must be between 8 and 16 characters containing letters(upper or lower) and numbers. cannot contain special characters or spaces
     Password rules: password must be at 12-20 characters containting letters, numbers and at least one special character
     */

    // successful -> "Account created"
    // userName invalid length -> "Username must be between 8 and 16 characters"
    // userName contains special character -> "Username can only contain letters and numbers"
    // password invalid length -> "Password must be between 12 and 20 characters"
    // password doesnt contain letter -> "Password must contain at least one letter one number and one special character"
    // password doesnt contain number -> "Password must contain at least one letter one number and one special character"
    // password doesnt contain special character -> "Password must contain at least one letter one number and one special character"
    public String createAccount(String userName, String password){
        if(isValidUsername(userName)){
            if(isValidPassword(password)){
                DBManager DBM = new DBManager();
                
            }
            
            
        }
        return null;
    }

    public String login(String userName, String password){
        if(isValidUsername(userName)){
            DBManager DBM = new DBManager();
            User u = DBM.getUser(userName);
            if(u.validatePassword(password)){
                this.user = u;
                return "login successful";
            }else{
                return "login failed";
            }
        }
        return "login failed";
    }
    public String logout(User user){
        this.user = null;
        return "logout successful";
    }
    private boolean userNameLengthValid(String userName){
        if(userName.length() < MIN_LENGTH_USERNAME && userName.length() > MAX_LENGTH_USERNAME){
            return true;
        } else return false;
    }
    private boolean userNameCharactersValid(String userName){
        for(int i = 0; i < userName.length(); i++){
            if(!Character.isDigit(userName.charAt(i)) || !Character.isAlphabetic(userName.charAt(i)))
                return false;
        }
        return true;
    }
    private boolean passwordValidLength(String password){
        if(password.length() < MIN_PASSWORD_LENGTH && password.length() > MAX_PASSWORD_LENGTH )
            return true;
        else return false;
    }
    private boolean passwordHasLetter(String password){
        for(int i = 0; i < password.length(); i++){
            if(Character.isAlphabetic(password.charAt(i)))
                return true;
        }
        return false;
    }
    private boolean passwordHasNumber(String password){
        for(int i = 0; i < password.length(); i++){
            if(Character.isDigit(password.charAt(i))){
                return true;
            }
        }
        return false;
    }
    //List of valid special characters !@#$%&*_-+=
    private boolean passwordHasSpecialCharacter(String password){
        for(int i = 0; i < password.length(); i++){
            //make enum and check if character is in this type
        }
    }
    
    // if password is valid, returns 0
    // if password is too short, return 1
    // if password is too long, return 2
    // if password doesnt contain letter, return 3
    // if password doesnt contain number, return 4
    // if password doesnt contain special character, return 5
    private int isValidPassword(String password){
        return 0;
    }
    // if username is valid, return 0
    // if username is too short, return 1
    // if username contains invalid character, return 2

    private int isValidUsername(String userName){
        if(!userNameLengthValid(userName))
            return 1;
        else if(!userNameCharactersValid(userName))
            return 2;
        else return 0;
    }
}
