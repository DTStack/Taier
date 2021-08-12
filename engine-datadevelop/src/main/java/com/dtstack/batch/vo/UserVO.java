package com.dtstack.batch.vo;

import com.dtstack.engine.api.domain.User;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * @author sishu.yss
 */
@Data
public class UserVO extends User {

    private static final Logger logger = LoggerFactory.getLogger(UserVO.class);

    private Long tenantId = 0L;

    private Integer roleValue = 0;

    private String roleName;

    private Long dtuicTenantId = 0L;

    private Boolean tenantOwner;
    private Boolean isRootOnly;
    private Boolean isOwnerOnly;

    public Boolean getRootOnly() {
        return isRootOnly;
    }

    public void setRootOnly(Boolean rootOnly) {
        isRootOnly = rootOnly;
    }

    public Boolean getOwnerOnly() {
        return isOwnerOnly;
    }

    public void setOwnerOnly(Boolean ownerOnly) {
        isOwnerOnly = ownerOnly;
    }



    public static UserVO toVO(User origin) {
        UserVO vo = new UserVO();
        try {
            BeanUtils.copyProperties(origin, vo);
        } catch (Exception e) {
            logger.error("", e);
        }
        return vo;
    }
}
