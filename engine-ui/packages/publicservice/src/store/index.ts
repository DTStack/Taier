
import { createStore, applyMiddleware, combineReducers } from 'redux'
import { routerReducer, routerMiddleware } from 'react-router-redux'

import thunk from 'redux-thunk'
import { composeWithDevTools } from 'redux-devtools-extension'
import {history} from 'utils/index'
import { API,URL} from "@/services";
// import appReducer from './global';
import global from './global';


const appReducer = {
  global,
};



const middlewares:any = [thunk.withExtraArgument({API,URL}), routerMiddleware(history)];

const store = createStore(
  combineReducers({ routing: routerReducer, ...appReducer }),
  process.env.NODE_ENV=='production' ? applyMiddleware(...middlewares): composeWithDevTools(applyMiddleware(...middlewares))
)
export default  store;
