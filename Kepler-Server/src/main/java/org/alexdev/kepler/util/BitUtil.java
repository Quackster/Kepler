package org.alexdev.kepler.util;

public class BitUtil {
    private static final int NUMBER_OF_BITS_IN_A_BYTE = 8;
    private static final short MASK_TO_BYTE = 0xFF;

    public static byte[] intToBytes(int i) {
        byte[] bytes = new byte[4];
        bytes[3] = (byte) (i & MASK_TO_BYTE);
        i >>= NUMBER_OF_BITS_IN_A_BYTE;
        bytes[2] = (byte) (i & MASK_TO_BYTE);
        i >>= NUMBER_OF_BITS_IN_A_BYTE;
        bytes[1] = (byte) (i & MASK_TO_BYTE);
        i >>= NUMBER_OF_BITS_IN_A_BYTE;
        bytes[0] = (byte) (i & MASK_TO_BYTE);

        return bytes;
    }

    private static int bytesToInt(byte A, byte B, byte C, byte D) {
        int i = (D & MASK_TO_BYTE);
        i |= ((C & MASK_TO_BYTE) << 8);
        i |= ((B & MASK_TO_BYTE) << 16);
        i |= ((A & MASK_TO_BYTE) << 24);

        return i;
    }

    public static int bytesToInt(byte[] bytes) {
        return bytesToInt(bytes[0], bytes[1], bytes[2], bytes[3]);
    }
}