package com.dtstack.batch.vo;

import com.dtstack.batch.domain.Catalogue;
import lombok.Data;

import java.util.List;

/**
 * Created by Administrator on 2017/4/28 0028.
 */
@Data
public class CatalogueVO {

    public static CatalogueVO toVO(Catalogue catalogue) {
        CatalogueVO vo = new CatalogueVO();
        vo.setName(catalogue.getNodeName());
        vo.setLevel(catalogue.getLevel());
        vo.setId(catalogue.getId());
        vo.setOrderVal(catalogue.getOrderVal());
        vo.setParentId(catalogue.getNodePid());
        vo.setEngineType(catalogue.getEngineType());
        return vo;
    }

    private Long id = 0L;
    private Long parentId = 0L;
    private String name;
    /**
     * 项目别名 中文名
     */
    private String projectAlias;
    private Integer level;
    private String type;
    private Integer taskType;
    private Integer resourceType;
    private String catalogueType;
    private String createUser;
    private Integer orderVal;
    private List<CatalogueVO> children;
    private ReadWriteLockVO readWriteLockVO;
    private Integer version;

    /**
     * 操作模式 0-资源模式，1-编辑模式
     */
    private Integer operateModel = 1;

    /**
     * 2-python2.x,3-python3.x
     */
    private Integer pythonVersion;

    /**
     * 0-TensorFlow,1-MXNet
     */
    private Integer learningType;

    private Integer scriptType;

    /**
     * 0-普通任务，1-工作流中的子任务
     */
    private Integer isSubTask = 0;

    /**
     *  引擎类型
     */
    private Integer engineType;

    /**
     * 任务状态 0：未提交 ；1：已提交
     */
    private Integer status;

    public CatalogueVO(){}

    public CatalogueVO(long id, long parentId, String name, Integer level, String type) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.level = level;
        this.type = type;
    }

    public Integer getIsSubTask() {
        return isSubTask;
    }

    public void setIsSubTask(Integer isSubTask) {
        this.isSubTask = isSubTask;
    }

}
