import * as React from 'react';
import { connect } from 'react-redux';
import { get } from 'lodash';
import {
    Form, Input,
    Select, Button, AutoComplete, Checkbox
} from 'antd';

import HelpDoc from '../../../../../views/helpDoc';
import LifeCycle from '../../../../../components/lifeCycleSelect';

import { SettingMap } from '../../../../../store/modules/realtimeTask/collection';
import Api from '../../../../../api';
import { DATA_SOURCE } from '../../../../../comm/const';

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
        dirtySourceList: []
    }
    constructor (props: any) {
        super(props);
        const settingMap: SettingMap = get(props, 'collectionData.settingMap.isSaveDirty');
        if (settingMap) {
            this.loadSource();
        }
    }

    recordDirtyChange (e: React.ChangeEvent<HTMLInputElement>) {
        if (e.target.checked) {
            this.loadSource();
        }
    }
    async loadSource () {
        this.setState({
            dirtySourceList: []
        })
        let res = await Api.getSourceList({
            sourceType: DATA_SOURCE.HIVE
        });
        if (res && res.code == 1) {
            this.setState({
                dirtySourceList: res.data
            })
        }
    }
    render () {
        const { collectionData } = this.props;
        const { dirtySourceList } = this.state;
        const settingMap: SettingMap = get(collectionData, 'settingMap', {});
        const { isSaveDirty } = settingMap;
        const { getFieldDecorator } = this.props.form;
        const speedOption: any = [];
        const channelOption: any = [];
        const unLimitedOption: any[] = [
            <Option value='-1' key={-1}>不限制上传速率</Option>
        ]
        for (let i = 1; i <= 20; i++) {
            speedOption.push(<Option value={`${i}`} key={i}>{i}</Option>)
        }
        for (let i = 1; i <= 5; i++) {
            channelOption.push(<Option value={`${i}`} key={i}>{i}</Option>)
        }

        return <div className="g-step4">
            <Form>
                <FormItem
                    {...formItemLayout}
                    label="作业速率上限"
                >
                    {getFieldDecorator('speed', {
                        rules: [{
                            required: true
                        }]
                    })(
                        <AutoComplete
                            dataSource={unLimitedOption.concat(speedOption)}
                        // optionLabelProp="value"
                        >
                            <Input suffix="MB/s" />
                        </AutoComplete>
                    )}
                    <HelpDoc doc="jobSpeedLimit" />
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="作业并发数"
                >
                    {getFieldDecorator('channel', {
                        rules: [{
                            required: true
                        }]
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
                    label="错误记录数"
                >
                    {getFieldDecorator('isSaveDirty', {
                        rules: [{
                            required: false
                        }],
                        valuePropName: 'checked'
                    })(
                        <Checkbox onChange={this.recordDirtyChange.bind(this)}>记录保存</Checkbox>
                    )}
                    <HelpDoc doc="recordDirty" />
                </FormItem>
                {isSaveDirty ? (
                    <React.Fragment>
                        <FormItem
                            {...formItemLayout}
                            label="脏数据写入hive库"
                        >
                            {getFieldDecorator('sourceId', {
                                rules: [{
                                    required: true,
                                    message: '请选择脏数据写入的hive库'
                                }]
                            })(
                                <Select placeholder='请选择脏数据写入的hive库'>
                                    {dirtySourceList.map((source: any) => {
                                        return <Option key={source.id} value={source.id}>{source.dataName}</Option>
                                    })}
                                </Select>
                            )}
                            {/* <HelpDoc doc="dirtySource" /> */}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="脏数据写入hive表"
                        >
                            {getFieldDecorator('tableName', {
                                rules: [{
                                    required: false,
                                    message: '请填写脏数据写入的hive表'
                                }]
                            })(
                                <Input placeholder='系统默认分配' />
                            )}
                            <HelpDoc doc="dirtySource" />
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="脏数据存储天数"
                        >
                            {getFieldDecorator('lifeDay', {
                                rules: [{
                                    required: true,
                                    message: '请选择存储天数'
                                }]
                            })(
                                <LifeCycle width={120}/>
                            )}
                        </FormItem>
                    </React.Fragment>
                ) : null}
            </Form>
            {!this.props.readonly && (
                <div className="steps-action">
                    <Button style={{ marginRight: 8 }} onClick={() => this.prev()}>上一步</Button>
                    <Button type="primary" onClick={() => this.next()}>下一步</Button>
                </div>
            )}
        </div>
    }

    prev () {
        this.props.navtoStep(1)
    }

    next () {
        this.props.form.validateFields(null, {}, (err: any, values: any) => {
            if (!err) {
                this.props.navtoStep(3)
            }
        })
    }
}

const ChannelFormWrap = Form.create({
    onValuesChange: function (props: any, fields: any) {
        props.updateChannelControlMap(fields, false);
    },
    mapPropsToFields (props: any) {
        const { collectionData } = props;
        const settingMap: SettingMap = collectionData.settingMap;
        if (!settingMap) return {};
        return {
            speed: {
                value: settingMap.speed
            },
            channel: {
                value: settingMap.channel
            },
            recordDirty: {
                value: settingMap.isSaveDirty
            },
            dirtySource: {
                value: settingMap.sourceId
            },
            dirtyTable: {
                value: settingMap.tableName
            },
            lifeDay: {
                value: settingMap.lifeDay
            }
        }
    }
})(ChannelForm);

class ChannelControl extends React.Component<any, any> {
    render () {
        return <div>
            <ChannelFormWrap {...this.props} />
        </div>
    }
}

export default connect()(ChannelControl);
