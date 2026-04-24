public class LoginTest {
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
        System.out.println("\n=== LOGIN TESTS ===\n");

        // Setup test data directly in DB
        DBManager db = new DBManager();
        db.addUser(new User("00000099", "loginuser", "Password123!", "login@test.com"));

        LoginController lc = new LoginController();

        // TC1: valid username and password
        assertEquals("TC1: Valid login",
            "login successful",
            lc.login("loginuser", "Password123!")
        );

        // TC2: valid username, wrong password
        assertEquals("TC2: Wrong password",
            "login failed",
            lc.login("loginuser", "WrongPassword1!")
        );

        System.out.println("\n========================================");
        System.out.println("LOGIN RESULTS: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");
        System.out.println("========================================\n");
    }
}