package com.dtstack.taier.develop.enums.develop;


import com.dtstack.taier.common.exception.RdosDefineException;

/**
 * @author zhiChen
 * @date 2021/10/25 14:13
 */
public enum EDataSyncJobType {

    /**
     * 数据同步
     */
    SYNC(2, "离线任务", 2),

    /**
     * 实时采集
     */
    DATA_ACQUISITION(37, "实时任务", 2),
//
//    /**
//     * 批量同步
//     */
//    BATCH_SYNC(40, "批量同步任务", 2)
    ;

    /**
     * 类型值
     */
    private Integer type;

    /**
     * 名称
     */
    private String name;

    /**
     * 引擎能够接受的jobType
     * SQL              0
     * MR               1
     * SYNC             2
     * PYTHON           3
     * 不接受的任务类型    -1
     */
    private Integer engineJobType;

    EDataSyncJobType(Integer type, String name, Integer engineJobType){
        this.type = type;
        this.name = name;
        this.engineJobType = engineJobType;
    }

    public Integer getVal(){
        return this.type;
    }

    public String getName() {
        return name;
    }

    public Integer getEngineJobType() {
        return engineJobType;
    }

    public static EDataSyncJobType getEJobType(int type){
        EDataSyncJobType[] eJobTypes = EDataSyncJobType.values();
        for(EDataSyncJobType eJobType:eJobTypes){
            if(eJobType.type == type){
                return eJobType;
            }
        }
        return null;
    }

    public static Integer getEngineJobType(int type){
        EDataSyncJobType[] eJobTypes = EDataSyncJobType.values();
        for(EDataSyncJobType eJobType:eJobTypes){
            if(eJobType.type == type){
                if (eJobType.getVal() != -1){
                    return eJobType.getEngineJobType();
                }
                break;
            }

        }
        throw new RdosDefineException("不支持的任务类型");
    }
}
