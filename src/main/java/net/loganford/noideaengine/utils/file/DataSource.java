package net.loganford.noideaengine.utils.file;

import net.loganford.noideaengine.GameEngineException;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class DataSource {
    public static int DEFAULT_BUFFER_SIZE = 8192;

    protected abstract InputStream getInputStream();

    public String load() {
        try(InputStream stream = getInputStream()) {
            return IOUtils.toString(stream, StandardCharsets.UTF_8);
        }
        catch(IOException e) {
            throw new GameEngineException(e);
        }
    }

    public ByteBuffer loadBytes() {
        ByteBuffer buffer = BufferUtils.createByteBuffer(DEFAULT_BUFFER_SIZE);
        try (InputStream source = getInputStream()) {
            byte[] buf = new byte[8192];
            while (true) {
                int bytes = source.read(buf, 0, buf.length);
                if (bytes == -1)
                    break;
                if (buffer.remaining() < bytes)
                    buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                buffer.put(buf, 0, bytes);
            }
            buffer.flip();
        }
        catch(IOException e) {
            throw new GameEngineException(e);
        }

        return buffer;
    }

    private ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    public boolean isSaveSupported() {
        return false;
    }

    public void save(String data) {
        throw new SaveNotSupportedException();
    }

    public abstract boolean exists();
}
