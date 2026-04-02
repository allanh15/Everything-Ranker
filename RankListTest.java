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

        // ============================
        // SECTION 6: Skip Item tests ("skip X" button under each card)
        // ============================

        // TC27: Skip an item removes it from the session
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        assertTrue("TC27: 3 items before skip", ranker.getItems().size() == 3);
        boolean skipped27 = ranker.skipItem("Pizza");
        assertTrue("TC27b: skipItem returns true", skipped27 == true);
        assertTrue("TC27c: 2 items remain after skip", ranker.getItems().size() == 2);
        teardown();

        // TC28: Skipped item does not appear in final ranking
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        ranker.skipItem("Pizza");
        // Complete remaining comparisons (2 items = 1 pair)
        while(ranker.getNextPair() != null){
            Item[] p = ranker.getNextPair();
            ranker.recordChoice(listID, p[0]);
        }
        List<String> ranking28 = ranker.computeAndSaveRanking("00000001");
        assertTrue("TC28: Final ranking has 2 items (Pizza excluded)", ranking28 != null && ranking28.size() == 2);
        boolean pizzaInRanking = false;
        for(String name : ranking28) if(name.equals("Pizza")) pizzaInRanking = true;
        assertTrue("TC28b: Pizza not in final ranking", pizzaInRanking == false);
        teardown();

        // TC29: Skipped item appears in skippedItems list
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        ranker.skipItem("Sushi");
        assertTrue("TC29: Skipped items list has 1 entry", ranker.getSkippedItems().size() == 1);
        assertTrue("TC29b: Skipped item is Sushi", ranker.getSkippedItems().get(0).getName().equals("Sushi"));
        teardown();

        // TC30: Skip invalid item name returns false
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        boolean badSkip = ranker.skipItem("Burger");
        assertTrue("TC30: skipItem with non-existent name → false", badSkip == false);
        assertTrue("TC30b: Items unchanged", ranker.getItems().size() == 3);
        teardown();

        // TC31: Skip null item returns false
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        boolean nullSkip = ranker.skipItem(null);
        assertTrue("TC31: skipItem(null) → false", nullSkip == false);
        teardown();

        // TC32: Skip all but one item ends session
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        ranker.skipItem("Pizza");
        ranker.skipItem("Sushi");
        assertTrue("TC32: Session ends when only 1 item left", ranker.isSessionActive() == false);
        assertTrue("TC32b: 1 item remains", ranker.getItems().size() == 1);
        teardown();

        // ============================
        // SECTION 7: Skip Pair / Next tests ("next" button top-right)
        // ============================

        // TC33: skipPair advances without recording a win
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        Item[] pair33 = ranker.getNextPair();
        String item1Name = pair33[0].getName();
        String item2Name = pair33[1].getName();
        boolean skippedPair = ranker.skipPair();
        assertTrue("TC33: skipPair returns true", skippedPair == true);
        assertTrue("TC33b: Session still active", ranker.isSessionActive() || ranker.isComplete());
        teardown();

        // TC34: Skipped pairs don't count as wins in final ranking
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        ranker.skipPair(); // skip first pair
        ranker.skipPair(); // skip second pair
        ranker.skipPair(); // skip third pair — all skipped
        assertTrue("TC34: Session complete after all pairs skipped", ranker.isComplete());
        List<String> ranking34 = ranker.computeAndSaveRanking("00000001");
        assertTrue("TC34b: All 3 items in ranking (just tied)", ranking34 != null && ranking34.size() == 3);
        teardown();

        // TC35: skipPair when no active session returns false
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        boolean badSkipPair = ranker.skipPair();
        assertTrue("TC35: skipPair with no session → false", badSkipPair == false);
        teardown();

        // ============================
        // SECTION 8: Undo tests ("undo" button top-left)
        // ============================

        // TC36: Undo a choice restores the previous pair
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        Item[] pair36 = ranker.getNextPair();
        String firstItemName = pair36[0].getName();
        String secondItemName = pair36[1].getName();
        ranker.recordChoice(listID, pair36[0]);
        boolean undone36 = ranker.undo();
        assertTrue("TC36: undo returns true", undone36 == true);
        Item[] restored36 = ranker.getNextPair();
        assertTrue("TC36b: Same pair restored after undo",
            restored36[0].getName().equals(firstItemName) &&
            restored36[1].getName().equals(secondItemName));
        assertTrue("TC36c: Session is active again", ranker.isSessionActive());
        teardown();

        // TC37: Undo a skipPair restores the pair
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        Item[] pair37 = ranker.getNextPair();
        String p37name1 = pair37[0].getName();
        String p37name2 = pair37[1].getName();
        ranker.skipPair();
        ranker.undo();
        Item[] restored37 = ranker.getNextPair();
        assertTrue("TC37: Pair restored after undo of skipPair",
            restored37[0].getName().equals(p37name1) &&
            restored37[1].getName().equals(p37name2));
        teardown();

        // TC38: Undo a skipItem restores the item
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        ranker.skipItem("Tacos");
        assertTrue("TC38: 2 items after skip", ranker.getItems().size() == 2);
        ranker.undo();
        assertTrue("TC38b: 3 items restored after undo", ranker.getItems().size() == 3);
        assertTrue("TC38c: Skipped items list is empty", ranker.getSkippedItems().size() == 0);
        assertTrue("TC38d: Session is active again", ranker.isSessionActive());
        teardown();

        // TC39: Undo with no history returns false
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        boolean badUndo = ranker.undo();
        assertTrue("TC39: undo with no history → false", badUndo == false);
        teardown();

        // TC40: canUndo reflects history state
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        assertTrue("TC40: canUndo is false at start", ranker.canUndo() == false);
        ranker.recordChoice(listID, ranker.getNextPair()[0]);
        assertTrue("TC40b: canUndo is true after choice", ranker.canUndo() == true);
        ranker.undo();
        assertTrue("TC40c: canUndo is false after undo", ranker.canUndo() == false);
        teardown();

        // TC41: Multiple undos in sequence
        listID = setupWithItems();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        Item[] p41a = ranker.getNextPair();
        ranker.recordChoice(listID, p41a[0]); // choice 1
        Item[] p41b = ranker.getNextPair();
        ranker.recordChoice(listID, p41b[0]); // choice 2
        ranker.undo(); // undo choice 2
        ranker.undo(); // undo choice 1
        Item[] restored41 = ranker.getNextPair();
        assertTrue("TC41: Two undos restore to first pair",
            restored41[0].getName().equals(p41a[0].getName()) &&
            restored41[1].getName().equals(p41a[1].getName()));
        teardown();

        // ============================
        // SECTION 9: ERGUI skip/undo/next display tests
        // ============================

        // TC42: ERGUI.displaySkipOptions captures both items
        listID = setupWithItems();
        gui = new ERGUI();
        ranker = new Ranker(dbManager);
        ranker.startRankingSession(listID);
        Item[] pair42 = ranker.getNextPair();
        gui.displaySkipOptions(pair42[0], pair42[1]);
        assertTrue("TC42: Skip options visible", gui.isSkipOptionsVisible());
        assertTrue("TC42b: Item1 captured", gui.getLastItem1() != null);
        assertTrue("TC42c: Item2 captured", gui.getLastItem2() != null);
        teardown();

        // TC43: ERGUI.displayUndoAvailable tracks state
        gui = new ERGUI();
        gui.displayUndoAvailable(false);
        assertTrue("TC43: Undo initially disabled", gui.isUndoAvailable() == false);
        gui.displayUndoAvailable(true);
        assertTrue("TC43b: Undo enabled after action", gui.isUndoAvailable() == true);

        // TC44: ERGUI.displayItemSkipped captures skipped item name
        gui = new ERGUI();
        gui.displayItemSkipped("Pizza");
        assertTrue("TC44: Last skipped item is Pizza", "Pizza".equals(gui.getLastSkippedItem()));
        assertTrue("TC44b: Message confirms removal", gui.getLastMessage().contains("removed"));

        // TC45: ERGUI.displayPairSkipped sets message
        gui = new ERGUI();
        gui.displayPairSkipped();
        assertTrue("TC45: Pair skipped message set", "Pair skipped".equals(gui.getLastMessage()));

        // TC46: ERGUI.displayUndoConfirmed sets message
        gui = new ERGUI();
        gui.displayUndoConfirmed();
        assertTrue("TC46: Undo confirmed message set", gui.getLastMessage().contains("undone"));

        // Summary
        System.out.println("\n========================================");
        System.out.println("RANK LIST RESULTS: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");
        System.out.println("========================================\n");
    }
}
