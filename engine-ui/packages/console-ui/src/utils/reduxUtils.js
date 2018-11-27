import { browserHistory, hashHistory } from 'react-router'
import { createLogger } from 'redux-logger'
import thunkMiddleware from 'redux-thunk'
import { createStore, applyMiddleware, compose } from 'redux'
import { syncHistoryWithStore } from 'react-router-redux'

function configureStoreDev (rootReducer) {
    const store = createStore(
        rootReducer,
        compose(
            applyMiddleware(thunkMiddleware, createLogger()),
            window.devToolsExtension ? window.devToolsExtension() : fn => fn
        )
    )
    return store;
}

function configureStoreProd (rootReducer) {
    const stroe = createStore(
        rootReducer,
        applyMiddleware(thunkMiddleware)
    );
    return stroe;
}

/**
 *
 * @param { Object } rootReducer
 * @param { String } routeMode [hash, browser]
 */
export function getStore (rootReducer, routeMode) {
    const store = process.env.NODE_ENV === 'production'
        ? configureStoreProd(rootReducer) : configureStoreDev(rootReducer)
    const bhistory = !routeMode || routeMode !== 'hash' ? browserHistory : hashHistory
    const history = syncHistoryWithStore(bhistory, store);
    return {
        store,
        history
    }
}
