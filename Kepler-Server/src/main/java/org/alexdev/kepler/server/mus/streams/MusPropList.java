package org.alexdev.kepler.server.mus.streams;

import org.alexdev.kepler.util.BitUtil;

public class MusPropList {
    private String[] symbols;
    private short[] dataTypes;
    private byte[][] data;

    public MusPropList(int length) {
        symbols = new String[length];
        dataTypes = new short[length];
        data = new byte[length][0];
    }

    public boolean setPropAsBytes(String symbol, short type, byte[] data) {
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i] == null) {
                symbols[i] = symbol;
                dataTypes[i] = type;
                this.data[i] = data;

                return true;
            }
        }

        // No space
        return false;
    }

    public void setPropAsInt(String symbol, int i) {
        byte[] data = BitUtil.intToBytes(i);
        this.setPropAsBytes(symbol, MusTypes.Integer, data);
    }

    public void setPropAsString(String symbol, String str) {
        byte[] data = str.getBytes();
        this.setPropAsBytes(symbol, MusTypes.String, data);
    }

    public short getPropType(String symbol) {
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i] != null) {
                if (symbols[i].equals(symbol)) {
                    return dataTypes[i];
                }
            }
        }

        return MusTypes.Void;
    }

    public byte[] getPropAsBytes(String symbol) {
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i] != null) {
                if (symbols[i].equals(symbol)) {
                    return data[i];
                }
            }
        }

        return new byte[0];
    }

    public int getPropAsInt(String symbol) {
        byte[] bytes = this.getPropAsBytes(symbol);
        if (bytes.length == 0) {
            return -1;
        } else {
            return BitUtil.bytesToInt(bytes);
        }
    }

    public String getPropAsString(String symbol) {
        byte[] bytes = this.getPropAsBytes(symbol);
        return new String(bytes);
    }

    public String getSymbolAt(int slot) {
        return symbols[slot];
    }

    public short getDataTypeAt(int slot) {
        return dataTypes[slot];
    }

    public byte[] getDataAt(int slot) {
        return data[slot];
    }

    public int length() {
        return symbols.length;
    }
}