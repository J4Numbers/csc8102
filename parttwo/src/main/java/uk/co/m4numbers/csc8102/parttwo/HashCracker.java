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

/**
 * Class Name - HashCracker
 * Package - uk.co.m4numbers.csc8102.parttwo
 * Desc of Class -
 * Author(s) - M. D. Ball
 * Last Mod - 30/11/2016
 */
public class HashCracker {

    public void crack_hashes(String[] hash_collection, PasswordDictionary dictionary)
            throws Exception
    {
        String test_password = dictionary.next();
        String test_hash;

        while (!test_password.equals(""))
        {
            test_hash = hash_generation(test_password);

            for (String actual_hash : hash_collection)
            {
                if (actual_hash.equals(test_hash))
                {
                    System.out.printf("Hash %s has password %s\n", actual_hash, test_password);
                }
            }

            test_password = dictionary.next();
        }
    }

    private String hash_generation(String password) throws NoSuchAlgorithmException,
            UnsupportedEncodingException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes("utf-8"));
        return Utils.byte_array_to_hex_string(md.digest());
    }

}
