import { notification } from 'antd';

const Notification = {
  error: (message) => {
    notification.error({
      message: '错误',
      description: message,
      duration: 5,
    })
  }
}

export default Notification;
