import * as React from 'react';
import { Modal, Form, Select, Button, message } from 'antd';
import { Cookie } from 'dt-utils';

import Api from '../../api';
import _ from 'lodash';
import './style.scss';

declare var window: any;
declare var APP_CONF: any;
const UIC_URL_TARGET = APP_CONF.UIC_URL || '';

const FormItem = Form.Item;
const Option = Select.Option;

const formItemLayout = {
  labelCol: {
    xs: { span: 2 },
    sm: { span: 6 },
  },
  wrapperCol: {
    xs: { span: 22 },
    sm: { span: 15 },
  },
};

class TenantModal extends React.Component<any, any> {
  constructor(props: any) {
    super(props);
    this.state = {
      curTenantId: Cookie.getCookie('dt_tenant_id') || '',
      tenants: [],
    };
  }
  componentDidMount() {
    this.init();
  }

  init = () => {
    Api.getFullTenants().then((data: any) => {
      let res = data.data || [];
      res = this.checkTenant(res);
      this.setTenants(res.reverse());
    });
  };

  setTenants = (tenants: any) => {
    this.setState({
      tenants: tenants,
    });
  };

  checkTenant = (data: any) => {
    const { curTenantId } = this.state;
    const curTenantName = Cookie.getCookie('dt_tenant_name') || '';
    const isHavaCurTenant = data.findIndex(
      (v: any) => v.tenantId === parseInt(curTenantId)
    );
    if (isHavaCurTenant === -1) {
      data.push({ tenantId: curTenantId, tenantName: curTenantName });
    }
    return data;
  };

  checkProductsRight = async () => {
    const data = await Api.getProducts();
    if (data.success) {
      const products = data.data || [];
      const right = products.filter((v: any) => v.productCode === 'RDOS');
      if (right.length === 1) {
        return true;
      } else {
        return false;
      }
    }
  };

  handleTenantSearch = async (value: any) => {
    const data = await Api.getFullTenants(value);
    if (data.success) {
      let res = data.data || [];
      if (!value) res = this.checkTenant(res);
      this.setState({
        tenants: res,
      });
    }
  };

  handleChangeTenSubmit = () => {
    const { form } = this.props;
    form.validateFields('change_ten_id', (err: any, values: any) => {
      if (err) {
        return console.log('Received values of form: ', values);
      } else {
        this.doTenantChange(values.change_ten_id);
      }
    });
  };

  doTenantChange = async (tenantId: any) => {
    const { closeTenantModal } = this.props;
    if (tenantId.toString() === this.state.curTenantId) {
      closeTenantModal();
      return;
    }
    if (tenantId.toString() !== this.state.curTenantId) {
      const data = await Api.switchTenants({ tenantId: tenantId });
      if (data.success) {
        window.isSwitchTenant = true;
        const isHaveRight = await this.checkProductsRight();
        if (isHaveRight) {
          window.location.href = `${location.origin}/portal/`;
        } else {
          window.location.href = UIC_URL_TARGET;
        }
      } else {
        message.error(data.data.message);
      }
    }
  };

  render() {
    const { showTanantModal, closeTenantModal, apps } = this.props;
    const { getFieldDecorator } = this.props.form;
    const { tenants } = this.state;
    console.log(apps);
    return (
      <Modal
        className="c-tenantModal__modal"
        maskClosable={false}
        width={520}
        visible={showTanantModal}
        onCancel={closeTenantModal}
        footer={[
          <Button
            key="back"
            className="c-tenantModal__modal__btn"
            onClick={closeTenantModal}>
            取消
          </Button>,
          <Button
            key="submit"
            type="primary"
            className="c-tenantModal__modal__btn"
            style={{ marginLeft: 15 }}
            onClick={this.handleChangeTenSubmit}>
            确定
          </Button>,
        ]}>
        <Form className="c-tenantModal__form">
          <p className="title">选择租户</p>
          <FormItem
            {...formItemLayout}
            style={{ marginBottom: 0 }}
            label="租户名称">
            {getFieldDecorator('change_ten_id', {
              rules: [],
              initialValue: `${this.state.curTenantId}`,
            })(
              <Select
                showSearch
                onSearch={_.debounce(this.handleTenantSearch, 200)}
                style={{ width: 310 }}
                filterOption={false}>
                {tenants &&
                  tenants.map((o: any) => (
                    <Option key={o.tenantId} value={`${o.tenantId}`}>
                      {o.tenantName}
                    </Option>
                  ))}
              </Select>
            )}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}

export default Form.create<any>()(TenantModal);
