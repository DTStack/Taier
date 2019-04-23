import React, { PureComponent } from 'react';
import { Tabs, Form, Button, Modal, Transfer, Select, InputNumber } from 'antd';
import { MemorySetting as BaseMemorySetting } from './typeChange';
import { isEmpty } from 'lodash';
const TabPane = Tabs.TabPane;
const Option = Select.Option;
const FormItem = Form.Item;
const formItemLayout = {
    labelCol: {
        span: 24
    },
    wrapperCol: {
        span: 24
    }
};
const inputStyle = {
    width: '100%'
}
/* 选择字段弹出框 */
class ChooseModal extends PureComponent {
    state = {
        sourceData: [],
        targetKeys: []
    }
    componentDidMount () {
        this.getSourceData();
    }
    getSourceData = () => {
        const { data } = this.props;
        const res = {
            code: 1,
            data: [{
                title: 'name',
                type: 'string'
            }, {
                title: 'age',
                type: 'int'
            }]
        }
        const sourceData = [].concat(res.data).map((item) => {
            item.disabled = item.type !== 'int';
            return item;
        });
        const targetKeys = sourceData
            .filter(
                o => isEmpty(data) ? false : data.chooseData.findIndex(title => title === o.title) > -1
            ).map(
                item => item.title
            );
        if (res.code == 1) {
            this.setState({
                sourceData,
                targetKeys
            })
        }
    }
    handleCancel = () => {
        this.props.onCancel();
    }
    filterOption = (inputValue, option) => {
        return option.title.indexOf(inputValue) > -1;
    }
    handleChange = (targetKeys) => {
        this.setState({ targetKeys });
    }
    render () {
        const { visible } = this.props;
        return (
            <Modal
                title="选择字段"
                visible={visible}
                onOk={this.handleOk}
                onCancel={this.handleCancel}
                getContainer={() => document.querySelector('.chooseWrap')}
            >
                <Transfer
                    className="params-transfer"
                    rowKey={record => record.title}
                    dataSource={this.state.sourceData}
                    showSearch
                    filterOption={this.filterOption}
                    targetKeys={this.state.targetKeys}
                    onChange={this.handleChange}
                    render={item => item.title}
                />
            </Modal>
        );
    }
}
/* 参数设置 */
class paramSetting extends PureComponent {
    state = {
        regexDatas: [{
            value: 'none',
            name: 'none'
        }, {
            value: 'L1',
            name: 'L1'
        }, {
            value: 'L2',
            name: 'L2'
        }]
    }
    render () {
        const { regexDatas } = this.state;
        const { getFieldDecorator } = this.props.form;
        return (
            <Form className="params-form">
                <FormItem
                    label='正则项'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('regex', {
                        initialValue: 'none',
                        rules: [{ required: false }]
                    })(
                        <Select placeholder="请选择目标列">
                            {regexDatas.map((item, index) => {
                                return <Option key={index} value={item.value}>{item.name}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='最大迭代次数'
                    {...formItemLayout}
                >
                    {getFieldDecorator('maxIterations', {
                        initialValue: 100,
                        rules: [{ required: false }]
                    })(
                        <InputNumber
                            parser={value => parseInt(value)}
                            formatter={value => parseInt(value)}
                            style={inputStyle}
                        />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label={<div>正则系数<span className="supplementary">正则项为None时，此值无效</span></div>}
                    {...formItemLayout}
                >
                    {getFieldDecorator('regexCoefficient', {
                        initialValue: 1,
                        rules: [{ required: false }]
                    })(
                        <InputNumber
                            parser={value => parseInt(value)}
                            formatter={value => parseInt(value)}
                            style={inputStyle}
                        />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='最小收敛误差'
                    {...formItemLayout}
                >
                    {getFieldDecorator('minConvergence', {
                        initialValue: 0.000001,
                        rules: [{ required: false }]
                    })(
                        <InputNumber
                            step={0.000001}
                            style={inputStyle}
                        />
                    )}
                </FormItem>
            </Form>
        )
    }
}
/* 字段设置 */
class FieldSetting extends PureComponent {
    state = {
        chooseModalVisible: false,
        columns: [{
            value: '0',
            name: 'name'
        }, {
            value: '1',
            name: 'age'
        }]
    }
    handleChoose = () => {
        this.setState({
            chooseModalVisible: true
        });
    }
    handleCancel = () => {
        this.setState({
            chooseModalVisible: false
        });
    }
    render () {
        const { chooseModalVisible, columns } = this.state;
        const { data } = this.props;
        const { getFieldDecorator } = this.props.form;
        const btnStyle = { display: 'block', width: '100%', fontSize: 13, color: '#2491F7', fontWeight: 'normal', marginTop: 4 };
        const btnContent = isEmpty(data) ? '选择字段' : `已选择${data.chooseData.length}个字段`
        return (
            <Form className="params-form">
                <FormItem
                    label={<div style={{ display: 'inline-block' }}>训练特征列<span className="supplementary">支持double、int类型字段</span></div>}
                    colon={false}
                    required
                    {...formItemLayout}
                >
                    <Button style={btnStyle} onClick={this.handleChoose}>{btnContent}</Button>
                </FormItem>
                <FormItem
                    colon={false}
                    label='目标列'
                    {...formItemLayout}
                >
                    {getFieldDecorator('targetColumn', {
                        rules: [{ required: true, message: '请选择目标列！' }]
                    })(
                        <Select placeholder="请选择目标列">
                            {columns.map((item, index) => {
                                return <Option key={index} value={item.value}>{item.name}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='正类值'
                    {...formItemLayout}
                >
                    {getFieldDecorator('positiveValue', {
                        initialValue: '1',
                        rules: [{ required: true }]
                    })(
                        <Select>
                            <Option value={'1'}>1</Option>
                        </Select>
                    )}
                </FormItem>
                <div className="chooseWrap">
                    <ChooseModal
                        data={data}
                        visible={chooseModalVisible}
                        onCancel={this.handleCancel} />
                </div>
            </Form>
        )
    }
}
/* 内存设置 */
class MemorySetting extends BaseMemorySetting {
    constructor (props) {
        super(props)
    }
}
class LogisticRegression extends PureComponent {
    state = {
        data: {}
    }
    render () {
        const { data } = this.state;
        const WrapFieldSetting = Form.create()(FieldSetting);
        const WrapParamSetting = Form.create()(paramSetting);
        const WrapMemorySetting = Form.create()(MemorySetting);
        return (
            <Tabs type="card" className="params-tabs">
                <TabPane tab="字段设置" key="1">
                    <WrapFieldSetting data={data} />
                </TabPane>
                <TabPane tab="参数设置" key="2">
                    <WrapParamSetting />
                </TabPane>
                <TabPane tab="内存设置" key="3">
                    <WrapMemorySetting />
                </TabPane>
            </Tabs>
        );
    }
}

export default LogisticRegression;
