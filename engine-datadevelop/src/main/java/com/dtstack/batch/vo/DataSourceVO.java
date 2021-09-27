package com.dtstack.batch.vo;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.domain.BatchDataSource;
import com.dtstack.engine.domain.User;
import com.dtstack.dtcenter.common.util.Base64Util;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/10
 */
@Data
public class DataSourceVO{

    private Long id = 0L;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    private Long tenantId;

    private Long projectId;

    private String dataName;

    private String dataDesc;

    private Integer type;

    private Integer active;

    private Integer linkState;

    private Long modifyUserId;

    private Long createUserId;

    private User modifyUser;

    private JSONObject dataJson;

    private String dataJsonString;

    private Long linkSourceId;

    private String linkSourceName;

    private Integer isDefault;

    private Map<String, Object> kerberosConfig;

    private String localKerberosConf;

    public BatchDataSource toEntity() {
        BatchDataSource source = new BatchDataSource();
        source.setId(this.getId());
        source.setProjectId(this.getProjectId());
        source.setTenantId(this.getTenantId());
        source.setGmtCreate(this.getGmtCreate());
        source.setGmtModified(this.getGmtModified());
        source.setCreateUserId(this.getCreateUserId());
        source.setDataName(this.getDataName());
        source.setDataDesc(this.getDataDesc());
        source.setDataJson(Base64Util.baseEncode(this.dataJson.toJSONString()));
        source.setType(this.getType());
        source.setCreateUserId(this.getCreateUserId());
        source.setModifyUserId(this.getModifyUserId());
        source.setActive(this.active);
        source.setLinkState(this.linkState);
        source.setIsDefault(this.isDefault);
        return source;
    }

    public static DataSourceVO toVO(BatchDataSource source, int active) {
        DataSourceVO vo = new DataSourceVO();
        vo.setId(source.getId());
        vo.setGmtCreate(source.getGmtCreate());
        vo.setGmtModified(source.getGmtModified());
        vo.setProjectId(source.getProjectId());
        vo.setTenantId(source.getTenantId());
        vo.setDataName(source.getDataName());
        vo.setDataDesc(source.getDataDesc());
        vo.setType(source.getType());
        vo.setModifyUserId(source.getModifyUserId());
        vo.setDataJson(JSON.parseObject(source.getDataJson()));
        vo.setCreateUserId(source.getCreateUserId());
        vo.setIsDefault(source.getIsDefault());
        vo.setLinkState(source.getLinkState());
        //兼容之前的设定0:未使用, 1:使用
        if(active > 0){
            vo.setActive(1);
        }else{
            vo.setActive(0);
        }

        return vo;
    }

    @Override
    public String toString() {
        return "DataSourceVO{" +
                "id=" + id +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", tenantId=" + tenantId +
                ", projectId=" + projectId +
                ", dataName='" + dataName + '\'' +
                ", dataDesc='" + dataDesc + '\'' +
                ", type=" + type +
                ", active=" + active +
                ", linkState=" + linkState +
                ", modifyUserId=" + modifyUserId +
                ", createUserId=" + createUserId +
                ", modifyUser=" + modifyUser +
                ", dataJson=" + dataJson +
                ", linkSourceId=" + linkSourceId +
                ", linkSourceName='" + linkSourceName + '\'' +
                '}';
    }
}
