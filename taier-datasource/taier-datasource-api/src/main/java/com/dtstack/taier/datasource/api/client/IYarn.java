package com.dtstack.taier.datasource.api.client;

import com.dtstack.taier.datasource.api.base.Client;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.yarn.YarnApplicationInfoDTO;
import com.dtstack.taier.datasource.api.dto.yarn.YarnApplicationStatus;
import com.dtstack.taier.datasource.api.dto.yarn.YarnResourceDTO;
import com.dtstack.taier.datasource.api.dto.yarn.YarnResourceDescriptionDTO;

import java.util.List;

/**
 * yarn client interface
 *
 * @author ：wangchuan
 * date：Created in 下午1:50 2022/3/15
 * company: www.dtstack.com
 */
public interface IYarn extends Client {

    /**
     * 获取 yarn 上的任务信息
     *
     * @param source        数据源信息
     * @param status        任务状态, 不可为空, 状态 {@link YarnApplicationStatus}
     * @param applicationId 任务 appId, 可为空
     * @param taskName      任务名称, 只会查询以 _taskName 结尾的 application
     * @return yarn app list
     */
    List<YarnApplicationInfoDTO> listApplication(ISourceDTO source, YarnApplicationStatus status,
                                                 String taskName, String applicationId);

    /**
     * 获取 yarn 资源信息
     *
     * @param source 数据源信息
     * @return yarn 资源信息
     */
    YarnResourceDTO getYarnResource(ISourceDTO source);

    /**
     * 获取 yarn 资源队列信息
     *
     * @param source 数据源信息
     * @return yarn 资源队列信息
     */
    YarnResourceDescriptionDTO getYarnResourceDescription(ISourceDTO source);
}
