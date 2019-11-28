import * as React from 'react';
import { Tabs, Form, Button, Select, InputNumber, message, Spin, Input } from 'antd';
import { MemorySetting as BaseMemorySetting, ChooseModal as BaseChooseModal } from './typeChange';
import { formItemLayout } from './index';
import { isEmpty, cloneDeep, debounce, isNumber, get } from 'lodash';
import api from '../../../../../../api/experiment';
const TabPane = Tabs.TabPane;
const Option = Select.Option;
const FormItem = Form.Item;
const inputStyle: any = {
    width: '100%'
}
/* 选择字段弹出框 */
class ChooseModal extends BaseChooseModal {
    constructor (props: any) {
        super(props);
    }
    // 重写钩子函数，实现由父级更新soureceData
    static getDerivedStateFromProps (nextProps: any, prevState: any) {
        if (nextProps.sourceData.length != 0) {
            return {
                sourceData: nextProps.sourceData,
                loading: nextProps.loading
            }
        } else {
            return null;
        }
    }
    componentDidUpdate (prevProps: any, prevState: any, snapshot: any) {
        if (prevState.sourceData.length != this.state.sourceData.length) {
            this.initTargetKeys();
        }
    }
    initTargetKeys = () => {
        // 继承重写该方法
        const { data, transferField } = this.props;
        const { backupSource } = this.state;
        const chooseData = data.col || [];
        const targetKeys = chooseData.map((item: any) => {
            return item.key;
        });
        const sourceData = cloneDeep(backupSource);
        sourceData.forEach((item: any) => {
            if (targetKeys.findIndex((o: any) => o === item.key) > -1) {
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
class ParamSetting extends React.PureComponent<any, any> {
    state: any = {
        regexDatas: [{
            value: 'l1',
            name: 'L1'
        }, {
            value: 'l2',
            name: 'L2'
        }]
    }
    handleChange = (value: any) => {
        const { regexDatas } = this.state;
        const object = regexDatas.find((o: any) => o.value === value);
        if (object) {
            this.props.form.validateFieldsAndScroll(['penalty'], (err: any, values: any) => {
                if (!err) {
                    this.props.handleSaveComponent('penalty', value);
                }
            });
        }
    }
    handleSubmit (name: any, value: any) {
        this.props.form.validateFieldsAndScroll([name], (err: any, values: any) => {
            if (!err) {
                this.props.handleSaveComponent(name, value);
            }
        });
    }
    /* 最小收敛误差 */
    validatorTol = (rule: any, value: any, callback: any) => {
        if (isNumber(value) && value > 0 && value <= 100000) {
            callback()
        } else {
            callback(new Error('最小收敛误差的区间在(0, 100000]'))
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
                        initialValue: 'l1',
                        rules: [{ required: false }]
                    })(
                        <Select placeholder="请选择目标列" onChange={this.handleChange}>
                            {regexDatas.map((item: any, index: any) => {
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
                            { min: 1, max: 100000, message: '最大迭代次数的取值范围为[1,100000]', type: 'number' }
                        ]
                    })(
                        <InputNumber
                            {...{
                                onBlur: (e: any) => this.handleSubmit('max_iter', e.target.value)
                            }}
                            parser={(value: any) => isNumber(value) ? ~~value : value}
                            formatter={(value: any) => isNumber(value) ? ~~value : value}
                            style={inputStyle}
                        />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label={<div>正则系数</div>}
                    {...formItemLayout}
                >
                    {getFieldDecorator('c', {
                        initialValue: 1,
                        rules: [
                            { required: false },
                            { min: 1, max: 100000, message: '正则系数取值范围为[1,100000]', type: 'number' }
                        ]
                    })(
                        <InputNumber
                            {...{
                                onBlur: (e: any) => this.handleSubmit('c', e.target.value)
                            }}
                            parser={(value: any) => isNumber(value) ? ~~value : value}
                            formatter={(value: any) => isNumber(value) ? ~~value : value}
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
                        initialValue: 0.0001,
                        rules: [
                            { required: false },
                            { validator: this.validatorTol }
                        ]
                    })(
                        <InputNumber
                            {...{
                                onBlur: (e: any) => this.handleSubmit('tol', e.target.value)
                            }}
                            step={0.0001}
                            style={inputStyle}
                        />
                    )}
                </FormItem>
            </Form>
        )
    }
}
/* 字段设置 */
class FieldSetting extends React.PureComponent<any, any> {
    state: any = {
        chooseModalVisible: false,
        columns: [],
        fetching: false
    }
    getColumns = () => {
        if (this.state.columns.length > 0) {
            /**
             * 此处是为了减少请求次数
             */
            return;
        }
        const { currentTab, componentId, data } = this.props;
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
                                type: element,
                                disabled: key === get(data, 'label.key', '')
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
    handleChoose = () => {
        this.getColumns();
        this.setState({
            chooseModalVisible: true
        });
    }
    handleChange = (value: any) => {
        const { columns } = this.state;
        const object = columns.find((o: any) => o.key === value);
        if (object) {
            this.props.handleSaveComponent('label', object);
        }
    }
    handelOk = (targetObjects: any) => {
        this.props.handleSaveComponent('col', targetObjects);
    }
    handleCancel = () => {
        this.setState({
            chooseModalVisible: false
        });
    }
    render () {
        const { chooseModalVisible, columns, fetching } = this.state;
        const { data, currentTab, componentId } = this.props;
        const { getFieldDecorator } = this.props.form;
        const btnStyle: any = { display: 'block', width: '100%', fontSize: 13, color: '#2491F7', fontWeight: 'normal', marginTop: 4 };
        const btnContent = (!data || isEmpty(data.col) || data.col.length == 0) ? '选择字段' : `已选择${data.col.length}个字段`
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
                        <Select
                            notFoundContent={fetching ? <Spin size="small" /> : '未找到数据表'}
                            placeholder="请选择目标列"
                            onFocus={this.getColumns} // 获取焦点的时候再去请求，防止日志弹窗的弹出导致不停的触发didmount函数
                            onChange={this.handleChange}>
                            {columns.map((item: any, index: any) => {
                                const disabled = !!data.col.find((o: any) => o.key === item.key);
                                return <Option key={item.key} value={item.key} disabled={disabled}>{item.key}</Option>
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
                        rules: [{ required: true }]
                    })(
                        <Input placeholder="请输入正类值" />
                    )}
                </FormItem>
                <div className="chooseWrap">
                    <ChooseModal
                        loading={fetching}
                        sourceData={columns}
                        currentTab={currentTab}
                        componentId={componentId}
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
    constructor (props: any) {
        super(props)
    }
}
class LogisticRegression extends React.PureComponent<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    handleSaveComponent = (field: any, filedValue: any) => {
        const { data, currentTab, componentId, changeContent } = this.props;
        const currentComponentData = currentTab.graphData.find((o: any) => o.vertex && o.data.id === componentId);
        const params: any = {
            ...currentComponentData.data,
            logisticComponent: {
                ...data
            }
        }
        if (field) {
            params.logisticComponent[field] = filedValue
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
        const { data, currentTab, componentId } = this.props;
        const WrapFieldSetting = Form.create({
            mapPropsToFields: (props: any) => {
                const { data } = props;
                const values: any = {
                    label: { value: (!data.label || isEmpty(data.label)) ? '' : data.label.key },
                    pos: { value: data.pos ? String(data.pos) : '' }
                }
                return values;
            },
            onFieldsChange: (props: any, changedFields: any) => {
                for (const key in changedFields) {
                    if (key === 'label') {
                        // label是下拉菜单，在组件里自己触发onChange函数,对数据封装过后再请求
                        continue;
                    }
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.errors && !element.validating && !element.dirty && element.name !== 'transferField') {
                            props.handleSaveComponent(key, element.value)
                        }
                    }
                }
            }
        })(FieldSetting);
        const WrapParamSetting = Form.create({
            mapPropsToFields: (props: any) => {
                const { data } = props;
                const values: any = {
                    penalty: { value: data.penalty },
                    // eslint-disable-next-line @typescript-eslint/camelcase
                    max_iter: { value: data.max_iter },
                    c: { value: data.c },
                    tol: { value: data.tol }
                }
                return values;
            }
        })(ParamSetting);
        const WrapMemorySetting = Form.create({
            onFieldsChange: (props: any, changedFields: any) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.errors && !element.validating && !element.dirty) {
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
                    <WrapFieldSetting
                        data={data}
                        handleSaveComponent={this.handleSaveComponent}
                        currentTab={currentTab}
                        componentId={componentId} />
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
