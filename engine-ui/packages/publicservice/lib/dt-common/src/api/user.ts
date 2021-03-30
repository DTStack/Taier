import { Cookie, LocalDB } from 'dt-utils';

import http from './http';
import req from '../consts/reqUrls';

import { User } from '../model';
import { MY_APPS } from '../consts';

declare var APP_CONF: any;

// eslint-disable-next-line
const UIC_URL_TARGET = APP_CONF.UIC_URL || '';
// eslint-disable-next-line
const UIC_DOMAIN_URL = APP_CONF.UIC_DOMAIN || '';

export default {
  // ========== User ========== //
  logout(appKey?: string) {
    // 注销退出
    let logoutUrl = req.LOGOUT;
    if (appKey == MY_APPS.API) {
      logoutUrl = req.API_LOGOUT;
    }
    http.post(logoutUrl).then((res) => {
      this.openLogin();
    });
  },

  openLogin() {
    LocalDB.clear();
    Cookie.deleteCookie('dt_user_id', UIC_DOMAIN_URL, '/');
    Cookie.deleteCookie('dt_token', UIC_DOMAIN_URL, '/');
    Cookie.deleteCookie('dt_tenant_id', UIC_DOMAIN_URL, '/');
    Cookie.deleteCookie('dt_tenant_name', UIC_DOMAIN_URL, '/');
    Cookie.deleteCookie('dt_username', UIC_DOMAIN_URL, '/');
    Cookie.deleteCookie('dt_is_tenant_admin', UIC_DOMAIN_URL, '/');
    Cookie.deleteCookie('dt_is_tenant_creator', UIC_DOMAIN_URL, '/');
    Cookie.deleteAllCookies(null, '/');
    window.location.href = `${UIC_URL_TARGET}`;
  },

  getInitUser() {
    return this.getLoginedUser();
  },

  getLoginedUser(): User {
    const user: User = {};
    user.userName = Cookie.getCookie('dt_username');
    user.dtuicUserId = Cookie.getCookie('dt_user_id');
    user.isTenantAdmin = Cookie.getCookie('dt_is_tenant_admin') === 'true';
    user.isTenantCreator = Cookie.getCookie('dt_is_tenant_creator') === 'true';
    return user;
  },
};
