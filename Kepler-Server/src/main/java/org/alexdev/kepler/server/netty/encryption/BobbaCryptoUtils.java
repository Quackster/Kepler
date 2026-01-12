package org.alexdev.kepler.server.netty.encryption;

import java.math.BigInteger;

/**
 * Reverse Engineered by <a href="https://github.com/UnfamiliarLegacy">Mikee</a>.
 */
public class BobbaCryptoUtils {

    public static byte[] getKeyBytes(BigInteger value) {
        byte[] arr = value.toByteArray();

        if (arr[0] == 0) {
            byte[] bytesWithoutSignBit = new byte[arr.length - 1];
            System.arraycopy(arr, 1, bytesWithoutSignBit, 0, bytesWithoutSignBit.length);
            arr = bytesWithoutSignBit;
        }

        return arr;
    }

}
