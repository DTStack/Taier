import * as React from 'react';
import { Input, Form, Tooltip } from 'antd';

import { formItemLayout, COMPONEMT_CONFIG_KEYS } from '../../../consts'
const FormItem = Form.Item;

export default class LearningConfig extends React.Component<any, any> {
    render () {
        const { singleButton, customView, isView, getFieldDecorator, kerberosView } = this.props;
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="learning.python3.path"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LEARNING}.learningPython3Path`, {
                        })(
                            <Input disabled={isView} placeholder="/root/anaconda3/bin/python3" />
                        )}
                    </FormItem>
                    <FormItem
                        label="learning.python2.path"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LEARNING}.learningPython2Path`, {
                        })(
                            <Input disabled={isView} placeholder="/root/anaconda2/bin/python2" />
                        )}
                    </FormItem>
                    <FormItem
                        label="learning.history.address"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LEARNING}.learningHistoryAddress`, {
                        })(
                            <Input disabled={isView} placeholder="rdos1:10021" />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="learning.history.webapp.address">learning.history.webapp.address</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LEARNING}.learningHistoryWebappAddress`, {
                        })(
                            <Input disabled={isView} placeholder="rdos1:19886" />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="learning.history.webapp.https.address">learning.history.webapp.https.address</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LEARNING}.learningHistoryWebappHttpsAddress`, {
                        })(
                            <Input disabled={isView} placeholder="rdos1:19885" />
                        )}
                    </FormItem>
                    {customView}
                    {kerberosView}
                </div>
                {/* config底部功能按钮（测试连通性、取消、保存） */}
                {singleButton}
            </React.Fragment>
        )
    }
}
