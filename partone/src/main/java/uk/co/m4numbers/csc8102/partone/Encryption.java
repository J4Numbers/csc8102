package uk.co.m4numbers.csc8102.partone;

/**
 * Copyright 2016 M. D. Ball (m.d.ball2@ncl.ac.uk)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;

import java.security.spec.KeySpec;
import java.util.*;

/**
 * Class Name - Encryption
 * Package - uk.co.m4numbers.csc8102.partone
 * Desc of Class -
 * Author(s) - M. D. Ball
 * Last Mod: 29/11/2016
 */
public class Encryption {

    /**
     * A map of the starting files that are available to be encrypted, decrypted, and checked. If a file exists
     * in the folder whose base name isn't on this list, they get kicked out for some reason or another
     *
    private Map<String, PasswordFiles> allowed_files;

    /**
     * Salt array for the purpose of decrypting our encrypted file key
     *
    private byte[] salt = new byte[]{29, -99, -107, -87, -75, -90, 35, 121};

    /**
     * Encrypt a file with AES symmetric encryption. However, we will only do so if our user gives us a password
     * which has been verified through SHA-256. Once that has been done and everything is above-board, we will proceed
     * in the encryption and also generate a signature of the file afterwards through Hmac and SHA-256. All of this
     * data is dumped into the files [filename].enc and [filename].sig afterwards with Base64 encoding.
     *
     * After all that, we delete the original file and move on with our lives.
     *
     * @param fileName The name of the file that we're encrypting
     * @param password The password of the file (which must have write permissions)
     * @return The result of the operation - if it wasn't successful, we return false
     * @throws Exception If something to do with our cryptography wasn't supported or we were handed a bogus file
     *
    public boolean encrypt(String fileName, String password) throws Exception
    {
        // Check to see whether we were handed an unsupported file or just a plain-ol' bogus file
        if (!allowed_files.containsKey(fileName) || !new File(fileName).exists())
        {
            throw new Exception();
        }

        //Get the associated passwords for our file and hash the password we were given above
        PasswordFiles pf = allowed_files.get(fileName);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes("utf-8"));
        byte[] dig = md.digest();

        //If the passwords match, then the password we were given is, indeed, the correct password for our use
        if (Arrays.equals(dig, pf.getWriteKey()))
        {
            //Therefore, we may as well start by reading in our file to a byte array before we encrypt it all
            byte[] contents = Utils.read_file(fileName).getBytes("utf-8");

            //Now, taking the key that we were given, we turn it into a secret key using whatever algorithm is named
            // below, because heaven knows if I'm going to spell it out at all...
            //
            //Incidentally, heaven says 'no' to that.
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec ks = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKey tmp = skf.generateSecret(ks);
            SecretKey passK = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher kc = Cipher.getInstance("AES");
            kc.init(Cipher.DECRYPT_MODE, passK);

            //Boot up the AES cipher and give it a key which is found after decrypting a stored secret key that
            // sounds a lot more complicated than it is, and yet is far too complicated for what it does.
            Cipher c = Cipher.getInstance("AES");
            SecretKey k = new SecretKeySpec(kc.doFinal(pf.getSecretWriteKey()), "AES");
            c.init(Cipher.ENCRYPT_MODE, k);

            //Encrypt the file
            byte[] enc = c.doFinal(contents);

            //And sign the encrypted contents of the file with a MAC so that we know the contents of the file haven't
            // been tampered with at all when we decrypt it later
            Mac mac = Mac.getInstance("HmacSHA256");
            Key macKey = new SecretKeySpec(pf.getWriteKey(), "AES");
            mac.init(macKey);
            byte[] sig = mac.doFinal(enc);

            //Start our files with their respective endings
            File encF = new File(fileName + ".enc");
            File sigF = new File(fileName + ".sig");

            //Write out our encoded file
            FileOutputStream fos = new FileOutputStream(encF);
            fos.write(new sun.misc.BASE64Encoder().encode(enc).getBytes("utf-8"));
            fos.flush();
            fos.close();

            //Write out our signature
            fos = new FileOutputStream(sigF);
            fos.write(new sun.misc.BASE64Encoder().encode(sig).getBytes("utf-8"));
            fos.flush();
            fos.close();

            //And delete the original file before we bugger off out of here
            Path p = new File(fileName).toPath();
            Files.delete(p);

            return true;
        }
        //Since the else was triggered, someone made an incorrect guess at the password... Naughty peoples...
        // ...
        // ...
        // ...
        // Hss...
        else
        {
            System.out.println("Incorrect password for file.");
        }
        return false;
    }
*/
}
