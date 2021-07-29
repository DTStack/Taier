package com.dtstack.engine.datasource.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * Created by 袋鼠云-数栈产研部-应用研发中心.
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a>
 * @date 2021/3/29
 * @desc 校验重复名称枚举
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FieldEnum implements BaseEnum {
    /**
     * 模型名称
     */
    MODEL_NAME(1, "model_name"),
    /**
     * 模型英文名称
     */
    MODEL_EN_NAME(2, "model_en_name"),
    ;

    private Integer code;
    private String name;

    public static FieldEnum ofCode(Integer code) {
        FieldEnum[] values = values();
        for (FieldEnum e : values) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    public static String obtainName(Integer code) {
        return Optional.ofNullable(ofCode(code))
                .map(BaseEnum::getName)
                .orElse(null);
    }

    /**
     * 判断code合法性
     */
    public static boolean judge(Integer code) {
        if (code == null) {
            return false;
        }
        BaseEnum[] values = values();
        for (BaseEnum value : values) {
            if (value.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断code合法性
     */
    public static boolean judgeIgnoreNull(Integer code) {
        if (code == null) {
            return true;
        }
        BaseEnum[] values = values();
        for (BaseEnum value : values) {
            if (value.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
