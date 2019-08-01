// configureStore dev
import { createStore, applyMiddleware, compose } from 'redux'
import thunkMiddleware from 'redux-thunk'
import { createLogger } from 'redux-logger'

import rootReducer from './reducers'

const nextRootReducer = require('./reducers').default
declare var window: any;
/* eslint-disable */
export default function configureStore(initialState: any) {
    const store = createStore(
        rootReducer,
        initialState,
        compose(
            applyMiddleware(thunkMiddleware, createLogger()),
            window.devToolsExtension ? window.devToolsExtension() : (fn: any) => fn
        ),
    )
    if ((module as any).hot) {
        (module as any).hot.accept('./reducers', () => {
            store.replaceReducer(nextRootReducer);
        })
    }
    return store;
}
/* eslint-disable */
