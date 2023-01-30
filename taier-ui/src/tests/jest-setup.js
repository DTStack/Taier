import React from 'react';
import 'reflect-metadata';
import ResizeObserver from 'resize-observer-polyfill';
import 'jest-canvas-mock';
import timezoneMock from 'timezone-mock';

global.React = React;
global.ResizeObserver = ResizeObserver;

// Set timezone
timezoneMock.register('UTC');

Object.defineProperty(window, 'matchMedia', {
    writable: true,
    value: jest.fn().mockImplementation((query) => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: jest.fn(), // Deprecated
        removeListener: jest.fn(), // Deprecated
        addEventListener: jest.fn(),
        removeEventListener: jest.fn(),
        dispatchEvent: jest.fn(),
    })),
});

jest.mock('antd', () => {
    return {
        ...jest.requireActual('antd'),
        // I don't care what Modal does, I just want it's children to render
        Modal: jest.fn(({ children, title, onOk, footer }) => (
            <>
                <p data-testid="antd-mock-Modal-title">{title}</p>
                {children}
                {footer ?? (
                    <button data-testid="antd-mock-Modal-confirm" onClick={onOk}>
                        confirm
                    </button>
                )}
            </>
        )),
        message: {
            success: jest.fn(),
        },
    };
});
