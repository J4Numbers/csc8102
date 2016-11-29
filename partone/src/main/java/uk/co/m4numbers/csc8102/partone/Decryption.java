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

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileOutputStream;

import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.KeySpec;

import java.util.Arrays;

/**
 * Class Name - Decryption
 * Package - uk.co.m4numbers.csc8102.partone
 * Desc of Class -
 * Author(s) - M. D. Ball
 * Last Mod: 29/11/2016
 */
public class Decryption {

    /**
     * Decrypt and check a file for validity according to its associated signature
     *
     * @param fileName The file that we're decrypting - must end with .enc and must be in our approved lists
     * @param password The password (read) that is going to prove that this person has the permissions to look at this
     *                 file
     * @return Whether or not we succeeded in opening up this not-so-classified information
     * @throws Exception When we're handed a bogus file, or if the crypto libraries complain at us for some reason
     */
    public boolean decryptFile(String fileName, String password) throws Exception
    {
        //Get the basename of the file we're looking at (everything before the .enc), and plug it into our check
        // to make sure this is a verified file and actually exists too
        String baseName = fileName.split("\\.")[0];
        if (!allowed_files.containsKey(baseName) || !new File(fileName).exists())
        {
            throw new Exception();
        }

        //Check the password through the hash
        PasswordFiles pf = allowed_files.get(baseName);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes());
        byte[] dig = md.digest();

        //If the password hash and the stored hash match, then they can look at the file, but no touchy... well,
        // that's not true, but they can't re-encrypt if they don't have the write password, so eh.
        if (Arrays.equals(dig, pf.getReadKey()))
        {
            //Read in the contents of the encrypted file and decrypt the base64 encoded file
            byte[] contents = new sun.misc.BASE64Decoder().decodeBuffer(Utils.readFile(fileName));

            //Once again, we get our file key through a decryption of an encrypted block somewhere, meaning that
            // we need to use our password as a key to get that decryption before we do anything else
            //
            //Remember when coding was simple? No... me neither...
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec ks = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKey tmp = skf.generateSecret(ks);
            SecretKey passK = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher kc = Cipher.getInstance("AES");
            kc.init(Cipher.DECRYPT_MODE, passK);

            //Boot up the key with the decrypted key we get from our password we gave it
            Key k = new SecretKeySpec(kc.doFinal(pf.getSecretReadKey()), "AES");

            //And check the signature of the encrypted message before we decrypt it
            Mac mac = Mac.getInstance("HmacSHA256");
            Key macKey = new SecretKeySpec(pf.getWriteKey(), "AES");
            mac.init(macKey);
            byte[] sig = mac.doFinal(contents);
            byte[] oldSig = new sun.misc.BASE64Decoder().decodeBuffer(Utils.readFile(baseName + ".sig"));

            //And, if they're not equal, foul play has occurred somewhere and we cancel the operation pronto
            if (!Arrays.equals(sig, oldSig))
            {
                System.out.println("Incorrect signature associated with encoded file...");
                return false;
            }

            //Boot up AES
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, k);

            //Decrypt the data
            byte[] decr = c.doFinal(contents);

            //Create the original file for us to write to
            File decrF = new File(baseName);

            //And write the decrypted data to it before getting out of dodge
            FileOutputStream fos = new FileOutputStream(decrF);
            fos.write(decr);
            fos.flush();
            fos.close();

            return true;
        }
        //Tch... Someone else put in a wrong password... typical
        else
        {
            System.out.println("Incorrect password for file.");
        }
        return false;
    }

}
