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

import { useEffect, useState } from 'react';

import { ENGINE_SOURCE_TYPE, RESOURCE_TYPE } from '@/constant';

/**
 * 初始化
 */
function initailValue() {
    const initailValue: Record<string, boolean> = {};
    for (const key in ENGINE_SOURCE_TYPE) {
        initailValue[(ENGINE_SOURCE_TYPE as any)[key]] = false;
    }
    return initailValue;
}

function handleEngine(enginList: any[], type: string) {
    if (type == RESOURCE_TYPE.KUBERNETES) {
        return enginList.filter((item: any) => item.resourceType == type).length > 0;
    }
    return enginList.filter((item: any) => item.engineType == type).length > 0;
}

function useEnv({
    clusterId,
    form,
    clusterList,
    visible,
}: {
    clusterId: any;
    form: any;
    clusterList: any[];
    visible: boolean;
}) {
    const [queueList, setQueueList] = useState([]);
    const [env, setEnv] = useState({ ...initailValue() });
    useEffect(() => {
        if (!clusterId) return;
        if (!visible) {
            form.resetFields(['queueId']);
            setEnv({ ...initailValue() });
            setQueueList([]);
            return;
        }

        const currentCluster = clusterList.filter((clusItem: any) => clusItem?.clusterId == clusterId); // 选中当前集群
        const currentEngineList = currentCluster?.[0]?.engines || [];

        const hadoopEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_SOURCE_TYPE.HADOOP);
        const newEnv: Record<string, boolean> = {};
        for (const key in ENGINE_SOURCE_TYPE) {
            newEnv[(ENGINE_SOURCE_TYPE as any)[key]] = handleEngine(
                currentEngineList,
                (ENGINE_SOURCE_TYPE as any)[key]
            );
        }
        setEnv({ ...newEnv });
        setQueueList(hadoopEngine?.[0]?.queues || []);
    }, [clusterId, clusterList, visible, form]);
    return { env, queueList };
}
export default useEnv;
