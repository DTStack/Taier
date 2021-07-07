package com.dtstack.batch.vo;

import com.dtstack.dtcenter.loader.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 数据源类型前端展示vo层
 *
 * @author ：wangchuan
 * date：Created in 上午10:05 2020/10/26
 * company: www.dtstack.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceTypeVO {

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源类型
     */
    private Integer value;

    /**
     * 排序标示
     */
    private Integer order;

    /**
     * dto -> vo
     * @param dataSourceType
     * @return
     */
    public static DataSourceTypeVO toVO (DataSourceType dataSourceType) {
        if (Objects.isNull(dataSourceType)) {
            return new DataSourceTypeVO();
        }
        return DataSourceTypeVO.builder()
                .name(dataSourceType.getName())
                .value(dataSourceType.getVal())
                .order(dataSourceType.getOrder()).build();
    }
}
