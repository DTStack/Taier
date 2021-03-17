package com.dtstack.engine.api.vo.lineage;

import java.util.List;

/**
 * @author chener
 * @Classname LineageTableTableParam
 * @Description TODO
 * @Date 2020/12/1 14:40
 * @Created chener@dtstack.com
 */
public class LineageTableTableParam {

    private List<LineageTableTableVO> lineageTableTableVOs;

    public List<LineageTableTableVO> getLineageTableTableVOs() {
        return lineageTableTableVOs;
    }

    public void setLineageTableTableVOs(List<LineageTableTableVO> lineageTableTableVOs) {
        this.lineageTableTableVOs = lineageTableTableVOs;
    }
}
