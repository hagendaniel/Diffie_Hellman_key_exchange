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

    public AESEncryption(/*int key*/) {

        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "BC"); //was no padding /NoPadding"
            //this.key = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
            System.out.println(e.getMessage());
        }
    }

    public void createKey(byte[] keyBytes) {
        this.keyBytes = keyBytes;
        key = new SecretKeySpec(keyBytes, "AES");
    }

    public byte[] encrypt(byte[] plainText) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainText);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public byte[] decrypt(byte[] codedText) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(codedText);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}