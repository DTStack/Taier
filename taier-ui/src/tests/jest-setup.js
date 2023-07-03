import React from 'react';
import 'reflect-metadata';
import ResizeObserver from 'resize-observer-polyfill';
import 'jest-canvas-mock';
import timezoneMock from 'timezone-mock';
import { provider } from 'ant-design-testing';

global.React = React;
global.ResizeObserver = ResizeObserver;

provider({ prefixCls: 'ant' });

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
