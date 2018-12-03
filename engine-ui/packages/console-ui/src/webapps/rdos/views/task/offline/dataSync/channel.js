import React from 'react';
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

const FormItem = Form.Item;
const Option = Select.Option;

class ChannelForm extends React.Component {
    state = {
        isRecord: false
    }

    constructor (props) {
        super(props);
    }

    onLifeDayChange = val => {
        this.props.changeChannelSetting({
            lifeDay: val
        })
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { setting, navtoStep, form } = this.props;

        const formItemLayout = {
            labelCol: {
                sm: { span: 6 }
            },
            wrapperCol: {
                sm: { span: 14 }
            }
        };

        const speedOption = [];
        const channelOption = [];

        for (let i = 1; i <= 20; i++) {
            speedOption.push(<Option value={`${i}`} key={i}>{ i }MB/s</Option>)
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
                            dataSource={speedOption}
                            optionLabelProp="value"
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
            </Form>
            {!this.props.readonly && <div className="steps-action">
                <Button style={{ marginRight: 8 }} onClick={() => this.prev(navtoStep)}>上一步</Button>
                <Button type="primary" onClick={() => this.next(navtoStep)}>下一步</Button>
            </div>}
        </div>
    }

    prev (cb) {
        // eslint-disable-next-line
        cb.call(null, 2);
    }

    next (cb) {
        const { form } = this.props;
        form.validateFields((err, values) => {
            if (!err) {
                // eslint-disable-next-line
                cb.call(null, 4);
            }
        });
    }
}

const ChannelFormWrap = Form.create({
    onValuesChange: function (props, values) {
        const { changeChannelSetting, setting } = props;
        if (setting.isSaveDirty && !setting.lifeDay) {
            values.lifeDay = 90;
        }
        if (!setting.isSaveDirty) {
            values.tableName = null;
        }
        changeChannelSetting(values);
    }
})(ChannelForm);

class Channel extends React.Component {
    render () {
        return <div>
            <ChannelFormWrap {...this.props} />
        </div>
    }
}

const mapState = state => {
    const { dataSync } = state.offlineTask;
    const { setting, targetMap } = dataSync;

    return { setting, targetMap };
};

const mapDispatch = dispatch => {
    return {
        changeChannelSetting (params) {
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
