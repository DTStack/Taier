import * as monaco from '@dtinsight/molecule/esm/monaco';
import { cleanup, render, waitFor } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import Editor from '..';

describe('Test Editor Component', () => {
	beforeEach(() => {
		cleanup();
	});

	it('Should match snapshot', () => {
		const { asFragment } = render(<Editor />);

		expect(asFragment()).toMatchSnapshot();
	});

	it('Should register Commands', () => {
		const fn = jest.fn();
		(monaco.editor.create as jest.Mock).mockReset().mockImplementation(() => ({
			addCommand: fn,
			onDidChangeModelContent: jest.fn(),
			onDidBlurEditorText: jest.fn(),
			onDidBlurEditorWidget: jest.fn(),
			onDidFocusEditorText: jest.fn(),
			onDidChangeCursorSelection: jest.fn(),
			onContextMenu: jest.fn(),
			dispose: jest.fn(),
		}));
		render(<Editor />);

		expect(fn).toBeCalledTimes(7);
	});

	it('Should change current value', () => {
		const emits: (() => {})[] = [];
		const fn = jest.fn((cb) => emits.push(cb));

		(monaco.editor.create as jest.Mock).mockReset().mockImplementation(() => ({
			addCommand: jest.fn(),
			onDidChangeModelContent: fn,
			getValue: jest.fn(() => 'abc'),
			onDidBlurEditorText: jest.fn(),
			onDidBlurEditorWidget: jest.fn(),
			onDidFocusEditorText: jest.fn(),
			onDidChangeCursorSelection: jest.fn(),
			onContextMenu: jest.fn(),
			dispose: jest.fn(),
		}));

		const onChangeFn = jest.fn();
		const { container } = render(<Editor onChange={onChangeFn} />);

		expect(container.querySelector<HTMLElement>('.dt-placeholder')?.style.display).toBe(
			'initial',
		);

		// Mock trigger onDidChangeModelContent
		emits[0]();

		expect(onChangeFn.mock.calls[0][0]).toBe('abc');
		expect(container.querySelector<HTMLElement>('.dt-placeholder')?.style.display).toBe('none');
	});

	it('Should trigger onBlur event handler', () => {
		const emits: (() => {})[] = [];
		const fn = jest.fn((cb) => emits.push(cb));

		(monaco.editor.create as jest.Mock).mockReset().mockImplementation(() => ({
			addCommand: jest.fn(),
			onDidChangeModelContent: jest.fn(),
			getValue: jest.fn(() => 'abc'),
			onDidBlurEditorText: fn,
			onDidBlurEditorWidget: jest.fn(),
			onDidFocusEditorText: jest.fn(),
			onDidChangeCursorSelection: jest.fn(),
			onContextMenu: jest.fn(),
			dispose: jest.fn(),
		}));

		const onBlurFn = jest.fn();

		render(<Editor onBlur={onBlurFn} />);

		// Mock trigger onDidBlurEditorText
		emits[0]();

		expect(onBlurFn.mock.calls[0][0]).toBe('abc');
	});

	it('Should toggle placeholder onDidBlurEditorWidget', () => {
		const emits: (() => {})[] = [];
		const fn = jest.fn((cb) => emits.push(cb));

		(monaco.editor.create as jest.Mock).mockReset().mockImplementation(() => ({
			addCommand: jest.fn(),
			onDidChangeModelContent: jest.fn(),
			getValue: jest.fn(() => 'abc'),
			onDidBlurEditorText: jest.fn(),
			onDidBlurEditorWidget: fn,
			onDidFocusEditorText: jest.fn(),
			onDidChangeCursorSelection: jest.fn(),
			onContextMenu: jest.fn(),
			dispose: jest.fn(),
		}));

		const onBlurFn = jest.fn();

		const { container } = render(<Editor onBlur={onBlurFn} />);

		expect(container.querySelector<HTMLElement>('.dt-placeholder')?.style.display).toBe(
			'initial',
		);

		// Mock trigger onDidBlurEditorWidget
		emits[0]();

		expect(container.querySelector<HTMLElement>('.dt-placeholder')?.style.display).toBe('none');
	});

	it('Should trigger onFocus event handler', () => {
		const emits: (() => {})[] = [];
		const fn = jest.fn((cb) => emits.push(cb));

		(monaco.editor.create as jest.Mock).mockReset().mockImplementation(() => ({
			addCommand: jest.fn(),
			onDidChangeModelContent: jest.fn(),
			getValue: jest.fn(() => 'abc'),
			onDidBlurEditorText: jest.fn(),
			onDidBlurEditorWidget: jest.fn(),
			onDidFocusEditorText: fn,
			onDidChangeCursorSelection: jest.fn(),
			onContextMenu: jest.fn(),
			dispose: jest.fn(),
		}));

		const onFocusFn = jest.fn();

		render(<Editor onFocus={onFocusFn} />);

		// Mock trigger onDidFocusEditorText
		emits[0]();

		expect(onFocusFn.mock.calls[0][0]).toBe('abc');
	});

	it('Should trigger onCursorSelection event handler', () => {
		const emits: (() => {})[] = [];
		const fn = jest.fn((cb) => emits.push(cb));

		(monaco.editor.create as jest.Mock).mockReset().mockImplementation(() => ({
			addCommand: jest.fn(),
			onDidChangeModelContent: jest.fn(),
			getValue: jest.fn(() => 'abc'),
			onDidBlurEditorText: jest.fn(),
			onDidBlurEditorWidget: jest.fn(),
			onDidFocusEditorText: jest.fn(),
			onDidChangeCursorSelection: fn,
			onContextMenu: jest.fn(),
			dispose: jest.fn(),
			getSelections: jest.fn(() => [{}, {}, {}]),
			getModel: jest.fn(() => ({
				getValueInRange: jest
					.fn()
					.mockImplementationOnce(() => 'a')
					.mockImplementation(() => 'b'),
			})),
		}));

		const onCursorSelectionFn = jest.fn();

		render(<Editor onCursorSelection={onCursorSelectionFn} />);

		// Mock trigger onDidChangeCursorSelection
		emits[0]();

		expect(onCursorSelectionFn.mock.calls[0][0]).toBe('abb');
	});

	it("Should change contextMenu's display to fixed", () => {
		const emits: ((e: any) => {})[] = [];
		const fn = jest.fn((cb) => emits.push(cb));

		(monaco.editor.create as jest.Mock).mockReset().mockImplementation(() => ({
			addCommand: jest.fn(),
			onDidChangeModelContent: jest.fn(),
			getValue: jest.fn(() => 'abc'),
			onDidBlurEditorText: jest.fn(),
			onDidBlurEditorWidget: jest.fn(),
			onDidFocusEditorText: jest.fn(),
			onDidChangeCursorSelection: jest.fn(),
			onContextMenu: fn,
			dispose: jest.fn(),
			getDomNode: jest.fn(() => document.body),
		}));

		render(<Editor />);

		// Mock trigger onContextMenu
		const div = document.createElement('div');
		div.classList.add('monaco-menu-container');
		document.body.appendChild(div);
		emits[0]({ event: { posy: 100, posx: 100 } });

		expect(div.style.position).toBe('fixed');
		expect(div.style.top).toBe('100px');
		expect(div.style.left).toBe('100px');
	});

	it('Should update language since it changed', async () => {
		(monaco.editor.create as jest.Mock).mockReset().mockImplementation(() => ({
			addCommand: jest.fn(),
			onDidChangeModelContent: jest.fn(),
			getValue: jest.fn(() => 'abc'),
			onDidBlurEditorText: jest.fn(),
			onDidBlurEditorWidget: jest.fn(),
			onDidFocusEditorText: jest.fn(),
			onDidChangeCursorSelection: jest.fn(),
			onContextMenu: jest.fn(),
			dispose: jest.fn(),
			getModel: jest.fn(),
		}));
		(monaco.editor.setModelLanguage as jest.Mock).mockReset();

		const { rerender } = render(<Editor language="json" />);

		await act(async () => {
			rerender(<Editor language="html" />);
		});

		expect((monaco.editor.setModelLanguage as jest.Mock).mock.calls[0][1]).toBe('html');
	});

	it('Should update options since it changed', async () => {
		const fn = jest.fn();
		(monaco.editor.create as jest.Mock).mockReset().mockImplementation(() => ({
			addCommand: jest.fn(),
			onDidChangeModelContent: jest.fn(),
			getValue: jest.fn(() => 'abc'),
			onDidBlurEditorText: jest.fn(),
			onDidBlurEditorWidget: jest.fn(),
			onDidFocusEditorText: jest.fn(),
			onDidChangeCursorSelection: jest.fn(),
			onContextMenu: jest.fn(),
			dispose: jest.fn(),
			getModel: jest.fn(),
			updateOptions: fn,
		}));

		const { rerender } = render(<Editor options={{}} />);

		await act(async () => {
			rerender(<Editor options={{}} />);
		});

		expect(fn).toBeCalled();
	});

	it('Should update value since sync is true and scroll to bottom', async () => {
		const setValueFn = jest.fn();
		const fn = jest.fn();
		(monaco.editor.create as jest.Mock).mockReset().mockImplementation(() => ({
			addCommand: jest.fn(),
			onDidChangeModelContent: jest.fn(),
			getValue: jest.fn(() => 'abc'),
			onDidBlurEditorText: jest.fn(),
			onDidBlurEditorWidget: jest.fn(),
			onDidFocusEditorText: jest.fn(),
			onDidChangeCursorSelection: jest.fn(),
			onContextMenu: jest.fn(),
			dispose: jest.fn(),
			getModel: jest.fn(() => ({
				getLanguageId: jest.fn(() => 'log'),
				getLineCount: jest.fn(),
			})),
			setValue: setValueFn,
			revealLineInCenterIfOutsideViewport: fn,
		}));

		const { rerender, container } = render(<Editor value="" sync />);

		expect(container.querySelector<HTMLElement>('.dt-placeholder')?.style.display).toBe(
			'initial',
		);

		await act(async () => {
			rerender(<Editor value="ab" sync />);
		});

		expect(container.querySelector<HTMLElement>('.dt-placeholder')?.style.display).toBe('none');

		expect(setValueFn).toBeCalledWith('ab');
		expect(fn).toBeCalled();
	});

	it('Should reveal cursor position', () => {
		const setPositionFn = jest.fn();
		const focusFn = jest.fn();
		const fn = jest.fn();
		(monaco.editor.create as jest.Mock).mockReset().mockImplementation(() => ({
			addCommand: jest.fn(),
			onDidChangeModelContent: jest.fn(),
			getValue: jest.fn(() => 'abc'),
			onDidBlurEditorText: jest.fn(),
			onDidBlurEditorWidget: jest.fn(),
			onDidFocusEditorText: jest.fn(),
			onDidChangeCursorSelection: jest.fn(),
			onContextMenu: jest.fn(),
			dispose: jest.fn(),
			getModel: jest.fn(),
			setPosition: setPositionFn,
			focus: focusFn,
			revealPosition: fn,
		}));
		render(<Editor cursorPosition={{ lineNumber: 1, column: 1 }} />);

		expect(setPositionFn).toBeCalled();
		expect(focusFn).toBeCalled();
		expect(fn).toBeCalled();
	});
});
