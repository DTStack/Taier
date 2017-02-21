package com.dtstack.rdos.engine.execution.base.util;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private static final int BUFFER_SIZE = 10240;

    /**
     * FIXME 需要判断dstFile的父目录是否存在
     * @param urlStr
     * @param dstFileName
     * @return
     */
    public static boolean downLoadFile(String urlStr, String dstFileName){

        try {
            File outFile = new File(dstFileName);
            //FIXME 如果当前文件存在则删除,覆盖最新的文件
            if(outFile.exists()){
                outFile.delete();
            }

            Files.createParentDirs(outFile);//如果父目录不存在则创建

            FileOutputStream fout = new FileOutputStream(outFile);
            URL url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            BufferedInputStream bfInputStream = new BufferedInputStream(httpURLConnection.getInputStream());

            byte[] buf = new byte[BUFFER_SIZE];
            int readSize = -1;
            while((readSize = bfInputStream.read(buf)) != -1){
                fout.write(buf, 0, readSize);
            }

            //释放资源
            fout.close();
            bfInputStream.close();
            httpURLConnection.disconnect();

            logger.info("download from remote url:{} success,dest file name is {}.", urlStr, dstFileName);
        } catch (IOException e) {
            logger.error("download from remote url:" + urlStr +"failure.", e);
            return false;
        }

        return true;
    }
}
