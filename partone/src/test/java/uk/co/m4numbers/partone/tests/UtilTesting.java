package uk.co.m4numbers.partone.tests;

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

import org.junit.Assert;
import org.junit.Test;

import uk.co.m4numbers.csc8102.partone.Utils;

/**
 * Class Name - UtilTesting
 * Package - uk.co.m4numbers.partone.tests
 * Desc of Class - Test Driven Development class for testing the utility
 *   functions available to all classes
 * Author(s) - M. D. Ball
 * Last Mod - 29/11/2016
 */
public class UtilTesting {

    /**
     * Evaluate whether the conversion from hexadecimal strings to byte
     * arrays is working properly by converting the numbers 0 to 15 in
     * sequence and comparing the output against those numbers
     */
    @Test
    public void evaluate_hex_string_to_byte_array()
    {
        String hex_string = "000102030405060708090a0b0c0d0e0f";
        byte[] bytes = Utils.hex_string_to_byte_array(hex_string);
        for (byte i = 0; i < 16; ++i)
        {
            Assert.assertEquals(bytes[i], i);
        }
    }

    /**
     * Evaluate whether the conversion from byte arrays to hexadecimal
     * strings is working properly by converting the byte values from 0 to 15
     * into a hexadecimal string and comparing the hexadecimal string for
     * those numbers against the output
     */
    @Test
    public void evaluate_byte_array_to_hex_string()
    {
        String hex_string = "000102030405060708090a0b0c0d0e0f";
        byte[] bytes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        String comparator = Utils.byte_array_to_hex_string(bytes);
        Assert.assertEquals(hex_string, comparator);
    }

    @Test
    public void evaluate_split_byte_array()
    {
        byte[] whole = {0, 1, 2, 3, 4, 5, 6, 7};
        byte[] test_half  = {0, 1, 2, 3};
        byte[] actual_half = Utils.split_byte_array(whole, 0, 4);

        for (int i = 0; i < 4; ++i)
        {
            Assert.assertEquals(test_half[i], actual_half[i]);
        }
    }

    @Test
    public void evaluate_concatenate_byte_arrays()
    {
        byte[] left = {0, 1, 2, 3, 4, 5, 6, 7};
        byte[] right = {8, 9, 10, 11, 12, 13, 14, 15};

        byte[] test_both = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        byte[] actual_both = Utils.concatenate_byte_arrays(left, right);

        for (int i = 0; i < 16; ++i)
        {
            Assert.assertEquals(test_both[i], actual_both[i]);
        }
    }

    /**
     * Evaluate whether we can read the file that is present in the module
     * directory containing the value below by reading in the value from that
     * file and comparing it against the value we entered below
     */
    @Test
    public void evaluate_read_file()
    {
        String test_val = "This was a test!\n";
        String read_val = Utils.read_file("test.txt");
        Assert.assertEquals(test_val, read_val);
    }

}
