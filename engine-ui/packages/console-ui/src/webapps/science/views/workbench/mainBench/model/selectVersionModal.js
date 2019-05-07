import React from 'react';

import { Modal, Form, Select, Alert, message } from 'antd';

import { formItemLayout } from '../../../../consts';
import api from '../../../../api/model'

const Option = Select.Option;

class SelectVersionsModal extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            versionsList: [],
            selectVersion: props.data && props.data.version
        }
    }
    componentDidMount () {
        this.loadData();
    }
    loadData = async () => {
        const { data } = this.props;
        if (!data) {
            return;
        }
        this.setState({
            loading: true
        })
        let res = await api.getModelVersions({
            modelId: data.id
        });
        if (res && res.code == 1) {
            this.setState({
                versionsList: res.data
            })
        }
        this.setState({
            loading: false
        })
    }
    async changeVersion () {
        const { selectVersion } = this.state;
        const { data } = this.props;
        let res = await api.switchVersion({
            modelId: data.id,
            version: selectVersion
        })
        if (res && res.code == 1) {
            message.success('操作成功');
            this.props.onOk();
        }
    }
    render () {
        const { versionsList = [], selectVersion } = this.state;
        const { visible, onCancel } = this.props;
        return (
            <Modal
                visible={visible}
                onCancel={onCancel}
                onOk={this.changeVersion.bind(this)}
                title='切换版本'
            >
                <Alert style={{ marginBottom: '20px' }} message="注：切换版本时，由于某些模型较大，切换可能需要较长时间，请耐心等待。" type="info" />
                <Form.Item
                    {...formItemLayout}
                    label='选择版本'
                >
                    <Select onSelect={(value) => {
                        this.setState({
                            selectVersion: value
                        })
                    }} style={{ width: '100%' }} value={selectVersion} >
                        {versionsList.map((version) => {
                            console.log(version.version)
                            return <Option key={version.version} value={version.version}>v{version.version}</Option>
                        })}
                    </Select>
                </Form.Item>
            </Modal>
        )
    }
}
export default SelectVersionsModal;
