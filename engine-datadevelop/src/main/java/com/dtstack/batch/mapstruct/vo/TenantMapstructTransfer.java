package com.dtstack.batch.mapstruct.vo;

import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.batch.vo.TenantVO;
import com.dtstack.batch.web.tenant.vo.query.BatchTenantAddOrUpdateVO;
import com.dtstack.batch.web.tenant.vo.result.TenantResultVO;
import com.dtstack.batch.web.tenant.vo.result.TenantUpdateResultVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TenantMapstructTransfer {

    TenantMapstructTransfer INSTANCE = Mappers.getMapper(TenantMapstructTransfer.class);

    /**
     * TenantAddOrUpdateVO -> Tenant
     *
     * @param addOrUpdateVO
     * @return
     */
    Tenant tenantAddOrUpdateVOToTenant(BatchTenantAddOrUpdateVO addOrUpdateVO);

    /**
     * Tenant -> TenantUpdateResultVO
     *
     * @param tenant
     * @return
     */
    TenantUpdateResultVO tenantToTenantUpdateResultVO(Tenant tenant);

    /**
     * TenantVO -> TenantResultVO
     *
     * @param tenantVO
     * @return
     */
    List<TenantResultVO> tenantVOToTenantResultVO(List<TenantVO> tenantVO);
}
