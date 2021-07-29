package com.dtstack.batch.vo;

import com.dtstack.batch.domain.Project;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 项目概览：
 *   任务信息
 *   任务失败信息
 *   项目占用存储
 *   创建时间
 *   支持的引擎类型
 * Date: 2019/6/10
 * Company: www.dtstack.com
 * @author xuchao
 */
@Data
public class ProjectOverviewVO extends Project {

    private Integer jobSum;

    private Integer tableCount;

    private String totalSize;

    private Map<String, Integer> taskCountMap;

    private Timestamp stick;

    private Integer stickStatus;

    private List<Integer> supportEngineType;

    private String cataloguePath;
}
