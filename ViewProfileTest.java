import java.util.ArrayList;

public class ViewProfileTest {
    static int passed = 0;
    static int failed = 0;

    static void assertEquals(String testName, String expected, String actual){
        if(expected.equals(actual)){
            System.out.println("  PASS: " + testName);
            passed++;
        } else {
            System.out.println("  FAIL: " + testName + " (expected \"" + expected + "\", got \"" + actual + "\")");
            failed++;
        }
    }

    public static void main(String Args[]){
        System.out.println("\n=== DISPLAY PROFILE TESTS ===\n");


        Profile testProfile = new Profile("Charli Hoelzle", "I <3 Ranking", "profilePicture");
        ArrayList<EverythingList> createdLists = new ArrayList<EverythingList>();
        createdLists.add(new EverythingList(54321 ,"Movies", "12345", true));
        createdLists.add(new EverythingList(54322, "Games", "12345", true));
        createdLists.add(new EverythingList(54323, "Books","12345", true));
        testProfile.setCreatedLists(createdLists);

        //TC1 Displays all lists correctly
        assertEquals("TC 1",
            "  - Movies (0 items) [Public]\n" + 
            "  - Games (0 items) [Public]\n" + 
            "  - Books (0 items) [Public]\n", 
            testProfile.displayCreatedLists());

        //TC2 Displays error message if profile has no lists
        Profile emptyProfile = new Profile();
        assertEquals("TC 2",
            "  No lists created yet.",
            emptyProfile.displayCreatedLists());

        System.out.println("\n========================================");
        System.out.println("DISPLAY PROFILE RESULTS: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");
        System.out.println("========================================\n");
    }
        
    
    
     
}
