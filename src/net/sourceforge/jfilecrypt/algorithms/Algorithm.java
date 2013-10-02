package net.sourceforge.jfilecrypt.algorithms;

import java.io.*;

/**
 * Every Encryption-/Decryption-Algorithm used by jFileCrypt must implement 
 * this interface, which provides methods for calling the encryption/decryption
 * and for getting information about it (Name , Author, License, ...) .
 * @since 0.2.0
 */

public interface Algorithm { 
    //@TODO: write Doc
    public boolean initEncrypt(String password);

    public boolean initDecrypt(String password);

    public byte[] encryptBytes(byte[] input);

    public byte[] decryptBytes(byte[] input);

    public InputStream getDecryptionStream(InputStream in);

    public OutputStream getEncryptionStream(OutputStream out);

    public String getSuffix();

    /**
     * returns the EncryptionModes supported by this algorithm.
     * If it can handle more than one EncryptionMode, the user can
     * decide which to use. The default is MODE_STREAM.
     * @return preffered EncryptionMode
     */

    public EncryptionMode[] getEncryptionMode();

    /**
     * returns the preffered block size when using
     * block-wise encryption. If this algorithm does not support
     * block-wise encryption, it should return -1.
     * @return preffered block size
     */
     public int getBlockSize();
    /**
     * returns the URL of the author's website.
     * @return the author's website's URL
     */
    public String getWebsite();
    /**
     * returns the name of the Algorithm
     * @return for example "Twofish"
     */
    public String getName();
    /**
     * Returns the length which the key must have. Return 0 if key length is variable.
     * @return exact length of the key
     */
    public int getKeyLength();
    /**
     * returns the name of the license.
     * This method should only return the name, NOT! the whole license text.
     * @return the name of the license, for example "GPLv3"
     */
    public String getLicense();
    /**
     * returns the name of the author
     * @return the name of the author, for example "John Doe"
     */
    public String getAuthor();
}
