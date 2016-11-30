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

import java.io.*;

/**
 * Class Name - Utils
 * Package - uk.co.m4numbers.csc8102.partone
 * Desc of Class - Static class for allowing the users to do common actions on
 *  files and data
 * Author(s) - M. D. Ball
 * Last Mod: 29/11/2016
 */
public class Utils {

    /**
     * Found from StackOverflow: http://stackoverflow.com/questions/140131/
     *
     * Convert a hexadecimal string into a byte array
     *
     * @param s The hexadecimal string
     * @return A byte array from said string of length s/2
     */
    public static byte[] hex_string_to_byte_array(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Convert a byte array into a hex string... A function which I could have
     * used a bit earlier to stop myself from manually doing it... Dammit
     *
     * @param b The byte array to convert
     * @return A hex string
     */
    public static String byte_array_to_hex_string(byte[] b)
    {
        String s = "";
        for (byte aB : b) {
            s = s + String.format("%02x", aB);
        }
        return s;
    }

    /**
     * Join two byte arrays together to form one larger byte array that shall
     * consume all in its hunger. Those that look upon the larger byte array
     * shall shrink into madness for 'tis too boring a thing to behold
     *
     * @param a The first half of the array that we're joining
     * @param b The second half of the array that we're joining
     * @return The joined array
     */
    public static byte[] concatenate_byte_arrays(byte[] a, byte[] b)
    {
        byte[] concat = new byte[a.length + b.length];

        System.arraycopy(a, 0, concat, 0, a.length);
        System.arraycopy(b, 0, concat, a.length, b.length);

        return concat;
    }

    /**
     * Split apart one large byte array into a smaller subsection for use by
     * other people. To get two halves from this method, it has to be called
     * twice; once for each half
     *
     * @param to_split The byte array that we're going to downsize
     * @param start The start location (inclusive)
     * @param end The end location (exclusive)
     * @return The subsection of the array we were given
     */
    public static byte[] split_byte_array(byte[] to_split, int start, int end)
    {
        byte[] result = new byte[end - start];
        System.arraycopy(to_split, start, result, 0, end - start);
        return result;
    }

    /**
     * Ask the user to enter a password and return whatever input we get to
     * whoever asked for it
     *
     * @return The entered password
     * @throws IOException If the input stream was a bust basically
     */
    public static String get_user_password() throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.printf("Please enter a password: ");
        return br.readLine();
    }

    /**
     * Return whether a given filename exists or not
     *
     * @param filename Path (relative or absolute) of the file whose existance
     *                 we are unsure of
     * @return Whether that file exists
     */
    public static boolean file_exists(String filename)
    {
        return (new File(filename)).exists();
    }

    /**
     * Read a file and return its contents
     *
     * @param fileName The file we're reading
     * @return The contents of the file as a string
     */
    public static String read_file(String fileName)
    {
        File f = new File(fileName);
        String ret = "";

        //If the file doesn't exist, we don't have anything to return
        if (!f.exists())
        {
            return ret;
        }

        try {
            //Read every character in the file
            FileInputStream fif = new FileInputStream(f);
            int in;
            do {
                in = fif.read();
                if (in != -1)
                {
                    ret = ret + (char) in;
                }
            } while (in != -1);
            fif.close();
        }
        catch (FileNotFoundException ignored)
        {
        }
        catch (IOException ignored)
        {
        }

        //And return what we found
        return ret;
    }

    /**
     * Write some data to a file
     *
     * @param file_contents The data we are writing to the file
     * @param file_name The file we are writing the data to
     * @throws IOException If we have stream issues basically
     */
    public static void write_to_file(byte[] file_contents, String file_name) throws IOException
    {
        File encrypted_file = new File(file_name);
        FileOutputStream fos = new FileOutputStream(encrypted_file);

        fos.write(file_contents);
        fos.flush();
        fos.close();
    }

    /**
     * Attempt to delete a file from the system
     *
     * @param file_name The file we want to delete from the system
     * @return Whether that file got deleted or not
     */
    public static boolean delete_file(String file_name)
    {
        return new File(file_name).delete();
    }

    /**
     * Print the help information which is related to this program
     */
    public static void print_help()
    {
        System.out.println("Recover can be used in the following way:");
        System.out.println("\t-i [hash_filename] -o [output_filename]");
        System.out.println("\t\tUse cryptanalysis techniques to break the hashes in" +
                " [hash_filename] and print them in [output_filename]");
    }

}
