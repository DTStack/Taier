import React from 'react';
import { Input, Form } from 'antd';

import { formItemLayout } from '../../../consts'

const FormItem = Form.Item;

export class SparkThriftConfig extends React.Component {
    render () {
        const { isView, getFieldDecorator, customView, singleButton } = this.props;
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="JDBC URL"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.jdbcUrl', {
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
                        {getFieldDecorator('hiveConf.username')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="密码"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('hiveConf.password')(
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
export class CarbonDataConfig extends React.Component {
    render () {
        const { isView, getFieldDecorator, singleButton } = this.props;
        return (
            <React.Fragment>
                <div className="engine-config-content" style={{ width: '680px' }}>
                    <FormItem
                        label="JDBC URL"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('carbonConf.jdbcUrl', {
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
                        {getFieldDecorator('carbonConf.username')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                    <FormItem
                        label="密码"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('carbonConf.password')(
                            <Input disabled={isView} />
                        )}
                    </FormItem>
                </div>
                {singleButton}
            </React.Fragment>
        )
    }
}
