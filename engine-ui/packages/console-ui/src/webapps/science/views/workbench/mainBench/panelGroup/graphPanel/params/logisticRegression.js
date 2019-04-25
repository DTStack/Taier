import React, { PureComponent } from 'react';
import { Tabs, Form, Button, Select, InputNumber, message } from 'antd';
import { MemorySetting as BaseMemorySetting, ChooseModal as BaseChooseModal } from './typeChange';
import { formItemLayout } from './index';
import { isEmpty, cloneDeep, debounce } from 'lodash';
import api from '../../../../../../api/experiment';
const TabPane = Tabs.TabPane;
const Option = Select.Option;
const FormItem = Form.Item;
const inputStyle = {
    width: '100%'
}
/* 选择字段弹出框 */
class ChooseModal extends BaseChooseModal {
    constructor (props) {
        super(props);
        this.disabledType = 'string';
    }
    initTargetKeys = () => {
        const { data, transferField } = this.props;
        const { backupSource } = this.state;
        const chooseData = data.col || [];
        const targetKeys = chooseData.map((item) => {
            return item.key;
        });
        const sourceData = cloneDeep(backupSource);
        sourceData.forEach((item) => {
            if (targetKeys.findIndex(o => o === item.key) > -1) {
                item.type = transferField;
            }
        });
        this.setState({
            targetKeys,
            sourceData
        });
    }
}
/* 参数设置 */
class paramSetting extends PureComponent {
    state = {
        regexDatas: [{
            value: 'none',
            name: 'none'
        }, {
            value: 'L1',
            name: 'L1'
        }, {
            value: 'L2',
            name: 'L2'
        }]
    }
    handleChange = (value) => {
        const { columns } = this.state;
        const object = columns.find(o => o.key === value);
        if (object) {
            this.props.handleSaveComponent('label', object);
        }
    }
    render () {
        const { regexDatas } = this.state;
        const { getFieldDecorator } = this.props.form;
        return (
            <Form className="params-form">
                <FormItem
                    label='正则项'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('penalty', {
                        initialValue: 'none',
                        rules: [{ required: false }]
                    })(
                        <Select placeholder="请选择目标列" onChange={this.handleChange}>
                            {regexDatas.map((item, index) => {
                                return <Option key={index} value={item.value}>{item.name}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='最大迭代次数'
                    {...formItemLayout}
                >
                    {getFieldDecorator('max_iter', {
                        initialValue: 100,
                        rules: [
                            { required: false },
                            { max: 100000, message: '最大迭代次数为100000', type: 'number' }
                        ]
                    })(
                        <InputNumber
                            parser={value => value ? parseInt(value) : value}
                            formatter={value => value ? parseInt(value) : value}
                            style={inputStyle}
                        />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label={<div>正则系数<span className="supplementary">正则项为None时，此值无效</span></div>}
                    {...formItemLayout}
                >
                    {getFieldDecorator('c', {
                        initialValue: 1,
                        rules: [
                            { required: false },
                            { max: 100000, message: '正则系数最大为100000', type: 'number' }
                        ]
                    })(
                        <InputNumber
                            parser={value => value ? parseInt(value) : value}
                            formatter={value => value ? parseInt(value) : value}
                            style={inputStyle}
                        />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='最小收敛误差'
                    {...formItemLayout}
                >
                    {getFieldDecorator('tol', {
                        initialValue: 0.000001,
                        rules: [
                            { required: false },
                            { max: 100000, min: -100000, type: 'number', message: '最小收敛误差的区间在[-100000, 100000]' }
                        ]
                    })(
                        <InputNumber
                            step={0.000001}
                            style={inputStyle}
                        />
                    )}
                </FormItem>
            </Form>
        )
    }
}
/* 字段设置 */
class FieldSetting extends PureComponent {
    state = {
        chooseModalVisible: false,
        columns: []
    }
    componentDidMount () {
        this.getColumns()
    }
    getColumns = () => {
        const { taskId } = this.props;
        api.getInputTableColumns({ taskId }).then(res => {
            if (res.code === 1) {
                let columns = [];
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
        })
    }
    handleChoose = () => {
        this.setState({
            chooseModalVisible: true
        });
    }
    handleChange = (value) => {
        const { columns } = this.state;
        const object = columns.find(o => o.key === value);
        if (object) {
            this.props.handleSaveComponent('label', object);
        }
    }
    handelOk = (targetObjects) => {
        this.props.handleSaveComponent('col', targetObjects);
    }
    handleCancel = () => {
        this.setState({
            chooseModalVisible: false
        });
    }
    render () {
        const { chooseModalVisible, columns } = this.state;
        const { data } = this.props;
        const { getFieldDecorator } = this.props.form;
        const btnStyle = { display: 'block', width: '100%', fontSize: 13, color: '#2491F7', fontWeight: 'normal', marginTop: 4 };
        const btnContent = (isEmpty(data) || data.col.length == 0) ? '选择字段' : `已选择${data.col.length}个字段`
        return (
            <Form className="params-form">
                <FormItem
                    label={<div style={{ display: 'inline-block' }}>训练特征列<span className="supplementary">支持double、int类型字段</span></div>}
                    colon={false}
                    required
                    {...formItemLayout}
                >
                    <Button style={btnStyle} onClick={this.handleChoose}>{btnContent}</Button>
                </FormItem>
                <FormItem
                    colon={false}
                    label='目标列'
                    {...formItemLayout}
                >
                    {getFieldDecorator('label', {
                        rules: [{ required: true, message: '请选择目标列！' }]
                    })(
                        <Select placeholder="请选择目标列" onChange={this.handleChange}>
                            {columns.map((item, index) => {
                                return <Option key={item.key} value={item.key}>{item.key}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='正类值'
                    {...formItemLayout}
                >
                    {getFieldDecorator('pos', {
                        initialValue: '1',
                        rules: [{ required: true }]
                    })(
                        <Select>
                            <Option value={'1'}>1</Option>
                        </Select>
                    )}
                </FormItem>
                <div className="chooseWrap">
                    <ChooseModal
                        data={data}
                        transferField="double"
                        onOK={this.handelOk}
                        visible={chooseModalVisible}
                        onCancel={this.handleCancel} />
                </div>
            </Form>
        )
    }
}
/* 内存设置 */
class MemorySetting extends BaseMemorySetting {
    constructor (props) {
        super(props)
    }
}
class LogisticRegression extends PureComponent {
    constructor (props) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    handleSaveComponent = (field, filedValue) => {
        const { data } = this.props;
        const params = cloneDeep(data);
        if (field) {
            params[field] = filedValue
        }
        api.addOrUpdateTask(params).then((res) => {
            if (res.code == 1) {
                message.success('保存成功!');
            } else {
                message.warning('保存失败');
            }
        })
    }
    render () {
        const { data, taskId } = this.props;
        const WrapFieldSetting = Form.create({
            mapPropsToFields: (props) => {
                const { data } = props;
                const values = {
                    label: { value: isEmpty(data.label) ? '' : data.label.key },
                    pos: { value: String(data.pos) }
                }
                return values;
            },
            onFieldsChange: (props, changedFields) => {
                for (const key in changedFields) {
                    if (key === 'label') {
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
        })(FieldSetting);
        const WrapParamSetting = Form.create({
            onFieldsChange: (props, changedFields) => {
                for (const key in changedFields) {
                    if (key === 'penalty') {
                        // penalty是下拉菜单，与上同理
                        continue;
                    }
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.validating && !element.dirty) {
                            props.handleSaveComponent(key, element.value)
                        }
                    }
                }
            },
            mapPropsToFields: (props) => {
                const { data } = props;
                const values = {
                    penalty: { value: data.penalty },
                    max_iter: { value: data.max_iter },
                    c: { value: data.c },
                    tol: { value: data.tol }
                }
                return values;
            }
        })(paramSetting);
        const WrapMemorySetting = Form.create({
            onFieldsChange: (props, changedFields) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.validating && !element.dirty) {
                            props.handleSaveComponent(key, element.value)
                        }
                    }
                }
            },
            mapPropsToFields: (props) => {
                const { data } = props;
                const values = {
                    workerMemory: { value: data.workerMemory },
                    workerCores: { value: data.workerCores }
                }
                return values;
            }
        })(MemorySetting);
        return (
            <Tabs type="card" className="params-tabs">
                <TabPane tab="字段设置" key="1">
                    <WrapFieldSetting data={data} handleSaveComponent={this.handleSaveComponent} taskId={taskId} />
                </TabPane>
                <TabPane tab="参数设置" key="2">
                    <WrapParamSetting data={data} handleSaveComponent={this.handleSaveComponent} />
                </TabPane>
                <TabPane tab="内存设置" key="3">
                    <WrapMemorySetting data={data} handleSaveComponent={this.handleSaveComponent} />
                </TabPane>
            </Tabs>
        );
    }
}

export default LogisticRegression;
