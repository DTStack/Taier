// Main Reducer
import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux';
import {
  apps,
  app,
  licenseApps,
} from 'dt-common/src/reducers/modules/apps';
import { msgList } from 'dt-common/src/reducers/modules/message';
import { user } from 'dt-common/src/reducers/modules/user';

const rootReducer = combineReducers({
  routing,
  apps,
  app,
  licenseApps,
  msgList,
  user,
});

export default rootReducer;
