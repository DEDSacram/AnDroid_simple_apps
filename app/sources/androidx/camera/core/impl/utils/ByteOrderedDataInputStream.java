package androidx.camera.core.impl.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

final class ByteOrderedDataInputStream extends InputStream implements DataInput {
    private static final ByteOrder BIG_ENDIAN = ByteOrder.BIG_ENDIAN;
    private static final ByteOrder LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
    private ByteOrder mByteOrder;
    private final DataInputStream mDataInputStream;
    final int mLength;
    int mPosition;

    ByteOrderedDataInputStream(InputStream in) throws IOException {
        this(in, ByteOrder.BIG_ENDIAN);
    }

    ByteOrderedDataInputStream(InputStream in, ByteOrder byteOrder) throws IOException {
        this.mByteOrder = ByteOrder.BIG_ENDIAN;
        DataInputStream dataInputStream = new DataInputStream(in);
        this.mDataInputStream = dataInputStream;
        int available = dataInputStream.available();
        this.mLength = available;
        this.mPosition = 0;
        dataInputStream.mark(available);
        this.mByteOrder = byteOrder;
    }

    ByteOrderedDataInputStream(byte[] bytes) throws IOException {
        this((InputStream) new ByteArrayInputStream(bytes));
    }

    public void setByteOrder(ByteOrder byteOrder) {
        this.mByteOrder = byteOrder;
    }

    public void seek(long byteCount) throws IOException {
        int i = this.mPosition;
        if (((long) i) > byteCount) {
            this.mPosition = 0;
            this.mDataInputStream.reset();
            this.mDataInputStream.mark(this.mLength);
        } else {
            byteCount -= (long) i;
        }
        if (skipBytes((int) byteCount) != ((int) byteCount)) {
            throw new IOException("Couldn't seek up to the byteCount");
        }
    }

    public int peek() {
        return this.mPosition;
    }

    public int available() throws IOException {
        return this.mDataInputStream.available();
    }

    public int read() throws IOException {
        this.mPosition++;
        return this.mDataInputStream.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int bytesRead = this.mDataInputStream.read(b, off, len);
        this.mPosition += bytesRead;
        return bytesRead;
    }

    public int readUnsignedByte() throws IOException {
        this.mPosition++;
        return this.mDataInputStream.readUnsignedByte();
    }

    public String readLine() {
        throw new UnsupportedOperationException("readLine() not implemented.");
    }

    public boolean readBoolean() throws IOException {
        this.mPosition++;
        return this.mDataInputStream.readBoolean();
    }

    public char readChar() throws IOException {
        this.mPosition += 2;
        return this.mDataInputStream.readChar();
    }

    public String readUTF() throws IOException {
        this.mPosition += 2;
        return this.mDataInputStream.readUTF();
    }

    public void readFully(byte[] buffer, int offset, int length) throws IOException {
        int i = this.mPosition + length;
        this.mPosition = i;
        if (i > this.mLength) {
            throw new EOFException();
        } else if (this.mDataInputStream.read(buffer, offset, length) != length) {
            throw new IOException("Couldn't read up to the length of buffer");
        }
    }

    public void readFully(byte[] buffer) throws IOException {
        int length = this.mPosition + buffer.length;
        this.mPosition = length;
        if (length > this.mLength) {
            throw new EOFException();
        } else if (this.mDataInputStream.read(buffer, 0, buffer.length) != buffer.length) {
            throw new IOException("Couldn't read up to the length of buffer");
        }
    }

    public byte readByte() throws IOException {
        int i = this.mPosition + 1;
        this.mPosition = i;
        if (i <= this.mLength) {
            int ch = this.mDataInputStream.read();
            if (ch >= 0) {
                return (byte) ch;
            }
            throw new EOFException();
        }
        throw new EOFException();
    }

