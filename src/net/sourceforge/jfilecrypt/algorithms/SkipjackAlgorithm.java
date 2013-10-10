package net.sourceforge.jfilecrypt.algorithms;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * Two fish encryption algorithm
 * 
 * TODO implement methods
 * 
 * @author Harm
 *
 */

public class SkipjackAlgorithm implements Algorithm {

	@Override
	public byte[] decryptBytes(byte[] input) {
		// TODO add decryption
		return input;
	}

	@Override
	public byte[] encryptBytes(byte[] input) {
		// TODO add encryption
		return input;
	}

	@Override
	public String getAuthor() {
		// TODO add
		return "TwoFish";
	}

	@Override
	public int getBlockSize() {
		// TODO Auto-generated method stub
		return 128;
	}

	@Override
	public InputStream getDecryptionStream(InputStream in) {
		// TODO Auto-generated method stub
		return in;
	}

	@Override
	public EncryptionMode[] getEncryptionMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getEncryptionStream(OutputStream out) {
		// TODO Auto-generated method stub
		return out;
	}

	@Override
	public int getKeyLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLicense() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "TwoFish";
	}

	@Override
	public String getSuffix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWebsite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean initDecrypt(String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initEncrypt(String password) {
		// TODO Auto-generated method stub
		return false;
	}

}
