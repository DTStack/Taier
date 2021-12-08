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

import { registerMicroApps, setDefaultMountApp, start } from 'qiankun';

export const AppContainer = 'AppContainer';
const container = `#${AppContainer}`;

let ENTRY_CONSOLE = '//local.dtstack.cn:8082/console/';
let ENTRY_OPERATION = '//local.dtstack.cn:8082/console/';
let ENTRY_DATABASE = '//local.dtstack.cn:8083/datasource/';

// For Production
if (process.env.NODE_ENV === 'production') {
    ENTRY_CONSOLE = '/console/';
    ENTRY_OPERATION = '/console/';
    ENTRY_DATABASE = '/datasource/';
}

registerMicroApps([
    {
        name: 'Operation',
        entry: ENTRY_OPERATION,
        container: container,
        activeRule: '#/operation-ui',
    },
    {
        name: 'DTConsoleApp',
        entry: ENTRY_CONSOLE,
        container: container,
        activeRule: '#/console-ui',
    },
    {
        name: 'Datasource',
        entry: ENTRY_DATABASE,
        container: container,
        activeRule: '#/data-source',
    },
]);

start({
    /**
     * TODO: Style renaming isolation has a BUG, removed for now
     */
    // sandbox: {
    // experimentalStyleIsolation: true,
    // },
});

setDefaultMountApp('/');
