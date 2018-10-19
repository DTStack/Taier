import utils from 'utils'
import http from './http'
import localDb from 'utils/localDb'
import req from '../consts/reqUrls'

/* eslint-disable */
const UIC_URL_TARGET = APP_CONF.UIC_URL || ''
const UIC_DOMAIN_URL = APP_CONF.UIC_DOMAIN || ''

export default {
    // ========== User ========== //
    logout() { // 注销退出
        http.post(req.LOGOUT).then(res => {
            localDb.clear()
            utils.deleteCookie('dt_user_id', UIC_DOMAIN_URL, '/')
            utils.deleteCookie('dt_token', UIC_DOMAIN_URL, '/')
            utils.deleteCookie('dt_tenant_id', UIC_DOMAIN_URL, '/')
            utils.deleteCookie('dt_tenant_name', UIC_DOMAIN_URL, '/')
            utils.deleteCookie('dt_username', UIC_DOMAIN_URL, '/')
            utils.deleteCookie('dt_is_tenant_admin', UIC_DOMAIN_URL, '/')
            utils.deleteCookie('dt_is_tenant_creator', UIC_DOMAIN_URL, '/')
            utils.deleteCookie('project_id', UIC_DOMAIN_URL, '/')
            window.location.href = `${UIC_URL_TARGET}/#/login`
        })
    },

    getInitUser() {
        return this.getLoginedUser();
    },

    getLoginedUser() {
        var user = {};
        user.userName = decodeURI(utils.getCookie('dt_username'));
        user.dtuicUserId = utils.getCookie('dt_user_id');
        user.isTenantAdmin = utils.getCookie('dt_is_tenant_admin') === 'true';
        user.isTenantCreator = utils.getCookie('dt_is_tenant_creator') === 'true';
        return user
    },

}