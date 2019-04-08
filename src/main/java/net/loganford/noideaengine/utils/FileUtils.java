package net.loganford.noideaengine.utils;

import lombok.extern.log4j.Log4j2;
import net.loganford.noideaengine.GameEngineException;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2
public class FileUtils {

    public static String readFileAsString(File file) {
        try {
            return org.apache.commons.io.FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GameEngineException("Unable to read file: " + file.getAbsolutePath());
        }
    }

    //Adapted from LWJGL demo utils, but with some bug fixes
    public static ByteBuffer loadByteBuffer(String fileName, int bufferSize) throws IOException {
        File file = new File(fileName);
        ByteBuffer buffer;
        if (file.isFile()) {
            FileInputStream fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            fc.close();
            fis.close();
        } else {
            buffer = BufferUtils.createByteBuffer(bufferSize);
            try (InputStream source = new FileInputStream(file)) {
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
        }
        return buffer;
    }


    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    public static String readResourceAsString(String resource) {
        try {
            InputStream stream = FileUtils.class.getResourceAsStream(resource);
            return IOUtils.toString(stream, StandardCharsets.UTF_8);
        }
        catch(IOException e) {
            throw new GameEngineException(e);
        }
    }
}