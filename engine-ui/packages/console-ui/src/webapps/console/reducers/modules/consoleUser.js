import { userActions, clusterActions } from '../../consts/consoleActions'
import { cloneDeep } from 'lodash'
const defaultState = {
    tenantList: [
        {
            'tenantId': 1,
            'tenantName': 'test'
        },
        {
            'tenantId': 2,
            'tenantName': '蘑古测试'
        }
    ],
    engineList: [{
        'engineName': 'HADOOP',
        engineId: 1
    }, {
        'engineName': 'Libra',
        engineId: 2
    }], // engine列表 （hadoop, libra）
    hadoopComponentList: [{
        'componentName': 'HDFS',
        'componentId': 1,
        'componentTypeCode': 4,
        'config': {
            'dfs.ha.namenodes.ns1': 'nn1,nn2',
            'fs.defaultFS': 'hdfs://ns1',
            'dfs.client.failover.proxy.provider.ns1': 'org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider',
            'dfs.namenode.rpc-address.ns1.nn2': 'kudu2:9000',
            'dfs.namenode.rpc-address.ns1.nn1': 'kudu1:9000',
            'dfs.nameservices': 'ns1',
            'fs.hdfs.impl.disable.cache': 'true',
            'fs.hdfs.impl': 'org.apache.hadoop.hdfs.DistributedFileSystem'
        }
    }, {
        'componentName': 'Flink',
        'componentId': 1,
        'componentTypeCode': 0,
        'config': {
            'dfs.ha.namenodes.ns1': 'nn1,nn2',
            'fs.defaultFS': 'hdfs://ns1',
            'dfs.client.failover.proxy.provider.ns1': 'org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider',
            'dfs.namenode.rpc-address.ns1.nn2': 'kudu2:9000',
            'dfs.namenode.rpc-address.ns1.nn1': 'kudu1:9000',
            'dfs.nameservices': 'ns1',
            'fs.hdfs.impl.disable.cache': 'true',
            'fs.hdfs.impl': 'org.apache.hadoop.hdfs.DistributedFileSystem'
        }
    }, {
        'componentName': 'SPARK',
        'componentId': 1,
        'componentTypeCode': 1,
        'config': {
            'dfs.ha.namenodes.ns1': 'nn1,nn2',
            'fs.defaultFS': 'hdfs://ns1',
            'dfs.client.failover.proxy.provider.ns1': 'org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider',
            'dfs.namenode.rpc-address.ns1.nn2': 'kudu2:9000',
            'dfs.namenode.rpc-address.ns1.nn1': 'kudu1:9000',
            'dfs.nameservices': 'ns1',
            'fs.hdfs.impl.disable.cache': 'true',
            'fs.hdfs.impl': 'org.apache.hadoop.hdfs.DistributedFileSystem'
        }
    }, {
        'componentName': 'LEARNING',
        'componentId': 1,
        'componentTypeCode': 2,
        'config': {
            'dfs.ha.namenodes.ns1': 'nn1,nn2',
            'fs.defaultFS': 'hdfs://ns1',
            'dfs.client.failover.proxy.provider.ns1': 'org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider',
            'dfs.namenode.rpc-address.ns1.nn2': 'kudu2:9000',
            'dfs.namenode.rpc-address.ns1.nn1': 'kudu1:9000',
            'dfs.nameservices': 'ns1',
            'fs.hdfs.impl.disable.cache': 'true',
            'fs.hdfs.impl': 'org.apache.hadoop.hdfs.DistributedFileSystem'
        }
    }, {
        'componentName': 'DTYARNSHELL',
        'componentId': 1,
        'componentTypeCode': 3,
        'config': {
            'dfs.ha.namenodes.ns1': 'nn1,nn2',
            'fs.defaultFS': 'hdfs://ns1',
            'dfs.client.failover.proxy.provider.ns1': 'org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider',
            'dfs.namenode.rpc-address.ns1.nn2': 'kudu2:9000',
            'dfs.namenode.rpc-address.ns1.nn1': 'kudu1:9000',
            'dfs.nameservices': 'ns1',
            'fs.hdfs.impl.disable.cache': 'true',
            'fs.hdfs.impl': 'org.apache.hadoop.hdfs.DistributedFileSystem'
        }
    }],
    libraComponentList: [{
        'componentName': 'LIBRASAL',
        'componentId': 1,
        'componentTypeCode': 8,
        'config': {
            'dfs.ha.namenodes.ns1': 'nn1,nn2',
            'fs.defaultFS': 'hdfs://ns1',
            'dfs.client.failover.proxy.provider.ns1': 'org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider',
            'dfs.namenode.rpc-address.ns1.nn2': 'kudu2:9000',
            'dfs.namenode.rpc-address.ns1.nn1': 'kudu1:9000',
            'dfs.nameservices': 'ns1',
            'fs.hdfs.impl.disable.cache': 'true',
            'fs.hdfs.impl': 'org.apache.hadoop.hdfs.DistributedFileSystem'
        }
    }]
}
export default function (state = defaultState, action) {
    switch (action.type) {
        case userActions.SET_TENANT_LIST: {
            const list = action.data;
            const newState = cloneDeep(state)
            newState.tenantList = list;
            return newState
        }
        case clusterActions.UPDATE_ENGINE_LIST: {
            const list = action.data;
            const newState = cloneDeep(state);
            newState.engineList = list;
            return newState
        }
        case clusterActions.UPDATE_HADOOP_COMPONENT_LIST: {
            const list = action.data;
            const newState = cloneDeep(state);
            newState.hadoopComponentList = list;
            return newState
        }
        case clusterActions.UPDATE_LIBRA_COMPONENT_LIST: {
            const list = action.data;
            const newState = cloneDeep(state);
            newState.libraComponentList = list;
            return newState
        }
        default:
            return state
    }
}
