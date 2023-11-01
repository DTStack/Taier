import { useContext, useState } from 'react';
import { Button, Modal, Space, Tooltip } from 'antd';

import Editor from '@/components/editor';
import context from '@/context/cluster';

interface IToolbarProps {
    current?: string;
    disabled?: boolean;
    onConnection?: () => Promise<true | string>;
    onSave?: () => Promise<void>;
}

export default function Toolbar({ current, disabled, onConnection, onSave }: IToolbarProps) {
    const { connectable } = useContext(context);
    const [connecting, setConnecting] = useState(false);
    const [visible, setVisible] = useState(false);

    const handleTestConnectable = () => {
        if (onConnection) {
            setConnecting(true);

            onConnection()
                .then((result) => {
                    if (typeof result === 'string') {
                        setVisible(true);
                    }
                })
                .finally(() => {
                    setConnecting(false);
                });
        }
    };

    const handleSaveComponent = () => {
        if (onConnection) {
            setConnecting(true);

            onSave?.().finally(() => {
                setConnecting(false);
            });
        }
    };

    const renderFailedReason = () => {
        if (typeof current === 'undefined') return null;

        if (typeof connectable[current] === 'string') {
            return (
                <Tooltip title="测试连通性失败">
                    <Button danger onClick={() => setVisible(true)}>
                        查看失败原因
                    </Button>
                </Tooltip>
            );
        }

        return null;
    };

    return (
        <>
            <Space>
                {renderFailedReason()}
                <Tooltip title="测试连通性需要先保存当前组件">
                    <Button disabled={disabled} loading={connecting} onClick={handleTestConnectable}>
                        测试连通性
                    </Button>
                </Tooltip>
                <Button disabled={disabled} type="primary" onClick={handleSaveComponent}>
                    保存当前组件
                </Button>
            </Space>
            <Modal
                width={800}
                title="错误信息"
                visible={visible}
                onCancel={() => setVisible(false)}
                footer={null}
                maskClosable
                destroyOnClose
            >
                <Editor
                    style={{ height: 500 }}
                    sync
                    value={connectable[current!] as string}
                    language="jsonlog"
                    options={{
                        readOnly: true,
                        minimap: {
                            enabled: false,
                        },
                    }}
                />
            </Modal>
        </>
    );
}
