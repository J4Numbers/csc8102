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

/**
 * Class Name - MySafe
 * Package - uk.co.m4numbers.csc8102.partone
 * Desc of Class - Main class which contains our main program
 * Author(s) - M. D. Ball
 * Last Mod: 29/11/2016
 */
public class MySafe {

    /**
     * It's a main method... what more do you really need to know?
     *
     * @param argv The argument array that contains all of our args
     */
    public static void main(String[] argv)
    {

        try {
            //If exactly 2 arguments were not provided, throw out some help text
            if (argv.length != 2) {
                System.out.println("Incorrect usage of the program...");
                throw new Exception("Incorrect args found");
            }

            if (!(new File(argv[1]).exists()))
            {
                System.out.println("File does not exist...");
                throw new Exception("File does not exist");
            }

            //If the user wants to encrypt something, they use -e [filename]
            if (argv[0].equals("-e"))
            {
                if (argv[1].endsWith(".8102"))
                {
                    System.out.println("File already encrypted...");
                    throw new Exception("File already encrypted");
                }
                p.encryptFile(argv[1], argv[2]);
            }
            //If they want to decrypt something, they use -d [filename].8102
            else if (argv[0].equals("-d"))
            {
                if (!argv[1].endsWith(".8102"))
                {
                    System.out.println("File is not encrypted...");
                    throw new Exception("File not encrypted");
                }
                p.decryptFile(argv[1], argv[2]);
            }
            //Otherwise, if none of those were applicable, throw out some help
            else
            {
                Utils.print_help();
            }
        }
        catch (Exception ex)
        {
            //If this was triggered, then it's more-than-likely their error, so throw out a help thingy to them
            ex.printStackTrace();
            Utils.print_help();
        }
    }
}
