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
            value: 'ls',
            name: 'ls'
        }, {
            value: 'lad',
            name: 'lad'
        }, {
            value: 'huber',
            name: 'huber'
        }, {
            value: 'quantile',
            name: 'quantile'
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
        const { regexDatas } = this.state;
        const { getFieldDecorator } = this.props.form;
        return (
            <Form className="params-form">
                <FormItem
                    label='损失函数类型'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('loss', {
                        initialValue: 'ls',
                        rules: [{ required: false }]
                    })(
                        <Select placeholder="请选择损失函数类型" onChange={this.handleSubmit.bind(this, 'loss')}>
                            {regexDatas.map((item: any, index: any) => {
                                return <Option key={index} value={item.value}>{item.name}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: 'alpha',
                    key: 'alpha',
                    max: 1,
                    step: 0.1,
                    initialValue: 0.9
                }, getFieldDecorator)}
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '树数量',
                    key: 'nEstimators',
                    max: 10000,
                    step: 5,
                    initialValue: 500,
                    isInt: true
                }, getFieldDecorator)}
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '学习速率',
                    key: 'learningRate',
                    max: 1,
                    excludeMin: true,
                    excludeMax: true,
                    step: 0.05,
                    initialValue: 0.05,
                    isInt: false
                }, getFieldDecorator)}
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '一棵树的最大深度',
                    key: 'maxDepth',
                    max: 100,
                    step: 1,
                    initialValue: 10,
                    isInt: true
                }, getFieldDecorator)}
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '叶子节点容纳的最少样本数',
                    key: 'minSamplesLeaf',
                    max: 1000,
                    step: 1,
                    initialValue: 500,
                    isInt: true
                }, getFieldDecorator)}
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '样本采样比例',
                    key: 'subsample',
                    max: 1,
                    excludeMin: true,
                    excludeMax: true,
                    step: 0.1,
                    initialValue: 0.6,
                    isInt: false
                }, getFieldDecorator)}
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '训练中采集的特征比例',
                    key: 'maxFeatures',
                    max: 1,
                    excludeMin: true,
                    excludeMax: true,
                    step: 0.1,
                    initialValue: 0.6,
                    isInt: false
                }, getFieldDecorator)}
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '测试样本数比例',
                    key: 'validationFraction',
                    max: 1,
                    excludeMax: true,
                    step: 0.1,
                    initialValue: 0,
                    isInt: false
                }, getFieldDecorator)}
                {renderNumberFormItem({
                    handleSubmit: this.handleSubmit.bind(this),
                    label: '随机数产生器种子',
                    key: 'randomState',
                    max: 10,
                    step: 1,
                    initialValue: 0,
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
                    label={<div style={{ display: 'inline-block' }}>输入列<span className="supplementary">支持double、int类型字段</span></div>}
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
class GbdtRegression extends React.PureComponent<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    handleSaveComponent = (field: any, filedValue: any) => {
        const { data, currentTab, componentId, changeContent } = this.props;
        const fieldName = TASK_ENUM[COMPONENT_TYPE.MACHINE_LEARNING.GBDT_REGRESSION];
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
                    loss: { value: data.loss },
                    alpha: { value: data.alpha },
                    nEstimators: { value: data.nEstimators },
                    learningRate: { value: data.learningRate },
                    maxDepth: { value: data.maxDepth },
                    minSamplesLeaf: { value: data.minSamplesLeaf },
                    subsample: { value: data.subsample },
                    maxFeatures: { value: data.maxFeatures },
                    validationFraction: { value: data.validationFraction },
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

export default GbdtRegression;
