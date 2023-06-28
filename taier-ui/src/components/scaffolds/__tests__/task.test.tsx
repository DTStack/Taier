import http from '@/api/http';
import viewStoreService from '@/services/viewStoreService';
import molecule from '@dtinsight/molecule';
import { cleanup, fireEvent, render, waitFor } from '@testing-library/react';
import { Form, Input } from 'antd';
import {
    AutoCompleteWithRequest,
    InputWithColumns,
    ResourcePicker,
    SelectWithCreate,
    SelectWithPreviewer,
    SelectWithRequest,
    TextareaWithJSONValidator,
} from '../task';
import { Context } from '@/context/dataSync';
import { act } from 'react-dom/test-utils';
import api from '@/api';
import { NamePath } from 'antd/lib/form/interface';
import '@testing-library/jest-dom';
import { EventKind } from '@/pages/editor/dataSync';
import { input, modal, select, tooltip } from 'ant-design-testing';
import * as form from 'ant-design-testing/dist/cjs/form';

jest.useFakeTimers();

jest.mock('@/api/http');
jest.mock('@/api');
jest.mock('@/services', () => ({}));
jest.mock('@/services/viewStoreService', () => {
    return {
        getViewStorage: jest.fn(),
        setViewStorage: jest.fn(),
    };
});

jest.mock('@/context/dataSync', () => {
    const react = jest.requireActual('react');
    return {
        Context: react.createContext({
            optionCollections: {},
            dispatch: jest.fn(),
            transformerFactory: {},
        }),
    };
});

jest.mock('../../editor', () => {
    return ({ value, onChange }: any) => {
        return <textarea value={value} onChange={onChange} data-testid="mockEditor" />;
    };
});

jest.mock('../../folderPicker', () => () => {
    return <input data-testid="mockFolderPicker" />;
});

function FormItem({ name, initialValue }: { name: NamePath; initialValue: any }) {
    return (
        <Form.Item name={name} label="mockFormItem" initialValue={initialValue}>
            <Input />
        </Form.Item>
    );
}

function FormContainer({ children, optionCollections, dispatch, transformerFactory }: any) {
    return (
        <Context.Provider
            value={{
                optionCollections: optionCollections ?? {},
                dispatch: dispatch ?? jest.fn(),
                transformerFactory: transformerFactory ?? {},
            }}
        >
            <Form>{children}</Form>
        </Context.Provider>
    );
}

