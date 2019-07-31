import * as React from 'react';
import { connect } from 'react-redux'
import { Modal, Form, message, Select } from 'antd';

import { formItemLayout, SECURITY_TYPE } from '../../consts'
import api from '../../api/apiManage';

const FormItem = Form.Item;
const Option = Select.Option;

@(connect((state: any) as any) => {
    const { apiManage } = state;
    return { apiManage }
})
class SecurityModal extends React.Component<any, any> {
    state: any = {
        loading: false
    }
    onOk = () => {
        const { form, apiId, onOk, closeModal } = this.props;
        const { validateFields } = form;
        validateFields((err: any, values: any) => {
            if (!err) {
                this.setState({
                    loading: true
                })
                api.updateLimiter({
                    apiId,
                    ...values
                }).then((res: any) => {
                    this.setState({
                        loading: false
                    })
                    if (res.code == 1) {
                        message.success('修改成功');
                        onOk();
                        closeModal();
                    }
                })
            }
        })
    }
    getSecurityListView () {
        const data = this.props.apiManage.securityList;
        return data.map(
            (item: any, index: any) => {
                return <Option
                    value={item.id}
                    key={item.id}
                >
                    {`${item.name} (${item.type == SECURITY_TYPE.BLACK ? '黑名单' : '白名单'})`}
                </Option>
            }
        )
    }
    exchangeDataToSelectData(data: any) {
        return data.map((item: any) => {
            return item.id
        })
    }
    render () {
        const { visible, closeModal, data, form } = this.props;
        const { loading } = this.state;
        const { getFieldDecorator } = form;
        return <Modal
            visible={visible}
            onCancel={closeModal}
            onOk={this.onOk}
            confirmLoading={loading}
            title="安全组"
        >
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="安全组"
                >
                    {getFieldDecorator('groupIdList', {
                        initialValue: this.exchangeDataToSelectData(data) || []
                    })(
                        <Select mode="multiple" optionFilterProp="children">
                            {this.getSecurityListView()}
                        </Select>
                    )}
                </FormItem>
            </Form>
        </Modal>
    }
}
export default Form.create<any>()(SecurityModal);
