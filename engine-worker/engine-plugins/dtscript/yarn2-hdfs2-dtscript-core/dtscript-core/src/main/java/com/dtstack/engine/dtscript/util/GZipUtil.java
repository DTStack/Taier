package com.dtstack.engine.dtscript.util;

import com.dtstack.engine.common.exception.ExceptionUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:32 2019-07-24
 * @Description：Gip 压缩工具
 */
public class GZipUtil {

    private static final Logger LOG = LoggerFactory.getLogger(GZipUtil.class);

    public static byte[] compress(byte[] data) {
        ByteArrayOutputStream bos = null;
        GZIPOutputStream gzip = null;
        try {
            bos = new ByteArrayOutputStream(data.length);
            gzip = new GZIPOutputStream(bos);
            gzip.write(data);
        } catch (IOException e) {
            LOG.error("GZipUtil.compress error:", e);
        } finally {
            if (null != bos) {
                try {
                    bos.close();
                } catch (IOException e) {
                    LOG.error("GZipUtil.compress error:", e);
                }
            }

            if (null != gzip) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    LOG.error("GZipUtil.compress error:", e);
                }
            }
        }
        byte[] compressed = bos.toByteArray();
        return compressed;
    }

    public static byte[] deCompress(byte[] compressed) {
        ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = null;
        byte[] backData = null;
        try {
            gis = new GZIPInputStream(bis);
            backData = IOUtils.toByteArray(gis);
        } catch (IOException e) {
            LOG.error("GZipUtil.deCompress error:", e);
        } finally {
                try {
                    bis.close();
                } catch (IOException e) {
                    LOG.error("GZipUtil.deCompress error:", e);
                }
            if (null != gis) {
                try {
                    gis.close();
                } catch (IOException e) {
                    LOG.error("GZipUtil.deCompress error:", e);
                }
            }
        }
        return backData;
    }

    public static String compress(String rowData) {
        return new String(Base64Util.baseEncode(compress(rowData.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
    }

    public static String deCompress(String rowData) {
        return new String(deCompress(Base64Util.baseDecode(rowData.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
    }
}
