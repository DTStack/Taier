/* ---------------------------------------------------------------------------------------------
*  Copyright (c) Microsoft Corporation. All rights reserved.
*  Licensed under the MIT License. See License.txt in the project root for license information.
*-------------------------------------------------------------------------------------------- */
'use strict';
import { registerLanguage } from 'monaco-editor/esm/vs/basic-languages/_.contribution';
import { registeCompleteItemsProvider, disposeProvider } from './simpleComplete';

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
export { registeCompleteItemsProvider, disposeProvider };
