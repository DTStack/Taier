package com.dtstack.taier.develop.dto.devlop;

public class StreamTaskCheckpointVO {

    private static String SPLIT = "_";


    /**
     * 构成: rdos_stream_task_checkpoint的记录id + checkpoint内容的具体id
     */
    private String id;


    private Long time;

    /**
     * 其实应该服务器重新查询,太麻烦了暂时直接让客户端回传
     */
    private String externalPath;

    public StreamTaskCheckpointVO(Long dbId, Long cpId, Long time, String externalPath) {
        this.id = dbId + SPLIT + cpId;
        this.time = time;
        this.externalPath = externalPath;
    }

    public StreamTaskCheckpointVO() {
    }

    public static String getSPLIT() {
        return SPLIT;
    }

    public static void setSPLIT(String SPLIT) {
        StreamTaskCheckpointVO.SPLIT = SPLIT;
    }

    public String getId() {
        return id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getExternalPath() {
        return externalPath;
    }

    public void setExternalPath(String externalPath) {
        this.externalPath = externalPath;
    }
}
