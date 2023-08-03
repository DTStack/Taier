import { input, modal, form } from 'ant-design-testing';
import { cleanup, render } from '@testing-library/react';
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
        const { asFragment } = render(
            <div id="add-tenant-modal">
                <AddTenantModal />
            </div>
        );
        expect(asFragment()).toMatchSnapshot();
    });

    it('Should render error tips', async () => {
        const { findByText, container } = render(
            <div id="add-tenant-modal">
                <AddTenantModal />
            </div>
        );

        modal.fireOk(container);

        expect(await findByText('请输入租户名称!')).toBeInTheDocument();
        expect(await findByText('请输入租户标识!')).toBeInTheDocument();

        input.fireChange(form.queryFormItems(container, 0)!, new Array(100).fill('1').join(''));
        input.fireChange(form.queryFormItems(container, 1)!, '测试');

        expect(await findByText('请输入 64 个字符以内')).toBeInTheDocument();
        expect(await findByText('租户标识只能由字母、数字、下划线组成，且长度不超过64个字符!')).toBeInTheDocument();
    });

    it('Should call onSubmit', async () => {
        (api.addTenant as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
        });
        jest.spyOn(HTMLElement.prototype, 'remove').mockImplementation();
        const { container, findByText } = render(
            <div id="add-tenant-modal">
                <AddTenantModal />
            </div>
        );

        input.fireChange(form.queryFormItems(container, 0)!, 'DTStack');
        input.fireChange(form.queryFormItems(container, 1)!, 'test');

        modal.fireOk(container);

        expect(await findByText('新增成功')).toBeInTheDocument();
        expect(api.addTenant).toBeCalledWith({
            tenantName: 'DTStack',
            tenantIdentity: 'test',
            userId: 1,
        });
        expect(HTMLElement.prototype.remove).toBeCalled();
    });
});
