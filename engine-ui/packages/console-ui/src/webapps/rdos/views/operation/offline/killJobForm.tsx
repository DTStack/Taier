import * as React from 'react';
import { Modal, Form, DatePicker, Checkbox, Row, Col, Alert, message } from 'antd';
import moment from 'moment';
import Api from '../../../api'
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker
const CheckboxGroup = Checkbox.Group;
const yesterDay = moment().subtract(1, 'days');
const formItemLayout: any = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
};
class KillJobForm extends React.Component<any, any> {
    state: any = {
        indeterminate: false,
        checkAll: false,
        submitLoading: false
    }
    getSchedulingOptions = () => {
        return [{
            label: '分钟', value: 0
        }, {
            label: '小时', value: 1
        }, {
            label: '天', value: 2
        }, {
            label: '周', value: 3
        }, {
            label: '月', value: 4
        }]
    }
    disabledDate = (current: any) => {
        return current && current.valueOf() > moment().subtract(1, 'days').valueOf();
    }
    // 调度周期全选触发函数
    handleSelectAll = (e: any) => {
        const checked = e.target.checked;
        this.props.form.setFieldsValue({
            'schedulingCycle': checked ? this.getSchedulingOptions().map((o: any) => o.value) : []
        })
        this.setState({
            indeterminate: false,
            checkAll: checked
        });
    }
    // 调度周期
    handleSechedulingChange = (checkedList: any) => {
        const indeterminate = !!checkedList.length && (checkedList.length < this.getSchedulingOptions().length);
        const checkAll = checkedList.length === this.getSchedulingOptions().length;
        this.setState({
            indeterminate: indeterminate,
            checkAll: checkAll
        });
    }
    handleSubmit = () => {
        this.props.form.validateFields((err: any, values: any) => {
            if (!err) {
                this.setState({
                    submitLoading: true
                })
                Api.batchStopJobByDate({
                    type: 0,
                    taskPeriodId: values.schedulingCycle.join(','),
                    bizStartDay: values.businessDate[0].unix(),
                    bizEndDay: values.businessDate[1].unix()
                }).then((res: any) => {
                    this.setState({
                        submitLoading: false
                    })
                    if (res.code === 1) {
                        message.success(res.data);
                        this.props.autoFresh();
                        this.handleCancel();
                    }
                })
            }
        });
    }
    handleCancel = () => {
        this.setState({
            indeterminate: false,
            checkAll: false
        })
        this.props.form.resetFields();
        this.props.onCancel();
    }
    render () {
        const { visible } = this.props;
        const { getFieldDecorator } = this.props.form;
        return (
            <Modal
                title="按业务日期杀实例"
                confirmLoading={this.state.submitLoading}
                visible={visible}
                width={600}
                onOk={this.handleSubmit}
                onCancel={this.handleCancel}
                okText='杀任务'
            >
                <Alert
                    description="根据业务日期和调度周期来快速筛选大量实例，例如选择业务日期在2018-01-01~2018-01-20的分钟任务实例"
                    message=""
                    type="info"
                    showIcon
                    style={{ padding: '5px 16px 5px 60px', marginBottom: 20 }}
                />
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="业务日期"
                    >
                        {getFieldDecorator('businessDate', {
                            rules: [{
                                required: true, message: '请输入业务日期'
                            }]
                        })(
                            <RangePicker
                                size="default"
                                format="YYYY-MM-DD"
                                allowClear={false}
                                disabledDate={this.disabledDate}
                                ranges={{
                                    '昨天': [moment().subtract(2, 'days'), yesterDay],
                                    '最近7天': [moment().subtract(8, 'days'), yesterDay],
                                    '最近30天': [moment().subtract(31, 'days'), yesterDay]
                                }}
                            />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="调度周期"
                    >
                        <Row>
                            <Col span={4}>
                                <Checkbox
                                    checked={this.state.checkAll}
                                    indeterminate={this.state.indeterminate}
                                    onChange={this.handleSelectAll}
                                >
                                    全选
                                </Checkbox>
                            </Col>
                            <Col span={20}>
                                {getFieldDecorator('schedulingCycle', {
                                    rules: [{
                                        required: true, message: '请选择调度周期'
                                    }]
                                })(
                                    <CheckboxGroup options={this.getSchedulingOptions()} onChange={this.handleSechedulingChange} />
                                )}
                            </Col>
                        </Row>
                    </FormItem>
                </Form>
            </Modal>
        );
    }
}

export default Form.create<any>()(KillJobForm);
