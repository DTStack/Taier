/* eslint-disable new-cap */
import * as React from 'react';
import { Tooltip, Spin, Icon } from 'antd';
import classnames from 'classnames';
import html2canvas from 'html2canvas';
import mx from 'mxgraph';
import './style.scss';

declare const window: any;
window.mxLoadResources = false;
window.mxForceIncludes = false;
window.mxResourceExtension = false;
window.mxLoadStylesheets = false;
window.mxBasePath = '/assets/libs/mxgraph';

const mxgraph = mx();

enum EnumToolActionType {
  ZOOM_IN = 'ZOOM_IN',
  ZOOM_OUT = 'ZOOM_OUT',
  DOWNLOAD = 'DOWNLOAD',
  ALIGN_CENTER = 'ALIGN_CENTEr',
}

interface IToolAction {
  type: EnumToolActionType;
  payload?: any;
}
interface Props {
  loading?: boolean;
  name: string;
  rootCell?: any;
  showMenu?: boolean;
  hideMenu?: any;
  onInit?: (graph: any, Mx: any) => void;
}
interface State {
  outLineVisible: boolean;
}

const legendList = [
  { color: '#3F87FF', title: '主表', border: false },
  { color: '#FFB310', title: '关联表', border: false },
  { color: '#E8E8E8', title: '非模型字段（仅作为关联键）', border: true },
];

export default class GraphEditor extends React.Component<Props, State> {
  state: State = {
    outLineVisible: false,
  };
  Mx = mxgraph;
  Container: any;
  graph: any;
  outln: any;
  tools: any[] = [
    {
      title: '居中',
      action: () =>
        this.handleToolAction({ type: EnumToolActionType.ALIGN_CENTER }),
      icon: 'iconOutlinedxianxing_juzhong',
    },
    {
      title: '放大',
      action: () => this.handleToolAction({ type: EnumToolActionType.ZOOM_IN }),
      icon: 'iconOutlinedxianxing_zoom-in',
    },
    {
      title: '缩小',
      action: () =>
        this.handleToolAction({ type: EnumToolActionType.ZOOM_OUT }),
      icon: 'iconOutlinedxianxing_zoom-in',
    },
    {
      title: '下载',
      action: () =>
        this.handleToolAction({ type: EnumToolActionType.DOWNLOAD }),
      icon: 'iconOutlinedxianxing_xiazai',
    },
  ];
  componentDidMount() {
    this.Container.innerHTML = ''; // 清理容器内的Dom元素
    this.graph = '';
    const editor = this.Container;
    this.loadEditor(editor);
    this.outLine();
  }

  handleToolAction = (toolAction: IToolAction) => {
    switch (toolAction.type) {
      case EnumToolActionType.ALIGN_CENTER:
        this.alignCenter();
        break;
      case EnumToolActionType.ZOOM_IN:
        this.graph.zoomIn();
        break;
      case EnumToolActionType.ZOOM_OUT:
        this.graph.zoomOut();
        break;
      case EnumToolActionType.DOWNLOAD:
        this.downLoadImage();
        break;
    }
  };
  render() {
    const { outLineVisible } = this.state;
    const { loading } = this.props;

    return (
      <div className="graph-editor">
        {loading && <Spin spinning={loading} className="graph_loading"></Spin>}
        <div
          className="graph_container"
          ref={(e: any) => {
            this.Container = e;
          }}
        />
        <div className="graph_toolbar">
          <div className="basic_bar">
            {this.tools.map((item) => (
              <Tooltip placement="left" title={item.title}>
                <i
                  onClick={item.action}
                  className={`iconfont2 toolbar-icon ${item.icon}`}
                />
              </Tooltip>
            ))}
          </div>
          <div className="spread_bar">
            <i
              onClick={this.outLine}
              className={classnames({
                active: outLineVisible,
                iconfont2: true,
                'toolbar-icon': true,
                iconOutlinedxianxing_daohangqi: true,
              })}
            />
            <div
              className={classnames('outlineContainer', {
                active: outLineVisible,
              })}>
              <div className="outline_title">
                <div className="name">导航器</div>
                <Icon
                  onClick={this.outLine}
                  className="double-right"
                  type="double-right"
                />
              </div>
              <div className="outline-content" id="outline"></div>
            </div>
          </div>
        </div>
        <div className="graph-legend">
          {legendList.map((item, index) => (
            <div key={index} className="legend-item">
              <span
                className={classnames({
                  'legend-item-icon': true,
                  border: item.border,
                })}
                style={{ background: item.color }}
              />
              <span className="legend-title">{item.title}</span>
            </div>
          ))}
        </div>
      </div>
    );
  }

