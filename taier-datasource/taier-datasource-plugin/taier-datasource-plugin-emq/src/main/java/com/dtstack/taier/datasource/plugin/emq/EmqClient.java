package com.dtstack.taier.datasource.plugin.emq;

import com.dtstack.taier.datasource.api.dto.source.EMQSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;

/**
 * Date: 2020/4/7
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class EmqClient extends AbsNoSqlClient {
    @Override
    public Boolean testCon(ISourceDTO iSource) {
        EMQSourceDTO emqSourceDTO = (EMQSourceDTO) iSource;
        return EMQUtils.checkConnection(emqSourceDTO.getUrl(), emqSourceDTO.getUsername(), emqSourceDTO.getPassword());
    }
}
