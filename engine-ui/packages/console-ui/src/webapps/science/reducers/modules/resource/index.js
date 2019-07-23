
import { combineReducers } from 'redux';
import files from './files';
import expandedKeys from './expandedKeys';

export default combineReducers({
    files,
    expandedKeys
});
