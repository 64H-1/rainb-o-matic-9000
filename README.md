# rainb-o-matic-9000

Welcome!
We created this rainbow table so you can have a look at it and experiment with it!

Fire it up and change plaintext_x in Main.java to see if it can crack your password!
Alternatively you can input the hash directly with minor modifications.

To see the inner workings open Rainbow.java.
Here you can change the number of rows and columns in the table and see if you can tune the table to
be faster and more successful!

Challenges for the reader:

1. Disable the different reduction functions, i.e. reduce it to a table of hash chains
and see what happens to the collisions.

2. Implement a timing function and experiment with the number of rows and columns.
What happens to the generating and lookup time? What happens to the likelyhood of success?

3. If you want to crack longer plaintexts longer than 6 or so, you can implement a way to read in
a precomputed table, so that you can reuse it.

4. See if you can adapt this program to work in a alphanumeric plaintext space.

5. Building on top of 4: Can you think of reduction functions that sample the plaintext space
not uniformly, but prefer passwords as a human might generate them?

If you complete any of these challenges, feel free to comment or request a merge!

Have fun!
