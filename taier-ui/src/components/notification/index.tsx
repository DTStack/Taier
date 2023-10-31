import { CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';
import molecule from '@dtinsight/molecule';
import { notification as antNotification } from 'antd';
import type { ArgsProps } from 'antd/lib/notification';

import './notification.scss';

type INotificationConfigs = Pick<ArgsProps, 'message'> & { key: string };

class Notification {
    private assertNotExistNotification(key: string) {
        const { data } = molecule.notification.getState();
        return !data?.find((n) => n.id === key);
    }
    private showNotification() {
        const { showNotifications } = molecule.notification.getState();
        if (!showNotifications) {
            molecule.notification.toggleNotification();
        }
    }
    private highlightNotification(key: string) {
        const notificationItem = document.getElementById(key)?.parentElement;
        if (notificationItem) {
            notificationItem.tabIndex = 0;
            notificationItem.focus();
        }
    }
    /**
     * Add a notification both in antd and molecule
     */
    openWithMolecule(config: INotificationConfigs) {
        const { showNotifications } = molecule.notification.getState();
        // the antd's notification pops up only when the molecule's notification invisible
        if (!showNotifications) {
            antNotification.open({
                key: config.key,
                message: config.message,
                placement: 'bottomRight',
                className: 'dt-notification',
                duration: 8,
                closeIcon: <></>,
                onClose() {},
                onClick: () => {
                    antNotification.close(config.key);
                    this.showNotification();
                    setTimeout(() => {
                        this.highlightNotification(config.key);
                    }, 0);
                },
            });
        }

        if (this.assertNotExistNotification(config.key)) {
            molecule.notification.add([
                {
                    id: config.key,
                    value: '',
                    render() {
                        return config.message;
                    },
                },
            ]);
        }
    }

    /**
     * Open a notification both in antd and molecule in bottomRight with danger icon
     */
    error({ key, message }: { key: string; message: string }) {
        this.openWithMolecule({
            key,
            message: (
                <>
                    <CloseCircleOutlined
                        style={{
                            color: 'var(--editorError-foreground)',
                            marginRight: 5,
                        }}
                    />
                    <span id={key} title={message}>
                        {message}
                    </span>
                </>
            ),
        });
    }

    success({ key, message }: { key: string; message: string }) {
        this.openWithMolecule({
            key,
            message: (
                <>
                    <CheckCircleOutlined
                        style={{
                            color: 'var(--terminal-ansiBrightGreen)',
                            marginRight: 5,
                        }}
                    />
                    <span id={key} title={message}>
                        {message}
                    </span>
                </>
            ),
        });
    }
}
export default new Notification();
