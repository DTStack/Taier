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
  notification,
} from "antd";
import { FormComponentProps } from "antd/es/form";
import copy from "copy-to-clipboard";
import { API } from "@/services";

import downloadFile from "@/utils/downloadFile";
import { getSaveStatus } from "../../utils/handelSession";
import { getRules, getRulesJdbc } from "../../utils/formRules";
import "../../List/style.scss";

const { TextArea } = Input;
const { Option } = Select;

interface IProps extends FormComponentProps {
  cRef: any;
}

interface IParams {
  dataType: string;
  dataVersion: string;
  productCodeList: string[];
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
  const { form, cRef, record } = props;
  const { getFieldDecorator, validateFields } = form;

  const [templateData, setTemplateData] = useState([]);
  const [showUpload, setShowUpload] = useState<boolean>(false);
  const [file, setFile] = useState(null);
  const [params, setParams] = useState<IParams>({
    dataType: "",
    dataVersion: "",
    productCodeList: [],
  });

  useImperativeHandle(cRef, () => ({
    testForm,
    submitForm,
  }));

  const getTemplate = () => {
    let saveStatus = getSaveStatus();
    let dataType = saveStatus.sqlType?.dataType || ""; //数据库名称
    // let dataVersion = saveStatus.version || ""; //版本号
    let dataVersion = "";

    API.findTemplateByTypeVersion({
      dataType: record.dataTypeName || dataType,
      dataVersion: record.dataVersion || dataVersion,
    })
      .then((tem) => {
        if (record) {
          API.detail({
            dataInfoId: record?.dataInfoId,
          }).then((result) => {
            tem.data.fromFieldVoList.forEach((element) => {
              if (record && element.label === "数据源名称") {
                element.disabled = true;
              } else if (element.label === "数据源类型") {
                element.disabled = true;
              }
            });
            if (result) {
              tem.data.fromFieldVoList.forEach((element) => {
                element.initialValue = result.data[element.name];
              });
            }
            setTemplateData(tem.data.fromFieldVoList || []);
          });
        } else {
          setTemplateData(tem.data.fromFieldVoList || []);
        }
      })
      .catch((err) => {
        console.log("err: ", err);
      });
  };

