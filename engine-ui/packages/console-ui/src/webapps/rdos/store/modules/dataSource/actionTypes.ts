import mc from 'mirror-creator';

export const dataSourceAction = mc([
    'GET_DATA_SOURCE_TYPES'
], { prefix: 'dataSource/sourceTypes/' });

export const dataSourceListAction = mc([
    'LOAD_DATASOURCE'
], { prefix: 'dataSource/dataSourceList/' });
