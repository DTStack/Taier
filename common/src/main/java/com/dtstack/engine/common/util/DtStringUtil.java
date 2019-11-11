package com.dtstack.engine.common.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DtStringUtil {

    public static List<String> splitIngoreBlank(String str){

        String[] strs = str.trim().split("\\s+");
        List<String> tokensList = new ArrayList<>();
        boolean inSingleQuotes = false;
        StringBuilder b = new StringBuilder();
        for (String c : strs) {
            if (c.contains("\'")) {
                inSingleQuotes = !inSingleQuotes;
                b.append(c).append(' ');
                if (!inSingleQuotes){
                    tokensList.add(b.toString().replace('\'',' '));
                    b = new StringBuilder();
                }
            } else if (inSingleQuotes){
                b.append(c).append(' ');
            } else {
                tokensList.add(c);
            }
        }

        return tokensList;
    }

    /**
     * 根据指定分隔符分割字符串---忽略在引号里面的分隔符
     * @param str
     * @param delimiter
     * @return
     */
    public static List<String> splitIgnoreQuota(String sqls, char delimiter){
        List<String> tokensList = new ArrayList<>();
        boolean inQuotes = false;
        boolean inSingleQuotes = false;
        StringBuilder b = new StringBuilder();
        char[] chars = sqls.toCharArray();
        int idx = 0;
        for (char c : chars) {
            char flag = 0;
            if (idx>0){
                flag = chars[idx-1];
            }
            if(c == delimiter){
                if (inQuotes) {
                    b.append(c);
                } else if(inSingleQuotes){
                    b.append(c);
                }else {
                    tokensList.add(b.toString());
                    b = new StringBuilder();
                }
            }else if(c == '\"' && '\\'!=flag){
                inQuotes = !inQuotes;
                b.append(c);
            }else if(c == '\'' && '\\'!=flag && !inQuotes){
                inSingleQuotes = !inSingleQuotes;
                b.append(c);
            }else{
                b.append(c);
            }
            idx++;
        }

        tokensList.add(b.toString());

        return tokensList;
    }

    /***
     * 根据指定分隔符分割字符串---忽略在引号 和 括号 里面的分隔符
     * @param str
     * @param delimter
     * @return
     */
    public static String[] splitIgnoreQuotaBrackets(String str, String delimter){
        String splitPatternStr = delimter + "(?![^()]*+\\))(?![^{}]*+})(?![^\\[\\]]*+\\])(?=(?:[^\"]|\"[^\"]*\")*$)";
        return str.split(splitPatternStr);
    }

    /**
     * 使用zip进行压缩
     * @param str 压缩前的文本
     * @return 返回压缩后的文本
     */
    public static final String zip(String str) {
        if (str == null)
            return null;
        byte[] compressed;
        ByteArrayOutputStream out = null;
        ZipOutputStream zout = null;
        String compressedStr = null;
        try {
            out = new ByteArrayOutputStream();
            zout = new ZipOutputStream(out);
            zout.putNextEntry(new ZipEntry("0"));
            zout.write(str.getBytes());
            zout.closeEntry();
            compressed = out.toByteArray();
            compressedStr = new sun.misc.BASE64Encoder().encodeBuffer(compressed);
        } catch (IOException e) {
            compressed = null;
            compressedStr = str;
        } finally {
            if (zout != null) {
                try {
                    zout.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return compressedStr;
    }
}
