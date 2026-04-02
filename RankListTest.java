import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * RankListTest
 * Use Case: UC9 - Rank List
 *
 * Tests the rankList(listID, authorID, rankedItems) method of ListManager
 * following the Kung book test case analysis (Figures 20.13-20.15).
 */
public class RankListTest {
    static int passed = 0;
    static int failed = 0;
    static DBManager dbManager;
    static ListManager listManager;

    static void assertEquals(String testName, String expected, String actual){
        if(expected.equals(actual)){
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName + " (expected \"" + expected + "\", got \"" + actual + "\")");
            failed++;
        }
    }

    static void assertTrue(String testName, boolean condition){
        if(condition){
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName);
            failed++;
        }
    }

    static int setupWithItems(){
        // Delete test db if it exists so each run is fresh
        File f = new File("rank_list_test.db");
        if(f.exists()) f.delete();

        dbManager = new DBManager("rank_list_test.db");
        listManager = new ListManager(dbManager);

        // Seed a user
        dbManager.addUser(new User("00000001", "testuser", "Pass123!", "test@email.com"));

        // Create a list and add items
        int listID = listManager.createList("Best Foods", "00000001", false);
        dbManager.addItem(listID, "Pizza");
        dbManager.addItem(listID, "Sushi");
        dbManager.addItem(listID, "Tacos");
        return listID;
    }

    static void teardown(){
        dbManager.close();
        File f = new File("rank_list_test.db");
        if(f.exists()) f.delete();
    }

    public static void main(String args[]){
        System.out.println("\n=== UC9 - RANK LIST TESTS ===\n");

        // TC1: Valid listID, valid author, valid ranked items → ranking successful
        int listID = setupWithItems();
        List<String> ranking1 = Arrays.asList("Pizza", "Sushi", "Tacos");
        String result1 = listManager.rankList(listID, "00000001", ranking1);
        assertEquals("TC1: Valid ranking → ranking successful", "ranking successful", result1);
        teardown();

        // TC1b: Verify rankings are stored in correct order
        listID = setupWithItems();
        listManager.rankList(listID, "00000001", Arrays.asList("Tacos", "Pizza", "Sushi"));
        List<String> stored = dbManager.getRankings(listID, "00000001");
        assertTrue("TC1b: Rankings stored in correct order",
            stored.size() == 3 &&
            stored.get(0).equals("Tacos") &&
            stored.get(1).equals("Pizza") &&
            stored.get(2).equals("Sushi"));
        teardown();

        // TC2: Invalid listID (does not exist) → list not found
        listID = setupWithItems();
        List<String> ranking2 = Arrays.asList("Pizza");
        String result2 = listManager.rankList(999, "00000001", ranking2);
        assertEquals("TC2: Non-existent list → list not found", "list not found", result2);
        teardown();

        // TC3: Exceptional authorID (empty string) → invalid author
        listID = setupWithItems();
        List<String> ranking3 = Arrays.asList("Pizza", "Sushi", "Tacos");
        String result3 = listManager.rankList(listID, "", ranking3);
        assertEquals("TC3: Empty authorID → invalid author", "invalid author", result3);
        teardown();

        // TC3b: Null authorID → invalid author
        listID = setupWithItems();
        String result3b = listManager.rankList(listID, null, Arrays.asList("Pizza"));
        assertEquals("TC3b: Null authorID → invalid author", "invalid author", result3b);
        teardown();

        // TC4: Ranked items contain an item not in the list → item not in list
        listID = setupWithItems();
        List<String> ranking4 = Arrays.asList("Pizza", "Sushi", "Burger");
        String result4 = listManager.rankList(listID, "00000001", ranking4);
        assertEquals("TC4: Item not in list → item not in list", "item not in list", result4);
        teardown();

        // TC5: Duplicate items in ranking → duplicate item in ranking
        listID = setupWithItems();
        List<String> ranking5 = Arrays.asList("Pizza", "Sushi", "Pizza");
        String result5 = listManager.rankList(listID, "00000001", ranking5);
        assertEquals("TC5: Duplicate items → duplicate item in ranking", "duplicate item in ranking", result5);
        teardown();

        // TC6: Empty ranked items list → invalid ranking
        listID = setupWithItems();
        List<String> ranking6 = new ArrayList<>();
        String result6 = listManager.rankList(listID, "00000001", ranking6);
        assertEquals("TC6: Empty ranking list → invalid ranking", "invalid ranking", result6);
        teardown();

        // TC7: Null ranked items list → invalid ranking
        listID = setupWithItems();
        String result7 = listManager.rankList(listID, "00000001", null);
        assertEquals("TC7: Null ranking list → invalid ranking", "invalid ranking", result7);
        teardown();

        // TC8: Re-ranking overwrites previous ranking
        listID = setupWithItems();
        listManager.rankList(listID, "00000001", Arrays.asList("Pizza", "Sushi", "Tacos"));
        listManager.rankList(listID, "00000001", Arrays.asList("Tacos", "Sushi", "Pizza"));
        List<String> reranked = dbManager.getRankings(listID, "00000001");
        assertTrue("TC8: Re-ranking overwrites previous (Tacos now #1)",
            reranked.size() == 3 &&
            reranked.get(0).equals("Tacos") &&
            reranked.get(2).equals("Pizza"));
        teardown();

        // TC9: Partial ranking (subset of items) is allowed
        listID = setupWithItems();
        String result9 = listManager.rankList(listID, "00000001", Arrays.asList("Pizza", "Sushi"));
        assertEquals("TC9: Partial ranking (2 of 3 items) → ranking successful", "ranking successful", result9);
        teardown();

        // TC10: Ranking on a list with no items → list has no items
        File f = new File("rank_list_test.db");
        if(f.exists()) f.delete();
        dbManager = new DBManager("rank_list_test.db");
        listManager = new ListManager(dbManager);
        dbManager.addUser(new User("00000001", "testuser", "Pass123!", "test@email.com"));
        int emptyListID = listManager.createList("Empty List", "00000001", false);
        String result10 = listManager.rankList(emptyListID, "00000001", Arrays.asList("Pizza"));
        assertEquals("TC10: List with no items → list has no items", "list has no items", result10);
        teardown();

        // Summary
        System.out.println("\n========================================");
        System.out.println("RANK LIST RESULTS: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");
        System.out.println("========================================\n");
    }
}
