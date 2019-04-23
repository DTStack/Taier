/* eslint-disable no-unused-vars */
import React, { PureComponent } from 'react';
import { Form, Tabs, Button, Checkbox, Modal, Transfer } from 'antd';
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
        const { data } = this.props;
        const btnStyle = { display: 'block', width: '100%', fontSize: 13, color: '#2491F7', fontWeight: 'normal', marginTop: 4 };
        const btnContent = isEmpty(data) ? '选择字段' : `已选择${data.chooseData.length}个字段`
        return (
            <Form className="params-form">
                {<FormItem
                    label="默认全选"
                    colon={false}
                    {...formItemLayout}
                >
                    <Button style={btnStyle} onClick={this.handleChoose}>{btnContent}</Button>
                    <div style={{ display: 'grid', gridTemplateColumns: '78px auto' }}>
                        <Checkbox>保留原列</Checkbox>
                        <div className="supplementary" style={{ paddingTop: 5, lineHeight: 1.5 }}>{'若保留，原列名不变，处理过的列增加"nornalized_"前缀。'}</div>
                    </div>
                </FormItem>}
                {<div className="chooseWrap">
                    <ChooseModal
                        data={data}
                        visible={chooseModalVisible}
                        onCancel={this.handleCancel} />
                </div>}
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
class Normalise extends PureComponent {
    state = {
        data: {}
    }
    render () {
        const { data } = this.state;
        const WrapMemorySetting = Form.create()(MemorySetting);
        return (
            <Tabs type="card" className="params-tabs">
                <TabPane tab="字段设置" key="1">
                    <FieldSetting data={data} />
                </TabPane>
                <TabPane tab="内存设置" key="2">
                    <WrapMemorySetting />
                </TabPane>
            </Tabs>
        );
    }
}

export default Normalise;
