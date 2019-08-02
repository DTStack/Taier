// configureStore dev
import { createStore, applyMiddleware, compose } from 'redux'
import thunkMiddleware from 'redux-thunk'
import { createLogger } from 'redux-logger'

import rootReducer from './reducers'

const nextRootReducer = require('./reducers').default

/* eslint-disable */
export default function configureStore(initialState: any) {
    const store = createStore(
        rootReducer,
        initialState,
        compose(
            applyMiddleware(thunkMiddleware, createLogger()),
            (window as any).devToolsExtension ? (window as any).devToolsExtension() : (fn: any) => fn
        ),
    )
    if ((module as any).hot) {
        (module as any).hot.accept('./reducers', () => {
            store.replaceReducer(nextRootReducer);
        })
    }
    return store;
}
