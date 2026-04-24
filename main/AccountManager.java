/**
 * AccountManager
 * Based on the Design Class Diagram for Everything Ranker
 *
 * Handles account-level operations: access validation and password management.
 *
 * Methods (from DCD):
 *   - validateRankAccess(int listID)
 *   - changePassword(userID, oldPW, newPW)
 *   - verify(oldPW, newPW)
 */
public class AccountManager {
    DBManager dbManager;

    public AccountManager(DBManager dbManager){
        this.dbManager = dbManager;
    }

    /**
     * Validates whether a user has access to rank a specific list.
     * Referenced in Rank List sequence diagram: validateRankAccess(listID)
     * A user can rank if they are the author OR the list is public.
     */
    public boolean validateRankAccess(String userID, int listID){
        if(userID == null || userID.trim().isEmpty()) return false;

        EverythingList list = dbManager.getList(listID);
        if(list == null) return false;

        // User can rank if they are the author OR the list is public
        return list.getAuthorID().equals(userID) || list.getAccess() == true;
    }

    /**
     * Changes a user's password after verifying the old one.
     * Referenced in Change Password DSD.
     */
    public String changePassword(String userID, String oldPW, String newPW){
        if(userID == null || oldPW == null || newPW == null) return "invalid input";

        User user = dbManager.getUser(userID);
        if(user == null) return "user not found";

        if(verify(oldPW, user.getPassword())){
            user.setPassword(newPW);
            return "password changed";
        }

        return "incorrect current password";
    }

    /**
     * Verifies that the provided password matches the stored password.
     * Referenced in DCD: verify(oldPW, newPW)
     */
    public boolean verify(String oldPW, String storedPW){
        if(oldPW == null || storedPW == null) return false;
        return oldPW.equals(storedPW);
    }
}
