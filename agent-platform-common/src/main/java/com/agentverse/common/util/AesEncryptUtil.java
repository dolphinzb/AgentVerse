/*
 * Decompiled with CFR 0.152.
 */
package com.agentverse.common.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryptUtil {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static String encryptionKey;

    public static void setEncryptionKey(String key) {
        encryptionKey = key;
    }

    public static String getEncryptionKey() {
        return encryptionKey;
    }

    public static String encrypt(String plaintext) {
        try {
            byte[] keyBytes = AesEncryptUtil.normalizeKey(encryptionKey);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(1, (Key)keySpec, new GCMParameterSpec(128, iv));
            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            return Base64.getEncoder().encodeToString(combined);
        }
        catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }

    public static String decrypt(String ciphertext) {
        try {
            byte[] keyBytes = AesEncryptUtil.normalizeKey(encryptionKey);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            byte[] combined = Base64.getDecoder().decode(ciphertext);
            byte[] iv = new byte[12];
            byte[] encrypted = new byte[combined.length - 12];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(2, (Key)keySpec, new GCMParameterSpec(128, iv));
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            throw new RuntimeException("AES decryption failed", e);
        }
    }

    private static byte[] normalizeKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] normalized = new byte[32];
        System.arraycopy(keyBytes, 0, normalized, 0, Math.min(keyBytes.length, 32));
        return normalized;
    }
}

