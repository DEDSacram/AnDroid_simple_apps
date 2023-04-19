package androidx.camera.core.impl.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

class ByteOrderedDataOutputStream extends FilterOutputStream {
    private ByteOrder mByteOrder;
    final OutputStream mOutputStream;

    ByteOrderedDataOutputStream(OutputStream out, ByteOrder byteOrder) {
        super(out);
        this.mOutputStream = out;
        this.mByteOrder = byteOrder;
    }

    public void setByteOrder(ByteOrder byteOrder) {
        this.mByteOrder = byteOrder;
    }

    public void write(byte[] bytes) throws IOException {
        this.mOutputStream.write(bytes);
    }

    public void write(byte[] bytes, int offset, int length) throws IOException {
        this.mOutputStream.write(bytes, offset, length);
    }

    public void writeByte(int val) throws IOException {
        this.mOutputStream.write(val);
    }

    public void writeShort(short val) throws IOException {
        if (this.mByteOrder == ByteOrder.LITTLE_ENDIAN) {
            this.mOutputStream.write((val >>> 0) & 255);
            this.mOutputStream.write((val >>> 8) & 255);
        } else if (this.mByteOrder == ByteOrder.BIG_ENDIAN) {
            this.mOutputStream.write((val >>> 8) & 255);
            this.mOutputStream.write((val >>> 0) & 255);
        }
    }

    public void writeInt(int val) throws IOException {
        if (this.mByteOrder == ByteOrder.LITTLE_ENDIAN) {
            this.mOutputStream.write((val >>> 0) & 255);
            this.mOutputStream.write((val >>> 8) & 255);
            this.mOutputStream.write((val >>> 16) & 255);
            this.mOutputStream.write((val >>> 24) & 255);
        } else if (this.mByteOrder == ByteOrder.BIG_ENDIAN) {
            this.mOutputStream.write((val >>> 24) & 255);
            this.mOutputStream.write((val >>> 16) & 255);
            this.mOutputStream.write((val >>> 8) & 255);
            this.mOutputStream.write((val >>> 0) & 255);
        }
    }

    public void writeUnsignedShort(int val) throws IOException {
        writeShort((short) val);
    }

    public void writeUnsignedInt(long val) throws IOException {
        writeInt((int) val);
    }
}
