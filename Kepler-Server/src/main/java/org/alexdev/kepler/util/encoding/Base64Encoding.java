package org.alexdev.kepler.util.encoding;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Kepler Copyright (C) 2018 Quackster
 * <a href="https://github.com/Quackster/Kepler">Kepler</a>
 */
public class Base64Encoding {

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

    public static byte[] encode(int i, int numBytes) {
        byte[] bzRes = new byte[numBytes];
        for (int j = 1; j <= numBytes; j++)
        {
            int k = ((numBytes - j) * 6);
            bzRes[j - 1] = (byte)(0x40 + ((i >> k) & 0x3f));
        }

        return bzRes;
    }

    public static byte[] encode(byte[] data, int offset, int length) {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();

        for (int i = 0; i < length; i += 3) {
            int firstByte = data[offset + i];
            int secondByte = data.length > i + 1 ? (data[i + 1]) : 0;

            result.write(BASE64_ENCODING_MAP[(firstByte & 0xFC) >> 2]);
            result.write(BASE64_ENCODING_MAP[((firstByte & 0x03) << 4) | ((secondByte & 0xF0) >> 4)]);

            if (data.length > i + 1) {
                int thirdByte = data.length > i + 2 ? (data[i + 2]) : 0;

                result.write(BASE64_ENCODING_MAP[((secondByte & 0x0F) << 2) | ((thirdByte & 0xC0) >> 6)]);

                if (data.length > i + 2) {
                    result.write(BASE64_ENCODING_MAP[thirdByte & 0x3F]);
                }
            }
        }

        return result.toByteArray();
    }

    public static int decode(byte[] data) {
        int res = 0;

        for (byte x : data) {
            final int byteVal = x - 0x40;

            res = (res << 6) | byteVal;
        }

        return res;
    }

    public static byte[] decode(byte[] data, int offset, int length) {
        final ByteBuffer buffer = ByteBuffer.wrap(data, offset, length);
        final ByteArrayOutputStream resultBuffer = new ByteArrayOutputStream();

        while (buffer.hasRemaining()) {
            int firstByte = BASE64_DECODING_MAP[buffer.get()];
            int secondByte = BASE64_DECODING_MAP[buffer.get()];

            int byte1a = firstByte << 2;
            int byte1b = (secondByte & 0x30) >> 4;

            resultBuffer.write((byte) ((byte1a | byte1b)));

            if (buffer.hasRemaining()) {
                int thirdByte = BASE64_DECODING_MAP[buffer.get()];

                int byte2a = (secondByte & 0x0F) << 4;
                int byte2b = (thirdByte & 0x3C) >> 2;

                resultBuffer.write((byte) ((byte2a | byte2b)));

                if (buffer.hasRemaining()) {
                    int fourthByte = BASE64_DECODING_MAP[buffer.get()];

                    int byte3a = (thirdByte & 0x03) << 6;
                    int byte3b = fourthByte & 0x3F;

                    resultBuffer.write((byte) ((byte3a | byte3b)));
                }
            }
        }

        return resultBuffer.toByteArray();
    }
}
