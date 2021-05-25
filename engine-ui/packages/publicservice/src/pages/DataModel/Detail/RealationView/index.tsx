import React, { useEffect, useRef, useState } from 'react';
import { Tooltip } from 'antd';
import mx from 'mxgraph';
import classnames from 'classnames';
declare const window: any;
window.mxLoadResources = false;
window.mxForceIncludes = false;
window.mxResourceExtension = false;
window.mxLoadStylesheets = false;
import './style';

enum EnumNodeType {
  TABLE_NAME = 'TABLE_NAME',
  COLUMN_NAME = 'COLUMN_NAME',
  PARTITION_COLUMN = 'PARTITION_COLUMN',
}

const mxgraph = mx({
  mxBasePath: '../../../../../node_modules/mxgraph/javascript/src',
});

const _columnList = [
  // {
  //   schema: 'tag_engine',
  //   tableName: 'dl_user_main',
  //   columnName: 'sex',
  //   columnType: 'INTEGER',
  //   columnComment: '性别',
  //   dimension: false,
  //   metric: true,
  // },
  // {
  //   schema: 'tag_engine',
  //   tableName: 'dl_user_main',
  //   columnName: 'age',
  //   columnType: 'TINYINT',
  //   columnComment: '年龄',
  //   dimension: false,
  //   metric: true,
  // },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'register_date',
    columnType: 'TIMESTAMP',
    columnComment: '注册时间',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'last_login_date',
    columnType: 'TIMESTAMP',
    columnComment: '上次登陆时间',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'total_money',
    columnType: 'INTEGER',
    columnComment: '消费额度',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'member_level',
    columnType: 'varchar',
    columnComment: '消费等级',
    dimension: false,
    metric: true,
  }
];

enum EnumToolActionType {
  ZOOM_IN = 'ZOOM_IN',
  ZOOM_OUT = 'ZOOM_OUT',
  DOWNLOAD = 'DOWNLOAD',
  ALIGN_CENTER = 'ALIGN_CENTEr',
}

interface IToolAction {
  type: EnumToolActionType,
  payload?: any;
}

const {
  mxGraph,
  mxClient,
  mxUtils,
  mxConstants,
  mxEdgeStyle,
  mxOutline,
  mxHierarchicalLayout,
} = mxgraph;

