package com.dtstack.taier.develop.mapstruct.vo;

import com.dtstack.taier.datasource.api.dto.yarn.YarnResourceDTO;
import com.dtstack.taier.pluginapi.pojo.ClusterResource;
import org.mapstruct.Mapper;

/**
 * datasource transfer
 *
 * @author ：wangchuan
 * date：Created in 19:30 2022/10/8
 * company: www.dtstack.com
 */
@Mapper(componentModel = "spring")
public interface DatasourceMapstructTransfer {

    ClusterResource yarnResourceDTOtoClusterResource(YarnResourceDTO yarnResource);
}
