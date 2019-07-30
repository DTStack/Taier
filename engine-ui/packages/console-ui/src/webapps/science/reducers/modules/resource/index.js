
import { combineReducers } from 'redux';
import files from './files';
import expandedKeys from './expandedKeys';
import isShowFixResource from './resourceExt';
export default combineReducers({
    files,
    expandedKeys,
    isShowFixResource
});
