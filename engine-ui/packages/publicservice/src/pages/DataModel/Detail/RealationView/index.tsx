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

const mxgraph = mx({
  mxBasePath: '../../../../../node_modules/mxgraph/javascript/src',
});

const _columnList = [
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'id',
    columnType: 'varchar',
    columnComment: '默认主键',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'sort_filed',
    columnType: 'INTEGER',
    columnComment: '排序字段',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'name',
    columnType: 'varchar',
    columnComment: '用户名称',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'sex',
    columnType: 'INTEGER',
    columnComment: '性别',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'age',
    columnType: 'TINYINT',
    columnComment: '年龄',
    dimension: false,
    metric: true,
  },
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
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'phone',
    columnType: 'varchar',
    columnComment: '电话号码',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'education',
    columnType: 'varchar',
    columnComment: '学历水平',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'character',
    columnType: 'varchar',
    columnComment: '性格',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'income',
    columnType: 'varchar',
    columnComment: '收入',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'submit_city',
    columnType: 'varchar',
    columnComment: '城市',
    dimension: true,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'profession',
    columnType: 'varchar',
    columnComment: '职业',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'channel',
    columnType: 'varchar',
    columnComment: '渠道',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'activity_type',
    columnType: 'varchar',
    columnComment: '促销',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'book_id',
    columnType: 'varchar',
    columnComment: 'book_id',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_main',
    columnName: 'ds',
    columnType: 'varchar',
    columnComment: '',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_book',
    columnName: 'id',
    columnType: 'varchar',
    columnComment: '',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_book',
    columnName: 'sort_filed',
    columnType: 'INTEGER',
    columnComment: '排序字段',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_book',
    columnName: 'bk_name',
    columnType: 'varchar',
    columnComment: '',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_book',
    columnName: 'cate_lv4_id',
    columnType: 'varchar',
    columnComment: '',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_book',
    columnName: 'cate_full_name',
    columnType: 'varchar',
    columnComment: '',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_book',
    columnName: 'page_size',
    columnType: 'INTEGER',
    columnComment: '',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_book',
    columnName: 'price',
    columnType: 'INTEGER',
    columnComment: '',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_book',
    columnName: 'isbn',
    columnType: 'varchar',
    columnComment: '',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_book',
    columnName: 'auth_name',
    columnType: 'varchar',
    columnComment: '',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_book',
    columnName: 'store_id',
    columnType: 'varchar',
    columnComment: '',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_book',
    columnName: 'ds',
    columnType: 'varchar',
    columnComment: '',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_transaction',
    columnName: 'id',
    columnType: 'varchar',
    columnComment: '默认主键',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_transaction',
    columnName: 'user_id',
    columnType: 'varchar',
    columnComment: '用户id',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_transaction',
    columnName: 'transaction_count',
    columnType: 'INTEGER',
    columnComment: '交易数量',
    dimension: false,
    metric: true,
  },
  {
    schema: 'tag_engine',
    tableName: 'dl_user_transaction',
    columnName: 'ds',
    columnType: 'varchar',
    columnComment: '',
    dimension: false,
    metric: true,
  },
];

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
      const tableName = 'tableName';
      // 字段信息
      const columnList = _columnList;

      const cellWidth = 60;
      const cellHeight = 20;
      const parent = graph.getDefaultParent();
      const height = (columnList.length + 1) * cellHeight;
      const parentCell = graph.insertVertex(
        parent,
        null,
        '',
        0,
        0,
        cellWidth,
        height
      );
      const tableNameCell = graph.insertVertex(
        parentCell,
        null,
        tableName,
        0,
        0,
        cellWidth,
        cellHeight,
        'fillColor=orange'
      );
      tableNameCell.geometry.relative = true;
      _tableCellList.push(tableNameCell);
      columnList.forEach((column, index) => {
        const scrollTop = (index + 1) / columnList.length;
        const cell = graph.insertVertex(
          parentCell,
          null,
          column.columnName,
          0,
          scrollTop,
          cellWidth,
          cellHeight
        );
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

  useEffect(() => {
    if (!graph.current) return;
    refMo.current = new mxOutline(graph.current, refNavigatorContainer.current);
  }, []);

  const alignCenter = () => {
    graph.current.zoomActual();
    if (rootCell.current) {
      graph.current.scrollCellToVisible(rootCell, true);
    } else {
      graph.current.center();
    }
  };

  const zoomIn = () => {
    graph.current.zoomIn();
  };
  const zoomOut = () => {
    graph.current.zoomOut();
  };

  useEffect(() => {
    if (!visibleNavigator) return;
    refMo.current = new mxOutline(graph.current, refNavigatorContainer.current);
  }, [visibleNavigator]);

  const tools = useRef([
    {
      title: '居中',
      action: alignCenter,
      icon: 'iconOutlinedxianxing_juzhong',
    },
    { title: '放大', action: zoomIn, icon: 'iconOutlinedxianxing_zoom-in' },
    { title: '缩小', action: zoomOut, icon: 'iconOutlinedxianxing_zoom-in' },
    { title: '下载', action: () => {}, icon: 'iconOutlinedxianxing_xiazai' },
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

        {/* <div className="spread_bar">
            <i onClick={outLine} className={classnames('iconfont toolbar-icon iconnavigator', { active: true })}/>
            <div className={classnames('outlineContainer', { active: true })}>
                <div className="outline_title">
                    <div className="name">导航器</div>
                    <Icon onClick={outLine} className="double-right" type="double-right" />
                </div>
                <div className="outline-content" id="outline"></div>
            </div>
        </div> */}
      </div>
    </div>
  );
};

export default RelationView;
