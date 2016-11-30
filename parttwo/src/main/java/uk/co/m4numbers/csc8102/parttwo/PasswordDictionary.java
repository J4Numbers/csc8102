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

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class Name - PasswordDictionary
 * Package - uk.co.m4numbers.csc8102.parttwo
 * Desc of Class -
 * Author(s) - M. D. Ball
 * Last Mod - 30/11/2016
 */
public class PasswordDictionary {

    private Scanner curr_file;
    private List<String> variations;
    private int current_iteration;
    private int current_word;

    private int password_definition;

    private String get_next_word() {
        if (password_definition == 3) {
            String ret = variations.get(current_word) + current_iteration;
            ++current_iteration;
            if (current_iteration > 9999)
            {
                ++current_word;
            }
            return ret;
        }
        if (password_definition == 5)
        {
            return "";
        }
        if (curr_file.hasNextLine())
        {
            return curr_file.nextLine();
        }
        return "";
    }

    private String switch_file()
    {
        String new_file;
        switch (password_definition)
        {
            case 1:
                new_file = "girl_names.txt";
                break;
            case 2:
                new_file = "boy_names.txt";
                break;
            case 4:
                new_file = "word_list_moby_all_moby_words.flat.txt";
                break;
            default:
                new_file = "";
        }
        return new_file;
    }

    private void generate_combinatorials(String[] letters, int curr_letter, String curr_total)
    {
        if (curr_letter != curr_total.length())
        {
            System.out.printf("ERR\n");
        }
        if (curr_letter == letters.length)
        {
            variations.add(curr_total);
        }
        else
        {
            generate_combinatorials(letters, curr_letter + 1, curr_total + letters[curr_letter]);
            generate_combinatorials(letters, curr_letter + 1, curr_total + letters[curr_letter].toUpperCase());
        }
    }

    private String calculate_next_word()
    {
        String nx = get_next_word();

        if (nx.equals(""))
        {
            curr_file.close();
            ++password_definition;
            if (password_definition == 3 || password_definition == 5)
            {
                variations.clear();

                if (password_definition == 3)
                {
                    String[] girl_names = Utils.read_file("dictionary/girl_names.txt").split("\n");
                    String[] boy_names = Utils.read_file("dictionary/boy_names.txt").split("\n");

                    for (String girl :
                            girl_names) {
                        String[] letters = girl.split("");
                        generate_combinatorials(letters, 0, "");
                    }
                    for (String boy :
                            boy_names) {
                        String[] letters = boy.split(".");
                        generate_combinatorials(letters, 0, "");
                    }

                    variations.add("");
                    current_word = 0;
                    current_iteration = 1;
                }
                else
                {
                    //a
                }
            }
            else
            {
                curr_file = new Scanner("dictionary/" + switch_file());
                nx = get_next_word();
            }
        }
        return nx;
    }

    public PasswordDictionary()
    {
        password_definition = 1;
        curr_file = new Scanner("dictionary/girl_names.txt");
        variations = new ArrayList<String>();
    }

    public String next()
    {
        return calculate_next_word();
    }
}
