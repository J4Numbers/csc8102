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
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;
import java.security.*;

/**
 * Class Name - Encryption
 * Package - uk.co.m4numbers.csc8102.partone
 * Desc of Class -
 * Author(s) - M. D. Ball
 * Last Mod: 29/11/2016
 */
public class Encryption {

    public void encrypt(String filename, String password) throws Exception
    {
        String plaintext = Utils.read_file(filename);
        DerivedKeys key_set = derive_keys(password.getBytes("utf-8"));

        byte[] iv = generate_initial_vector();
        byte[] ciphertext = generate_ciphertext(iv, key_set.aes_key,
                plaintext.getBytes("utf-8"));
        byte[] hmac = generate_hmac(iv, ciphertext, key_set.mac_code);

        byte[] joined = Utils.concatenate_byte_arrays(
                Utils.concatenate_byte_arrays(iv, ciphertext), hmac);

        write_to_file(joined, filename);
    }

    private byte[] generate_initial_vector()
    {
        SecureRandom sr = new SecureRandom();
        byte[] ret_bytes = new byte[16 * 8];
        sr.nextBytes(ret_bytes);
        return ret_bytes;
    }

    private byte[] generate_hmac(byte[] iv, byte[] ciphertext, byte[] mac)
            throws NoSuchAlgorithmException, InvalidKeyException
    {
        Mac mac_cipher = Mac.getInstance("HmacSHA256");
        SecretKeySpec signing = new SecretKeySpec(mac, "HmacSHA256");
        mac_cipher.init(signing);

        return mac_cipher.doFinal(Utils.concatenate_byte_arrays(iv, ciphertext));
    }

    private DerivedKeys derive_keys(byte[] password) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA256");
        md.update(password);
        byte[] digest = md.digest();
        return new DerivedKeys(Utils.split_byte_array(digest, 0, 16),
                Utils.split_byte_array(digest, 16, 32));
    }

    /**
     *
     * @param iv 16-byte random data
     * @param key 16-byte AES key
     * @param plaintext plaintext bytes to encrypt
     * @return Ciphertext
     */
    private byte[] generate_ciphertext(byte[] iv, byte[] key, byte[] plaintext)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        SecretKey aes_key = new SecretKeySpec(key, "AES");
        Cipher aes_cipher = Cipher.getInstance("AES/CBC/PKC5");
        aes_cipher.init(Cipher.ENCRYPT_MODE, aes_key, new IvParameterSpec(iv));

        return aes_cipher.doFinal(plaintext);
    }

    private void write_to_file(byte[] file_contents, String file_name) throws IOException
    {
        File encrypted_file = new File(file_name + ".8102");
        FileOutputStream fos = new FileOutputStream(encrypted_file);
        fos.write(new sun.misc.BASE64Encoder().encode(file_contents).getBytes("utf-8"));
        fos.flush();
        fos.close();

        if (!new File(file_name).delete())
        {
            throw new IOException("File deletion failed");
        }
    }

}
