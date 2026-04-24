import java.io.File;

/**
 * EditListTest
 * Use Case: Edit List (R22 - only the author can edit their own list)
 *
 * Tests the editList(userID, listID, newTitle, newAccess) method of ListManager
 * following the Kung book test case analysis (Figures 20.13-20.15).
 */
public class EditListTest {
    static int passed = 0;
    static int failed = 0;
    static DBManager dbManager;
    static ListManager listManager;

    static void assertEquals(String testName, String expected, String actual) {
        if (expected.equals(actual)) {
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName + " (expected \"" + expected + "\", got \"" + actual + "\")");
            failed++;
        }
    }

    static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName);
            failed++;
        }
    }

    static void setup() {
        File f = new File("edit_list_test.db");
        if (f.exists()) f.delete();
        dbManager = new DBManager("edit_list_test.db");
        listManager = new ListManager(dbManager);
        dbManager.addUser(new User("00000001", "authoruser", "Pass123!", "author@email.com"));
        dbManager.addUser(new User("00000002", "otheruser", "Pass456!", "other@email.com"));
    }

    static void teardown() {
        dbManager.close();
        File f = new File("edit_list_test.db");
        if (f.exists()) f.delete();
    }

    public static void main(String[] args) {
        System.out.println("\n=== EDIT LIST TESTS ===\n");

        // ============================
        // SECTION 1: Valid edits by the author
        // ============================

        // TC1: Author edits title, keeps private → edit successful
        setup();
        int id1 = listManager.createList("Old Title", "00000001", false);
        String result1 = listManager.editList("00000001", id1, "Best Foods 2.0", false);
        assertEquals("TC1: Author edits title (private) → edit successful", "edit successful", result1);
        teardown();

        // TC1b: New title is actually persisted in DB
        setup();
        int id1b = listManager.createList("Old Title", "00000001", false);
        listManager.editList("00000001", id1b, "Best Foods 2.0", false);
        EverythingList updated1b = dbManager.getList(id1b);
        assertTrue("TC1b: New title persisted in DB", updated1b != null && updated1b.getTitle().equals("Best Foods 2.0"));
        teardown();

        // TC2: Author toggles private → public → edit successful
        setup();
        int id2 = listManager.createList("My List", "00000001", false);
        String result2 = listManager.editList("00000001", id2, "My List", true);
        assertEquals("TC2: Author toggles private→public → edit successful", "edit successful", result2);
        teardown();

        // TC2b: Access updated to public in DB
        setup();
        int id2b = listManager.createList("My List", "00000001", false);
        listManager.editList("00000001", id2b, "My List", true);
        EverythingList updated2b = dbManager.getList(id2b);
        assertTrue("TC2b: Access updated to public in DB", updated2b != null && updated2b.getAccess() == true);
        teardown();

        // TC3: Author toggles public → private → edit successful
        setup();
        int id3 = listManager.createList("Public List", "00000001", true);
        String result3 = listManager.editList("00000001", id3, "Public List", false);
        assertEquals("TC3: Author toggles public→private → edit successful", "edit successful", result3);
        teardown();

        // TC3b: Access updated to private in DB
        setup();
        int id3b = listManager.createList("Public List", "00000001", true);
        listManager.editList("00000001", id3b, "Public List", false);
        EverythingList updated3b = dbManager.getList(id3b);
        assertTrue("TC3b: Access updated to private in DB", updated3b != null && updated3b.getAccess() == false);
        teardown();

        // TC4: Author changes title AND access in one call
        setup();
        int id4 = listManager.createList("Old Title", "00000001", false);
        listManager.editList("00000001", id4, "Brand New Title", true);
        EverythingList updated4 = dbManager.getList(id4);
        assertTrue("TC4: Title updated when both changed together", updated4 != null && updated4.getTitle().equals("Brand New Title"));
        assertTrue("TC4b: Access updated to public when both changed together", updated4 != null && updated4.getAccess() == true);
        teardown();

        // ============================
        // SECTION 2: Authorization (R22)
        // ============================

        // TC5: Non-author attempts edit → unauthorized
        setup();
        int id5 = listManager.createList("Author's List", "00000001", false);
        String result5 = listManager.editList("00000002", id5, "Hacked Title", true);
        assertEquals("TC5: Non-author edit → unauthorized", "unauthorized", result5);
        teardown();

        // TC5b: Title is unchanged after unauthorized attempt
        setup();
        int id5b = listManager.createList("Author's List", "00000001", false);
        listManager.editList("00000002", id5b, "Hacked Title", true);
        EverythingList unchanged5b = dbManager.getList(id5b);
        assertTrue("TC5b: Title unchanged after unauthorized attempt", unchanged5b != null && unchanged5b.getTitle().equals("Author's List"));
        teardown();

        // TC5c: Access is unchanged after unauthorized attempt
        setup();
        int id5c = listManager.createList("Author's List", "00000001", false);
        listManager.editList("00000002", id5c, "Hacked Title", true);
        EverythingList unchanged5c = dbManager.getList(id5c);
        assertTrue("TC5c: Access unchanged after unauthorized attempt", unchanged5c != null && unchanged5c.getAccess() == false);
        teardown();

        // ============================
        // SECTION 3: Invalid/Exceptional userID values
        // ============================

        // TC6: Null userID → invalid user
        setup();
        int id6 = listManager.createList("Some List", "00000001", false);
        assertEquals("TC6: Null userID → invalid user", "invalid user", listManager.editList(null, id6, "New Title", false));
        teardown();

        // TC7: Empty userID → invalid user
        setup();
        int id7 = listManager.createList("Some List", "00000001", false);
        assertEquals("TC7: Empty userID → invalid user", "invalid user", listManager.editList("", id7, "New Title", false));
        teardown();

        // TC7b: Whitespace-only userID → invalid user
        setup();
        int id7b = listManager.createList("Some List", "00000001", false);
        assertEquals("TC7b: Whitespace userID → invalid user", "invalid user", listManager.editList("   ", id7b, "New Title", false));
        teardown();

        // ============================
        // SECTION 4: Invalid/Exceptional title values
        // ============================

        // TC8: Null title → invalid title
        setup();
        int id8 = listManager.createList("Some List", "00000001", false);
        assertEquals("TC8: Null title → invalid title", "invalid title", listManager.editList("00000001", id8, null, false));
        teardown();

        // TC9: Empty title → invalid title
        setup();
        int id9 = listManager.createList("Some List", "00000001", false);
        assertEquals("TC9: Empty title → invalid title", "invalid title", listManager.editList("00000001", id9, "", false));
        teardown();

        // TC9b: Whitespace-only title → invalid title
        setup();
        int id9b = listManager.createList("Some List", "00000001", false);
        assertEquals("TC9b: Whitespace-only title → invalid title", "invalid title", listManager.editList("00000001", id9b, "  ", false));
        teardown();

        // ============================
        // SECTION 5: Invalid/Exceptional listID values
        // ============================

        // TC10: Non-existent listID → list not found
        setup();
        assertEquals("TC10: Non-existent listID → list not found", "list not found", listManager.editList("00000001", 9999, "Title", false));
        teardown();

        // TC11: Negative listID → list not found
        setup();
        assertEquals("TC11: Negative listID → list not found", "list not found", listManager.editList("00000001", -1, "Title", false));
        teardown();

        // Summary
        System.out.println("\n========================================");
        System.out.println("EDIT LIST RESULTS: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");
        System.out.println("========================================\n");
    }
}
