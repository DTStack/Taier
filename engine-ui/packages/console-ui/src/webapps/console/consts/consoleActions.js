import mc from 'mirror-creator';

export const userActions = mc([
    'SET_TENANT_LIST'
], { prefix: 'console/user' });

export const clusterActions = mc([
    'UPDATE_ENGINE_LIST',
    'UPDATE_HADOOP_COMPONENT_LIST',
    'UPDATE_LIBRA_COMPONENT_LIST'
], { prefix: 'console/cluster' })
