import React, { Component } from 'react';
import { Modal, Form, Select } from 'antd';
import { difference } from 'lodash';
import { formItemLayout, otherEngineType, huaWeiOptions } from '../../consts';
// import { hashHistory } from 'react-router';
// import Api from '../../api/console';
const FormItem = Form.Item;
const Option = Select.Option;
class AddEngineModal extends Component {
    getEngineOptions = () => {
        const { clusterType, engineSelectedLists } = this.props;
        const isHuawei = clusterType === 'huawei';
        const unSelectEngine = difference((isHuawei ? huaWeiOptions : otherEngineType), engineSelectedLists);
        return unSelectEngine && unSelectEngine.map(item => {
            return <Option value={`${item}`} key={`${item}`}>{item}</Option>
        })
    }
    isHaveEngine () {
        let validate = false
        this.props.form.validateFields(null, {}, (err, value) => {
            if (!err) {
                validate = true
            }
        })
        return validate
    }
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
                        label='增加引擎'
                        {...formItemLayout}
                    >
                        {getFieldDecorator('engineType', {
                            rules: [{
                                required: true,
                                message: '请选择引擎！'
                            }]
                        })(
                            <Select>
                                {this.getEngineOptions()}
                            </Select>
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
export default Form.create()(AddEngineModal);
