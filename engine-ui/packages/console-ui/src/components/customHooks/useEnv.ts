import { useEffect, useState } from 'react'
import { ENGINE_TYPE, RESOURCE_TYPE } from '../../consts'

/**
 * 初始化
 */
function initailValue () {
    const initailValue = {}
    for (const key in ENGINE_TYPE) {
        initailValue[ENGINE_TYPE[key]] = false
    }
    return initailValue
}

function handleEngine (enginList, type) {
    if (type == RESOURCE_TYPE.KUBERNETES) {
        return enginList.filter((item: any) => item.resourceType == type).length > 0
    }
    return enginList.filter((item: any) => item.engineType == type).length > 0
}

function useEnv ({ clusterId, form, clusterList, visible }) {
    const [queueList, setQueueList] = useState([])
    const [env, setEnv] = useState({ ...initailValue() })
    useEffect(() => {
        if (!clusterId) return
        if (!visible) {
            form.resetFields(['queueId']);
            setEnv({ ...initailValue() })
            setQueueList([])
            return
        }

        const currentCluster = clusterList.filter((clusItem: any) => clusItem?.clusterId == clusterId); // 选中当前集群
        const currentEngineList = currentCluster?.[0]?.engines || [];

        const hadoopEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.HADOOP);
        const newEnv = {}
        for (const key in ENGINE_TYPE) {
            newEnv[ENGINE_TYPE[key]] = handleEngine(currentEngineList, ENGINE_TYPE[key])
        }
        setEnv({ ...newEnv })
        setQueueList(hadoopEngine?.[0]?.queues || [])
    }, [clusterId, clusterList, visible, form])
    return { env, queueList }
}
export default useEnv
