package uk.co.m4numbers.partone.tests;

import org.junit.Assert;
import org.junit.Test;

import uk.co.m4numbers.csc8102.partone.Utils;

/**
 * Class Name - UtilTesting
 * Package - uk.co.m4numbers.partone.tests
 * Desc of Class -
 * Author(s) - M. D. Ball
 * Last Mod - 29/11/2016
 */
public class UtilTesting {

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

    @Test
    public void evaluate_byte_array_to_hex_string()
    {
        String hex_string = "000102030405060708090a0b0c0d0e0f";
        byte[] bytes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        String comparator = Utils.byte_array_to_hex_string(bytes);
        Assert.assertEquals(hex_string, comparator);
    }

    @Test
    public void evaluate_read_file()
    {
        String test_val = "This was a test!\n";
        String read_val = Utils.read_file("test.txt");
        Assert.assertEquals(test_val, read_val);
    }

}
