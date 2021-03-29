import React from 'react';
import { notification } from 'antd';
import { NotificationApi } from 'antd/lib/notification';

export function initNotification() {
  notification.config({
    duration: 1000,
  });
  const changeArr = ['error', 'success'];
  const iconMap: any = {
    error: <img src="public/assets/imgs/notification-error.svg" />,
    success: <img src="public/assets/imgs/notification-success.svg" />,
  };
  changeArr.forEach((key: keyof NotificationApi) => {
    const oldFunc: any = notification[key];
    notification[key] = function (config: any = {}) {
      const notifyMsgs = document.querySelectorAll(
        '.ant-notification-notice-description'
      );
      config = {
        ...config,
        icon: iconMap[key],
        className: 'dt-notification',
        message: (
          <span>
            {config.message}
            {notifyMsgs.length ? null : (
              <a
                onClick={() => {
                  notification.destroy();
                }}
                className="dt-notification__close-btn">
                全部关闭
              </a>
            )}
          </span>
        ),
      };
      oldFunc.apply(notification, [config]);
    };
  });
}
