import React from 'react';
import 'reflect-metadata';
import ResizeObserver from 'resize-observer-polyfill';
import 'jest-canvas-mock';

global.React = React;
global.ResizeObserver = ResizeObserver;

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
		Modal: jest.fn(({ children, title, onOk }) => (
			<>
				<p data-testid="antd-mock-Modal-title">{title}</p>
				{children}
				<button data-testid="antd-mock-Modal-confirm" onClick={onOk}>
					confirm
				</button>
			</>
		)),
		message: {
			success: jest.fn(),
		},
	};
});
