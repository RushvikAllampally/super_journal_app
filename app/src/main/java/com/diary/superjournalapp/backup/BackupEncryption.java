package com.diary.superjournalapp.backup;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Handles AES-256-GCM encryption and decryption for backup files
 * Includes GZIP compression for file size reduction
 */
public class BackupEncryption {
    
    private static final String TAG = "BackupEncryption";
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
    
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 16; // 128 bits
    private static final int KEY_LENGTH = 256; // 256 bits
    private static final int PBKDF2_ITERATIONS = 100000;
    private static final int SALT_LENGTH = 32; // 256 bits
    
    /**
     * Encrypts and compresses a database file
     * @param inputFile The database file to encrypt
     * @param password The password for encryption
     * @return EncryptedData object containing encrypted bytes, salt, and IV
     */
    public static EncryptedData encryptDatabase(File inputFile, String password) throws Exception {
        try {
            // Read and compress the database file
            byte[] compressedData = compressFile(inputFile);
            Log.d(TAG, "Original file size: " + inputFile.length() + " bytes");
            Log.d(TAG, "Compressed size: " + compressedData.length + " bytes");
            
            // Generate salt and derive key
            byte[] salt = generateSalt();
            SecretKey secretKey = deriveKeyFromPassword(password, salt);
            
            // Generate IV for GCM
            byte[] iv = generateIV();
            
            // Initialize cipher for encryption
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
            
            // Encrypt the compressed data
            byte[] encryptedData = cipher.doFinal(compressedData);
            
            Log.d(TAG, "Encryption completed. Encrypted size: " + encryptedData.length + " bytes");
            
            return new EncryptedData(encryptedData, salt, iv);
            
        } catch (Exception e) {
            Log.e(TAG, "Encryption failed", e);
            throw new Exception("Failed to encrypt database: " + e.getMessage(), e);
        }
    }
    
    /**
     * Decrypts and decompresses backup data
     * @param encryptedData The encrypted data object
     * @param password The password for decryption
     * @return Decompressed database file bytes
     */
    public static byte[] decryptDatabase(EncryptedData encryptedData, String password) throws Exception {
        try {
            // Derive key from password and salt
            SecretKey secretKey = deriveKeyFromPassword(password, encryptedData.getSalt());
            
            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, encryptedData.getIv());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
            
            // Decrypt the data
            byte[] decryptedCompressed = cipher.doFinal(encryptedData.getEncryptedBytes());
            
            // Decompress the data
            byte[] decompressedData = decompressData(decryptedCompressed);
            
            Log.d(TAG, "Decryption completed. Decompressed size: " + decompressedData.length + " bytes");
            
            return decompressedData;
            
        } catch (Exception e) {
            Log.e(TAG, "Decryption failed", e);
            throw new Exception("Failed to decrypt database: " + e.getMessage(), e);
        }
    }
    
    /**
     * Compresses file using GZIP
     */
    private static byte[] compressFile(File file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (FileInputStream fis = new FileInputStream(file);
             GZIPOutputStream gzipOut = new GZIPOutputStream(baos)) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                gzipOut.write(buffer, 0, bytesRead);
            }
        }
        
        return baos.toByteArray();
    }
    
    /**
     * Decompresses GZIP data
     */
    private static byte[] decompressData(byte[] compressedData) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (GZIPInputStream gzipIn = new GZIPInputStream(
                new java.io.ByteArrayInputStream(compressedData))) {
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = gzipIn.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
        }
        
        return baos.toByteArray();
    }
    
    /**
     * Derives encryption key from password using PBKDF2
     */
    private static SecretKey deriveKeyFromPassword(String password, byte[] salt) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
    }
    
    /**
     * Generates a secure random salt
     */
    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
    
    /**
     * Generates a secure random IV for GCM
     */
    private static byte[] generateIV() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }
    
    /**
     * Writes encrypted data to file
     */
    public static void writeEncryptedDataToFile(EncryptedData encryptedData, File outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            // Write salt length (4 bytes)
            fos.write(intToBytes(encryptedData.getSalt().length));
            // Write salt
            fos.write(encryptedData.getSalt());
            
            // Write IV length (4 bytes)
            fos.write(intToBytes(encryptedData.getIv().length));
            // Write IV
            fos.write(encryptedData.getIv());
            
            // Write encrypted data
            fos.write(encryptedData.getEncryptedBytes());
        }
    }
    
    /**
     * Reads encrypted data from file
     */
    public static EncryptedData readEncryptedDataFromFile(File inputFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputFile)) {
            // Read salt length
            byte[] saltLengthBytes = new byte[4];
            fis.read(saltLengthBytes);
            int saltLength = bytesToInt(saltLengthBytes);
            
            // Read salt
            byte[] salt = new byte[saltLength];
            fis.read(salt);
            
            // Read IV length
            byte[] ivLengthBytes = new byte[4];
            fis.read(ivLengthBytes);
            int ivLength = bytesToInt(ivLengthBytes);
            
            // Read IV
            byte[] iv = new byte[ivLength];
            fis.read(iv);
            
            // Read remaining encrypted data
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] encryptedBytes = baos.toByteArray();
            
            return new EncryptedData(encryptedBytes, salt, iv);
        }
    }
    
    // Helper methods for byte conversion
    private static byte[] intToBytes(int value) {
        return new byte[] {
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value
        };
    }
    
    private static int bytesToInt(byte[] bytes) {
        return (bytes[0] << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }
    
    /**
     * Data class to hold encrypted data with salt and IV
     */
    public static class EncryptedData {
        private final byte[] encryptedBytes;
        private final byte[] salt;
        private final byte[] iv;
        
        public EncryptedData(byte[] encryptedBytes, byte[] salt, byte[] iv) {
            this.encryptedBytes = encryptedBytes;
            this.salt = salt;
            this.iv = iv;
        }
        
        public byte[] getEncryptedBytes() { return encryptedBytes; }
        public byte[] getSalt() { return salt; }
        public byte[] getIv() { return iv; }
        
        public long getTotalSize() {
            return encryptedBytes.length + salt.length + iv.length + 8; // +8 for length headers
        }
    }
}
