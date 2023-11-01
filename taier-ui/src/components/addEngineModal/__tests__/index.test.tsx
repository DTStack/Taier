import { cleanup, render, waitFor } from '@testing-library/react';
import { input,modal } from 'ant-design-testing';
import '@testing-library/jest-dom';

import { $ } from '@/tests/utils';
import AddEngineModal from '..';

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
        const { findByText } = render(<AddEngineModal title="新增集群" visible onOk={fn} />);

        modal.fireOk(document);
        expect(await findByText('集群标识不可为空！')).toBeInTheDocument();

        input.fireChange($<HTMLElement>('#clusterName')!, 'abc-abc');
        expect(await findByText('集群标识不能超过64字符，支持英文、数字、下划线')).toBeInTheDocument();

        input.fireChange($<HTMLElement>('#clusterName')!, new Array(100).fill('a').join(''));
        expect(await findByText('集群标识不能超过64字符，支持英文、数字、下划线')).toBeInTheDocument();

        input.fireChange($<HTMLElement>('#clusterName')!, 'abc');
        modal.fireOk(document);

        await waitFor(() => {
            expect(fn).toBeCalledWith({ clusterName: 'abc' });
        });
    });
});
