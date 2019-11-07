import * as React from 'react';
import { Tabs, Form, Button, Select, message, Spin } from 'antd';
import { MemorySetting as BaseMemorySetting, ChooseModal as BaseChooseModal } from './typeChange';
import { formItemLayout } from './index';
import { isEmpty, cloneDeep, debounce, get } from 'lodash';
import api from '../../../../../../api/experiment';
import { TASK_ENUM, COMPONENT_TYPE } from '../../../../../../consts';
import { renderNumberFormItem } from './helper';

const TabPane = Tabs.TabPane;
const Option = Select.Option;
const FormItem = Form.Item;

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
                item.type = transferField || item.type;
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
            value: 'rbf',
            name: 'rbf'
        }, {
            value: 'linear',
            name: 'linear'
        }, {
            value: 'poly',
            name: 'poly'
        }, {
            value: 'sigmoid',
            name: 'sigmoid'
        }, {
            value: 'precomputed',
            name: 'precomputed'
        }],
        nuclearDatas: [{
            value: 'auto',
            name: 'auto'
        }, {
            value: 'float',
            name: 'float'
        }]
    }
    handleSubmit (name: any, value: any) {
        this.props.form.validateFieldsAndScroll([name], (err: any, values: any) => {
            if (!err) {
                this.props.handleSaveComponent(name, value);
            }
        });
    }
    render () {
        const { regexDatas, nuclearDatas } = this.state;
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const svmVal = getFieldValue('svm');
        const showNuclear: boolean = svmVal == 'rbf' || svmVal == 'poly' || svmVal == 'sigmoid';
        return (
            <Form className="params-form">
                <FormItem
                    label='SVM的核函数'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('kernel', {
                        initialValue: 'rbf',
                        rules: [{ required: false }]
                    })(
                        <Select placeholder="请选择SVM的核函数" onChange={this.handleSubmit.bind(this, 'kernel')}>
                            {regexDatas.map((item: any, index: any) => {
                                return <Option key={item.value} value={item.value}>{item.name}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '多项式核的阶',
                    key: 'degree',
                    max: 10000,
                    min: 1,
                    step: 1,
                    initialValue: 500,
                    isInt: true
                }, getFieldDecorator)}
                {
                    showNuclear && (
                        <FormItem
                            label='核系数'
                            colon={false}
                            {...formItemLayout}
                        >
                            {getFieldDecorator('gamma', {
                                initialValue: 'auto',
                                rules: [{ required: false }]
                            })(
                                <Select placeholder="请选择核系数" onChange={this.handleSubmit.bind(this, 'gamma')}>
                                    {nuclearDatas.map((item: any, index: any) => {
                                        return <Option key={item.value} value={item.value}>{item.name}</Option>
                                    })}
                                </Select>
                            )}
                        </FormItem>
                    )
                }
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '最小收敛误差',
                    key: 'tol',
                    excludeMin: true,
                    excludeMax: true,
                    step: 0.0001,
                    initialValue: 0.0001,
                    isInt: false
                }, getFieldDecorator)}
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '惩罚项系数',
                    key: 'c',
                    excludeMin: true,
                    excludeMax: true,
                    step: 0.1,
                    initialValue: 1,
                    isInt: false
                }, getFieldDecorator)}
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '最大迭代次数',
                    key: 'maxIter',
                    max: 1000,
                    step: 1,
                    initialValue: 1000,
                    isInt: true
                }, getFieldDecorator)}
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '随机数产生器种子',
                    key: 'randomState',
                    max: 10,
                    step: 1,
                    initialValue: null,
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
        const { getFieldDecorator } = this.props.form;
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
                    colon={false}
                    label='标签列'
                    {...formItemLayout}
                >
                    {getFieldDecorator('label', {
                        rules: [{ required: true, message: '请选择标签列！' }]
                    })(
                        <Select
                            showSearch
                            notFoundContent={fetching ? <Spin size="small" /> : '未找到数据表'}
                            placeholder="请选择标签列"
                            onFocus={this.getColumns} // 获取焦点的时候再去请求，防止日志弹窗的弹出导致不停的触发didmount函数
                            onChange={this.handleChange}>
                            {columns.map((item: any, index: any) => {
                                const disabled = !!(data.col || []).find((o: any) => o.key === item.key);
                                return <Option key={item.key} value={item.key} disabled={disabled}>{item.key}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <div className="chooseWrap">
                    <ChooseModal
                        loading={fetching}
                        sourceData={columns}
                        currentTab={currentTab}
                        componentId={componentId}
                        data={data}
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
class SvmComponent extends React.PureComponent<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    handleSaveComponent = (field: any, filedValue: any) => {
        const { data, currentTab, componentId, changeContent } = this.props;
        const fieldName = TASK_ENUM[COMPONENT_TYPE.MACHINE_LEARNING.SVM];
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
                    kernel: { value: data.kernel },
                    degree: { value: data.degree },
                    gamma: { value: data.gamma },
                    tol: { value: data.tol },
                    c: { value: data.c },
                    maxIter: { value: data.maxIter },
                    randomState: { value: data.randomState }
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

export default SvmComponent;
