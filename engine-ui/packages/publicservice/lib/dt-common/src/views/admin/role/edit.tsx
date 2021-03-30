import * as React from 'react';
import { assign } from 'lodash';
import { Row, Col, Button, message, Spin } from 'antd';

import { Utils } from 'dt-utils';

import Api from '../../../api';
import { formItemLayout } from '../../../consts';
import { GoBack } from 'dt-react-component';
import { AppName } from '../../../components/display';
import '../../../styles/views/admin.scss';

import RoleForm from './form';

export default class RoleEdit extends React.Component<any, any> {
  form: any;
  state = {
    roleInfo: {},
    loading: false,
    app: Utils.getParameterByName('app'),
  };

  componentDidMount() {
    this.getRoleInfo();
  }

  goIndex = () => {
    this.props.router.go(-1);
  };

  submit = () => {
    const ctx = this;
    const app = this.state.app;

    ctx.form.validateFieldsAndScroll((err: any, roleData: any) => {
      if (!err) {
        const updateData = assign(this.state.roleInfo, roleData);

        Api.updateRole(app, updateData).then((res: any) => {
          if (res.code === 1) {
            message.success('角色更新成功！');
            ctx.goIndex();
          }
        });
      }
    });
  };

  getRoleInfo = () => {
    const ctx = this;
    const app = this.state.app;
    const { params } = ctx.props;

    ctx.setState({ loading: true });
    Api.getRoleInfo(app, { roleId: params.roleId }).then((res: any) => {
      if (res.code === 1) {
        ctx.setState({ roleInfo: res.data, loading: false });
      }
    });
  };

  render() {
    const { app, roleInfo } = this.state;
    return (
      <div className="box-1">
        <div className="box-card">
          <h1 className="card-title">
            <GoBack /> 查看 {AppName(app)}角色
          </h1>
          <Spin tip="Loading..." spinning={this.state.loading}>
            <article className="section">
              <RoleForm
                roleInfo={roleInfo}
                ref={(e: any) => (this.form = e)}
                isDisabled={true}
              />
              <Row>
                <Col {...formItemLayout.labelCol}></Col>
                <Col {...formItemLayout.wrapperCol}>
                  <Button type="primary" disabled onClick={this.submit}>
                    确认更新
                  </Button>
                  <Button style={{ marginLeft: '20px' }} onClick={this.goIndex}>
                    取消
                  </Button>
                </Col>
              </Row>
            </article>
          </Spin>
        </div>
      </div>
    );
  }
}
