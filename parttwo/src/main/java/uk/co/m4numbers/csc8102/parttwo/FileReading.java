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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Class Name - FileReading
 * Package - uk.co.m4numbers.csc8102.parttwo
 * Desc of Class -
 * Author(s) - M. D. Ball
 * Last Mod - 01/12/2016
 */
public class FileReading {

    private FileInputStream fis;

    public FileReading(String filename) throws FileNotFoundException
    {
        fis = new FileInputStream(new File(filename));
    }

    public String readLine() throws IOException
    {
        String line = "";
        //Read every character in the file
        int in;
        do {
            in = fis.read();
            if (in == (int)'\r')
            {
                in = fis.read();
            }
            if (in != (int)'\n' && in != -1)
            {
                line = line + (char) in;
            }
        } while (in != (int)'\n' && in != -1);
        return line;
    }

    public void close() throws IOException
    {
        fis.close();
    }
}
