import molecule from 'molecule/esm'
import { TASK_RUN_ID, TASK_STOP_ID } from './const'
import { browserHistory, hashHistory } from 'react-router'
import { createLogger } from 'redux-logger'
import thunkMiddleware from 'redux-thunk'
import { createStore, applyMiddleware, compose } from 'redux'
import { syncHistoryWithStore } from 'react-router-redux'

declare let window: any

function configureStoreDev (rootReducer: any) {
    const store = createStore(
        rootReducer,
        compose(
            applyMiddleware(thunkMiddleware, createLogger()),
            window.devToolsExtension
                ? window.devToolsExtension()
                : (fn: any) => fn
        )
    )
    return store
}

function configureStoreProd (rootReducer: any) {
    const stroe = createStore(rootReducer, applyMiddleware(thunkMiddleware))
    return stroe
}

/**
 *
 * @param { Object } rootReducer
 * @param { String } routeMode [hash, browser]
 */
export function getStore (rootReducer: any, routeMode?: any) {
    const store =
        process.env.NODE_ENV === 'production'
            ? configureStoreProd(rootReducer)
            : configureStoreDev(rootReducer)
    const bhistory =
        !routeMode || routeMode !== 'hash' ? browserHistory : hashHistory
    const history = syncHistoryWithStore(bhistory, store)
    return {
        store,
        history
    }
}

export function resetEditorGroup () {
    molecule.editor.updateActions([
        { id: TASK_RUN_ID, disabled: true },
        { id: TASK_STOP_ID, disabled: true }
    ])
}
