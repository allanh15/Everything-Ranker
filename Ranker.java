import java.util.ArrayList;
import java.util.List;

/**
 * Ranker
 * Based on the Design Class Diagram for Everything Ranker
 *
 * Handles the pairwise comparison ranking session.
 *
 * Sequence (from Rank List DSD):
 *   1. startRankingSession(listID) — loads items from DBManager
 *   2. getNextPair() — returns the next pair of items to compare
 *   3. recordChoice(listID, selectedItemID) — records user's choice
 *   4. When all pairs compared, computes final ranking
 *
 * Methods (from DCD):
 *   - startRankingSession(int listID)
 *   - recordChoice(int listID, Item selectedItemID)
 */
public class Ranker {
    DBManager dbManager;
    private List<Item> items;
    private int[][] wins;       // wins[i][j] = 1 means item i was chosen over item j
    private int currentI;
    private int currentJ;
    private boolean sessionActive;
    private int listID;

    public Ranker(DBManager dbManager){
        this.dbManager = dbManager;
        this.items = new ArrayList<>();
        this.sessionActive = false;
    }

    /**
     * Starts a ranking session for a given list.
     * Loads items from the database and prepares pairwise comparisons.
     * Referenced in Rank List DSD: startRankingSession(listID)
     *
     * @param listID the list to rank
     * @return true if session started successfully, false otherwise
     */
    public boolean startRankingSession(int listID){
        this.listID = listID;

        // Retrieve list items from DB
        List<String> itemNames = dbManager.getItemsForList(listID);
        if(itemNames == null || itemNames.size() < 2){
            return false; // need at least 2 items to compare
        }

        items.clear();
        for(String name : itemNames){
            items.add(new Item(name));
        }

        // Initialize wins matrix
        wins = new int[items.size()][items.size()];
        currentI = 0;
        currentJ = 1;
        sessionActive = true;
        return true;
    }

    /**
     * Returns the next pair of items to compare.
     * Referenced in Rank List DSD via ERGUI.displayNextPair(item1, item2)
     *
     * @return array of two Items, or null if ranking is complete
     */
    public Item[] getNextPair(){
        if(!sessionActive) return null;
        if(currentI >= items.size() - 1) return null; // all pairs compared

        return new Item[]{ items.get(currentI), items.get(currentJ) };
    }

    /**
     * Records the user's choice between the current pair.
     * Referenced in Rank List DSD: recordChoice(listID, selectedItemID)
     *
     * @param listID the list being ranked
     * @param selectedItem the item the user chose as better
     * @return true if recorded, false if session not active or item invalid
     */
    public boolean recordChoice(int listID, Item selectedItem){
        if(!sessionActive || this.listID != listID) return false;
        if(selectedItem == null) return false;

        // Determine which item was selected
        Item item1 = items.get(currentI);
        Item item2 = items.get(currentJ);

        if(selectedItem.getName().equalsIgnoreCase(item1.getName())){
            wins[currentI][currentJ] = 1;
        } else if(selectedItem.getName().equalsIgnoreCase(item2.getName())){
            wins[currentJ][currentI] = 1;
        } else {
            return false; // selected item not in current pair
        }

        // Advance to next pair
        currentJ++;
        if(currentJ >= items.size()){
            currentI++;
            currentJ = currentI + 1;
        }

        // Check if all pairs have been compared
        if(currentI >= items.size() - 1){
            sessionActive = false;
        }

        return true;
    }

    /**
     * Returns whether all pairs have been compared.
     */
    public boolean isComplete(){
        return !sessionActive && items.size() > 0 && currentI >= items.size() - 1;
    }

    /**
     * Returns whether a ranking session is currently active.
     */
    public boolean isSessionActive(){
        return sessionActive;
    }

    /**
     * Computes the final ranking based on wins and saves to database.
     * Called when all pairs have been compared.
     *
     * @param authorID the user who performed the ranking
     * @return ordered list of item names (rank 1 first), or null if not complete
     */
    public List<String> computeAndSaveRanking(String authorID){
        if(sessionActive || items.size() == 0) return null;

        // Compute total wins for each item
        int[] totalWins = new int[items.size()];
        for(int i = 0; i < items.size(); i++){
            for(int j = 0; j < items.size(); j++){
                totalWins[i] += wins[i][j];
            }
        }

        // Sort items by total wins (descending) using simple selection sort
        List<Integer> indices = new ArrayList<>();
        for(int i = 0; i < items.size(); i++) indices.add(i);

        indices.sort((a, b) -> totalWins[b] - totalWins[a]);

        // Build ranked list and save to DB
        List<String> rankedNames = new ArrayList<>();
        dbManager.deleteRankings(listID, authorID);

        for(int rank = 0; rank < indices.size(); rank++){
            String name = items.get(indices.get(rank)).getName();
            rankedNames.add(name);
            dbManager.addRanking(listID, authorID, name, rank + 1);
        }

        return rankedNames;
    }

    /**
     * Returns the items loaded for the current session.
     */
    public List<Item> getItems(){
        return items;
    }

    /**
     * Returns the list ID for the current session.
     */
    public int getListID(){
        return listID;
    }
}
