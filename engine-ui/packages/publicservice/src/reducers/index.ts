// Main Reducer
import { combineReducers } from 'redux';
import { routerReducer as routing } from 'react-router-redux';
import {
  apps,
  app,
  licenseApps,
} from 'lib/dt-common/src/reducers/modules/apps';
import { msgList } from 'lib/dt-common/src/reducers/modules/message';
import { user } from 'lib/dt-common/src/reducers/modules/user';

const rootReducer = combineReducers({
  routing,
  apps,
  app,
  licenseApps,
  msgList,
  user,
});

export default rootReducer;
