/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import type { CSSProperties } from 'react';
import { useEffect, useRef } from 'react';
import * as monaco from '@dtinsight/molecule/esm/monaco';
import classNames from 'classnames';
import './language/jsonlog';

import { defaultOptions } from './config';
import './style.scss';

interface IEditorProps {
    className?: string;
    style?: CSSProperties;
    value?: string;
    language?: string;
    sync?: boolean;
    options?: monaco.editor.IStandaloneEditorConstructionOptions;
    cursorPosition?: monaco.IPosition;
    placeholder?: string;
    onChange?: (value: string, instance: monaco.editor.IStandaloneCodeEditor) => void;
    onBlur?: (value: string) => void;
    onFocus?: (value: string) => void;
    onCursorSelection?: (value: string) => void;
}

export default function Editor({
    className,
    style,
    options,
    value,
    sync,
    language,
    cursorPosition,
    placeholder,
    onChange,
    onBlur,
    onFocus,
    onCursorSelection,
}: IEditorProps) {
    const container = useRef<HTMLDivElement>(null);
    const monacoEditor = useRef<monaco.editor.IStandaloneCodeEditor>();
    const placeholderDOM = useRef<HTMLPreElement>(null);

    const initMonaco = () => {
        if (container.current) {
            const editorOptions = {
                ...defaultOptions,
                ...options,
                value,
                language: language || 'sql',
            };

            monacoEditor.current = monaco.editor.create(container.current, editorOptions);

            handleShowPlaceholder(value);

            if (monacoEditor.current && cursorPosition) {
                monacoEditor.current.setPosition(cursorPosition);
                monacoEditor.current.focus();
                monacoEditor.current.revealPosition(cursorPosition, monaco.editor.ScrollType.Immediate);
            }

            initEditor();
            registerCommands();
        }
    };

    /**
     * TODO: I don't why lost these commands, it should be figure it out after a while
     */
    const registerCommands = () => {
        if (monacoEditor.current) {
            monacoEditor.current.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyZ, () => {
                monacoEditor.current?.trigger('editor', 'undo', null);
            });

            monacoEditor.current.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyA, () => {
                monacoEditor.current?.trigger('editor', 'editor.action.selectAll', null);
            });

            monacoEditor.current.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyMod.Shift | monaco.KeyCode.KeyA, () => {
                monacoEditor.current?.trigger('editor', 'redo', null);
            });

            // disabled these global commands in editor
            monacoEditor.current.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyF, () => {});
            monacoEditor.current.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.KeyK, () => {});
            monacoEditor.current.addCommand(monaco.KeyMod.CtrlCmd | monaco.KeyCode.Comma, () => {});
            monacoEditor.current.addCommand(
                monaco.KeyMod.CtrlCmd | monaco.KeyMod.Shift | monaco.KeyCode.KeyP,
                () => {}
            );
        }
    };

    const initEditorEvent = () => {
        if (monacoEditor.current) {
            monacoEditor.current.onDidChangeModelContent(() => {
                const newValue = monacoEditor.current!.getValue();
                if (onChange) {
                    onChange(newValue, monacoEditor.current!);
                }
                handleShowPlaceholder(newValue);
            });

            monacoEditor.current.onDidBlurEditorText(() => {
                if (onBlur) {
                    const val = monacoEditor.current!.getValue();
                    onBlur(val);
                }
            });

            monacoEditor.current.onDidBlurEditorWidget(() => {
                handleShowPlaceholder(monacoEditor.current!.getValue());
            });

            monacoEditor.current.onDidFocusEditorText(() => {
                if (onFocus) {
                    const val = monacoEditor.current!.getValue();
                    onFocus(val);
                }
            });

            monacoEditor.current.onDidChangeCursorSelection(() => {
                const ranges = monacoEditor.current!.getSelections() || [];
                const model = monacoEditor.current!.getModel();
                let selectionContent = '';
                if (model) {
                    for (let i = 0; i < ranges.length; i += 1) {
                        selectionContent += model.getValueInRange(ranges[i]);
                    }
                }
                if (onCursorSelection) {
                    onCursorSelection(selectionContent);
                }
            });
            /**
             * 改变contextMenu的定位为fixed，避免容器内overflow:hidden属性截断contextMenu
             */
            monacoEditor.current.onContextMenu((e) => {
                const contextMenuElement = monacoEditor.current
                    ?.getDomNode()
                    ?.querySelector<HTMLElement>('.monaco-menu-container');

                if (contextMenuElement) {
                    const posY =
                        e.event.posy + contextMenuElement.clientHeight > window.innerHeight
                            ? e.event.posy - contextMenuElement.clientHeight
                            : e.event.posy;

                    const posX =
                        e.event.posx + contextMenuElement.clientWidth > window.innerWidth
                            ? e.event.posx - contextMenuElement.clientWidth
                            : e.event.posx;

                    contextMenuElement.style.position = 'fixed';
                    contextMenuElement.style.top = `${Math.max(0, Math.floor(posY))}px`;
                    contextMenuElement.style.left = `${Math.max(0, Math.floor(posX))}px`;
                }
            });
        }
    };

    const initEditor = () => {
        initEditorEvent();
    };

    const handleShowPlaceholder = (val?: string) => {
        if (!val) {
            placeholderDOM.current!.style.display = 'initial';
        } else {
            placeholderDOM.current!.style.display = 'none';
        }
    };

    const destroyMonaco = () => {
        if (monacoEditor.current) {
            monacoEditor.current.dispose();
        }
    };

    useEffect(() => {
        if (monacoEditor.current) {
            if (sync) {
                /**
                 * value更新， 并且含有sync同步标记，则更新编辑器值
                 */
                const editorText = value || '';
                monacoEditor.current.setValue(editorText);
                handleShowPlaceholder(editorText);

                const currentLanguage = monacoEditor.current.getModel()?.getLanguageId();
                const isScrollToBottom = currentLanguage?.endsWith('log');
                if (isScrollToBottom) {
                    monacoEditor.current.revealLineInCenterIfOutsideViewport(
                        monacoEditor.current.getModel()!.getLineCount()
                    );
                }
            }
        }
    }, [value]);

    useEffect(() => {
        if (monacoEditor.current) {
            monaco.editor.setModelLanguage(monacoEditor.current.getModel()!, language || 'ini');
        }
    }, [language]);

    useEffect(() => {
        if (monacoEditor.current && options) {
            monacoEditor.current.updateOptions(options);
        }
    }, [options]);

    useEffect(() => {
        initMonaco();

        return () => {
            destroyMonaco();
        };
    }, []);

    return (
        <div
            className={classNames('code-editor', className)}
            style={{
                position: 'relative',
                minHeight: '400px',
                height: '100%',
                width: '100%',
                ...style,
            }}
            ref={container}
        >
            <pre ref={placeholderDOM} className="dt-placeholder" style={{ fontSize: options?.fontSize || 12 }}>
                {placeholder}
            </pre>
        </div>
    );
}
