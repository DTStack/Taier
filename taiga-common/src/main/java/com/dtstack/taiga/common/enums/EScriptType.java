package com.dtstack.taiga.common.enums;

public enum EScriptType {

    SparkSQL(0, "Spark SQL"),
    Python_2x(1, "Python2.x"),
    Python_3x(2, "Python3.x"),
    Shell(3, "Shell"),
    GaussDBSQL(4, "GaussDB SQL"),
    ImpalaSQL(5, "Impala SQL"),
    HiveSQL(6, "Hive SQL");

    private Integer type;

    private String name;


    EScriptType(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public static EScriptType getEScriptType(int type){
        EScriptType[] eScriptTypes = EScriptType.values();
        for(EScriptType eScriptType : eScriptTypes){
            if(eScriptType.type == type){
                return eScriptType;
            }
        }
        return null;
    }

    public EJobType getEJobType() {
        switch (this) {
            case SparkSQL: return EJobType.SPARK_SQL;
            case Python_2x:
            case Python_3x: return EJobType.PYTHON;
            case Shell: return EJobType.SHELL;
            case GaussDBSQL: return EJobType.GaussDB_SQL;
            case HiveSQL: return EJobType.HIVE_SQL;
            case ImpalaSQL: return EJobType.IMPALA_SQL;
        }

        return null;
    }
}
