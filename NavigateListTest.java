import java.io.File;
import java.util.List;

/**
 * NavigateListTest
 * Use Case: Navigate List (browse / search public lists)
 *
 * Tests the browsePublicLists() and searchLists(titleQuery, authorUsername)
 * methods of ListManager, following the Kung book test case analysis
 * (Figures 20.13-20.15).
 */
public class NavigateListTest {
    static int passed = 0;
    static int failed = 0;
    static DBManager dbManager;
    static ListManager listManager;

    static void assertEquals(String testName, int expected, int actual) {
        if (expected == actual) {
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName + " (expected " + expected + ", got " + actual + ")");
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
        File f = new File("navigate_list_test.db");
        if (f.exists()) f.delete();
        dbManager = new DBManager("navigate_list_test.db");
        listManager = new ListManager(dbManager);
        dbManager.addUser(new User("00000001", "alice", "Pass123!", "alice@email.com"));
        dbManager.addUser(new User("00000002", "bob",   "Pass456!", "bob@email.com"));
    }

    static void teardown() {
        dbManager.close();
        File f = new File("navigate_list_test.db");
        if (f.exists()) f.delete();
    }

    /**
     * Seeds 4 public + 1 private list across two authors:
     *   alice (00000001): "Best Foods" (pub), "Worst Foods" (pub), "Top Movies" (pub), "Secret List" (priv)
     *   bob   (00000002): "My Picks" (pub)
     */
    static void seedFiveLists() {
        listManager.createList("Best Foods",  "00000001", true);
        listManager.createList("Worst Foods", "00000001", true);
        listManager.createList("Top Movies",  "00000001", true);
        listManager.createList("Secret List", "00000001", false);
        listManager.createList("My Picks",    "00000002", true);
    }

