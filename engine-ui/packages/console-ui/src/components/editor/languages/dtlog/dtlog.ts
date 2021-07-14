/* ---------------------------------------------------------------------------------------------
*  Copyright (c) Microsoft Corporation. All rights reserved.
*  Licensed under the MIT License. See License.txt in the project root for license information.
*-------------------------------------------------------------------------------------------- */
'use strict';
export var conf: any = {

};
export var language: any = {
    defaultToken: '',
    ignoreCase: true,
    tokenPostfix: '.dtlog',
    tokenizer: {
        root: [
            [/[=]{5,}.*[=]{5,}/, 'comment']
        ]
    }
};
