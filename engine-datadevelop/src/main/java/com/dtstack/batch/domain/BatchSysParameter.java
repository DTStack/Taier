package com.dtstack.batch.domain;


import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * Reason:
 * Date: 2017/6/7
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */
@Data
public class BatchSysParameter {

    private long id;

    private String paramName;

    private String paramCommand;

    /**
     * 是否删除
     */
    private int isDeleted;

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean strIsSysParam(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        String target = String.format("${%s}", this.getParamName());
        return target.equals(str);
    }
}
