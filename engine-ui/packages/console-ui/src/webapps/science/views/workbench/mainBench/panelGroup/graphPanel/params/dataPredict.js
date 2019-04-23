import React, { PureComponent } from 'react';
import { Form, Tabs, Button, Modal, Transfer, Input } from 'antd';
import { isEmpty } from 'lodash';
import { MemorySetting as BaseMemorySetting } from './typeChange';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const formItemLayout = {
    labelCol: {
        span: 24
    },
    wrapperCol: {
        span: 24
    }
};
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
/* 字段设置 */
class FieldSetting extends PureComponent {
    state = {
        chooseModalVisible: false
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
        const { chooseModalVisible } = this.state;
        const { getFieldDecorator } = this.props.form;
        const { data } = this.props;
        const btnStyle = { display: 'block', width: '100%', fontSize: 13, color: '#2491F7', fontWeight: 'normal', marginTop: 4 };
        const btnContent = isEmpty(data) ? '选择字段' : `已选择${data.chooseData.length}个字段`
        return (
            <Form className="params-form">
                <FormItem
                    label={<div style={{ display: 'inline-block' }}>特征类<span className="supplementary">默认全选</span></div>}
                    colon={false}
                    required
                    {...formItemLayout}
                >
                    <Button style={btnStyle} onClick={this.handleChoose}>{btnContent}</Button>
                </FormItem>
                <FormItem
                    label={<div style={{ display: 'inline-block' }}>原样输出列<span className="supplementary">推荐添加label列，方便评估</span></div>}
                    colon={false}
                    required
                    {...formItemLayout}
                >
                    <Button style={btnStyle} onClick={this.handleChoose}>{btnContent}</Button>
                </FormItem>
                <FormItem
                    colon={false}
                    label='输出结果列名'
                    {...formItemLayout}
                >
                    {getFieldDecorator('outputResult', {
                        initialvalue: 'forecast_result',
                        rules: [
                            { required: true, message: '请输入输出结果列名' },
                            {
                                pattern: /^(\w)$/,
                                message: '表名称只能由字母、数字、下划线组成!'
                            }
                        ]
                    })(
                        <Input placeholder="请输入输出结果列名" />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='输出分数列名'
                    {...formItemLayout}
                >
                    {getFieldDecorator('outputScore', {
                        initialvalue: 'forecast_score',
                        rules: [
                            { required: true, message: '请输入输出分数列名' },
                            {
                                pattern: /^(\w)$/,
                                message: '表名称只能由字母、数字、下划线组成!'
                            }
                        ]
                    })(
                        <Input placeholder="请输入输出分数列名" />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='输出详细列名'
                    {...formItemLayout}
                >
                    {getFieldDecorator('outputDetail', {
                        initialvalue: 'forecast_detail',
                        rules: [
                            { required: true, message: '请输入输出详细列名' },
                            {
                                pattern: /^(\w)$/,
                                message: '表名称只能由字母、数字、下划线组成!'
                            }
                        ]
                    })(
                        <Input placeholder="请输入输出详细列名" />
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
class DataPredict extends PureComponent {
    state = {
        data: {}
    }
    render () {
        const { data } = this.state;
        const WrapFieldSetting = Form.create()(FieldSetting);
        const WrapMemorySetting = Form.create()(MemorySetting);
        return (
            <Tabs type="card" className="params-tabs">
                <TabPane tab="字段设置" key="1">
                    <WrapFieldSetting data={data} />
                </TabPane>
                <TabPane tab="内存设置" key="2">
                    <WrapMemorySetting />
                </TabPane>
            </Tabs>
        );
    }
}

export default DataPredict;
