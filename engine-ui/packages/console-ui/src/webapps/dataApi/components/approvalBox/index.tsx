import * as React from 'react';
import moment from 'moment';
import { connect } from 'react-redux';

import { Modal, Form, Input, Button, message } from 'antd';
import { API_USER_STATUS } from '../../consts';
import CallCountFormItem from '../callCountFormItem';
import CallDateRangeFormItem from '../callDateRangeFormItem';

import api from '../../api/approval';
import { approvalActions } from '../../actions/approval';

const FormItem = Form.Item;
const TextArea = Input.TextArea;

const formLayout: any = {
    labelCol: {
        sm: 6, xs: 24
    },
    wrapperCol: {
        sm: 18, xs: 24
    }
}
const mapDispatchToProps = (dispatch: any) => ({
    handleApply (params: any) {
        return dispatch(approvalActions.handleApply(params));
    }
});

@(connect(null, mapDispatchToProps) as any)
class ApprovalModal extends React.Component<any, any> {
    // 审批操作
    sp (isPass: any) {
        const { data } = this.props;
        this.props.form.validateFields(
            (err: any, values: any) => {
                if (!err) {
                    const applyId = data.id;
                    const approvalContent = values.APIGroup;
                    this.props.handleApply({
                        applyId: applyId,
                        isPassed: isPass,
                        approvalContent: approvalContent
                    })
                        .then(
                            (res: any) => {
                                if (res.code == 1) {
                                    message.success('审批成功');
                                    this.props.onOk();
                                }
                            }
                        )
                }
            }
        )
    }
    editSp () {
        const { data } = this.props;
        this.props.form.validateFields(
            (err: any, values: any) => {
                if (!err) {
                    const applyId = data.id;
                    const approvalContent = values.APIGroup;
                    const callLimit = values.callCount;
                    const callTime = values.callDateRange;
                    const isTimeUnLimit = callTime && !callTime.length;
                    api.editHandleApply({
                        applyId: applyId,
                        approvalContent: approvalContent,
                        callLimit: callLimit,
                        beginTime: isTimeUnLimit ? null : callTime[0].valueOf(),
                        endTime: isTimeUnLimit ? null : callTime[1].valueOf()
                    })
                        .then(
                            (res: any) => {
                                if (res.code == 1) {
                                    message.success('修改成功');
                                    this.props.onOk();
                                }
                            }
                        )
                }
            }
        )
    }
    couldEditUserMsg () {
        const { data } = this.props;
        return data.status == API_USER_STATUS.PASS;
    }
    getModalFooter () {
        const { mode, onCancel, data } = this.props;
        const isHanding = data.status == API_USER_STATUS.IN_HAND;
        const couldEditUserMsg = this.couldEditUserMsg();
        if (isHanding && mode == 'approval') {
            return (
                <div>
                    <Button type="danger" onClick={this.sp.bind(this, false)}>拒绝</Button>
                    <Button type="primary" onClick={this.sp.bind(this, true)}>同意</Button>
                </div>
            )
        } else if (couldEditUserMsg) {
            return (
                <div>
                    <Button onClick={onCancel}>取消</Button>
                    <Button type="primary" onClick={this.editSp.bind(this)}>修改</Button>
                </div>
            );
        } else {
            return <Button type="primary" onClick={onCancel}>关闭</Button>
        }
    }
    render () {
        const { spVisible, mode, data = {}, onCancel } = this.props;
        const { getFieldDecorator } = this.props.form;
        const modalTitle = mode == 'view' ? '审批详情' : '授权审批';
        const isHanding = data.status == API_USER_STATUS.IN_HAND;
        const couldEditUserMsg = this.couldEditUserMsg();
        return (
            <Modal
                title={modalTitle}
                visible={spVisible}
                onCancel={onCancel}
                footer={this.getModalFooter()}
                key={data.id}
            >
                <Form>
                    <FormItem
                        {...formLayout}
                        label="API名称"
                    >
                        {data.apiName}
                    </FormItem>
                    <FormItem
                        {...formLayout}
                        label="申请人"
                    >
                        {data.applyUserName}
                    </FormItem>
                    <CallCountFormItem
                        disabled={!couldEditUserMsg}
                        form={this.props.form}
                        formItemLayout={formLayout}
                        initialValue={data.callLimit}
                    />
                    <CallDateRangeFormItem
                        disabled={!couldEditUserMsg}
                        form={this.props.form}
                        formItemLayout={formLayout}
                        initialValue={data.beginTime ? [moment(data.beginTime), moment(data.endTime)] : []}
                    />
                    <FormItem
                        {...formLayout}
                        label="申请说明"
                    >
                        <TextArea style={{ width: 300 }} rows={4} value={data.applyContent} disabled />
                    </FormItem>
                    <FormItem
                        {...formLayout}
                        label="审批说明"
                    >
                        {getFieldDecorator('APIGroup', {
                            rules: [
                                { required: true, message: '请填写审批说明' },
                                { max: 200, message: '最大字数不能超过200' }
                            ],
                            initialValue: data.replyContent
                        })(<TextArea disabled={!isHanding && !couldEditUserMsg} style={{ width: 300 }} rows={4} />)
                        }
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
export default Form.create<any>()(ApprovalModal);