    static boolean containsTitle(List<EverythingList> lists, String title) {
        for (EverythingList l : lists) {
            if (l.getTitle().equals(title)) return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println("\n=== NAVIGATE LIST TESTS ===\n");

        // ============================================================
        // SECTION 1: browsePublicLists() — returns only public lists
        // ============================================================

        // TC1: No lists in DB → empty (not null)
        setup();
        List<EverythingList> r1 = listManager.browsePublicLists();
        assertTrue("TC1: Empty DB → non-null result", r1 != null);
        assertEquals("TC1b: Empty DB → 0 lists", 0, r1.size());
        teardown();

        // TC2: Only private lists exist → empty
        setup();
        listManager.createList("Private 1", "00000001", false);
        listManager.createList("Private 2", "00000002", false);
        List<EverythingList> r2 = listManager.browsePublicLists();
        assertEquals("TC2: Only private lists exist → 0 returned", 0, r2.size());
        teardown();

        // TC3: Mix of public and private → only public returned
        setup();
        seedFiveLists();
        List<EverythingList> r3 = listManager.browsePublicLists();
        assertEquals("TC3: 4 public + 1 private → 4 returned", 4, r3.size());
        teardown();

        // TC4: Every list returned has access = true
        setup();
        seedFiveLists();
        List<EverythingList> r4 = listManager.browsePublicLists();
        boolean allPublic = true;
        for (EverythingList l : r4) {
            if (!l.getAccess()) { allPublic = false; break; }
        }
        assertTrue("TC4: All returned lists have access=true", allPublic);
        teardown();

        // TC5: Private list "Secret List" never appears in browse results
        setup();
        seedFiveLists();
        List<EverythingList> r5 = listManager.browsePublicLists();
        assertTrue("TC5: Private 'Secret List' excluded from browse", !containsTitle(r5, "Secret List"));
        teardown();

        // TC6: Public lists from multiple authors are all included
        setup();
        seedFiveLists();
        List<EverythingList> r6 = listManager.browsePublicLists();
        assertTrue("TC6a: Browse includes alice's 'Best Foods'", containsTitle(r6, "Best Foods"));
        assertTrue("TC6b: Browse includes bob's 'My Picks'",     containsTitle(r6, "My Picks"));
        teardown();

        // ============================================================
        // SECTION 2: searchLists() — no filters → all public lists
        // ============================================================

        // TC7: Both filters null → all public lists (4)
        setup();
        seedFiveLists();
        List<EverythingList> r7 = listManager.searchLists(null, null);
        assertEquals("TC7: searchLists(null, null) → 4 public lists", 4, r7.size());
        assertTrue("TC7b: Private list excluded when both filters null", !containsTitle(r7, "Secret List"));
        teardown();

        // TC8: Both filters empty string → all public lists (4)
        setup();
        seedFiveLists();
        List<EverythingList> r8 = listManager.searchLists("", "");
        assertEquals("TC8: searchLists(\"\", \"\") → 4 public lists", 4, r8.size());
        teardown();

        // TC9: Both filters whitespace-only → treated as no filter
        setup();
        seedFiveLists();
        List<EverythingList> r9 = listManager.searchLists("   ", "   ");
        assertEquals("TC9: searchLists(whitespace, whitespace) → 4 public lists", 4, r9.size());
        teardown();

        // ============================================================
        // SECTION 3: searchLists() — title filter only
        // ============================================================

        // TC10: Title substring "Foods" → 2 matches ("Best Foods", "Worst Foods")
        setup();
        seedFiveLists();
        List<EverythingList> r10 = listManager.searchLists("Foods", null);
        assertEquals("TC10: Title 'Foods' → 2 matches", 2, r10.size());
        assertTrue("TC10b: 'Best Foods' matched",  containsTitle(r10, "Best Foods"));
        assertTrue("TC10c: 'Worst Foods' matched", containsTitle(r10, "Worst Foods"));
        teardown();

        // TC11: Title is case-insensitive — "foods" lowercase
        setup();
        seedFiveLists();
        List<EverythingList> r11 = listManager.searchLists("foods", null);
        assertEquals("TC11: Title 'foods' (lowercase) → 2 matches (case-insensitive)", 2, r11.size());
        teardown();

        // TC12: Title partial match — single result
        setup();
        seedFiveLists();
        List<EverythingList> r12 = listManager.searchLists("Movies", null);
        assertEquals("TC12: Title 'Movies' → 1 match", 1, r12.size());
        assertTrue("TC12b: 'Top Movies' matched", containsTitle(r12, "Top Movies"));
        teardown();

        // TC13: Title that matches nothing → empty
        setup();
        seedFiveLists();
        List<EverythingList> r13 = listManager.searchLists("xyz_no_match", null);
        assertEquals("TC13: Title with no matches → 0 results", 0, r13.size());
        teardown();

        // TC14: Title query never matches private lists
        setup();
        seedFiveLists();
        List<EverythingList> r14 = listManager.searchLists("Secret", null);
        assertEquals("TC14: Title 'Secret' (only private has it) → 0 results", 0, r14.size());
        teardown();

        // ============================================================
        // SECTION 4: searchLists() — author filter only
        // ============================================================

        // TC15: Author "alice" → her 3 PUBLIC lists (private excluded)
        setup();
        seedFiveLists();
        List<EverythingList> r15 = listManager.searchLists(null, "alice");
        assertEquals("TC15: Author 'alice' → 3 public lists (private excluded)", 3, r15.size());
        assertTrue("TC15b: 'Secret List' excluded for author filter", !containsTitle(r15, "Secret List"));
        teardown();

        // TC16: Author "bob" → his single public list
        setup();
        seedFiveLists();
        List<EverythingList> r16 = listManager.searchLists(null, "bob");
        assertEquals("TC16: Author 'bob' → 1 list", 1, r16.size());
        assertTrue("TC16b: 'My Picks' returned", containsTitle(r16, "My Picks"));
        teardown();

        // TC17: Non-existent author → empty
        setup();
        seedFiveLists();
        List<EverythingList> r17 = listManager.searchLists(null, "ghost_user");
        assertEquals("TC17: Non-existent author → 0 results", 0, r17.size());
        teardown();

        // ============================================================
        // SECTION 5: searchLists() — both filters combined
        // ============================================================

        // TC18: Title "Foods" + author "alice" → 2 matches (intersection)
        setup();
        seedFiveLists();
        List<EverythingList> r18 = listManager.searchLists("Foods", "alice");
        assertEquals("TC18: 'Foods' + 'alice' → 2 matches", 2, r18.size());
        teardown();

        // TC19: Title "Foods" + author "bob" → 0 (bob has no Foods list)
        setup();
        seedFiveLists();
        List<EverythingList> r19 = listManager.searchLists("Foods", "bob");
        assertEquals("TC19: 'Foods' + 'bob' → 0 matches", 0, r19.size());
        teardown();

        // TC20: Title "Picks" + author "bob" → 1 match
        setup();
        seedFiveLists();
        List<EverythingList> r20 = listManager.searchLists("Picks", "bob");
        assertEquals("TC20: 'Picks' + 'bob' → 1 match", 1, r20.size());
        assertTrue("TC20b: 'My Picks' returned", containsTitle(r20, "My Picks"));
        teardown();

        // TC21: Both filters target a private list → 0 (private excluded)
        setup();
        seedFiveLists();
        List<EverythingList> r21 = listManager.searchLists("Secret", "alice");
        assertEquals("TC21: 'Secret' + 'alice' (private list) → 0 results", 0, r21.size());
        teardown();

        // ============================================================
        // SECTION 6: searchLists() — exceptional / edge cases
        // ============================================================

        // TC22: Empty DB → searchLists returns empty (not null)
        setup();
        List<EverythingList> r22 = listManager.searchLists("anything", null);
        assertTrue("TC22: searchLists on empty DB → non-null", r22 != null);
        assertEquals("TC22b: searchLists on empty DB → 0 results", 0, r22.size());
        teardown();

        // TC23: Title filter trims whitespace before matching
        setup();
        seedFiveLists();
        List<EverythingList> r23 = listManager.searchLists("  Foods  ", null);
        assertEquals("TC23: Title with surrounding whitespace → 2 matches (trimmed)", 2, r23.size());
        teardown();

        // TC24: Returned list objects are populated (id > 0, title set)
        setup();
        seedFiveLists();
        List<EverythingList> r24 = listManager.searchLists("Best", "alice");
        assertEquals("TC24: 'Best' + 'alice' → 1 match", 1, r24.size());
        if (r24.size() == 1) {
            EverythingList found = r24.get(0);
            assertTrue("TC24b: Returned list has positive ID", found.getID() > 0);
            assertTrue("TC24c: Returned list has correct title",  found.getTitle().equals("Best Foods"));
            assertTrue("TC24d: Returned list has correct author", found.getAuthorID().equals("00000001"));
            assertTrue("TC24e: Returned list is public",          found.getAccess());
        }
        teardown();

        // ============================================================
        // SECTION 7: Asymmetric filter combinations + structural checks
        // ============================================================

        // TC25: Valid title + non-existent author → 0
        // (covers the early-return in searchLists when getUser returns null,
        //  with a non-null title that would otherwise match)
        setup();
        seedFiveLists();
        List<EverythingList> r25 = listManager.searchLists("Foods", "ghost_user");
        assertEquals("TC25: 'Foods' + non-existent author → 0 results", 0, r25.size());
        teardown();

        // TC26: Valid title + whitespace author → behaves as no-author filter
        // (matches by title only — proves whitespace handling is per-filter)
        setup();
        seedFiveLists();
        List<EverythingList> r26 = listManager.searchLists("Foods", "   ");
        assertEquals("TC26: 'Foods' + whitespace author → 2 matches (title-only)", 2, r26.size());
        assertTrue("TC26b: 'Best Foods' matched",  containsTitle(r26, "Best Foods"));
        assertTrue("TC26c: 'Worst Foods' matched", containsTitle(r26, "Worst Foods"));
        teardown();

        // TC27: Whitespace title + valid author → behaves as no-title filter
        // (matches by author only — proves whitespace handling is per-filter)
        setup();
        seedFiveLists();
        List<EverythingList> r27 = listManager.searchLists("   ", "alice");
        assertEquals("TC27: whitespace title + 'alice' → 3 matches (author-only)", 3, r27.size());
        assertTrue("TC27b: 'Secret List' still excluded (private)", !containsTitle(r27, "Secret List"));
        teardown();

        // TC28: Valid title + empty author → behaves as no-author filter
        setup();
        seedFiveLists();
        List<EverythingList> r28 = listManager.searchLists("Foods", "");
        assertEquals("TC28: 'Foods' + empty author → 2 matches (title-only)", 2, r28.size());
        teardown();

        // TC29: Author exists but owns only private lists → 0
        // (distinct from non-existent author: getUser succeeds, but no public matches)
        setup();
        dbManager.addUser(new User("00000003", "carol", "Pass789!", "carol@email.com"));
        listManager.createList("Carol's Private 1", "00000003", false);
        listManager.createList("Carol's Private 2", "00000003", false);
        List<EverythingList> r29 = listManager.searchLists(null, "carol");
        assertEquals("TC29: Existing author with only private lists → 0 results", 0, r29.size());
        teardown();

        // TC30: Author exists but owns no lists at all → 0
        setup();
        dbManager.addUser(new User("00000004", "dave", "PassXYZ!", "dave@email.com"));
        seedFiveLists();
        List<EverythingList> r30 = listManager.searchLists(null, "dave");
        assertEquals("TC30: Existing author with zero lists → 0 results", 0, r30.size());
        teardown();

        // TC31: Same title across two authors — title-only returns both,
        //       title+author returns only the matching author's copy
        setup();
        listManager.createList("Shared Title", "00000001", true); // alice
        listManager.createList("Shared Title", "00000002", true); // bob
        List<EverythingList> r31a = listManager.searchLists("Shared Title", null);
        assertEquals("TC31a: Title-only across authors → 2 matches", 2, r31a.size());
        List<EverythingList> r31b = listManager.searchLists("Shared Title", "alice");
        assertEquals("TC31b: Title + 'alice' → 1 match",   1, r31b.size());
        if (r31b.size() == 1) {
            assertTrue("TC31c: alice's copy is returned",
                r31b.get(0).getAuthorID().equals("00000001"));
        }
        List<EverythingList> r31c = listManager.searchLists("Shared Title", "bob");
        assertEquals("TC31d: Title + 'bob' → 1 match",     1, r31c.size());
        if (r31c.size() == 1) {
            assertTrue("TC31e: bob's copy is returned",
                r31c.get(0).getAuthorID().equals("00000002"));
        }
        teardown();

        // TC32: Items added to a list are preserved in browse/search results
        // (verifies getList() round-trips items, not just metadata)
        setup();
        int idItems = listManager.createList("Item Test List", "00000001", true);
        listManager.addItem(idItems, "Pizza");
        listManager.addItem(idItems, "Sushi");
        List<EverythingList> r32a = listManager.browsePublicLists();
        assertEquals("TC32a: browse returns 1 list", 1, r32a.size());
        if (r32a.size() == 1) {
            assertEquals("TC32b: browse list has 2 items", 2, r32a.get(0).getItems().size());
            assertTrue("TC32c: browse list contains 'Pizza'", r32a.get(0).hasItem("Pizza"));
            assertTrue("TC32d: browse list contains 'Sushi'", r32a.get(0).hasItem("Sushi"));
        }
        List<EverythingList> r32b = listManager.searchLists("Item Test", null);
        if (r32b.size() == 1) {
            assertEquals("TC32e: search list has 2 items", 2, r32b.get(0).getItems().size());
        }
        teardown();

        // Summary
        System.out.println("\n========================================");
        System.out.println("NAVIGATE LIST RESULTS: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");
        System.out.println("========================================\n");
    }
}
