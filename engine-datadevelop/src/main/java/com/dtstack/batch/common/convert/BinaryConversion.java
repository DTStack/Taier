package com.dtstack.batch.common.convert;

public class BinaryConversion {
    //用来转换B M G 数字的方法
    public static String getPrintSize(long size, boolean isPerSecond) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + getUnit("B", isPerSecond);
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + getUnit("KB", isPerSecond);
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + getUnit("MB", isPerSecond);
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + getUnit("GB", isPerSecond);
        }
    }

    private static String getUnit(String unit, boolean isPerSecond) {
        if (isPerSecond) {
            return unit + "/S";
        }
        return unit;
    }

}

