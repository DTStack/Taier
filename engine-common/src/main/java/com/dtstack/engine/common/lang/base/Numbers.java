package com.dtstack.engine.common.lang.base;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Objects;

public class Numbers {
    public static final Long LONG_ZERO = Long.valueOf(0L);
    public static final Integer INTEGER_ZERO = Integer.valueOf(0);

    public static final boolean zero(Long value) {
        return Objects.nonNull(value) && LONG_ZERO.equals(value);
    }

    public static final boolean zero(Integer value) {
        return Objects.nonNull(value) && INTEGER_ZERO.equals(value);
    }

    /**
     * 判断给定字符串是否可以转为整数
     *
     * @param value 给定的字符串型整数
     * @return 若能转换为整数, 则返回true
     */
    public static final boolean isInteger(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (i == 0 && ch == '-') {
                continue;
            } else {
                if (!Character.isDigit(ch)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 是否是浮点数
     *
     * @param value 规定的字符串型浮点数
     * @return 若能转换为浮点数, 则返回true
     */
    public static final boolean isDouble(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return false;
        }
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 分区大小
     *
     * @param total 记录总数
     * @param size  每页记录数
     * @return 分区数
     */
    public static final Long partition(Long total, Long size) {
        Objects.requireNonNull(total);
        Objects.requireNonNull(size);

        if (size > 0L) {
            return total / size
                    + (total % size == 0 ? 0L : 1L);
        } else {
            return total;
        }
    }

    /**
     * 分区大小
     *
     * @param total 记录总数
     * @param size  每页记录数
     * @return 分区数
     */
    public static final Integer partition(Integer total, Integer size) {
        Objects.requireNonNull(total);
        Objects.requireNonNull(size);

        if (size > 0) {
            return total / size
                    + (total % size == 0 ? 0 : 1);
        } else {
            return total;
        }
    }

    public static final String price(Double number) {
        Objects.requireNonNull(number);
        return new DecimalFormat("#0.00").format(number);
    }

    public static final String percent(Number object) {
        Objects.requireNonNull(object);
        return NumberFormat.getPercentInstance().format(object);
    }

    public static final String decimal(Number number) {
        return decimal(number, 2, RoundingMode.HALF_UP, true, true);
    }

    public static final String decimal(Number number, int scale, RoundingMode roundingMode, boolean displayDecimalZero, boolean comma) {
        Objects.requireNonNull(number);
        Checks.isTrue(scale >= 0, "精度必须大于等于0");

        if (number instanceof Integer || number instanceof Long) {
            if (comma) {
                return new DecimalFormat("#,###").format(number);
            }
        } else if (number instanceof BigDecimal || number instanceof Double || number instanceof Float) {
            BigDecimal newNumber = null;
            if (number instanceof BigDecimal) {
                newNumber = (BigDecimal) number;
            } else if (number instanceof Double) {
                newNumber = new BigDecimal((Double) number);
            } else if (number instanceof Float) {
                newNumber = new BigDecimal((Float) number);
            }

            StringBuffer sb = new StringBuffer();
            if (comma) {
                sb.append("#,###.");
            } else {
                sb.append("#.");
            }
            sb.append(Strings.repeat(displayDecimalZero ? '0' : '#', scale));
            DecimalFormat df = new DecimalFormat(sb.toString());
            if (newNumber == null) {
                return "-";
            } else {
                return df.format(newNumber.setScale(scale, roundingMode));
            }
        }
        return String.valueOf(number);
    }
}