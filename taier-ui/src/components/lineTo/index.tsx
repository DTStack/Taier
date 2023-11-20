import type { Root } from 'react-dom/client';
import { createRoot } from 'react-dom/client';
import { Table } from 'antd';
import type { ColumnType } from 'antd/lib/table';
import { mouse, select } from 'd3-selection';
import { mergeWith } from 'lodash';

import './index.scss';

interface IOptions<T> {
    rowKey: string | ((record: T) => any);
    className?: string;
    onDragStart?: (data: T, node: Element) => boolean;
    onDrop?: (data: T, node: Element) => boolean;
    onLineChanged?: (source: T, target: T) => void;
    onLineClick?: (source: T, target: T) => void;
    onRenderColumns?: (source: boolean) => ColumnType<T>[];
    onRenderFooter?: (source: boolean) => React.ReactNode;
}

export default class LintTo<T extends object> {
    container: HTMLElement;

    sourceRoot: Root | null = null;
    targetRoot: Root | null = null;

    source: T[] = [];
    target: T[] = [];

    lines: {
        from: T;
        to: T;
    }[] = [];

    rowClassName = 'taier__lineTo__row';
    sourceClassName = 'taier__lintTo__source';
    targetClassName = 'taier__lintTo__target';
    svgClassName = 'taier__lintTo__svg';
    sourcePointsClassName = 'taier__lintTo__sourcePoints';
    targetPointsClassName = 'taier__lintTo__targetPoints';
    pointClassName = 'taier__lintTo__point';
    previewerLineClassName = 'taier__lintTo__previewer__line';
    linesClassName = 'taier__lintTo__lines';
    lineClassName = 'taier__lintTo__line';
    tooltipClassName = 'taier__lineTo__tooltip';
    tooltipContentClassName = 'taier__lintTo__tooltip__content';

    options: IOptions<T> = {
        rowKey: 'key',
    };

    constructor(container: HTMLElement, options?: Partial<IOptions<T>>) {
        this.container = container;
        this.options = mergeWith(options, this.options, (obj) => obj);

        this.appendSVG();
    }

    private appendSVG = () => {
        const container = select(this.container).attr('class', `taier__lineTo ${this.options.className || ''}`);

        const source = container.append('div').attr('class', this.sourceClassName);
        const lines = container.append('svg').attr('class', this.svgClassName);
        const target = container.append('div').attr('class', this.targetClassName);

        const tooltipWrapper = lines
            .append('foreignObject')
            .attr('class', this.tooltipClassName)
            .attr('x', 0)
            .attr('y', 0)
            .attr('width', '100%')
            .attr('height', '100%');

        const marker = lines
            .append('marker')
            .attr('id', 'arrow')
            .attr('markerUnits', 'userSpaceOnUse')
            .attr('markerWidth', '20')
            .attr('markerHeight', '20')
            .attr('viewBox', '0 0 12 12')
            .attr('refX', '6')
            .attr('refY', '6')
            .attr('orient', 'auto');

        tooltipWrapper
            .append('xhtml:div')
            .attr('class', this.tooltipContentClassName)
            .text(() => '取消映射');
        marker.append('path').attr('d', 'M2,3 L9,6 L2,9 L2,6 L2,3').attr('fill', 'currentColor');

        this.sourceRoot = createRoot(source.node() as Element);
        this.targetRoot = createRoot(target.node() as Element);
    };

    public setSourceData(data: T[]) {
        this.source = data;
    }

    public setTargetData(data: T[]) {
        this.target = data;
    }

    public setLine(lines: { from: T; to: T }[]) {
        this.lines = lines;
    }

    public render() {
        Promise.all([
            new Promise<void>((resolve) => {
                this.sourceRoot?.render(
                    <Table
                        ref={() => resolve()}
                        rowKey={this.options.rowKey}
                        dataSource={this.source}
                        rowClassName={this.rowClassName}
                        columns={this.options.onRenderColumns?.(true)}
                        size="small"
                        pagination={false}
                        bordered
                        footer={
                            this.options.onRenderFooter?.(true) ? () => this.options.onRenderFooter?.(true) : undefined
                        }
                    />
                );
            }),
            new Promise<void>((resolve) => {
                this.targetRoot?.render(
                    <Table
                        ref={() => resolve()}
                        rowKey={this.options.rowKey}
                        dataSource={this.target}
                        rowClassName={this.rowClassName}
                        columns={this.options.onRenderColumns?.(false)}
                        size="small"
                        pagination={false}
                        bordered
                        footer={
                            this.options.onRenderFooter?.(false)
                                ? () => this.options.onRenderFooter?.(false)
                                : undefined
                        }
                    />
                );
            }),
        ]).then(() => {
            // Ensure render drag points after table
            this.renderPoint();
            this.renderLines();
            this.bindEvents();
        });
    }

