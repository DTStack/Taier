import * as React from 'react';
import { connect } from 'react-redux';
import { hashHistory } from 'react-router';
import moment from 'moment';

import { Modal, Form, Input, message, Button } from 'antd'
import CallCountFormItem from '../callCountFormItem';
import CallDateRangeFormItem from '../callDateRangeFormItem';

import { apiMarketActions } from '../../actions/apiMarket';

const FormItem = Form.Item;
const TextArea = Input.TextArea

const formLayout: any = {
    labelCol: {
        sm: 6, xs: 24
    },
    wrapperCol: {
        sm: 18, xs: 24
    }
}
let modal: any;

const mapDispatchToProps = (dispatch: any) => ({
    apiApply (apiId: any, applyContent: any, callLimit: any, callTime: any) {
        return dispatch(apiMarketActions.apiApply({
            apiId: apiId,
            applyContent: applyContent,
            callLimit: callLimit,
            beginTime: callTime && callTime.length > 1 && callTime[0].valueOf(),
            endTime: callTime && callTime.length > 1 && callTime[1].valueOf()
        }));
    },
    getApiExtInfo (apiId: any) {
        dispatch(
            apiMarketActions.getApiExtInfo({
                apiId: apiId
            })
        )
    }
});

@(connect(null, mapDispatchToProps) as any)
class ApplyBox extends React.Component<any, any> {
    state: any = {
        loading: false
    }
    handleSubmit (values: any) {
        console.log(values)
    }
    handleOk () {
        const { hideJump } = this.props;

        this.props.form.validateFields((err: any, values: any) => {
            if (!err) {
                this.setState({
                    loading: true
                })
                const callDateRange = values.callDateRange;
                this.props.apiApply(this.props.apiId, values.applyMsg, values.callCount, callDateRange && callDateRange.length ? callDateRange : null)
                    .then(
                        (res: any) => {
                            this.setState({
                                loading: false
                            })

                            if (res.code == 1) {
                                this.props.getApiExtInfo(this.props.apiId);
                                this.props.form.resetFields();
                                this.props.successCallBack();
                                message.success('操作成功')
                                if (!hideJump) {
                                    this.showApplySuccessModal();
                                }
                            }
                        }
                    )
            }
        });
    }
    handleCancel () {
        this.props.form.resetFields();
        this.props.cancelCallback();
    }
    showApplySuccessModal () {
        modal = Modal.success({
            title: '申请提交成功',
            content: (
                <span>您可以在 <a onClick={this.jumpToMine.bind(this)}>我的API</a> 中查看审批进度</span>
            ),
            okText: '确定'
        });
    }
    jumpToMine () {
        if (modal) {
            modal.destroy();
        }

        hashHistory.push('/api/mine/myApi');
    }
    changeDateMode (evt: any) {
        this.props.form.resetFields(['callDateRange'])
        this.setState({
            dateMode: evt.target.checked
        })
    }
    disabledDate = (current: any) => {
        return current && current.valueOf() < moment().subtract(1, 'days').valueOf();
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        return (
            <Modal
                title="申请API"
                visible={this.props.show}
                onOk={this.handleOk.bind(this)}
                onCancel={this.handleCancel.bind(this)}
                footer={[
                    <Button key="back" size="large" onClick={this.handleCancel.bind(this)}>取消</Button>,
                    <Button key="submit" type="primary" size="large" loading={this.state.loading} onClick={this.handleOk.bind(this)}>
                        提交
                    </Button>
                ]}
            >

                <Form onSubmit={this.handleSubmit.bind(this)}>
                    <FormItem
                        label="API名称"
                        hasFeedback
                        {...formLayout}
                    >
                        {this.props.apiName}
                    </FormItem>
                    <CallCountFormItem
                        form={this.props.form}
                        formItemLayout={formLayout}
                    />
                    <CallDateRangeFormItem
                        form={this.props.form}
                        formItemLayout={formLayout}
                    />
                    <FormItem
                        label="申请说明"
                        required
                        hasFeedback
                        {...formLayout}
                    >
                        {getFieldDecorator('applyMsg',
                            {
                                rules: [
                                    { required: true, message: '请输入申请信息' },
                                    { max: 200, message: '最大字符不能超过200' }]
                            })(<TextArea style={{ width: 300 }} rows={4} />)}

                    </FormItem>

                </Form>
            </Modal>
        )
    }
}
const WrappedApplyBox = Form.create<any>()(ApplyBox);
export default WrappedApplyBox;
