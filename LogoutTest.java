public class LogoutTest {
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

    public static void main(String[] args){
        System.out.println("\n=== LOGOUT TESTS ===\n");

        // Setup test data directly in DB
        DBManager db = new DBManager();
        db.addUser(new User("00000100", "logoutuser", "Password123!", "logout@test.com"));

        LoginController lc = new LoginController();
        lc.login("logoutuser", "Password123!");

        // TC1: logout after login
        assertEquals("TC1: Logout after successful login",
            "logout successful",
            lc.logout(null)
        );

        // TC2: logout again
        assertEquals("TC2: Logout when already logged out",
            "logout successful",
            lc.logout(null)
        );

        System.out.println("\n========================================");
        System.out.println("LOGOUT RESULTS: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");
        System.out.println("========================================\n");
    }
}