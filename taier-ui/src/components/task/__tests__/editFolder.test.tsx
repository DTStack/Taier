import { fireEvent, render, waitFor } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import '@testing-library/jest-dom';
import { Input } from 'antd';
import EditFolder from '../editFolder';

jest.mock('../../../components/folderPicker', () => {
    return (props: any) => <Input data-testid="folderPicker" {...props} />;
});

describe('Test EditFolder Component', () => {
    it('Should match snapshot', () => {
        const { asFragment } = render(<EditFolder tabId={1} />);

        expect(asFragment()).toMatchSnapshot();
    });

    describe('Test EditFolder Without record', () => {
        it('Should submit successfully', async () => {
            const fn = jest.fn().mockResolvedValue(true);
            const { container, getByTestId } = render(<EditFolder tabId={1} onSubmitFolder={fn} />);

            fireEvent.change(container.querySelector('input#dt_nodeName')!, {
                target: { value: 'test' },
            });

            fireEvent.change(getByTestId('folderPicker'), {
                target: { value: '1' },
            });

            act(() => {
                // submit
                fireEvent.click(container.querySelector('button.ant-btn[type="submit"]')!);
            });

            await waitFor(() => {
                expect(fn).toBeCalledWith({
                    nodeName: 'test',
                    nodePid: '1',
                });
            });
        });

        it('Should log error on fields', async () => {
            const { container, getByText } = render(<EditFolder tabId={1} />);

            // submit
            fireEvent.click(container.querySelector('button.ant-btn[type="submit"]')!);

            await waitFor(() => {
                expect(getByText('文件夹名称不能为空')).toBeInTheDocument();
                expect(getByText("'nodePid' is required")).toBeInTheDocument();
            });

            fireEvent.change(container.querySelector('input#dt_nodeName')!, {
                target: { value: new Array(100).fill('a').join('') },
            });

            act(() => {
                // submit
                fireEvent.click(container.querySelector('button.ant-btn[type="submit"]')!);
            });

            await waitFor(() => {
                expect(getByText('目录名称不得超过64个字符！')).toBeInTheDocument();
            });
        });
    });

    describe('Test EditFolder With record', () => {
        it('Should set fields value with record', async () => {
            const { container, getByTestId } = render(
                <EditFolder tabId={1} current={{ tab: { data: { id: 1, dt_nodeName: 'test', nodePid: '1' } } }} />
            );

            await waitFor(() => {
                expect(container.querySelector<HTMLInputElement>('input#dt_nodeName')?.value).toBe('test');
                expect((getByTestId('folderPicker') as HTMLInputElement).value).toBe('1');
            });
        });
    });
});
