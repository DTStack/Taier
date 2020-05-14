
import {globalType} from './actionType';

const initialState = {
  userData: {},
  navData:[]
};
 const globalReducer = (state = initialState, action) => {
  const { type, payload } = action;
  console.log();
  switch (type) {
    case globalType.GET_USER_DATA:
      return Object.assign({}, state, {
        userData: payload,
      });
    case globalType.GET_NAV_DATA:
      return Object.assign({}, state, {
        navData: payload,
      });
    default:
      return state;
  }
};
export default globalReducer;
