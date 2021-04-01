import React from 'react';
import { Form, Select, Input, Switch } from 'antd';
import { EnumFormItemType, IFormItem } from './types';
import RelationList from '../RelationList';

const WrapperSwitch = (props: any) => {
  const _props = { ...props };
  delete _props.value;
  return <Switch checked={props.value} {..._props} />;
};

interface IPropsFormRender {
  formList: IFormItem[];
  form: any;
}

const getComponentByFormItemType = (type: EnumFormItemType) => {
  switch (type) {
    case EnumFormItemType.INPUT:
      return Input;
    case EnumFormItemType.SELECT:
      return Select;
    case EnumFormItemType.TEXT_AREA:
      return Input.TextArea;
    case EnumFormItemType.SWITCH:
      return WrapperSwitch;
    case EnumFormItemType.RELATION_LIST:
      return RelationList;
  }
};

const FormRender = (props: IPropsFormRender) => {
  const { formList, form } = props;
  return (
    <>
      {formList.map((item) => {
        const FormComponent = getComponentByFormItemType(item.type);
        const isRequired =
          item.rules &&
          item.rules.findIndex((rule) => rule.required === true) > -1;
        const className = `form-item-${item.type}`;
        const visible = item.visible === undefined ? true : item.visible;
        const ext = item.ext ? item.ext : {};
        return visible && item.label !== '' ? (
          <Form.Item key={item.key} required={isRequired} label={item.label}>
            {form.getFieldDecorator(item.key, {
              rules: item.rules,
              validateTrigger: 'onBlur',
            })(
              <FormComponent
                className={className}
                placeholder={item.placeholder}
                {...ext}>
                {FormComponent === Select && item.options
                  ? item.options.map((option) => (
                      <Select.Option
                        key={option.key}
                        value={option.value}
                        data-ext={option.ext}>
                        {option.label}
                      </Select.Option>
                    ))
                  : null}
              </FormComponent>
            )}
          </Form.Item>
        ) : // 非form组件，不渲染Form.Item
        visible ? (
          <FormComponent key={item.key} className={className} {...ext} />
        ) : null;
      })}
    </>
  );
};

export default FormRender;
