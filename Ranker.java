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
 *   4. skipPair() — skip current pair without choosing (next button)
 *   5. skipItem(itemName) — remove an item from ranking entirely (skip X button)
 *   6. undo() — revert the last action
 *   7. When all pairs compared, computes final ranking
 *
 * Methods (from DCD):
 *   - startRankingSession(int listID)
 *   - recordChoice(int listID, Item selectedItemID)
 *   - skipPair(): boolean
 *   - skipItem(String itemName): boolean
 *   - undo(): boolean
 */
public class Ranker {
    DBManager dbManager;
    private List<Item> items;
    private List<Item> skippedItems;    // items removed via skip X
    private int[][] wins;               // wins[i][j] = 1 means item i was chosen over item j
    private int currentI;
    private int currentJ;
    private boolean sessionActive;
    private int listID;
    private List<Action> history;       // action history for undo

    // Represents a single action that can be undone
    private static class Action {
        enum Type { CHOICE, SKIP_PAIR, SKIP_ITEM }
        Type type;
        int prevI, prevJ;           // cursor position before the action
        int winnerIdx, loserIdx;    // for CHOICE: who won over whom
        Item removedItem;           // for SKIP_ITEM: the item that was removed
        int removedIndex;           // for SKIP_ITEM: original index in items list
        int[][] savedWins;          // for SKIP_ITEM: wins matrix before removal

        Action(Type type, int prevI, int prevJ){
            this.type = type;
            this.prevI = prevI;
            this.prevJ = prevJ;
        }
    }

    public Ranker(DBManager dbManager){
        this.dbManager = dbManager;
        this.items = new ArrayList<>();
        this.skippedItems = new ArrayList<>();
        this.history = new ArrayList<>();
        this.sessionActive = false;
    }

    /**
     * Starts a ranking session for a given list.
     * Loads items from the database and prepares pairwise comparisons.
     */
    public boolean startRankingSession(int listID){
        this.listID = listID;

        List<String> itemNames = dbManager.getItemsForList(listID);
        if(itemNames == null || itemNames.size() < 2){
            return false;
        }

        items.clear();
        skippedItems.clear();
        history.clear();
        for(String name : itemNames){
            items.add(new Item(name));
        }

        wins = new int[items.size()][items.size()];
        currentI = 0;
        currentJ = 1;
        sessionActive = true;
        return true;
    }

    /**
     * Returns the next pair of items to compare.
     */
    public Item[] getNextPair(){
        if(!sessionActive) return null;
        if(currentI >= items.size() - 1) return null;

        return new Item[]{ items.get(currentI), items.get(currentJ) };
    }

    /**
     * Records the user's choice between the current pair.
     */
    public boolean recordChoice(int listID, Item selectedItem){
        if(!sessionActive || this.listID != listID) return false;
        if(selectedItem == null) return false;
        if(currentI >= items.size() - 1) return false;

        Item item1 = items.get(currentI);
        Item item2 = items.get(currentJ);

        Action action = new Action(Action.Type.CHOICE, currentI, currentJ);

        if(selectedItem.getName().equalsIgnoreCase(item1.getName())){
            wins[currentI][currentJ] = 1;
            action.winnerIdx = currentI;
            action.loserIdx = currentJ;
        } else if(selectedItem.getName().equalsIgnoreCase(item2.getName())){
            wins[currentJ][currentI] = 1;
            action.winnerIdx = currentJ;
            action.loserIdx = currentI;
        } else {
            return false;
        }

        history.add(action);
        advancePair();
        return true;
    }

    /**
     * Skips the current pair without recording a choice.
     * Corresponds to the "next" button in the wireframe.
     * Neither item gets a win for this matchup.
     *
     * @return true if pair was skipped, false if no active session
     */
    public boolean skipPair(){
        if(!sessionActive) return false;
        if(currentI >= items.size() - 1) return false;

        Action action = new Action(Action.Type.SKIP_PAIR, currentI, currentJ);
        history.add(action);
        advancePair();
        return true;
    }

