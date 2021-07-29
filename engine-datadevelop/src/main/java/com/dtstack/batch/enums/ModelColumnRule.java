package com.dtstack.batch.enums;

/**
 * @author sanyue
 */
public enum ModelColumnRule {
    /**
     * 原子指标
     */
    ATOM(1),
    /**
     * 衍生指标
     */
    DERIVE(2);

    private int type;

    ModelColumnRule(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static ModelColumnRule getByType(int type) {
        for (ModelColumnRule rule : ModelColumnRule.values()) {
            if (rule.getType() == type) {
                return rule;
            }
        }
        return null;
    }
}
