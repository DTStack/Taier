import React from 'react'
import { ENGINE_TYPE } from '../../consts'


export default (clusterId: string,form: any,clusterList: any[]) => {
  form?.resetFields(['queueId']);
  let currentCluster: any;
  currentCluster = clusterList.filter((clusItem: any) => clusItem.clusterId == clusterId); // 选中当前集群

  const currentEngineList = currentCluster?.[0]?.engines || [];
  const hadoopEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.HADOOP);
  const libraEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.LIBRA);
  const tiDBEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.TI_DB);
  const oracleEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.ORACLE);
  const greenPlumEngine = currentEngineList.filter((item: any) => item.engineType == ENGINE_TYPE.GREEN_PLUM);

  const hasHadoop = hadoopEngine.length >= 1;
  const hasLibra = libraEngine.length >= 1;
  const hasTiDB = tiDBEngine.length > 0;
  const hasOracle = oracleEngine.length > 0;
  const hasGreenPlum = greenPlumEngine.length > 0;

  const queueList = hadoopEngine?.[0]?.queues;

  return {
    hasHadoop,
    hasLibra,
    hasTiDB,
    hasOracle,
    hasGreenPlum,
    queueList
  }
}