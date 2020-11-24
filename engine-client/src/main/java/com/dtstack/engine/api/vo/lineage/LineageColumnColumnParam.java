package com.dtstack.engine.api.vo.lineage;

import java.util.List;

/**
 * @author chener
 * @Classname LineageColumnColumnParam
 * @Description
 * @Date 2020/11/24 15:46
 * @Created chener@dtstack.com
 */
public class LineageColumnColumnParam {

    private List<LineageColumnColumnVO> lineageTableTableVOs;

    public List<LineageColumnColumnVO> getLineageTableTableVOs() {
        return lineageTableTableVOs;
    }

    public void setLineageTableTableVOs(List<LineageColumnColumnVO> lineageTableTableVOs) {
        this.lineageTableTableVOs = lineageTableTableVOs;
    }
}
