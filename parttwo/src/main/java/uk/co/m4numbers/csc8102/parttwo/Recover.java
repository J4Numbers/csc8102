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

/**
 * Class Name - Recover
 * Package - uk.co.m4numbers.csc8102.parttwo
 * Desc of Class -
 * Author(s) - M. D. Ball
 * Last Mod - 30/11/2016
 */
public class Recover {

    /**
     * It's a main method... what more do you really need to know?
     *
     * @param argv The argument array that contains all of our args
     */
    public static void main(String[] argv)
    {

        try {
            //If exactly 4 arguments were not provided, throw out some help text
            if (argv.length != 4) {
                throw new Exception("Incorrect usage of the program...");
            }

            //If the user does not provide the hash file... cry
            if (!argv[0].equals("-i"))
            {
                throw new Exception("No hash file flag '-i' found");
            }

            //If the file doesn't exist that we're meant to be reading from, cry
            if (!Utils.file_exists(argv[1]))
            {
                throw new Exception("Hashes file does not exist...");
            }

            //Likewise, if they don't provide an output file... cry more
            if (!argv[2].equals("-o"))
            {
                throw new Exception("No output file flag '-o' found");
            }

            String hashes = Utils.read_file(argv[1]);
            byte[][] hash_collection =
                    Utils.hex_string_array_to_byte_arrays(hashes.split("\r?\n"));

            PasswordDictionary pD = new PasswordDictionary(false, "dictionary/complete.txt");
            HashCracker hC = new HashCracker(false);

            if (Utils.file_exists(argv[3]))
            {
                Utils.delete_file(argv[3]);
            }
            Utils.write_to_file(hC.crack_hashes(hash_collection, pD).getBytes("utf-8"), argv[3]);

        }
        catch (Exception ex)
        {
            //If this was triggered, then it's more-than-likely their error, so
            // throw out a help thingy to them
            System.out.printf("Err: %s\n", ex.getMessage());
            ex.printStackTrace();
            Utils.print_help();
        }
    }

}
