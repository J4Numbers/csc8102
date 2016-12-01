package uk.co.m4numbers.csc8102.parttwo;

/*
 * Copyright 2016 M. D. Ball (m.d.ball2@ncl.ac.uk)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Class Name - HashCracker
 * Package - uk.co.m4numbers.csc8102.parttwo
 * Desc of Class - Attempt to crack a number of user-provided hashes against a
 * dictionary that the user also provides to us before we start getting on
 * with cracking
 * Author(s) - M. D. Ball
 * Last Mod - 30/11/2016
 */
public class HashCracker
{

    /**
     * Generate a SHA-256 hash from the given password
     *
     * @param password The password that we're hashing
     *
     * @return The bytecode of the hash of that password
     *
     * @throws NoSuchAlgorithmException     If the algorithm we were using doesn't
     *                                      exist on this machine somehow
     * @throws UnsupportedEncodingException If the encoding we're using doesn't
     *                                      exist on this machine somehow
     */
    private byte[] hash_generation(String password)
            throws NoSuchAlgorithmException,
            UnsupportedEncodingException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes("utf-8"));
        return md.digest();
    }

    /**
     * Attempt to crack a set of hashes according to a dictionary that we have
     * been provided with. Exit execution once we either run out of words to
     * test against or find an answer to all of our hashes
     *
     * @param hash_collection The hash collection that we're attempting to
     *                        crack
     * @param dictionary      A dictionary of possible words that the hashes can be
     *                        cracked with
     *
     * @return An output string containing all the hashes we solved and their
     * passwords separated by the \n character
     *
     * @throws Exception If something goes wrong somewhere in our code
     *                   basically
     */
    public String crack_hashes(
            byte[][] hash_collection, PasswordDictionary dictionary
    )
            throws Exception
    {
        //Start out with a new word from the dictionary and some local
        // variables
        String test_password = dictionary.next();
        String output        = "";

        byte[] test_hash;

        //Metric variables to track the time and the number of tests we have
        // run so far
        int  solved_hashes = 0;
        long clock         = System.currentTimeMillis();
        long tests         = 0;

        //While we still have a password to test and we still have hashes to
        // solve...
        while (!test_password
                .equals("") && solved_hashes < hash_collection.length)
        {
            //Generate a hash of the current password and increase our test
            // counter
            test_hash = hash_generation(test_password);
            ++tests;

            for (int i = 0; i < hash_collection.length; ++i)
            {
                //Now, for each hash that we're trying to crack, compare that
                // hash with the one we have generated and see if we get a
                // match
                if (Arrays.equals(hash_collection[i], test_hash))
                {
                    //Since we got a match, add that match to the output string
                    output += String.format(
                            "Hash: %s Password: %s\n",
                            Utils.byte_array_to_hex_string(hash_collection[i]),
                            test_password
                    );

                    //Wipe that hash from the collection so we don't waste time
                    // comparing other passwords to it and increment the number
                    // of hashes that we have solved
                    hash_collection[i] = null;
                    ++solved_hashes;
                }
            }

            //Get the next word from the dictionary
            test_password = dictionary.next();
        }

        //Print out the metrics to the console to say how long it took us to
        // solve however many hashes we solved and how many tests we performed
        // in order to track that metric
        System.out
                .printf(
                        "%d hashes solved in %.2f seconds after %d password tests\n",
                        solved_hashes,
                        (double) (System.currentTimeMillis() - clock) / 1000,
                        tests
                );

        return output;
    }

}
