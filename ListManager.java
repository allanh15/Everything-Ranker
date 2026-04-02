import java.util.List;

/**
 * ListManager
 * Handles Create List (UC6) and Rank List (UC9) use cases.
 *
 * Create List sequence:
 *   1. Validate inputs (title, authorID)
 *   2. DBManager.addList(list) → returns new list ID
 *
 * Rank List sequence:
 *   1. Validate inputs (listID, authorID, rankedItems)
 *   2. DBManager.getList(listID) → verify list exists
 *   3. Verify all ranked items exist in the list
 *   4. Delete any existing ranking by this user for this list
 *   5. DBManager.addRanking() for each item in order
 */
public class ListManager {
    DBManager dbManager;

    public ListManager(DBManager dbManager){
        this.dbManager = dbManager;
    }

    /**
     * Creates a new list and stores it in the database.
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
     * Ranks items in a list. Saves the ranking order for a specific user.
     * Referenced in Rank List sequence diagram.
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
