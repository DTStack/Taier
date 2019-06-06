import React from 'react';
import { Input, Form, Tooltip } from 'antd';

import { formItemLayout } from '../../../consts'
const FormItem = Form.Item;

export default class LearningConfig extends React.Component {
    render () {
        const { singleButton, customView, isView, getFieldDecorator } = this.props;
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="learning.python3.path"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('learningConf.learningPython3Path', {
                        })(
                            <Input disabled={isView} placeholder="/root/anaconda3/bin/python3" />
                        )}
                    </FormItem>
                    <FormItem
                        label="learning.python2.path"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('learningConf.learningPython2Path', {
                        })(
                            <Input disabled={isView} placeholder="/root/anaconda2/bin/python2" />
                        )}
                    </FormItem>
                    <FormItem
                        label="learning.history.address"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('learningConf.learningHistoryAddress', {
                        })(
                            <Input disabled={isView} placeholder="rdos1:10021" />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="learning.history.webapp.address">learning.history.webapp.address</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator('learningConf.learningHistoryWebappAddress', {
                        })(
                            <Input disabled={isView} placeholder="rdos1:19886" />
                        )}
                    </FormItem>
                    <FormItem
                        label={<Tooltip title="learning.history.webapp.https.address">learning.history.webapp.https.address</Tooltip>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator('learningConf.learningHistoryWebappHttpsAddress', {
                        })(
                            <Input disabled={isView} placeholder="rdos1:19885" />
                        )}
                    </FormItem>
                    {customView}
                </div>
                {/* config底部功能按钮（测试连通性、取消、保存） */}
                {singleButton}
            </React.Fragment>
        )
    }
}
