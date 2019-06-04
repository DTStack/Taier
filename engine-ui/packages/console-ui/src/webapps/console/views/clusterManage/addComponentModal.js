import React, { Component } from 'react';
import { Modal, Form, Checkbox, message } from 'antd';
// import Api from '../../api/console';
import { formItemLayout, hadoopEngineOptionsValue } from '../../consts';
const FormItem = Form.Item;
const CheckboxGroup = Checkbox.Group;
class AddComponent extends Component {
    state = {
        checkedList: [],
        checkAll: false
    }
    tranEngineData = () => {
        let hadoopOptionValue = [];
        hadoopEngineOptionsValue.map(item => {
            hadoopOptionValue.push(item.value)
        })
        return hadoopOptionValue
    }
    onChange = (checkedList) => {
        this.setState({
            checkedList,
            checkAll: checkedList.length === hadoopEngineOptionsValue.length
        })
    }
    onCheckAllChange = (e) => {
        this.setState({
            checkedList: e.target.checked ? this.tranEngineData() : [],
            checkAll: e.target.checked
        })
    }
    validateComponent = () => {
        let validate = false
        if (this.state.checkedList.length === 0) {
            message.error('请选择增加的组件！')
        } else {
            validate = true
        }
        return validate
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { visible, onCancel } = this.props;
        const { checkAll, checkedList } = this.state;
        return (
            <Modal
                title='增加组件'
                visible={visible}
                onCancel={onCancel}
                onOk={() => this.props.addComponent(checkedList, this.validateComponent)}
            >
                <Form>
                    <FormItem
                        label={<span>
                            <span style={{ color: '#f04134', fontSize: '12px', fontFamily: 'SimSun' }}>* </span>
                            <span>增加组件</span>
                        </span>}
                        {...formItemLayout}
                    >
                        {getFieldDecorator('engines', {
                        })(
                            <div>
                                <Checkbox
                                    onChange={this.onCheckAllChange}
                                    checked={checkAll}
                                >全选</Checkbox>
                                <CheckboxGroup
                                    options={hadoopEngineOptionsValue}
                                    value={checkedList}
                                    onChange={this.onChange}
                                />
                            </div>
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
export default Form.create()(AddComponent);
