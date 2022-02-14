package com.dtstack.taier.develop.vo.fill;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 4:40 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ReturnFillDataJobListVO {

    /**
     * 补数据ID
     */
    @ApiModelProperty(value = "补数据ID",example = "1")
    private Long id;

    /**
     * 补数据名称
     */
    @ApiModelProperty(value = "补数据名称",example = "123123")
    private String fillDataName;

    /**
     * REALLY_GENERATED(1,"表示正在生成"),
     * FILL_FINISH(2,"完成生成补数据实例"),
     * FILL_FAIL(3,"生成补数据失败"),
     */
    @ApiModelProperty(value = "REALLY_GENERATED(1,\"表示正在生成\"),FILL_FINISH(2,\"完成生成补数据实例\"),FILL_FAIL(3,\"生成补数据失败\"),",example = "1")
    private Integer fillGenerateStatus;

    /**
     * 补数据实例
     */
    private List<FillDataJobVO> fillDataJobVOLists;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFillDataName() {
        return fillDataName;
    }

    public void setFillDataName(String fillDataName) {
        this.fillDataName = fillDataName;
    }

    public Integer getFillGenerateStatus() {
        return fillGenerateStatus;
    }

    public void setFillGenerateStatus(Integer fillGenerateStatus) {
        this.fillGenerateStatus = fillGenerateStatus;
    }

    public List<FillDataJobVO> getFillDataJobVOLists() {
        return fillDataJobVOLists;
    }

    public void setFillDataJobVOLists(List<FillDataJobVO> fillDataJobVOLists) {
        this.fillDataJobVOLists = fillDataJobVOLists;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReturnFillDataJobListVO that = (ReturnFillDataJobListVO) o;
        return Objects.equals(id, that.id) && Objects.equals(fillDataName, that.fillDataName) && Objects.equals(fillGenerateStatus, that.fillGenerateStatus) && Objects.equals(fillDataJobVOLists, that.fillDataJobVOLists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fillDataName, fillGenerateStatus, fillDataJobVOLists);
    }

    @Override
    public String toString() {
        return "FillDataJobReturnListVO{" +
                "id=" + id +
                ", fillDataName='" + fillDataName + '\'' +
                ", fillGenerateStatus=" + fillGenerateStatus +
                ", fillDataJobVOLists=" + fillDataJobVOLists +
                '}';
    }
}
