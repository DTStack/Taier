import mc from 'mirror-creator';

const componentActionType = mc([
    'SAVE_GRAPH',
    'SAVE_SELECTED_CELL'
], { prefix: 'component/' })

export default componentActionType;
