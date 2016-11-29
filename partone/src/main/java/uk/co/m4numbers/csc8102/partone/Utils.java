package uk.co.m4numbers.csc8102.partone;

/**
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class Name - Utils
 * Package - uk.co.m4numbers.csc3124.q1
 * Desc of Class - Static class for allowing the users to do common actions on files and data
 * Author(s) - M. D. Ball
 * Last Mod: 06/05/2016
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
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /**
     * Convert a byte array into a hex string... A function which I could have used a bit earlier to stop
     * myself from manually doing it... Dammit
     *
     * @param b The byte array to convert
     * @return A hex string
     */
    public static String byteArrayToHexString(byte[] b)
    {
        String s = "";
        for (byte aB : b) {
            s = s + String.format("%02X", aB);
        }
        return s;
    }

    /**
     * Read a file and return its contents
     *
     * @param fileName The file we're reading
     * @return The contents of the file as a string
     */
    public static String readFile(String fileName)
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
                ret = ret + (char) in;
            } while (in != -1);
            fif.close();
        }
        catch (FileNotFoundException fnf)
        {
        }
        catch (IOException ioe)
        {
        }

        //And return what we found
        return ret;
    }

    /**
     * Print the help information which is related to this program
     */
    public static void printHelp()
    {
        System.out.println("Protect can be used in one of three ways:");
        System.out.println("\t-e [filename] [password]");
        System.out.println("\t\tWrite a new, encrypted version of a file into [filename].enc.");
        System.out.println("\t\tThis will only work if you have the correct password for writing to a file");
        System.out.println("");
        System.out.println("\t-d [filename].enc [password]");
        System.out.println("\t\tRead [filename].enc into a new file: [filename].");
        System.out.println("\t\tThe password must allow for reading the file, and the signature on\n"
                + "\t\t[filename].enc must also be correct before it can be read");
        System.out.println("");
        System.out.println("\t-c");
        System.out.println("\t\tCheck if all files in this folder are encrypted and delete any unencrypted ones.");
        System.out.println("\t\tCheck that all files are signed correctly, deleting all failing signatures");
        System.out.println("\t\tCheck for missing files compared to the original folder");
        System.out.println("\t\tDelete any superfluous files in the folder");
    }

}
