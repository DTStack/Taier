package com.dtstack.engine.common.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * @Auther: dazhi
 * @Date: 2020/10/9 9:44 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AESUtil {
    private final static int BYTE_PARSE_HEX_BASE = 0xFF;

    private final static char[] ENCRYPT_AES_KEYS = {0xfe, 0xee, 0xe3, 0x34, 0x2f, 0x9f, 0xe2, 0xbe, 0xc1, 0xd4, 0xdf,
            0xc3, 0xa0, 0x78, 0x89, 0x12};

    /**
     * 加密
     *
     * @param sSrc
     *            待加密的内容
     * @param raw
     *            密钥
     * @return 加密后的密文
     * @throws Exception
     */
    private static byte[] encrypt(String sSrc, String raw)
            throws Exception
    {

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");

        secureRandom.setSeed(raw.getBytes("UTF-8"));

        kgen.init(128, secureRandom);
        SecretKey secretKey = kgen.generateKey();
        byte[] encodeFormat = secretKey.getEncoded();

        SecretKeySpec key = new SecretKeySpec(encodeFormat, "AES");

        Cipher cipher = Cipher.getInstance("AES");

        byte[] byteContent = sSrc.getBytes("utf-8");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] result = cipher.doFinal(byteContent);
        return result;
    }

    /**
     * 加密
     *
     * @param sSrc
     *            待加密的内容
     * @return 加密后的密文
     * @throws Exception 异常信息
     */
    public static String encrypt(String sSrc)
            throws Exception
    {

        char[] key = ENCRYPT_AES_KEYS;

        StringBuffer buffer = new StringBuffer();
        int temp;
        for (int i = 0; i < key.length; i++)
        {
            temp = key[i];
            buffer.append(temp);
        }
        return parseByte2HexStr(encrypt(sSrc, buffer.toString()));
    }

    /**
     * 解密
     *
     * @param sSrc
     *            待解密的内容
     * @param raw
     *            密钥
     * @return 解密后的明文
     * @throws Exception 异常
     */

    private static byte[] decrypt(byte[] sSrc, String raw)
            throws Exception
    {

        byte[] result = null;
        try
        {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");

            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");

            secureRandom.setSeed(raw.getBytes("UTF-8"));

            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] encodeFormat = secretKey.getEncoded();

            SecretKeySpec key = new SecretKeySpec(encodeFormat, "AES");

            Cipher cipher = Cipher.getInstance("AES");

            cipher.init(Cipher.DECRYPT_MODE, key);
            result = cipher.doFinal(sSrc);
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 解密
     *
     * @param sSrc
     *            待解密的内容
     * @return 解密后的明文
     * @throws Exception 异常
     */
    public static String decrypt(String sSrc)
            throws Exception
    {
        char[] key = ENCRYPT_AES_KEYS;

        StringBuffer buffer = new StringBuffer();
        int temp;
        for (int i = 0; i < key.length; i++)
        {
            temp = key[i];
            buffer.append(temp);
        }

        return new String(decrypt(parseHexStr2Byte(sSrc), buffer.toString()));
    }

    /**
     * 将二进制转换为十六进制
     *
     * @param buf 源二进制
     * @return 十六进制
     */
    public static String parseByte2HexStr(byte buf[])
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++)
        {
            String hex = Integer.toHexString(buf[i] & BYTE_PARSE_HEX_BASE);
            if (hex.length() == 1)
            {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将十六进制转换为二进制
     * @param hexStr 十六进制
     * @return 二进制
     */
    public static byte[] parseHexStr2Byte(String hexStr)
    {
        if (hexStr.length() < 1)
        {
            return null;
        }

        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++)
        {
            int high =
                    Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1),
                            16);
            int low =
                    Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2
                                    + 2),
                            16);
            result[i] = (byte)(high * 16 + low);
        }

        return result;
    }
}
