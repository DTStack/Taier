package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author ：qianyi
 * date：Created in 下午10:51 2021/04/27
 * company: www.dtstack.com
 */

@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SolrSourceDTO extends RdbmsSourceDTO {
    /**
     * zk 地址
     */
    private String zkHost;

    /**
     * chroot
     */
    private String chroot;


    @Override
    public Integer getSourceType() {
        return DataSourceType.SOLR.getVal();
    }

}
