package com.dtstack.engine.api.vo;


import com.dtstack.engine.api.domain.Queue;
import io.swagger.annotations.ApiModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
@ApiModel
public class QueueVO {

    public static QueueVO toVO(Queue queue) {
        QueueVO vo = new QueueVO();
        vo.setQueueId(queue.getId());
        vo.setQueueName(queue.getQueuePath());
        return vo;
    }


    public static List<QueueVO> toVOs(List<Queue> queues) {
        List<QueueVO> vos = new ArrayList<>();
        if (CollectionUtils.isEmpty(queues)) {
            return vos;
        }

        for (Queue queue : queues) {
            vos.add(QueueVO.toVO(queue));
        }

        return vos;
    }

    private Long queueId;

    private String queueName;

    public Long getQueueId() {
        return queueId;
    }

    public void setQueueId(Long queueId) {
        this.queueId = queueId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }
}
