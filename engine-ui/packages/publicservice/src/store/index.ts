import { createStore, combineReducers } from 'redux';
// import thunk from 'redux-thunk';
// import { composeWithDevTools } from 'redux-devtools-extension';
// import { API, URL } from '@/services';
import appReducer from '@/pages/global';

//TODO: apply middlwares in redux
// const middlewares = [thunk.withExtraArgument({ API, URL })];

const store = createStore(
  combineReducers({ ...appReducer })
  // process.env.NODE_ENV == 'production'
  //   ? applyMiddleware(...middlewares)
  //   : composeWithDevTools(applyMiddleware(...middlewares))
);
export default store;
