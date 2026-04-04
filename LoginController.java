import java.util.Random;
public class LoginController {
    User currentUser;
    public static final int MIN_LENGTH_USERNAME = 8;
    public static final int MAX_LENGTH_USERNAME = 16;

    public static final int MIN_PASSWORD_LENGTH = 12;
    public static final int MAX_PASSWORD_LENGTH = 20;


    //List of valid special characters !#$%&*_-
    private enum SpecialChar {
        EXCLAMATION(33),
        POUND(35),
        DOLLAR(36),
        PERCENT(37),
        AMPERSAND(38),
        STAR(42),
        UNDERSCORE(95),
        HYPHEN(45);

        private final int value;
        private SpecialChar(int value){this.value = value; }
        public int getValue() {return value;}
    }
    
     /*
     Username rules: must be between 8 and 16 characters containing letters(upper or lower) and numbers. cannot contain special characters or spaces
     Password rules: password must be at 12-20 characters containting letters, numbers and at least one special character
     */

    /* -Craeate Account Retrun Values Based On Condition-
        successful -> "Account created"
        userName invalid length -> "Username must be between 8 and 16 characters"
        userName contains special character -> "Username can only contain letters and numbers"
        password invalid length -> "Password must be between 12 and 20 characters"
        password doesnt contain letter -> "Password must contain at least one letter one number and one special character"
        password doesnt contain number -> "Password must contain at least one letter one number and one special character"
        password doesnt contain special character -> "Password must contain at least one letter one number and one special character"
        user already exists(username is not unique) -> "Username already exists"
    */
    
    public String createAccount(String userName, String password){
        switch(isValidUsername(userName)){
            case 1:
                return "Username must be between 8 and 16 characters";
            case 2:
                return "Username can only contain letters and numbers";
            default:
                switch(isValidPassword(password)){
                    case 1:
                        return "Password must be between 12 and 20 characters";
                    case 2:
                        return "Password must contain at least one letter one number and one special character";
                    case 3:
                        return "Password must contain at least one letter one number and one special character";
                    case 4:
                        return "Password must contain at least one letter one number and one special character";
                    default:
                        DBManager dbManager = new DBManager();
                        if(dbManager.userExists(userName))
                            return "Username already exists";
                        Random rnd = new Random();
                        String userID = String.valueOf(10000000 + rnd.nextInt(90000000));
                        User newUser = new User(userID, userName, password, null);
                        dbManager.addUser(newUser);
                        return "Account Created";
                }
        }
        
    }


    public String login(String userName, String password){
        DBManager DBM = new DBManager();
        User u = DBM.getUser(userName);
        if(u.validatePassword(password)){
            currentUser = u;
            return "login successful";
        }else{
            return "login failed";
        }
    }


    public String logout(User user){
        currentUser = null;
        return "logout successful";
    }

    
    private boolean userNameLengthValid(String userName){
        if(userName.length() > MIN_LENGTH_USERNAME && userName.length() < MAX_LENGTH_USERNAME){
            return true;
        } else return false;
    }
    private boolean userNameCharactersValid(String userName){
        for(int i = 0; i < userName.length(); i++){
            if(!Character.isDigit(userName.charAt(i)) || !Character.isLetter(userName.charAt(i)))
                return false;
        }
        return true;
    }
    private boolean passwordValidLength(String password){
        if(password.length() > MIN_PASSWORD_LENGTH && password.length() < MAX_PASSWORD_LENGTH)
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
            for(SpecialChar specialChar : SpecialChar.values()){
                if((int) password.charAt(i) == specialChar.getValue())
                    return true;
            }
        }
        return false;
    }
    
    // if password is valid, returns 0
    // if password is invalid length, return 1
    // if password doesnt contain letter, return 2
    // if password doesnt contain number, return 3
    // if password doesnt contain special character, return 4
    private int isValidPassword(String password){
        if(!passwordValidLength(password))
            return 1;
        else if(!passwordHasLetter(password))
            return 2;
        else if(!passwordHasNumber(password))
            return 3;
        else if(!passwordHasSpecialCharacter(password))
            return 4;
        else return 0;
    }
    // if username is valid, return 0
    // if username is invalid length, return 1
    // if username contains invalid character, return 2

    private int isValidUsername(String userName){
        if(!userNameLengthValid(userName))
            return 1;
        else if(!userNameCharactersValid(userName))
            return 2;
        else return 0;
    }
}
