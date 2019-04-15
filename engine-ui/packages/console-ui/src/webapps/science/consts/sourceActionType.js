import mc from 'mirror-creator';

const sourceAction = mc([
    'SET_TABLE_LIST',
    'UPDATE',
    'RESET'
], { prefix: 'source/' })

export default sourceAction;
