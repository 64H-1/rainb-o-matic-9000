import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Rainbow {

    public final HashMap<String, List<String>> rainbowStorage = new HashMap<String, List<String>>();
    //central data structure, stores the endpoint as a key, and the list of starting plaintexts that led to that endpoint

    private final Integer plaintextLength = 4; // the plaintext space will be defined by this length
    private final Integer columns = 16 ; // number of columns in the table ( = hash + reduction rounds + 1)
    public final Integer rows = 8000; //number of starting plaintexts generated for the table; ie. rows

    public void generateTable() throws NoSuchAlgorithmException {
        for (int i = 0; i < rows; i++) {
            String startingPlaintext = leftPadZeros(Integer.toHexString(i), plaintextLength); //generate first plaintext

            //System.out.print("Row Nr." + i + ": start = " + startingPlaintext);
            String endpoint = startingPlaintext;
            for (int j = 0; j < columns; j++) {
                endpoint = rainbowStep(j, endpoint); // Move one Step along the rainbow, to the next plaintext.
                //System.out.print(" ," + j + "=" + endpoint);
            }
            putToMap(startingPlaintext,endpoint); // put the fist and the last plaintext together in the table.
            //System.out.println();
            //System.out.println("Rainbow table entry Nr. " + i + ": " + startingPlaintext + ", " + endpoint);
        }

    }

    public void writeTable() {

    }

    public void loadTable() {

    }

    // tries to find plaintext_x, the preimage of hash_of_x in the rainbow table
    public String searchTable(String hash_of_x) throws NoSuchAlgorithmException {

        for (int i = 0; i <= columns; i++) { //found the off-by-one error
            String plaintext_x = reductionFunction(columns - (i+1), hash_of_x); //ith Hypothesis plaintext
            // assuming this was the plaintext in round (totalRounds-i), what would the final plaintext in the rainbow table be?
            String hypotheticalEndpoint = rainbowLeap(columns - i, columns, plaintext_x);

            //System.out.println("i = " + i + ", hypotheticalFinal plaintext = " + hypotheticalEndpoint + ", plaintext_x = " + plaintext_x);
            if (rainbowStorage.containsKey(hypotheticalEndpoint)) { //Hypothesis Endpoint contained or not?

                //match found! apply all rounds up to the previous plaintext, to the originally generated plaintext, to approach the solution from the front of the rainbow
                List<String> startingPlaintexts = rainbowStorage.get(hypotheticalEndpoint); //this plaintext is the one that originally generated the solution.

                //try all plaintexts in startingPlaintexts.
                for (String candidateStart:startingPlaintexts) {


                    String candidatePreimage = rainbowLeap(0, columns - i - 1, candidateStart);
                    String foundHash = hash(candidatePreimage);
                    if(foundHash.equals(hash_of_x)) {
                        return "SUCESS: Hash inverted, preimage = " + candidatePreimage + ", with corresponding hash(" + candidatePreimage +") = " + foundHash;
                    }
                }
                // else: False positive, try next. May be contained deeper back in the rainbow table.
            }
            //else: hypothesis plaintext is not contained, try the next.
        }
        return "FAILURE: Preimage of " + hash_of_x + " not found.";
    }

    //############################# Helper Functions #################################################################
    //############################# Helper Functions #################################################################


    // add another newStartingPlaintext to the List of all initial plaintexts with final plaintext = endpoint.
    // or, if list empty, create new list, and add newInitalPlaintext
    public void putToMap (String newStartingPlaintext, String endpoint) {
        //get previously saved initial plaintexts
        List<String> startingPlaintexts = rainbowStorage.get(endpoint);

        //if empty, create new empty list.
        if(startingPlaintexts==null) {
            startingPlaintexts = new LinkedList<String>();
            rainbowStorage.put(endpoint, startingPlaintexts);
        }

        //add newStartingPlaintext to list
        startingPlaintexts.add(newStartingPlaintext);
    }

    //Steps one step along the rainbow, generates the next plaintext.
    public String rainbowStep(Integer round, String prevPlaintext) throws NoSuchAlgorithmException {
        String prevHash = hash(prevPlaintext); //generate hash of previous plaintext.
        String newPlaintext = reductionFunction(round, prevHash); // apply reduction function and get next plaintext.
        return newPlaintext;
    }

    //steps along the rainbow, applies round "begin" up to and including round "end"
    public String rainbowLeap(Integer begin, Integer end, String inputPlaintext) throws NoSuchAlgorithmException {
        //System.out.println("Leaping from line " + begin +" to " + end);
        String plaintext = inputPlaintext;
        for (int i = begin; i < end; i++) {
            //apply all relevant rounds to the plaintext successively
            plaintext = rainbowStep(i, plaintext);

        }
        return plaintext;
    }

    public String reductionFunction(Integer round, String prevHash) {
        String shortenedHash = prevHash.substring(0, plaintextLength); // take first few chars of hash
        Integer sum = Integer.valueOf(shortenedHash, 16) + round; //turns Hex into Integer and adds round number
        String newPlaintext = Integer.toHexString(sum); //convert back to hex
        newPlaintext = newPlaintext.substring(0, plaintextLength); //avoiding overflow errors. analogous to modulus 16^plaintextLength.
        return newPlaintext;
    }

    public String leftPadZeros(String string, Integer len) {
        while (string.length() < len){
            string = "0" + string;
        }
        return string;
    }

    public static String hash(String input) throws NoSuchAlgorithmException {
        return toHexString(getSHA(input));
    }

    // ::::::::::::::::::::::::::: The following two functions, getSHA and toHexString are adapted from ::::::::::::::::::
    // :::::::::::::::::::::::::::::https://www.geeksforgeeks.org/sha-256-hash-in-java/:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}