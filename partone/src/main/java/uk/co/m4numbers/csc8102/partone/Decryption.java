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

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;
import java.security.*;

import java.util.Arrays;

/**
 * Class Name - Decryption
 * Package - uk.co.m4numbers.csc8102.partone
 * Desc of Class -
 * Author(s) - M. D. Ball
 * Last Mod: 29/11/2016
 */
public class Decryption {

    void decrypt(String filename, String password) throws Exception
    {
        byte[] encrypted_file = new sun.misc.BASE64Decoder().decodeBuffer(
                Utils.read_file(filename));

        byte[] iv = Utils.split_byte_array(encrypted_file, 0, 16);
        byte[] ciphertext = Utils.split_byte_array(encrypted_file, 16,
                encrypted_file.length - 20);
        byte[] hmac = Utils.split_byte_array(encrypted_file, encrypted_file.length - 20,
                encrypted_file.length);

        DerivedKeys key_set = derive_keys(password.getBytes("utf-8"));
        byte[] test_hmac = generate_hmac(iv, ciphertext, key_set.mac_code);

        if (!Arrays.equals(test_hmac, hmac))
        {
            throw new Exception("Wrong password or possibly corrupted file " + Utils.byte_array_to_hex_string(test_hmac) + " vs " + Utils.byte_array_to_hex_string(hmac));
        }

        byte[] plaintext = regenerate_plaintext(iv, key_set.aes_key, ciphertext);
        write_to_file(plaintext, filename);
    }

    private DerivedKeys derive_keys(byte[] password) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password);
        byte[] digest = md.digest();
        return new DerivedKeys(Utils.split_byte_array(digest, 0, 16),
                Utils.split_byte_array(digest, 16, 32));
    }

    private byte[] generate_hmac(byte[] iv, byte[] ciphertext, byte[] mac)
            throws NoSuchAlgorithmException, InvalidKeyException
    {
        Mac mac_cipher = Mac.getInstance("HmacSHA1");
        SecretKeySpec signing = new SecretKeySpec(mac, "HmacSHA1");
        mac_cipher.init(signing);

        return mac_cipher.doFinal(Utils.concatenate_byte_arrays(iv, ciphertext));
    }

    /**
     *
     * @param iv 16-byte random data
     * @param key 16-byte AES key
     * @param ciphertext plaintext bytes to encrypt
     * @return Ciphertext
     */
    private byte[] regenerate_plaintext(byte[] iv, byte[] key, byte[] ciphertext)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        SecretKey aes_key = new SecretKeySpec(key, "AES");
        Cipher aes_cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aes_cipher.init(Cipher.DECRYPT_MODE, aes_key, new IvParameterSpec(iv));

        return aes_cipher.doFinal(ciphertext);
    }

    private void write_to_file(byte[] file_contents, String file_name) throws IOException
    {
        File decrypted_file = new File(file_name.substring(0, file_name.length() - 5) );
        FileOutputStream fos = new FileOutputStream(decrypted_file);
        fos.write(file_contents);
        fos.flush();
        fos.close();

        if (!new File(file_name).delete())
        {
            throw new IOException("File deletion failed");
        }
    }

}
