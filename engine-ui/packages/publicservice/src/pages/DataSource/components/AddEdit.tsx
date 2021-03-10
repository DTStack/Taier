/*
 * @Author: 云乐
 * @Date: 2021-03-10 19:03:06
 * @LastEditTime: 2021-03-10 20:29:39
 * @LastEditors: 云乐
 * @Description: 新增和编辑数据源信息
 */
import React from "react";
import { Form, Input, Button,Upload,Icon,message } from "antd";
import "../List/style.less";

const formItemLayout = {
  labelCol: {
    xs: { span: 24 },
    sm: { span: 8 },
  },
  wrapperCol: {
    xs: { span: 24 },
    sm: { span: 10 },
  },
};

function AddEdit({ form }) {
  const { getFieldDecorator, validateFields } = form;

  const props = {
    name: 'file',
    action: '',
    headers: {
      authorization: 'authorization-text',
    },
    onChange(info) {
      console.log('info: ', info);
      // {
      //   file:{
        // response: "<!DOCTYPE html>↵<html l
        // size: 46028
        // status: "error"
        // type: "application/zip"
        //  uid:"rc-upload-1615377741675-2"
      // }
      //   fileList:[]
      // }
      
      if (info.file.status !== 'uploading') {
        console.log(info.file, info.fileList);
      }
      if (info.file.status === 'done') {
        message.success(`${info.file.name}successfully`);
      } else if (info.file.status === 'error') {
        message.error(`${info.file.name}failed.`);
      }
    },
  };

  
  // 提交
  const handleSubmit = (e) => {
    e.preventDefault();
    validateFields((err, values) => {
      if (!err) {
        console.log(values);
      }
    });
  };

  return (
    <div className="source">
      <Form {...formItemLayout} onSubmit={handleSubmit}>
        <Form.Item label="数据源类型">
          {getFieldDecorator("dsType", {
            initialValue: "",
            rules: [{ required: true, message: "Please input your username!" }],
          })(<Input />)}
        </Form.Item>
        <Form.Item label="数据源名称">
          {getFieldDecorator("param1", {
            initialValue: "",
            rules: [{ required: true, message: "Please input your username!" }],
          })(<Input />)}
        </Form.Item>
        <Form.Item label="描述">
          {getFieldDecorator("param2", {
            initialValue: "",
            rules: [{ message: "Please input your username!" }],
          })(<Input />)}
        </Form.Item>
        <Form.Item label="JDBC URL">
          {getFieldDecorator("param3", {
            initialValue: "",
            rules: [{ required: true, message: "Please input your username!" }],
          })(<Input />)}
        </Form.Item>
        <Form.Item label="用户名">
          {getFieldDecorator("param4", {
            initialValue: "",
            rules: [{ message: "Please input your username!" }],
          })(<Input />)}
        </Form.Item>
        <Form.Item label="密码">
          {getFieldDecorator("param5", {
            initialValue: "",
            rules: [{ message: "Please input your username!" }],
          })(<Input.Password placeholder="input password"/>)}
        </Form.Item>
        <Form.Item label="DefaultFS">
          {getFieldDecorator("param6", {
            initialValue: "",
            rules: [{ required: true, message: "Please input your username!" }],
          })(<Input />)}
        </Form.Item>
        <Form.Item label="高可用配置">
          {getFieldDecorator("param7", {
            initialValue: "",
            rules: [{ message: "Please input your username!" }],
          })(<Input />)}
        </Form.Item>
        <Form.Item label="Kerberos认证">
          {getFieldDecorator("param8")(
            <Upload {...props}>
            <Button>
              <Icon type="upload" /> Click to Upload
            </Button>
          </Upload>,
          )}
        </Form.Item>
        <Form.Item label="Principle">
          {getFieldDecorator("param9", {
            initialValue: "",
            rules: [{ required: true, message: "Please input your username!" }],
          })(<Input />)}
        </Form.Item>

        <Form.Item>
          <Button type="primary">测试连通性</Button>
          <Button type="primary">
            取消
          </Button>
          <Button type="primary" htmlType="submit">
            保存
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
}
export default Form.create()(AddEdit);