    /**
     * Removes an item from the ranking session entirely.
     * Corresponds to the "skip X" button under each card in the wireframe.
     * The item is excluded from all future comparisons and the final ranking.
     *
     * @param itemName the name of the item to skip/remove
     * @return true if item was removed, false if not found or session not active
     */
    public boolean skipItem(String itemName){
        if(!sessionActive) return false;
        if(itemName == null) return false;

        // Find the item in the active list
        int removeIdx = -1;
        for(int i = 0; i < items.size(); i++){
            if(items.get(i).getName().equalsIgnoreCase(itemName.trim())){
                removeIdx = i;
                break;
            }
        }
        if(removeIdx == -1) return false;

        // Need at least 2 items remaining after removal to continue
        if(items.size() <= 2){
            // Save action for undo, then end session with remaining item
            Action action = new Action(Action.Type.SKIP_ITEM, currentI, currentJ);
            action.removedItem = items.get(removeIdx);
            action.removedIndex = removeIdx;
            action.savedWins = copyWins();
            history.add(action);

            skippedItems.add(items.remove(removeIdx));
            // Rebuild wins matrix for single remaining item
            wins = new int[1][1];
            currentI = 0;
            currentJ = 1;
            sessionActive = false;
            return true;
        }

        // Save state for undo
        Action action = new Action(Action.Type.SKIP_ITEM, currentI, currentJ);
        action.removedItem = items.get(removeIdx);
        action.removedIndex = removeIdx;
        action.savedWins = copyWins();
        history.add(action);

        // Remove item and rebuild wins matrix
        skippedItems.add(items.remove(removeIdx));
        rebuildWinsMatrix(removeIdx);

        // Adjust cursor: if removed index affects current pair, reset to valid position
        if(currentI >= items.size() - 1){
            currentI = items.size() - 2;
            currentJ = items.size() - 1;
        }
        if(currentJ >= items.size()){
            currentI++;
            currentJ = currentI + 1;
        }
        if(currentI >= items.size() - 1){
            sessionActive = false;
        }

        return true;
    }

    /**
     * Undoes the last action (choice, skip pair, or skip item).
     * Corresponds to the "undo" button in the wireframe.
     *
     * @return true if undo was successful, false if no actions to undo
     */
    public boolean undo(){
        if(history.isEmpty()) return false;

        Action last = history.remove(history.size() - 1);

        switch(last.type){
            case CHOICE:
                // Undo the win record
                wins[last.winnerIdx][last.loserIdx] = 0;
                // Restore cursor
                currentI = last.prevI;
                currentJ = last.prevJ;
                sessionActive = true;
                break;

            case SKIP_PAIR:
                // Just restore cursor
                currentI = last.prevI;
                currentJ = last.prevJ;
                sessionActive = true;
                break;

            case SKIP_ITEM:
                // Re-insert the removed item at its original position
                items.add(last.removedIndex, last.removedItem);
                skippedItems.remove(last.removedItem);
                // Restore the entire wins matrix from before the skip
                wins = last.savedWins;
                // Restore cursor
                currentI = last.prevI;
                currentJ = last.prevJ;
                sessionActive = true;
                break;
        }

        return true;
    }

    /**
     * Returns the list of items that have been skipped/removed from the session.
     */
    public List<Item> getSkippedItems(){
        return skippedItems;
    }

    /**
     * Returns whether undo is available.
     */
    public boolean canUndo(){
        return !history.isEmpty();
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
     * Skipped items are excluded from the final ranking.
     *
     * @param authorID the user who performed the ranking
     * @return ordered list of item names (rank 1 first), or null if not complete
     */
    public List<String> computeAndSaveRanking(String authorID){
        if(sessionActive || items.size() == 0) return null;

        int[] totalWins = new int[items.size()];
        for(int i = 0; i < items.size(); i++){
            for(int j = 0; j < items.size(); j++){
                totalWins[i] += wins[i][j];
            }
        }

        List<Integer> indices = new ArrayList<>();
        for(int i = 0; i < items.size(); i++) indices.add(i);

        indices.sort((a, b) -> totalWins[b] - totalWins[a]);

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
     * Returns the items loaded for the current session (excluding skipped).
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

    // ===== PRIVATE HELPERS =====

    private void advancePair(){
        currentJ++;
        if(currentJ >= items.size()){
            currentI++;
            currentJ = currentI + 1;
        }
        if(currentI >= items.size() - 1){
            sessionActive = false;
        }
    }

    private int[][] copyWins(){
        int[][] copy = new int[wins.length][wins.length];
        for(int i = 0; i < wins.length; i++){
            for(int j = 0; j < wins.length; j++){
                copy[i][j] = wins[i][j];
            }
        }
        return copy;
    }

    private void rebuildWinsMatrix(int removedIdx){
        int newSize = items.size();
        int[][] newWins = new int[newSize][newSize];
        for(int i = 0; i < newSize; i++){
            for(int j = 0; j < newSize; j++){
                int oldI = (i >= removedIdx) ? i + 1 : i;
                int oldJ = (j >= removedIdx) ? j + 1 : j;
                if(oldI < wins.length && oldJ < wins.length){
                    newWins[i][j] = wins[oldI][oldJ];
                }
            }
        }
        wins = newWins;
    }
}
