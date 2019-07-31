import mc from 'mirror-creator';

export const taskFlowAction = mc([
    'SET_TASK_FLOW',
    'GET_TASK_FLOW',
    'SET_TASK_FLOW_TYPE'
], { prefix: 'operation/taskflow/' });

export const graphAction = mc([
    'UPDATE_GRAPH_STATUS'
], { prefix: 'operation/graph/' });
