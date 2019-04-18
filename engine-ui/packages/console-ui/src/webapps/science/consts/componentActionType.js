import mc from 'mirror-creator';

const componentActionType = mc([
    'GET_TASK_DATA',
    'SAVE_GRAPH',
    'SAVE_SELECTED_CELL',
    'ADD_JOBID',
    'CHANGE_TASK_STATUS'
], { prefix: 'component/' })

export default componentActionType;
