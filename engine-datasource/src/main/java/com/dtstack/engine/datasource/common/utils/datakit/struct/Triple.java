package com.dtstack.engine.datasource.common.utils.datakit.struct;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * SLOGAN:让现在编程未来
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a> 2019/11/19.
 */
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Triple<A, B, C> implements java.io.Serializable {
    /**
     * A值
     */
    private A firstValue;
    /**
     * B值
     */
    private B secondValue;
    /**
     * C值
     */
    private C thirdValue;

    static public <A, B, C> Triple<A, B, C> with(A aValue, B bValue, C cValue) {
        return new Triple<>(aValue, bValue, cValue);
    }

    public A firstValue() {
        return this.firstValue;
    }

    public B secondValue() {
        return this.secondValue;
    }

    public C thirdValue() {
        return this.thirdValue;
    }
}
