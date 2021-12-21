package com.dtstack.engine.common.lang.base;

/**
 * SLOGAN:让未来变成现在
 *
 * @author <a href="mailto:maoba@dtstack.com">猫爸</a>
 * 2018-05-07 09:27.
 */
public class Chars {
    /**
     * 换行符
     */
    public static final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * 判断字符是否是中文字符(不包含中文标点!!!)
     *
     * @param c 需进行判断的字符
     * @return 若为中文字符, 则返回true
     */
    public final static boolean isChinese(char c) {
        // 根据字节码判断
        return c >= 0x4E00 && c <= 0x9FA5;
    }

    public final static boolean isCapital(char c) {
        return c >= 'A' && c <= 'Z';
    }
}
