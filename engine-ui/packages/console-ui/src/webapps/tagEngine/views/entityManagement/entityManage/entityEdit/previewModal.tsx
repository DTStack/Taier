import * as React from 'react';
import { Modal } from 'antd';

export default class PrevieModal extends React.Component<any, any> {
    state: any = {

    }

    componentDidMount () {

    }

    onCancel = () => {
        this.props.onCancel();
    }

    onOK = () => {
        this.props.onOk();
    }

    render () {
        const { visible, infor } = this.props;

        return (
            <Modal
                title="预览标签值详情"
                wrapClassName="vertical-center-modal"
                visible={visible}
                onOk={this.onOK}
                onCancel={this.onCancel}
                maskClosable={false}
            >
                <div>仅展示20条</div>
                <div>{infor}</div>
            </Modal>
        )
    }
}
