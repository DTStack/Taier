import * as React from 'react'
import { Form, Input, Button } from 'antd';
import { LockOutlined,UserOutlined } from '@ant-design/icons';
import './style.scss';
const FormItem = Form.Item;

interface IProps {
  history:any,
  form:any
}

const Login = (props: IProps) => {

  const [form] = Form.useForm();

  return <div style={{ minHeight: "1200px"}} className="login-bg">
     
  <Form form={form} className="login-form">
    <FormItem>
      <div className="login-title">
        <div>西湖景区中枢</div>
      </div>
    </FormItem>
    <FormItem  name="userName" rules={[{ required: true, message: '请输入登陆账号!' }]}>
      <Input size='large'  prefix={<UserOutlined  style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder="请输入登录账号" />
    </FormItem>
    <FormItem name="password" rules={[{ required: true, message: '请输入登陆密码!' }]}>
      <Input size='large'  prefix={<LockOutlined  style={{ color: 'rgba(0,0,0,.25)' }} />} type="password" placeholder="请输入登录密码" />
    </FormItem>
    <FormItem style={{marginTop:"45px"}}>
      <Button type="primary" size='large' htmlType="submit" className="login-form-button">登录</Button>
    </FormItem>
  </Form>
  </div>
}

export default  Login;
