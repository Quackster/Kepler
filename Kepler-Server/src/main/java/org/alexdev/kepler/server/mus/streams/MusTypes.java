package org.alexdev.kepler.server.mus.streams;

public final class MusTypes
{
    /**
     * No encoded data or additional bytes
     */
    public final static short Void = 0;
    /**
     * 4 bytes in network (not Intel) order
     */
    public final static short Integer = 1;
    /**
     * 4 bytes with string length, then N bytes of string data, not null terminated,but padded to an even byte boundary
     */
    public final static short Symbol = 2;
    /**
     * 4 bytes with string length, then N bytes of string data, not null terminated,but padded to an even byte boundary
     */
    public final static short String = 3;
    /**
     * 4 bytes with data length, then N bytes of binary picture data, padded to an even byte boundary
     */
    public final static short Picture = 5;
    /**
     * 8 bytes in network (not Intel) order
     */
    public final static short Float = 6;
    /**
     * 4 bytes with the number of elements in the list, then N values
     */
    public final static short List = 7;
    /**
     * 2 numeric values, each may be either an integer or float. First is X, then Y
     */
    public final static short Point = 8;
    /**
     * 4 numeric values, each may be either an integer or float. First is top, left, bottom, then right
     */
    public final static short Rect = 9;
    /**
     * 4 bytes with the number of elements in the list, then N pairs of values. The first of each pair is a symbol, then the corresponding value
     */
    public final static short PropList = 10;
    /**
     * 4 bytes of binary data
     */
    public final static short Color = 18;
    /**
     * 16 bytes of binary data
     */
    public final static short Date = 19;
    /**
     * 4 bytes with the data length, then N bytes of binary media data, padded to an even byte boundary
     */
    public final static short Media = 20;
    /**
     * 3 floats (8 bytes each, 24 total) in network order
     */
    public final static short Vector3D = 22;
    /**
     * 16 floats (8 bytes each, 128 total) in network order
     */
    public final static short Transform3D = 23;
}