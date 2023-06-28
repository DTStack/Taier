import Function from '../';
import { cleanup, fireEvent, render, waitFor } from '@testing-library/react';
import { Input } from 'antd';
import { FolderTree } from '@dtinsight/molecule/esm/workbench/sidebar/explore';
import { FUNCTOIN_ACTIONS } from '@/constant';
import api from '@/api';
import functionManagerService from '@/services/functionManagerService';
import { act } from 'react-dom/test-utils';
import '@testing-library/jest-dom';
import { TreeViewUtil } from '@dtinsight/molecule/esm/common/treeUtil';
import { fillFormContent } from './fixtures/utils';
import { catalogueService } from '@/services';
import { input, modal } from 'ant-design-testing';

jest.useFakeTimers();
jest.mock('@/api');
jest.mock('@/services', () => ({
    catalogueService: {
        loadTreeNode: jest.fn(),
    },
}));
jest.mock('@/services/functionManagerService', () => ({
    setActive: jest.fn(),
    getState: jest.fn(() => ({
        folderTree: {
            data: [
                {
                    id: 1,
                },
            ],
        },
    })),
    get: jest.fn(() => ({
        data: {},
    })),
}));
jest.mock('@/services/resourceManagerService', () => ({
    checkNotDir: jest.fn().mockResolvedValue(true),
}));
jest.mock('@/components/folderPicker', () => ({ id, value, onChange }: any) => (
    <Input data-testid="mockFolderPicker" id={id} value={value} onChange={(e) => onChange(e.target.value)} />
));

