import java.security.NoSuchAlgorithmException;

// Java program to calculate SHA hash value

class Main {
    private static final Rainbow myRainbow = new Rainbow();
    private static final String testKey = "c7bc";
    private static String testHash;

    static {
        try {
            testHash = Rainbow.hash(testKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // Driver code
    public static void main(String[] args) {
        try {

            myRainbow.generateTable();
            System.out.println("searching table for: hash(" + testKey +") = " + testHash);
            System.out.println(myRainbow.searchTable(testHash));

        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }
    }
}
