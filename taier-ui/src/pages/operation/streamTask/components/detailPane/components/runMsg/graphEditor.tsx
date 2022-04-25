/* eslint-disable new-cap */
import * as React from 'react';
import { Tooltip, Spin } from 'antd'
import MxFactory from '@/components/mxGraph';
// import { Fullscreen } from 'dt-react-component'

interface Props {
    targetKey?: string;
    isSubVertex?: boolean;
    loading?: boolean;
    rootCell?: any;
    hideMenu?: any;
    refresh?: Function;
    onInit?: (graph: any, Mx: any) => void;
}

export default class GraphEditor extends React.Component<Props, any> {
    state: any = {
    }
    Mx = MxFactory.create();
    Container: any;
    graph: any;
    outln: any;
    componentDidMount () {
        this.Container.innerHTML = '';
        this.graph = '';
        const editor = this.Container
        this.loadEditor(editor);
    }
    render () {
        const { loading, targetKey, isSubVertex, refresh } = this.props;
        return (
            <div className="graph-editor">
                {
                    loading && (<Spin spinning={loading} className="graph_loading"></Spin>)
                }
                <div className="graph_container" ref={(e: any) => { this.Container = e }} />
                <div className="graph_toolbar">
                    <div className="basic_bar">
                        {isSubVertex ? null : <Tooltip placement="left" title="刷新">
                            <div className='toolbar-icon'>
                                <img src='public/img/icon/btn_refresh.svg' onClick={() => {
                                    refresh()
                                    this.alignCenter()
                                }} />
                            </div>
                        </Tooltip>}
                        <Tooltip placement="left" title="居中">
                            <div className='toolbar-icon'>
                                <img src='public/img/icon/btn_arrange.svg' onClick={this.alignCenter} />
                            </div>
                        </Tooltip>
                        <Tooltip placement="left" title="放大">
                            <div className='toolbar-icon'>
                                <img src='public/img/icon/btn_zoomin.svg' onClick={this.zoomIn} />
                            </div>
                        </Tooltip>
                        <Tooltip placement="left" title="缩小">
                            <div className='toolbar-icon'>
                                <img src='public/img/icon/btn_zoomout.svg' onClick={this.zoomOut} />
                            </div>
                        </Tooltip>
                        {!isSubVertex ? null : <div onClick={() => {
                            // 延迟冒泡重新 resize
                            setTimeout(() => {
                                this.alignCenter()
                            }, 100)
                        }}>
                            TODO
                            {/* <Fullscreen
                                className='graph-fullscreen'
                                target={targetKey}
                                fullIcon={<Icon className="alt" type="arrows-alt" />}
                                exitFullIcon={<Icon className="alt" type="shrink" />}
                                isShowTitle={false} /> */}
                        </div>}
                    </div>
                </div>
            </div>)
    }

