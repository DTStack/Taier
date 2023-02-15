import molecule from '@dtinsight/molecule';
import notification from '..';
import { notification as antNotification } from 'antd';
import { cleanup, render } from '@testing-library/react';

jest.mock('antd', () => ({
    notification: {
        open: jest.fn(),
    },
}));

describe('Test Notification Component', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';
    });
    it("Should pop up an antd's notification", () => {
        (molecule.notification.getState as jest.Mock).mockReset().mockImplementation(() => ({
            showNotifications: false,
        }));

        notification.openWithMolecule({ message: 'test', key: '1' });

        expect(antNotification.open).toBeCalledTimes(1);
    });

    it('Should not render duplicated notification', () => {
        (molecule.notification.getState as jest.Mock)
            .mockReset()
            .mockImplementationOnce(() => ({
                showNotifications: true,
                data: [],
            }))
            .mockImplementation(() => ({
                showNotifications: true,
                data: [{ id: '1' }],
            }));

        notification.openWithMolecule({ message: 'test', key: '1' });
        expect(molecule.notification.add).toBeCalledTimes(1);

        notification.openWithMolecule({ message: 'test', key: '1' });
        // Still called one time since duplicated key notification should be prevented
        expect(molecule.notification.add).toBeCalledTimes(1);
    });

    it('Should match snapshots', () => {
        (molecule.notification.getState as jest.Mock).mockReset().mockImplementation(() => ({
            showNotifications: true,
            data: [],
        }));

        (molecule.notification.add as jest.Mock).mockReset();

        notification.error({ message: 'test', key: '1' });

        const message = (molecule.notification.add as jest.Mock).mock.calls[0][0][0].render();
        expect(render(<>{message}</>).asFragment()).toMatchSnapshot();

        notification.success({ message: 'test', key: '1' });
        expect(
            render(<>{(molecule.notification.add as jest.Mock).mock.calls[1][0][0].render()}</>).asFragment()
        ).toMatchSnapshot();
    });
});
