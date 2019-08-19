import * as React from 'react';
import { Modal, Form, Input, message, Button } from 'antd'
import { connect } from 'react-redux';

import { apiMarketActions } from '../../actions/apiMarket';
import { formItemLayout } from '../../consts';

const FormItem = Form.Item;
const TextArea = Input.TextArea

const mapDispatchToProps = (dispatch: any) => ({
    apiApply (apiId: any, applyContent: any) {
        return dispatch(apiMarketActions.apiApply({ apiId: apiId, applyContent: applyContent }));
    },
    getApiExtInfo (tagId: any) {
        dispatch(
            apiMarketActions.getApiExtInfo({
                tagId: tagId
            })
        )
    }
});

@(connect(null, mapDispatchToProps) as any)
class ApplyBox extends React.Component<any, any> {
    state: any = {
        loading: false
    }

    handleOk () {
        this.props.form.validateFields((err: any, values: any) => {
            if (!err) {
                this.setState({
                    loading: true
                })
                this.props.apiApply(this.props.apiId, values.applyMsg)
                    .then(
                        (res: any) => {
                            this.setState({
                                loading: false
                            })
                            // this.props.getApiExtInfo(this.props.tagId);
                            if (this.props.getMarketApi) {
                                this.props.getMarketApi();
                            }

                            if (res) {
                                message.success('操作成功')
                                this.props.successCallBack();
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
    render () {
        const { getFieldDecorator } = this.props.form;
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
                        </Button>
                    ]}
                >

                    <Form>
                        <FormItem
                            label="标签名称"
                            {...formItemLayout}
                        >
                            <p>{this.props.name}</p>
                        </FormItem>

                        <FormItem
                            label="标签描述"
                            {...formItemLayout}
                        >
                            <p>{this.props.desc}</p>
                        </FormItem>

                        <FormItem
                            label="申请说明"
                            hasFeedback
                            required
                            {...formItemLayout}
                        >
                            {
                                getFieldDecorator('applyMsg', {
                                    rules: [{
                                        required: true,
                                        message: '请输入申请信息'
                                    }, {
                                        max: 200,
                                        message: '最大字符不能超过200'
                                    }]
                                })(
                                    <TextArea
                                        placeholder="请输入申请信息"
                                        autosize={{ minRows: 4, maxRows: 8 }}
                                    />
                                )
                            }
                        </FormItem>
                    </Form>
                </Modal>

            </div>

        )
    }
}
const WrappedApplyBox = Form.create<any>()(ApplyBox);
export default WrappedApplyBox;
