package uk.co.m4numbers.csc8102.partone;

/*
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
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;
import java.util.Arrays;

/**
 * Class Name - EncryptionDecryption
 * Package - uk.co.m4numbers.csc8102.partone
 * Desc of Class - A class centred around the encryption and decryption of
 * a files for the user. All functions within this class allow for the
 * fulfillment of those requirements.
 * Author(s) - M. D. Ball
 * Last Mod: 29/11/2016
 */
public class EncryptionDecryption
{

    /**
     * Derive the two keys from the provided password that we're going to be
     * using for our cipher and hash functions respectively.
     *
     * @param password The password that we hash in order to get our keys and
     *                 codes
     *
     * @return A collection object of a 16-byte AES key and a 16-byte MAC code
     *
     * @throws NoSuchAlgorithmException If SHA-256 doesn't exist or cannot be
     *                                  used
     */
    private DerivedKeys derive_keys(byte[] password)
            throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password);
        byte[] digest = md.digest();
        return new DerivedKeys(
                //AES key
                Utils.split_byte_array(digest, 0, 16),
                //MAC code
                Utils.split_byte_array(digest, 16, 32)
        );
    }

    /**
     * Generate a securely random sequence of 16 bytes using Java's
     * SecureRandom class to act as an initial vector for our CBC bit later on
     *
     * @return 16 bytes of secure pseudo-randomness
     */
    private byte[] generate_initial_vector()
    {
        SecureRandom sr        = new SecureRandom();
        byte[]       ret_bytes = new byte[16];
        sr.nextBytes(ret_bytes);
        return ret_bytes;
    }

    /**
     * Generate a 20-byte message signature according to all the input data
     * that we used to generate this message
     *
     * @param iv         The initial vector (hashed with the ciphertext)
     * @param ciphertext The ciphertext itself (hashed with the iv)
     * @param mac        The mac code that we use as a secret key for signing the
     *                   message
     *
     * @return A 20-byte HMAC output to be attached to the encrypted file
     *
     * @throws NoSuchAlgorithmException If HmacSHA1 doesn't exist
     * @throws InvalidKeyException      If HmacSHA1 can't be used on the key
     */
    private byte[] generate_hmac(byte[] iv, byte[] ciphertext, byte[] mac)
            throws NoSuchAlgorithmException, InvalidKeyException
    {
        Mac           mac_cipher = Mac.getInstance("HmacSHA1");
        SecretKeySpec signing    = new SecretKeySpec(mac, "HmacSHA1");
        mac_cipher.init(signing);

        return mac_cipher
                .doFinal(Utils.concatenate_byte_arrays(iv, ciphertext));
    }

    /**
     * Generate the ciphertext from the given data via the cipher as given in
     * the coursework specification (of AES in CBC mode using PKC5 padding).
     *
     * @param iv        16-byte random data
     * @param key       16-byte AES key
     * @param plaintext plaintext bytes to encrypt
     *
     * @return The ciphertext according to our inputs
     */
    private byte[] generate_ciphertext(byte[] iv, byte[] key, byte[] plaintext)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException
    {
        SecretKey aes_key    = new SecretKeySpec(key, "AES");
        Cipher    aes_cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aes_cipher.init(Cipher.ENCRYPT_MODE, aes_key, new IvParameterSpec(iv));

        return aes_cipher.doFinal(plaintext);
    }

    /**
     * Regenerate the plaintext now that we have all the data that was used
     * to generate it in the first place. Basically just use the decrypt mode
     * of the AES/CBC block cipher and we're golden.
     *
     * @param iv         16-byte random data
     * @param key        16-byte AES key
     * @param ciphertext plaintext bytes to encrypt
     *
     * @return The plaintext that this ciphertext once was
     */
    private byte[] regenerate_plaintext(
            byte[] iv, byte[] key, byte[] ciphertext
    )
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException
    {
        SecretKey aes_key    = new SecretKeySpec(key, "AES");
        Cipher    aes_cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aes_cipher.init(Cipher.DECRYPT_MODE, aes_key, new IvParameterSpec(iv));

        return aes_cipher.doFinal(ciphertext);
    }

    /**
     * Encrypt a file with a given password, deleting the original file in
     * the process
     *
     * @param filename The file that we're going to encrypt
     * @param password The password we're going to encrypt it with
     *
     * @throws Exception If something goes wrong within individual methods
     */
    public void encrypt(String filename, String password) throws Exception
    {
        //Read in the plaintext and get the two halves of our keys for later
        String      plaintext = Utils.read_file(filename);
        DerivedKeys key_set   = derive_keys(password.getBytes("utf-8"));

        byte[] iv = generate_initial_vector();
        byte[] ciphertext = generate_ciphertext(iv, key_set.aes_key,
                                                plaintext.getBytes("utf-8")
        );
        byte[] hmac = generate_hmac(iv, ciphertext, key_set.mac_code);

        //Join all of our parts together to create our final output byte array
        // that we pass to be written to file
        byte[] joined = Utils.concatenate_byte_arrays(
                Utils.concatenate_byte_arrays(iv, ciphertext), hmac);

        //Write to the encrypted file in a more stable way
        Utils.write_to_file(
                new sun.misc.BASE64Encoder().encode(joined).getBytes("utf-8"),
                filename + ".8102"
        );

        //Now delete the original file
        if (!Utils.delete_file(filename))
        {
            throw new IOException("File deletion failed");
        }
    }

    /**
     * When given a file that can be decrypted and a password, attempt to
     * decrypt said file with said password
     *
     * @param filename Said file that we're going to try and decrypt
     * @param password The password we're going to decrypt it with
     *
     * @throws Exception If something went wrong in lower methods
     */
    public void decrypt(String filename, String password) throws Exception
    {
        //Read in and decode the data from the given file
        byte[] encrypted_file = new sun.misc.BASE64Decoder().decodeBuffer(
                Utils.read_file(filename));

        //Split apart the encrypted file into its constituent parts
        byte[] iv = Utils.split_byte_array(encrypted_file, 0, 16);
        byte[] ciphertext = Utils.split_byte_array(encrypted_file, 16,
                                                   encrypted_file.length - 20
        );
        byte[] hmac = Utils
                .split_byte_array(encrypted_file, encrypted_file.length - 20,
                                  encrypted_file.length
                );

        //Derive the keys and generate a HMAC from what we have currently
        DerivedKeys key_set   = derive_keys(password.getBytes("utf-8"));
        byte[]      test_hmac = generate_hmac(iv, ciphertext, key_set.mac_code);

        //If our generated HMAC doesn't match the one in the file... we have
        // problems
        if (!Arrays.equals(test_hmac, hmac))
        {
            throw new Exception("Wrong password or possibly corrupted file");
        }

        //Since we've reached this point, everything's valid, so let's
        // regenerate the plaintext from the ciphertext
        byte[] plaintext = regenerate_plaintext(
                iv, key_set.aes_key, ciphertext);

        //And write the decrypted output to the original filename
        Utils.write_to_file(
                plaintext, filename.substring(0, filename.length() - 5));

        //Finally, delete the old encrypted file and finish up
        if (!Utils.delete_file(filename))
        {
            throw new IOException("File deletion failed");
        }
    }

}
