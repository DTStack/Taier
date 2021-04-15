package com.dtstack.engine.api.vo.task;

/**
 * @Auther: dazhi
 * @Date: 2021/4/15 10:45 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class SaveTaskTaskVO {

    private String msg;

    /**
     * isRink true: 成环 false: 未成环
     *
     */
    private Boolean isRink;

    public static SaveTaskTaskVO noRink() {
        SaveTaskTaskVO saveTaskTaskVO = new SaveTaskTaskVO();
        saveTaskTaskVO.setRing(Boolean.FALSE);
        return saveTaskTaskVO;
    }

    public static SaveTaskTaskVO isRink(String msg) {
        SaveTaskTaskVO saveTaskTaskVO = new SaveTaskTaskVO();
        saveTaskTaskVO.setRing(Boolean.TRUE);
        saveTaskTaskVO.setMsg(msg);
        return saveTaskTaskVO;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getRing() {
        return isRink;
    }

    public void setRing(Boolean ring) {
        isRink = ring;
    }

}
