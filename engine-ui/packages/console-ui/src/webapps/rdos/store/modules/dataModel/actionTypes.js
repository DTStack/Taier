import mc from 'mirror-creator';

export const dataModelActions = mc([
    'GET_THEME_FIELDS',
    'GET_MODEL_LEVELS',
    'GET_INCREMENT_COUNTS',
    'GET_FRESH_FREQUENCIES',
], { prefix: 'dataModel/' });
