import type {
    mxCell,
    mxGraph,
    mxGraphExportObject,
    mxGraphOptions,
    mxGraphView,
    mxPoint,
    mxRectangle,
    StyleMap,
} from 'mxgraph';
import Mx from 'mxgraph';

import './index.scss';

interface IMxGraph extends mxGraph {
    scrollTileSize?: mxRectangle;
    getPagePadding?: () => mxPoint;
    getPageSize?: () => mxRectangle;
    getPageLayout?: () => mxRectangle;
}

/**
 * refer to: https://jgraph.github.io/mxgraph/docs/js-api/files/index-txt.html
 * typings refer to: https://github.com/typed-mxgraph/typed-mxgraph#readme
 */
class MxFactory {
    static config: mxGraphOptions = {
        mxImageBasePath: 'images',
        mxLanguage: 'none',
        mxLoadResources: false,
        mxLoadStylesheets: false,
    };

    static VertexSize = {
        width: 210,
        height: 50,
    };

    public layoutEventHandler: () => void = () => {};

    public mxInstance: mxGraphExportObject;

    public mxGraph: IMxGraph | null = null;

    constructor() {
        this.mxInstance = Mx(MxFactory.config);
    }

    /**
     * 边框的默认样式
     */
    private getDefaultVertexStyle = () => {
        const { mxConstants, mxPerimeter } = this.mxInstance;
        const style: StyleMap = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_FONTSIZE] = '12';
        style[mxConstants.STYLE_FONTFAMILY] = 'PingFangSC-Regular';
        style[mxConstants.FONT_BOLD] = 'normal';
        style[mxConstants.STYLE_WHITE_SPACE] = 'nowrap';
        style[mxConstants.STYLE_FONTSTYLE] = 1;
        return style;
    };

    /**
     * 边线的默认样式
     */
    private getDefaultEdgeStyle = () => {
        const { mxConstants, mxEdgeStyle } = this.mxInstance;
        const style: StyleMap = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
        style[mxConstants.STYLE_STROKECOLOR] = '#3F87FFFF';
        style[mxConstants.STYLE_STROKEWIDTH] = 1;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.TopToBottom;
        style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_BLOCK;
        style[mxConstants.STYLE_FONTSIZE] = '10';
        style[mxConstants.STYLE_ROUNDED] = false;
        return style;
    };

    /**
     * 初始化 mxgraph 实例
     */
    create(containerDom: HTMLElement, config?: Record<string, any>) {
        const {
            mxGraphView,
            mxText,
            mxGraph: MxGraph,
            mxEvent,
            mxConstants,
            mxGraphHandler,
            mxSvgCanvas2D,
            mxClient,
        } = this.mxInstance;

        /**
         * Function: updateStroke
         *
         * Transfers the stroke attributes from <state> to <node>.
         */
        mxSvgCanvas2D.prototype.updateStroke = function () {
            const s = this.state;

            const strokeColor = String(s.strokeColor);
            this.node.setAttribute(
                'stroke',
                // Prevent transform css variables into lower case
                strokeColor.startsWith('var') ? strokeColor : strokeColor.toLowerCase()
            );

            if (s.alpha < 1 || s.strokeAlpha < 1) {
                this.node.setAttribute('stroke-opacity', (s.alpha * s.strokeAlpha).toString());
            }

            const sw = this.getCurrentStrokeWidth();

            if (sw !== 1) {
                this.node.setAttribute('stroke-width', sw.toString());
            }

            if (this.node.nodeName === 'path') {
                this.updateStrokeAttributes();
            }

            if (s.dashed) {
                this.node.setAttribute(
                    'stroke-dasharray',
                    this.createDashPattern((s.fixDash ? 1 : s.strokeWidth) * s.scale)
                );
            }
        };

        /**
         * Function: updateFill
         *
         * Transfers the stroke attributes from <state> to <node>.
         */
        mxSvgCanvas2D.prototype.updateFill = function () {
            const s = this.state;

            if (s.alpha < 1 || s.fillAlpha < 1) {
                this.node.setAttribute('fill-opacity', (s.alpha * s.fillAlpha).toString());
            }

            if (s.fillColor != null) {
                if (s.gradientColor != null) {
                    const id = this.getSvgGradient(
                        String(s.fillColor),
                        String(s.gradientColor),
                        s.gradientFillAlpha.toString(),
                        s.gradientAlpha.toString(),
                        s.gradientDirection
                    );

                    if (
                        !mxClient.IS_CHROMEAPP &&
                        !mxClient.IS_IE &&
                        !mxClient.IS_IE11 &&
                        !mxClient.IS_EDGE &&
                        this.root.ownerDocument === document
                    ) {
                        // Workaround for potential base tag and brackets must be escaped
                        const base = this.getBaseUrl().replace(/([()])/g, '\\$1');
                        this.node.setAttribute('fill', `url(${base}#${id})`);
                    } else {
                        this.node.setAttribute('fill', `url(#${id})`);
                    }
                } else {
                    const fillColor = String(s.fillColor);
                    this.node.setAttribute(
                        'fill',
                        // Prevent transform css variables into lower case
                        fillColor.startsWith('var') ? fillColor : fillColor.toLowerCase()
                    );
                }
            }
        };

        mxGraphView.prototype.optimizeVmlReflows = false;
        // to avoid calling getBBox
        mxText.prototype.ignoreStringSize = true;
        mxGraphHandler.prototype.guidesEnabled = true;
        // Disable context menu
        mxEvent.disableContextMenu(containerDom);
        const graph = new MxGraph(containerDom);

        this.mxGraph = graph;

        // 启用绘制
        graph.setPanning(true);
        // 允许鼠标移动画布
        graph.panningHandler.useLeftButtonForPanning = true;
        graph.setTooltips(config?.tooltips ?? true);
        graph.view.setScale(1);
        // Enables HTML labels
        graph.setHtmlLabels(true);

        graph.setAllowDanglingEdges(false);
        // 禁止连接
        graph.setConnectable(config?.connectable ?? false);
        // 禁止Edge对象移动
        graph.isCellsMovable = function () {
            const cell = graph.getSelectionCell();
            return !(cell && cell.edge);
        };
        // 禁止cell编辑
        graph.isCellEditable = () => false;
        graph.isCellResizable = () => false;

        const vertexStyle = this.getDefaultVertexStyle();
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle);
        // 默认边界样式
        const edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);

        // anchor styles
        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#2491F7';

        mxConstants.VERTEX_SELECTION_COLOR = 'var(--editor-background)';
        // @ts-ignore
        mxConstants.STYLE_OVERFLOW = 'hidden';

        return graph;
    }

    /**
     * Event handler that selects rectangular regions.
     */
    createRubberBand() {
        const { mxRubberband: MxRubberband } = this.mxInstance;
        if (!this.mxGraph) {
            throw new Error('Please call create before createRubberBand');
        }

        return new MxRubberband(this.mxGraph);
    }

    /**
     * Vertex 渲染的 HTML 样式
     * @description Returns the textual representation for the given cell.  This implementation returns the nodename or string-representation of the user object.
     */
    renderVertex(handler: (cell: mxCell) => string) {
        if (this.mxGraph) {
            this.mxGraph.convertValueToString = (cell) => {
                if (cell && cell.value) {
                    return handler(cell);
                }
                return '';
            };
        }
    }

    /**
     * 初始化 graph 相关配置
     */
    public initContainerScroll = () => {
        const { mxRectangle: MxRectangle, mxPoint: MxPoint, mxUtils } = this.mxInstance;
        if (this.mxGraph) {
            const graph = this.mxGraph;
            /**
             * Specifies the size of the size for "tiles" to be used for a graph with
             * scrollbars but no visible background page. A good value is large
             * enough to reduce the number of repaints that is caused for auto-
             * translation, which depends on this value, and small enough to give
             * a small empty buffer around the graph. Default is 400x400.
             */
            graph.scrollTileSize = new MxRectangle(0, 0, 200, 200);

            /**
             * Returns the padding for pages in page view with scrollbars.
             */
            graph.getPagePadding = function () {
                return new MxPoint(
                    Math.max(0, Math.round(graph.container.offsetWidth - 34)),
                    Math.max(0, Math.round(graph.container.offsetHeight - 34))
                );
            };

            /**
             * Returns the size of the page format scaled with the page size.
             */
            graph.getPageSize = function () {
                return this.pageVisible
                    ? new MxRectangle(
                          0,
                          0,
                          this.pageFormat.width * this.pageScale,
                          this.pageFormat.height * this.pageScale
                      )
                    : this.scrollTileSize!;
            };

            /**
             * Returns a rectangle describing the position and count of the
             * background pages, where x and y are the position of the top,
             * left page and width and height are the vertical and horizontal
             * page count.
             */
            graph.getPageLayout = function () {
                const size = this.pageVisible ? this.getPageSize!() : this.scrollTileSize!;
                const bounds = this.getGraphBounds();

                if (bounds.width === 0 || bounds.height === 0) {
                    return new MxRectangle(0, 0, 1, 1);
                }

                // Computes untransformed graph bounds
                const x = Math.ceil(bounds.x / this.view.scale - this.view.translate.x);
                const y = Math.ceil(bounds.y / this.view.scale - this.view.translate.y);
                const w = Math.floor(bounds.width / this.view.scale);
                const h = Math.floor(bounds.height / this.view.scale);

                const x0 = Math.floor(x / size.width);
                const y0 = Math.floor(y / size.height);
                const w0 = Math.ceil((x + w) / size.width) - x0;
                const h0 = Math.ceil((y + h) / size.height) - y0;

                return new MxRectangle(x0, y0, w0, h0);
            };

            // Fits the number of background pages to the graph
            graph.view.getBackgroundPageBounds = function () {
                const layout = (this.graph as IMxGraph).getPageLayout!();
                const page = (this.graph as IMxGraph).getPageSize!();

                return new MxRectangle(
                    this.scale * (this.translate.x + layout.x * page.width),
                    this.scale * (this.translate.y + layout.y * page.height),
                    this.scale * layout.width * page.width,
                    this.scale * layout.height * page.height
                );
            };

            graph.getPreferredPageSize = function () {
                const pages = this.getPageLayout!();
                const size = this.getPageSize!();

                return new MxRectangle(0, 0, pages.width * size.width, pages.height * size.height);
            };

            /**
             * Guesses autoTranslate to avoid another repaint (see below).
             * Works if only the scale of the graph changes or if pages
             * are visible and the visible pages do not change.
             */
            const graphViewValidate = graph.view.validate;
            graph.view.validate = function (this: {
                graph: IMxGraph;
                translate: mxPoint;
                scale: number;
                // set in customized sizeDidChange method
                x0: number;
                y0: number;
            }) {
                if (this.graph.container != null && mxUtils.hasScrollbars(this.graph.container)) {
                    const pad = this.graph.getPagePadding!();
                    const size = this.graph.getPageSize!();

                    // Updating scrollbars here causes flickering in quirks and is not needed
                    // if zoom method is always used to set the current scale on the graph.
                    this.translate.x = pad.x / this.scale - (this.x0 || 0) * size.width;
                    this.translate.y = pad.y / this.scale - (this.y0 || 0) * size.height;
                }

                // eslint-disable-next-line prefer-rest-params
                graphViewValidate.apply(this, arguments as any);
            };

            const graphSizeDidChange = graph.sizeDidChange;
            graph.sizeDidChange = function (
                this: IMxGraph & {
                    autoTranslate: boolean;
                    // used in view.validate method
                    view: mxGraphView & { x0: number; y0: number };
                }
            ) {
                if (this.container != null && mxUtils.hasScrollbars(this.container)) {
                    const pages = this.getPageLayout!();
                    const pad = this.getPagePadding!();
                    const size = this.getPageSize!();

                    // Updates the minimum graph size
                    const minw = Math.ceil((2 * pad.x) / this.view.scale + pages.width * size.width);
                    const minh = Math.ceil((2 * pad.y) / this.view.scale + pages.height * size.height);

                    const min = graph.minimumGraphSize;

                    // LATER: Fix flicker of scrollbar size in IE quirks mode
                    // after delayed call in window.resize event handler
                    if (min === null || min.width !== minw || min.height !== minh) {
                        graph.minimumGraphSize = new MxRectangle(0, 0, minw, minh);
                    }

                    // Updates auto-translate to include padding and graph size
                    const dx = pad.x / this.view.scale - pages.x * size.width;
                    const dy = pad.y / this.view.scale - pages.y * size.height;

                    if (!this.autoTranslate && (this.view.translate.x !== dx || this.view.translate.y !== dy)) {
                        this.autoTranslate = true;
                        this.view.x0 = pages.x;
                        this.view.y0 = pages.y;

                        // NOTE: THIS INVOKES THIS METHOD AGAIN. UNFORTUNATELY THERE IS NO WAY AROUND THIS SINCE THE
                        // BOUNDS ARE KNOWN AFTER THE VALIDATION AND SETTING THE TRANSLATE TRIGGERS A REVALIDATION.
                        // SHOULD MOVE TRANSLATE/SCALE TO VIEW.
                        const tx = graph.view.translate.x;
                        const ty = graph.view.translate.y;

                        graph.view.setTranslate(dx, dy);
                        graph.container.scrollLeft += (dx - tx) * graph.view.scale;
                        graph.container.scrollTop += (dy - ty) * graph.view.scale;

                        this.autoTranslate = false;
                        return;
                    }

                    // eslint-disable-next-line prefer-rest-params
                    graphSizeDidChange.apply(this, arguments as any);
                }
            };
        }
    };

    setView({ scale, dx, dy }: { scale: number; dx: number; dy: number }) {
        if (this.mxGraph) {
            this.mxGraph.view.setScale(scale);
            this.mxGraph.view.setTranslate(dx, dy);
        }
    }

    resetScrollPosition() {
        if (this.mxGraph) {
            const graph = this.mxGraph;
            const bounds = graph.getGraphBounds();
            const boundsWidth = Math.max(bounds.width, graph.scrollTileSize!.width * graph.view.scale);
            const boundsHeight = Math.max(bounds.height, graph.scrollTileSize!.height * graph.view.scale);
            graph.container.scrollTop = Math.floor(
                Math.max(0, bounds.y - Math.max(20, (graph.container.clientHeight - boundsHeight) / 2))
            );
            graph.container.scrollLeft = Math.floor(
                Math.max(0, bounds.x - Math.max(0, (graph.container.clientWidth - boundsWidth) / 2))
            );
        }
    }

    dispose() {
        if (this.mxGraph) {
            this.mxGraph.destroy();
        }
    }
}

export default MxFactory;
