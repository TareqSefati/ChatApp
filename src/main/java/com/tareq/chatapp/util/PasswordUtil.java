package com.tareq.chatapp.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Tareq Sefati on 26-Jul-23
 */
public class PasswordUtil {
    public static String hashPassword(String plainPassword) {
        try {
            // Create a SHA-256 MessageDigest instance
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Get the byte array of the password
            byte[] passwordBytes = plainPassword.getBytes(StandardCharsets.UTF_8);

            // Generate the hash value
            byte[] hashBytes = digest.digest(passwordBytes);

            // Convert the byte array to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
