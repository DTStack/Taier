import * as React from 'react';
import { Form, Tabs, Spin, Select, InputNumber, message } from 'antd';
import { formItemLayout } from './index';
import { MemorySetting as BaseMemorySetting } from './typeChange';
import { debounce, get } from 'lodash';
import { TASK_ENUM, COMPONENT_TYPE } from '../../../../../../consts';
import api from '../../../../../../api/experiment';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const Option = Select.Option;
const inputStyle: any = {
    width: '100%'
}
/* 字段设置 */
class FieldSetting extends React.PureComponent<any, any> {
    state: any = {
        columns: [],
        fetching: false
    }
    handleSubmit (name: any, value: any) {
        this.props.form.validateFieldsAndScroll([name], (err: any, values: any) => {
            if (!err) {
                this.props.handleSaveComponent(name, value);
            }
        });
    }
    getColumns = () => {
        if (this.state.columns.length > 0) {
            /**
             * 此处是为了减少请求次数
             */
            return;
        }
        const { currentTab, componentId } = this.props;
        const targetEdge = currentTab.graphData.find((o: any) => {
            return o.edge && o.target.data.id == componentId
        })
        if (targetEdge) {
            this.setState({
                fetching: true
            })
            api.getInputTableColumns({ taskId: componentId, inputType: targetEdge.inputType }).then((res: any) => {
                if (res.code === 1) {
                    let columns: any = [];
                    for (const key in res.data) {
                        if (res.data.hasOwnProperty(key)) {
                            const element = res.data[key];
                            columns.push({
                                key,
                                type: element
                            })
                        }
                    }
                    this.setState({
                        columns
                    })
                }
                this.setState({
                    fetching: false
                })
            })
        }
    }
    handleChange = (key: string, value: any) => {
        const { columns } = this.state;
        const object = columns.find((o: any) => o.key === value);
        if (object) {
            this.props.handleSaveComponent(key, object);
        }
    }
    render () {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { fetching, columns } = this.state;
        const { data } = this.props;
        console.log(getFieldValue('threshold'))

        const isRequired = !getFieldValue('threshold');
        console.log(isRequired)
        return (
            <Form className="params-form">
                <FormItem
                    colon={false}
                    label='原数据的标签列列名'
                    {...formItemLayout}
                >
                    {getFieldDecorator('label', {
                        rules: [{ required: true, message: '请选择原数据的标签列列名！' }]
                    })(
                        <Select
                            showSearch
                            notFoundContent={fetching ? <Spin size="small" /> : '未找到数据表'}
                            onFocus={this.getColumns}
                            placeholder="请选择原数据的标签列列名"
                            // onFocus={this.getColumns}
                            onChange={this.handleChange.bind(this, 'label')}
                        >
                            {columns.map((item: any, index: any) => {
                                const disabled = !!(data.col || []).find((o: any) => o.key === item.key);
                                return <Option key={item.key} value={item.key} disabled={disabled}>{item.key}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label={<div style={{ display: 'inline-block' }}>预测结果的标签列列名<span className="supplementary">若无阈值，必选项</span></div>}
                    {...formItemLayout}
                >
                    {getFieldDecorator('pre', {
                        rules: [{ required: isRequired, message: '请选择预测结果的标签列列名！' }]
                    })(
                        <Select
                            showSearch
                            notFoundContent={fetching ? <Spin size="small" /> : '未找到数据表'}
                            placeholder="请选择预测结果的标签列列名"
                            onFocus={this.getColumns}
                            // onFocus={this.getColumns}
                            onChange={this.handleChange.bind(this, 'pre')}
                        >
                            {columns.map((item: any, index: any) => {
                                const disabled = !!(data.col || []).find((o: any) => o.key === item.key);
                                return <Option key={item.key} value={item.key} disabled={disabled}>{item.key}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label={<div style={{ display: 'inline-block' }}>类别个数</div>}
                    {...formItemLayout}
                >
                    {getFieldDecorator('classes', {
                        rules: [{ required: true, message: '请填写类别个数！' }]
                    })(
                        <InputNumber style={inputStyle} />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label={<div style={{ display: 'inline-block' }}>阈值<span className="supplementary">可选项，大于该阈值的样本为正样本</span></div>}
                    {...formItemLayout}
                >
                    {getFieldDecorator('threshold', {
                        rules: [{ required: false, message: '请填写阈值！' }]
                    })(
                        <InputNumber style={inputStyle} />
                    )}
                </FormItem>
            </Form>
        )
    }
}
/* 内存设置 */
class MemorySetting extends BaseMemorySetting {
    constructor (props: any) {
        super(props)
    }
}
/* main页面 */
class ConfusionMatrix extends React.PureComponent<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    handleSaveComponent = (field: any, filedValue: any) => {
        const { currentTab, componentId, data, changeContent } = this.props;
        const fieldName = TASK_ENUM[COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX];
        const currentComponentData = currentTab.graphData.find((o: any) => o.vertex && o.data.id === componentId);
        const params: any = {
            ...currentComponentData.data,
            [fieldName]: {
                ...data
            }
        }
        if (field) {
            params[fieldName][field] = filedValue
        }
        api.addOrUpdateTask(params).then((res: any) => {
            if (res.code == 1) {
                currentComponentData.data = { ...params, ...res.data };
                changeContent({}, currentTab);
            } else {
                message.warning('保存失败');
            }
        })
    }
    render () {
        const { data, componentId, currentTab } = this.props;
        const WrapFieldSetting = Form.create({
            mapPropsToFields: (props: any) => {
                const { data } = props;
                const values: any = {
                    label: { value: get(data, 'label.key') },
                    pre: { value: get(data, 'pre.key', '') },
                    classes: { value: data.classes },
                    threshold: { value: data.threshold }
                }
                return values;
            },
            onFieldsChange: (props: any, changedFields: any) => {
                for (const key in changedFields) {
                    if (key === 'label' || key == 'pre') {
                        // label是下拉菜单，在组件里自己触发onChange函数,对数据封装过后再请求
                        continue;
                    }
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.validating && !element.dirty && element.name !== 'transferField') {
                            props.handleSaveComponent(key, element.value)
                        }
                    }
                }
            }
        })(FieldSetting)
        const WrapMemorySetting = Form.create({
            onFieldsChange: (props: any, changedFields: any) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.validating && !element.dirty) {
                            props.handleSaveComponent(key, element.value)
                        }
                    }
                }
            },
            mapPropsToFields: (props: any) => {
                const { data } = props;
                const values: any = {
                    workerMemory: { value: data.workerMemory },
                    workerCores: { value: data.workerCores }
                }
                return values;
            }
        })(MemorySetting);
        return (
            <Tabs type="card" className="params-tabs">
                <TabPane tab="字段设置" key="1">
                    <WrapFieldSetting data={data} handleSaveComponent={this.handleSaveComponent} currentTab={currentTab} componentId={componentId} />
                </TabPane>
                <TabPane tab="内存设置" key="2">
                    <WrapMemorySetting data={data} handleSaveComponent={this.handleSaveComponent} />
                </TabPane>
            </Tabs>
        );
    }
}

export default ConfusionMatrix;