  const getParams = () => {
    let saveStatus = getSaveStatus();
    let dataType = record.dataTypeName || saveStatus.sqlType?.dataType;
    let dataVersion = record.dataVersion || saveStatus.version;
    let productCodeList =
      record.productNames?.split(";") || saveStatus.checkdList?.split(",");

    setParams({
      dataType,
      dataVersion,
      productCodeList,
    });
  };
  useEffect(() => {
    getTemplate();
    getParams();
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
    validateFields(async (err, fieldsValue) => {
      if (!err) {
        fieldsValue = { ...fieldsValue, ...params };
        console.log("fieldsValue: ", fieldsValue);

        if (showUpload) {
          fieldsValue.file = file;
          let { success, message } = await API.testConWithKerberos(fieldsValue);
          if (success) {
            message.success("连接成功");
          } else {
            notification.error({
              message: "异常",
              description: "连接异常",
            });
          }
        } else {
          let { success, message } = await API.testCon(fieldsValue);
          if (success) {
            message.success("连接成功");
          } else {
            notification.error({
              message: "异常",
              description: "连接异常",
            });
          }
        }
      }
    });
  };

  //父组件-确定
  const submitForm = () => {
    validateFields(async (err, fieldsValue) => {
      //验证字段
      if (!err) {
        fieldsValue = { ...fieldsValue, ...params };
        console.log("fieldsValue: ", fieldsValue);

        if (showUpload) {
          fieldsValue.file = file;
          try {
            let { success } = await API.addDatasourceWithKerberos(fieldsValue);
            if (success) {
              message.success("添加数据源成功");
            }
          } catch (error) {
            notification.error({
              message: "错误！",
              description: "添加数据源失败",
            });
          }
        } else {
          try {
            let { success } = await API.addDatasource(fieldsValue);
            if (success) {
              message.success("添加数据源成功");
            }
          } catch (error) {
            notification.error({
              message: "错误！",
              description: "添加数据源失败",
            });
          }
        }
      }
    });
  };

  //1.InputWithCopy|Radio处理方法
  const changeInput = (e, name) => {
    // let jsondata = {};
    // jsondata[`${name}`] = e.target.value;
    // setFieldsValue(jsondata);
  };
  //2.Select
  const handleSelectChange = (value, name) => {
    // let jsondata = {};
    // jsondata[`${name}`] = value;
    // setFieldsValue(jsondata);
  };
  //3.switchChange处理选择框方法
  const switchChange = (value, name) => {
    // console.log("value: ", value); //false|true
    // let jsondata = {};
    // jsondata[`${name}`] = value;
    // setFieldsValue(jsondata);
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

  //渲染表单方法
  const formItem = templateData.map((item) => {
    switch (item.widget) {
      case "Input":
        return (
          <Form.Item label={item.label}>
            {getFieldDecorator(
              `${item.name}`,
              getRulesJdbc(item)
            )(
              <div style={{ position: "relative" }}>
                <Input
                  placeholder={item.placeHold || `请输入${item.label}`}
                  disabled={item.disabled}
                />
                {item.tooltip && (
                  <span style={{ position: "absolute", top: 0, right: -20 }}>
                    <Tooltip title={item.tooltip}>
                      <Icon type="question-circle-o" />
                    </Tooltip>
                  </span>
                )}
              </div>
            )}
          </Form.Item>
        );
      case "InputWithCopy":
        return (
          <Form.Item label={item.label}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(
              <div style={{ position: "relative" }}>
                <TextArea
                  id="copy"
                  rows={4}
                  placeholder={
                    JSON.stringify(item.placeHold) || `请输入${item.label}`
                  }
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
          </Form.Item>
        );
      case "Select":
        return (
          <Form.Item label={item.label}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(
              <Select
                placeholder={item.placeHold || `请输入${item.label}`}
                onChange={(value) => handleSelectChange(value, item.name)}
              >
                <Option value="male">male</Option>
                <Option value="female">female</Option>
              </Select>
            )}
          </Form.Item>
        );
      case "TextArea":
        return (
          <Form.Item label={item.label}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(
              <TextArea
                rows={4}
                placeholder={item.placeHold || `请输入${item.label}`}
              />
            )}
          </Form.Item>
        );
      case "RichText":
        return (
          <Form.Item label={item.label}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(<p>展示文字内容</p>)}
          </Form.Item>
        );
      case "Password":
        return (
          <Form.Item label={item.label}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(
              <Input.Password
                placeholder={item.placeHold || `请输入${item.label}`}
              />
            )}
          </Form.Item>
        );
      case "Switch":
        return (
          <Form.Item label={item.label}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(
              <Switch
                onChange={(checked) => switchChange(checked, item.name)}
              />
            )}
          </Form.Item>
        );
      case "Upload":
        return (
          <Form.Item label={item.label}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(
              <div>
                <Switch
                  onChange={(checked) => {
                    setShowUpload(checked);
                  }}
                />
                {showUpload && (
                  <div style={{ display: "flex" }}>
                    <Upload {...uploadProps}>
                      <Button>
                        <Icon type="upload" /> Click to upload
                      </Button>
                      <p>上传单个文件，支持扩展格式：.zip</p>
                    </Upload>
                    <div style={{ marginLeft: -40 }}>
                      <Icon type="question-circle" />
                      <span onClick={downloadtemplate} className="down-temp">
                        下载文件模板
                      </span>
                    </div>
                  </div>
                )}
              </div>
            )}
          </Form.Item>
        );
      case "Radio":
        return (
          <Form.Item label={item.label}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(
              <Radio.Group onChange={(e) => changeInput(e, item.name)}>
                <Radio value={1}>默认</Radio>
                <Radio value={2}>自定义</Radio>
              </Radio.Group>
            )}
          </Form.Item>
        );
      case "Integer":
        return (
          <Form.Item label={item.label}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(<InputNumber style={{ width: "100%" }} />)}
          </Form.Item>
        );
      default:
        break;
    }
  });

  return (
    <div>
      <Form {...formItemLayout} onSubmit={handleSubmit}>
        {formItem}
      </Form>
    </div>
  );
}
export default Form.create<IProps>({})(InfoConfig);
