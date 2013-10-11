package net.sourceforge.jfilecrypt.algorithms;

import java.io.*;

import java.security.*;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import net.sourceforge.jfilecrypt.Application;

/**
 * This class provides the jFileCrypt-own encryption-functionality
 * using the Java Cryptography Extensions in javax.crypto and the JCA in java.security.
 * For most of the implementing details, see GenericAlgorithm.
 */
public class JCEAlgorithm implements Algorithm {

    private Cipher cipher = null;
    private String algName;
    private int keyLength;
    private final static byte[] IV = new byte[]{0x53, 0x45, 0x43, 0x55, 0x52, 0x49, 0x54, 0x59};//ASCII code of SECURITY

    public JCEAlgorithm() {
        this("Blowfish");
    }

    public JCEAlgorithm(String name) {
        algName = name;
        if (algName.equals("Blowfish")) {
            keyLength = 0;
        } else if (algName.equals("DES")) {
            keyLength = 8;
        } else if (algName.equals("TripleDES")) {
            keyLength = 24;
        } else if (algName.equals("AES")) {
            keyLength = 16;
        } else if (algName.equals("RC4")) {
            keyLength = 16;
        } else {
            Application.getController().displayError(Application.getResourceBundle().getString("unknown_alg_title"), Application.getResourceBundle().getString("unknown_alg_text"));
            //System.exit(Application.EXIT_UNKNOWN_ALGORITHM);
        }
    }

    public byte[] decryptBytes(byte[] buffer) {
    	return null;
    }

    public InputStream getDecryptionStream(InputStream in) {
        CipherInputStream cis = new CipherInputStream(in, cipher);
        return cis;
    }

    public byte[] encryptBytes(byte[] buffer) {
        return null;
    }

    public OutputStream getEncryptionStream(OutputStream out) {
        CipherOutputStream cos = new CipherOutputStream(out, cipher);
        return cos;
    }

    public int getBlockSize() {
        return -1;
    }

    public boolean initDecrypt(String password) 
    {
        if (password == null || password.length() < 1) 
        {
            Application.getController().displayError(Application.getResourceBundle().getString("password_not_null_title"),
                    Application.getResourceBundle().getString("password_not_null_message"));
            return false;
        }
        try 
        {
            cipher = Cipher.getInstance(getCipherInitString());
            Key k = new SecretKeySpec(PasswordUtil.getKeyWithRightLength(password, getKeyLength()).getBytes(), algName);
            if (!algName.equalsIgnoreCase("RC4")) {
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

    public boolean initEncrypt(String password) {
        if (password == null || password.length() < 1) {
            Application.getController().displayError(Application.getResourceBundle().getString("password_not_null_title"),
                    Application.getResourceBundle().getString("password_not_null_message"));
            return false;
        }
        try {
            cipher = Cipher.getInstance(getCipherInitString());
            Key k = new SecretKeySpec(PasswordUtil.getKeyWithRightLength(password, getKeyLength()).getBytes(), algName);
            if (!algName.equalsIgnoreCase("RC4")) {
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

    public String getAuthor() {
        return "jFileCrypt Developers";
    }

    public int getKeyLength() {
        return keyLength;
    }

    public String getLicense() {
        return "GNU GPLv2";
    }

    public String getName() {
        return algName;
    }

    public String getSuffix() {
        return ".jf" + algName.toLowerCase();
    }

    public String getWebsite() {
        return Application.getResourceBundle().getString("url_" + algName.toLowerCase());
    }

    public EncryptionMode[] getEncryptionMode() {
        return new EncryptionMode[]{EncryptionMode.MODE_STREAM};
    }

    private String getCipherInitString() {
        if (algName.equals("Blowfish")) {
            return "Blowfish/CBC/PKCS5Padding";
        } else if (algName.equals("DES")) {
            return "DES/CBC/PKCS5Padding";
        } else if (algName.equals("TripleDES")) {
            return "TripleDES/CBC/PKCS5Padding";
        } else if (algName.equals("AES")) {
            return "AES/CBC/PKCS5Padding";
        } else if (algName.equals("RC4")) {
            return "RC4"; //stream chiffre
        } else {
            return algName;
        }
    }

    private byte[] getIV() {
        if (algName.equals("Blowfish")) {
            return IV;
        } else if (algName.equals("DES")) {
            return IV;
        } else if (algName.equals("TripleDES")) {
            return IV;
        } else if (algName.equals("AES")) {
            byte res[] = new byte[16];
            for (int i = 0; i < res.length; i++) {
                res[i] = IV[i % IV.length];
            }
            return res;
        } else if (algName.equals("RC4")) {
            return null;
        } else {
            return new byte[0];
        }
    }
}
