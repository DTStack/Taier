package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author K
 * @date 2021/3/1 10:57
 */
@AllArgsConstructor
@NoArgsConstructor
public class StartTaskVO extends DtInsightAuthParam {

    private Long id;

    /**
     * savePoint 地址
     */
    private String externalPath;


    /**
     * 操作人
     */
    private String userName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalPath() {
        return externalPath;
    }

    public void setExternalPath(String externalPath) {
        this.externalPath = externalPath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
