package com.dtstack.lang;

import com.dtstack.lang.base.Strings;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * 使用次数多的util会逐步抽取到此Facade中
 *
 * @Author 猫爸(maoba@dtstack.com)
 * @Date 2017-05-03 5:55 PM
 * @Motto 一生伏首拜阳明
 */
public final class Langs {
    /**
     * 跨平台的换行符
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * 判断对象是否为空
     *
     * @param object 需进行判断的对象
     * @return 对象为null, 则返回true, 否则返回false
     */
    public final static boolean isNull(final Object object) {
        return object == null;
    }

    /**
     * 判断对象是否为空
     *
     * @param object 需要进行判断的对象
     * @return 若对象不为null, 则返回true, 否则返回false
     */
    public final static boolean isNotNull(final Object object) {
        return !isNull(object);
    }

    public final static boolean isEmpty(Collection<?> collection) {
        return isNull(collection) || collection.size() == 0;
    }

    public final static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 是否是空白字符串
     *
     * @param stringText 需判断的字符串
     * @return 是空白字符串或null则返回true
     */
    public final static boolean isBlank(String stringText) {
        return Strings.isBlank(stringText);
    }

    public final static boolean isNotBlank(String string) {
        return Strings.isNotBlank(string);
    }

    /**
     * 将对象转换为字符串形式,对一下情况做了特殊处理
     * <ul>
     * <li>1.null输出@null</li>
     * <li>2.Array进行了聚合</li>
     * </ul>
     *
     * @param object
     * @return
     */
    public final static String toString(Object object) {
        if (isNull(object)) {
            return "@null";
        } else if (object.getClass().isArray()) {
            StringBuilder sb = new StringBuilder();
            int len = Array.getLength(object);
            for (int k = 0; k < len; k++) {
                sb.append(toString(Array.get(object, k))).append(',');
            }
            return sb.length() > 0 ? sb.substring(0, sb.length() - 1) : sb.toString();
        } else {
            return object.toString();
        }
    }
}