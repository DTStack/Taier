import React from 'react';
import { Input, Form } from 'antd';

import { formItemLayout } from '../../../consts'

const FormItem = Form.Item;

export default class HiveServerConfig extends React.Component {
    render () {
        const { isView, getFieldDecorator, customView, singleButton } = this.props;
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="JDBC URL"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveServerConf.jdbcUrl', {
                            rules: [{
                                required: true,
                                message: '请输入jdbcUrl'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="用户名"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveServerConf.username')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="密码"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveServerConf.password')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    { customView }
                </div>
                {singleButton}
            </React.Fragment>
        )
    }
}
