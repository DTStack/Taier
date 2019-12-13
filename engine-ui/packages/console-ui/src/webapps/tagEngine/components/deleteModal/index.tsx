import * as React from 'react';
import { Modal, Button } from 'antd';
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
    footer?: any;
}

export default class DeleteModal extends React.Component<IProps, any> {
    state: any = {

    }

    componentDidMount () {

    }

    render () {
        const { visible, title, content, notice = '提示', cancelText = '取消', okText = '删除', footer } = this.props;
        return (
            < Modal
                title={title}
                footer={null}
                onCancel={this.props.onCancel}
                visible={visible}
            >
                <div className="delete-modal">
                    <div className="content">
                        <i style={{ fontSize: 20, color: 'orange' }} className='iconfont iconwarning'></i>
                        <div className="notice">
                            <div style={{ fontSize: 16, color: '#333333', marginBottom: 10 }}>{notice}</div>
                            <div>{content}</div>
                        </div>
                    </div>
                    {footer ? <div className="footer">{footer}</div> : <div className="footer">
                        <Button size="large" style={{ marginLeft: 8 }} onClick={this.props.onOk} type="primary">{okText}</Button>
                        <Button size="large" onClick={this.props.onCancel}>{cancelText}</Button>
                    </div>}
                </div>
            </Modal >
        )
    }
}