describe('Test Task Scaffolds', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';
        (viewStoreService.setViewStorage as jest.Mock).mockReset();
        (viewStoreService.getViewStorage as jest.Mock).mockReset();
    });

    describe('Test SelectWithRequest Component', () => {
        beforeEach(() => {
            (molecule.editor.getState as jest.Mock).mockReset().mockImplementation(() => ({
                current: {
                    activeTab: 1,
                },
            }));

            (http.get as jest.Mock).mockReset();
        });

        it('Should match snapshot', () => {
            const { asFragment } = render(
                <FormContainer>
                    <SelectWithRequest optionsFromRequest={false} options={[{ label: 'test', value: '1' }]} />
                </FormContainer>
            );

            expect(asFragment()).toMatchSnapshot();
        });

        it('Should support get options from request', async () => {
            (http.get as jest.Mock).mockReset().mockResolvedValue({
                code: 1,
                data: [0, 1, 2, 3],
            });

            const fn = jest.fn();

            render(
                <FormContainer dispatch={fn}>
                    <SelectWithRequest
                        optionsFromRequest
                        url="/api/test"
                        method="get"
                        name="test"
                        params={{
                            a: 1,
                        }}
                    />
                </FormContainer>
            );

            expect(http.get).toBeCalledWith('/api/test', { a: 1 });

            await waitFor(() => {
                expect(viewStoreService.setViewStorage).toBeCalledTimes(1);
                expect(fn).toBeCalledWith({
                    type: 'update',
                    payload: {
                        field: 'test',
                        collection: [0, 1, 2, 3],
                    },
                });
            });
        });

        it('Should support transformer data', async () => {
            (http.get as jest.Mock).mockReset().mockResolvedValue({
                code: 1,
                data: [0, 1, 2, 3],
            });

            const fn = jest.fn();

            render(
                <FormContainer
                    dispatch={fn}
                    transformerFactory={{
                        toString: (value: any) => `${value}`,
                    }}
                >
                    <SelectWithRequest
                        optionsFromRequest
                        url="/api/test"
                        method="get"
                        name="test"
                        params={{
                            a: 1,
                        }}
                        transformer="toString"
                    />
                </FormContainer>
            );

            expect(http.get).toBeCalledWith('/api/test', { a: 1 });

            await waitFor(() => {
                expect(viewStoreService.setViewStorage).toBeCalledTimes(1);
                expect(fn).toBeCalledWith({
                    type: 'update',
                    payload: {
                        field: 'test',
                        collection: ['0', '1', '2', '3'],
                    },
                });
            });
        });

        it('Should get values from storage', () => {
            (viewStoreService.getViewStorage as jest.Mock).mockReset().mockImplementation(() => ({
                b428243da8d971d426048e00af23ab68: [0, 1, 2, 3],
            }));
            const fn = jest.fn();
            render(
                <FormContainer dispatch={fn}>
                    <SelectWithRequest
                        optionsFromRequest
                        url="/api/test"
                        method="get"
                        name="test"
                        params={{
                            a: 1,
                        }}
                    />
                </FormContainer>
            );

            expect(http.get).not.toBeCalled();
            expect(fn).toBeCalledWith({
                type: 'update',
                payload: {
                    field: 'test',
                    collection: [0, 1, 2, 3],
                },
            });
        });

        it('Should prevent request with required lost', () => {
            (http.get as jest.Mock).mockReset().mockResolvedValue({ code: 0, message: 'error' });
            render(
                <FormContainer>
                    <SelectWithRequest
                        optionsFromRequest
                        url="/api/test"
                        method="get"
                        name="test"
                        params={{
                            a: 1,
                        }}
                        required={['test', 'a']}
                    />
                    <Form.Item name="test" label="test">
                        <Input />
                    </Form.Item>
                </FormContainer>
            );

            expect(http.get).not.toBeCalled();
        });

        it('Should render error message by Tooltip', async () => {
            (http.get as jest.Mock).mockReset().mockResolvedValue({ code: 0, message: 'error message' });
            await act(async () => {
                render(
                    <FormContainer>
                        <SelectWithRequest
                            optionsFromRequest
                            url="/api/test"
                            method="get"
                            name="test"
                            params={{
                                a: 1,
                            }}
                        />
                    </FormContainer>
                );
            });

            act(() => {
                tooltip.fireOpen(select.query(document, 0)!);
            });

            await waitFor(() => {
                expect(document.querySelector('.ant-tooltip')).not.toBeNull();
                expect(document.querySelector('.ant-tooltip-inner')?.textContent).toBe('error message');
            });
        });
    });

    describe('Test AutoCompleteWithRequest Component', () => {
        beforeEach(() => {
            (molecule.editor.getState as jest.Mock).mockReset().mockImplementation(() => ({
                current: {
                    activeTab: 1,
                },
            }));

            (http.get as jest.Mock).mockReset();
        });

        it('Should match snapshot', () => {
            const { asFragment } = render(
                <FormContainer>
                    <AutoCompleteWithRequest
                        optionsFromRequest={false}
                        options={[{ label: '1', value: 1 }]}
                        placeholder="this is input"
                    />
                </FormContainer>
            );

            expect(asFragment()).toMatchSnapshot();
        });

        it('Should support get options from request', async () => {
            (http.get as jest.Mock).mockReset().mockResolvedValue({
                code: 1,
                data: [0, 1, 2, 3],
            });

            const fn = jest.fn();

            render(
                <FormContainer dispatch={fn}>
                    <AutoCompleteWithRequest
                        optionsFromRequest
                        url="/api/test"
                        method="get"
                        name="test"
                        params={{
                            a: 1,
                        }}
                    />
                </FormContainer>
            );

            expect(http.get).toBeCalledWith('/api/test', { a: 1 });

            await waitFor(() => {
                expect(viewStoreService.setViewStorage).toBeCalledTimes(1);
                expect(fn).toBeCalledWith({
                    type: 'update',
                    payload: {
                        field: 'test',
                        collection: [0, 1, 2, 3],
                    },
                });
            });
        });

        it('Should support transformer data', async () => {
            (http.get as jest.Mock).mockReset().mockResolvedValue({
                code: 1,
                data: [0, 1, 2, 3],
            });

            const fn = jest.fn();

            render(
                <FormContainer
                    dispatch={fn}
                    transformerFactory={{
                        toString: (value: any) => `${value}`,
                    }}
                >
                    <AutoCompleteWithRequest
                        optionsFromRequest
                        url="/api/test"
                        method="get"
                        name="test"
                        params={{
                            a: 1,
                        }}
                        transformer="toString"
                    />
                </FormContainer>
            );

            expect(http.get).toBeCalledWith('/api/test', { a: 1 });

            await waitFor(() => {
                expect(viewStoreService.setViewStorage).toBeCalledTimes(1);
                expect(fn).toBeCalledWith({
                    type: 'update',
                    payload: {
                        field: 'test',
                        collection: ['0', '1', '2', '3'],
                    },
                });
            });
        });

        it('Should get values from storage', () => {
            (viewStoreService.getViewStorage as jest.Mock).mockReset().mockImplementation(() => ({
                b428243da8d971d426048e00af23ab68: [0, 1, 2, 3],
            }));
            const fn = jest.fn();
            render(
                <FormContainer dispatch={fn}>
                    <AutoCompleteWithRequest
                        optionsFromRequest
                        url="/api/test"
                        method="get"
                        name="test"
                        params={{
                            a: 1,
                        }}
                    />
                </FormContainer>
            );

            expect(http.get).not.toBeCalled();
            expect(fn).toBeCalledWith({
                type: 'update',
                payload: {
                    field: 'test',
                    collection: [0, 1, 2, 3],
                },
            });
        });

        it('Should prevent request with required lost', () => {
            (http.get as jest.Mock).mockReset().mockResolvedValue({ code: 0, message: 'error' });
            render(
                <FormContainer>
                    <AutoCompleteWithRequest
                        optionsFromRequest
                        url="/api/test"
                        method="get"
                        name="test"
                        params={{
                            a: 1,
                        }}
                        required={['test', 'a']}
                    />
                    <Form.Item name="test" label="test">
                        <Input />
                    </Form.Item>
                </FormContainer>
            );

            expect(http.get).not.toBeCalled();
        });
    });

    describe('Test SelectWithCreate Component', () => {
        it('Should match snapshot', () => {
            const { asFragment } = render(
                <FormContainer>
                    <SelectWithCreate optionsFromRequest={false} options={[{ label: 1, value: 1 }]} />
                </FormContainer>
            );

            expect(asFragment()).toMatchSnapshot();
        });

        it('Should support get create sql', async () => {
            (api.getCreateTargetTable as jest.Mock).mockReset().mockResolvedValue({
                code: 1,
                data: "CREATE TABLE `targetTable`(\n`i1` VARCHAR(255) COMMENT''\n)comment'';\n",
            });
            (api.createDdlTable as jest.Mock).mockReset().mockResolvedValue({
                code: 1,
                data: 'xxxx',
            });

            const { container, getByTestId } = render(
                <FormContainer>
                    <SelectWithCreate optionsFromRequest={false} options={[{ label: 1, value: 1 }]} />
                    <FormItem name={['sourceMap', 'table']} initialValue="sourceTable" />
                    <FormItem name={['sourceMap', 'sourceId']} initialValue="1" />
                    <FormItem name={['sourceMap', 'partition']} initialValue />
                    <FormItem name={['sourceMap', 'schema']} initialValue="xx" />
                    <FormItem name={['targetMap', 'sourceId']} initialValue="1" />
                    <FormItem name={['targetMap', 'table']} initialValue="targetTable" />
                    <FormItem name={['targetMap', 'schema']} initialValue="xxxx" />
                </FormContainer>
            );

            await act(async () => {
                fireEvent.click(container.querySelector('.anticon-console-sql')!);
            });

            expect((getByTestId('mockEditor') as HTMLTextAreaElement).value).toBe(
                "CREATE TABLE `targetTable`(\n`i1` VARCHAR(255) COMMENT''\n)comment'';\n"
            );

            modal.fireOk(document);

            await waitFor(() => {
                expect(api.createDdlTable).toBeCalled();
                expect((input.query(form.queryFormItems(container)[5]!, 0) as HTMLInputElement).value).toBe('xxxx');
            });
        });
    });

    describe('Test SelectWithPreviewer Component', () => {
        beforeEach(() => {
            (molecule.layout.togglePanelVisibility as jest.Mock).mockReset();
            (molecule.panel.setActive as jest.Mock).mockReset();
            (molecule.panel.add as jest.Mock).mockReset();
        });

        it('Should match snapshot', () => {
            const { asFragment } = render(
                <FormContainer>
                    <SelectWithPreviewer optionsFromRequest={false} options={[]} />
                </FormContainer>
            );

            expect(asFragment()).toMatchSnapshot();
        });

        it('Should support on multiple mode', () => {
            const { container } = render(
                <FormContainer>
                    <SelectWithPreviewer optionsFromRequest={false} options={[]} />
                    <FormItem name={['sourceMap', 'type']} initialValue={1} />
                </FormContainer>
            );

            // the difference between default and multiple mode
            expect(container.querySelector('.ant-select-selection-overflow-item')).toBeInTheDocument();
        });

        it('Should support preview data', async () => {
            (api.getDataSourcePreview as jest.Mock).mockReset().mockResolvedValue({
                code: 1,
                data: {
                    test: 1,
                },
            });
            (molecule.panel.getPanel as jest.Mock)
                .mockReset()
                .mockImplementationOnce(() => undefined)
                .mockImplementation(() => ({ id: 'table' }));

            (molecule.layout.getState as jest.Mock).mockReset().mockImplementation(() => ({
                panel: {
                    hidden: true,
                },
            }));

            const { container } = render(
                <FormContainer>
                    <SelectWithPreviewer optionsFromRequest={false} options={[]} />
                    <FormItem name={['sourceMap', 'table']} initialValue="table" />
                    <FormItem name={['sourceMap', 'sourceId']} initialValue={1} />
                    <FormItem name={['sourceMap', 'schema']} initialValue="xxx" />
                </FormContainer>
            );

            expect(container.querySelector<HTMLSpanElement>('.anticon-fund-view')!.style.pointerEvents).toBe('auto');

            await act(async () => {
                fireEvent.click(container.querySelector('.anticon-fund-view')!);
            });

            expect(api.getDataSourcePreview).toBeCalledWith({
                sourceId: 1,
                tableName: 'table',
                schema: 'xxx',
            });

            // If panel is invisible, then show it
            expect(molecule.layout.togglePanelVisibility).toBeCalled();
            // Panel isn't exist at first time, so create one
            expect(molecule.panel.add).toBeCalledTimes(1);
            expect(molecule.panel.add).toBeCalledWith(
                expect.objectContaining({
                    id: 'table',
                    name: 'table 数据预览',
                    closable: true,
                    data: {
                        test: 1,
                    },
                })
            );
            // Active this panel
            expect(molecule.panel.setActive).toBeCalledWith('table');

            await act(async () => {
                fireEvent.click(container.querySelector('.anticon-fund-view')!);
            });

            // ONLY need to active the exist panel
            expect(molecule.panel.setActive).toBeCalledWith('table');
            // NOT call add function since last called
            expect(molecule.panel.add).toBeCalledTimes(1);
        });
    });

    describe('Test TextareaWithJSONValidator Component', () => {
        it('Should match snapshot', () => {
            expect(render(<TextareaWithJSONValidator />).asFragment()).toMatchSnapshot();
        });
    });

    describe('Test ResourcePicker Component', () => {
        it('Should match snapshot', () => {
            expect(
                render(<ResourcePicker id="mock" value="1" onChange={jest.fn()} event={{} as any} />).asFragment()
            ).toMatchSnapshot();
        });
    });

    describe('Test InputWithColumns Component', () => {
        it('Should match snapshot', () => {
            const { asFragment } = render(
                <FormContainer>
                    <FormItem name={['sourceMap', 'type']} initialValue={9} />
                    <InputWithColumns id="mock" value="1" onChange={jest.fn()} event={{} as any} />
                </FormContainer>
            );

            expect(asFragment()).toMatchSnapshot();
        });

        it('Should trigger onChange', () => {
            const fn = jest.fn();
            const { container } = render(
                <FormContainer>
                    <FormItem name={['sourceMap', 'type']} initialValue={9} />
                    <InputWithColumns id="mock" value="" onChange={fn} event={{} as any} />
                </FormContainer>
            );

            act(() => {
                fireEvent.click(container.querySelector('.anticon-cloud-sync')!);
            });

            expect(fn).toBeCalledWith(undefined);
        });

        it('Should call request', async () => {
            (api.getFTPColumns as jest.Mock).mockReset().mockResolvedValue({
                code: 1,
                data: {
                    column: [1, 2, 3],
                },
            });

            const fn = jest.fn();
            const { container } = render(
                <FormContainer>
                    <FormItem name={['sourceMap', 'type']} initialValue={9} />
                    <FormItem name={['sourceMap', 'fieldDelimiter|FTP']} initialValue="xxx" />
                    <FormItem name={['sourceMap', 'isFirstLineHeader']} initialValue />
                    <FormItem name={['sourceMap', 'sourceId']} initialValue="1" />
                    <FormItem name={['sourceMap', 'encoding']} initialValue="utf8" />
                    <InputWithColumns id="mock" value="1" onChange={jest.fn()} event={{ emit: fn } as any} />
                </FormContainer>
            );

            act(() => {
                fireEvent.click(container.querySelector('.anticon-cloud-sync')!);
            });

            expect(api.getFTPColumns).toBeCalledWith({
                filepath: '1',
                columnSeparator: 'xxx',
                firstColumnName: true,
                sourceId: '1',
                encoding: 'utf8',
            });

            await waitFor(() => {
                expect(fn).toBeCalledWith(EventKind.SourceKeyChange, [1, 2, 3]);
            });
        });
    });
});
