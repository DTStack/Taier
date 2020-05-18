import * as React from 'react';
import { Input, Form } from 'antd';

import { formItemLayout, COMPONEMT_CONFIG_KEYS } from '../../../consts'

const FormItem = Form.Item;

export default class LibraSqlConfig extends React.Component<any, any> {
    render () {
        const { isView, getFieldDecorator, singleButton } = this.props;
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="JDBC URL"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LIBRA_SQL}.jdbcUrl`, {
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
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LIBRA_SQL}.username`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="密码"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LIBRA_SQL}.password`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="driverClassName"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LIBRA_SQL}.driverClassName`, {
                            rules: [{
                                required: true,
                                message: '请输入driverClassName'
                            }]
                        })(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="useConnectionPool"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LIBRA_SQL}.useConnectionPool`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="maxPoolSize"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LIBRA_SQL}.maxPoolSize`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="minPoolSize"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LIBRA_SQL}.minPoolSize`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="initialPoolSize"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LIBRA_SQL}.initialPoolSize`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="jdbcIdel"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LIBRA_SQL}.jdbcIdel`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="maxRows"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LIBRA_SQL}.maxRows`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="queryTimeout"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LIBRA_SQL}.queryTimeout`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="checkTimeout"
                        {...formItemLayout}
                    >
                        {getFieldDecorator(`${COMPONEMT_CONFIG_KEYS.LIBRA_SQL}.checkTimeout`)(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                </div>
                {singleButton}
            </React.Fragment>
        )
    }
}
