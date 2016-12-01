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
 * Desc of Class -
 * Author(s) - M. D. Ball
 * Last Mod - 30/11/2016
 */
public class PasswordDictionary {

    private Scanner curr_file;
    private List<String> variations;

    private int current_iteration;
    private int current_word;

    private String[] alphabet;

    private int password_definition;

    private String get_next_word() {
        if (password_definition == 4) {
            if (current_word >= variations.size())
            {
                return "";
            }
            String ret = variations.get(current_word) + current_iteration;
            ++current_iteration;
            if (current_iteration > 9999)
            {
                ++current_word;
                current_iteration = 1;
            }
            return ret;
        }
        if (password_definition == 5)
        {
            if (current_word >= variations.size())
            {
                return "";
            }
            String ret = variations.get(current_word) + alphabet[current_iteration];
            ++current_iteration;
            if (current_iteration >= alphabet.length)
            {
                ++current_word;
                current_iteration = 0;
            }
            return ret;
        }
        if (curr_file.hasNextLine())
        {
            return curr_file.nextLine();
        }
        return "";
    }

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

    private void generate_alphanumerics(String[] alphabet, int curr_letter, String curr_total)
    {
        if (curr_letter == 3)
        {
            variations.add(curr_total);
        }
        else
        {
            for (String letter : alphabet) {
                generate_alphanumerics(alphabet, curr_letter + 1, curr_total + letter);
            }
        }
    }

    private void generate_name_dictionary()
    {
        String[] girl_names = Utils.read_file(
                "dictionary/girl_names.txt").split("\n");
        String[] boy_names = Utils.read_file(
                "dictionary/boy_names.txt").split("\n");

        for (String girl : girl_names) {
            String[] letters = girl.split("");
            generate_name_combinations(letters, 0, "");
        }
        for (String boy : boy_names) {
            String[] letters = boy.split("");
            generate_name_combinations(letters, 0, "");
        }
    }

    private void generate_name_combinations(String[] letters, int curr_letter, String curr_total)
    {
        if (curr_letter == letters.length)
        {
            variations.add(curr_total);
        }
        else
        {
            generate_name_combinations(
                    letters, curr_letter + 1,
                    curr_total + letters[curr_letter]);
            generate_name_combinations(
                    letters, curr_letter + 1,
                    curr_total + letters[curr_letter].toUpperCase());
        }
    }

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

    public PasswordDictionary() throws FileNotFoundException
    {
        password_definition = 1;
        curr_file = new Scanner(new File("dictionary/girl_names.txt"));
        variations = new ArrayList<String>();
        alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_!@#$%^&*".split("");
    }

    public String next() throws FileNotFoundException
    {
        return calculate_next_word();
    }
}
