package com.dtstack.taier.datasource.plugin.doris;

import com.dtstack.taier.datasource.plugin.mysql5.MysqlConnFactory;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.List;

/**
 * @author ：qianyi
 * date：Created in 下午1:46 2021/07/09
 * company: www.dtstack.com
 */
@Slf4j
public class DorisConnFactory extends MysqlConnFactory {

    public DorisConnFactory() {
        driverName = DataBaseType.Doris.getDriverClassName();
        this.errorPattern = new DorisErrorPattern();
    }

    @Override
    public Connection getConn(ISourceDTO sourceDTO) {
        //jdbc也支持多节点连接
        RdbmsSourceDTO rdbmsSource = (RdbmsSourceDTO) sourceDTO;
        AssertUtils.notBlank(rdbmsSource.getUrl(), "url can't be null");

        //url支持以逗号分隔的多节点，任一节点连接成功即可
        List<String> urls = Lists.newArrayList(rdbmsSource.getUrl().split(","));
        Exception lastException = null;
        for (String url : urls) {
            rdbmsSource.setUrl(url);
            try {
                return super.getConn(rdbmsSource);
            } catch (Exception e) {
                lastException = e;
                log.error("dorisRestful connect error.", e);
            }
        }

        //全部节点都连接失败则抛出最后一个节点的失败信息
        throw new SourceException("no url available ,last exception : ", lastException);
    }
}
