import { message as Message, notification } from 'antd';

Message.config({
  top: 80,
});

const message = {
  success: Message.success,
  error: (message) => {
    notification.error({
      duration: 5,
      message: '错误',
      description: message,
    })
  },
  msgError: Message.error,
}

export default message;
