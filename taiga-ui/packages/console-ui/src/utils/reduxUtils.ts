/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { browserHistory, hashHistory } from 'react-router'
import { createLogger } from 'redux-logger'
import thunkMiddleware from 'redux-thunk'
import { createStore, applyMiddleware, compose } from 'redux'
import { syncHistoryWithStore } from 'react-router-redux'

declare var window: any;
declare var APP: any;

function configureStoreDev (rootReducer: any) {
    const store = createStore(
        rootReducer,
        compose(
            applyMiddleware(thunkMiddleware, createLogger()),
            window.devToolsExtension ? window.devToolsExtension() : (fn: any) => fn
        )
    )
    return store;
}

function configureStoreProd (rootReducer: any) {
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
export function getStore (rootReducer: any, routeMode?: any) {
    const store = process.env.NODE_ENV === 'production'
        ? configureStoreProd(rootReducer) : configureStoreDev(rootReducer)
    const bhistory = !routeMode || routeMode !== 'hash' ? browserHistory : hashHistory
    const history = syncHistoryWithStore(bhistory, store);
    return {
        store,
        history
    }
}
