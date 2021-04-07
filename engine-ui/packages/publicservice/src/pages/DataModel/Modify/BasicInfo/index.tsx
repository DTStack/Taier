import React, { useEffect, useImperativeHandle, useState } from 'react';
import { Form, Input, Select } from 'antd';
import { API } from '@/services';
import Message from '@/pages/DataModel/components/Message';
import { IModelDetail } from '@/pages/DataModel/types';
import { Mode } from 'node:fs';

const { Option } = Select;
const { TextArea } = Input;

interface IPropsBasicInfo {
  form?: any;
  cref: any;
  modelDetail?: Partial<IModelDetail>;
  globalStep?: number;
  mode?: Mode;
}

interface DataSourceItem {
  id: number;
  dsType: 1 | 2;
  dsTypeName: string;
  dsUrl: string;
  name: string;
}

const BasicInfo = (props: IPropsBasicInfo) => {
  const { form, cref, modelDetail, globalStep, mode } = props;
  const {
    getFieldDecorator,
    getFieldsValue,
    setFieldsValue,
    validateFields,
  } = form;
  const [dataSourceList, setDataSourceList] = useState<DataSourceItem[]>([]);
  const [extra, setExtra] = useState({});
  const isDisabled = mode === 'EDIT' && globalStep >= 0;
  useEffect(() => {
    setFieldsValue({
      modelName: modelDetail.modelName,
      modelEnName: modelDetail.modelEnName,
      dsId: modelDetail.dsId,
      remark: modelDetail.remark,
    });
  }, [modelDetail]);

  const fetchAllDataSourceList = async () => {
    try {
      const { success, data, message } = await API.getAllDataSourceList();
      if (success) {
        setDataSourceList(data);
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    }
  };

  useImperativeHandle(cref, () => {
    return {
      validate: () => {
        return new Promise((resolve, reject) => {
          validateFields((err, data) => {
            if (err) return reject(err.message);
            const _value = {
              ...data,
              ...extra,
            };
            return resolve(_value);
          });
        });
      },
      getValue: () => {
        const formData = getFieldsValue();
        const _value = {
          ...formData,
          ...extra,
        };
        return _value;
      },
    };
  });

  const repeatValidateGenerator = (options) => {
    const { fieldCode, msgTips, id } = options;
    return async (rule, value, callback) => {
      const { success, data, message } = await API.repeatValidate({
        fieldCode: fieldCode,
        value,
        id,
      });
      if (success && data) {
        callback(msgTips);
      } else if (success && !data) {
        callback();
      } else {
        callback(message);
      }
    };
  };

  useEffect(() => {
    fetchAllDataSourceList();
  }, []);

  return (
    <div className="padding-top-20" ref={cref}>
      <Form labelCol={{ span: 4 }} wrapperCol={{ span: 20 }}>
        <Form.Item label="模型名称">
          {getFieldDecorator('modelName', {
            validateTrigger: 'onBlur',
            rules: [
              { required: true, message: '请输入模型名称' },
              { max: 50, message: '不超过50个字符' },
              {
                pattern: /^[a-zA-Z0-9_\u4e00-\u9fa5]+$/g,
                message: '仅支持中文、字母、数字和下划线',
              },
              {
                validator: repeatValidateGenerator({
                  fieldCode: 1,
                  msgTips: '模型名称不能重复',
                  id: modelDetail.id,
                }),
              },
            ],
          })(<Input placeholder="请输入模型名称" />)}
        </Form.Item>
        <Form.Item label="模型英文名">
          {getFieldDecorator('modelEnName', {
            validateTrigger: 'onBlur',
            rules: [
              { required: true, message: '请输入模型英文名' },
              { max: 50, message: '不超过50个字符' },
              {
                pattern: /^[a-zA-Z0-9_]+$/g,
                message: '仅支持字母、数字和下划线',
              },
              {
                validator: repeatValidateGenerator({
                  fieldCode: 2,
                  msgTips: '模型英文名称不能重复',
                  id: modelDetail.id,
                }),
              },
            ],
          })(<Input disabled={isDisabled} placeholder="请输入模型英文名称" />)}
        </Form.Item>
        <Form.Item label="数据源">
          {getFieldDecorator('dsId', {
            rules: [{ required: true, message: '请选择数据源' }],
          })(
            <Select
              placeholder="请选择数据源"
              disabled={isDisabled}
              onChange={(value, target) => {
                const extraString = (target as any).props['data-ext'];
                try {
                  const extra = JSON.parse(extraString);
                  setExtra(extra);
                } catch (error) {
                  console.error(error);
                }
              }}>
              {dataSourceList.map((dataSource) => (
                <Option
                  key={dataSource.id}
                  value={dataSource.id}
                  data-ext={JSON.stringify(dataSource)}>
                  {dataSource.name}({dataSource.dsTypeName})
                </Option>
              ))}
            </Select>
          )}
        </Form.Item>
        <Form.Item label="备注">
          {getFieldDecorator('remark', {
            initialValue: '',
            rules: [{ max: 50, message: '不超过50个字符' }],
          })(<TextArea style={{ height: '92px' }} placeholder="请输入备注" />)}
        </Form.Item>
      </Form>
    </div>
  );
};

export default Form.create()(BasicInfo) as any;