  loadEditor = (container: any) => {
    const { onInit, showMenu = false } = this.props;
    const { mxGraph, mxEvent, mxRubberband, mxConstants } = this.Mx;
    // mxClient.NO_FO = true;

    mxEvent.disableContextMenu(container);
    const graph = new mxGraph(container); // eslint-disable-line
    this.graph = graph;
    // 启用绘制
    graph.setPanning(true);
    graph.keepEdgesInBackground = true;
    // 允许鼠标移动画布
    graph.panningHandler.useLeftButtonForPanning = true;
    graph.setCellsMovable(false);
    graph.setEnabled(showMenu); // 设置启用,就是允不允许你改变CELL的形状内容。
    graph.setConnectable(false); // 是否允许Cells通过其中部的连接点新建连接,false则通过连接线连接
    graph.setCellsResizable(false); // 禁止改变元素大小
    graph.setAutoSizeCells(false);
    graph.centerZoom = true;
    graph.setTooltips(false);
    graph.view.setScale(1);
    // Enables HTML labels
    graph.setHtmlLabels(true);
    graph.setAllowDanglingEdges(false);
    // 禁止Edge对象移动
    graph.isCellsMovable = function () {
      return false;
    };
    // 禁止cell编辑
    graph.isCellEditable = function () {
      return false;
    };
    // 设置Vertex样式
    const vertexStyle = this.getDefaultVertexStyle();
    graph.getStylesheet().putDefaultVertexStyle(vertexStyle);

    // 默认边界样式
    let edgeStyle = this.getDefaultEdgeStyle();
    graph.getStylesheet().putDefaultEdgeStyle(edgeStyle);
    // anchor styles
    mxConstants.HANDLE_FILLCOLOR = '#ffffff';
    mxConstants.HANDLE_STROKECOLOR = 'transparent';
    mxConstants.VERTEX_SELECTION_COLOR = 'transparent';
    mxConstants.CURSOR_MOVABLE_VERTEX = 'pointer';
    // 重置tooltip
    // enables rubberband
    this.initContainerScroll();
    new mxRubberband(graph); // eslint-disable-line
    onInit && onInit(graph, this.Mx);
    if (showMenu) {
      this.initContextMenu();
      this.addEventListenerMenu();
    }
  };
  initContainerScroll = () => {
    // 滚动监听，一般为默认，不需要更改
    const { mxRectangle, mxPoint, mxUtils } = this.Mx;
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
      return new mxPoint(
        Math.max(0, Math.round(graph.container.offsetWidth - 40)),
        Math.max(0, Math.round(graph.container.offsetHeight - 40))
      );
    };

    /**
     * Returns the size of the page format scaled with the page size.
     */
    graph.getPageSize = function () {
      // eslint-disable-next-line new-cap
      return this.pageVisible
        ? new mxRectangle(
            0,
            0,
            this.pageFormat.width * this.pageScale,
            this.pageFormat.height * this.pageScale
          )
        : this.scrollTileSize;
    };

