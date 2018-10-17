import React, { Component } from 'react';

import { Modal, Button, } from 'antd';
import CopyToClipboard from 'react-copy-to-clipboard';

import Editor from 'widgets/editor';
import workbenchAction from '../../../../consts/workbenchActionType';

class DDLModal extends Component {

    handleOk = () => {
        message.info('复制成功，代码窗口即将关闭');
        setTimeout(() => {
            this.props.resetModal();
        }, 1000)
    }

    render () {
        const { modal, resetModal } = this.props;
        const visible =  modal && modal.visibleModal === workbenchAction.GENERATE_CREATE_SQL 
        ? true : false;

        return (
            <div>
                 <Modal className="m-codemodal"
                    title="建表语句"
                    width="750"
                    visible={visible}
                    maskClosable={false}
                    closable
                    onCancel={resetModal}
                    footer={[
                        <Button key="cancel" onClick={resetModal}>取消</Button>,
                        <CopyToClipboard key="copy" text={modal.modalData}
                            onCopy={this.handleOk.bind(this)}>
                            <Button type="primary">复制</Button>
                        </CopyToClipboard>
                    ]}
                >
                    <Editor value={modal.modalData} language="dtsql" options={{readOnly:false}} style={{ height: '400px' }} />
                </Modal>
            </div>
        )
    }
}

export default DDLModal