import * as React from 'react';
import { Modal, Icon, Button } from 'antd';
import './style.scss';

interface IProps {
    title: string;
    content: string;
    notice?: string;
    cancelText?: string;
    okText?: string;
    visible: boolean;
    onCancel: any;
    onOk: any;
}

interface IState {

}

export default class DeleteModal extends React.Component<IProps, IState> {

    state: IState = {

    }

    componentDidMount () {

    }

    render () {
        const { visible, title, content, notice = '提示', cancelText = '取消', okText = '删除' } = this.props;
        return (
            < Modal
                title={title}
                footer={null}
                onCancel={this.props.onCancel}
                visible={visible}
            >
                <div className="delete-modal">
                    <div className="content">
                        <Icon style={{ fontSize: 20, color: 'orange' }} type="exclamation-circle" />
                        <div className="notice">
                            <div style={{ fontSize: 16, color: '#333333', marginBottom: 10 }}>{notice}</div>
                            <div>{content}</div>
                        </div>
                    </div>
                    <div className="footer">
                        <Button size="large" style={{ marginLeft: 8 }} onClick={this.props.onOk} type="primary">{okText}</Button>
                        <Button size="large" onClick={this.props.onCancel}>{cancelText}</Button>
                    </div>
                </div>
            </Modal >
        )
    }
}