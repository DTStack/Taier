import React from 'react';
import { Form, Select, Input } from 'antd';
import { EnumFormItemType, IFormItem } from './types';

interface IPropsFormRender {
  formList: IFormItem[];
  form: any;
}

const getComponentByFormItemType = (type: EnumFormItemType) => {
  switch(type) {
    case EnumFormItemType.INPUT:
      return Input;
    case EnumFormItemType.SELECT:
      return Select;
    case EnumFormItemType.TEXT_AREA:
      return Input.TextArea;
  }
}

const FormRender = (props: IPropsFormRender) => {
  const { formList, form } = props;
  return (
    <>
      {
        formList.map(item => {
          const FormComponent = getComponentByFormItemType(item.type);
          const isRequired = item.rules && item.rules.findIndex(rule => rule.required === true) > -1;
          return (
            <Form.Item required={isRequired} label={item.label}>
              {
                form.getFieldDecorator(item.key, {
                  rules: item.rules,
                })(
                  <FormComponent className={`form-item-${item.type}`} placeholder={item.placeholder}>
                    {
                      FormComponent === Select && item.options ? (
                        item.options.map(option => (
                          <Select.Option key={option.key} value={option.value}>
                            {option.label}
                          </Select.Option>
                        ))
                      ) : null
                    }
                  </FormComponent>
                )
              }
            </Form.Item>
          )
        })
      }
    </>
  )
}

export default FormRender;
