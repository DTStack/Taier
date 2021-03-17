import React, { useEffect, useState, useImperativeHandle } from "react";
import {
  Form,
  Input,
  Button,
  Upload,
  Icon,
  message,
  Select,
  Radio,
  InputNumber,
  Tooltip,
  Switch,
} from "antd";
import "../List/style.scss";
import { FormComponentProps } from "antd/es/form";
import copy from "copy-to-clipboard";
import { API } from "@/services";

import downloadFile from "@/utils/downloadFile";

const { TextArea } = Input;
const { Option } = Select;

interface IProps extends FormComponentProps {
  cRef: any;
}

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

function InfoConfig(props) {
  const { form, cRef } = props;
  const {
    getFieldDecorator,
    validateFields,
    setFieldsValue,
  } = form;

  const [templateData, setTemplateData] = useState([]);

  useImperativeHandle(cRef, () => ({
    testForm: () => {
      //测试连通性方法
      testForm();
    },
    submitForm: () => {
      submitForm();
    },
  }));

  //根据数据库类型和版本查找表单模版
  const getTemplate = async () => {
    // try {
    //   let { data, success } = await API.findTemplateByTypeVersion({
    //     dataType: "MySQL",
    //     dataVersion: "",
    //   });

    //   if (success) {
    //     setTemplateData(data.fromFieldVoList || []);
    //   }
    // } catch (error) {
    //   message.error("获取信息配置失败");
    // }
    setTemplateData([
      {
        label: "数据源名称",
        name: "sqlname",
        placeHold: "请输入数据源名称",
        widget: "Input",
        required: 0,
      },
      {
        label: "高可用配置",
        name: "peizhi",
        placeHold: {
          "dfs.nameservices": "defaultDfs",
          "dfs.ha.namenodes.defaultDfs": "namenode1",
          "dfs.namenode.rpc-address.defaultDfs.namenode1": "",
          "dfs.client.failover.proxy.provider.defaultDfs":
          "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider",
        },
        widget: "InputWithCopy",
        required: 0,
      },
      {
        label: "是否关闭",
        name: "sel",
        placeHold: "请选择是否关闭",
        widget: "Select",
        required: 0,
      },
      {
        label: "描述",
        name: "desc",
        placeHold: "请输入描述",
        widget: "TextArea",
        required: 0,
      },
      {
        label: "富文本输入框",
        name: "richtext",
        placeHold: "请输入描述",
        widget: "RichText",
        required: 0,
      },
      {
        label: "Kerberos认证",
        name: "kerberos",
        placeHold: "请选择Kerberos认证",
        widget: "Upload",
        required: 0,
      },
      {
        label: "密码",
        name: "passward",
        placeHold: "请输入密码",
        widget: "Passward",
        required: 0,
      },
      //number类型
      {
        label: "HDFS配置",
        name: "radio",
        placeHold: "请选择",
        widget: "Radio",
        required: 0,
      },
      {
        label: "数字输入框",
        name: "integer",
        placeHold: "请输入数字",
        widget: "Integer",
        required: 0,
      },
      {
        label: "开关",
        name: "switch",
        placeHold: "",
        widget: "Switch",
        required: 0,
      },
    ]);
  };

  useEffect(() => {
    getTemplate();
  }, []);

  // 提交
  const handleSubmit = (e) => {
    e.preventDefault();
    validateFields((err, fieldsValue) => {
      //验证字段
      if (!err) {
        console.log("values", fieldsValue);
      }
    });
  };

  //文件上传处理
  const uploadProps = {
    name: "file",
    action: "https://www.mocky.io/v2/5cc8019d300000980a055e76",
    headers: {
      authorization: "authorization-text",
    },
    onChange(info) {
      if (info.file.status !== "uploading") {
        console.log(info.file, info.fileList);
      }
      if (info.file.status === "done") {
        message.success(`${info.file.name} file uploaded successfully`);
      } else if (info.file.status === "error") {
        message.error(`${info.file.name} file upload failed.`);
      }
    },
  };

  //父组件-测试连通性方法
  const testForm = () => {
    validateFields((err, values) => {
      if (!err) {
        console.log("values", values);
      }
    });
  };

  //父组件-确定
  const submitForm = () => {
    validateFields((err, fieldsValue) => {
      //验证字段
      if (!err) {
        console.log("values", fieldsValue);
      }
    });
  };

  //1.Input｜InputWithCopy｜TextArea|Passward|Integer|Radio处理方法
  const changeInput = (e, name) => {
    let jsondata = {};
    jsondata[`${name}`] = e.target.value;
    setFieldsValue(jsondata);
  };
  //2.Select|Integer处理选择框方法
  const handleSelectChange = (value, name) => {
    let jsondata = {};
    jsondata[`${name}`] = value;
    setFieldsValue(jsondata);
  };
  //3.switchChange处理选择框方法
  const switchChange = (value, name) => {
    console.log("value: ", value); //false|true
    let jsondata = {};
    jsondata[`${name}`] = value;
    setFieldsValue(jsondata);
  };

  //InputWithCopy之复制功能
  const handleCopy = (val) => {
    if (copy(JSON.stringify(val))) {
      message.success("复制成功");
    } else message.error("复制失敗，请手动复制");
  };

  //下载模板
  const downloadtemplate = async () => {
    try {
      const res = await API.downloadtemplate(
        {},
        {
          responseType: "blob",
        }
      );
      downloadFile(res);
    } catch (error) {}
  };

  // 渲染表单方法
  const formitems = templateData.map((item) => {
    let dynamicForm = (
      <Form.Item label={item.label}>
        {getFieldDecorator(`${item.name}`, {
          rules: [
            {
              required: item.required === 1 ? true : false,
              message: `${item.label}不能为空`,
            },
          ],
        })(
          <>
            {item.widget === "Input" && (
              <Input
                placeholder={item.placeHold || `请输入${item.label}`}
                onChange={(e) => changeInput(e, item.name)}
              />
            )}

            {item.widget === "InputWithCopy" && (
              <div style={{ position: "relative" }}>
                <TextArea
                  id="copy"
                  rows={4}
                  placeholder={
                    JSON.stringify(item.placeHold) || `请输入${item.label}`
                  }
                  onChange={(e) => changeInput(e, item.name)}
                />
                <div
                  style={{
                    position: "absolute",
                    right: -20,
                    top: 0,
                    marginLeft: 8,
                  }}
                >
                  <Tooltip
                    title={
                      <div>
                        高可用模式下的填写规则：
                        <br />
                        1、分别要填写：nameservice名称、
                        namenode名称（多个以逗号分隔）、proxy.provider参数；
                        <br />
                        2、所有参数以JSON格式填写；
                        <br />
                        3、格式为： "dfs.nameservices": "nameservice名称",
                        "dfs.ha.namenodes.nameservice名称":
                        "namenode名称，以逗号分隔",
                        "dfs.namenode.rpc-address.nameservice名称.namenode名称":
                        "",
                        "dfs.namenode.rpc-address.nameservice名称.namenode名称":
                        "", "dfs.client.failover.proxy.provider.
                        nameservice名称": "org.apache.hadoop.
                        hdfs.server.namenode.ha.
                        ConfiguredFailoverProxyProvider"
                        <br />
                        4、详细参数含义请参考《帮助文档》或
                        <a
                          style={{ color: "#3F87FF" }}
                          target="_blank"
                          href="http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html"
                        >
                          Hadoop官方文档
                        </a>
                      </div>
                    }
                  >
                    <Icon type="question-circle-o" />
                  </Tooltip>
                  <Icon
                    className="copy"
                    type="copy"
                    onClick={() => handleCopy(item.placeHold)}
                    style={{ display: "block", marginTop: 20 }}
                  />
                </div>
              </div>
            )}

            {item.widget === "Select" && (
              <Select
                placeholder={item.placeHold || `请输入${item.label}`}
                onChange={(value) => handleSelectChange(value, item.name)}
              >
                <Option value="male">male</Option>
                <Option value="female">female</Option>
              </Select>
            )}

            {item.widget === "TextArea" && (
              <TextArea
                rows={4}
                placeholder={item.placeHold || `请输入${item.label}`}
                onChange={(e) => changeInput(e, item.name)}
              />
            )}

            {item.widget === "RichText" && <p>展示文字内容</p>}

            {item.widget === "Passward" && (
              <Input.Password
                placeholder={item.placeHold || `请输入${item.label}`}
                onChange={(e) => changeInput(e, item.name)}
              />
            )}

            {item.widget === "Switch" && (
              <Switch
                defaultChecked
                onChange={(checked) => switchChange(checked, item.name)}
              />
            )}

            {item.widget === "Upload" && (
              <div style={{display:"flex"}}>
                <Upload {...uploadProps}>
                  <Button>
                    <Icon type="upload" /> Click to upload
                  </Button>
                  <p>上传单个文件，支持扩展格式：.zip</p>
                </Upload>
                <div style={{marginLeft:-40}}>
                  <Icon type="question-circle"/>
                  <span onClick={downloadtemplate} className="down-temp">
                    下载文件模板
                  </span>
                </div>
              </div>
            )}

            {item.widget === "Radio" && (
              <Radio.Group onChange={(e) => changeInput(e, item.name)}>
                <Radio value={1}>默认</Radio>
                <Radio value={2}>自定义</Radio>
              </Radio.Group>
            )}

            {item.widget === "Integer" && (
              <InputNumber
                style={{ width: "100%" }}
                onChange={(value) => handleSelectChange(value, item.name)}
              />
            )}
          </>
        )}
      </Form.Item>
    );
    return dynamicForm;
  });

  return (
    <div>
      <Form {...formItemLayout} onSubmit={handleSubmit}>
        {formitems}
      </Form>
    </div>
  );
}
export default Form.create<IProps>({})(InfoConfig);
