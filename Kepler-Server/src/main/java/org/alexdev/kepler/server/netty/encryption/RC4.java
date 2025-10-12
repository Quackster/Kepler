package org.alexdev.kepler.server.netty.encryption;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Non-standard RC4 algorithm using base64.
 * Thanks to Joni Aromaa for the original implementation.
 * <a href="https://github.com/aromaa/Skylight3/blob/72ec3a07d126de09f6de4251c91001329f77a8a2/src/Skylight.Server/Net/Crypto/RC4Base64.cs">
 *     https://github.com/aromaa/Skylight3/blob/72ec3a07d126de09f6de4251c91001329f77a8a2/src/Skylight.Server/Net/Crypto/RC4Base64.cs
 * </a>
 * Modified by Mikee (<a href="https://github.com/UnfamiliarLegacy">https://github.dev/UnfamiliarLegacy</a>) to 512 byte support.
 */
public class RC4 {

    private static final byte[] BASE64_ENCODING_MAP = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes(StandardCharsets.US_ASCII);

    private static final byte[] BASE64_DECODING_MAP = new byte[]{
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63,
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1,
            -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
            41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    };

    private final int[] state;
    private int q;
    private int j;

    public RC4(BigInteger key) {
        this.state = new int[512];

        final int[] tKey = new int[512];

        final int[] tXorVals = {
                0x35, 0x47, 0x48, 0x48, 0x47, 0x45, 0x6C, 0x70, 0x77, 0x73, 0x30, 0x52, 0x67, 0x52, 0x68, 0x31,
                0x52, 0x65, 0x54, 0x4D, 0x59, 0x6A, 0x39, 0x36, 0x6E, 0x74, 0x5A, 0x78, 0x35, 0x63, 0x6A, 0x48,
                0x50, 0x47, 0x72, 0x4D, 0x4A, 0x66, 0x72, 0x38, 0x4D, 0x73, 0x6B, 0x6D, 0x7A, 0x62, 0x4C, 0x33,
                0x56, 0x4B, 0x45, 0x45, 0x6C, 0x6A, 0x52, 0x33, 0x62, 0x65, 0x76, 0x48, 0x52, 0x4E, 0x6F, 0x61
        };

        int[] keyBytes = getKeyBytes(key);
        int[] tModKey = new int[keyBytes.length];
        int l = 0;
        for (int i = 0; i < keyBytes.length; i++) {
            tModKey[i] = keyBytes[i] ^ tXorVals[l];
            l = (l + 1) % tXorVals.length;
        }
        for (int q = 0; q < 512; q++) {
            tKey[q] = tModKey[q % tModKey.length];
            state[q] = q;
        }

        j = 0;
        for (int q = 0; q < 512; q++) {
            j = (j + state[q] + tKey[q]) % 256;
            int k = state[q];
            state[q] = state[j];
            state[j] = k;
        }

        this.q = 0;
        this.j = 0;
        byte[] tPrMixString = "fN7qKvULJixzDh2EC3o6ywRStmHYAFZl1rBv4npOWgaXMb5JkIPduQeGVhT0csL9NfyCm72tzEoBdWlUVKYAiTRHgZqJXxM5rnQwhbL3FcNp84OE6ky9Pasg1jvDcztXGJMWYIVTNRhAKbL2qowfz7gXCn13ylp9uE5BOKQvMtRdjZGHmWaXciY6sT2Pe0LbnUq9RzNVA7yJhCfMgF4eLXsOiwKpBDThxn3YvQUZJ12m9CTsrRgEoWFPvkyMbXNzLlaJHg1nc4TPqOoRiA75dUzYFtXeWB68vSKGwDLNHfVCTUpqRMoYi93xnXazLt7PWjdg5mEyBsFQlKURHwtZ1OYvcGEp2naoJd4bs6TNrxMIhCmAQXLuWgfkvWDbq03vTcJlgF".getBytes(StandardCharsets.UTF_8);
        for (l = 1; l <= 64; l++) {
            this.cipher(tPrMixString);
        }
    }

    private static int[] getKeyBytes(BigInteger tSharedKey) {
        byte[] arr = tSharedKey.toByteArray();

        // Remove the "sign bit" at the start that Java adds to the BigInteger so that
        // it can match Shockwave's output
        if (arr[0] == 0) {
            byte[] bytesWithoutSignBit = new byte[arr.length - 1];
            System.arraycopy(arr, 1, bytesWithoutSignBit, 0, bytesWithoutSignBit.length);
            arr = bytesWithoutSignBit;
        }

        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < 0) {
                result[i] = 256 - Math.abs(arr[i]);
            } else {
                result[i] = arr[i];
            }
        }
        return result;
    }

    public byte[] cipher(byte[] data) {
        return cipher(data, 0, data.length);
    }

    public byte[] cipher(byte[] data, int offset, int length) {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();

        for (int i = 0; i < length; i += 3) {
            int firstByte = data[offset + i] ^ moveUp();
            int secondByte = data.length > i + 1 ? (data[i + 1] ^ moveUp()) : 0;

            result.write(BASE64_ENCODING_MAP[(firstByte & 0xFC) >> 2]);
            result.write(BASE64_ENCODING_MAP[((firstByte & 0x03) << 4) | ((secondByte & 0xF0) >> 4)]);

            if (data.length > i + 1) {
                int thirdByte = data.length > i + 2 ? (data[i + 2] ^ moveUp()) : 0;

                result.write(BASE64_ENCODING_MAP[((secondByte & 0x0F) << 2) | ((thirdByte & 0xC0) >> 6)]);

                if (data.length > i + 2) {
                    result.write(BASE64_ENCODING_MAP[thirdByte & 0x3F]);
                }
            }
        }

        return result.toByteArray();
    }

    public byte[] decipher(byte[] data) {
        return decipher(data, 0, data.length);
    }

    public byte[] decipher(byte[] data, int offset, int length) {
        final ByteBuffer buffer = ByteBuffer.wrap(data, offset, length);
        final ByteArrayOutputStream resultBuffer = new ByteArrayOutputStream();

        while (buffer.hasRemaining()) {
            int firstByte = BASE64_DECODING_MAP[buffer.get()];
            int secondByte = BASE64_DECODING_MAP[buffer.get()];

            int byte1a = firstByte << 2;
            int byte1b = (secondByte & 0x30) >> 4;

            resultBuffer.write((byte) ((byte1a | byte1b) ^ moveUp()));

            if (buffer.hasRemaining()) {
                int thirdByte = BASE64_DECODING_MAP[buffer.get()];

                int byte2a = (secondByte & 0x0F) << 4;
                int byte2b = (thirdByte & 0x3C) >> 2;

                resultBuffer.write((byte) ((byte2a | byte2b) ^ moveUp()));

                if (buffer.hasRemaining()) {
                    int fourthByte = BASE64_DECODING_MAP[buffer.get()];

                    int byte3a = (thirdByte & 0x03) << 6;
                    int byte3b = fourthByte & 0x3F;

                    resultBuffer.write((byte) ((byte3a | byte3b) ^ moveUp()));
                }
            }
        }

        return resultBuffer.toByteArray();
    }

    public byte moveUp() {
        q = (q + 1) & 0xff;
        j = ((state[q] & 0xff) + j) & 0xff;

        int tmp = state[q];
        state[q] = state[j];
        state[j] = tmp;

        if ((q & 128) == 128) {
            int x2 = 279 * (q + 67) & 0xff;
            int y2 = (j + (state[x2] & 0xff)) & 0xff;

            tmp = state[x2];
            state[x2] = state[y2];
            state[y2] = tmp;
        }

        int xorIndex = ((state[q] &0xff) + (state[j] & 0xff)) & 0xff;

        return (byte) (state[xorIndex] & 0xff);
    }
}