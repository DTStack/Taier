import { act, cleanup, fireEvent, render, renderHook } from '@testing-library/react';
import '@testing-library/jest-dom';
import Sketch, { useSketchRef } from '../';
import React from 'react';

jest.useFakeTimers();

describe('Test Sketch Component', () => {
    beforeEach(() => {
        cleanup();
        document.body.innerHTML = '';
    });

    it('Should match snapshot', async () => {
        await act(async () => {
            render(
                <Sketch
                    header={[
                        'input',
                        'inputWithCondition',
                        'owner',
                        'rangeDate',
                        'datePicker',
                        'select',
                        { name: 'radioGroup' },
                    ]}
                    extra={<button>test</button>}
                    columns={[
                        { dataIndex: 'id', key: 'id' },
                        { dataIndex: 'name', key: 'name' },
                    ]}
                    request={jest.fn().mockResolvedValue({
                        total: 2,
                        data: [
                            {
                                id: 1,
                                name: 'test1',
                            },
                            {
                                id: 2,
                                name: 'test2',
                            },
                        ],
                        polling: false,
                    })}
                />
            );
        });
        expect(document.body).toMatchSnapshot();
    });

    it('Should request again when pagination changed', async () => {
        const fn = jest.fn().mockResolvedValue({
            total: 50,
            data: [
                {
                    id: 1,
                    name: 'test1',
                },
                {
                    id: 2,
                    name: 'test2',
                },
            ],
            polling: false,
        });
        await act(async () => {
            render(
                <Sketch
                    header={['input']}
                    columns={[
                        { dataIndex: 'id', key: 'id' },
                        { dataIndex: 'name', key: 'name' },
                    ]}
                    request={fn}
                />
            );
        });

        await act(async () => {
            fireEvent.click(document.querySelector('.ant-pagination-next')!.firstChild!);
        });

        expect(fn).toBeCalledTimes(2);
    });

    it('Should request again when form field changed', async () => {
        const fn = jest.fn().mockResolvedValue({
            total: 50,
            data: [
                {
                    id: 1,
                    name: 'test1',
                },
                {
                    id: 2,
                    name: 'test2',
                },
            ],
            polling: false,
        });
        const changeFn = jest.fn();
        await act(async () => {
            render(
                <Sketch
                    header={[
                        'input',
                        {
                            name: 'inputWithCondition',
                            props: {
                                formItemProps: {
                                    name: 'jest',
                                },
                            },
                        },
                    ]}
                    columns={[
                        { dataIndex: 'id', key: 'id' },
                        { dataIndex: 'name', key: 'name' },
                    ]}
                    request={fn}
                    onFormFieldChange={changeFn}
                />
            );
        });

        await act(async () => {
            // name field is an insensitive field which won't request again
            fireEvent.change(document.querySelector('input#name')!, { target: { value: '123' } });
        });
        expect(changeFn).toBeCalledTimes(1);
        expect(fn).toBeCalledTimes(1);

        await act(async () => {
            // common field would trigger request
            fireEvent.change(document.querySelector('input#jest')!, { target: { value: '123' } });
        });

        expect(changeFn).toBeCalledTimes(2);
        expect(fn).toBeCalledTimes(2);
        expect(fn.mock.calls[1][0]).toEqual({
            name: '123',
            jest: '123',
            multipleNameSuffix: 'fuzzy',
        });
    });

    it('Should support table with select', async () => {
        const fn = jest.fn().mockResolvedValue({
            total: 50,
            data: [
                {
                    id: 1,
                    name: 'test1',
                },
                {
                    id: 2,
                    name: 'test2',
                },
            ],
            polling: false,
        });
        const selectFn = jest.fn();
        await act(async () => {
            render(
                <Sketch
                    header={['input']}
                    columns={[
                        { dataIndex: 'id', key: 'id' },
                        { dataIndex: 'name', key: 'name' },
                    ]}
                    request={fn}
                    onTableSelect={selectFn}
                />
            );
        });

        act(() => {
            fireEvent.click(document.querySelector('table')!.querySelector('label.ant-checkbox-wrapper')!);
        });

        expect(selectFn).toBeCalled();
    });

    it('Should support expand table', async () => {
        const fn = jest.fn().mockResolvedValue({
            total: 50,
            data: [
                {
                    id: 1,
                    name: 'test1',
                },
                {
                    id: 2,
                    name: 'test2',
                    children: [],
                },
            ],
            polling: false,
        });
        const expandFn = jest.fn().mockResolvedValue([]);
        await act(async () => {
            render(
                <Sketch
                    header={['input']}
                    columns={[
                        { dataIndex: 'id', key: 'id' },
                        { dataIndex: 'name', key: 'name' },
                    ]}
                    request={fn}
                    onExpand={expandFn}
                />
            );
        });

        const expandButtons = document.querySelectorAll(
            'button.ant-table-row-expand-icon.ant-table-row-expand-icon-collapsed'
        );
        // ONLY data with children has expand button
        expect(expandButtons.length).toBe(1);

        await act(async () => {
            fireEvent.click(expandButtons[0]);
        });

        expect(expandFn).toBeCalledWith(true, { id: 2, name: 'test2', children: [] });
    });

    it('Should support polling request', async () => {
        const fn = jest
            .fn()
            .mockResolvedValueOnce({
                total: 50,
                data: [
                    {
                        id: 1,
                        name: 'test1',
                    },
                    {
                        id: 2,
                        name: 'test2',
                        children: [],
                    },
                ],
                polling: true,
            })
            .mockResolvedValue({
                total: 50,
                data: [
                    {
                        id: 1,
                        name: 'test1',
                    },
                    {
                        id: 2,
                        name: 'test2',
                        children: [],
                    },
                ],
                polling: false,
            });

        await act(async () => {
            render(
                <Sketch
                    header={['input']}
                    columns={[
                        { dataIndex: 'id', key: 'id' },
                        { dataIndex: 'name', key: 'name' },
                    ]}
                    request={fn}
                />
            );
        });

        expect(fn).toBeCalledTimes(1);
        expect(fn).toBeCalledWith({ name: undefined }, { current: 1, pageSize: 20 }, {}, undefined);

        await act(async () => {
            jest.runAllTimers();
        });

        expect(fn).toBeCalledTimes(2);
        expect(fn).lastCalledWith({ name: undefined }, { current: 1, pageSize: 20 }, {}, undefined);
    });

    it('Should have ref instance', async () => {
        const fn = jest.fn().mockResolvedValue({
            total: 50,
            data: [
                {
                    id: 1,
                    name: 'test1',
                },
            ],
            polling: false,
        });
        const ref = React.createRef<any>();
        await act(async () => {
            render(
                <Sketch
                    header={['input']}
                    columns={[
                        { dataIndex: 'id', key: 'id' },
                        { dataIndex: 'name', key: 'name' },
                    ]}
                    request={fn}
                    actionRef={ref}
                />
            );
        });

        expect(ref.current.selectedRowKeys).toEqual([]);
        expect(ref.current.selectedRows).toEqual([]);

        act(() => {
            ref.current.setSelectedKeys([1, 2, 3]);
        });
        expect(ref.current.selectedRowKeys).toEqual([1, 2, 3]);

        expect(fn).toBeCalledTimes(1);
        await act(async () => {
            ref.current.submit();
        });
        expect(fn).toBeCalledTimes(2);

        expect(ref.current.form.getFieldsValue).toBeInstanceOf(Function);

        expect(ref.current.getTableData()).toEqual([
            {
                id: 1,
                name: 'test1',
            },
        ]);
    });

    it('Should get a ref', () => {
        const result = renderHook(() => useSketchRef());

        expect(result.result.current).toEqual({ current: null });
    });
});
