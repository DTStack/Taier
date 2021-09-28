import { globalType } from './actionType';

const initialState = {
  userData: {},
  navData: [],
};
const globalReducer = (state = initialState, action) => {
  const { type, payload } = action;
  switch (type) {
    case globalType.GET_USER_DATA:
      return { ...state, userData: payload };
    case globalType.GET_NAV_DATA:
      return { ...state, navData: payload };
    default:
      return state;
  }
};
export default globalReducer;
