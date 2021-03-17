package com.thibsworkshop.voxand.toolbox;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BufferTools {

    /**
     * Creates a new {@link IntBuffer} and fills it with the data in argument
     * @param data The data to fill the buffer with
     * @return The buffer filled with data
     */
    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = org.lwjgl.BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Creates a new {@link ByteBuffer} and fills it with the data in argument
     * @param data The data to fill the buffer with
     * @return The buffer filled with data
     */
    public static ByteBuffer storeDataInByteBuffer(byte[] data) {
        ByteBuffer buffer = org.lwjgl.BufferUtils.createByteBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Creates a new {@link FloatBuffer} and fills it with the data in argument
     * @param data The data to fill the buffer with
     * @return The buffer filled with data
     */
    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = org.lwjgl.BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Returns a new {@link ByteBuffer} with the new capacity and filled with the buffer in argument
     * @param buffer The data to fill the buffer with
     * @param newCapacity The new size
     * @return A new {@link ByteBuffer}
     */
    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     * @param resource   the resource to read
     * @param bufferSize the initial buffer size
     * @return the resource data
     * @throws IOException if an IO error occurs
     */
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1);
            }
        } else {
            try (
                    InputStream source = Utility.class.getClassLoader().getResourceAsStream(resource);
                    ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = BufferUtils.createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);
                    if (bytes == -1) {
                        break;
                    }
                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
        }

        buffer.flip();
        return buffer.slice();
    }
}
