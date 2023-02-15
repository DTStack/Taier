import { selectItem, toggleOpen } from '@/tests/utils';
import molecule from '@dtinsight/molecule';
import { cleanup, fireEvent, render, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import { act } from 'react-dom/test-utils';
import Create from '../create';
import type { IOfflineTaskProps } from '@/interface';
import api from '@/api';
import { Form, Input } from 'antd';

jest.useFakeTimers();
jest.mock('@/api');

(molecule.folderTree.getState as jest.Mock).mockReset().mockImplementation(() => ({
    folderTree: {
        data: [{ id: 1 }],
    },
}));

jest.mock('@/services', () => ({
    taskRenderService: {
        renderCreateForm: jest.fn(() => (
            <>
                <Form.Item name="syncModel">
                    <Input data-testid="syncModel" />
                </Form.Item>
                <Form.Item name="test">
                    <Input data-testid="test" />
                </Form.Item>
                <Form.Item name="pythonVersion">
                    <Input data-testid="test" />
                </Form.Item>
            </>
        )),
        getState: jest.fn(() => ({
            supportTaskList: [
                {
                    key: 1,
                    taskProperties: {
                        formField: ['syncModel', 'pythonVersion', 'test'],
                    },
                },
            ],
        })),
    },
}));

jest.mock('@/context', () => {
    const react = jest.requireActual('react');
    return react.createContext({
        supportJobTypes: [{ key: 1, value: 'spark' }],
    });
});

describe('Test Create Component', () => {
    beforeEach(() => {
        cleanup();
    });
    it('Should match snapshot', () => {
        const { asFragment } = render(<Create />);
        expect(asFragment()).toMatchSnapshot();
    });

    describe('Test Create Without Record', () => {
        it('Should support create task', async () => {
            (molecule.editor.updateTab as jest.Mock).mockReset();
            const fn = jest.fn().mockResolvedValue(true);
            const { container } = render(<Create current={{ id: 'test', tab: { id: 1, data: {} } }} onSubmit={fn} />);

            // Edit name
            fireEvent.change(container.querySelector('input#name')!, { target: { value: 'test' } });
            expect(molecule.editor.updateTab).lastCalledWith({
                data: {
                    name: 'test',
                    nodePid: 1,
                    sqlText: undefined,
                    taskDesc: undefined,
                    taskType: undefined,
                },
                id: 1,
                status: 'edited',
            });

            // Select taskType
            toggleOpen(container);
            selectItem(0);
            expect(molecule.editor.updateTab).lastCalledWith({
                data: {
                    name: 'test',
                    nodePid: 1,
                    sqlText: undefined,
                    taskDesc: undefined,
                    taskType: 1,
                },
                id: 1,
                status: 'edited',
            });

            act(() => {
                // submit
                fireEvent.click(container.querySelector('button.ant-btn[type="submit"]')!);
            });

            await waitFor(() => {
                expect(fn).toBeCalledTimes(1);
                expect(fn).toBeCalledWith({
                    name: 'test',
                    nodePid: 1,
                    sqlText: undefined,
                    taskDesc: undefined,
                    taskType: 1,
                });
            });
        });

        it('Should log error on name field', async () => {
            (molecule.editor.updateTab as jest.Mock).mockReset();
            const { container, getByText } = render(<Create current={{ id: 'test', tab: { id: 1, data: {} } }} />);

            // submit
            fireEvent.click(container.querySelector('button.ant-btn[type="submit"]')!);

            await waitFor(() => {
                expect(getByText('任务名称不可为空！')).toBeInTheDocument();
                expect(
                    getByText('请选择任务类型', {
                        selector: 'div',
                    })
                ).toBeInTheDocument();
            });

            // Edit name
            fireEvent.change(container.querySelector('input#name')!, {
                target: { value: new Array(200).fill('a').join('') },
            });
            // submit
            fireEvent.click(container.querySelector('button.ant-btn[type="submit"]')!);

            await waitFor(() => {
                expect(getByText('任务名称不得超过128个字符！')).toBeInTheDocument();
            });

            // Edit name
            fireEvent.change(container.querySelector('input#name')!, {
                target: { value: 'a-b%' },
            });
            // submit
            fireEvent.click(container.querySelector('button.ant-btn[type="submit"]')!);

            await waitFor(() => {
                expect(getByText('任务名称只能由字母、数字、中文、下划线组成!')).toBeInTheDocument();
            });
        });

        it('Should log errors on taskDesc field', async () => {
            (molecule.editor.updateTab as jest.Mock).mockReset();
            const { container, getByText } = render(<Create current={{ id: 'test', tab: { id: 1, data: {} } }} />);

            // Edit taskDesc
            fireEvent.change(container.querySelector('textarea#taskDesc')!, {
                target: { value: new Array(300).fill('a').join('') },
            });

            // submit
            fireEvent.click(container.querySelector('button.ant-btn[type="submit"]')!);

            await waitFor(() => {
                expect(getByText('描述请控制在200个字符以内！')).toBeInTheDocument();
            });
        });
    });

    describe('Test Create With Record', () => {
        it('Should alert tips about version updated', async () => {
            (api.getOfflineTaskByID as jest.Mock).mockReset().mockResolvedValue({
                code: 1,
                data: {
                    syncModel: 1,
                },
            });
            const { getByText } = render(
                <Create current={{ id: 'test', tab: { id: 1, data: {} } }} record={{ id: 1 } as IOfflineTaskProps} />
            );

            await waitFor(() => {
                expect(getByText('由于版本更新，需要重新设置该字段')).toBeInTheDocument();
            });
        });

        it('Should set value into fields', async () => {
            (api.getOfflineTaskByID as jest.Mock).mockReset().mockResolvedValue({
                code: 1,
                data: {
                    name: 'test',
                    taskType: 1,
                    taskDesc: 'abc',
                    sqlText: 'aaa',
                    sourceMap: {
                        syncModal: 1,
                    },
                    exeArgs: JSON.stringify({ '--app-type': 'python3' }),
                    test: 1,
                },
            });
            const fn = jest.fn().mockResolvedValue(true);
            const { container } = render(
                <Create
                    current={{ id: 'test', tab: { id: 1, data: {} } }}
                    record={{ id: 1 } as IOfflineTaskProps}
                    onSubmit={fn}
                />
            );

            await waitFor(() => {
                expect(container.querySelector<HTMLInputElement>('input#name')?.value).toBe('test');
            });

            act(() => {
                // submit
                fireEvent.click(container.querySelector('button.ant-btn[type="submit"]')!);
            });

            await waitFor(() => {
                expect(fn).toBeCalledWith({
                    name: 'test',
                    nodePid: 1,
                    sqlText: 'aaa',
                    syncModel: undefined,
                    taskDesc: 'abc',
                    taskType: 1,
                    pythonVersion: 3,
                    test: undefined,
                });
            });
        });

        it('Should fill fields with record', async () => {
            const fn = jest.fn().mockResolvedValue(true);
            const { container } = render(
                <Create
                    current={{ id: 'test', tab: { id: 1, data: {} } }}
                    record={
                        {
                            id: 1,
                            name: 'test',
                            taskType: 1,
                            taskDesc: 'abc',
                            sqlText: 'aaa',
                            sourceMap: {
                                syncModal: 1,
                            },
                            exeArgs: JSON.stringify({ '--app-type': 'python3' }),
                            test: 1,
                        } as any
                    }
                    isRequest={false}
                    onSubmit={fn}
                />
            );

            await waitFor(() => {
                expect(container.querySelector<HTMLInputElement>('input#name')?.value).toBe('test');
            });

            act(() => {
                // submit
                fireEvent.click(container.querySelector('button.ant-btn[type="submit"]')!);
            });

            await waitFor(() => {
                expect(fn).toBeCalledWith({
                    name: 'test',
                    nodePid: 1,
                    sqlText: 'aaa',
                    syncModel: undefined,
                    taskDesc: 'abc',
                    taskType: 1,
                    pythonVersion: undefined,
                    test: 1,
                });
            });
        });
    });
});
