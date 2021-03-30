import { message } from 'antd';

import UserApi from './api/user';

export function authBeforeFormate(response: any) {
  switch (response.status) {
    case 200:
      return response;
    case 302:
      message.info('登录超时, 请重新登录！');
      return Promise.reject(response);
    default:
      if (process.env.NODE_ENV !== 'production') {
        console.error('Request error: ', response.code, response.message);
      }
      return response;
  }
}

export function authAfterFormatted(response: any, extOptions: any = {}) {
  switch (response.code) {
    case 1:
      return response;
    case 0: // 无权限，需要登录
      UserApi.logout();
      return response;
    case 16: // 项目不存在，需要重新进入Web首页选择项目，并进入
      // hashHistory.push('/');
      return Promise.reject(response);
    default:
      if (response.message && !extOptions.isSilent && !response.success) {
        message.error(response.message, 3); // 异常消息默认显示5s
      }
      return response;
  }
}
