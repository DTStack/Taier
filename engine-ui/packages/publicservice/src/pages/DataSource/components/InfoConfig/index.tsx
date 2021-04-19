import React, { useEffect, useState, useImperativeHandle } from 'react';
import { withRouter } from 'react-router';
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
  Spin,
} from 'antd';
import { FormComponentProps } from 'antd/es/form';
import copy from 'copy-to-clipboard';
import moment from 'moment';
import Base64 from 'base-64';

import { API } from '@/services';

import { checks, getSaveStatus } from '../../utils/handelSession';
import { getRules, IParams, formItemLayout, formNewLayout } from './formRules';
import { HDFSCONG } from '../../constants/index';
import { hdfsConfig } from './tooltips';
import '../../List/style.scss';

const { TextArea } = Input;
const { Option } = Select;

interface IProps extends FormComponentProps {
  cRef: any;
  record: any;
  form: any;
  changeBtnStatus?: () => void;
}

const InfoConfig = (props) => {
  const { form, cRef, record } = props;
  const {
    getFieldDecorator,
    validateFields,
    getFieldValue,
    setFieldsValue,
  } = form;

  const [templateData, setTemplateData] = useState([]);
  const [showUpload, setShowUpload] = useState<boolean>(false);
  const [file, setFile] = useState(null);
  const [fileList, setFileList] = useState([]);
  const [otherParams, setOtherParams] = useState<IParams>({
    dataType: '',
    dataVersion: '',
    appTypeList: [],
  });

  const [webSocketParams, setWebSocketParams] = useState({});
  const [principalsList, setPrincipalsList] = useState([]);
  const [loading, setLoading] = useState<boolean>(false);

  const [editChangeFile, setEditChangeFile] = useState<boolean>(false);

  //定制化组件编辑渲染
  const [detailData, setDetailData] = useState(null);

  useImperativeHandle(cRef, () => ({
    testForm: () => {
      testForm(false);
    },
    submitForm: () => {
      testForm(true);
    },
  }));

  const templateForm = async () => {
    let saveStatus = getSaveStatus();
    let dataType = saveStatus.sqlType?.dataType || ''; //数据库名称
    let dataVersion = saveStatus.version; //版本号
    let { data } = await API.findTemplateByTypeVersion({
      dataType: record?.dataType || dataType,
      dataVersion: record?.dataVersion || dataVersion,
    });
    return (await data) || [];
  };

  const getDetail = async () => {
    let { data } = await API.detail({
      dataInfoId: record?.dataInfoId,
    });
    return (await data) || [];
  };

  //编辑时需获取产品授权列表
  const getAuthProductList = async () => {
    let { data, success } = await API.authProductList({
      dataInfoId: record?.dataInfoId,
    });
    let newList = [];
    if (success) {
      if (data.length > 0) {
        data.forEach((item) => {
          if (item.isAuth === 1) {
            newList.push(item.appType);
          }
        });
      }
    }
    return newList;
  };

  const getAllData = async () => {
    let { fromFieldVoList } = await templateForm();

    fromFieldVoList = fromFieldVoList.filter((item) => item.invisible !== 1);
    if (record) {
      let detailData = await getDetail();
      if (detailData) {
        fromFieldVoList.forEach((item) => {
          if (item.label === '数据源名称') {
            item.disabled = true;
          }
          try {
            item.initialValue =
              detailData[item.name] ||
              JSON.parse(Base64.decode(detailData.dataJson))[item.name];
          } catch (error) {}

          let data: any = {};
          try {
            data = JSON.parse(Base64.decode(detailData.dataJson));
          } catch (error) {}
          setDetailData(data);

          //webSocket定制化
          setWebSocketParams(data?.webSocketParams || {});
        });
      }
    }
    setTemplateData(fromFieldVoList || []);
  };

  const getParams = async () => {
    let saveStatus = getSaveStatus();
    let dataType = record.dataType || saveStatus.sqlType?.dataType;
    let dataVersion = record.dataVersion || saveStatus.version;
    let appTypeList = record ? await getAuthProductList() : checks;

    setOtherParams({
      dataType,
      dataVersion,
      appTypeList,
    });
  };

  useEffect(() => {
    getAllData();
    getParams();
  }, []);

  //父组件-测试连通性方法
  const testForm = (submit?: boolean) => {
    validateFields(async (err, fieldsValue) => {
      if (!err) {
        setLoading(true);

        let handelParams: any = { ...otherParams };

        //Remove leading and trailing spaces
        for (const key in fieldsValue) {
          if (typeof fieldsValue[key] === 'string') {
            fieldsValue[key] = fieldsValue[key].trim();
          }
        }
        handelParams.dataName = fieldsValue.dataName;
        handelParams.dataDesc = fieldsValue?.dataDesc || '';

        delete fieldsValue.dataName;
        delete fieldsValue.dataDesc;
        delete fieldsValue.dataType;

        let infoMsg = '添加数据源成功';
        if (record) {
          //edit need id
          handelParams.id = record.dataInfoId;
          infoMsg = '修改数据源成功';
        }

        //webSocket定制化
        try {
          if (JSON.stringify(webSocketParams) !== '{}') {
            fieldsValue.webSocketParams = webSocketParams;
          }
        } catch (error) {
          setLoading(false);
        }

        if (getFieldValue('kerberosFile')) {
          if (editChangeFile) {
            handelParams.file = fieldsValue?.kerberosFile;
          }
          delete fieldsValue.openKerberos;
          delete fieldsValue.kerberosFile;
          delete handelParams.appTypeList;

          handelParams.appTypeListString = otherParams.appTypeList.toString();
          try {
            handelParams.dataJsonString = Base64.encode(
              JSON.stringify(fieldsValue)
            );
          } catch (error) {
            console.log('error: ', error);
            setLoading(false);
          }

          if (submit) {
            //确定按钮
            submitForm(handelParams, infoMsg);
          } else {
            //测试连通性按钮
            request(handelParams, 'testConWithKerberos')
              .then((res: any) => {
                if (res.success && res.data) {
                  message.success('连接成功');
                } else {
                  message.error(res.message || '连接失败');
                }
                setLoading(false);
              })
              .catch(() => {
                setLoading(false);
                message.error('测试连通性请求超时');
              });
          }
        } else {
          try {
            handelParams.dataJsonString = Base64.encode(
              JSON.stringify(fieldsValue)
            );
          } catch (error) {
            setLoading(false);
          }

          if (submit) {
            //确定按钮
            submitForm(handelParams, infoMsg);
          } else {
            //测试连通性按钮
            request(handelParams, 'testCon')
              .then((res: any) => {
                if (res.success && res.data) {
                  message.success('连接成功');
                } else {
                  message.error(res.message || '连接失败');
                }
                setLoading(false);
              })
              .catch(() => {
                setLoading(false);
                message.error('测试连通性请求超时');
              });
          }
        }
      } else {
        props.changeBtnStatus(false);
      }
    });
  };

  const request = (handelParams, name) => {
    const controller = new AbortController();
    const { signal } = controller;
    return new Promise((resolve, reject) => {
      let status = 0; // 0 等待 1 完成 2 超时
      let timer = setTimeout(() => {
        if (status === 0) {
          status = 2;
          timer = null;
          controller.abort();
          reject('测试连通性请求超时');
        }
      }, 10000);

      API[name](handelParams, { signal }).then((res) => {
        if (status !== 2) {
          clearTimeout(timer);
          resolve(res);
          timer = null;
          status = 1;
        }
      });
    });
  };

  //父组件-确定
  const submitForm = (handelParams, infoMsg) => {
    validateFields(async (err, fieldsValue) => {
      //验证字段
      if (!err) {
        if (getFieldValue('kerberosFile')) {
          let {
            success,
            message: msg,
            data,
          } = await API.addOrUpdateSourceWithKerberos(handelParams);

          if (success && data) {
            message.success(`${infoMsg}`);
            setTimeout(() => {
              props.router.push('/data-source/list');
            }, 500);
          } else {
            message.error(`${msg}`);
          }
          setLoading(false);
          props.changeBtnStatus(false);
        } else {
          let { success, message: msg, data } = await API.addDatasource(
            handelParams
          );

          if (success && data) {
            message.success(`${infoMsg}`);
            setTimeout(() => {
              props.router.push('/data-source/list');
            }, 500);
          } else {
            message.error(`${msg}`);
          }
          setLoading(false);
          props.changeBtnStatus(false);
        }
      } else {
        props.changeBtnStatus(false);
      }
    });
  };
  //InputWithCopy｜TextAreaWithCopy之复制功能
  const handleCopy = (item) => {
    if (copy(item.placeHold)) {
      message.success('复制成功');
    } else message.error('复制失败，请手动复制');
  };

  //WebSocket定制化处理方式
  const addWsParams = () => {
    let params = Object.assign({}, webSocketParams);
    params[''] = '';
    if (validateIsEmpty(webSocketParams)) {
      message.warning('请先完整填写参数!');
      return;
    }
    if (Object.keys(webSocketParams).length === 20) {
      message.warning('最多可添加20行鉴权参数!');
      return;
    }
    setWebSocketParams(params);
  };
  const validateIsEmpty = (params) => {
    return (
      Object.keys(params).includes('') || Object.values(params).includes('')
    );
  };
  const delWsParams = (index: number) => {
    let params = Object.assign({}, webSocketParams);
    delete params[Object.keys(webSocketParams)[index]];
    setWebSocketParams(params);
  };
  const editWsParams = (e, index: number, type: 'key' | 'value') => {
    const { value } = e.target;
    let params = Object.assign({}, webSocketParams);
    if (type === 'key') {
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
            autoComplete="off"
            onChange={(e) => {
              editWsParams(e, index, 'key');
            }}
            value={ws.key}
            placeholder="请输入key值"
          />{' '}
          : &nbsp;
          <Input
            autoComplete="off"
            onChange={(e) => {
              editWsParams(e, index, 'value');
            }}
            value={ws.value}
            type="password"
            placeholder="请输入value值"
          />
          <a
            onClick={() => {
              delWsParams(index);
            }}>
            删除
          </a>
        </div>
      );
    });
  };

  //Kerberos||HbaseKerberos定制化
  const principalsOptions = principalsList.map((item: any) => (
    <Option key={item} value={item}>
      {item}
    </Option>
  ));

  const getPrincipalsWithConf = async (
    kerberosFile?: any,
    callBack?: Function
  ) => {
    let princiPrams = { ...otherParams };
    delete princiPrams.appTypeList;
    const res = await await API.uploadCode({
      file: kerberosFile,
      ...princiPrams,
    });
    if (res.success) {
      setFile(file); //设置file的名字 后续接口传参
      setFileList(fileList); //控制上传列表数量
      message.success('上传成功');
    } else {
      message.error('上传失败!');
    }
    callBack && callBack(res);
  };

  const uploadForm = () => {
    const nullArr: any[] = [];
    const upProps = {
      beforeUpload: (file: any) => {
        file.modifyTime = moment();
        setEditChangeFile(true);
        getPrincipalsWithConf(file, (res: any) => {
          if (res.code !== 1) {
            setFieldsValue({
              [`kerberosFile`]: '',
            });
            return;
          }
          // 上传文件前清空 masterKer、regionserverKer
          setFieldsValue({
            [`kerberosFile`]: file,
            [`principal`]: res.data[0],
            [`hbase_master_kerberos_principal`]: '',
            [`hbase_regionserver_kerberos_principal`]: '',
          });
          setPrincipalsList(res.data);
        });
        return false;
      },
      fileList: nullArr,
      name: 'file',
      accept: '.zip',
    };
    return (
      <Form.Item {...formNewLayout} label="" key="kerberosFile">
        {getFieldDecorator(`kerberosFile`, {
          rules: [
            {
              required: true,
              message: '文件不可为空！',
            },
          ],
          initialValue: detailData?.kerberosFile || '',
        })(<div />)}
        <div
          style={{
            display: 'flex',
          }}>
          <Upload {...upProps}>
            <Button style={{ color: '#999' }}>
              <Icon type="upload" /> Click to upload
            </Button>
            <p style={{marginTop: 8, color:'#666'}}>上传单个文件，支持扩展格式：.zip</p>
          </Upload>
          <div style={{ marginLeft: -57 }}>
            <Tooltip title="仅支持Zip格式，压缩包需包含xxx.keytab、krb5.config文件。上传文件前，请在控制台开启SFTP服务。">
              <Icon
                type="question-circle-o"
                style={{
                  fontSize: '14px',
                  marginTop: '11px',
                  marginLeft: '10px',
                  color: '#999',
                }}
              />
            </Tooltip>
          </div>
        </div>
        <p>上传单个文件，支持扩展格式：.zip</p>
        {getFieldValue(`kerberosFile`) ? (
          <div
            style={{
              width: '100%',
              position: 'relative',
              marginTop: -16
            }}>
            <Icon
              type="close"
              style={{
                cursor: 'pointer',
                position: 'absolute',
                right: 0,
                top: 13,
                zIndex: 99,
                color: '#999'
              }}
              onClick={() => {
                setFieldsValue({
                  [`kerberosFile`]: '',
                });
              }}
            />
            <i className="iconfont2 iconOutlinedxianxing_Attachment" style={{
              position: 'absolute',
              left: 0,
              zIndex: 99,
              color: '#999'
            }}></i>
            <Input
              style={{border: 0, paddingRight: 20, background: 'transparent', paddingLeft: 20}}
              autoComplete="off"
              value={
                getFieldValue(`kerberosFile`).name +
                '   ' +
                moment(getFieldValue(`kerberosFile`).modifyTime).format(
                  'YYYY-MM-DD HH:mm:ss'
                )
              }
            />
          </div>
        ) : null}
      </Form.Item>
    );
  };

  //渲染表单方法
  const formItem = templateData.map((item, index) => {
    switch (item.widget) {
      case 'Input':
        return (
          <Form.Item label={item.label} key={index}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(
              <Input
                autoComplete="off"
                placeholder={item.placeHold || `请输入${item.label}`}
                disabled={item.disabled}
              />
            )}
            {item.tooltip && (
              <Tooltip title={item.tooltip}>
                <Icon className="help-doc" type="question-circle-o" />
              </Tooltip>
            )}
          </Form.Item>
        );
      case 'InputWithCopy':
        return (
          <Form.Item label={item.label} key={index}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(
              <Input
                autoComplete="off"
                placeholder={item.placeHold || `请输入${item.label}`}
                disabled={item.disabled}
              />
            )}
            <Icon
              className="help-doc"
              type="copy"
              onClick={() => handleCopy(item)}
            />
          </Form.Item>
        );
      case 'Select':
        return (
          <Form.Item label={item.label} key={index}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(
              <Select placeholder={item.placeHold || `请输入${item.label}`}>
                <Option value="option1" key="option1">
                  option1
                </Option>
                <Option value="option2" key="option2">
                  option2
                </Option>
              </Select>
            )}
          </Form.Item>
        );
      case 'TextArea':
        return (
          <Form.Item label={item.label} key={index}>
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
      case 'TextAreaWithCopy':
        return (
          <Form.Item label={item.label} key={index}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(<TextArea id="copy" rows={4} placeholder={item.placeHold} />)}
            {item.tooltip && (
              <Tooltip title={hdfsConfig} className="help-tooltip">
                <Icon type="question-circle-o" />
              </Tooltip>
            )}

            <Icon
              className="help-doc"
              type="copy"
              onClick={() => handleCopy(item)}
            />
          </Form.Item>
        );
      case 'RichText':
        return (
          <Form.Item label={item.label} key={index}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(<p>{item.defaultValue}</p>)}
          </Form.Item>
        );
      case 'Upload':
        return (
          <Form.Item label={item.label} key={index}>
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
                  <div style={{ display: 'flex' }}>
                    <Upload>
                      <Button>
                        <Icon type="upload" /> Click to upload
                      </Button>
                      <p style={{marginTop: 8}}>上传单个文件，支持扩展格式：.zip</p>
                    </Upload>
                    <p>上传单个文件，支持扩展格式：.zip</p>
                    <div style={{ marginLeft: -40 }}>
                      <Icon type="question-circle" />
                    </div>
                  </div>
                )}
              </div>
            )}
          </Form.Item>
        );
      case 'Password':
        return (
          <Form.Item label={item.label} key={index}>
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
      case 'Radio':
        return (
          <Form.Item label={item.label} key={index}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(
              <Radio.Group>
                <Radio value={1}>默认</Radio>
                <Radio value={2}>自定义</Radio>
              </Radio.Group>
            )}
          </Form.Item>
        );
      case 'Integer':
        return (
          <Form.Item label={item.label} key={index}>
            {getFieldDecorator(
              `${item.name}`,
              getRules(item)
            )(<InputNumber autoComplete="off" style={{ width: '100%' }} />)}
          </Form.Item>
        );
      case 'Switch':
        return (
          <Form.Item label={item.label} key={index}>
            {getFieldDecorator(`${item.name}`, getRules(item))(<Switch />)}
          </Form.Item>
        );
      // 定制化内容
      case 'Kerberos':
        return (
          <>
            <Form.Item label={item.label} key={index}>
              {getFieldDecorator(`${item.name}`, {
                valuePropName: 'checked',
                initialValue: detailData?.openKerberos || false,
              })(<Switch />)}
            </Form.Item>

            {getFieldValue('openKerberos') && uploadForm()}

            {getFieldValue('kerberosFile') && getFieldValue('openKerberos') && (
              <Form.Item label="Kerberos Principal" key="principal">
                {getFieldDecorator('principal', {
                  rules: [
                    {
                      required: true,
                      message: 'Kerberos Principal不可为空',
                    },
                  ],
                  initialValue:
                    detailData?.principal || principalsList[0] || '',
                })(<Select>{principalsOptions}</Select>)}
              </Form.Item>
            )}
          </>
        );
      case 'HbaseKerberos':
        return (
          <>
            <Form.Item label="开启Kerberos认证" key={index}>
              {getFieldDecorator('openKerberos', {
                valuePropName: 'checked',
                initialValue: detailData?.openKerberos || false,
              })(<Switch />)}
            </Form.Item>

            {getFieldValue('openKerberos') && uploadForm()}

            {getFieldValue('kerberosFile') && getFieldValue('openKerberos') && (
              <>
                <Form.Item label="client.principal">
                  {getFieldDecorator('principal', {
                    initialValue:
                      detailData?.principal || principalsList[0] || '',
                    rules: [
                      {
                        required: true,
                        message: 'client.principal不能为空',
                      },
                    ],
                  })(<Select>{principalsOptions}</Select>)}
                </Form.Item>
                <Form.Item label="master.kerberos">
                  {getFieldDecorator('hbase_master_kerberos_principal', {
                    initialValue: detailData?.hbase_master_kerberos_principal,
                    rules: [
                      {
                        required: true,
                        message: 'master.kerberos不能为空',
                      },
                      {
                        max: 128,
                        message: 'master.kerberos不可超过128个字符',
                      },
                    ],
                  })(<Input autoComplete="off" />)}
                </Form.Item>
                <Form.Item label="regioserver.kerberos">
                  {getFieldDecorator('hbase_regionserver_kerberos_principal', {
                    initialValue:
                      detailData?.hbase_regionserver_kerberos_principal,
                    rules: [
                      {
                        required: true,
                        message: 'regioserver.kerberos不能为空',
                      },
                      {
                        max: 128,
                        message: 'regioserver.kerberos不可超过128个字符',
                      },
                    ],
                  })(<Input autoComplete="off" />)}
                </Form.Item>
              </>
            )}
          </>
        );
      case 'FtpReact':
        return (
          <>
            <Form.Item label="协议" key={index}>
              {getFieldDecorator('protocol', {
                initialValue: detailData?.protocol || 'FTP',
                rules: [
                  {
                    required: true,
                    message: '协议不能为空',
                  },
                ],
              })(
                <Radio.Group>
                  <Radio value="FTP">FTP</Radio>
                  <Radio value="SFTP">SFTP</Radio>
                </Radio.Group>
              )}
            </Form.Item>
            {getFieldValue('protocol') === 'FTP' && (
              <Form.Item label="连接模式">
                {getFieldDecorator('connectMode', {
                  initialValue: detailData?.connectMode || 'PORT',
                  rules: [
                    {
                      required: true,
                      message: '连接模式不能为空',
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

            {getFieldValue('protocol') === 'SFTP' && (
              <>
                <Form.Item label="认证方式">
                  {getFieldDecorator('auth', {
                    initialValue: detailData?.auth || 1,
                    rules: [
                      {
                        required: true,
                        message: '认证方式不能为空',
                      },
                    ],
                  })(
                    <Radio.Group>
                      <Radio value={1}>密码</Radio>
                      <Radio value={2}>私钥</Radio>
                    </Radio.Group>
                  )}
                </Form.Item>

                {getFieldValue('auth') === 2 && (
                  <Form.Item label="私钥地址">
                    {getFieldDecorator('rsaPath', {
                      initialValue: '~/.ssh/id_rsa',
                      rules: [
                        {
                          required: true,
                          message: '私钥地址不能为空',
                        },
                      ],
                    })(<Input autoComplete="off" />)}
                    <Tooltip
                      title="用户的私钥储存路径，默认为~/.ssh/id_rsa"
                      arrowPointAtCenter>
                      <Icon className="help-doc" type="question-circle-o" />
                    </Tooltip>
                  </Form.Item>
                )}
              </>
            )}
          </>
        );
      case 'CarbonReact':
        return (
          <>
            <Form.Item label="HDFS配置" key={index}>
              {getFieldDecorator('hdfsCustomConfig', {
                initialValue: detailData?.hdfsCustomConfig || 'default',
              })(
                <Radio.Group>
                  <Radio value="default">默认</Radio>
                  <Radio value="custom">custom</Radio>
                </Radio.Group>
              )}
            </Form.Item>

            {getFieldValue('hdfsCustomConfig') === 'custom' && (
              <>
                <Form.Item label="defaultFS">
                  {getFieldDecorator('defaultFS', {
                    initialValue: detailData?.defaultFS || '',
                    rules: [
                      {
                        required: true,
                        message: 'defaultFS不能为空',
                      },
                    ],
                  })(
                    <Input placeholder="hdfs://host:port" autoComplete="off" />
                  )}
                </Form.Item>

                <Form.Item label="高可用配置">
                  {getFieldDecorator('hadoopConfig', {
                    initialValue: detailData?.hadoopConfig || '',
                  })(<TextArea id="copy" rows={4} placeholder={HDFSCONG} />)}
                  <Tooltip title={hdfsConfig} className="help-tooltip">
                    <Icon type="question-circle-o" />
                  </Tooltip>
                  <Icon
                    className="help-doc"
                    type="copy"
                    onClick={() =>
                      handleCopy({
                        label: '高可用配置',
                        placeHold: HDFSCONG,
                      })
                    }
                  />
                </Form.Item>
              </>
            )}
          </>
        );
      case 'RedisReact':
        return (
          <>
            <Form.Item label="模式" key="redisType">
              {getFieldDecorator('redisType', {
                initialValue: detailData?.redisType || 1,
                rules: [
                  {
                    required: true,
                    message: '模式不能为空',
                  },
                ],
              })(
                <Radio.Group>
                  <Radio value={1}>单机</Radio>
                  <Radio value={3}>集群</Radio>
                  <Radio value={2}>哨兵</Radio>
                </Radio.Group>
              )}
            </Form.Item>
            <Form.Item label="地址" key="hostPort">
              {getFieldDecorator('hostPort', {
                initialValue: detailData?.hostPort || '',
                rules: [
                  {
                    required: true,
                    message: '地址不能为空',
                  },
                ],
              })(
                <TextArea
                  rows={4}
                  placeholder={
                    getFieldValue('redisType') === 1
                      ? 'Redis地址，例如：IP1:Port'
                      : 'Redis地址，例如：IP1:Port，多个地址以英文逗号分开'
                  }
                />
              )}
            </Form.Item>
            {getFieldValue('redisType') === 2 && (
              <Form.Item label="master名称" key="masterName">
                {getFieldDecorator('masterName', {
                  initialValue: detailData?.masterName || '',
                  rules: [
                    {
                      required: true,
                      message: 'master名称不能为空',
                    },
                  ],
                })(<Input placeholder="请输入master名称" autoComplete="off" />)}
              </Form.Item>
            )}
            {(getFieldValue('redisType') === 1 ||
              getFieldValue('redisType') === 2) && (
              <Form.Item label="数据库">
                {getFieldDecorator('database', {
                  initialValue: detailData?.database || '',
                })(<Input autoComplete="off" />)}
              </Form.Item>
            )}
            <Form.Item label="密码">
              {getFieldDecorator('password', {
                initialValue: detailData?.password || '',
              })(<Input.Password />)}
            </Form.Item>
          </>
        );
      case 'WebSocketSub':
        return (
          <Form.Item label="鉴权参数" key="webSocketParams">
            {renderWebSocketParams()}
            <span className="ws-add" onClick={addWsParams}>
              <Icon type="plus-circle-o" />
              <span>新增参数</span>
            </span>
          </Form.Item>
        );

      default:
        break;
    }
  });

  return (
    <div className="info-config">
      <Spin spinning={loading}>
        <Form {...formItemLayout} className="info-config-form">
          <Form.Item label="数据源类型">
            {getFieldDecorator('dataType', {
              initialValue: otherParams.dataType + otherParams.dataVersion,
              rules: [
                {
                  required: true,
                  message: '数据源类型不能为空',
                },
              ],
            })(<Input disabled />)}
          </Form.Item>

          {formItem}
        </Form>
      </Spin>
    </div>
  );
};
export default Form.create<IProps>({})(withRouter(InfoConfig));
