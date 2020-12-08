import { globalType } from './actionType';

const userData = data => ({
  type: globalType.GET_USER_DATA,
  payload: data,
});
export const getUserData = params => async (dispatch, getState, { API }) => {
  try {
    const { meta, data } = await API.getUserData(params);
    if (meta && meta.success) {
      dispatch(userData(data));
    }
  } catch (ex) {
    console.warn(ex);
  }
};

const navData = data => ({
  type: globalType.GET_NAV_DATA,
  payload: data,
});
export const getNavData = params => async (dispatch, getState, { API }) => {
  try {
    const { meta, data } = await API.getUserData(params);
    if (meta && meta.success) {
      dispatch(navData(data));
    }
  } catch (ex) {
    console.warn(ex);
  }
};
