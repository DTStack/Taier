package com.dtstack.engine.sql.hive.until;

/**
 * 判断是否是函数操作
 */
public enum ASTNodeFunctionEnum {
    TOK_FUNCTION(750),
    TOK_FUNCTIONDI(751),
    TOK_FUNCTIONSTAR(752),
    DIVIDE(15),
    PLUS(322),
    MINUS(318),
    STAR(330),
    GREATERTHAN(23),
    GREATERTHANOREQUALTO(24),
    LESSTHAN(313),
    LESSTHANOREQUALTO(314),
    EQUAL(20),
    EQUAL_NS(21),
    KW_OR(193),
    KW_AND(34),
    KW_NOT(184)
    ;
    ASTNodeFunctionEnum(Integer value) {
        this.value = value;
    }

    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public static Boolean isFunction(Integer type){
        for (ASTNodeFunctionEnum value : ASTNodeFunctionEnum.values()) {
            if (type.equals(value.getValue())){
                return true;
            }
        }
        return false;
    }

}
