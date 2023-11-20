import React from 'react';
import { provider } from 'ant-design-testing';
import ResizeObserver from 'resize-observer-polyfill';
import timezoneMock from 'timezone-mock';
import 'reflect-metadata';
import 'jest-canvas-mock';

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
