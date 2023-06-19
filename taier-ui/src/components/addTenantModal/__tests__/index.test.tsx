import { fireConfirmOnModal } from '@/tests/utils';
import { input } from 'ant-design-testing';
import { cleanup, render, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import AddTenantModal from '..';
import api from '@/api';

jest.mock('@/api');
jest.mock('@/utils', () => ({
    getUserId: jest.fn(() => 1),
}));

describe('Test AddTenantModal', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';
    });
    it('Should match snapshot', () => {
        const { asFragment } = render(<AddTenantModal />);
        expect(asFragment()).toMatchSnapshot();
    });

    it('Should render error tips', async () => {
        const { getByTestId, getByText, container } = render(<AddTenantModal />);
        fireConfirmOnModal(getByTestId);

        await waitFor(() => {
            expect(getByText('请输入租户名称!')).toBeInTheDocument();
            expect(getByText('请输入租户标识!')).toBeInTheDocument();
        });

        const formItems = container.querySelectorAll<HTMLElement>('.ant-form-item-control');
        input.fireChange(formItems[0], new Array(100).fill('1').join(''));
        input.fireChange(formItems[1], '测试');

        await waitFor(() => {
            expect(getByText('请输入 64 个字符以内')).toBeInTheDocument();
            expect(getByText('租户标识只能由字母、数字、下划线组成，且长度不超过64个字符!')).toBeInTheDocument();
        });
    });

    it('Should call onSubmit', async () => {
        (api.addTenant as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
        });
        const { getByTestId, container } = render(<AddTenantModal />);

        const formItems = container.querySelectorAll<HTMLElement>('.ant-form-item-control');
        input.fireChange(formItems[0], 'DTStack');
        input.fireChange(formItems[1], 'test');

        fireConfirmOnModal(getByTestId);
        await waitFor(() => {
            expect(api.addTenant).toBeCalledWith({
                tenantName: 'DTStack',
                tenantIdentity: 'test',
                userId: 1,
            });
        });
    });
});
