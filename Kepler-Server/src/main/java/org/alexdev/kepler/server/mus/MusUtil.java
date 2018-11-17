package org.alexdev.kepler.server.mus;

import io.netty.buffer.ByteBuf;
import org.alexdev.kepler.server.mus.streams.MusPropList;
import org.alexdev.kepler.server.mus.streams.MusTypes;

public class MusUtil {
    public static String readEvenPaddedString(ByteBuf in) {
        // String length
        int length = in.readInt();
        if (length <= 0) {
            return "";
        } else {
            // Actual string bytes
            byte[] bytes = new byte[length];
            in.readBytes(bytes);

            // Advance one byte if uneven
            if ((length % 2) != 0) {
                in.readByte();
            }

            // Return the string
            return new String(bytes);
        }
    }

    public static void writeEvenPaddedString(ByteBuf out, String str) {
        // String length
        out.writeInt(str.length());

        // Actual string bytes
        out.writeBytes(str.getBytes());

        // Add a null byte if uneven
        if ((str.length() % 2) != 0) {
            out.writeByte(0);
        }
    }

    public static MusPropList readPropList(ByteBuf in) {
        // Length of list
        int length = in.readInt();

        // Allocate props
        MusPropList props = new MusPropList(length);

        // Parse them
        for (int i = 0; i < length; i++) {
            // Symbol type (always string)
            in.readShort();

            // Symbol (key)
            String symbol = MusUtil.readEvenPaddedString(in);

            // Data type
            short dataType = in.readShort();

            // Data (value)
            int dataLength;
            if (dataType == MusTypes.Integer) {
                dataLength = 4;
            } else {
                dataLength = in.readInt();
            }
            byte[] data = new byte[dataLength];
            in.readBytes(data);
            if ((dataLength % 2) != 0) {
                in.readByte();
            }

            // Set prop
            props.setPropAsBytes(symbol, dataType, data);
        }

        return props;
    }

    public static void writePropList(ByteBuf out, MusPropList props) {
        // Length
        out.writeInt(props.length());

        // Serialize elements
        for (int i = 0; i < props.length(); i++) {
            // Symbol
            out.writeShort(MusTypes.Symbol);
            MusUtil.writeEvenPaddedString(out, props.getSymbolAt(i));

            // Value
            out.writeShort(props.getDataTypeAt(i));
            byte[] data = props.getDataAt(i);
            if (props.getDataTypeAt(i) != MusTypes.Integer) {
                out.writeInt(data.length);
            }
            out.writeBytes(data);
            if ((data.length % 2) != 0) {
                out.writeByte(0);
            }
        }
    }
}
