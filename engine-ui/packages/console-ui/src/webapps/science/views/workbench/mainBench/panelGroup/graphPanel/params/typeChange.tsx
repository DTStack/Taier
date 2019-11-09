import * as React from 'react';
import { Tabs, Form, Input, Radio, Checkbox, Button, Tooltip, Icon, InputNumber, Modal, Transfer, message, Spin } from 'antd';
import { formItemLayout } from './index';
import { cloneDeep, debounce, isNumber } from 'lodash';
import api from '../../../../../../api/experiment';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const TYPE_ENUM: any = {
    double: 'Double类型',
    int: 'Int类型',
    string: 'String类型'
}
const KEY_VALUE_ENUM: any = {
    double: 'doubleTrans',
    int: 'intTrans',
    string: 'stringTrans'
}
/* 选择字段弹出框 */
export class ChooseModal extends React.PureComponent<any, any> {
    warpClassName: any;
    disabledType: string;
    constructor (props: any) {
        super(props);
        this.warpClassName = '.chooseWrap';
    }
    state: any = {
        sourceData: [],
        backupSource: [],
        targetKeys: [],
        loading: false
    }
    componentDidUpdate (prevProps: any, prevState: any, snapshot: any) {
        if (this.props.visible && !prevProps.visible) {
            // 说明是打开选择字段的弹窗
            if (this.state.sourceData.length === 0) {
                /**
                 * 以sourceData的长度来标志
                 * 表示第一次打开弹窗还没有获取soureceData
                 */
                this.getSourceData();
            } else {
                this.initTargetKeys();
            }
        }
    }
    initTargetKeys () {
        const { data, transferField } = this.props;
        const { backupSource } = this.state;
        const chooseData = data[KEY_VALUE_ENUM[transferField]] || [];
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
    getSourceData = () => {
        const { currentTab, componentId } = this.props;
        const targetEdge = currentTab.graphData && currentTab.graphData.find((o: any) => {
            return o.edge && o.target.data.id == componentId
        })
        if (targetEdge) {
            this.setState({
                loading: true
            });
            api.getInputTableColumns({ taskId: componentId, inputType: targetEdge.inputType }).then((res: any) => {
                if (res.code === 1) {
                    let sourceData: any = [];
                    for (const key in res.data) {
                        if (res.data.hasOwnProperty(key)) {
                            const element = res.data[key];
                            if (this.disabledType) {
                                sourceData.push({
                                    key,
                                    type: element,
                                    disabled: element === this.disabledType
                                })
                            } else {
                                sourceData.push({
                                    key,
                                    type: element
                                })
                            }
                        }
                    }
                    this.setState({
                        sourceData,
                        backupSource: cloneDeep(sourceData)
                    }, () => {
                        this.initTargetKeys();
                    })
                }
                this.setState({
                    loading: false
                })
            })
        }
    }
    handleCancel = () => {
        this.initTargetKeys();
        this.setState({
            sourceData: []
        })
        this.props.onCancel();
    }
    handleOk = () => {
        const { targetKeys, sourceData } = this.state;
        const { transferField } = this.props;
        const sourceDataMap: any = {};
        (sourceData || []).forEach((item: any) => {
            sourceDataMap[item.key] = item;
        });
        const keyTypes = targetKeys.map((item: any) => {
            const data = sourceDataMap[item];
            if (!data) {
                return null;
            }
            return { key: item, type: transferField || sourceDataMap[item].type }
        }).filter(Boolean);
        this.props.onOK(keyTypes)
        this.handleCancel();
    }
    filterOption = (inputValue: any, option: any) => {
        return option.key.indexOf(inputValue) > -1;
    }
    handleChange = (targetKeys: any, direction: any, moveKeys: any) => {
        const { sourceData, backupSource } = this.state;
        const { transferField } = this.props;
        if (direction === 'right') {
            moveKeys.forEach((item: any) => {
                let object = sourceData.find((o: any) => o.key === item);
                if (object) {
                    object.type = transferField || object.type
                }
            })
        } else {
            // 用替补数据源替换掉当前的数据源
            moveKeys.forEach((item: any) => {
                let sourecObject = backupSource.find((o: any) => o.key === item);
                let index = sourceData.findIndex((o: any) => o.key === item);
                if (sourecObject && index > -1) {
                    sourceData[index] = cloneDeep(sourecObject);
                }
            })
        }
        this.setState({ targetKeys, sourceData });
    }
    renderItem = (item: any) => {
        return (
            <span style={{ display: 'inline-grid', gridTemplateColumns: '1fr 1fr', width: '90%' }}>
                <span>{item.key}</span>
                <span>{item.type}</span>
            </span>
        );
    }

    render () {
        const { visible } = this.props;
        return (
            <Modal
                title="选择字段"
                visible={visible}
                width={800}
                onOk={this.handleOk}
                onCancel={this.handleCancel}
                getContainer={() => document.querySelector(this.warpClassName)}
            >
                <Spin spinning={this.state.loading}>
                    <Transfer
                        listStyle={{
                            height: 400
                        }}
                        className="params-transfer"
                        rowKey={(record: any) => record.key}
                        dataSource={this.state.sourceData}
                        showSearch
                        filterOption={this.filterOption}
                        targetKeys={this.state.targetKeys}
                        onChange={this.handleChange}
                        render={this.renderItem}
                    />
                </Spin>
            </Modal>
        );
    }
}

/* 转化字段 */
class Transform extends React.PureComponent<any, any> {
    state: any = {
        chooseModalVisible: false
    }
    handleChoose = () => {
        this.setState({
            chooseModalVisible: true
        });
    }
    handleOK = (targetObjects: any) => {
        const transferField = this.props.form.getFieldValue('transferField');
        const field = KEY_VALUE_ENUM[transferField];
        this.props.handleSaveComponent(field, targetObjects);
    }
    handleCancel = () => {
        this.setState({
            chooseModalVisible: false
        });
    }
    renderPartition = () => {
        const { getFieldDecorator } = this.props.form;
        const { data } = this.props;
        const transferField = this.props.form.getFieldValue('transferField');
        switch (transferField) {
            case 'int':return (
                <>
                    <FormItem
                        style={{ marginBottom: 0 }}
                        colon={false}
                        label={`转化成Int异常时，默认填充值`}
                        {...formItemLayout}
                    >
                        {getFieldDecorator('intError', {
                            initialValue: data.intError
                        })(
                            <Input />
                        )}
                    </FormItem>
                </>
            )
            case 'string': return (
                <>
                    <FormItem
                        style={{ marginBottom: 0 }}
                        colon={false}
                        label={`转化成String异常时，默认填充值`}
                        {...formItemLayout}
                    >
                        {getFieldDecorator('strError', {
                            initialValue: data.strError
                        })(
                            <Input />
                        )}
                    </FormItem>
                </>
            )
            case 'double':
            default: return (
                <>
                    <FormItem
                        style={{ marginBottom: 0 }}
                        colon={false}
                        label={`转化成Double异常时，默认填充值`}
                        {...formItemLayout}
                    >
                        {getFieldDecorator('doubleError', {
                            initialValue: data.doubleError
                        })(
                            <Input />
                        )}
                    </FormItem>
                </>
            )
        }
    }
    renderBtnContent = () => {
        const { data } = this.props;
        const transferField = this.props.form.getFieldValue('transferField');
        const chooseData = KEY_VALUE_ENUM[transferField];
        const btnContent = (data && data[chooseData] && data[chooseData].length > 0) ? `已选择${data[chooseData].length}个字段` : '选择字段'
        return btnContent;
    }
    render () {
        const { data, componentId, currentTab } = this.props;
        const transferField = this.props.form.getFieldValue('transferField');
        const { chooseModalVisible } = this.state;
        const { getFieldDecorator } = this.props.form;
        const btnStyle: any = { display: 'block', width: '100%', fontSize: 13, color: '#2491F7', fontWeight: 'normal', marginTop: 4 };
        return (
            <Form className="params-form">
                <FormItem
                    label="转化类型及字段"
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('transferField', {
                        initialValue: data.transferField || 'double',
                        rules: [{ required: false }]
                    })(
                        <RadioGroup>
                            <Radio style={{ marginRight: 4, fontSize: 13 }} value={'double'}>{TYPE_ENUM.double}</Radio>
                            <Radio style={{ marginRight: 4, fontSize: 13 }} value={'int'}>{TYPE_ENUM.int}</Radio>
                            <Radio style={{ marginRight: 0, fontSize: 13 }} value={'string'}>{TYPE_ENUM.string}</Radio>
                        </RadioGroup>
                    )}
                    <Button style={btnStyle} onClick={this.handleChoose}>{this.renderBtnContent()}</Button>
                </FormItem>
                {this.renderPartition()}
                <FormItem
                    colon={false}
                    label=""
                    {...formItemLayout}
                >
                    <div style={{ display: 'grid', gridTemplateColumns: '82px auto' }}>
                        {getFieldDecorator('isSaveOld', {
                            initialValue: data.isSaveOld === 1,
                            valuePropName: 'checked',
                            getValueFromEvent: (e: any) => {
                                if (!e || !e.target) {
                                    return e;
                                }
                                const { target } = e;
                                return target.type === 'checkbox' ? (target.checked ? 1 : 0) : target.value;
                            }
                        })(
                            <Checkbox style={{ fontSize: 13 }}>保留原列</Checkbox>
                        )}
                        <div className="supplementary" style={{ paddingTop: 5, lineHeight: 1.5, marginLeft: 0 }}>{'若保留，原列名不变，处理过的列增加"typed_"前缀'}</div>
                    </div>
                </FormItem>
                <div className="chooseWrap">
                    <ChooseModal
                        currentTab={currentTab}
                        componentId={componentId}
                        data={data}
                        transferField={transferField}
                        visible={chooseModalVisible}
                        onOK={this.handleOK}
                        onCancel={this.handleCancel} />
                </div>
            </Form>
        )
    }
}
/* 内存设置 */
export class MemorySetting extends React.PureComponent<any, any> {
    renderTooltips = (label: any, title: any) => {
        return (
            <div>
                {label}
                <Tooltip overlayClassName="big-tooltip" title={title}>
                    <Icon type="question-circle-o" className="supplementary" />
                </Tooltip>
            </div>
        )
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const inputStyle: any = { width: '100%' };
        return (
            <Form className="params-form">
                <FormItem
                    label={this.renderTooltips('占用内存大小', '可选项。正整数，单位MB，范围[256, 64 *1024]，默认512M')}
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('workerMemory', {
                        initialValue: '512',
                        rules: [
                            { required: false },
                            { max: 65536, min: 256, message: '范围[256, 64 *1024]', type: 'number' }
                        ]
                    })(
                        <InputNumber
                            parser={(value: any) => isNumber(value) ? ~~value : value}
                            formatter={(value: any) => isNumber(value) ? ~~value : value}
                            style={inputStyle} />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label={this.renderTooltips('并发数', '可选项。正整数，范围[1, 9999]，默认并发数为1，单线程运行')}
                    {...formItemLayout}
                >
                    {getFieldDecorator('workerCores', {
                        initialValue: '1',
                        rules: [
                            { required: false },
                            { max: 9999, min: 1, message: '范围[1, 9999]', type: 'number' }
                        ]
                    })(
                        <InputNumber
                            parser={(value: any) => isNumber(value) ? ~~value : value}
                            formatter={(value: any) => isNumber(value) ? ~~value : value}
                            style={inputStyle} />
                    )}
                </FormItem>
            </Form>
        )
    }
}
/* main页面 */
class TypeChange extends React.PureComponent<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
        this.state = {
            transferField: 'double'
        }
    }
    handleSaveComponent = (field: any, filedValue: any) => {
        const { data, currentTab, componentId, changeContent } = this.props;
        const currentComponentData = currentTab.graphData.find((o: any) => o.vertex && o.data.id === componentId);
        const params: any = {
            ...currentComponentData.data,
            transTypeComponent: {
                ...data
            }
        }
        if (field) {
            params.transTypeComponent[field] = filedValue
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
        const { transferField } = this.state;
        const { data, currentTab, componentId } = this.props;
        const WrapTransform = Form.create({
            onFieldsChange: (props: any, changedFields: any) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        const element = changedFields[key];
                        if (!element.errors && !element.validating && !element.dirty) {
                            if (element.name !== 'transferField') {
                                props.handleSaveComponent(key, element.value)
                            } else {
                                this.setState({
                                    transferField: element.value
                                })
                            }
                        }
                    }
                }
            }
        })(Transform);
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
                <TabPane tab="转化字段" key="1">
                    <WrapTransform
                        data={{ ...data, transferField }}
                        handleSaveComponent={this.handleSaveComponent}
                        currentTab={currentTab}
                        componentId={componentId} />
                </TabPane>
                <TabPane tab="内存设置" key="2">
                    <WrapMemorySetting data={data} handleSaveComponent={this.handleSaveComponent} />
                </TabPane>
            </Tabs>
        );
    }
}

export default TypeChange;
