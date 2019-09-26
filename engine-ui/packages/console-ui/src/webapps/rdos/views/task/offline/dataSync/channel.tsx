import * as React from 'react';
import { connect } from 'react-redux';
import {
    Form, InputNumber, Input,
    Select, Button, AutoComplete,
    Checkbox
} from 'antd';

import {
    settingAction,
    workbenchAction
} from '../../../../store/modules/offlineTask/actionType';

import HelpDoc from '../../../helpDoc';
import LifeCycle from '../../../dataManage/lifeCycle';
import { isRDB } from '../../../../comm';
import {
    DATA_SOURCE
} from '../../../../comm/const';
import ajax from '../../../../api';

const FormItem = Form.Item;
const Option = Select.Option;

const formItemLayout: any = {
    labelCol: {
        sm: { span: 6 }
    },
    wrapperCol: {
        sm: { span: 14 }
    }
};

class ChannelForm extends React.Component<any, any> {
    state: any = {
        isRecord: false,
        idFields: [] // 标识字段
    }

    constructor (props: any) {
        super(props);
    }

    componentDidMount () {
        // 开启断点续传功能，需要加载标识字段
        if (this.props.setting.isRestore) {
            this.loadIdFields();
        }
    }

    onLifeDayChange = (val: any) => {
        this.props.changeChannelSetting({
            lifeDay: val
        })
    }

    loadIdFields = async () => {
        const { sourceMap } = this.props;
        const res = await ajax.getIncrementColumns({
            sourceId: sourceMap.sourceId,
            tableName: sourceMap.type.table
        });

        if (res.code === 1) {
            this.setState({
                idFields: res.data || []
            })
        }
    }

    onEnableContinualTransfer = (e: any) => {
        if (e.target.checked) {
            this.loadIdFields();
        }
    }

