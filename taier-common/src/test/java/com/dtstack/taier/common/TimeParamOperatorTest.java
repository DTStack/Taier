package com.dtstack.taier.common;

import com.dtstack.taier.common.util.TimeParamOperator;
import org.junit.Test;

import java.text.ParseException;

public class TimeParamOperatorTest {

    @Test
    public void testParamOperator() {
        String result = TimeParamOperator.transform("yyyyMM - 1", "20171212010101");
        System.out.println(result);
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,10,'-')]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,-6,'/')]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,-6,':')]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,10)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMM,-10)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyy,-10)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyy,10)]", "20180607010101"));

        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd+7*1,'-']", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd-7*1,'-']", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd+10,'-']", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd-10,'-']", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss+3/24,'-']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss-4/24,'-']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss+3/24/60,'-']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss-3/24/60,'-']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMM,':']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd,':']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHH,':']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHHmm,':']", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHHmmss,':']", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddhh24,':']", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddhh24mm,':']", "20180607172233"));


        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMMdd,12*2)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMMdd,-12*1)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMMdd,2)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[add_months(yyyyMMdd,-2)]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd+7*1]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd-7*1]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd+10]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd-10]", "20180607010101"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss+3/24]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss-4/24]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss+3/24/60]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[hh24miss-3/24/60]", "20180607030000"));

        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyy]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[mm]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[dd]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[HH]", "20180607031700"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[MM]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[ss]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMM]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMdd]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHH]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHHmm]", "20180607030000"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddHHmmss]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddhh24]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyyMMddhh24mm]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyy-MM-dd]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[yyyy-MM-dd HH:mm:ss]", "20180607172233"));
        System.out.println(TimeParamOperator.dealCustomizeTimeOperator("$[HH:mm:ss]", "20180607172233"));

    }
}
