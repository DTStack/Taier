import { useEffect, useState } from 'react'
import { ENGINE_TYPE } from '../../consts'

function useEnv ({ clusterId, form, clusterList, visible }) {
    const [queueList, setQueueList] = useState([])
    const [env, setEnv] = useState({
        hasHadoop: false,
        hasLibra: false,
        hasTiDB: false,
        hasOracle: false,
        hasGreenPlum: false,
        hasPresto: false
    })
    useEffect(() => {
        if (!clusterId) return

        const currentCluster = clusterList.filter((clusItem: any) => clusItem?.clusterId == clusterId); // 选中当前集群
        const currentEngineList = currentCluster?.[0]?.engines || [];
        const hadoopEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.HADOOP);
        const libraEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.LIBRA);
        const tiDBEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.TI_DB);
        const oracleEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.ORACLE);
        const greenPlumEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.GREEN_PLUM);
        const prestoEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.PRESTO);

        if (!visible) {
            setEnv({
                hasHadoop: hadoopEngine.length >= 1,
                hasLibra: libraEngine.length >= 1,
                hasTiDB: tiDBEngine.length > 0,
                hasOracle: oracleEngine.length > 0,
                hasGreenPlum: greenPlumEngine.length > 0,
                hasPresto: prestoEngine.length > 0
            })
            setQueueList(hadoopEngine?.[0]?.queues || [])
        } else {
            form.resetFields(['queueId']);
            setEnv({
                hasHadoop: false,
                hasLibra: false,
                hasTiDB: false,
                hasOracle: false,
                hasGreenPlum: false,
                hasPresto: false
            })
            setQueueList([])
        }
    }, [clusterId, clusterList, visible, form])
    return { env, queueList }
}
export default useEnv
