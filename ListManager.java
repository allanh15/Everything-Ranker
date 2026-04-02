import java.util.Date;
import java.util.List;

/**
 * ListManager
 * Handles Create List (UC19/R19), Rank List (UC7), Delete List, and Add Item use cases.
 *
 * Methods (from DCD):
 *   - createList(String title, String authorID, boolean access): int
 *   - deleteList(String userID, int listID): boolean
 *   - addItem(int listID, String itemName): String
 *   - rankList(int listID, String authorID, List<String> rankedItems): String
 */
public class ListManager {
    DBManager dbManager;

    public ListManager(DBManager dbManager){
        this.dbManager = dbManager;
    }

    /**
     * Creates a new list and stores it in the database.
     * Referenced in UC19/R19: User creates a new ranking list and assigns it a title.
     *
     * @param title - the list title
     * @param authorID - the userID of the creator
     * @param access - true for public, false for private
     * @return the new list ID if successful, -1 if failed
     */
    public int createList(String title, String authorID, boolean access){
        // Validate title
        if(title == null || title.trim().isEmpty()){
            return -1;
        }
        // Validate authorID
        if(authorID == null || authorID.trim().isEmpty()){
            return -1;
        }

        EverythingList newList = new EverythingList(title.trim(), authorID, access);
        int newID = dbManager.addList(newList);
        return newID;
    }

    /**
     * Deletes a list if the requesting user is the author.
     * Enforces R22: Users cannot edit or delete lists created by other users.
     *
     * @param userID - the ID of the user requesting deletion
     * @param listID - the ID of the list to delete
     * @return true if list was deleted, false otherwise
     */
    public boolean deleteList(String userID, int listID){
        if(userID == null || userID.trim().isEmpty()) return false;

        String author = dbManager.getAuthor(listID);
        if(author == null) return false; // list does not exist

        // R22: verify requesting user is the author
        if(!author.equals(userID)) return false;

        return dbManager.deleteList(listID);
    }

    /**
     * Adds an item to a list.
     * Referenced in DCD: addItem(listID, itemName)
     *
     * @param listID - the ID of the list to add the item to
     * @param itemName - the name of the item to add
     * @return result message
     */
    public String addItem(int listID, String itemName){
        if(itemName == null || itemName.trim().isEmpty()){
            return "invalid item name";
        }

        EverythingList list = dbManager.getList(listID);
        if(list == null) return "list not found";

        if(list.hasItem(itemName.trim())){
            return "item already exists";
        }

        dbManager.addItem(listID, itemName.trim());
        return "item added";
    }

    /**
     * Ranks items in a list. Saves the ranking order for a specific user.
     * Referenced in UC7 / Rank List sequence diagram.
     *
     * @param listID - the ID of the list to rank
     * @param authorID - the userID of the person ranking
     * @param rankedItems - ordered list of item names (index 0 = rank 1)
     * @return "ranking successful" or an error message
     */
    public String rankList(int listID, String authorID, List<String> rankedItems){
        // Validate authorID
        if(authorID == null || authorID.trim().isEmpty()){
            return "invalid author";
        }

        // Validate rankedItems
        if(rankedItems == null || rankedItems.isEmpty()){
            return "invalid ranking";
        }

        // Validate listID by retrieving the list
        EverythingList list = dbManager.getList(listID);
        if(list == null){
            return "list not found";
        }

        // Check that list has items to rank
        if(list.getItems().isEmpty()){
            return "list has no items";
        }

        // Verify all ranked items actually exist in the list
        for(String item : rankedItems){
            if(item == null || item.trim().isEmpty()){
                return "invalid ranking";
            }
            if(!list.hasItem(item.trim())){
                return "item not in list";
            }
        }

        // Check for duplicate items in the ranking
        for(int i = 0; i < rankedItems.size(); i++){
            for(int j = i + 1; j < rankedItems.size(); j++){
                if(rankedItems.get(i).trim().equalsIgnoreCase(rankedItems.get(j).trim())){
                    return "duplicate item in ranking";
                }
            }
        }

        // Delete any existing ranking by this user for this list
        dbManager.deleteRankings(listID, authorID);

        // Save new rankings
        for(int i = 0; i < rankedItems.size(); i++){
            int result = dbManager.addRanking(listID, authorID, rankedItems.get(i).trim(), i + 1);
            if(result == 0){
                return "ranking failed";
            }
        }

        return "ranking successful";
    }
}