    /**
     * Returns a rectangle describing the position and count of the
     * background pages, where x and y are the position of the top,
     * left page and width and height are the vertical and horizontal
     * page count.
     */
    graph.getPageLayout = function () {
      var size = this.pageVisible ? this.getPageSize() : this.scrollTileSize;
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
      return new mxRectangle(
        this.scale * (this.translate.x + layout.x * page.width),
        this.scale * (this.translate.y + layout.y * page.height),
        this.scale * layout.width * page.width,
        this.scale * layout.height * page.height
      );
    };

    graph.getPreferredPageSize = function (
      bounds: any,
      width: any,
      height: any
    ) {
      var pages = this.getPageLayout();
      var size = this.getPageSize();

      // eslint-disable-next-line new-cap
      return new mxRectangle(
        0,
        0,
        pages.width * size.width,
        pages.height * size.height
      );
    };

    /**
     * Guesses autoTranslate to avoid another repaint (see below).
     * Works if only the scale of the graph changes or if pages
     * are visible and the visible pages do not change.
     */
    var graphViewValidate = graph.view.validate;
    graph.view.validate = function () {
      if (
        this.graph.container != null &&
        mxUtils.hasScrollbars(this.graph.container)
      ) {
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
        var minw = Math.ceil(
          (2 * pad.x) / this.view.scale + pages.width * size.width
        );
        var minh = Math.ceil(
          (2 * pad.y) / this.view.scale + pages.height * size.height
        );

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

        if (
          !this.autoTranslate &&
          (this.view.translate.x != dx || this.view.translate.y != dy)
        ) {
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
  };
  initContextMenu = () => {
    const { mxPopupMenu } = this.Mx;
    const graph = this.graph;
    var mxPopupMenuShowMenu = mxPopupMenu.prototype.showMenu;

    mxPopupMenu.prototype.showMenu = function () {
      var cells = this.graph.getSelectionCells();
      if (cells.length > 0 && cells[0].vertex) {
        let mxPopupMenus = document.querySelectorAll('.customMenu');
        mxPopupMenus.forEach((item) => item.remove());
        mxPopupMenuShowMenu.apply(this, arguments);
      } else return false;
    };
    graph.popupMenuHandler.autoExpand = true;
  };
  componentWillUnmount() {
    const { showMenu } = this.props;
    if (showMenu) {
      this.hideMenu();
      document.removeEventListener('click', this.hideMenu);
    }
  }
  addEventListenerMenu = () => {
    document.addEventListener('click', this.hideMenu);
  };
  hideMenu = (e?: any) => {
    const graph = this.graph;
    this.props.hideMenu && this.props.hideMenu(false);
    if (graph.popupMenuHandler.isMenuShowing()) {
      graph.popupMenuHandler.hideMenu();
    }
  };
  executeLayout = (change: any, post: any) => {
    // 更新布局
    const { mxHierarchicalLayout } = this.Mx;
    const graph = this.graph;
    const model = graph.getModel();
    model.beginUpdate();
    try {
      const layout = new mxHierarchicalLayout(graph); // eslint-disable-line
      layout.orientation = 'west';
      layout.disableEdgeStyle = false;
      layout.interRankCellSpacing = 60;
      layout.intraCellSpacing = 80;

      if (change != null) {
        change();
      }
      layout.execute(graph.getDefaultParent());
    } catch (e) {
      throw e;
    } finally {
      model.endUpdate();
      if (post != null) {
        post();
      }
    }
  };
  graphEnable() {
    const status = this.graph.isEnabled();
    this.graph.setEnabled(!status);
  }
  alignCenter = () => {
    const { rootCell } = this.props;
    this.graph.zoomActual();
    if (rootCell) {
      this.graph.scrollCellToVisible(rootCell, true);
    } else {
      this.graph.center();
    }
  };
  setEdgeHighlight = (hover) => {
    // 设置连接线高亮
    const { mxConstants } = this.Mx;
    const cells = this.graph.view.getCellStates().map;
    for (let key in cells) {
      let state = cells[key];
      if (state.cell.edge) {
        let styles = state.style;
        if (hover) {
          styles[mxConstants.STYLE_STROKEWIDTH] = 3;
        } else {
          styles[mxConstants.STYLE_STROKEWIDTH] = 1;
        }
        state.style = styles;
        state.shape.apply(state);
        state.shape.redraw();
        if (state.text != null) {
          state.text.apply(state);
          state.text.redraw();
        }
      }
    }
  };
  downLoadImage = () => {
    const { name } = this.props;
    let image = document.querySelector('.graph_container svg');
    let imageAttarbute = image.getBoundingClientRect();
    this.html2canvas(
      this.Container,
      name,
      imageAttarbute.width,
      imageAttarbute.height * 1.2
    );
  };
  html2canvas = (node, name, width, height, type = 'png') => {
    const newCloneDom = node.cloneNode(true);
    newCloneDom.style.transform = 'scale(1.0)';
    newCloneDom.style.position = 'absolute';
    newCloneDom.style.left = '0';
    newCloneDom.style.top = '0';
    newCloneDom.style.zIndex = '-9999';
    newCloneDom.style.padding = '40px';
    newCloneDom.style.width = width + 'px';
    newCloneDom.style.height = height + 'px';
    newCloneDom.style.background =
      'url(/dataAssets/public/img/grid.gif) #ffffff';
    document.body.appendChild(newCloneDom);
    html2canvas(newCloneDom, {
      allowTaint: true, // 允许跨域图片
      scale: 1,
      width: width,
      height: height,
      imageTimeout: 0,
    })
      .then((canvas) => {
        let image = canvas.toDataURL('image/' + type, 1.0);
        const alink = document.createElement('a');
        alink.href = image;
        alink.download = name;
        alink.click();
        document.body.removeChild(newCloneDom);
      })
      .catch(() => {
        document.body.removeChild(newCloneDom);
      });
  };
  outLine = () => {
    // 显示导航区
    const { mxOutline } = this.Mx;
    this.setState(
      {
        outLineVisible: !this.state.outLineVisible,
      },
      () => {
        if (!this.outln) {
          // 导航器
          var outline = document.getElementById('outline');
          // eslint-disable-next-line new-cap
          this.outln = new mxOutline(this.graph, outline);
        }
      }
    );
  };
  getStyles = (data: any) => {
    // 获取线条及轮廓样式
    if (data.isParent) {
      return 'whiteSpace=wrap;fillColor=#ffffff;strokeColor=#26D6AE;';
    } else if (data.isRoot) {
      return 'whiteSpace=wrap;fillColor=#ffffff;strokeColor=#3F87FF;';
    } else if (data.isChild) {
      return 'whiteSpace=wrap;fillColor=#ffffff;strokeColor=#7460EF;';
    }
  };
  getDefaultVertexStyle() {
    const { mxConstants, mxPerimeter } = this.Mx;
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
  getDefaultEdgeStyle() {
    const { mxConstants, mxEdgeStyle } = this.Mx;
    let style: any = [];
    style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
    style[mxConstants.STYLE_STROKECOLOR] = '#3F87FF';
    style[mxConstants.STYLE_STROKEWIDTH] = 1;
    style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
    style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
    style[mxConstants.STYLE_EDGE] = mxEdgeStyle.SideToSide;
    style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_BLOCK;
    style[mxConstants.STYLE_FONTSIZE] = '10';
    style[mxConstants.STYLE_ROUNDED] = true;
    style[mxConstants.STYLE_CURVED] = false;

    style[mxConstants.STYLE_ENTRY_X] = 0;
    style[mxConstants.STYLE_ENTRY_Y] = 0.5;
    style[mxConstants.STYLE_EXIT_Y] = 0.5;
    style[mxConstants.STYLE_EXIT_X] = 1;
    return style;
  }
}
