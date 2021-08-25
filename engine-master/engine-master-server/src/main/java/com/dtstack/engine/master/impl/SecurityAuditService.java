package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.security.ApiOperateTypeVO;
import com.dtstack.engine.api.vo.security.SecurityLogVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecurityAuditService {

    /**
     * @param appTag
     * @param startTime
     * @param endTime
     * @param operator
     * @param currentPage
     * @param pageSize
     * @return
     */

    public PageResult<List<SecurityLogVO>> pageQuery(@Param("appTag") String appTag,
                                                     @Param("tenantId") long tenantId,
                                                     @Param("startTime") Long startTime,
                                                     @Param("endTime") Long endTime,
                                                     @Param("operator") String operator,
                                                     @Param("currentPage") Integer currentPage,
                                                     @Param("pageSize") Integer pageSize,
                                                     @Param("operation") String oprtation,
                                                     @Param("operationObject") String operationObject,
                                                     @Param("isRoot") Boolean isRoot) {

        PageResult<List<SecurityLogVO>> result = new PageResult<>(currentPage, pageSize, 1, 1, null);
        return result;
    }

    /**
     * 添加安全日志接口免登陆，需将参数加密传输
     * @param sign
     */
    public void addSecurityLog(@Param("sign") String sign) {
    }

    public List<ApiOperateTypeVO> getOperationList(@Param("appTag") String appTag){
        List<ApiOperateTypeVO> result = new ArrayList<>();
        return result;
    }


}