    public short readShort() throws IOException {
        int i = this.mPosition + 2;
        this.mPosition = i;
        if (i <= this.mLength) {
            int ch1 = this.mDataInputStream.read();
            int ch2 = this.mDataInputStream.read();
            if ((ch1 | ch2) >= 0) {
                ByteOrder byteOrder = this.mByteOrder;
                if (byteOrder == LITTLE_ENDIAN) {
                    return (short) ((ch2 << 8) + ch1);
                }
                if (byteOrder == BIG_ENDIAN) {
                    return (short) ((ch1 << 8) + ch2);
                }
                throw new IOException("Invalid byte order: " + this.mByteOrder);
            }
            throw new EOFException();
        }
        throw new EOFException();
    }

    public int readInt() throws IOException {
        int i = this.mPosition + 4;
        this.mPosition = i;
        if (i <= this.mLength) {
            int ch1 = this.mDataInputStream.read();
            int ch2 = this.mDataInputStream.read();
            int ch3 = this.mDataInputStream.read();
            int ch4 = this.mDataInputStream.read();
            if ((ch1 | ch2 | ch3 | ch4) >= 0) {
                ByteOrder byteOrder = this.mByteOrder;
                if (byteOrder == LITTLE_ENDIAN) {
                    return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + ch1;
                }
                if (byteOrder == BIG_ENDIAN) {
                    return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
                }
                throw new IOException("Invalid byte order: " + this.mByteOrder);
            }
            throw new EOFException();
        }
        throw new EOFException();
    }

    public int skipBytes(int byteCount) throws IOException {
        int totalSkip = Math.min(byteCount, this.mLength - this.mPosition);
        int skipped = 0;
        while (skipped < totalSkip) {
            skipped += this.mDataInputStream.skipBytes(totalSkip - skipped);
        }
        this.mPosition += skipped;
        return skipped;
    }

    public int readUnsignedShort() throws IOException {
        int i = this.mPosition + 2;
        this.mPosition = i;
        if (i <= this.mLength) {
            int ch1 = this.mDataInputStream.read();
            int ch2 = this.mDataInputStream.read();
            if ((ch1 | ch2) >= 0) {
                ByteOrder byteOrder = this.mByteOrder;
                if (byteOrder == LITTLE_ENDIAN) {
                    return (ch2 << 8) + ch1;
                }
                if (byteOrder == BIG_ENDIAN) {
                    return (ch1 << 8) + ch2;
                }
                throw new IOException("Invalid byte order: " + this.mByteOrder);
            }
            throw new EOFException();
        }
        throw new EOFException();
    }

    public long readUnsignedInt() throws IOException {
        return ((long) readInt()) & 4294967295L;
    }

    public long readLong() throws IOException {
        int i = this.mPosition + 8;
        this.mPosition = i;
        if (i <= this.mLength) {
            int ch1 = this.mDataInputStream.read();
            int ch2 = this.mDataInputStream.read();
            int ch3 = this.mDataInputStream.read();
            int ch4 = this.mDataInputStream.read();
            int ch5 = this.mDataInputStream.read();
            int ch6 = this.mDataInputStream.read();
            int ch7 = this.mDataInputStream.read();
            int ch8 = this.mDataInputStream.read();
            if ((ch1 | ch2 | ch3 | ch4 | ch5 | ch6 | ch7 | ch8) >= 0) {
                ByteOrder byteOrder = this.mByteOrder;
                if (byteOrder == LITTLE_ENDIAN) {
                    return (((long) ch8) << 56) + (((long) ch7) << 48) + (((long) ch6) << 40) + (((long) ch5) << 32) + (((long) ch4) << 24) + (((long) ch3) << 16) + (((long) ch2) << 8) + ((long) ch1);
                }
                int ch22 = ch2;
                if (byteOrder == BIG_ENDIAN) {
                    return (((long) ch1) << 56) + (((long) ch22) << 48) + (((long) ch3) << 40) + (((long) ch4) << 32) + (((long) ch5) << 24) + (((long) ch6) << 16) + (((long) ch7) << 8) + ((long) ch8);
                }
                throw new IOException("Invalid byte order: " + this.mByteOrder);
            }
            throw new EOFException();
        }
        throw new EOFException();
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public void mark(int readlimit) {
        synchronized (this.mDataInputStream) {
            this.mDataInputStream.mark(readlimit);
        }
    }

    public int getLength() {
        return this.mLength;
    }
}
