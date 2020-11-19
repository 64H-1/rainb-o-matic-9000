import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Rainbow {

    /*** Change the path ***/
    final static String outputFilePath = "~/Documents/infosec/bifroest/rainbowTable.txt";
    private HashMap<String, String> rainbowTable = new HashMap<String, String>(); //central data structure

    private Integer keyLength = 4;
    private Integer totalRounds = 16; // hash + reduction rounds.
    private Integer rows = 16; //number of starting keys generated for the table; ie. rows

    public void generateTable() throws NoSuchAlgorithmException {
        for (int i = 0; i < rows; i++) {
            String firstKey = Integer.toHexString(i); //generate first key
            System.out.print("Row Nr." +i+": 1 = " + firstKey);
            String lastKey = firstKey;
            for (int j = 0; j < totalRounds; j++) {
                lastKey = rainbowStep(j, lastKey); // Move one Step along the rainbow, to the next key.
                System.out.print(" ,"+ j + "=" + lastKey);
            }
            rainbowTable.put(lastKey,firstKey); // put the fist and the last key together in the table.
            System.out.println();
            System.out.println( "Rainbow table entry Nr. " + i+ ": " + firstKey + ", " + lastKey);
        }

    }

    public void writeTable(){

        //key-value pairs
        rainbowTable.put("124234", "One");
        rainbowTable.put("234", "Two");
        rainbowTable.put("1242", "Three");

        //new file object
        File file = new File(outputFilePath);

        BufferedWriter bf = null;;

        try{

            //create new BufferedWriter for the output file
            bf = new BufferedWriter( new FileWriter(file) );

            //iterate map entries
            for(Map.Entry<String, String> entry : rainbowTable.entrySet()){

                //put key and value separated by a colon
                bf.write( entry.getKey() + ":" + entry.getValue() );

                //new line
                bf.newLine();
            }

            bf.flush();

        }catch(IOException e){
            e.printStackTrace();
        }finally{

            try{
                //always close the writer
                bf.close();
            }catch(Exception e){}
        }
    }

    public void loadTable(){

    }

    public String searchTable(String soughtHash) throws NoSuchAlgorithmException {

        String preimage = "FAILURE: Preimage not found."; //initiating the key, and preparing for failure. Overwritten upon success.


        for (int i = 0; i < totalRounds; i++) {
            String soughtKey = reductionFunction(totalRounds-i, soughtHash); //ith Hypothesis key
            // assuming this was the key in round (totalRounds-i), what would the final key in the rainbow table be?
            String hypotheticalFinalKey = rainbowLeap(totalRounds-i, totalRounds, soughtKey);

            if(rainbowTable.containsKey(hypotheticalFinalKey)){ //Hypothesis key contained or not?

                //match found! apply all rounds up to the previous key, to the originally generated key, to approach the solution from the front of the rainbow
                String startingKey = rainbowTable.get(hypotheticalFinalKey); //this key is the one that originally generated the solution.

                preimage = rainbowLeap(0,totalRounds-i-1,startingKey);
                return preimage;
            }
            //if hypothesis key is not contained, try the next.
        }
        return preimage;
    }

    //############################# Helper Functions #################################################################
    //############################# Helper Functions #################################################################

    //Steps one step along the rainbow, generates the next key.
    public String rainbowStep(Integer round, String prevKey) throws NoSuchAlgorithmException {
        String prevKeyHash = toHexString(getSHA(prevKey)); //generate hash of previous key.
        String newKey = reductionFunction(round,prevKeyHash); // apply reduction function and get next key.
        return newKey;
    }

    //steps along the rainbow, from round "begin" to round "end"
    public String rainbowLeap(Integer begin, Integer end, String inputKey) throws NoSuchAlgorithmException {
        String key = inputKey;
        for (int i = begin; i < end; i++) {
            //apply all relevant rounds to the key successively
            key = rainbowStep(i,key);
        }
        return key;
    }

    //Applies the last i rounds to the soughtKey in the correct order.
    public String steppingBackwards(Integer i, String soughtKey) throws NoSuchAlgorithmException {

        String resultingKey = soughtKey;

        System.out.println("Stepping backwards to find " + soughtKey);

        for (int j = totalRounds-i; j < totalRounds; j++) {
            resultingKey = rainbowStep(j, resultingKey);
            System.out.println("Round "+ j + ": " + resultingKey);
        }

        return resultingKey;
    }

    public String reductionFunction(Integer round, String prevHash){
        String shortenedHash = prevHash.substring(0, keyLength); // take first few chars of hash
        Integer sum = Integer.valueOf(shortenedHash, 16) + round; //turns Hex into Integer and adds round number
        String newKey = Integer.toHexString(sum); //convert back to hex
        newKey = newKey.substring(0,keyLength); //avoiding overflow errors. analogous to modulus.
        return newKey;
    }

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}