public class CreateAccountTest {
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
        System.out.println("\n=== CREATE ACCOUNT TESTS ===\n");

        LoginController lc = new LoginController();

        assertEquals("TC1: Valid account",
            "Account created",
            lc.createAccount("user1234", "Password123!")
        );

        assertEquals("TC2: Username too short",
            "Username must be between 8 and 16 characters",
            lc.createAccount("user12", "Password123!")
        );

        assertEquals("TC3: Username too long",
            "Username must be between 8 and 16 characters",
            lc.createAccount("user12345678901234", "Password123!")
        );

        assertEquals("TC4: Username has special character",
            "Username can only contain letters and numbers",
            lc.createAccount("user@1234", "Password123!")
        );

        assertEquals("TC5: Username has space",
            "Username can only contain letters and numbers",
            lc.createAccount("user 1234", "Password123!")
        );

        assertEquals("TC6: Password too short",
            "Password must be between 12 and 20 characters",
            lc.createAccount("user1234", "Pass123!")
        );

        assertEquals("TC7: Password too long",
            "Password must be between 12 and 20 characters",
            lc.createAccount("user1234", "VeryLongPassword1234!!")
        );

        assertEquals("TC8: Password missing letter",
            "Password must contain at least one letter one number and one special character",
            lc.createAccount("user1234", "1234567890!@")
        );

        assertEquals("TC9: Password missing number",
            "Password must contain at least one letter one number and one special character",
            lc.createAccount("user1234", "PasswordOnly!!")
        );

        assertEquals("TC10: Password missing special character",
            "Password must contain at least one letter one number and one special character",
            lc.createAccount("user1234", "Password1234")
        );

        System.out.println("\n========================================");
        System.out.println("CREATE ACCOUNT RESULTS: " + passed + " passed, " + failed + " failed, " + (passed + failed) + " total");
        System.out.println("========================================\n");
    }
}