import * as React from 'react';
import { connect } from 'react-redux';
import {
    Form, Input,
    Select, Button, AutoComplete
    // Checkbox
} from 'antd';

// import {
//     settingAction,
//     workbenchAction
// } from '../../../../store/modules/offlineTask/actionType';

import HelpDoc from '../../../../../views/helpDoc';
// import ajax from '../../../../api';

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
    _form: any;
    constructor (props: any) {
        super(props);
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { collectionData } = this.props;
        const setting = collectionData.setting || {};
        const speedOption: any = [];
        const channelOption: any = [];
        const unLimitedOption: any[] = [
            <Option value={`0`} key={0}>不限制上传速率</Option>
        ]
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
                            dataSource={unLimitedOption.concat(speedOption)}
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
        const setting = collectionData.setting;
        if (!setting) return {};
        return {
            speed: {
                value: setting.speed
            },
            channel: {
                value: setting.channel
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
