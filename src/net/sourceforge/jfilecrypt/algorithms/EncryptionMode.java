/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.sourceforge.jfilecrypt.algorithms;

/**
 *
 * @author me
 */
public enum EncryptionMode {
    MODE_BLOCK, MODE_STREAM;

    public static EncryptionMode getBestEncryptionMode(EncryptionMode[] modes){
        for(int i = 0; i < modes.length; i++){
            if(modes[i] == MODE_STREAM){
                return MODE_STREAM;
            }
        }
        
        for(int i = 0; i < modes.length; i++){
            if(modes[i] == MODE_STREAM){
                return MODE_BLOCK;
            }
        }
        //not reachable at the moment, but for future additions like
        //encrypt single byte or bitwise encryption (one time pad).
        return modes[0];
    }
}
