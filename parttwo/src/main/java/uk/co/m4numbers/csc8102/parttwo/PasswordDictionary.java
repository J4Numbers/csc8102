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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class Name - PasswordDictionary
 * Package - uk.co.m4numbers.csc8102.parttwo
 * Desc of Class - A dictionary of potential passwords that are given one-at-a-
 * time to whoever calls the next() method. Generated from files in a
 * dictionary subfolder as a strict necessity (because coursework basically)
 * Author(s) - M. D. Ball
 * Last Mod - 30/11/2016
 */
public class PasswordDictionary
{

    /**
     * Variables that we access to get the next word in the dictionary at
     * different points in time during the program
     */
    private Scanner curr_file;
    private List<String> variations;

    /**
     * Variables that we use to track what the current word actually is when
     * we're working off the combination generations because wow... complexity
     */
    private int current_iteration;
    private int current_word;

    /**
     * An array of all the different characters (in string format) that are
     * available for the alphanumeric combinations
     */
    private String[] alphabet;

    /**
     * The current method of generating passwords that we are using. This can
     * have valid values from 1 to 5, where:
     * <p>
     * 1 is using the ten most popular girl names in the UK.
     * 2 is using the ten most popular boy names in the UK.
     * 3 is using a word within Moby Dick
     * 4 is using a word from 1 or 2 with no specified case followed by a
     * number between 1 and 9999, and
     * 5 is a combination of 4 alphanumeric characters as defined in the
     * alphabet variable just above this one.
     * <p>
     * If this variable ever goes over our valid options, we have exhausted the
     * dictionary and return an empty string to signify as such.
     */
    private int password_definition;

    /**
     * @return The next word in our current method of finding the next word
     */
    private String get_next_word()
    {
        //If we are using a case-sensitive name followed by a number between 1
        // and 9999...
        if (password_definition == 4)
        {
            //First, make sure we are still able to generate a valid word from
            // our current dictionary...
            if (current_word >= variations.size())
            {
                return "";
            }

            //Then create the current word by getting the name followed by the
            // iteration we are on between 1 and 9999 (then increment that
            // iteration for the next time)
            String ret = variations.get(current_word) + current_iteration;
            ++current_iteration;

            //If we have exceeded the number of iterations we have available,
            // move onto the next word and reset the iteration count to 1
            // again
            if (current_iteration > 9999)
            {
                ++current_word;
                current_iteration = 1;
            }

            //Return whatever we found as the next word previously
            return ret;
        }

        //If we are using 4 alphanumeric characters as defined by the alphabet
        // variable above...
        if (password_definition == 5)
        {
            //Make sure we are in bounds before we continue
            if (current_word >= variations.size())
            {
                return "";
            }

            //Generate the next word with the three letters generated in the
            // variations variable followed by the next letter in our alphabet,
            // creating four letters. Then increment the letter of the alphabet
            // for the next cycle
            String ret = variations
                    .get(current_word) + alphabet[current_iteration];
            ++current_iteration;

            //If we have exhausted the alphabet, move onto the next three-
            // letter combination generated and reset our iteration count
            if (current_iteration >= alphabet.length)
            {
                ++current_word;
                current_iteration = 0;
            }

            //Return the current combination
            return ret;
        }

        //If we are reading from a file... Make sure we have a line to read
        // before we attempt to do so and return said line
        if (curr_file.hasNextLine())
        {
            return curr_file.nextLine();
        }

        //If none of those were true, don't return anything and move on
        return "";
    }

    /**
     * The previous dictionary has been exhausted so it is our job to link the
     * new dictionary together and present the next word to the calling things.
     * See the password definition javadoc above to see what each number
     * represents for the next dictionary choice
     *
     * @return The first word in the next dictionary
     *
     * @throws FileNotFoundException If the file we are using as a dictionary
     *                               doesn't actually exist
     */
    private String change_definition() throws FileNotFoundException
    {
        switch (password_definition)
        {
            case 1:
                curr_file = new Scanner(new File("dictionary/girl_names.txt"));
                break;
            case 2:
                curr_file = new Scanner(new File("dictionary/boy_names.txt"));
                break;
            case 3:
                curr_file = new Scanner(new File(
                        "dictionary/word_list_moby_all_moby_words.flat.txt"));
                break;
            case 4:
                generate_name_dictionary();
                current_word = 0;
                current_iteration = 1;
                break;
            case 5:
                generate_alphanumerics(alphabet, 0, "");
                current_word = 0;
                current_iteration = 0;
                break;
            default:
                return "";
        }

        return get_next_word();
    }

