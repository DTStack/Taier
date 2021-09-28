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
    engineType: string | number;
}

export default {

    getUnbindAccounts (params: { dtuicTenantId: numOrStr; engineType: numOrStr }) {
        return http.post(req.ACCOUNT_UNBIND_LIST, params)
    },

    bindAccount (params: IAccount) {
        return http.postWithDefaultHeader(req.ACCOUNT_BIND, params)
    },

    ldapBindAccount (params: {
        accountList: any[];
    }) {
        return http.postWithDefaultHeader(req.LDAP_ACCOUNT_BIND, params)
    },

    updateBindAccount (params: IAccount) {
        return http.postWithDefaultHeader(req.UPDATE_ACCOUNT_BIND, params)
    },

    getBindAccounts (params: { dtuicTenantId: numOrStr; username: string; engineType: numOrStr }) {
        return http.post(req.ACCOUNT_BIND_LIST, params)
    },

    unbindAccount (params: { id: numOrStr; name: string; password: string }) {
        return http.postWithDefaultHeader(req.ACCOUNT_UNBIND, params)
    },

    // 获取UIC租户
    getFullTenants (value?: any) {
        const getTenantsUrl = `${req.GET_FULL_TENANT + (value || '')}`
        return http.get(getTenantsUrl);
    },

    getTenantsList () {
        return http.post(req.GET_TENANTS_LIST);
    }
}
