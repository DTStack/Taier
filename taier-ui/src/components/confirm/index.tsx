import { useLayoutEffect, useRef } from 'react';
import ReactDOM from 'react-dom/client';
import { WarningOutlined } from '@ant-design/icons';
import { Button, Modal } from 'antd';

import type { CatalogueDataProps } from '@/interface';
import { createElement } from '@/utils';
import './index.scss';

interface IConfirmProps {
    open: boolean;
    tab: CatalogueDataProps;
    onSave?: () => void;
    onUnSave?: () => void;
    onCancel?: () => void;
}

const wrapClassName = 'taier--confirm';
let root: ReactDOM.Root | null = null;

export function confirm({ tab, onSave, onUnSave, onCancel }: Omit<IConfirmProps, 'open'>) {
    if (!root) {
        const dom = createElement({ className: wrapClassName });
        root = ReactDOM.createRoot(dom);
    }

    root.render(
        <Confirm
            open
            tab={tab}
            onSave={() => {
                root!.render(<Confirm open={false} tab={tab} />);
                onSave?.();
            }}
            onUnSave={() => {
                root!.render(<Confirm open={false} tab={tab} />);
                onUnSave?.();
            }}
            onCancel={() => {
                root!.render(<Confirm open={false} tab={tab} />);
                onCancel?.();
            }}
        />
    );
}

export default function Confirm({ open, tab, onSave, onUnSave, onCancel }: IConfirmProps) {
    const button = useRef<HTMLButtonElement>(null);

    useLayoutEffect(() => {
        if (open) {
            button.current?.focus();
        }
    }, [open]);

    return (
        <Modal
            open={open}
            footer={null}
            closable={false}
            width={250}
            style={{ top: 200 }}
            wrapClassName="taier__confirm__container"
            onCancel={onCancel}
            destroyOnClose
        >
            <WarningOutlined className="taier__confirm__icon" />
            <div className="taier__confirm__title">是否要保存对 {tab.name} 的修改</div>
            <div className="taier__confirm__desc">如果不保存，将丢失修改</div>
            <div className="taier__confirm__btnGroups">
                <Button type="primary" block onClick={onSave} ref={button}>
                    保存
                </Button>
                <Button type="ghost" block onClick={onUnSave}>
                    不保存
                </Button>
                <Button type="ghost" block onClick={onCancel}>
                    取消
                </Button>
            </div>
        </Modal>
    );
}