    private getSource() {
        return select(`.${this.sourceClassName}`);
    }

    private getTarget() {
        return select(`.${this.targetClassName}`);
    }

    private getSvg() {
        return select(`.${this.svgClassName}`);
    }

    private getLines() {
        return select(`.${this.linesClassName}`);
    }

    private getPreviewLine() {
        return this.getSvg().select(`.${this.previewerLineClassName}`);
    }

    /**
     * 渲染拖拽点
     */
    private renderPoint() {
        // source points
        const rowsInSource = this.getSource().select(`.${this.rowClassName}`);

        this.getSvg().select(`.${this.sourcePointsClassName}`).remove();
        this.getSvg().select(`.${this.targetPointsClassName}`).remove();

        if (rowsInSource.node()) {
            const { height } = (rowsInSource.node() as HTMLDivElement).getBoundingClientRect();

            this.getSvg()
                .append('g')
                .attr('class', this.sourcePointsClassName)
                .selectAll('g')
                .data(this.source)
                .enter()
                .append('g')
                .attr('class', `${this.pointClassName}`)
                .append('circle')
                .attr('cx', () => 10)
                .attr('cy', (_, i) => height * (i + 1.5))
                .attr('r', 5)
                .attr('stroke-width', 2)
                .attr('stroke', '#fff')
                .attr('fill', 'currentColor');
        }

        // target points
        const rowsInTarget = this.getTarget().select(`.${this.rowClassName}`);
        if (rowsInTarget.node()) {
            const { height } = (rowsInTarget.node() as HTMLDivElement).getBoundingClientRect();

            const { width } = document.querySelector(`.${this.svgClassName}`)!.getBoundingClientRect();

            this.getSvg()
                .append('g')
                .attr('class', this.targetPointsClassName)
                .selectAll('g')
                .data(this.target)
                .enter()
                .append('g')
                .attr('class', `${this.pointClassName}`)
                .append('circle')
                .attr('cx', () => width - 10)
                .attr('cy', (_, i) => height * (i + 1.5))
                .attr('r', 5)
                .attr('stroke-width', 2)
                .attr('stroke', '#fff')
                .attr('fill', 'currentColor');
        }
    }

    private bindEvents() {
        select(`.${this.sourcePointsClassName}`)
            .selectAll<SVGCircleElement, T>(`.${this.pointClassName}`)
            .on('mousedown', (data, idx, nodes) => {
                const node = nodes[idx];
                const isContinue = this.options.onDragStart?.(data, node);

                if (isContinue === true || isContinue === undefined) {
                    const cx = node.firstElementChild!.getAttribute('cx')!;
                    const cy = node.firstElementChild!.getAttribute('cy')!;

                    // Insert a previewer line
                    select(`.${this.svgClassName}`)
                        .append('g')
                        .data([data])
                        .append('line')
                        .attr('class', this.previewerLineClassName)
                        .attr('x1', cx)
                        .attr('y1', cy)
                        .attr('x2', cx)
                        .attr('y2', cy)
                        .attr('stroke', 'currentColor')
                        .attr('stroke-width', '2')
                        .attr('marker-end', 'url(#arrow)');
                }
            });

        this.getSvg()
            .on('mousemove', () => {
                if (this.getPreviewLine().size()) {
                    const mousePos = mouse(document.querySelector(`.${this.svgClassName}`)!);
                    const [ex, ey] = this.getNearestElement(mousePos);
                    this.getPreviewLine().attr('x2', ex).attr('y2', ey);
                }
            })
            .on('mouseup', () => {
                if (this.getPreviewLine().size()) {
                    const node = this.getPreviewLine().node() as SVGGElement;
                    const target = this.findTargetElementByPosition([
                        Number(node.getAttribute('x2')),
                        Number(node.getAttribute('y2')),
                    ]);

                    if (target) {
                        const isContinue = this.options.onDrop?.(target.data, target.element);
                        if (isContinue === true || isContinue === undefined) {
                            this.options.onLineChanged?.(this.getPreviewLine().data()[0] as T, target.data);
                        }
                    }

                    // Remove preview line
                    (this.getPreviewLine().node() as Element)?.parentElement?.remove();
                }
            });

        this.getLines()
            .selectAll<SVGGElement, { from: T; to: T }>('g')
            .on('mouseover', (_, idx, nodes) => {
                const node: SVGGElement = nodes[idx];
                const lineEle = node.firstElementChild;
                if (lineEle) {
                    const ele: HTMLDivElement | null = select<HTMLDivElement, void>(
                        `.${this.tooltipContentClassName}`
                    ).node();
                    const top = Number(lineEle.getAttribute('y1'));
                    const left = (Number(lineEle.getAttribute('x2')) + Number(lineEle.getAttribute('x1'))) / 2;
                    if (ele) {
                        ele.style.display = 'block';
                        ele.style.top = `${top - 32}px`;
                        ele.style.left = `${left - 30}px`;
                    }
                }
            })
            .on('mouseout', () => {
                setTimeout(() => {
                    const ele: HTMLDivElement | null = select<HTMLDivElement, void>(
                        `.${this.tooltipContentClassName}`
                    ).node();
                    if (ele) {
                        ele.removeAttribute('style');
                    }
                }, 500);
            })
            .on('click', (data) => {
                this.options.onLineClick?.(data.from, data.to);

                const ele: HTMLDivElement | null = select<HTMLDivElement, void>(
                    `.${this.tooltipContentClassName}`
                ).node();
                if (ele) {
                    ele.removeAttribute('style');
                }
            });
    }