    loadEditor = (container: any) => {
        const { onInit } = this.props;
        const {
            mxGraph,
            mxEvent,
            mxRubberband,
            mxConstants
        } = this.Mx;
        // mxClient.NO_FO = true;

        mxEvent.disableContextMenu(container)
        const graph = new mxGraph(container)// eslint-disable-line
        this.graph = graph
        // 启用绘制
        graph.setPanning(true);
        graph.keepEdgesInBackground = true;
        // 允许鼠标移动画布
        graph.panningHandler.useLeftButtonForPanning = true;
        graph.setCellsMovable(false);
        graph.setEnabled(true); // 设置启用,就是允不允许你改变CELL的形状内容。
        graph.setConnectable(false); // 是否允许Cells通过其中部的连接点新建连接,false则通过连接线连接
        graph.setCellsResizable(false); // 禁止改变元素大小
        graph.setAutoSizeCells(false);
        graph.centerZoom = true;
        graph.setTooltips(false);
        graph.view.setScale(1)
        // Enables HTML labels
        graph.setHtmlLabels(true)
        graph.setAllowDanglingEdges(false)
        // 禁止Edge对象移动
        graph.isCellsMovable = function () {
            var cell = graph.getSelectionCell()
            return !(cell && (cell.edge || cell.value === 'pagination'))
        }
        // 禁止cell编辑
        graph.isCellEditable = function () {
            return false;
        }

        // 设置Vertex样式
        const vertexStyle = this.getDefaultVertexStyle()
        graph.getStylesheet().putDefaultVertexStyle(vertexStyle);

        // 默认边界样式
        let edgeStyle = this.getDefaultEdgeStyle();
        graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);
        // anchor styles
        mxConstants.HANDLE_FILLCOLOR = '#ffffff';
        mxConstants.HANDLE_STROKECOLOR = '#42B1FB';
        mxConstants.VERTEX_SELECTION_COLOR = '#42B1FB';
        mxConstants.VERTEX_SELECTION_DASHED = false;
        mxConstants.CURSOR_MOVABLE_VERTEX = 'pointer';
        mxConstants.EDGE_SELECTION_COLOR = '#2491F7';
        mxConstants.EDGE_SELECTION_DASHED = false;
        // 重置tooltip
        // enables rubberband
        this.initContainerScroll();
        new mxRubberband(graph)// eslint-disable-line
        onInit && onInit(graph, this.Mx);
    }
    initContainerScroll = () => { // 滚动监听，一般为默认，不需要更改
        const {
            mxRectangle,
            mxPoint,
            mxUtils
        } = this.Mx
        const graph = this.graph;
        /**
         * Specifies the size of the size for "tiles" to be used for a graph with
         * scrollbars but no visible background page. A good value is large
         * enough to reduce the number of repaints that is caused for auto-
         * translation, which depends on this value, and small enough to give
         * a small empty buffer around the graph. Default is 400x400.
         */
        // eslint-disable-next-line new-cap
        graph.scrollTileSize = new mxRectangle(0, 0, 200, 200);

        /**
         * Returns the padding for pages in page view with scrollbars.
         */
        graph.getPagePadding = function () {
            // eslint-disable-next-line new-cap
            return new mxPoint(Math.max(0, Math.round(graph.container.offsetWidth - 40)),
                Math.max(0, Math.round(graph.container.offsetHeight - 40)));
        };

        /**
         * Returns the size of the page format scaled with the page size.
         */
        graph.getPageSize = function () {
            // eslint-disable-next-line new-cap
            return (this.pageVisible) ? new mxRectangle(0, 0, this.pageFormat.width * this.pageScale,
                this.pageFormat.height * this.pageScale) : this.scrollTileSize;
        };

        /**
         * Returns a rectangle describing the position and count of the
         * background pages, where x and y are the position of the top,
         * left page and width and height are the vertical and horizontal
         * page count.
         */
        graph.getPageLayout = function () {
            var size = (this.pageVisible) ? this.getPageSize() : this.scrollTileSize;
            var bounds = this.getGraphBounds();

            if (bounds.width == 0 || bounds.height == 0) {
                // eslint-disable-next-line new-cap
                return new mxRectangle(0, 0, 1, 1);
            } else {
                // Computes untransformed graph bounds
                var x = Math.ceil(bounds.x / this.view.scale - this.view.translate.x);
                var y = Math.ceil(bounds.y / this.view.scale - this.view.translate.y);
                var w = Math.floor(bounds.width / this.view.scale);
                var h = Math.floor(bounds.height / this.view.scale);

                var x0 = Math.floor(x / size.width);
                var y0 = Math.floor(y / size.height);
                var w0 = Math.ceil((x + w) / size.width) - x0;
                var h0 = Math.ceil((y + h) / size.height) - y0;

                // eslint-disable-next-line new-cap
                return new mxRectangle(x0, y0, w0, h0);
            }
        };

        // Fits the number of background pages to the graph
        graph.view.getBackgroundPageBounds = function () {
            var layout = this.graph.getPageLayout();
            var page = this.graph.getPageSize();

            // eslint-disable-next-line new-cap
            return new mxRectangle(this.scale * (this.translate.x + layout.x * page.width),
                this.scale * (this.translate.y + layout.y * page.height),
                this.scale * layout.width * page.width,
                this.scale * layout.height * page.height);
        };

        graph.getPreferredPageSize = function (bounds: any, width: any, height: any) {
            var pages = this.getPageLayout();
            var size = this.getPageSize();

            // eslint-disable-next-line new-cap
            return new mxRectangle(0, 0, pages.width * size.width, pages.height * size.height);
        };

        /**
         * Guesses autoTranslate to avoid another repaint (see below).
         * Works if only the scale of the graph changes or if pages
         * are visible and the visible pages do not change.
         */
        var graphViewValidate = graph.view.validate;
        graph.view.validate = function () {
            if (this.graph.container != null && mxUtils.hasScrollbars(this.graph.container)) {
                var pad = this.graph.getPagePadding();
                var size = this.graph.getPageSize();

                // Updating scrollbars here causes flickering in quirks and is not needed
                // if zoom method is always used to set the current scale on the graph.
                // var tx = this.translate.x;
                // var ty = this.translate.y;
                this.translate.x = pad.x / this.scale - (this.x0 || 0) * size.width;
                this.translate.y = pad.y / this.scale - (this.y0 || 0) * size.height;
            }

            graphViewValidate.apply(this, arguments);
        };

        var graphSizeDidChange = graph.sizeDidChange;
        graph.sizeDidChange = function () {
            if (this.container != null && mxUtils.hasScrollbars(this.container)) {
                var pages = this.getPageLayout();
                var pad = this.getPagePadding();
                var size = this.getPageSize();

                // Updates the minimum graph size
                var minw = Math.ceil(2 * pad.x / this.view.scale + pages.width * size.width);
                var minh = Math.ceil(2 * pad.y / this.view.scale + pages.height * size.height);

                var min = graph.minimumGraphSize;

                // LATER: Fix flicker of scrollbar size in IE quirks mode
                // after delayed call in window.resize event handler
                if (min == null || min.width != minw || min.height != minh) {
                    // eslint-disable-next-line new-cap
                    graph.minimumGraphSize = new mxRectangle(0, 0, minw, minh);
                }

                // Updates auto-translate to include padding and graph size
                var dx = pad.x / this.view.scale - pages.x * size.width;
                var dy = pad.y / this.view.scale - pages.y * size.height;

                if (!this.autoTranslate && (this.view.translate.x != dx || this.view.translate.y != dy)) {
                    this.autoTranslate = true;
                    this.view.x0 = pages.x;
                    this.view.y0 = pages.y;
                    var tx = graph.view.translate.x;
                    var ty = graph.view.translate.y;

                    graph.view.setTranslate(dx, dy);
                    graph.container.scrollLeft += (dx - tx) * graph.view.scale;
                    graph.container.scrollTop += (dy - ty) * graph.view.scale;

                    this.autoTranslate = false;
                    return;
                }

                graphSizeDidChange.apply(this, arguments);
            }
        };
    }
    executeLayout = (change: any, post: any, direction: 'north' | 'west') => { // 更新布局
        const { isSubVertex } = this.props;
        const { mxHierarchicalLayout } = this.Mx
        const graph = this.graph;
        const model = graph.getModel();
        model.beginUpdate();
        try {
            const layout = new mxHierarchicalLayout(graph, false);// eslint-disable-line
            layout.orientation = direction || (isSubVertex ? 'north' : 'west');
            layout.disableEdgeStyle = false;
            layout.interRankCellSpacing = 60;
            layout.intraCellSpacing = 80;
            if (change != null) {
                change();
            }
            layout.execute(graph.getDefaultParent())
        } catch (e) {
            throw e;
        } finally {
            model.endUpdate();
            if (post != null) { post(); }
        }
    }
    graphEnable () {
        const status = this.graph.isEnabled()
        this.graph.setEnabled(!status)
    }
    alignCenter = () => {
        this.executeLayout(null, () => {
            this.graph.center()
        }, 'west')
    }
    zoomIn = () => {
        this.graph.zoomIn()
    }
    zoomOut = () => {
        this.graph.zoomOut()
    }
    getStyles = () => { // 获取线条及轮廓样式
        return 'whiteSpace=wrap;fillColor=#ffffff'
    }
    getDefaultVertexStyle () {
        const {
            mxConstants,
            mxPerimeter
        } = this.Mx
        let style: any = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
        style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
        style[mxConstants.STYLE_STROKECOLOR] = 'none';
        style[mxConstants.STYLE_FILLCOLOR] = 'none';
        style[mxConstants.STYLE_FONTCOLOR] = '#333333';
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_FONTSIZE] = '12';
        style[mxConstants.STYLE_FONTSTYLE] = 1;
        style[mxConstants.STYLE_ARCSIZE] = 1;
        style[mxConstants.STYLE_ROUNDED] = true;
        return style;
    }
    getDefaultEdgeStyle () {
        const { mxConstants, mxEdgeStyle } = this.Mx
        let style: any = [];
        style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
        style[mxConstants.STYLE_STROKECOLOR] = '#95AFC7';
        style[mxConstants.STYLE_STROKEWIDTH] = 1;
        style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
        style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
        style[mxConstants.STYLE_EDGE] = mxEdgeStyle.EntityRelation;
        style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_BLOCK;
        style[mxConstants.STYLE_FONTSIZE] = '10';
        style[mxConstants.STYLE_ROUNDED] = true;
        style[mxConstants.STYLE_CURVED] = false;
        return style;
    }
}
