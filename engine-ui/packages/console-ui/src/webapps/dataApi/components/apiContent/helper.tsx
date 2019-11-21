import * as React from 'react';
import { inputColumnsKeys } from '../../model/inputColumnModel';
import { PARAMS_POSITION, API_METHOD } from '../../consts';
export function generateUrlQuery (params: any[] = []) {
    let query = params.map((param: any) => {
        if (param[inputColumnsKeys.POSITION] != PARAMS_POSITION.QUERY) {
            return null;
        }
        let name = param[inputColumnsKeys.NAME];
        return name + `={${name}}`;
    }).filter(Boolean).join('&');
    return <pre>{'http(s)://调用URL' + (query ? ('?' + query) : '')}</pre>
}
export function generateHeader (params: any) {
    let base: any = [
        'X-Auth-Key: {APP Key}',
        'X-Auth-ActionId: {API Id}',
        'X-Auth-Signature: {生成的签名}',
        'X-Auth-Timestamp: {时间戳}\n'
    ].join('\n')
    let headers = params.map((param: any) => {
        if (param[inputColumnsKeys.POSITION] != PARAMS_POSITION.HEAD) {
            return null;
        }
        let name = param[inputColumnsKeys.NAME];
        return name + `: {${name}}`;
    }).filter(Boolean);
    base = base + headers.join('\n');
    return <pre>{base}</pre>;
}
export function generateTokenHeader (params: any) {
    let base: any = [
        'API-TOKEN： {API-TOKEN}'
    ].join('\n')
    let headers = params.map((param: any) => {
        if (param[inputColumnsKeys.POSITION] != PARAMS_POSITION.HEAD) {
            return null;
        }
        let name = param[inputColumnsKeys.NAME];
        return name + `: {${name}}`;
    }).filter(Boolean);
    base = base + headers.join('\n');
    return <pre>{base}</pre>;
}
export function generateBody (params: any, method: any) {
    if (method == API_METHOD.GET || method == API_METHOD.DELETE) {
        return '无'
    }
    let base: any = {};
    params.map((param: any): any => {
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