const RelationView = () => {
  const container = useRef(null);
  const graph = useRef(null);
  const rootCell = useRef(null);

  const getDefaultEdgeStyle = () => {
    let style: any = [];
    style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
    style[mxConstants.STYLE_STROKECOLOR] = '#3F87FF';
    style[mxConstants.STYLE_STROKEWIDTH] = 1;
    style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
    style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
    style[mxConstants.STYLE_EDGE] = mxEdgeStyle.EntityRelation;
    style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_BLOCK;
    style[mxConstants.STYLE_FONTSIZE] = '10';
    style[mxConstants.STYLE_ROUNDED] = true;
    style[mxConstants.STYLE_CURVED] = false;
    return style;
  };

  const adjustPos = () => {
    const _graph = graph.current;
    var bounds = _graph.getGraphBounds();
    var margin = margin || 10;
    _graph.container.style.overflow = 'hidden';
    _graph.view.setTranslate(
      -bounds.x - (bounds.width - _graph.container.clientWidth) / 2,
      -bounds.y - (bounds.height - _graph.container.clientHeight) / 2
    );
    while (
      bounds.width + margin * 2 > _graph.container.clientWidth ||
      bounds.height + margin * 2 > _graph.container.clientHeight
    ) {
      _graph.zoomOut();
      bounds = _graph.getGraphBounds();
    }
    _graph.container.style.overflow = 'auto';
  };

  const tableRender = (graph) => {
    return (data) => {
      console.log(data);
      const _tableCellList = [];
      // 表名
      const tableName = '<span stlye="color=red">tableName</span>';
      // 字段信息
      const columnList = _columnList;

      const cellWidth = 180;
      const cellHeight = 32;
      const parent = graph.getDefaultParent();
      const height = (columnList.length + 1) * cellHeight;
      const tableNameCellColor = '#3F87FF';
      const tableNameFontColor = '#FFFFFF';
      const colNameCellColor= '#FFFFFF';
      console.log(height, columnList.length)
      graph.htmlLabels = true; // label开启html支持

      graph.getLabel = (cell) => {
        console.log(cell);
        switch (cell.nodeType) {
          case EnumNodeType.COLUMN_NAME:
            return cell.value;
            return '<span style="position: absolute;top: 0;right: 80px;transform: translate(50%, 0);">'+ cell.value + '</span>';
          case EnumNodeType.PARTITION_COLUMN:
            return cell.value;
            return '<span style="position: absolute;top: 0;right: 80px;transform: translate(50%, 0);">' + cell.value + '</span>';
          case EnumNodeType.TABLE_NAME:
            return '<div style="position: absolute;top: 0;right: 80px;transform: translate(50%, 0);">' +
              '<span style="color: #ffffff" class="iconfont2 iconFilltianchong_biao"></span>' +
              '<span style="color: #ffffff; vertical-align: 2px; margin-left: 8px;">' + cell.value + '</span>' +
            '</div>'
        }
      }

      const parentCell = graph.insertVertex(
        parent,
        null,
        '',
        0,
        0,
        cellWidth,
        height,
        `strokeColor=${tableNameCellColor}`
      );
      const tableNameCell = graph.insertVertex(
        parentCell,
        null,
        tableName,
        0,
        0,
        cellWidth,
        cellHeight,
        `fillColor=${tableNameCellColor};fontColor=${tableNameFontColor};strokeColor=${tableNameCellColor}`
      );
      tableNameCell.nodeType = 'TABLE_NAME';
      tableNameCell.geometry.relative = true;
      _tableCellList.push(tableNameCell);
      columnList.forEach((column, index) => {
        const scrollTop = (index + 1) / (columnList.length + 1);
        const cell = graph.insertVertex(
          parentCell,
          null,
          column.columnName,
          0,
          scrollTop,
          cellWidth,
          cellHeight,
          `fillColor=${colNameCellColor};strokeColor=#E8E8E8`
        );
        cell.nodeType = 'COLUMN_NAME';
        cell.geometry.relative = true;
        _tableCellList.push(cell);
      });
    };
  };

  useEffect(() => {
    // 浏览器兼容性检测
    if (!mxClient.isBrowserSupported())
      return mxUtils.error('浏览器不支持mxgraph');

    graph.current = new mxGraph(container.current);
    const defaultEdgeStyle = getDefaultEdgeStyle();
    graph.current.getStylesheet().putDefaultEdgeStyle(defaultEdgeStyle);
    const model = graph.current.getModel();
    model.beginUpdate();
    rootCell.current = graph.current.getDefaultParent();
    try {
      graph.current.setEnabled(false);
      const render = tableRender(graph.current);
      render({});

      var layout = new mxHierarchicalLayout(
        graph.current,
        mxConstants.DIRECTION_WEST
      );
      layout.execute(graph.current.getDefaultParent());
      adjustPos();
    } catch (err) {
      throw err;
    } finally {
      model.endUpdate();
    }
  }, []);

  const refNavigatorContainer = useRef(null);
  const refMo = useRef(null);

  const [visibleNavigator, setVisibleNabigator] = useState(true);

  const handleToolAction = (action: IToolAction) => {
    switch (action.type) {
      case EnumToolActionType.ZOOM_IN:
        graph.current.zoomIn();
        break;
      case EnumToolActionType.ZOOM_OUT:
        graph.current.zoomOut();
        break;
      case EnumToolActionType.ALIGN_CENTER:
        graph.current.zoomActual();
        if (rootCell.current) {
          graph.current.scrollCellToVisible(rootCell, true);
        } else {
          graph.current.center();
        }
        break;
      case EnumToolActionType.DOWNLOAD:
        break;
    }
  }

  useEffect(() => {
    if (!visibleNavigator) return;
    refMo.current = new mxOutline(graph.current, refNavigatorContainer.current);
  }, [visibleNavigator]);

  const tools = useRef([
    {
      title: '居中',
      action: () => handleToolAction({ type: EnumToolActionType.ALIGN_CENTER }),
      icon: 'iconOutlinedxianxing_juzhong',
    },
    {
      title: '放大',
      action: () => handleToolAction({ type: EnumToolActionType.ZOOM_IN }),
      icon: 'iconOutlinedxianxing_zoom-in'
    },
    {
      title: '缩小',
      action: () => handleToolAction({ type: EnumToolActionType.ZOOM_OUT }),
      icon: 'iconOutlinedxianxing_zoom-in'
    },
    {
      itle: '下载',
      action: () => handleToolAction({ type: EnumToolActionType.DOWNLOAD }),
      icon: 'iconOutlinedxianxing_xiazai'
    },
  ]);

  return (
    <div className="relation-view">
      <div className="graph-content">
        <div ref={container} className="graph-view"></div>
        <div className="graph-legend"></div>
      </div>
      <div className="graph-toolbar">
        <div className="basic-bar">
          {tools.current.map((item) => (
            <div className="tool-item">
              <Tooltip placement="left" title={item.title}>
                <i
                  className={`icon iconfont2 ${item.icon}`}
                  onClick={item.action}
                />
              </Tooltip>
            </div>
          ))}
        </div>
        <div className="nav-bar">
          <i
            className={classnames({
              icon: true,
              iconfont2: true,
              iconOutlinedxianxing_daohangqi: true,
              active: visibleNavigator,
            })}
            onClick={() => {
              setVisibleNabigator(true);
            }}
          />
        </div>
        {visibleNavigator ? (
          <div className="nav-content">
            <div className="nav-content-header">
              <span className="title">导航器</span>
              <span
                className="icon iconfont2 float-right iconOutlinedxianxing_shuangjiantou"
                onClick={() => {
                  setVisibleNabigator(false);
                }}
              />
            </div>
            <div className="nav-content-body" ref={refNavigatorContainer} />
          </div>
        ) : null}
      </div>
    </div>
  );
};

export default RelationView;
