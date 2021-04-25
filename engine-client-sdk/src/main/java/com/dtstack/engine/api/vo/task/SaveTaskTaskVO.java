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
     * isSave true: 保存成功  false: 保存失败
     *
     */
    private Boolean isSave;

    public static SaveTaskTaskVO save() {
        SaveTaskTaskVO saveTaskTaskVO = new SaveTaskTaskVO();
        saveTaskTaskVO.setSave(Boolean.TRUE);
        return saveTaskTaskVO;
    }

    public static SaveTaskTaskVO noSave(String msg) {
        SaveTaskTaskVO saveTaskTaskVO = new SaveTaskTaskVO();
        saveTaskTaskVO.setSave(Boolean.FALSE);
        saveTaskTaskVO.setMsg(msg);
        return saveTaskTaskVO;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSave() {
        return isSave;
    }

    public void setSave(Boolean save) {
        isSave = save;
    }
}
