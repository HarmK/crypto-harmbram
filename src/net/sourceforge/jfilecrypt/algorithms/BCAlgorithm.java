package net.sourceforge.jfilecrypt.algorithms;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
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
		if (algName.equals("SkipJack")) {
			keyLength = 8;
		} else if (algName.equals("TwoFish")) {
			keyLength = 16;
		} else if (algName.equals("Salsa20")) {
			keyLength = 32;
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
		return 10;
	}

	@Override
	public String getLicense() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return algName;
	}

	@Override
	public String getSuffix() {
		return ".jf" + algName.toLowerCase();
	}

	@Override
	public String getWebsite() {
		// TODO Auto-generated method stub
		return null;
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
			cipher = Cipher.getInstance(getCipherInitString(), new BouncyCastleProvider());
			Key k = new SecretKeySpec(PasswordUtil.getKeyWithRightLength(password, getKeyLength()).getBytes(), algName);
			if (!algName.equalsIgnoreCase("Salsa20")) {
				IvParameterSpec ivs = new IvParameterSpec(getIV());
				cipher.init(Cipher.DECRYPT_MODE, k, ivs);
			} else {
				cipher.init(Cipher.DECRYPT_MODE, k);
			}
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
			if (!algName.equalsIgnoreCase("Salsa20")) {
				IvParameterSpec ivs = new IvParameterSpec(getIV());
				cipher.init(Cipher.ENCRYPT_MODE, k, ivs);
			} else {
				cipher.init(Cipher.ENCRYPT_MODE, k);
			}
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
	

	private byte[] getIV() {
		if (algName.equalsIgnoreCase("TwoFish")) {
            byte res[] = new byte[16];
            for (int i = 0; i < res.length; i++) {
                res[i] = IV[i % IV.length];
            }
            return res;
		}else if (algName.equalsIgnoreCase("SkipJack")) {
            byte res[] = new byte[8];
            for (int i = 0; i < res.length; i++) {
                res[i] = IV[i % IV.length];
            }
            return res;
		}
		return IV;
		

		// return IV;
	}

	private String getCipherInitString() {
		if (algName.equals("SkipJack")) {
			return "SKIPJACK/CBC/PKCS5Padding";
		} else if (algName.equals("TwoFish")) {
			return "TWOFISH/CBC/PKCS5Padding";
		} else if (algName.equals("Salsa20")) {
			return "Salsa20"; // stream
		} else {
			return algName;
		}
	}

}
