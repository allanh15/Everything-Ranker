import java.io.File;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * RankListTest
 * Use Case: UC7 - Rank List
 *
 * Tests the ranking flow: AccountManager access check, Ranker pairwise session,
 * ListManager.rankList(), ERGUI display, and DBManager persistence.
 * Follows Kung book test case analysis (Figures 20.13-20.15).
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
        File f = new File("rank_list_test.db");
        if(f.exists()) f.delete();

        dbManager = new DBManager("rank_list_test.db");
        listManager = new ListManager(dbManager);

        dbManager.addUser(new User("00000001", "testuser", "Pass123!", "test@email.com"));
        dbManager.addUser(new User("00000002", "otheruser", "Pass456!", "other@email.com"));

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
        System.out.println("\n=== UC7 - RANK LIST TESTS ===\n");

        // ============================
        // SECTION 1: ListManager.rankList() core tests
        // ============================

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
        String result2 = listManager.rankList(999, "00000001", Arrays.asList("Pizza"));
        assertEquals("TC2: Non-existent list → list not found", "list not found", result2);
        teardown();

        // TC3: Exceptional authorID (empty string) → invalid author
        listID = setupWithItems();
        String result3 = listManager.rankList(listID, "", Arrays.asList("Pizza", "Sushi", "Tacos"));
        assertEquals("TC3: Empty authorID → invalid author", "invalid author", result3);
        teardown();

        // TC3b: Null authorID → invalid author
        listID = setupWithItems();
        String result3b = listManager.rankList(listID, null, Arrays.asList("Pizza"));
        assertEquals("TC3b: Null authorID → invalid author", "invalid author", result3b);
        teardown();

        // TC4: Ranked items contain an item not in the list → item not in list
        listID = setupWithItems();
        String result4 = listManager.rankList(listID, "00000001", Arrays.asList("Pizza", "Sushi", "Burger"));
        assertEquals("TC4: Item not in list → item not in list", "item not in list", result4);
        teardown();

        // TC5: Duplicate items in ranking → duplicate item in ranking
        listID = setupWithItems();
        String result5 = listManager.rankList(listID, "00000001", Arrays.asList("Pizza", "Sushi", "Pizza"));
        assertEquals("TC5: Duplicate items → duplicate item in ranking", "duplicate item in ranking", result5);
        teardown();

        // TC6: Empty ranked items list → invalid ranking
        listID = setupWithItems();
        String result6 = listManager.rankList(listID, "00000001", new ArrayList<>());
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

        // ============================
        // SECTION 2: AccountManager.validateRankAccess() tests (UC7 sequence step 1)
        // ============================

        // TC11: Author has access to rank their own private list
        listID = setupWithItems();
        AccountManager am = new AccountManager(dbManager);
        boolean access11 = am.validateRankAccess("00000001", listID);
        assertTrue("TC11: Author has access to rank own private list", access11 == true);
        teardown();

        // TC12: Non-author cannot rank a private list
        listID = setupWithItems();
        am = new AccountManager(dbManager);
        boolean access12 = am.validateRankAccess("00000002", listID);
        assertTrue("TC12: Non-author denied access to private list", access12 == false);
        teardown();

        // TC13: Non-author CAN rank a public list
        f = new File("rank_list_test.db");
        if(f.exists()) f.delete();
        dbManager = new DBManager("rank_list_test.db");
        listManager = new ListManager(dbManager);
        dbManager.addUser(new User("00000001", "testuser", "Pass123!", "test@email.com"));
        dbManager.addUser(new User("00000002", "otheruser", "Pass456!", "other@email.com"));
        int pubListID = listManager.createList("Public Foods", "00000001", true);
        am = new AccountManager(dbManager);
        boolean access13 = am.validateRankAccess("00000002", pubListID);
        assertTrue("TC13: Non-author can access public list", access13 == true);
        teardown();

        // TC14: Null userID → access denied
        listID = setupWithItems();
        am = new AccountManager(dbManager);
        boolean access14 = am.validateRankAccess(null, listID);
        assertTrue("TC14: Null userID → access denied", access14 == false);
        teardown();

        // TC15: Non-existent list → access denied
        listID = setupWithItems();
        am = new AccountManager(dbManager);
        boolean access15 = am.validateRankAccess("00000001", 999);
        assertTrue("TC15: Non-existent list → access denied", access15 == false);
        teardown();

        // ============================
        // SECTION 3: Ranker pairwise session tests (UC7 sequence steps 2-5)
        // ============================

        // TC16: Ranker.startRankingSession loads items
        listID = setupWithItems();
        Ranker ranker = new Ranker(dbManager);
        boolean started = ranker.startRankingSession(listID);
        assertTrue("TC16: startRankingSession returns true for valid list", started == true);
        assertTrue("TC16b: Ranker loaded 3 items", ranker.getItems().size() == 3);
        teardown();

        // TC17: getNextPair returns a pair
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        Item[] pair = ranker.getNextPair();
        assertTrue("TC17: getNextPair returns non-null pair", pair != null && pair.length == 2);
        assertTrue("TC17b: Pair contains two different items", !pair[0].getName().equals(pair[1].getName()));
        teardown();

        // TC18: recordChoice advances to next pair
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        Item[] firstPair = ranker.getNextPair();
        boolean recorded = ranker.recordChoice(listID, firstPair[0]);
        assertTrue("TC18: recordChoice returns true", recorded == true);
        assertTrue("TC18b: Session still active after first choice", ranker.isSessionActive() || ranker.isComplete());
        teardown();

        // TC19: Full pairwise session → computeAndSaveRanking
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        // Compare all pairs (3 items = 3 pairs)
        while(ranker.getNextPair() != null){
            Item[] p = ranker.getNextPair();
            ranker.recordChoice(listID, p[0]); // always pick first item
        }
        assertTrue("TC19: Session complete after all pairs", ranker.isComplete());
        List<String> finalRanking = ranker.computeAndSaveRanking("00000001");
        assertTrue("TC19b: computeAndSaveRanking returns ranked list", finalRanking != null && finalRanking.size() == 3);
        List<String> dbRanking = dbManager.getRankings(listID, "00000001");
        assertTrue("TC19c: Rankings saved to DB", dbRanking.size() == 3);
        teardown();

        // TC20: startRankingSession fails for non-existent list
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        boolean badStart = ranker.startRankingSession(999);
        assertTrue("TC20: startRankingSession fails for non-existent list", badStart == false);
        teardown();

        // ============================
        // SECTION 4: ERGUI display tests (UC7 sequence steps 4, 7)
        // ============================

        // TC21: ERGUI.displayNextPair captures items
        listID = setupWithItems();
        ERGUI gui = new ERGUI();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        Item[] displayPair = ranker.getNextPair();
        gui.displayNextPair(displayPair[0], displayPair[1]);
        assertTrue("TC21: ERGUI captures item1", gui.getLastItem1() != null);
        assertTrue("TC21b: ERGUI captures item2", gui.getLastItem2() != null);
        assertTrue("TC21c: ERGUI page is 'ranking'", "ranking".equals(gui.getPage()));
        teardown();

        // TC22: ERGUI.presentFinalResults captures ranked list
        listID = setupWithItems();
        gui = new ERGUI();
        List<String> results22 = Arrays.asList("Tacos", "Pizza", "Sushi");
        gui.presentFinalResults(listID, results22);
        assertTrue("TC22: ERGUI captures final results", gui.getLastResults() != null && gui.getLastResults().size() == 3);
        assertTrue("TC22b: ERGUI page is 'results'", "results".equals(gui.getPage()));
        teardown();

        // TC23: ERGUI.displayConfirmation works
        ERGUI gui23 = new ERGUI();
        gui23.displayConfirmation("Ranking saved!");
        assertTrue("TC23: displayConfirmation sets message", "Ranking saved!".equals(gui23.getLastMessage()));
        assertTrue("TC23b: page is 'confirmation'", "confirmation".equals(gui23.getPage()));

        // TC24: ERGUI.displayError works
        ERGUI gui24 = new ERGUI();
        gui24.displayError("Access denied");
        assertTrue("TC24: displayError sets message", "Access denied".equals(gui24.getLastMessage()));
        assertTrue("TC24b: page is 'error'", "error".equals(gui24.getPage()));

        // ============================
        // SECTION 5: DBManager.updateRankedList and retrieveListItems (UC7 sequence steps 3, 6)
        // ============================

        // TC25: retrieveListItems returns items
        listID = setupWithItems();
        List<String> items25 = dbManager.retrieveListItems(listID);
        assertTrue("TC25: retrieveListItems returns 3 items", items25.size() == 3);
        teardown();

        // TC26: updateRankedList saves ranking data
        listID = setupWithItems();
        List<String> rankData = Arrays.asList("Sushi", "Tacos", "Pizza");
        boolean updated = dbManager.updateRankedList(listID, "00000001", rankData);
        assertTrue("TC26: updateRankedList returns true", updated == true);
        List<String> verify26 = dbManager.getRankings(listID, "00000001");
        assertTrue("TC26b: Rankings match input order",
            verify26.size() == 3 &&
            verify26.get(0).equals("Sushi") &&
            verify26.get(1).equals("Tacos") &&
            verify26.get(2).equals("Pizza"));
        teardown();

        // Summary
        System.out.println("\n========================================");
        System.out.println("RANK LIST RESULTS: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");
        System.out.println("========================================\n");
    }
}
