import java.util.List;

/**
 * ERGUI (Everything Ranker GUI)
 * Based on the Design Class Diagram for Everything Ranker
 *
 * Handles all display/UI operations. In a real application this would
 * render HTML/templates. For backend testing, it captures messages
 * so tests can verify what would be displayed.
 *
 * Attributes (from DCD):
 *   - page (String representing current page)
 *
 * Methods (from DCD):
 *   - displayConfirmation(String msg)
 *   - displayError(String msg)
 *   - displayNextPair(Item item1, Item item2)
 *   - presentFinalResults(int listID)
 *
 * Wireframe features:
 *   - displaySkipOptions(Item item1, Item item2) — show skip X under each card
 *   - displayUndoAvailable(boolean available) — show/hide undo button
 *   - displayItemSkipped(String itemName) — confirm an item was removed
 */
public class ERGUI {
    private String page;
    private String lastMessage;
    private Item lastItem1;
    private Item lastItem2;
    private List<String> lastResults;
    private boolean undoAvailable;
    private boolean skipOptionsVisible;
    private String lastSkippedItem;

    public ERGUI(){
        this.page = "home";
        this.lastMessage = null;
        this.lastItem1 = null;
        this.lastItem2 = null;
        this.lastResults = null;
        this.undoAvailable = false;
        this.skipOptionsVisible = false;
        this.lastSkippedItem = null;
    }

    /**
     * Displays a confirmation message to the user.
     */
    public void displayConfirmation(String msg){
        this.page = "confirmation";
        this.lastMessage = msg;
        System.out.println("[ERGUI] Confirmation: " + msg);
    }

    /**
     * Displays an error message to the user.
     */
    public void displayError(String msg){
        this.page = "error";
        this.lastMessage = msg;
        System.out.println("[ERGUI] Error: " + msg);
    }

    /**
     * Displays the next pair of items for pairwise comparison.
     * Shows both items with skip X buttons underneath each, plus undo/next controls.
     */
    public void displayNextPair(Item item1, Item item2){
        this.page = "ranking";
        this.lastItem1 = item1;
        this.lastItem2 = item2;
        this.skipOptionsVisible = true;
        System.out.println("[ERGUI] Compare: " + item1.getName() + " vs " + item2.getName());
    }

    /**
     * Shows or hides the skip X buttons under each item card.
     * Corresponds to the "skip X" buttons in the wireframe.
     */
    public void displaySkipOptions(Item item1, Item item2){
        this.skipOptionsVisible = true;
        this.lastItem1 = item1;
        this.lastItem2 = item2;
        System.out.println("[ERGUI] Skip options: [skip " + item1.getName() + "] [skip " + item2.getName() + "]");
    }

    /**
     * Updates whether the undo button is available.
     * Corresponds to the "undo" button (top-left) in the wireframe.
     */
    public void displayUndoAvailable(boolean available){
        this.undoAvailable = available;
        System.out.println("[ERGUI] Undo button: " + (available ? "enabled" : "disabled"));
    }

    /**
     * Confirms that an item was skipped/removed from the ranking.
     * Shown after user clicks "skip X" on an item card.
     */
    public void displayItemSkipped(String itemName){
        this.lastSkippedItem = itemName;
        this.lastMessage = itemName + " removed from ranking";
        System.out.println("[ERGUI] Skipped: " + itemName + " removed from ranking");
    }

    /**
     * Confirms that a pair was skipped (next button pressed).
     */
    public void displayPairSkipped(){
        this.lastMessage = "Pair skipped";
        System.out.println("[ERGUI] Pair skipped — moving to next comparison");
    }

    /**
     * Confirms that the last action was undone.
     */
    public void displayUndoConfirmed(){
        this.lastMessage = "Last action undone";
        System.out.println("[ERGUI] Undo — reverted to previous comparison");
    }

    /**
     * Presents the final ranking results for a list.
     */
    public void presentFinalResults(int listID, List<String> rankedItems){
        this.page = "results";
        this.lastResults = rankedItems;
        this.skipOptionsVisible = false;
        System.out.println("[ERGUI] Final Results for list " + listID + ":");
        if(rankedItems != null){
            for(int i = 0; i < rankedItems.size(); i++){
                System.out.println("  #" + (i + 1) + ": " + rankedItems.get(i));
            }
        }
    }

    // Getters for testing
    public String getPage(){ return page; }
    public String getLastMessage(){ return lastMessage; }
    public Item getLastItem1(){ return lastItem1; }
    public Item getLastItem2(){ return lastItem2; }
    public List<String> getLastResults(){ return lastResults; }
    public boolean isUndoAvailable(){ return undoAvailable; }
    public boolean isSkipOptionsVisible(){ return skipOptionsVisible; }
    public String getLastSkippedItem(){ return lastSkippedItem; }
}
