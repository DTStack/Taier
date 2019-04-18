import React from 'react';

import { Dropdown, Menu, Icon, Button } from 'antd';
import SubmitModal from '../submitModal';
import ModelSubmitModal from '../modelSubmitModal';

class PublishButtons extends React.Component {
    state = {
        submitTaskModalVisible: false,
        submitModelModalVisble: false
    }
    onMenuClick (key) {
        switch (key) {
            case 'submit': {
                this.setState({
                    submitTaskModalVisible: true
                })
                break;
            }
            case 'modelPublish': {
                this.setState({
                    submitModelModalVisble: true
                })
                break;
            }
        }
    }
    renderMenu () {
        const { name } = this.props;
        return (
            <Menu onClick={({ key }) => { this.onMenuClick(key) }}>
                <Menu.Item key="submit">{name}提交</Menu.Item>
                <Menu.Item key="modelPublish">模型在线部署</Menu.Item>
            </Menu>
        )
    }
    render () {
        const { submitTaskModalVisible, submitModelModalVisble } = this.state;
        const { disabled, name, isNotebook, data } = this.props;
        return (
            <React.Fragment>
                <Dropdown disabled={disabled} overlay={this.renderMenu()} trigger={['click']}>
                    <Button icon="rocket" title="部署">
                        部署<Icon type="down" />
                    </Button>
                </Dropdown>
                <SubmitModal
                    name={name}
                    visible={submitTaskModalVisible}
                    onClose={() => {
                        this.setState({
                            submitTaskModalVisible: false
                        })
                    }}
                    onOk={(values) => {
                        return this.props.onSubmit(values);
                    }}
                />
                <ModelSubmitModal
                    data={data}
                    isNotebook={isNotebook}
                    visible={submitModelModalVisble}
                    onClose={() => {
                        this.setState({
                            submitModelModalVisble: false
                        })
                    }}
                    onOk={(values) => {
                        return this.props.onSubmitModel(values);
                    }}
                />
            </React.Fragment>
        )
    }
}
export default PublishButtons;
