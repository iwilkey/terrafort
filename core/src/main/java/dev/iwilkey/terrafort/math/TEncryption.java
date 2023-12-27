package dev.iwilkey.terrafort.math;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Static utilities that facilitate the encryption and decryption of data, mainly save data.
 * @author Ian Wilkey (iwilkey)
 */
public final class TEncryption {
	
	public static final String KEY       = "2F4FwiLDpr6fVQP7HGW/bw==";
	public static final String ALGORITHM = "AES";

	/**
	 * Quickly encrypts given data using engine key, returns a string.
	 */
	public static String encrypt(final String data) {
		final byte[] key = Base64.getDecoder().decode(KEY);
		final SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		byte[] encryptedData = null;
		try {
			encryptedData = cipher.doFinal(data.getBytes());
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return Base64.getEncoder().encodeToString(encryptedData);
	}
	
	/**
	 * Quickly decrypts given data using engine key, returns a string.
	 */
	public static String decrypt(String data) {
		final byte[] key = Base64.getDecoder().decode(KEY);
		final SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		byte[] decryptedData = null;
		try {
			decryptedData = cipher.doFinal(Base64.getDecoder().decode(data));
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return new String(decryptedData);
	}
	
}
