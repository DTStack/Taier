package com.dtstack.taiga.common.util;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * SLOGAN:让现在编程未来
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a> 2019/10/24.
 * @description 键值对
 */
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Pair<L, R> implements java.io.Serializable {
    private static final long serialVersionUID = 3295957496904951095L;
    /**
     * 左值（键）
     */
    private L leftValue;
    /**
     * 右值（值）
     */
    private R rightValue;

    public L leftValue() {
        return this.leftValue;
    }

    public R rightValue() {
        return this.rightValue;
    }

    static public <L, R> Pair<L, R> of(L lValue, R rValue) {
        return new Pair<>(lValue, rValue);
    }
}
