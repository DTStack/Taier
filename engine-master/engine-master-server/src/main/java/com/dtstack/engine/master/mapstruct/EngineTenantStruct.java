package com.dtstack.engine.master.mapstruct;

import com.dtstack.engine.domain.po.EngineTenantPO;
import com.dtstack.engine.master.vo.EngineTenantVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/9/8
 */
@Mapper(componentModel = "spring")
public interface EngineTenantStruct {


    EngineTenantVO toEngineTenantVO(EngineTenantPO engineTenantPO);

    List<EngineTenantVO> toEngineTenantVOs(List<EngineTenantPO> engineTenantPOs);
}
