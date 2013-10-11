package net.sourceforge.jfilecrypt.algorithms;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import net.sourceforge.jfilecrypt.Application;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

;
/**
 * 
 * Implements multiple bouncy castle algorithms
 * 
 * @author Harm
 * 
 */

public class BCAlgorithm implements Algorithm {

	private Cipher cipher = null;
	private String algName;
	private int keyLength;

	private final static byte[] IV = new byte[] { 0x62, 0x6f, 0x75, 0x6e, 0x63, 0x79, 0x63, 0x61, 0x73, 0x74, 0x6c,
			0x65 };// ASCII code of bouncycastle

	public BCAlgorithm(String name) {
		super();
		algName = name;
		if (algName.equalsIgnoreCase("SkipJack")) {
			keyLength = 8;
		} else if (algName.equalsIgnoreCase("TwoFish")) {
			keyLength = 16;
		} else if (algName.equalsIgnoreCase("Salsa20")) {
			keyLength = 16;
		}
	}

	@Override
	public byte[] decryptBytes(byte[] input) {
		return null;
	}

	@Override
	public byte[] encryptBytes(byte[] input) {
		return null;
	}

	@Override
	public String getAuthor() {
		// TODO add
		return "Bouncy Castle";
	}
	
	@Override
	public int getBlockSize() {
		// TODO Auto-generated method stub
		return -1;
	}

	// deze doen
	@Override
	public InputStream getDecryptionStream(InputStream in) {
		CipherInputStream cis = new CipherInputStream(in, cipher);
		return cis;
	}

	@Override
	public EncryptionMode[] getEncryptionMode() {
		return new EncryptionMode[] { EncryptionMode.MODE_STREAM };
	}

	@Override
	public OutputStream getEncryptionStream(OutputStream out) {
		CipherOutputStream cos = new CipherOutputStream(out, cipher);
		return cos;
	}

	@Override
	public int getKeyLength() {
		// TODO Auto-generated method stub
		return keyLength;
	}

	@Override
	public String getLicense() {
		return "MIT";
	}

	@Override
	public String getName() {
		return algName;
	}

	@Override
	public String getSuffix() {
		return ".jf" + algName.toLowerCase();
	}

	@Override
	public String getWebsite() {
		return "http://www.bouncycastle.org";
	}

	@Override
	public boolean initDecrypt(String password) {

		if (password == null || password.length() < 1) {
			Application.getController().displayError(
					Application.getResourceBundle().getString("password_not_null_title"),
					Application.getResourceBundle().getString("password_not_null_message"));
			return false;
		}
		try {
			cipher = Cipher.getInstance(getCipherInitString(),new BouncyCastleProvider());
			Key k = new SecretKeySpec(PasswordUtil.getKeyWithRightLength(password, getKeyLength()).getBytes(), algName);
			
				IvParameterSpec ivs = new IvParameterSpec(getIV());
				cipher.init(Cipher.DECRYPT_MODE, k, ivs);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return false;
		} 

		return true;
	}

	@Override
	public boolean initEncrypt(String password) {
		if (password == null || password.length() < 1) {
			Application.getController().displayError(
					Application.getResourceBundle().getString("password_not_null_title"),
					Application.getResourceBundle().getString("password_not_null_message"));
			return false;
		}
		try {
			cipher = Cipher.getInstance(getCipherInitString(), new BouncyCastleProvider());
			Key k = new SecretKeySpec(PasswordUtil.getKeyWithRightLength(password, getKeyLength()).getBytes(), algName);
			System.out.println(PasswordUtil.getKeyWithRightLength(password, getKeyLength()).getBytes());
			
				IvParameterSpec ivs = new IvParameterSpec(getIV());
				cipher.init(Cipher.ENCRYPT_MODE, k, ivs);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private byte[] trimBytes(int length)
	{
		 byte res[] = new byte[length];
         for (int i = 0; i < res.length; i++) {
             res[i] = IV[i % IV.length];
         }
         return res;
	}

	private byte[] getIV() {
		if (algName.equalsIgnoreCase("TwoFish")) {
			return trimBytes(16);
		}else if (algName.equalsIgnoreCase("SkipJack")) {
			return trimBytes(8);
		}else if (algName.equalsIgnoreCase("Salsa20")) {
			return trimBytes(8);
		}
		return IV;
	}

	private String getCipherInitString() {
		if (algName.equalsIgnoreCase("SkipJack")) {
			return "SKIPJACK/CBC/PKCS5Padding";
		} else if (algName.equalsIgnoreCase("TwoFish")) {
			return "TWOFISH/CBC/PKCS5Padding";
		} else if (algName.equalsIgnoreCase("Salsa20")) {
			return "Salsa20"; // stream
		} else {
			return algName;
		}
	}

}
