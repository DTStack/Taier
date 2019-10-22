import * as React from 'react';
import { Input, Form } from 'antd';

import { formItemLayout, COMPONEMT_CONFIG_KEYS } from '../../../consts'

const FormItem = Form.Item;

export default class HiveServerConfig extends React.Component<any, any> {
    render () {
        const { isView, getFieldDecorator, customView, singleButton, kerberosView } = this.props;
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="driverClassName"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.HIVESERVER}.driverClassName`, {
                            rules: [{
                                required: true,
                                message: '请输入driverClassName'
                            }],
                            initialValue: 'org.apache.hive.jdbc.HiveDriver'
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="JDBC URL"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.HIVESERVER}.jdbcUrl`, {
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
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.HIVESERVER}.username`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="密码"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.HIVESERVER}.password`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    {customView}
                    {kerberosView}
                </div>
                {singleButton}
            </React.Fragment>
        )
    }
}
