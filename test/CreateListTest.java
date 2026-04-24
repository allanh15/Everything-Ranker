import java.io.File;

/**
 * CreateListTest
 * Use Case: UC19/R19 - Create List
 *
 * Tests the createList(title, authorID, access) method of ListManager
 * following the Kung book test case analysis (Figures 20.13-20.15).
 */
public class CreateListTest {
    static int passed = 0;
    static int failed = 0;
    static DBManager dbManager;
    static ListManager listManager;

    static void assertEquals(String testName, int expected, int actual){
        if(expected == actual){
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName + " (expected " + expected + ", got " + actual + ")");
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

    static void setup(){
        File f = new File("create_list_test.db");
        if(f.exists()) f.delete();

        dbManager = new DBManager("create_list_test.db");
        listManager = new ListManager(dbManager);

        dbManager.addUser(new User("00000001", "testuser", "Pass123!", "test@email.com"));
    }

    static void teardown(){
        dbManager.close();
        File f = new File("create_list_test.db");
        if(f.exists()) f.delete();
    }

    public static void main(String args[]){
        System.out.println("\n=== UC19/R19 - CREATE LIST TESTS ===\n");

        // TC1: Valid title, valid author, private access → list created
        setup();
        int id1 = listManager.createList("Best Foods", "00000001", false);
        assertTrue("TC1: Valid title + valid author + private → returns positive ID", id1 > 0);
        teardown();

        // TC1b-e: Verify all stored attributes
        setup();
        int id1b = listManager.createList("Best Foods", "00000001", false);
        EverythingList retrieved = dbManager.getList(id1b);
        assertTrue("TC1b: Created list can be retrieved from DB", retrieved != null);
        assertTrue("TC1c: Retrieved list has correct title", retrieved != null && retrieved.getTitle().equals("Best Foods"));
        assertTrue("TC1d: Retrieved list has correct author", retrieved != null && retrieved.getAuthorID().equals("00000001"));
        assertTrue("TC1e: Retrieved list has correct access (private)", retrieved != null && retrieved.getAccess() == false);
        assertTrue("TC1f: Retrieved list has pubDate set", retrieved != null && retrieved.getPubDate() != null);
        assertTrue("TC1g: Retrieved list has update date set", retrieved != null && retrieved.getUpdate() != null);
        teardown();

        // TC2: Valid title, valid author, public access → list created
        setup();
        int id2 = listManager.createList("Top Movies", "00000001", true);
        assertTrue("TC2: Valid title + valid author + public → returns positive ID", id2 > 0);
        EverythingList pubList = dbManager.getList(id2);
        assertTrue("TC2b: Public list has access = true", pubList != null && pubList.getAccess() == true);
        teardown();

        // TC3: Empty title → creation failed
        setup();
        int id3 = listManager.createList("", "00000001", false);
        assertEquals("TC3: Empty title → returns -1", -1, id3);
        teardown();

        // TC3b: Whitespace-only title
        setup();
        int id3b = listManager.createList("   ", "00000001", false);
        assertEquals("TC3b: Whitespace-only title → returns -1", -1, id3b);
        teardown();

        // TC4: Valid title, empty author → creation failed
        setup();
        int id4 = listManager.createList("Best Foods", "", false);
        assertEquals("TC4: Empty authorID → returns -1", -1, id4);
        teardown();

        // TC5: Null title, null author → creation failed
        setup();
        int id5 = listManager.createList(null, null, false);
        assertEquals("TC5: Null title + null author → returns -1", -1, id5);
        teardown();

        // TC5b: Null title only
        setup();
        int id5b = listManager.createList(null, "00000001", false);
        assertEquals("TC5b: Null title → returns -1", -1, id5b);
        teardown();

        // TC5c: Null author only
        setup();
        int id5c = listManager.createList("Best Foods", null, false);
        assertEquals("TC5c: Null authorID → returns -1", -1, id5c);
        teardown();

        // TC6: Multiple lists can be created
        setup();
        int idA = listManager.createList("List A", "00000001", false);
        int idB = listManager.createList("List B", "00000001", true);
        assertTrue("TC6: Two lists created with different IDs", idA > 0 && idB > 0 && idA != idB);
        teardown();

        // TC7: Created list is associated with the creator (R19)
        setup();
        int id7 = listManager.createList("My List", "00000001", false);
        String author = dbManager.getAuthor(id7);
        assertTrue("TC7: List author matches creator (R19)", "00000001".equals(author));
        teardown();

        // TC8: R22 — non-author cannot delete another user's list
        setup();
        dbManager.addUser(new User("00000002", "otheruser", "Pass456!", "other@email.com"));
        int id8 = listManager.createList("Protected List", "00000001", false);
        boolean deleted = listManager.deleteList("00000002", id8);
        assertTrue("TC8: Non-author cannot delete list (R22)", deleted == false);
        EverythingList stillExists = dbManager.getList(id8);
        assertTrue("TC8b: List still exists after unauthorized delete (R22)", stillExists != null);
        teardown();

        // TC9: Author can delete their own list
        setup();
        int id9 = listManager.createList("Deletable List", "00000001", false);
        boolean del9 = listManager.deleteList("00000001", id9);
        assertTrue("TC9: Author can delete their own list", del9 == true);
        EverythingList gone = dbManager.getList(id9);
        assertTrue("TC9b: List is gone after author deletes it", gone == null);
        teardown();

        // Summary
        System.out.println("\n========================================");
        System.out.println("CREATE LIST RESULTS: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");
        System.out.println("========================================\n");
    }
}
