package com.dtstack.engine.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dt.insight.plat.lang.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 公用的工具类
 * @description:
 * @author: liuxx
 * @date: 2021/3/12
 */
@Slf4j
public class CommonUtils {

    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 抽取数组实体某个属性成为字符串数组
     *
     * @param list      源数组
     * @param fieldName 属性名
     * @return
     */
    public static <T> List<String> contractStringField(Collection<T> list, String fieldName) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(e -> {
            try {
                Object getValue = e.getClass().getMethod(String.format("get%s", uppercaseFirstChar(fieldName))).invoke(e);
                return getValue.toString();
            } catch (NoSuchMethodException e1) {
                log.info("该实体 [{}]没有field [{}] 属性!", e.getClass().getName(), fieldName);
                e1.printStackTrace();
            } catch (IllegalAccessException | InvocationTargetException e1) {
                e1.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    /**
     * 抽取数组实体某个属性成为数组
     *
     * @param list      源数组
     * @param fieldName 属性名称
     * @param eClass    要转换的类
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> List<E> contractField(Collection<T> list, String fieldName, Class<E> eClass) {
        if (Objects.isNull(list) || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(e -> {
            try {
                Object getValue = e.getClass().getMethod(String.format("get%s", uppercaseFirstChar(fieldName))).invoke(e);
                return eClass.cast(getValue);
            } catch (NoSuchMethodException e1) {
                log.info("该实体 [{}]没有field [{}] 属性!", e.getClass().getName(), fieldName);
                e1.printStackTrace();
            } catch (IllegalAccessException | InvocationTargetException e1) {
                e1.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
    }

    /**
     * 将字符串首字符大写显示
     *
     * @param str
     * @return
     */
    public static String uppercaseFirstChar(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * list去重
     *
     * @param list
     * @param <T>hashSet
     * @return
     */
    public static <T> List<T> removeDuplicateList(List<T> list) {
        HashSet<T> hashSet = Sets.newHashSet(list.iterator());
        return Lists.newArrayList(hashSet.iterator());
    }

    /**
     * 抽取数组实体某个属性成为数组 去重
     *
     * @param list      源数组
     * @param fieldName 属性名称
     * @param eClass    要转换的类
     * @param <T>
     * @param <E>
     * @return
     */
    public static <T, E> List<E> removeDupContractField(Collection<T> list, String fieldName, Class<E> eClass) {
        return removeDuplicateList(contractField(list, fieldName, eClass));
    }

    /**
     * 将对象转换成Map 剔除null值
     *
     * @param source
     * @param <T>
     * @return
     */
    public static <T> Map convertObjectToMap(T source) {
        Map<String, Object> returnMap = Maps.newHashMap();
        if (Objects.isNull(source)) {
            return returnMap;
        }

        Field[] fields = source.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                Object getValue = source.getClass().getMethod(String.format("get%s", uppercaseFirstChar(field.getName()))).invoke(source);
                if (Objects.nonNull(getValue)) {
                    returnMap.put(field.getName(), getValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMap;
    }

    /**
     * 判断source是否与后面的对象数组有相等值
     *
     * @param source
     * @param compares
     * @return
     */
    public static Boolean checkAnyObjectEquals(Object source, Object... compares) {
        if (Objects.isNull(source)) {
            return false;
        }
        for (Object compare : compares) {
            if (Objects.equals(source, compare)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 保留几位小数 返回数值字符串
     *
     * @param number
     * @param bit
     * @return
     */
    public static String decimalNumberByBit(Object number, Integer bit) {
        if (Objects.isNull(number)) {
            return "";
        }
        Objects.requireNonNull(bit);
        StringBuilder sb = new StringBuilder();
        sb.append("####");
        for (Integer i = 0; i < bit; i++) {
            if (i == 0) {
                sb.append(".");
            }
            sb.append("0");
        }
        DecimalFormat df = new DecimalFormat(sb.toString());
        return df.format(number);
    }

    /**
     * 将数值转换成百分比
     *
     * @param number
     * @return
     */
    public static String parsePercent(Object number, Integer scale) {
        if (Objects.isNull(number)) {
            return "";
        }
        Double d = Double.parseDouble(number.toString());
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(scale);
        return percentFormat.format(d);
    }

    /**
     * 获取两个整数相除的百分比
     *
     * @param divide
     * @param reDivide
     * @return
     */
    public static String dividePercent(Integer divide, Integer reDivide) {
        Objects.requireNonNull(divide);
        Objects.requireNonNull(reDivide);
        BigDecimal bigDecimal1 = new BigDecimal(divide);
        BigDecimal bigDecimal2 = new BigDecimal(reDivide);
        BigDecimal divideResult = bigDecimal1.divide(bigDecimal2, 2, BigDecimal.ROUND_HALF_UP);
        return parsePercent(divideResult.doubleValue(), 0);
    }

    /**
     * 数字类型数组排序
     *
     * @param numberList
     * @return
     */
    public static void sortNumberList(List<Integer> numberList, Boolean isAsc) {
        if (isAsc) {
            numberList.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1 > o2 ? 1 : (o1.equals(o2)) ? 0 : -1;
                }
            });
        } else {
            numberList.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1 > o2 ? -1 : (o1.equals(o2)) ? 0 : 1;
                }
            });
        }
    }

    /**
     * 日期转换成特定格式
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String formatDate(Object date, String dateFormat) {
        if (date == null || Strings.isBlank(dateFormat)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(date);
    }

    /**
     * collection join 字符串
     *
     * @param collection
     * @param join
     * @param <T>
     * @return
     */
    public static <T> String collectionJoin(Collection<T> collection, String join) {
        Objects.requireNonNull(join);
        if (Collections.isEmpty(collection)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (T t : collection) {
            sb.append("\'").append(t.toString()).append("\'").append(join);
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }


    /**
     * 反射取出某一个变量
     *
     * @param model
     * @param <T>
     * @return
     */
    public static <T> String reflectFieldInModel(T model, String fieldName) {
        try {
            Object valueObj = model.getClass().getMethod(
                    Strings.format("get{}", firstCharUpperCase(fieldName))).invoke(model);
            if (Objects.isNull(valueObj)) {
                return "";
            }
            return valueObj.toString();
        } catch (Exception e) {
            log.error("该对象无对应变量名", e);
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 根据特定条件拼接字符串
     *
     * @param condition
     * @param sb
     * @param append
     * @return
     */
    public static void stringAppend(Boolean condition, StringBuilder sb, String append) {
        if (condition) {
            sb.append(append);
        }
    }


    public static String firstCharUpperCase(String str) {
        if (str == null || Objects.equals("", str.trim())) {
            return "";
        }
        return str.substring(0, 1).toUpperCase() +
                str.substring(1);
    }

    /**
     * 下划线转驼峰
     *
     * @param underLineStr
     * @return
     */
    public static String underLineToHump(String underLineStr) {
        if (Strings.isBlank(underLineStr)) {
            return null;
        }
        underLineStr = underLineStr.toLowerCase();
        Matcher matcher = linePattern.matcher(underLineStr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 判断两个字符串 前者是否包含后者 不做字母大小写判断
     *
     * @param containStr
     * @param compareStr
     * @return
     */
    public static Boolean compareStrNotLower(String containStr, String compareStr) {
        if (Strings.isNullOrEmpty(containStr)) {
            return false;
        }
        if (Strings.isNullOrEmpty(compareStr)) {
            return true;
        }
        return containStr.toLowerCase().contains(compareStr.trim().toLowerCase());
    }

    /**
     * 从JsonObject获取key对应的string value
     * @param dataJson
     * @param key
     * @return
     */
    public static String getStrFromJson(JSONObject dataJson, String key) {
        Objects.requireNonNull(dataJson);
        Objects.requireNonNull(key);
        return dataJson.containsKey(key) ? dataJson.getString(key) : "";
    }

    /**
     * 从Json字符串中获取key对应的String value
     * @param dataJson
     * @param key
     * @return
     */
    public String getStrFromJson(String dataJson, String key) {
        if (Strings.isBlank(dataJson) || Strings.isBlank(key)) {
            return Strings.EMPTY_STRING;
        }
        return getStrFromJson(JSON.parseObject(dataJson), key);
    }

    /**
     * 从JsonObject获取key对应的object value
     * @param dataJson
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public static  <T> T getObjFromJson(JSONObject dataJson, String key, Class<T> clazz) {
        Objects.requireNonNull(dataJson);
        Objects.requireNonNull(key);
        return dataJson.containsKey(key) ? dataJson.getObject(key, clazz) : null;
    }

    /**
     * 判断文件路径是否存在
     * @param filePath
     * @return
     */
    public static Boolean filePathExist(String filePath) {
        if (Strings.isNullOrEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return file.exists();
    }


}
