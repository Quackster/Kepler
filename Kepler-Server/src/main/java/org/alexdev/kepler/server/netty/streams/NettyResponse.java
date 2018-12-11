package org.alexdev.kepler.server.netty.streams;

import io.netty.buffer.ByteBuf;
import org.alexdev.kepler.util.encoding.Base64Encoding;
import org.alexdev.kepler.util.encoding.VL64Encoding;

import java.nio.charset.Charset;

public class NettyResponse  {
    private short id;
    private ByteBuf buffer;
    private boolean finalised;

    public NettyResponse(short header, ByteBuf buffer) {
        this.id = header;
        this.buffer = buffer;
        this.buffer.writeBytes(Base64Encoding.encode(header, 2));
    }

    /**
     * Write an object as a raw string.
     *
     * @param obj the object to write
     */
    public void write(Object obj) {
        this.buffer.writeBytes(obj.toString().getBytes());
    }

    /**
     * Write an object with a character 2 suffix.
     *
     * @param obj the string to write
     */
    public void writeString(Object obj) {
        this.buffer.writeBytes(obj.toString().getBytes());
        this.buffer.writeByte(2);
    }

    /**
     * Write a VL74 encoded integer.
     *
     * @param number the number to encode.
     */
    public void writeInt(Integer number) {
        this.buffer.writeBytes(VL64Encoding.encode(number));
    }

    /**
     * Write a key value packet.
     *
     * @param key the key
     * @param value the value
     */
    public void writeKeyValue(Object key, Object value) {
        this.buffer.writeBytes(key.toString().getBytes());
        this.buffer.writeBytes(":".getBytes());
        this.buffer.writeBytes(value.toString().getBytes());
        this.buffer.writeByte(13);
    }

    /**
     * Write an object with a custom delimeter.
     *
     * @param key the key to write
     * @param value the delimeter to write
     */
    public void writeDelimeter(Object key, Object value) {
        this.buffer.writeBytes(key.toString().getBytes());
        this.buffer.writeBytes(value.toString().getBytes());
    }

    /**
     * Write boolean, H or I in VL64 representation.
     *
     * @param obj the boolean to write
     */
    public void writeBool(Boolean obj) {
        this.writeInt(obj ? 1 : 0);
    }

    /**
     * Get a packet string but in readable format.
     *
     * @return the readable packet
     */
    public String getBodyString() {
        String str = this.buffer.toString(Charset.defaultCharset());
        
        for (int i = 0; i < 14; i++) { 
            str = str.replace(Character.toString((char)i), "{" + i + "}");
        }

        return str;
    }

    /**
     * If this packet has been finalised before sending.
     * Means it will add the character 1 suffix.
     *
     * @return true, if it was
     */
    public boolean isFinalised() {
        return finalised;

    }

    /**
     * Setting to finalised means it will not add
     * the character 1 suffix since it has already been added.
     *
     * @param finalised whether it should be finalised or not
     */
    public void setFinalised(boolean finalised) {
        this.finalised = finalised;
    }

    /* (non-Javadoc)
     * @see org.alexdev.icarus.server.api.messages.Response#getHeader()
     */
    public int getHeader() {
        return this.id;
    }
}
