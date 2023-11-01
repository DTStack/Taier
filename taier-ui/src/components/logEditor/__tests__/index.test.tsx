import { cleanup, render, waitFor } from '@testing-library/react';
import { Input } from 'antd';
import '@testing-library/jest-dom';

import LogEditor from '..';

jest.mock('../../editor', () => {
    return ({ value }: any) => {
        return <Input data-testid="mockEditor" value={value} />;
    };
});

describe('Test LogEditor Component', () => {
    beforeEach(() => {
        cleanup();
    });

    it('Should render empty', () => {
        const { getByText } = render(<LogEditor editor={{}} results={{ log: {} }} />);

        expect(getByText('无法获取任务日志')).toBeInTheDocument();
    });

    it('Should render value', async () => {
        const { getByTestId, rerender } = render(
            <LogEditor
                editor={{
                    current: {
                        activeTab: 1,
                        tab: {
                            id: 1,
                        },
                    },
                }}
                results={{
                    logs: {
                        1: 'test',
                    },
                }}
            />
        );

        await waitFor(() => {
            expect((getByTestId('mockEditor') as HTMLInputElement).value).toBe('test');
        });

        rerender(
            <LogEditor
                editor={{
                    current: {
                        activeTab: 1,
                        tab: {
                            id: 1,
                        },
                    },
                }}
                results={{
                    logs: {},
                }}
            />
        );

        await waitFor(() => {
            expect((getByTestId('mockEditor') as HTMLInputElement).value).toBe('暂无日志');
        });
    });
});
