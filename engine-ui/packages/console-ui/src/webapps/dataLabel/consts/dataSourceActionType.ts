import mc from 'mirror-creator';

export const dataSourceActionType = mc([
    'CHANGE_LOADING',
    'GET_DATA_SOURCES',
    'GET_DATA_SOURCES_TYPE',
    'GET_DATA_SOURCES_LIST',
    'GET_TAG_DATA_SOURCES_LIST',
    'GET_DATA_SOURCES_TABLE',
    'RESET_DATA_SOURCES_TABLE',
    'GET_DATA_SOURCES_COLUMN',
    'RESET_DATA_SOURCES_COLUMN',
    'GET_DATA_SOURCES_PREVIEW'
], { prefix: 'dataSource/' });
