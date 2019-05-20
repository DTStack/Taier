import utils from 'utils'
import localDb from 'utils/localDb'
import http from './http';
import req from '../consts/reqUrls'

/* eslint-disable */
const UIC_URL_TARGET = APP_CONF.UIC_URL || ''
const UIC_DOMAIN_URL = APP_CONF.UIC_DOMAIN || ''

export default {
    openLogin() {
        localDb.clear()
        utils.deleteCookie('dt_user_id', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_token', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_tenant_id', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_tenant_name', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_username', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_is_tenant_admin', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('dt_is_tenant_creator', UIC_DOMAIN_URL, '/')
        utils.deleteCookie('project_id', UIC_DOMAIN_URL, '/')
        window.location.href = `${UIC_URL_TARGET}`
    },
    getLoginedUser() {
        return http.post(req.GET_USER_BY_ID)
    },
}