    private renderLines() {
        if (this.lines) {
            this.getLines().remove();

            const positions = this.lines.map((i) => {
                const [x1, y1] = this.getPositionByData(i.from, 'source');
                const [x2, y2] = this.getPositionByData(i.to, 'target');
                return {
                    x1,
                    y1,
                    x2,
                    y2,
                    ...i,
                };
            });

            select(`.${this.svgClassName}`)
                .append('g')
                .attr('class', this.linesClassName)
                .selectAll('g')
                .data(positions)
                .enter()
                .append('g')
                .attr('class', this.lineClassName)
                .append('line')
                .attr('x1', (d) => d.x1)
                .attr('y1', (d) => d.y1)
                .attr('x2', (d) => Number(d.x2) - 10)
                .attr('y2', (d) => d.y2)
                .attr('stroke', 'currentColor')
                .attr('stroke-width', 2)
                .attr('marker-end', 'url(#arrow)');
        }
    }

    private getPositionByData(data: T, source: 'source' | 'target') {
        const nodeSelections = select(
            `.${source === 'source' ? this.sourcePointsClassName : this.targetPointsClassName}`
        ).selectAll<SVGCircleElement, T>(`.${this.pointClassName}`);

        const getRowKey = (record: T) =>
            typeof this.options.rowKey === 'string'
                ? // @ts-ignore
                  record[this.options.rowKey]
                : this.options.rowKey(record);

        const idx = nodeSelections.data().findIndex((i) => getRowKey(i) === getRowKey(data));

        const circleNode = nodeSelections.nodes()[idx]?.firstElementChild;

        return [Number(circleNode?.getAttribute('cx')), Number(circleNode?.getAttribute('cy'))] as const;
    }

    private getNearestElement(position: [number, number]) {
        const nodes = select(`.${this.targetPointsClassName}`)
            .selectAll<SVGCircleElement, T>(`.${this.pointClassName}`)
            .nodes();

        const threshold = 10;
        for (let index = 0; index < nodes.length; index += 1) {
            const node = nodes[index];
            const cx = Number(node.firstElementChild?.getAttribute('cx') || '0');
            const cy = Number(node.firstElementChild?.getAttribute('cy') || '0');

            if (Math.abs(position[0] - cx) < threshold && Math.abs(position[1] - cy) < threshold) {
                return [cx, cy];
            }
        }

        return position;
    }

    private findTargetElementByPosition(position: [number, number]) {
        const selections = select(`.${this.targetPointsClassName}`).selectAll<SVGCircleElement, T>(
            `.${this.pointClassName}`
        );
        const nodes = selections.nodes();
        const data = selections.data();

        for (let index = 0; index < nodes.length; index += 1) {
            const node = nodes[index];
            const cx = Number(node.firstElementChild?.getAttribute('cx') || '0');
            const cy = Number(node.firstElementChild?.getAttribute('cy') || '0');

            if (cx === position[0] && cy === position[1]) {
                return {
                    data: data[index],
                    element: node,
                };
            }
        }

        return null;
    }
}
