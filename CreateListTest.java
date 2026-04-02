import java.io.File;

/**
 * CreateListTest
 * Use Case: UC6 - Create List
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
        // Delete test db if it exists so each run is fresh
        File f = new File("create_list_test.db");
        if(f.exists()) f.delete();

        dbManager = new DBManager("create_list_test.db");
        listManager = new ListManager(dbManager);

        // Seed a user
        dbManager.addUser(new User("00000001", "testuser", "Pass123!", "test@email.com"));
    }

    static void teardown(){
        dbManager.close();
        File f = new File("create_list_test.db");
        if(f.exists()) f.delete();
    }

    public static void main(String args[]){
        System.out.println("\n=== UC6 - CREATE LIST TESTS ===\n");

        // TC1: Valid title, valid author, private access → list created
        setup();
        int id1 = listManager.createList("Best Foods", "00000001", false);
        assertTrue("TC1: Valid title + valid author + private → returns positive ID", id1 > 0);
        teardown();

        // TC1b: Verify list is stored in database
        setup();
        int id1b = listManager.createList("Best Foods", "00000001", false);
        EverythingList retrieved = dbManager.getList(id1b);
        assertTrue("TC1b: Created list can be retrieved from DB", retrieved != null);
        assertTrue("TC1c: Retrieved list has correct title", retrieved != null && retrieved.getTitle().equals("Best Foods"));
        assertTrue("TC1d: Retrieved list has correct author", retrieved != null && retrieved.getAuthorID().equals("00000001"));
        assertTrue("TC1e: Retrieved list has correct access (private)", retrieved != null && retrieved.getAccess() == false);
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

        // Summary
        System.out.println("\n========================================");
        System.out.println("CREATE LIST RESULTS: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");
        System.out.println("========================================\n");
    }
}
