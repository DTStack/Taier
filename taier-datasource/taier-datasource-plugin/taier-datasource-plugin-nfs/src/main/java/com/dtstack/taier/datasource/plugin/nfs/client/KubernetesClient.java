package com.dtstack.taier.datasource.plugin.nfs.client;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.plugin.nfs.util.NfsUtil;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.emc.ecs.nfsclient.nfs.nfs3.Nfs3;
import lombok.extern.slf4j.Slf4j;
/**
 * nfs client
 *
 * @author ：wangchuan
 * date：Created in 下午2:08 2022/3/15
 * company: www.dtstack.com
 */
@Slf4j
public class KubernetesClient extends AbsNoSqlClient {

    @Override
    public Boolean testCon(ISourceDTO source) {
        Nfs3 client = NfsUtil.getClient(source);
        AssertUtils.notNull(client, "nfs client can't be null");
        return true;
    }
}