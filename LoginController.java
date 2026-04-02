public class LoginController {
    User user;
    public static final int MIN_LENGTH_USERNAME = 8;
    public static final int MAX_LENGTH_USERNAME = 16;

    public static final int MIN_PASSWORD_LENGTH = 12;
     /*
     Username rules: must be between 8 and 16 characters containing letters(upper or lower) and numbers. cannot contain special characters or spaces
     Password rules: password must be at least 12 characters containting letters, numbers and at least one special character
     */
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
    private boolean isValidUsername(String userName){
        if(userName.length() < 8 && userName.length() > 16){
            for(int i = 0; i < userName.length(); i++){
                if(!Character.isDigit(userName.charAt(i)) || !Character.isAlphabetic(userName.charAt(i)))
                    return false;
            }
            return true;
        }else return false;
    }
}