describe('Test Function Manage', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';
        (catalogueService.loadTreeNode as jest.Mock).mockReset();
        const mockFolderTree: any = {
            data: [
                {
                    id: 1,
                    name: 'root',
                    data: {
                        level: 1,
                    },
                },
                {
                    id: 2,
                    name: 'folder',
                    data: {
                        id: 2,
                        name: 'test',
                        parentId: 1,
                    },
                },
                {
                    id: 3,
                    name: 'file',
                    isLeaf: true,
                    data: {
                        type: 'file',
                    },
                },
            ],
        };

        const mo = document.createElement('div');
        mo.id = 'molecule';
        document.body.appendChild(mo);

        (FolderTree as unknown as jest.Mock)
            .mockReset()
            .mockImplementation(({ folderTree = mockFolderTree, onRightClick, onSelectFile, onClickContextMenu }) => (
                <div data-testid="mockFolderTree">
                    {folderTree?.data?.map((i: any) => (
                        <div
                            key={i.id}
                            data-testid="mockFolderTreeNode"
                            onContextMenu={() => {
                                const context = onRightClick(i) as {
                                    id: string;
                                    name: string;
                                }[];

                                if (document.querySelector('#mockFolderTreeContextMenu')) {
                                    const div = document.querySelector<HTMLDivElement>('#mockFolderTreeContextMenu')!;

                                    div.innerHTML = '';

                                    context.forEach((c) => {
                                        const node = document.createElement('div');
                                        node.dataset.testid = c.id;
                                        node.innerText = c.name;
                                        node.onclick = () => {
                                            onClickContextMenu(c, i);
                                        };
                                        div.appendChild(node);
                                    });
                                } else {
                                    const div = document.createElement('div');
                                    div.dataset.testid = 'mockFolderTreeContextMenu';
                                    div.id = 'mockFolderTreeContextMenu';

                                    context.forEach((c) => {
                                        const node = document.createElement('div');
                                        node.dataset.testid = c.id;
                                        node.innerText = c.name;
                                        node.onclick = () => {
                                            onClickContextMenu(c, i);
                                        };
                                        div.appendChild(node);
                                    });

                                    document.body.appendChild(div);
                                }
                            }}
                            onClick={() => onSelectFile(i)}
                        >
                            {i.name}
                        </div>
                    ))}
                </div>
            ));
    });

    it('Should match snapshot', () => {
        const { asFragment } = render(
            <Function
                panel={{
                    id: 'FunctionManager',
                    icon: 'variable-group',
                    name: '函数管理',
                    title: '函数管理',
                }}
                headerToolBar={[
                    {
                        id: 'refresh',
                        title: '刷新',
                        icon: 'refresh',
                    },
                ]}
            />
        );

        expect(asFragment()).toMatchSnapshot();
    });

    it('Should support create new folder', () => {
        const { getAllByTestId, getByTestId } = render(
            <Function
                panel={{
                    id: 'FunctionManager',
                    icon: 'variable-group',
                    name: '函数管理',
                    title: '函数管理',
                }}
                headerToolBar={[
                    {
                        id: 'refresh',
                        title: '刷新',
                        icon: 'refresh',
                    },
                ]}
            />
        );

        const nodes = getAllByTestId('mockFolderTreeNode');

        fireEvent.contextMenu(nodes[0]);
        [FUNCTOIN_ACTIONS.CREATE_FUNCTION, FUNCTOIN_ACTIONS.CREATE_FOLDER].forEach((item) => {
            expect(getByTestId(item.id)).toBeInTheDocument();
        });

        fireEvent.contextMenu(nodes[1]);
        [
            FUNCTOIN_ACTIONS.CREATE_FUNCTION,
            FUNCTOIN_ACTIONS.CREATE_FOLDER,
            FUNCTOIN_ACTIONS.EDIT,
            FUNCTOIN_ACTIONS.DELETE,
        ].forEach((item) => {
            expect(getByTestId(item.id)).toBeInTheDocument();
        });

        fireEvent.contextMenu(nodes[2]);
        [FUNCTOIN_ACTIONS.EDIT, FUNCTOIN_ACTIONS.DELETE].forEach((item) => {
            expect(getByTestId(item.id)).toBeInTheDocument();
        });
    });

    it("Should support view function's detail", async () => {
        (api.getOfflineFn as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
        });

        const { getAllByTestId } = render(
            <Function
                panel={{
                    id: 'FunctionManager',
                    icon: 'variable-group',
                    name: '函数管理',
                    title: '函数管理',
                }}
                headerToolBar={[
                    {
                        id: 'refresh',
                        title: '刷新',
                        icon: 'refresh',
                    },
                ]}
            />
        );

        const nodes = getAllByTestId('mockFolderTreeNode');

        await act(async () => {
            fireEvent.click(nodes[0]);
        });
        expect(functionManagerService.setActive).lastCalledWith(1);
        expect(api.getOfflineFn).not.toBeCalled();

        await act(async () => {
            fireEvent.click(nodes[2]);
        });
        expect(functionManagerService.setActive).lastCalledWith(3);
        expect(api.getOfflineFn).toBeCalled();
    });

    it('Should support create new function', async () => {
        (api.addOfflineFunction as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
        });
        (TreeViewUtil as jest.Mock).mockReset().mockImplementation(() => ({
            getHashMap: jest
                .fn()
                .mockImplementationOnce(() => ({
                    node: {
                        data: {
                            catalogueType: 'FunctionManager',
                        },
                    },
                    parent: {},
                }))
                .mockImplementation(() => ({
                    node: {
                        data: {
                            catalogueType: 'SparkSQLFunction',
                        },
                    },
                })),
        }));

        const { getAllByTestId, getByTestId, getByText } = render(
            <Function
                panel={{
                    id: 'FunctionManager',
                    icon: 'variable-group',
                    name: '函数管理',
                    title: '函数管理',
                }}
                headerToolBar={[
                    {
                        id: 'refresh',
                        title: '刷新',
                        icon: 'refresh',
                    },
                ]}
            />
        );

        const nodes = getAllByTestId('mockFolderTreeNode');

        // Open modal
        fireEvent.contextMenu(nodes[0]);
        fireEvent.click(getByTestId(FUNCTOIN_ACTIONS.CREATE_FUNCTION.id));

        await waitFor(() => {
            expect(getByText('新建自定义函数')).toBeInTheDocument();
        });

        // Click cancel button
        modal.fireCancel(document, { closeByButton: true });

        await waitFor(() => {
            expect(document.querySelector('#test-id')).toBeNull();
        });

        // Open modal again
        fireEvent.click(getByTestId(FUNCTOIN_ACTIONS.CREATE_FUNCTION.id));
        await waitFor(() => {
            expect(getByText('新建自定义函数')).toBeInTheDocument();
        });

        fillFormContent(getAllByTestId);
        modal.fireOk(document);

        await waitFor(() => {
            expect(api.addOfflineFunction).toBeCalledWith({
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
            // Refresh parent node after creating
            expect(catalogueService.loadTreeNode).toBeCalled();
        });
    });

    it('Should support create new folder', async () => {
        (api.addOfflineCatalogue as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
        });
        const { getAllByTestId, getByTestId, getByText } = render(
            <Function
                panel={{
                    id: 'FunctionManager',
                    icon: 'variable-group',
                    name: '函数管理',
                    title: '函数管理',
                }}
                headerToolBar={[
                    {
                        id: 'refresh',
                        title: '刷新',
                        icon: 'refresh',
                    },
                ]}
            />
        );

        const nodes = getAllByTestId('mockFolderTreeNode');
        // Open modal
        fireEvent.contextMenu(nodes[0]);
        fireEvent.click(getByTestId(FUNCTOIN_ACTIONS.CREATE_FOLDER.id));

        await waitFor(() => {
            expect(getByText('新建文件夹')).toBeInTheDocument();
        });

        input.fireChange(document.querySelector<HTMLInputElement>('input#dt_nodeName')!, 'test');
        input.fireChange(getByTestId('mockFolderPicker'), 1);

        modal.fireOk(document);

        await waitFor(() => {
            expect(api.addOfflineCatalogue).toBeCalledWith({
                nodeName: 'test',
                nodePid: '1',
            });
        });
    });

    it('Should support edit function', async () => {
        (api.getOfflineFn as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: {
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
            },
        });
        (api.addOfflineFunction as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
        });
        const { getAllByTestId, getByTestId, getByText } = render(
            <Function
                panel={{
                    id: 'FunctionManager',
                    icon: 'variable-group',
                    name: '函数管理',
                    title: '函数管理',
                }}
                headerToolBar={[
                    {
                        id: 'refresh',
                        title: '刷新',
                        icon: 'refresh',
                    },
                ]}
            />
        );

        const nodes = getAllByTestId('mockFolderTreeNode');
        // Open modal
        fireEvent.contextMenu(nodes[2]);
        fireEvent.click(getByTestId(FUNCTOIN_ACTIONS.EDIT.id));

        await waitFor(() => {
            expect(api.getOfflineFn).toBeCalled();
        });
        await waitFor(() => {
            expect(getByText('编辑自定义函数')).toBeInTheDocument();
        });

        input.fireChange(document.querySelector<HTMLInputElement>('input#nodePid')!, 11);
        modal.fireOk(document);

        await waitFor(() => {
            expect(api.addOfflineFunction).toBeCalledWith({
                className: 'com.dtstack',
                commandFormate: 'main',
                id: 1,
                name: 'mock',
                nodePid: '11',
                paramDesc: 'test',
                purpose: 'forTest',
                resourceId: 1,
                resources: 1,
                taskType: 5,
                udfType: 0,
            });
        });
    });

    it('Should support edit folder', async () => {
        (api.editOfflineCatalogue as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
        });
        const { getAllByTestId, getByTestId, getByText } = render(
            <Function
                panel={{
                    id: 'FunctionManager',
                    icon: 'variable-group',
                    name: '函数管理',
                    title: '函数管理',
                }}
                headerToolBar={[
                    {
                        id: 'refresh',
                        title: '刷新',
                        icon: 'refresh',
                    },
                ]}
            />
        );

        const nodes = getAllByTestId('mockFolderTreeNode');
        // Open modal
        fireEvent.contextMenu(nodes[1]);
        fireEvent.click(getByTestId(FUNCTOIN_ACTIONS.EDIT.id));

        await waitFor(() => {
            expect(getByText('编辑文件夹')).toBeInTheDocument();
        });

        input.fireChange(document.querySelector<HTMLInputElement>('input#dt_nodeName')!, 'mock');
        modal.fireOk(document);

        await waitFor(() => {
            expect(api.editOfflineCatalogue).toBeCalledWith({
                id: 2,
                nodeName: 'mock',
                nodePid: 1,
                type: 'folder',
            });
        });
    });

    it('Should support delete folder and function', async () => {
        (api.delOfflineFolder as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
        });
        (api.delOfflineFn as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
        });
        const { getAllByTestId, getByTestId, getByText } = render(
            <Function
                panel={{
                    id: 'FunctionManager',
                    icon: 'variable-group',
                    name: '函数管理',
                    title: '函数管理',
                }}
                headerToolBar={[
                    {
                        id: 'refresh',
                        title: '刷新',
                        icon: 'refresh',
                    },
                ]}
            />
        );

        const nodes = getAllByTestId('mockFolderTreeNode');
        // Open modal
        fireEvent.contextMenu(nodes[1]);
        fireEvent.click(getByTestId(FUNCTOIN_ACTIONS.DELETE.id));

        await waitFor(() => {
            expect(getByText('确认要删除此文件夹吗?')).toBeInTheDocument();
        });

        await act(async () => {
            modal.confirm.fireOk(document);
        });

        await waitFor(() => {
            expect(api.delOfflineFolder).toBeCalled();
        });
    });
});
