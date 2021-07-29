package com.dtstack.batch.mapstruct.vo;

import com.dtstack.batch.web.security.vo.result.SecurityResultAudit;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SecurityAuditMapstructTransfer {

    SecurityAuditMapstructTransfer INSTANCE = Mappers.getMapper(SecurityAuditMapstructTransfer.class);

    /**
     * com.dtstack.dtcenter.common.console.SecurityResult<String> --> SecurityResult<String>
     *
     * @param stringSecurityResult
     * @return
     */
    SecurityResultAudit stringSecurityResultTOMySecurityResult(com.dtstack.dtcenter.common.console.SecurityResult<String> stringSecurityResult);
}
