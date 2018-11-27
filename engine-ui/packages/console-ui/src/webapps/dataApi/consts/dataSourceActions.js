import mc from 'mirror-creator';

export const dataSourceActions = mc([
    'CHANGE_LOADING',
    'GET_DATA_SOURCES',
    'GET_DATA_SOURCES_TYPE',
    'GET_DATA_SOURCES_LIST',
    'GET_DATA_SOURCES_TABLE',
    'GET_DATA_SOURCES_COLUMN',
    'GET_DATA_SOURCES_PART',
    'RESET_DATA_SOURCES_PART',
    'GET_DATA_SOURCES_PREVIEW'
], { prefix: 'dataSource/' });
