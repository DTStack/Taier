import React, { useState, useCallback } from "react";
import { NavLink } from "react-router-dom";
import "./style.scss";
import {
  Form,
  Input,
  Checkbox,
  Button,
} from "antd";
const FormItem = Form.Item;

interface IProps {
  history:any,
  form:any
}

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 8 }
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 16 }
  }
};

const tailFormItemLayout = {
  wrapperCol: {
    xs: {
      span: 24,
      offset: 0
    },
    sm: {
      span: 16,
      offset: 8
    }
  }
};

const Register = (props: IProps) => {

  const [form] = Form.useForm();
  const [confirmDirty, setConfirmDirty] = useState(false);

  const validateToNextPassword = useCallback((rule, value, callback) => {
    if (value && this.state.confirmDirty) {
      form.validateFields(["confirm"]);
    }
    callback();
  }, [form])

  const handleConfirmBlur = useCallback(e => {
    const value = e.target.value;
    setConfirmDirty(confirmDirty || !!value );
  }, [setConfirmDirty])

  const compareToFirstPassword = useCallback((rule, value, callback) => {
    if (value && value !== form.getFieldValue("password")) {
      callback("Two passwords that you enter is inconsistent!");
    } else {
      callback();
    }
  }, [form]);

  return <div style={{ width: '100vw', height: '100vh' }} className="login-bg">
  <Form form={form} className="register-form" style={{ marginLeft: '50%', transform: 'translateX(-50%)', padding: '20px' }}>
    <FormItem {...formItemLayout} label="用户邮箱" name="email" rules={[{
        type: "email",
        message: "The input is not valid E-mail!"
      },
      {
        required: true,
        message: "Please input your E-mail!"
      }
    ]}>
      <Input />
    </FormItem>
    <FormItem {...formItemLayout} label="注册账号：" name="nickname" rules={[
      {
        required: true,
        message: "Please input your nickname!",
        whitespace: true
      }
    ]}>
     <Input />
    </FormItem>
    <FormItem {...formItemLayout} label="手机号码：" name="phone" rules={[
          { required: true, message: "Please input your phone number!" }
        ]}>
      <Input  style={{ width: "100%" }} />
    </FormItem>
    <FormItem {...formItemLayout} label="输入密码：" name="password" rules={[
          {
            required: true,
            message: "Please input your password!"
          },
          {
            validator: validateToNextPassword
          }
        ]}>
      <Input type="password" />
    </FormItem>
    <FormItem {...formItemLayout} label="确认密码：" name="confirm" rules={[
      {
        required: true,
        message: "Please confirm your password!"
      },
    {
        validator: compareToFirstPassword
      }
    ]}>
      <Input type="password" onBlur={handleConfirmBlur} />
    </FormItem>
    <FormItem {...tailFormItemLayout} name="agreement">
      <Checkbox>
        我已经阅读 <a href="">协议</a>
      </Checkbox>
    </FormItem>
    <FormItem {...tailFormItemLayout}>
      <Button type="primary" htmlType="submit"> <NavLink to="/index">注 册</NavLink></Button>
    </FormItem>
  </Form>
  </div>
}

export default Register;
