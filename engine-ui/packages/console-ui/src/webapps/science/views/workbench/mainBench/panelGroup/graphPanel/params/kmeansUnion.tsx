import * as React from 'react';
import { Tabs, Form, Button, Select, InputNumber, message } from 'antd';
import { MemorySetting as BaseMemorySetting, ChooseModal as BaseChooseModal } from './typeChange';
import { formItemLayout } from './index';
import { isEmpty, cloneDeep, debounce, isNumber, get } from 'lodash';
import api from '../../../../../../api/experiment';
import { TASK_ENUM, COMPONENT_TYPE } from '../../../../../../consts';
import HelpDoc from '../../../../../../components/helpDoc';
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
    }
    handleChange = (id: string, value: any) => {
        this.props.form.validateFieldsAndScroll([id], (err: any, values: any) => {
            if (!err) {
                this.props.handleSaveComponent(id, value);
            }
        });
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
    renderNumberFormItem (options: {
        label: string;
        key: string;
        initialValue?: number;
        min?: number;
        excludeMin?: boolean;
        max: number;
        excludeMax?: boolean;
        step?: number;
        isInt?: boolean;
    }, getFieldDecorator: any) {
        return <FormItem
            colon={false}
            label={<div style={{ display: 'inline-block' }}>{options.label}{options.max != null && (<span className="supplementary">{options.excludeMin ? '(' : '['}{options.min || 0},{options.max}{options.excludeMax ? ')' : ']'}, {options.isInt ? '正整数' : 'float型'}</span>)}</div>}
            {...formItemLayout}
        >
            {getFieldDecorator(options.key, {
                initialValue: options.initialValue,
                rules: [
                    { required: false },
                    options.max != null && { min: options.min || 0, max: options.max, message: `${options.label}的取值范围为${options.excludeMin ? '(' : '['}${options.min},${options.max}${options.excludeMax ? ')' : ']'}`, type: 'number' }
                ].filter(Boolean)
            })(
                <InputNumber
                    {...{
                        onBlur: (e: any) => this.handleSubmit(options.key, e.target.value)
                    }}
                    step={options.step}
                    formatter={options.isInt ? (value: any) => { return isNumber(value) ? ~~value : value; } : undefined}
                    style={inputStyle}
                />
            )}
        </FormItem>
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        return (
            <Form className="params-form">
                {this.renderNumberFormItem({
                    label: '聚类数',
                    key: 'unionNumber',
                    max: 1000,
                    step: 1,
                    initialValue: 10,
                    isInt: true
                }, getFieldDecorator)}
                <FormItem
                    label='距离度量方式'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('distanceMeasure', {
                        initialValue: 'Euclidean',
                        rules: [{ required: false }]
                    })(
                        <Select placeholder="请选择距离度量方式" onSelect={this.handleChange.bind(this, 'distanceMeasure')}>
                            <Option key={'Euclidean'} value={'Euclidean'}>Euclidean</Option>
                            <Option key={'Cosine'} value={'Cosine'}>Cosine</Option>
                            <Option key={'Cityblock'} value={'Cityblock'}>Cityblock</Option>
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    label='质心初始化方法'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('initMethod', {
                        initialValue: 'Random',
                        rules: [{ required: false }]
                    })(
                        <Select placeholder="请选择质心初始化方法" onSelect={this.handleChange.bind(this, 'initMethod')}>
                            <Option key={'Random'} value={'Random'}>Random</Option>
                            <Option key={'First K'} value={'First K'}>First K</Option>
                            <Option key={'Uniform'} value={'Uniform'}>Uniform</Option>
                            <Option key={'K-means++'} value={'K-means++'}>K-means++</Option>
                            <Option key={'使用初始质心表'} value={'使用初始质心表'}>使用初始质心表</Option>
                        </Select>
                    )}
                </FormItem>
                {this.renderNumberFormItem({
                    label: '最大迭代次数',
                    key: 'maxCount',
                    max: 1000,
                    step: 1,
                    initialValue: 100,
                    isInt: true
                }, getFieldDecorator)}
                {this.renderNumberFormItem({
                    label: '收敛标准',
                    key: 'convergence',
                    max: null,
                    step: 0.1,
                    initialValue: 0.1
                }, getFieldDecorator)}
                {this.renderNumberFormItem({
                    label: '初始随机种子',
                    key: 'randomSeed',
                    max: 10000,
                    step: 1,
                    initialValue: 1,
                    isInt: true
                }, getFieldDecorator)}
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
        const btnStyle: any = { display: 'block', width: '100%', fontSize: 13, color: '#2491F7', fontWeight: 'normal', marginTop: 4 };
        const btnContent = (!data || isEmpty(data.col) || data.col.length == 0) ? '选择字段' : `已选择${data.col.length}个字段`
        return (
            <Form className="params-form">
                <FormItem
                    label={<div style={{ display: 'inline-block' }}>特征列<span className="supplementary">支持double、int类型字段</span></div>}
                    colon={false}
                    required
                    {...formItemLayout}
                >
                    <Button style={btnStyle} onClick={this.handleChoose}>{btnContent}</Button>
                </FormItem>
                <FormItem
                    label={<div style={{ display: 'inline-block' }}>附加列<HelpDoc style={{ top: 2 }} doc='additionalColumn' /></div>}
                    colon={false}
                    {...formItemLayout}
                >
                    <Button style={btnStyle} onClick={this.handleChoose}>{btnContent}</Button>
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
class GbdtRegression extends React.PureComponent<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    handleSaveComponent = (field: any, filedValue: any) => {
        const { data, currentTab, componentId, changeContent } = this.props;
        const fieldName = TASK_ENUM[COMPONENT_TYPE.MACHINE_LEARNING.KMEANS_UNION];
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
        const { data, currentTab, componentId } = this.props;
        const WrapFieldSetting = Form.create({
            mapPropsToFields: (props: any) => {
                const { data } = props;
                const values: any = {
                    label: { value: (!data.label || isEmpty(data.label)) ? '' : data.label.key }
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
                        if (!element.validating && !element.dirty && element.name !== 'transferField') {
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
                    unionNumber: { value: data.unionNumber },
                    distanceMeasure: { value: data.distanceMeasure },
                    initMethod: { value: data.initMethod },
                    maxCount: { value: data.maxCount },
                    convergence: { value: data.convergence },
                    randomSeed: { value: data.randomSeed }
                }
                return values;
            }
        })(ParamSetting);
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

export default GbdtRegression;
