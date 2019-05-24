import mc from 'mirror-creator';

export const userActions = mc([
    'SET_USER_LIST'
], { prefix: 'console/user' });

export const clusterActions = mc([
    'UPDATE_ENGINE_LIST'
], { prefix: 'console/cluster' })
