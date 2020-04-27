package com.dtstack.engine.master.download;

import org.apache.commons.lang3.StringUtils;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;

/**
 * @author sanyue
 * @date 2019/8/23
 */
public class TemplateFileDownload implements IDownload {

    private RandomAccessFile aFile;

    private int bytesRead;

    private FileChannel inChannel;

    private ByteBuffer buf;

    private CharBuffer cb;

    private CharsetDecoder decoder;

    private String filePath;

    private int bufferSize = 512;

    private int charBufferSize = 512;

    private String CODE = "UTF-8";

    private String MODE = "r";

    public TemplateFileDownload(String filePath, Integer bufferSize) {
        this.filePath = filePath;
        this.bufferSize = bufferSize;
    }

    public TemplateFileDownload(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void configure() throws Exception {
        aFile = new RandomAccessFile(filePath, MODE);
        inChannel = aFile.getChannel();
        buf = ByteBuffer.allocate(bufferSize);
        bytesRead = inChannel.read(buf);
        //Java.nio.charset.Charset处理了字符转换问题。它通过构造CharsetEncoder和CharsetDecoder将字符序列转换成字节和逆转换。
        Charset charset = Charset.forName(CODE);
        decoder = charset.newDecoder();
        cb = CharBuffer.allocate(charBufferSize);
    }

    @Override
    public List<String> getMetaInfo() throws Exception {
        return null;
    }

    @Override
    public Object readNext() throws Exception {
        buf.flip();
        decoder.decode(buf, cb, false);
        cb.flip();
        StringBuilder sb = new StringBuilder();
        while(cb.hasRemaining()){
            sb.append(cb.get());

        }
        buf.clear();
        cb.clear();
        bytesRead = inChannel.read(buf);
        return sb.toString();
    }

    @Override
    public boolean reachedEnd() throws Exception {
        return bytesRead == -1;
    }

    @Override
    public void close() throws Exception {
        aFile.close();
    }

    @Override
    public String getFileName() {
        if (StringUtils.isNotBlank(filePath) && filePath.contains("/")) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        return filePath;
    }
}
