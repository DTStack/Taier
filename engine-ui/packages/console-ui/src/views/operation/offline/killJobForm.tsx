import * as React from 'react';
import { Modal, Form, DatePicker, Checkbox, Row, Col, Alert, message, Select, Radio } from 'antd';
import moment from 'moment';
import Api from '../../../api/operation'
const FormItem = Form.Item;
const RangePicker = DatePicker.RangePicker
const CheckboxGroup = Checkbox.Group;
const Option = Select.Option;
const RadioGroup = Radio.Group
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
const reqParams = {
    currentPage: 1,
    pageSize: 200,
    searchType: 'front'
}
class KillJobForm extends React.Component<any, any> {
    state: any = {
        indeterminate: false,
        checkAll: false,
        submitLoading: false,
        taskList: []
    }
    componentDidMount = () => {
        this.searchTask()
    }
    getSchedulingOptions = () => {
        return [
        //     {
        //     label: '分钟', value: 0
        // }, {
        //     label: '小时', value: 1
        // },
            {
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
        const { validateFields, getFieldValue } = this.props.form
        validateFields((err: any, values: any) => {
            if (!err) {
                const { appType, projectId } = this.props
                const type = getFieldValue('select')
                const { taskIds } = values
                if (type === 2 && (!taskIds || (Array.isArray(taskIds) && taskIds.length === 0))) {
                    return message.error('请选择任务');
                }
                this.setState({
                    submitLoading: true
                })
                Api.batchStopJobByDate({
                    appType,
                    projectId,
                    type: 0,
                    taskIds: values.taskIds || undefined,
                    taskPeriodId: values.schedulingCycle ? values.schedulingCycle.join(',') : undefined,
                    bizStartDay: values.businessDate ? values.businessDate[0].unix() : undefined,
                    bizEndDay: values.businessDate ? values.businessDate[1].unix() : undefined
                }).then((res: any) => {
                    this.setState({
                        submitLoading: false
                    })
                    if (res.code === 1) {
                        message.success(`取消了${res.data}个任务`);
                        this.props.autoFresh();
                        this.handleCancel();
                    }
                })
            }
        });
    }

    searchTask = async (value?: string) => {
        const { appType, projectId } = this.props
        const reg = new RegExp(/^[\u4E00-\u9FA5A-Za-z0-9_]+$/)
        const reqParam = { ...reqParams, appType, projectId }
        if (reg.exec(value) === null) return
        const params = value ? Object.assign({}, { name: value }, reqParam) : reqParam
        const res = await Api.queryOfflineTasks(params)
        if (!res) { return this.setState({ taskList: [] }) }
        this.setState({
            taskList: res?.data?.data || []
        })
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
        const { taskList } = this.state
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const type = getFieldValue('select')
        return (
            <Modal
                title="按业务日期杀实例"
                confirmLoading={this.state.submitLoading}
                visible={visible}
                width={650}
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
                        label="选择任务"
                    >
                        {getFieldDecorator('select', {
                            rules: [{
                                required: true, message: '请选择任务'
                            }],
                            initialValue: 1
                        })(
                            <RadioGroup>
                                <Radio value={1}>全部任务</Radio>
                                <Radio value={2}>指定任务</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                    {
                        type === 1 ? (
                            <>
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
                                                '昨天': [(moment() as any).subtract(2, 'days'), yesterDay],
                                                '最近7天': [(moment() as any).subtract(8, 'days'), yesterDay],
                                                '最近30天': [(moment() as any).subtract(31, 'days'), yesterDay]
                                            }}
                                        />
                                    )}
                                </FormItem>
                                <FormItem
                                    {...formItemLayout}
                                    label="调度周期"
                                >
                                    <Row>
                                        <Col span={4} style={{ paddingTop: 2 }}>
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
                            </>
                        ) : (
                            <>
                                <FormItem
                                    {...formItemLayout}
                                    label={(<></>)}
                                    colon={false}
                                >
                                    {getFieldDecorator('taskIds', {
                                    })(
                                        <Select filterOption={false} onSearch={this.searchTask} style={{ width: 286 }} mode="multiple" placeholder="输入任务名称搜索，可添加多个任务">
                                            {
                                                taskList.map(item => {
                                                    return (<Option key={item.taskId} value={item.taskId}>{item.name}</Option>)
                                                })
                                            }
                                        </Select>
                                    )}
                                </FormItem>
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
                                                '昨天': [(moment() as any).subtract(2, 'days'), yesterDay],
                                                '最近7天': [(moment() as any).subtract(8, 'days'), yesterDay],
                                                '最近30天': [(moment() as any).subtract(31, 'days'), yesterDay]
                                            }}
                                        />
                                    )}
                                </FormItem>
                            </>
                        )
                    }
                </Form>
            </Modal>
        );
    }
}

export default Form.create<any>()(KillJobForm);
