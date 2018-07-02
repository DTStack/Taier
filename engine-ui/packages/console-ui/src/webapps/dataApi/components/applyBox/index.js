import React, { Component } from "react";
import { Modal, Form, Input, message, Button, DatePicker, InputNumber, Checkbox } from "antd"
import { connect } from "react-redux";
import { hashHistory } from "react-router";
import moment from "moment";

import { apiMarketActions } from '../../actions/apiMarket';

const FormItem = Form.Item;
const TextArea = Input.TextArea
const RangePicker = DatePicker.RangePicker;

const formLayout = {
    labelCol: {
        sm: 6, xs: 24
    },
    wrapperCol: {
        sm: 18, xs: 24
    }
}
let modal;

const mapDispatchToProps = dispatch => ({
    apiApply(apiId, applyContent,callLimit,callTime) {
        return dispatch(apiMarketActions.apiApply({ 
            apiId: apiId, 
            applyContent: applyContent,
            callLimit:callLimit,
            beginTime:callTime&&callTime.length>1&&callTime[0].valueOf(),
            endTime:callTime&&callTime.length>1&&callTime[1].valueOf()
         }));
    },
    getApiExtInfo(apiId) {
        dispatch(
            apiMarketActions.getApiExtInfo({
                apiId: apiId
            })
        )
    }
});

@connect(null, mapDispatchToProps)
class ApplyBox extends Component {
    state = {
        loading: false,
        countMode:false,
        dateMode:false
    }
    handleSubmit(values) {
        console.log(values)
    }
    handleOk() {
        const {countMode, dateMode} = this.state;
        const {hideJump} = this.props;

        this.props.form.validateFields((err, values) => {
            if (!err) {
                this.setState({
                    loading: true
                })
                this.props.apiApply(this.props.apiId, values.applyMsg,countMode?-1:values.callCount,dateMode?null:values.callDateRange)
                    .then(
                        (res) => {
                            this.setState({
                                loading: false
                            })

                            if (res) {
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
    handleCancel() {
        this.props.form.resetFields();
        this.props.cancelCallback();
    }
    showApplySuccessModal() {
        modal = Modal.success({
            title: '申请提交成功',
            content: (
                <span>您可以在 <a onClick={this.jumpToMine.bind(this)}>我的API</a> 中查看审批进度</span>
            ),
            okText: "确定"
        });
    }
    jumpToMine() {
        if (modal) {
            modal.destroy();
        }

        hashHistory.push("/api/mine");
    }
    changeCountMode(evt){
        this.props.form.resetFields(['callCount'])
        this.setState({
            countMode:evt.target.checked
        })
    }
    changeDateMode(evt){
        this.props.form.resetFields(['callDateRange'])
        this.setState({
            dateMode:evt.target.checked
        })
    }
    disabledDate = (current) => {
        return current && current.valueOf() < moment().subtract(1, 'days').valueOf();
    }
    render() {
        const { getFieldDecorator } = this.props.form;
        const { countMode, dateMode } = this.state;
        return (
            <div>

                <Modal
                    title="申请API"
                    visible={this.props.show}
                    onOk={this.handleOk.bind(this)}
                    onCancel={this.handleCancel.bind(this)}
                    footer={[
                        <Button key="back" size="large" onClick={this.handleCancel.bind(this)}>取消</Button>,
                        <Button key="submit" type="primary" size="large" loading={this.state.loading} onClick={this.handleOk.bind(this)}>
                            提交
                        </Button>,
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
                        <FormItem
                            label="调用次数"
                            {...formLayout}
                        >
                            {getFieldDecorator('callCount', {
                                rules: [
                                    { required: !countMode, message: "请输入调用次数" },
                                ]
                            })(<InputNumber disabled={countMode} type="number" />)}
                             <Checkbox checked={countMode} onChange={this.changeCountMode.bind(this)}>不限制调用次数</Checkbox>
                        </FormItem>
                        <FormItem
                            label="调用周期"
                            {...formLayout}
                        >
                            {getFieldDecorator('callDateRange', {
                                rules: [{ required: !dateMode, message: "请选择调用周期" }]
                            })(<RangePicker disabledDate={this.disabledDate} disabled={dateMode} style={{width:"220px",verticalAlign:"middle",marginRight:"8px"}} popupStyle={{fontSize:"14px"}} />)}
                            <Checkbox checked={dateMode} onChange={this.changeDateMode.bind(this)}>不限制调用时间</Checkbox>
                        </FormItem>
                        <FormItem
                            label="申请说明"
                            required
                            hasFeedback
                            {...formLayout}
                        >
                            {getFieldDecorator('applyMsg',
                                {
                                    rules: [{ required: true, message: '请输入申请信息' },
                                    { max: 200, message: "最大字符不能超过200" },]
                                }, )(<TextArea style={{ width: 300 }} rows={4} />)}

                        </FormItem>

                    </Form>
                </Modal>

            </div>


        )
    }
}
const WrappedApplyBox = Form.create()(ApplyBox);
export default WrappedApplyBox;