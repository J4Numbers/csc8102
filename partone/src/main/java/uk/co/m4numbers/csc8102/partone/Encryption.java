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
 * Class Name - PasswordProtect
 * Package - uk.co.m4numbers.csc3124.q1
 * Desc of Class - General coverall class for Question 1. Has three functions: encryption, decryption, and consistency
 *                 checking of the files within the executed folder.
 * Author(s) - M. D. Ball
 * Last Mod: 06/05/2016
 */
public class Encryption {

    /**
     * A map of the starting files that are available to be encrypted, decrypted, and checked. If a file exists
     * in the folder whose base name isn't on this list, they get kicked out for some reason or another
     */
    private Map<String, PasswordFiles> allowed_files;

    /**
     * Salt array for the purpose of decrypting our encrypted file key
     */
    private byte[] salt = new byte[]{29, -99, -107, -87, -75, -90, 35, 121};

    /**
     * Constructor where we describe our ideal folder contents and construct the password hashes that access those
     * files for encryption and decryption. These four files are: "BS13", "FCR", "SVS", and "TA".
     */
    public PasswordProtect() throws Exception
    {
        allowed_files = new HashMap<>();
        allowed_files.put("BS13", new PasswordFiles(
                "BS13", "d4b2c946ea993e451983d474a3314316a7a4ff0c6b21701cb67f91f1ee92449a",
                "0f550d7d3470b65142eaa839477ed7b87d33d9ae82673b5d5a4e51e8389fc06c",
                "FCFFB82ADBC3B4ACF64E155FB0D0653E989C35214C363F8EEA4E3BE9DE031160",
                "52F255C5DD9721222199903AA62CA280F23CAEA83986BBCA6E35B2318B301BCB"
        ));
        allowed_files.put("FCR", new PasswordFiles(
                "FCR", "ecaf68a723178d85d4b69c3edefca42a38db4ce10aa7dfef74c6004ee58f6abe",
                "a714b795eeafd1adbfa9eae84c99be857eeec35f73e4203ff95c6ac70646b7b3",
                "6825D836D5A617003B083EF811E781D214C883CAD787B6B5F36F0A73F364666E",
                "347CB308AE17B143E484FCD908884225BE7870FC62FB6C929B4D30DAAD82CDB8"
        ));
        allowed_files.put("SVS", new PasswordFiles(
                "SVS", "f7c3989d7c79e83bfab3e5c37a9de6ec950743dcb64dcba16cd8942e7fab3c70",
                "ccb5e024d457d7f57aab4a864a540e21d44f7efbbd74b1b8d2c433cdb48f48d7",
                "997EEC78086C04B61D4AB00B4C8A34B0FA26F420A463AC6515BA65B9D051E18F",
                "5A65F68180DD58643593943CE91C7786AFEB2ACC2A8E72146D24DEC5BB13B244"
        ));
        allowed_files.put("TA", new PasswordFiles(
                "TA", "0059bfc57922c1708b63e31c04589f4b33155c5b24327bcb5b7b25859c84e399",
                "44a626bc3ea2d78e82237930689d8b6fbcda60fbb756afe93e5bebec5a14751d",
                "9734211C3C09583EFAC9FC0D5BB8F0A0E3B44FD7D52916CA6E57835A595374FB",
                "FFE58743A88B564BC65185D7537E9DAF6F04FFD1080899EB47B00FAC45D6DE74"
        ));
    }

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
     */
    public boolean encryptFile(String fileName, String password) throws Exception
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
            byte[] contents = Utils.readFile(fileName).getBytes("utf-8");

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

    /**
     * Run through the folder and do a few things... Delete every unencrypted file (that doesn't have a .enc extension).
     * Check that every signature for the encoded file matches - so simply renaming a file to .enc probably won't help
     * anyone sneak any unencrypted files on here - and delete all pairs which do not match up. Delete every extra file
     * that is in the folder, and tell the user if we are missing any of the original files that need to be here.
     *
     * @return Our success rate on completion
     */
    public boolean check()
    {
        //Get a list of all the files in the folder and make sure that hasn't broken anything
        File dir = new File("./");
        File[] files = dir.listFiles();
        if (files == null)
        {
            return false;
        }

        //Get a list of all the files that are allowed within this folder and have our own set of files that actually
        // are in this folder
        Set<String> aim = allowed_files.keySet();
        Set<String> reality = new HashSet<>();

        //For each file in this folder
        for (File cur : files) {
            try {
                //If the file exists (this is here due to deletion properties later on in the file that could screw
                // things up if it weren't here)
                if (cur.exists()) {
                    String basename = cur.getName().split("\\.")[0];

                    //If the file we're looking at is:
                    // a) Not in the list of allowed files OR
                    // b) Does not end with .enc or .sig
                    // Then bin the file and move on
                    if (!allowed_files.containsKey(basename) || !(cur.getName().endsWith(".enc") ||
                            cur.getName().endsWith(".sig"))) {
                        Files.delete(cur.toPath());
                        continue;
                    }

                    //And check that we haven't already gone over a file pairing
                    if (!reality.contains(basename)) {
                        //Get our associated password information
                        PasswordFiles pf = allowed_files.get(basename);

                        //And read the encoded file
                        byte[] contents = new sun.misc.BASE64Decoder().decodeBuffer(Utils.readFile(basename + ".enc"));

                        //Drag out our mac key
                        Key k = new SecretKeySpec(pf.getWriteKey(), "AES");

                        //And check the signature of the file against what the encrypted file signs to
                        Mac mac = Mac.getInstance("HmacSHA256");
                        mac.init(k);
                        byte[] sig = mac.doFinal(contents);
                        byte[] oldSig = new sun.misc.BASE64Decoder().decodeBuffer(Utils.readFile(basename + ".sig"));

                        //If they don't match, then someone has been tampering with the file and we should delete
                        // both files before we leave
                        if (!Arrays.equals(sig, oldSig)) {
                            System.out.printf("Incorrect signature associated with encoded %s...\n", basename);
                            Files.delete(new File(basename + ".sig").toPath());
                            Files.delete(cur.toPath());
                            continue;
                        }

                        //And, now that we've checked that everything is working, we can add this file to the list
                        // of verified files in this directory
                        reality.add(basename);
                    }
                }
            } catch (Exception e) {
            }
        }

        //Now we need to announce which files are missing in this folder, so, if a file in the aim was not found in
        // our real files, then we announce that that file is missing.
        for (String a: aim)
        {
            if (!reality.contains(a))
            {
                System.out.printf("Missing file: %s\n", a);
            }
        }

        return true;
    }
}
