package com.example.diffie_hellman_key_exchange.logic;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

public class AESEncryption {

    byte[] keyBytes;
    Cipher cipher;
    SecretKeySpec key;

    public AESEncryption() { //This constructor initializes an AES encryptor, and I used BouncyCastleProvider for the cryptographic operations provider

        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC"); //Cipher represents an encryption algorithm, and in the getInstance we define how to transform the text and what provider to use - PKCS5Padding is needed because otherwise it wouldn't work as the texts are not the same length
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createKey(byte[] keyBytes) { //Here we generate a SecretKeySpec key to crypt but based on the received bytearray made out of the shared key
        this.keyBytes = keyBytes;
        key = new SecretKeySpec(keyBytes, "AES");
    }

    public byte[] encrypt(byte[] plainText) { //We define in the cipher that we want to Encrypt and we pass the SecretSpecKey (generated based on the shared key)
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainText);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public byte[] decrypt(byte[] codedText) { //We define in the cipher that we want to Decrypt and we pass the SecretSpecKey (generated based on the shared key)
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(codedText);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}