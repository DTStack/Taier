import appActions from '../consts/appActions';
import Api from '../api';
export function updateApp(fields: any) {
  return {
    type: appActions.UPDATE_APP,
    data: fields,
  };
}
// licenseAction
export function getLicenseApp() {
  return (dispatch: any, getStore: any) => {
    const store = getStore();
    if (store.licenseApps.length === 0) {
      Api.getLicenseApp().then((res) => {
        if (res.success) {
          return dispatch({
            type: appActions.GET_LICENSE_APP,
            data: [...res.data],
          });
        }
        dispatch({
          type: appActions.SET_LICENSE_LOADED,
        });
      });
    }
  };
}

// getDefaultApps
export function getDefaultApps() {
  return (dispatch: any, getStore: any) => {
    const store = getStore();
    if (store.apps.length === 0) {
      Api.getDefaultApps().then((data) => {
        return dispatch({
          type: appActions.GET_DEFAULT_APPS,
          data: data || [],
        });
      });
    }
  };
}
