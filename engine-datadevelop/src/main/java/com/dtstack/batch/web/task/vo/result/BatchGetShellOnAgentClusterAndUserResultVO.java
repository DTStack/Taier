package com.dtstack.batch.web.task.vo.result;

import com.dtstack.batch.web.task.vo.result.BatchGetShellOnAgentUserNameResultVO;
import lombok.Data;

import java.util.List;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/6/24 8:44 下午
 */
@Data
public class BatchGetShellOnAgentClusterAndUserResultVO {

    /**
     * 节点的label
     */
    private String label;

    /**
     * label 的ip
     */
    private String labelIp;

    /**
     * 集群id
     */
    private Long clusterId;

    /**
     * 组件类型
     */
    private Integer componentTypeCode;

    /**
     * label 集群登录的用户名
     */
    private List<BatchGetShellOnAgentUserNameResultVO> userNameList;

    /**
     * 是否默认的label
     */
    private Boolean isDefault;

}
