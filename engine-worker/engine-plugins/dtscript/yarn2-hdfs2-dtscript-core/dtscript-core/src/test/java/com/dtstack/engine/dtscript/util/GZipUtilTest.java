package com.dtstack.engine.dtscript.util;

import org.junit.Test;

/**
 * @description:
 * @program: engine-all
 * @author: lany
 * @create: 2020/11/28 11:46
 */
public class GZipUtilTest {

    @Test
    public void testCompress() {
        String rowData = new String("data1, data2, data3");
        String result = GZipUtil.compress(rowData);
        System.out.println(result);
        GZipUtil.deCompress(result);

        byte[] rowDataByte = rowData.getBytes();
        byte[] resultByte = GZipUtil.compress(rowDataByte);
        System.out.println(resultByte);
        GZipUtil.deCompress(resultByte);
    }

}
