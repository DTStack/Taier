import React, { Component } from 'react';
import { Modal, Form } from 'antd';
// import { difference } from 'lodash';
import EngineSelect from '../../../../webapps/rdos/components/engineSelect';
import { formItemLayout, ENGINE_TYPE_ARRAY, ENGINE_TYPE_NAME } from '../../consts';
// import Api from '../../api/console';
const FormItem = Form.Item;
const defaultEngine = ENGINE_TYPE_NAME.HADOOP // hadoop

class AddEngineModal extends Component {
    render () {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const engineType = getFieldValue('engineType')
        return (
            <Modal
                visible={this.props.visible}
                title='新增引擎'
                onCancel={this.props.onCancel}
                onOk={() => this.props.onOk(engineType, this.isHaveEngine.bind(this))}
            >
                <Form>
                    <FormItem
                        label="引擎类型"
                        {...formItemLayout}
                    >
                        {getFieldDecorator('engineName', {
                            rules: [{
                                required: true,
                                message: '引擎类型不可为空！'
                            }],
                            initialValue: defaultEngine
                        })(
                            <EngineSelect
                                placeholder='请选择引擎类型'
                                tableTypes={ENGINE_TYPE_ARRAY}
                            />
                        )}
                    </FormItem>
                    {/* {this.renderDiffentEngine(hadoopFlag)} */}
                </Form>
            </Modal>
        )
    }
}
export default Form.create()(AddEngineModal);
