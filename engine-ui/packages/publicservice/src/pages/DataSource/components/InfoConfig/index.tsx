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
import { FormComponentProps } from "antd/es/form";
import copy from "copy-to-clipboard";
import { API } from "@/services";

import downloadFile from "@/utils/downloadFile";
import { getSaveStatus } from "../../utils/handelSession";
import { getRules, getRulesJdbc } from "../../utils/formRules";
import "../../List/style.scss";
import { HDFSCONG } from "../../constants/index";

const { TextArea } = Input;
const { Option } = Select;

interface IProps extends FormComponentProps {
  cRef: any;
  record: any;
  form: any;
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

const InfoConfig = (props) => {
  const { form, cRef, record } = props;
  const { getFieldDecorator, validateFields, setFieldsValue } = form;

  const [templateData, setTemplateData] = useState([]);
  const [showUpload, setShowUpload] = useState<boolean>(false);
  const [file, setFile] = useState(null);
  const [fileList, setFileList] = useState(null);
  const [params, setParams] = useState<IParams>({
    dataType: "",
    dataVersion: "",
    productCodeList: [],
  });

  const [connet, setConnet] = useState<boolean>(true);
  const [privateKey, setPrivateKey] = useState<boolean>(false);
  const [carbon, setCarbon] = useState<boolean>(false);
  const [webSocketParams, setWebSocketParams] = useState({});
  const [redisRadio, setRedisRadio] = useState<number>(1);

  useImperativeHandle(cRef, () => ({
    testForm,
    submitForm,
  }));

  const templateForm = async () => {
    let saveStatus = getSaveStatus();
    let dataType = saveStatus.sqlType?.dataType || ""; //数据库名称
    let dataVersion = saveStatus.version; //版本号
    let { data } = await API.findTemplateByTypeVersion({
      dataType: record.dataTypeName || dataType,
      dataVersion: record.dataVersion || dataVersion,
    });
    return await data;
  };

  const getDetail = async () => {
    let { data } = await API.detail({
      dataInfoId: record?.dataInfoId,
    });
    return await data;
  };

  const getAllData = async () => {
    let { fromFieldVoList } = await templateForm();
    if (record) {
      let detailData = await getDetail();
      if (detailData) {
        fromFieldVoList.forEach((element) => {
          element.initialValue = detailData[element.name];
        });
      }
      setTemplateData(fromFieldVoList || []);
    } else {
      fromFieldVoList.forEach((element) => {
        if (element.label === "数据源类型") {
          element.disabled = true;
        }
      });
      setTemplateData(fromFieldVoList || []);
    }
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
    getAllData();
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
  // const uploadProps = {
  //   name: "file",
  //   action: "https://www.mocky.io/v2/5cc8019d300000980a055e76",
  //   headers: {
  //     authorization: "authorization-text",
  //   },
  //   onChange(info) {
  //     if (info.file.status !== "uploading") {
  //       console.log(info.file, info.fileList);
  //     }
  //     if (info.file.status === "done") {
  //       message.success(`${info.file.name} file uploaded successfully`);
  //     } else if (info.file.status === "error") {
  //       message.error(`${info.file.name} file upload failed.`);
  //     }
  //   },
  // };

  //父组件-测试连通性方法
  const testForm = () => {
    validateFields(async (err, fieldsValue) => {
      if (!err) {
        fieldsValue = { ...fieldsValue, ...params };
        console.log("fieldsValue: ", fieldsValue);

        if (showUpload) {
          fieldsValue.file = file;
          // let { success, message } = await API.testConWithKerberos(fieldsValue);
          // if (success) {
          //   message.success("连接成功");
          // } else {
          //   message.error("连接异常");
          // }
        } else {
          // let { success, message } = await API.testCon(fieldsValue);
          // if (success) {
          //   message.success("连接成功");
          // } else {
          //   message.error("连接异常");
          // }
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
          let { success, message: msg } = await API.addDatasourceWithKerberos(
            fieldsValue
          );
          if (success) {
            message.success("添加数据源成功");
          } else {
            message.error(`${msg}`);
          }
        } else {
          let { success, message: msg } = await API.addDatasource(fieldsValue);
          if (success) {
            message.success("添加数据源成功");
          } else {
            message.error(`${msg}`);
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
  //3.switchChange处理upload方法
  const switchChange = (value, label, name) => {
    let jsondata = {};
    jsondata[`${name}`] = value;
    setFieldsValue(jsondata);
    if (label === "开启Kerberos认证") {
      setShowUpload(value);
    }
  };

  //InputWithCopy｜TextAreaWithCopy之复制功能
  const handleCopy = (item) => {
    if (copy(item.placeHold)) {
      message.success("复制成功");
    } else message.error("复制失败，请手动复制");
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

  const customRequest = () => {};

  //浏览文件
  const beforeUpload = async (file, fileList) => {
    const { success } = await API.uploadCode({ file });
    if (success) {
      setFile(file); //设置file的名字 后续接口传参
      setFileList(fileList); //控制上传列表数量
      message.success("上传成功");
    } else {
      message.error("上传失败!");
    }
  };

  //ftp定制化处理方式
  const handleFtpChange = (e) => {
    let { value } = e.target;
    if (value === "FTP") {
      setConnet(true);
    } else {
      setConnet(false);
    }

    if (value === 2) {
      setPrivateKey(true);
    } else {
      setPrivateKey(false);
    }
  };
  //CarbonData定制化处理方式
  const handleCarbonChange = (e) => {
    let { value } = e.target;
    if (value === "custom") {
      setCarbon(true);
    } else {
      setCarbon(false);
    }
  };

  //WebSocket定制化处理方式
  const addWsParams = () => {
    let params = Object.assign({}, webSocketParams);
    params[""] = "";
    if (validateIsEmpty(webSocketParams)) {
      message.warning("请先完整填写参数!");
      return;
    }
    if (Object.keys(webSocketParams).length === 20) {
      message.warning("最多可添加20行鉴权参数!");
      return;
    }
    setWebSocketParams(params);
  };
  const validateIsEmpty = (params) => {
    return (
      Object.keys(params).includes("") || Object.values(params).includes("")
    );
  };
  const delWsParams = (index: number) => {
    let params = Object.assign({}, webSocketParams);
    delete params[Object.keys(webSocketParams)[index]];
    setWebSocketParams(params);
  };
  const editWsParams = (e, index: number, type: "key" | "value") => {
    const { value } = e.target;
    let params = Object.assign({}, webSocketParams);
    if (type === "key") {
      const entriesArr = Object.entries(params);
      entriesArr[index][0] = value;
      params = (Object as any).fromEntries(entriesArr);
    } else {
      params[Object.keys(webSocketParams)[index]] = value;
    }
    setWebSocketParams(params);
  };

  const renderWebSocketParams = () => {
    let inputFormsData = [];
    for (let [key, value] of Object.entries(webSocketParams)) {
      inputFormsData.push({
        key,
        value,
      });
    }
    return inputFormsData.map((ws, index) => {
      return (
        <div key={index} className="ws-form">
          <Input
            onChange={(e) => {
              editWsParams(e, index, "key");
            }}
            value={ws.key}
            placeholder="请输入key值"
          />{" "}
          : &nbsp;
          <Input
            onChange={(e) => {
              editWsParams(e, index, "value");
            }}
            value={ws.value}
            type="password"
            placeholder="请输入value值"
          />
          <a
            onClick={() => {
              delWsParams(index);
            }}
          >
            删除
          </a>
        </div>
      );
    });
  };

  //redis定制化
  const handelRedisCom = (e) => {
    setRedisRadio(e.target.value);
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
                <Input
                  placeholder={item.placeHold || `请输入${item.label}`}
                  disabled={item.disabled}
                />
                <div
                  style={{
                    position: "absolute",
                    right: -20,
                    top: 0,
                  }}
                >
                  <Icon
                    className="copy"
                    type="copy"
                    onClick={() => handleCopy(item)}
                    style={{ display: "block", marginTop: 11 }}
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
      case "TextAreaWithCopy":
        return (
          <Form.Item label={item.label}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(
              <div style={{ position: "relative" }}>
                <TextArea id="copy" rows={4} placeholder={item.placeHold} />
                <div
                  style={{
                    position: "absolute",
                    right: -20,
                    top: 0,
                    marginLeft: 8,
                  }}
                >
                  {item.tooltip && (
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
                  )}

                  <Icon
                    className="copy"
                    type="copy"
                    onClick={() => handleCopy(item)}
                    style={{ display: "block", marginTop: 20 }}
                  />
                </div>
              </div>
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
                onChange={(checked) =>
                  switchChange(checked, item.label, item.name)
                }
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
                    <Upload>
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
      case "Kerberos":
        return (
          <>
            <Form.Item label={item.label}>
              {getFieldDecorator(
                `${item.name}`,
                getRules(item)
              )(
                <div>
                  <Switch
                    onChange={(checked) =>
                      switchChange(checked, item.label, item.name)
                    }
                  />
                  {showUpload && (
                    <div style={{ display: "flex" }}>
                      <Upload
                        customRequest={customRequest}
                        beforeUpload={beforeUpload}
                        fileList={fileList}
                      >
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
          </>
        );
      case "HbaseKerberos":
        // HbaseKerberos需要解析对应的内容，底部显示解析的字段
        return (
          <>
            <Form.Item label="开启Kerberos认证">
              {getFieldDecorator("HbaseKerberos")(
                <div>
                  <Switch
                    onChange={(checked) =>
                      switchChange(checked, item.label, item.name)
                    }
                  />
                  {showUpload && (
                    <div style={{ display: "flex" }}>
                      <Upload
                        customRequest={customRequest}
                        beforeUpload={beforeUpload}
                        fileList={fileList}
                      >
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
            {params.dataType === "HBase" && showUpload && (
              <div>
                <Form.Item label="client.principal">
                  {getFieldDecorator("principal", {
                    initialValue: "",
                    rules: [
                      {
                        required: true,
                        message: "client.principal不能为空",
                      },
                    ],
                  })(<Input />)}
                </Form.Item>
                <Form.Item label="master_kerberos_principal">
                  {getFieldDecorator("hbase_master_kerberos_principa", {
                    initialValue: "",
                    rules: [
                      {
                        required: true,
                        message: "master_kerberos_principal不能为空",
                      },
                    ],
                  })(<Input />)}
                </Form.Item>
                <Form.Item label="regionserver_kerberos_principal">
                  {getFieldDecorator("hbase_regionserver_kerberos_principal", {
                    initialValue: "",
                    rules: [
                      {
                        required: true,
                        message: "mregionserver_kerberos_principal不能为空",
                      },
                    ],
                  })(<Input />)}
                </Form.Item>
              </div>
            )}
          </>
        );
      case "FtpReact":
        return (
          <>
            {params.dataType === "FTP" && (
              <>
                <Form.Item label="协议">
                  {getFieldDecorator("agreement", {
                    initialValue: "FTP",
                    rules: [
                      {
                        required: true,
                        message: "协议不能为空",
                      },
                    ],
                  })(
                    <Radio.Group onChange={(value) => handleFtpChange(value)}>
                      <Radio value="FTP">FTP</Radio>
                      <Radio value="SFTP">SFTP</Radio>
                    </Radio.Group>
                  )}
                </Form.Item>
                {connet && (
                  <Form.Item label="连接模式">
                    {getFieldDecorator("connectMode", {
                      initialValue: "PORT",
                      rules: [
                        {
                          required: true,
                          message: "连接模式不能为空",
                        },
                      ],
                    })(
                      <Radio.Group>
                        <Radio value="PORT">Port (主动)</Radio>
                        <Radio value="PASV">Pasv（被动）</Radio>
                      </Radio.Group>
                    )}
                  </Form.Item>
                )}

                {!connet && (
                  <>
                    <Form.Item label="认证方式">
                      {getFieldDecorator("auth", {
                        initialValue: 1,
                        rules: [
                          {
                            required: true,
                            message: "认证方式不能为空",
                          },
                        ],
                      })(
                        <Radio.Group
                          onChange={(value) => handleFtpChange(value)}
                        >
                          <Radio value={1}>密码</Radio>
                          <Radio value={2}>私钥</Radio>
                        </Radio.Group>
                      )}
                    </Form.Item>

                    {privateKey && (
                      <Form.Item label="私钥地址">
                        {getFieldDecorator("rsaPath", {
                          initialValue: "~/.ssh/id_rsa",
                          rules: [
                            {
                              required: true,
                              message: "私钥地址不能为空",
                            },
                          ],
                        })(<Input />)}
                      </Form.Item>
                    )}
                  </>
                )}
              </>
            )}
          </>
        );
      case "CarbonReact":
        return (
          params.dataType === "CarbonData" && (
            <>
              <Form.Item label="HDFS配置">
                {getFieldDecorator("hdfsCustomConfig", {
                  initialValue: "default",
                })(
                  <Radio.Group onChange={(value) => handleCarbonChange(value)}>
                    <Radio value="default">默认</Radio>
                    <Radio value="custom">custom</Radio>
                  </Radio.Group>
                )}
              </Form.Item>

              {carbon && (
                <>
                  <Form.Item label="defaultFS">
                    {getFieldDecorator("defaultFS", {
                      rules: [
                        {
                          required: true,
                          message: "defaultFS不能为空",
                        },
                      ],
                    })(<Input placeholder="hdfs://host:port" />)}
                  </Form.Item>

                  <Form.Item label="高可用配置">
                    {getFieldDecorator("hadoopConfig")(
                      <div style={{ position: "relative" }}>
                        <TextArea id="copy" rows={4} placeholder={HDFSCONG} />
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
                                3、格式为： "dfs.nameservices":
                                "nameservice名称",
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
                            onClick={() =>
                              handleCopy({
                                label: "高可用配置",
                                placeHold: HDFSCONG,
                              })
                            }
                            style={{ display: "block", marginTop: 20 }}
                          />
                        </div>
                      </div>
                    )}
                  </Form.Item>
                </>
              )}
            </>
          )
        );
      case "WebSocketSub":
        return (
          <Form.Item label="鉴权参数" key="webSocketParams">
            {renderWebSocketParams()}
            <span className="ws-add" onClick={addWsParams}>
              <Icon type="plus-circle-o" />
              <span>新增参数</span>
            </span>
          </Form.Item>
        );
      case "RedisReact":
        return (
          <>
            <Form.Item label="模式">
              {getFieldDecorator("redisType", {
                initialValue: redisRadio,
                rules: [
                  {
                    required: true,
                    message: "模式不能为空",
                  },
                ],
              })(
                <Radio.Group onChange={(e) => handelRedisCom(e)}>
                  <Radio value={1}>单机</Radio>
                  <Radio value={2}>集群</Radio>
                  <Radio value={3}>哨兵</Radio>
                </Radio.Group>
              )}
            </Form.Item>
            <Form.Item label="地址">
              {getFieldDecorator("hostPort", {
                rules: [
                  {
                    required: true,
                    message: "地址不能为空",
                  },
                ],
              })(
                <TextArea
                  rows={4}
                  placeholder={
                    redisRadio === 1
                      ? "Redis地址，例如：IP1:Port"
                      : "Redis地址，例如：IP1:Port，多个地址以英文逗号分开"
                  }
                />
              )}
            </Form.Item>
            {redisRadio === 3 && (
              <Form.Item label="master名称">
                {getFieldDecorator("masterName", {
                  rules: [
                    {
                      required: true,
                      message: "master名称不能为空",
                    },
                  ],
                })(<Input placeholder="请输入master名称" />)}
              </Form.Item>
            )}
            {(redisRadio === 1 || redisRadio === 3) && (
              <Form.Item label="数据库">
                {getFieldDecorator("database")(<Input />)}
              </Form.Item>
            )}
            <Form.Item label="密码">
              {getFieldDecorator("password")(<Input.Password />)}
            </Form.Item>
          </>
        );
      default:
        break;
    }
  });

  return (
    <div>
      <Form {...formItemLayout} onSubmit={handleSubmit}>
        <Form.Item label="数据源类型">
          {getFieldDecorator("dataType", {
            initialValue: params.dataType + params.dataVersion,
            rules: [
              {
                required: true,
                message: "数据源类型不能为空",
              },
            ],
          })(<Input disabled />)}
        </Form.Item>
        {formItem}

        {/* 1.Kerberos */}
        {/* 2.HbaseKerberos */}
        {/* 3.ftp定制化 */}
        {/* 4.CarbonData定制化 */}
        {/* 5.WebSocket定制化 */}
        {/* 6.redis定制化 */}
      </Form>
    </div>
  );
};
export default Form.create<IProps>({})(InfoConfig);
