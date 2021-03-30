import { assign, cloneDeep } from 'lodash';
import appActions from '../../consts/appActions';

export function apps(state: any[] = [], action: any) {
  switch (action.type) {
    case appActions.GET_DEFAULT_APPS: {
      let nextState = [];
      if (action.data != null) {
        nextState = action.data.slice();
      }
      return nextState;
    }
    default:
      return state;
  }
}

export function licenseApps(state: any = [], action: any) {
  switch (action.type) {
    case appActions.GET_LICENSE_APP: {
      if (action.data != null) {
        const nextState = cloneDeep(action.data);
        nextState.splice(0, 1);
        return nextState;
      }
      return state;
    }
    default:
      return state;
  }
}

export function app(state = {}, action: any) {
  switch (action.type) {
    case appActions.UPDATE_APP: {
      if (action.data !== null) {
        return assign({}, state, action.data);
      }

      return state;
    }
    default:
      return state;
  }
}

export function isLicenseLoaded(state = false, action: any) {
  switch (action.type) {
    case appActions.SET_LICENSE_LOADED: {
      return true;
    }
    default:
      return state;
  }
}
