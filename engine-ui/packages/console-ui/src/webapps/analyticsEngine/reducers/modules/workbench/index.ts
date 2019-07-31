import { combineReducers } from 'redux';

import mainBench from './mainBench';
import folderTree from './folderTree';

const workbenchReducer = combineReducers({
    mainBench,
    folderTree
})

export default workbenchReducer;
