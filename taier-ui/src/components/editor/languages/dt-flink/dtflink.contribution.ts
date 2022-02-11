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

/* ---------------------------------------------------------------------------------------------
*  Copyright (c) Microsoft Corporation. All rights reserved.
*  Licensed under the MIT License. See License.txt in the project root for license information.
*-------------------------------------------------------------------------------------------- */
'use strict';
import { registerLanguage } from 'monaco-editor/esm/vs/basic-languages/_.contribution';
import { registeCompleteItemsProvider, disposeProvider, onChange } from './simpleComplete';

declare var monaco: any;
declare var self: any;

// Allow for running under nodejs/requirejs in tests
var _monaco = typeof monaco === 'undefined' ? self.monaco : monaco;
registerLanguage({
    id: 'dtflink',
    extensions: ['.dtflink', '.dtflink'],
    aliases: ['Dtflink', 'dtflink'],
    loader: function () { return _monaco.Promise.wrap(import('./dtflink')); }
});
export { registeCompleteItemsProvider, disposeProvider, onChange };
