import java.security.NoSuchAlgorithmException;

// Java program that generates a small rainbow table
// and then tries to invert a hash with it.

class Main {
    private static final Rainbow myRainbow = new Rainbow();
    private static final String plaintext_x = "1971"; // Enter a 4 digit string to generate a hash.
    private static String hash_of_x; // or enter the hash directly here.

    static {
        try {
            hash_of_x = Rainbow.hash(plaintext_x);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    // Driver code
    public static void main(String[] args) {
        try {

            myRainbow.generateTable();
            System.out.println("searching table for: hash(" + plaintext_x +") = " + hash_of_x);
            System.out.println(myRainbow.rainbowStorage);
            System.out.println("Generating the table caused " + (myRainbow.rows -  myRainbow.rainbowStorage.size()) + " collisions.");
            System.out.println(myRainbow.searchTable(hash_of_x));

        }


        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm: " + e);
        }
    }
}
