package com.dtstack.taier.develop.bo.datasource;

import com.dtstack.taier.dao.domain.po.BasePageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author 全阅
 * @Description:
 * @Date: 2021/3/8 19:14
 */
@ApiModel("数据源列表查询参数")
public class DsListParam extends BasePageParam {

    @ApiModelProperty("搜索参数")
    private String search;

    @ApiModelProperty(value = "数据源类型")
    private List<String> dataTypeList;

    @ApiModelProperty("是否显示默认数据库，0为不显示，1为显示")
    private Integer isMeta;

    @ApiModelProperty("连接状态")
    private List<Integer> status;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<String> getDataTypeList() {
        return dataTypeList;
    }

    public void setDataTypeList(List<String> dataTypeList) {
        this.dataTypeList = dataTypeList;
    }

    public Integer getIsMeta() {
        return isMeta;
    }

    public void setIsMeta(Integer isMeta) {
        this.isMeta = isMeta;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }
}
