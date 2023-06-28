import { cleanup, render, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import AddEngineModal from '..';
import { modal, input } from 'ant-design-testing';
import { $ } from '@/tests/utils';

describe('Test AddEngineModal Component', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';
    });
    it('Should match snapshot', async () => {
        render(<AddEngineModal title="新增集群" visible />);
        expect(document.body).toMatchSnapshot();
    });

    it('Should validate form before confirm', async () => {
        const fn = jest.fn();
        const { getByText } = render(<AddEngineModal title="新增集群" visible onOk={fn} />);

        modal.fireOk(document);
        await waitFor(() => {
            expect(getByText('集群标识不可为空！')).toBeInTheDocument();
        });

        input.fireChange($<HTMLElement>('#clusterName')!, 'abc-abc');

        await waitFor(() => {
            expect(getByText('集群标识不能超过64字符，支持英文、数字、下划线')).toBeInTheDocument();
        });

        input.fireChange($<HTMLElement>('#clusterName')!, new Array(100).fill('a').join(''));

        await waitFor(() => {
            expect(getByText('集群标识不能超过64字符，支持英文、数字、下划线')).toBeInTheDocument();
        });

        input.fireChange($<HTMLElement>('#clusterName')!, 'abc');
        modal.fireOk(document);

        await waitFor(() => {
            expect(fn).toBeCalledWith({ clusterName: 'abc' });
        });
    });
});
