package com.dtstack.engine.lineage.param;

import com.dtstack.engine.dto.DataSourceDTO;

import java.util.List;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 5:26 下午 2020/11/25
 */
public class DataSourceParam {

    private List<DataSourceDTO> dataSourceDTOList;

    public List<DataSourceDTO> getDataSourceDTOList() {
        return dataSourceDTOList;
    }

    public void setDataSourceDTOList(List<DataSourceDTO> dataSourceDTOList) {
        this.dataSourceDTOList = dataSourceDTOList;
    }
}
