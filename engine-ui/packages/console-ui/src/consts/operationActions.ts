import mc from 'mirror-creator';

export const operationActions = mc([
    'GET_PROJECT_LIST',
    'GET_PERSON_LIST'
], { prefix: 'operation/' });
