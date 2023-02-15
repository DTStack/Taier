import { fireConfirmOnModal } from '@/tests/utils';
import { cleanup, render, waitFor } from '@testing-library/react';
import { Input } from 'antd';
import FnModal from '../fnModal';
import { fillFormContent } from './fixtures/utils';

jest.useFakeTimers();
jest.mock('@/services/resourceManagerService', () => ({
    checkNotDir: jest.fn().mockResolvedValue(true),
}));
jest.mock('../../../components/folderPicker', () => ({ id, value, onChange }: any) => (
    <Input id={id} value={value} onChange={onChange} data-testid="mockFolderPicker" />
));
jest.mock('@/context', () => {
    const react = jest.requireActual('react');
    return react.createContext({
        supportJobTypes: [{ key: 1, value: 'spark' }],
    });
});

describe('Test FnModal Component for Function', () => {
    beforeEach(() => {
        cleanup();
    });
    it('Should match snapshots', () => {
        expect(render(<FnModal visible />).asFragment()).toMatchSnapshot('create new function');
        expect(
            render(
                <FnModal
                    visible
                    data={{
                        taskType: 1,
                        name: 'test',
                        id: 1,
                    }}
                />
            ).asFragment()
        ).toMatchSnapshot('edit function');
    });

    it('Should call onAddFunction when create a new function', async () => {
        const fn = jest.fn().mockResolvedValue(true);
        const onCloseFn = jest.fn();
        const { container, getByTestId, getAllByTestId } = render(
            <FnModal visible onAddFunction={fn} onClose={onCloseFn} />
        );
        const dom = document.createElement('div');
        dom.id = 'molecule';
        container.appendChild(dom);

        fillFormContent(getAllByTestId);

        fireConfirmOnModal(getByTestId);

        await waitFor(() => {
            expect(fn).toBeCalledWith({
                className: 'com.dtstack',
                commandFormate: 'main',
                name: 'mock',
                nodePid: '1',
                paramDesc: 'test',
                purpose: 'forTest',
                resourceId: '1',
                taskType: 5,
                udfType: 0,
            });
        });

        expect(onCloseFn).toBeCalled();
    });

    it('Should call onEditFunction when edit a function', async () => {
        const fn = jest.fn().mockResolvedValue(true);
        const onCloseFn = jest.fn();
        const { getByTestId } = render(
            <FnModal
                visible
                onEditFunction={fn}
                onClose={onCloseFn}
                data={{
                    id: 1,
                    className: 'com.dtstack',
                    commandFormate: 'main',
                    name: 'mock',
                    nodePid: 1,
                    paramDesc: 'test',
                    purpose: 'forTest',
                    resources: 1,
                    taskType: 5,
                    udfType: 0,
                }}
            />
        );

        fireConfirmOnModal(getByTestId);

        await waitFor(() => {
            expect(fn).toBeCalledWith({
                className: 'com.dtstack',
                commandFormate: 'main',
                id: 1,
                name: 'mock',
                nodePid: 1,
                paramDesc: 'test',
                purpose: 'forTest',
                resourceId: 1,
                resources: 1,
                taskType: 5,
                udfType: 0,
            });
        });
        expect(onCloseFn).toBeCalled();
    });
});