    /**
     * Create an exhaustive list of all three-letter combinations of
     * alphanumeric characters according to the alphabet that has been defined
     * elsewhere. These combinations will be added to before we use it as a
     * password, but since that's a difference of 71^3 vs 71^4 calculations for
     * this one method... yeah, no.
     * <p>
     * Also, the recursion in this is so useful...
     *
     * @param alphabet    Our available alphabet from which we can create
     *                    combinations
     * @param curr_letter The current letter in our current combination string.
     *                    When this reaches 3, we add whatever combination we
     *                    have created to the list and move on to the next
     * @param curr_total  The current combination string of length curr_letters
     */
    private void generate_alphanumerics(
            String[] alphabet, int curr_letter, String curr_total
    )
    {
        //We have generated a combination! Add it to the pile
        if (curr_letter == 3)
        {
            variations.add(curr_total);
        }
        else
        {
            //For each letter in the alphabet, add it to an instance of our
            // combination and move onto the next letter to generate
            for (String letter : alphabet)
            {
                generate_alphanumerics(
                        alphabet, curr_letter + 1, curr_total + letter);
            }
        }
    }

    /**
     * Wrapper method for generating every combination of a case-sensitive name
     * from the most popular names for boys and girls. This basically means
     * that every name of n letters long can have 2^n possible states... and
     * we're putting 20 of them into one list... wow.
     */
    private void generate_name_dictionary()
    {
        //Get a complete list of all the boy and girl names that we have in our
        // files
        String[] girl_names = Utils.read_file(
                "dictionary/girl_names.txt").split("\n");
        String[] boy_names = Utils.read_file(
                "dictionary/boy_names.txt").split("\n");

        for (String girl : girl_names)
        {
            //Split each name into letters and give it to our combination
            // generator to create those 2^n combinations of names
            String[] letters = girl.split("");
            generate_name_combinations(letters, 0, "");
        }
        for (String boy : boy_names)
        {
            //Split each name into letters and give it to our combination
            // generator to create those 2^n combinations of names
            String[] letters = boy.split("");
            generate_name_combinations(letters, 0, "");
        }
    }

    /**
     * A recursive method that has n layers where n is the length of whatever
     * name is given to us. We generate a case-sensitive arrangement of the
     * letters in that name and add it to the pile that is used as the next
     * dictionary
     *
     * @param letters     The letters that we are combining together in uppercase
     *                    and lowercase to provide passwords to our dictionary
     * @param curr_letter The current letter in the name that we are
     *                    considering
     * @param curr_total  The name so far
     */
    private void generate_name_combinations(
            String[] letters, int curr_letter, String curr_total
    )
    {
        //When we have created a version of the name we were given, add that
        // name to the pile and move onto the next one
        if (curr_letter == letters.length)
        {
            variations.add(curr_total);
        }
        else
        {
            //Add the lowercase version of this letter to the current string
            // and move onto the next letter
            generate_name_combinations(
                    letters, curr_letter + 1,
                    curr_total + letters[curr_letter]
            );

            //Add the uppercase version of this letter to the current string
            // and move onto the next letter
            generate_name_combinations(
                    letters, curr_letter + 1,
                    curr_total + letters[curr_letter].toUpperCase()
            );
        }
    }

    /**
     * Wrapper method that attempts to get the next word the easy way before
     * it then has to close all the current instances of dictionaries and boot
     * up the next instance
     *
     * @return The next word in the dictionary or a blank string if there were
     * no more words in any dictionaries
     *
     * @throws FileNotFoundException If we couldn't load the dictionaries
     */
    private String calculate_next_word() throws FileNotFoundException
    {
        String nx = get_next_word();

        if (nx.equals(""))
        {
            curr_file.close();
            variations.clear();
            ++password_definition;
            nx = change_definition();
        }
        return nx;
    }

    /**
     * Instantiate some base variables for ourselves that are going to start
     * the dictionary and mean that everything is ready for the first word to
     * be given out
     *
     * @throws FileNotFoundException If we couldn't find the file we're getting
     *                               the passwords from
     */
    public PasswordDictionary() throws FileNotFoundException
    {
        //Start out with testing the 10 most popular girl names in the UK.
        password_definition = 1;
        curr_file = new Scanner(new File("dictionary/girl_names.txt"));

        //Instantiate the variations array and create our alphabet of all
        // lowercase and uppercase letters, along with digits and some special
        // characters (|alphabet| == 71)
        variations = new ArrayList<String>();
        alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_!@#$%^&*"
                .split("");
    }

    /**
     * @return The next available word in the dictionary
     *
     * @throws FileNotFoundException If our dictionary couldn't open a
     *                               dictionary of passwords
     */
    public String next() throws FileNotFoundException
    {
        return calculate_next_word();
    }
}
