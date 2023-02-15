import { cleanup, fireEvent, render, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import AddEngineModal from '..';

describe('Test AddEngineModal Component', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';
    });
    it('Should match snapshot', async () => {
        const { asFragment } = render(<AddEngineModal title="新增集群" visible />);
        expect(asFragment()).toMatchSnapshot();
    });

    it('Should validate form before confirm', async () => {
        const fn = jest.fn();
        const { getByTestId, container, getByText } = render(<AddEngineModal title="新增集群" visible onOk={fn} />);

        fireEvent.click(getByTestId('antd-mock-Modal-confirm'));
        await waitFor(() => {
            expect(getByText('集群标识不可为空！')).toBeInTheDocument();
        });

        fireEvent.change(container.querySelector('#clusterName')!, {
            target: { value: 'abc-abc' },
        });

        await waitFor(() => {
            expect(getByText('集群标识不能超过64字符，支持英文、数字、下划线')).toBeInTheDocument();
        });

        fireEvent.change(container.querySelector('#clusterName')!, {
            target: { value: new Array(100).fill('a').join('') },
        });

        await waitFor(() => {
            expect(getByText('集群标识不能超过64字符，支持英文、数字、下划线')).toBeInTheDocument();
        });

        fireEvent.change(container.querySelector('#clusterName')!, {
            target: { value: 'abc' },
        });
        fireEvent.click(getByTestId('antd-mock-Modal-confirm'));

        await waitFor(() => {
            expect(fn).toBeCalledWith({ clusterName: 'abc' });
        });
    });
});
