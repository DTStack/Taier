import React from 'react';
import { inputColumnsKeys } from '../../model/inputColumnModel';
import { PARAMS_POSITION, API_METHOD } from '../../consts';
export function generateUrlQuery (params = []) {
    let query = params.map((param) => {
        if (param[inputColumnsKeys.POSITION] != PARAMS_POSITION.QUERY) {
            return null;
        }
        let name = param[inputColumnsKeys.NAME];
        return name + `={${name}}`;
    }).filter(Boolean).join('&');
    return <pre>{'http(s)://调用URL' + (query ? ('?' + query) : '')}</pre>
}
export function generateHeader (params) {
    let base = 'API_TOKEN: {API_TOKEN}\n';
    let headers = params.map((param) => {
        if (param[inputColumnsKeys.POSITION] != PARAMS_POSITION.HEAD) {
            return null;
        }
        let name = param[inputColumnsKeys.NAME];
        return name + `: {${name}}`;
    }).filter(Boolean);
    base = base + headers.join('\n');
    return <pre>{base}</pre>;
}
export function generateBody (params, method) {
    if (method == API_METHOD.GET || method == API_METHOD.DELETE) {
        return '无'
    }
    let base = { inFields: {} };
    params.map((param) => {
        if (param[inputColumnsKeys.POSITION] != PARAMS_POSITION.BODY) {
            return null;
        }
        let type = param[inputColumnsKeys.TYPE];
        let name = param[inputColumnsKeys.NAME];
        if (/int/i.test(type)) {
            base.inFields[name] = 0;
        } else {
            base.inFields[name] = '';
        }
    })
    return <pre>{JSON.stringify(base, null, '   \r')}</pre>;
}
