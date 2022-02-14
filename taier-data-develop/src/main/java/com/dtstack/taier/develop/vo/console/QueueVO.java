/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.develop.vo.console;


import com.dtstack.taier.dao.domain.Queue;
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
