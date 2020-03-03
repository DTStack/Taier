import http from './http';
import req from '../consts/reqUrls';
import { numOrStr } from 'typing';

export interface IAccount {
    name: string;
    username: string;
    password: string;
    bindUserId: string;
    bindTenantId: string;
    email: string;
}

export default {

    getUnbindAccounts (params: { dtuicTenantId: numOrStr }) {
        return http.post(req.ACCOUNT_UNBIND_LIST, params)
    },

    bindAccount (params: IAccount) {
        return http.post(req.ACCOUNT_BIND, params)
    },

    getBindAccounts (params: { dtuicTenantId: numOrStr; username: string }) {
        return http.post(req.ACCOUNT_BIND_LIST, params)
    },

    unbindAccount (params: { id: numOrStr; name: string; password: string }) {
        return http.post(req.ACCOUNT_UNBIND, params)
    }
}
