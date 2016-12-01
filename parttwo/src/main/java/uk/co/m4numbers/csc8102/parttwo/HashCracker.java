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
 * Desc of Class -
 * Author(s) - M. D. Ball
 * Last Mod - 30/11/2016
 */
public class HashCracker
{

    private byte[] hash_generation(String password)
            throws NoSuchAlgorithmException,
            UnsupportedEncodingException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes("utf-8"));
        return md.digest();
    }

    public String crack_hashes(
            byte[][] hash_collection, PasswordDictionary dictionary
    )
            throws Exception
    {
        String test_password = dictionary.next();
        byte[] test_hash;
        String output        = "";

        int solved_hashes = 0;
        long clock        = System.currentTimeMillis();
        long tests        = 0;

        while (!test_password
                .equals("") && solved_hashes < hash_collection.length)
        {
            test_hash = hash_generation(test_password);
            ++tests;

            for (int i = 0; i < hash_collection.length; ++i)
            {
                if (Arrays.equals(hash_collection[i], test_hash))
                {
                    output += String.format(
                            "Hash: %s Password: %s\n",
                            Utils.byte_array_to_hex_string(hash_collection[i]),
                            test_password
                    );
                    hash_collection[i] = null;
                    ++solved_hashes;
                }
            }

            test_password = dictionary.next();
        }

        System.out
                .printf("%d hashes solved in %.2f seconds after %d password tests\n",
                        solved_hashes,
                        (double) (System.currentTimeMillis() - clock) / 1000,
                        tests
                );

        return output;
    }

}
