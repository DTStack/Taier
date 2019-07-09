import { combineReducers } from 'redux';

import { sourceTypes } from './sourceTypes';
import { dataSourceList } from './dataSourceList';

export const dataSource = combineReducers({
    sourceTypes,
    dataSourceList
});
