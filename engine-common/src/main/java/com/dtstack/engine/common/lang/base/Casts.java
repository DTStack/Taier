package com.dtstack.engine.common.lang.base;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * SLOGAN:让未来变成现在
 *
 * @author maoba@dtstack.com
 */
public class Casts {
    public static final Map<Class<?>, Class<?>> primitiveToWrapperTypeMap = Maps.newHashMap();
    public static final Map<Class<?>, Class<?>> wrapperToPrimitiveTypeMap = Maps.newHashMap();

    static {
        primitiveToWrapperTypeMap.put(boolean.class, Boolean.class);
        primitiveToWrapperTypeMap.put(byte.class, Byte.class);
        primitiveToWrapperTypeMap.put(char.class, Character.class);
        primitiveToWrapperTypeMap.put(short.class, Short.class);
        primitiveToWrapperTypeMap.put(int.class, Integer.class);
        primitiveToWrapperTypeMap.put(long.class, Long.class);
        primitiveToWrapperTypeMap.put(float.class, Float.class);
        primitiveToWrapperTypeMap.put(double.class, Double.class);

        for (Map.Entry<Class<?>, Class<?>> entry : primitiveToWrapperTypeMap.entrySet()) {
            wrapperToPrimitiveTypeMap.put(entry.getValue(), entry.getClass());
        }
    }

    private Casts() {
    }

    public static Object cast(Class<?> clazz, Object object) {
        if (object == null) {
            return null;
        }
        if (clazz.equals(Integer.class) || int.class.equals(clazz)) {
            return castIntDefaultNull(object);
        } else if (clazz.equals(Long.class) || long.class.equals(clazz)) {
            return castLong(object);
        } else if (clazz.equals(Double.class) || double.class.equals(clazz)) {
            return castDouble(object);
        } else if (clazz.equals(Float.class) || float.class.equals(clazz)) {
            object = Float.parseFloat(object.toString());
        } else if (clazz.equals(Byte.class) || byte.class.equals(clazz)) {
            object = Byte.parseByte(object.toString());
        } else if (clazz.equals(Short.class) || short.class.equals(clazz)) {
            object = Short.parseShort(object.toString());
        } else if (clazz.equals(Boolean.class) || boolean.class.equals(clazz)) {
            object = Boolean.parseBoolean(object.toString());
        } else if (clazz.equals(String.class)) {
            object = object.toString();
        }
        return object;
    }


    /**
     * 将int转换成char类型
     *
     * @param iInt
     * @return
     */
    public static final char castChar(int iInt) {
        return (char) iInt;
    }

    public static final String castString(Object object) {
        return castString(object, Strings.EMPTY_STRING);
    }

    public static final String castString(Object object, String defaultValue) {
        return Objects.isNull(object) ? defaultValue : String.valueOf(object);
    }

    public static final Integer castIntDefaultZero(Object object) {
        return castInt(object, 0);

    }

    public static final Integer castIntDefaultNull(Object object) {
        return castInt(object, null);
    }

    public static final Integer castInt(Object object, Integer defaultValue) {
        String stringValue = castString(object);
        if (Numbers.isInteger(stringValue)) {
            return Integer.parseInt(stringValue.trim());
        } else {
            return defaultValue;
        }
    }

    /**
     * 将对象转换为Long类型,若转换失败,则返回0L
     *
     * @param object 需进行转换的对象
     * @return 转换后的Long对象
     */
    public static final Long castLong(Object object) {
        return castLong(object, 0L);
    }

    /**
     * 将对象转换成Long类型,若转换失败或为null则返回defaultValue
     *
     * @param object       需进行转换的对象
     * @param defaultValue 转换失败时的默认值
     * @return 转换后的Long对象
     */
    public static final Long castLong(Object object, Long defaultValue) {
        Long value = defaultValue;
        if (object != null) {
            String objectString = castString(object);
            if (!Strings.isNullOrEmpty(objectString)) {
                try {
                    value = Long.parseLong(objectString);
                } catch (NumberFormatException e) {
                    value = defaultValue;
                }
            }
        }
        return value;
    }

    public static final Double castDouble(Object object) {
        return castDouble(object, null);
    }

    public static final Double castDouble(Object object, Double defaultValue) {
        Double value = defaultValue;
        if (object != null) {
            String objectString = castString(object);
            try {
                value = Double.parseDouble(objectString);
            } catch (NumberFormatException e) {
                value = defaultValue;
            }
        }
        return value;
    }

    public static final Float castFloat(Object object) {
        return castFloat(object, null);
    }

    public static final Float castFloat(Object object, Float defaultValue) {
        Float value = defaultValue;
        if (object != null) {
            String objectString = castString(object);
            try {
                value = Float.parseFloat(objectString);
            } catch (NumberFormatException e) {
                value = defaultValue;
            }
        }
        return value;
    }

    public static boolean isPrimitiveType(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return Boolean.FALSE;
        } else {
            if (clazz.isPrimitive()) {
                return Boolean.TRUE;
            } else {
                return wrapperToPrimitiveTypeMap.containsKey(clazz);
            }
        }
    }

    public static boolean isPrimitiveType(Object object) {
        if (Objects.isNull(object)) {
            return Boolean.FALSE;
        } else {
            return isPrimitiveType(object.getClass());
        }
    }

    public static final boolean isMap(Object object) {
        return Objects.isNull(object) ? false : Map.class.isAssignableFrom(object.getClass());
    }

    public static final boolean isArray(Object object) {
        return Objects.isNull(object) ? false : object.getClass().isArray();
    }

    public static final boolean isCollection(Object object) {
        return Objects.isNull(object) ? false : Collection.class.isAssignableFrom(object.getClass());
    }

    /**
     * 判断是否是复杂对象
     *
     * @param object 需进行判断的对象
     * @return 是复杂对象, 则返回true, 否则返回false
     */
    public static boolean isComplex(Object object) {
        if (object == null) {
            return false;
        }
        if (isPrimitiveType(object)
                || object instanceof String
                || object instanceof Number) {
            return false;
        }

        return true;
    }
}