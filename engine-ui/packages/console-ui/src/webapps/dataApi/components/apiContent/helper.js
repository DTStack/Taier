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
    let base = [
        'X_Auth_Key: {APP Key}',
        'X_Auth_ActionId: {API Id}',
        'X_Auth_Signature: {生成的签名}',
        'X_Auth_Timestamp: {时间戳}\n'
    ].join('\n')
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
    let base = {};
    params.map((param) => {
        if (param[inputColumnsKeys.POSITION] != PARAMS_POSITION.BODY) {
            return null;
        }
        let type = param[inputColumnsKeys.TYPE];
        let name = param[inputColumnsKeys.NAME];
        if (/int/i.test(type)) {
            base[name] = 0;
        } else {
            base[name] = '';
        }
    })
    return <pre>{JSON.stringify(base, null, '   \r')}</pre>;
}
