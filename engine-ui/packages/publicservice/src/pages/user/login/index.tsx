import * as React from 'react'
import { Form, Input, Button,message } from 'antd';
import { LockOutlined,UserOutlined } from '@ant-design/icons';
import './style.scss';
import {API} from '@/services/index'
import token from 'utils/token';
const FormItem = Form.Item;
const xihubiaozhi =require('assets/imgs/xihubiaozhi.png');

interface IProps {
  history:any,
  form:any
}
interface IState{
  confirmDirty:boolean,
  autoCompleteResult:any
}
 class Login extends React.Component <IProps,IState>{
  constructor(IProps:any) {
    super(IProps);
  
  }
  componentDidMount() {
  }
  componentWillReceiveProps(nextProps) {
  }
  shouldComponentUpdate(nextProps, nextState) {
    return this.props != nextProps || this.state != nextState;
  }
  handleSubmit = (e) => {
    e.preventDefault();
    this.props.form.validateFields((err, values) => {
      if (!err) {
        let flag:boolean=  (values.userName||'').length
                 &&(values.password||'').length;
          if(flag){
            API.login(values)
              .then(({ success, content }) => {
                if(success) {
                  // window.localStorage.setItem('token', content.token);
                  token.set(content.token);
                  this.props.history.push('/');
                } else {
                  message.error(content);
                }
              })
          }else{
            message.warning("账号密码输入有误，请重新输入")
          }
        console.log('Received values of form: ', values,this.props);
      
      }
    });
  }
  render() {
    const { getFieldDecorator } = this.props.form;
    return (
      <div style={{ minHeight: "1200px"}} className="login-bg">
     
      <Form className="login-form">
        <FormItem>
          <div  className="login-title">
            <img src={xihubiaozhi}></img>
            <div>西湖景区中枢</div>
          </div>
        </FormItem>
        <FormItem>
          {getFieldDecorator('userName', {
            rules: [{ required: true, message: '请输入登陆账号!' }],
          })(
            <Input size='large'  prefix={<UserOutlined  style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder="请输入登录账号" />
          )}
        </FormItem>
        <FormItem>
          {getFieldDecorator('password', {
            rules: [{ required: true, message: '请输入登陆密码!' }],
          })(
            <Input size='large'  prefix={<LockOutlined  style={{ color: 'rgba(0,0,0,.25)' }} />} type="password" placeholder="请输入登录密码" />
          )}
        </FormItem>
        <FormItem style={{marginTop:"45px"}}>
          <Button type="primary" size='large' htmlType="submit" className="login-form-button">登录</Button>
        </FormItem>
      </Form>
      </div>
    );
  }
}
export default  Login;
