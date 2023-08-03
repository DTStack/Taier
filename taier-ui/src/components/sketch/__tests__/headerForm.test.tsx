import { cleanup, render, waitFor } from '@testing-library/react';
import { Form, Button } from 'antd';
import '@testing-library/jest-dom';
import {
    DatePickerItem,
    InputItem,
    InputWithConditionItem,
    OwnerItem,
    RadioItem,
    RangeItem,
    SelectItem,
} from '../headerForm';
import { button, input } from 'ant-design-testing';

jest.mock('@/context', () => {
    const react = jest.requireActual('react');
    return react.createContext({
        personList: [{ id: '1', userName: 'test' }],
    });
});

const Container = ({
    children,
    onFinish = jest.fn(),
    onFinishFailed = jest.fn(),
}: React.PropsWithChildren<Record<string, any>>) => {
    return (
        <Form onFinish={onFinish} onFinishFailed={onFinishFailed}>
            {children}
            <Form.Item>
                <Button type="primary" htmlType="submit" data-testid="confirmButton">
                    Submit
                </Button>
            </Form.Item>
        </Form>
    );
};

describe('Test HeaderForm Components', () => {
    beforeEach(() => {
        cleanup();
    });

    describe('Test InputItem Component', () => {
        it('Should match snapshot', () => {
            const { asFragment } = render(
                <Container>
                    <InputItem
                        formItemProps={{
                            rules: [
                                {
                                    required: true,
                                    message: "It's required",
                                },
                            ],
                        }}
                    />
                </Container>
            );

            expect(asFragment()).toMatchSnapshot();
        });

        it('Should log error', async () => {
            const fn = jest.fn();
            const { container, getByText } = render(
                <Container onFinishFailed={fn}>
                    <InputItem
                        formItemProps={{
                            rules: [
                                {
                                    required: true,
                                    message: "It's required",
                                },
                            ],
                        }}
                    />
                </Container>
            );

            button.fireClick(container);

            await waitFor(() => {
                expect(getByText("It's required")).toBeInTheDocument();
            });

            expect(fn).toBeCalled();
        });

        it('Should submit successfully', async () => {
            const fn = jest.fn();
            const { container } = render(
                <Container onFinish={fn}>
                    <InputItem
                        formItemProps={{
                            rules: [
                                {
                                    required: true,
                                    message: "It's required",
                                },
                            ],
                        }}
                    />
                </Container>
            );

            input.fireChange(container, 'test');
            button.fireClick(container);

            await waitFor(() => {
                expect(fn).toBeCalledWith({ name: 'test' });
            });
        });
    });

    describe('test InputWithConditionItem Component', () => {
        it('Should match snapshot', () => {
            const { asFragment } = render(
                <Container>
                    <InputWithConditionItem />
                </Container>
            );

            expect(asFragment()).toMatchSnapshot();
        });
    });

    describe('test OwnerItem Component', () => {
        it('Should match snapshot', () => {
            const { asFragment } = render(
                <Container>
                    <OwnerItem />
                </Container>
            );

            expect(asFragment()).toMatchSnapshot();
        });
    });

    describe('test RangeItem Component', () => {
        it('Should match snapshot', () => {
            const { asFragment } = render(
                <Container>
                    <RangeItem />
                </Container>
            );

            expect(asFragment()).toMatchSnapshot();
        });
    });

    describe('test DatePickerItem Component', () => {
        it('Should match snapshot', () => {
            const { asFragment } = render(
                <Container>
                    <DatePickerItem />
                </Container>
            );

            expect(asFragment()).toMatchSnapshot();
        });
    });

    describe('test SelectItem Component', () => {
        it('Should match snapshot', () => {
            const { asFragment } = render(
                <Container>
                    <SelectItem />
                </Container>
            );

            expect(asFragment()).toMatchSnapshot();
        });
    });

    describe('test RadioItem Component', () => {
        it('Should match snapshot', () => {
            const { asFragment } = render(
                <Container>
                    <RadioItem />
                </Container>
            );

            expect(asFragment()).toMatchSnapshot();
        });
    });
});