    renderBreakpointContinualTransfer = () => {
        const { idFields } = this.state;
        const { form, setting, isIncrementMode, sourceMap, targetMap } = this.props;
        const { getFieldDecorator } = form;

        const sourceType = sourceMap.type.type;
        const targetType = targetMap.type.type;

        const idFieldInitialValue = isIncrementMode ? sourceMap.increColumn : setting.restoreColumnName;

        return (
            isRDB(sourceType) &&
            (
                isRDB(targetType) ||
                targetType === DATA_SOURCE.HIVE ||
                targetType === DATA_SOURCE.MAXCOMPUTE
            )
        )
            ? <div>
                <FormItem
                    {...formItemLayout}
                    label="断点续传"
                    className="txt-left"
                >
                    {getFieldDecorator('isRestore', {
                        rules: [],
                        initialValue: setting.isRestore
                    })(
                        <Checkbox onChange={this.onEnableContinualTransfer} checked={setting.isRestore}> 开启 </Checkbox>
                    )}
                    <HelpDoc doc="breakpointContinualTransferHelp" />
                </FormItem>
                {
                    setting.isRestore
                        ? <FormItem
                            {...formItemLayout}
                            label="标识字段"
                            key="restoreColumnName"
                        >
                            {getFieldDecorator('restoreColumnName', {
                                rules: [{
                                    required: true,
                                    message: '请选择标识字段'
                                }],
                                initialValue: idFieldInitialValue
                            })(
                                <Select
                                    showSearch
                                    placeholder="请选择标识字段"
                                    disabled={isIncrementMode} // 增量模式时，默认使用增量字段，此处禁用选项
                                >
                                    { idFields.map((o: any) => <Option key={o.key}>{o.key}（{o.type}）</Option>) }
                                </Select>
                            )}
                        </FormItem> : null
                }
            </div> : null;
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { setting, navtoStep } = this.props;
        const speedOption: any = [];
        const channelOption: any = [];
        const unLimitedOption: any[] = [
            <Option value='-1' key={-1}>不限制上传速率</Option>
        ]

        for (let i = 1; i <= 20; i++) {
            speedOption.push(<Option value={`${i}`} key={i}>{ i }</Option>)
        }
        for (let i = 1; i <= 5; i++) {
            channelOption.push(<Option value={`${i}`} key={i}>{i}</Option>)
        }

        return <div className="g-step4">
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="作业速率上限"
                    style={{ height: '32px' }}
                >
                    {getFieldDecorator('speed', {
                        rules: [{
                            required: true
                        }],
                        initialValue: `${setting.speed}`
                    })(
                        <AutoComplete
                            dataSource={unLimitedOption.concat(speedOption)}
                            // optionLabelProp="value"
                        >
                            <Input suffix="MB/s" />
                        </AutoComplete>
                    )}
                    <HelpDoc doc="jobSpeedLimit"/>
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="作业并发数"
                    style={{ height: '32px' }}
                >
                    {getFieldDecorator('channel', {
                        rules: [{
                            required: true
                        }],
                        initialValue: `${setting.channel}`
                    })(
                        <AutoComplete
                            dataSource={channelOption}
                            optionLabelProp="value"
                        />
                    )}
                    <HelpDoc doc="jobConcurrence" />
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="错误记录管理"
                    className="txt-left"
                >
                    {getFieldDecorator('isSaveDirty', {
                        rules: [],
                        initialValue: setting.isSaveDirty
                    })(
                        <Checkbox checked={setting.isSaveDirty}> 记录保存 </Checkbox>
                    )}
                    <HelpDoc doc="recordDirtyData" />
                </FormItem>
                {
                    setting.isSaveDirty ? <div>
                        <FormItem
                            {...formItemLayout}
                            label="脏数据写入hive表"
                        >
                            {getFieldDecorator('tableName', {
                                rules: [],
                                initialValue: setting.tableName || null
                            })(
                                <Input placeholder="默认系统分配" />
                            )}
                            <HelpDoc doc="recordDirtyData" />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="脏数据存储生命周期"
                            className="txt-left"
                        >
                            {getFieldDecorator('lifeDay', {
                                rules: [{
                                    required: true,
                                    message: '生命周期不可为空！'
                                }],
                                initialValue: setting.lifeDay || 90
                            })(
                                <Input type="hidden"/>
                            )}
                            <LifeCycle value={setting.lifeDay} width={120} onChange={this.onLifeDayChange}/>
                            <HelpDoc doc="recordDirtyData" />
                        </FormItem>
                    </div> : ''
                }
                <FormItem
                    {...formItemLayout}
                    label="错误记录数超过"
                >
                    {getFieldDecorator('record', {
                        rules: [],
                        initialValue: setting.record
                    })(
                        <InputNumber
                            style={{ float: 'left' }}
                        />
                    )}
                    <span style={{ float: 'left' }}>
                    条, 任务自动结束
                        <HelpDoc
                            doc="errorCount"
                        />
                    </span>
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="错误记录比例配置"
                >
                    <span style={{ float: 'left' }}>
                        任务执行结束后，统计错误记录占比，大于
                    </span>
                    {getFieldDecorator('percentage', {
                        rules: [],
                        initialValue: setting.percentage
                    })(
                        <InputNumber
                            style={{ float: 'left' }}
                        />
                    )}
                    <span style={{ float: 'left' }}>
                        %时，任务置为失败
                    </span>
                    <HelpDoc
                        doc="errorPercentConfig"
                    />
                </FormItem>
                { this.renderBreakpointContinualTransfer() }
            </Form>
            {!this.props.readonly && <div className="steps-action">
                <Button style={{ marginRight: 8 }} onClick={() => this.prev(navtoStep)}>上一步</Button>
                <Button type="primary" onClick={() => this.next(navtoStep)}>下一步</Button>
            </div>}
        </div>
    }

    prev (cb: any) {
        // eslint-disable-next-line
        cb.call(null, 2);
    }

    next (cb: any) {
        const { form } = this.props;
        form.validateFields((err: any, values: any) => {
            if (!err) {
                // eslint-disable-next-line
                cb.call(null, 4);
            }
        });
    }
}

const ChannelFormWrap = Form.create({
    onValuesChange: function (props: any, values: any) {
        const { changeChannelSetting, setting, isIncrementMode, sourceMap } = props;
        if (setting.isSaveDirty && !setting.lifeDay) {
            values.lifeDay = 90;
        }
        if (!setting.isSaveDirty) {
            values.tableName = null;
        }
        if (values.isRestore === false) {
            values.restoreColumnName = undefined;
        }
        // 增量模式下，开启断点续传字段默认与增量字段保存一致
        if (isIncrementMode && values.isRestore === true) {
            values.restoreColumnName = sourceMap.increColumn;
        }
        // Remove no use
        setting.syncModel = undefined;
        changeChannelSetting(values);
    }
})(ChannelForm);

class Channel extends React.Component<any, any> {
    render () {
        return <div>
            <ChannelFormWrap {...this.props} />
        </div>
    }
}

const mapState = (state: any) => {
    const { dataSync } = state.offlineTask;
    const { setting, targetMap, sourceMap } = dataSync;

    return { setting, targetMap, sourceMap };
};

const mapDispatch = (dispatch: any) => {
    return {
        changeChannelSetting (params: any) {
            dispatch({
                type: settingAction.CHANGE_CHANNEL_FIELDS,
                payload: params
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_DIRTY
            });
        }
    };
};

export default connect(mapState, mapDispatch)(Channel);
