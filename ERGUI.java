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
 */
public class ERGUI {
    private String page;
    private String lastMessage;
    private Item lastItem1;
    private Item lastItem2;
    private List<String> lastResults;

    public ERGUI(){
        this.page = "home";
        this.lastMessage = null;
        this.lastItem1 = null;
        this.lastItem2 = null;
        this.lastResults = null;
    }

    /**
     * Displays a confirmation message to the user.
     * Referenced in multiple sequence diagrams.
     */
    public void displayConfirmation(String msg){
        this.page = "confirmation";
        this.lastMessage = msg;
        System.out.println("[ERGUI] Confirmation: " + msg);
    }

    /**
     * Displays an error message to the user.
     * Referenced in multiple sequence diagrams.
     */
    public void displayError(String msg){
        this.page = "error";
        this.lastMessage = msg;
        System.out.println("[ERGUI] Error: " + msg);
    }

    /**
     * Displays the next pair of items for pairwise comparison.
     * Referenced in Rank List DSD: displayNextPair(item1, item2)
     */
    public void displayNextPair(Item item1, Item item2){
        this.page = "ranking";
        this.lastItem1 = item1;
        this.lastItem2 = item2;
        System.out.println("[ERGUI] Compare: " + item1.getName() + " vs " + item2.getName());
    }

    /**
     * Presents the final ranking results for a list.
     * Referenced in Rank List DSD: presentFinalResults(listID)
     */
    public void presentFinalResults(int listID, List<String> rankedItems){
        this.page = "results";
        this.lastResults = rankedItems;
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
}
