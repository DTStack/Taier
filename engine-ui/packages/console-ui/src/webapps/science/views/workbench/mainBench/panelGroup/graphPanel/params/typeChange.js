import React, { PureComponent } from 'react';
import { Tabs, Form, Input, Radio, Checkbox, Button, Tooltip, Icon, InputNumber, Modal, Transfer } from 'antd';
import { isEmpty } from 'lodash';
const TabPane = Tabs.TabPane;
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
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
            item.disabled = item.type !== 'int'
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

/* 转化字段 */
class Transform extends PureComponent {
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
        const { data } = this.props;
        const { getFieldDecorator } = this.props.form;
        const btnStyle = { display: 'block', width: '100%', fontSize: 13, color: '#2491F7', fontWeight: 'normal', marginTop: 4 };
        const btnContent = isEmpty(data) ? '选择字段' : `已选择${data.chooseData.length}个字段`
        return (
            <Form className="params-form">
                <FormItem
                    label="转化类型及字段"
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('tableName', {
                        initialValue: 'double',
                        rules: [{ required: false }]
                    })(
                        <RadioGroup>
                            <Radio value={'double'}>Double类型</Radio>
                            <Radio value={'int'}>Int类型</Radio>
                            <Radio style={{ marginRight: 0 }} value={'string'}>String类型</Radio>
                        </RadioGroup>
                    )}
                    <Button style={btnStyle} onClick={this.handleChoose}>{btnContent}</Button>
                </FormItem>
                <FormItem
                    colon={false}
                    label="转化成Double类型异常时，默认填充值"
                    {...formItemLayout}
                >
                    {getFieldDecorator('partitionParam', {})(
                        <Input />
                    )}
                    <div style={{ display: 'grid', gridTemplateColumns: '78px auto' }}>
                        <Checkbox>保留原列</Checkbox>
                        <div className="supplementary" style={{ paddingTop: 5, lineHeight: 1.5 }}>{'若保留，原列名不变，处理过的列增加"typed_"前缀'}</div>
                    </div>
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
export class MemorySetting extends PureComponent {
    renderTooltips = (label, title) => {
        return (
            <div>
                {label}
                <Tooltip overlayClassName="big-tooltip" title={title}>
                    <Icon type="question-circle-o" className="supplementary" />
                </Tooltip>
            </div>
        )
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const inputStyle = { width: '100%' };
        return (
            <Form className="params-form">
                <FormItem
                    label={this.renderTooltips('占用内存大小', '可选项。正整数，单位MB，范围[256, 64 *1024]，默认512M')}
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('memorySize', {
                        initialValue: '512',
                        rules: [
                            { required: false },
                            { max: 65536, min: 256, message: '范围[256, 64 *1024]', type: 'number' }
                        ]
                    })(
                        <InputNumber
                            parser={value => parseInt(value)}
                            formatter={value => parseInt(value)}
                            style={inputStyle} />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label={this.renderTooltips('并发数', '可选项。正整数，范围[1, 9999]，默认并发数为1，单线程运行')}
                    {...formItemLayout}
                >
                    {getFieldDecorator('channel', {
                        initialValue: '1',
                        rules: [
                            { required: false },
                            { max: 9999, min: 1, message: '范围[1, 9999]', type: 'number' }
                        ]
                    })(
                        <InputNumber
                            parser={value => parseInt(value)}
                            formatter={value => parseInt(value)}
                            style={inputStyle} />
                    )}
                </FormItem>
            </Form>
        )
    }
}
/* main页面 */
class TypeChange extends PureComponent {
    state = {
        data: {}
    }
    componentDidMount () {
        this.getRenderData();
    }
    getRenderData = () => {
        const res = {
            code: 1,
            data: {
                chooseData: ['name']
            }
        }
        if (res.code == 1) {
            this.setState({
                data: res.data
            });
        }
    }
    render () {
        const { data } = this.state;
        const WrapTransform = Form.create()(Transform);
        const WrapMemorySetting = Form.create()(MemorySetting);
        return (
            <Tabs type="card" className="params-tabs">
                <TabPane tab="转化字段" key="1">
                    <WrapTransform data={data} />
                </TabPane>
                <TabPane tab="内存设置" key="2">
                    <WrapMemorySetting />
                </TabPane>
            </Tabs>
        );
    }
}

export default TypeChange;
