import React, { useRef } from 'react';
import GraphEditor from './GraphEditor';

import './style';
import _ from 'highlight.js/lib/languages/*';

enum EnumNodeType {
  TABLE_NAME = 'TABLE_NAME',
  COLUMN_NAME = 'COLUMN_NAME',
  PARTITION_COLUMN = 'PARTITION_COLUMN',
}

const tree = {
  tableName: 'aaaaaaaaaaa',
  joinPairs: null,
  columns: [
    {
      schema: 'tag_engine',
      tableName: 'dl_user_main',
      columnName: 'aaa',
      columnType: 'INTEGER',
      columnComment: '消费额度',
      dimension: false,
      metric: true,
    },
    {
      schema: 'tag_engine',
      tableName: 'dl_user_main',
      columnName: 'bbb',
      columnType: 'varchar',
      columnComment: '消费等级',
      dimension: false,
      metric: true,
    },
  ],
  children: [
    {
      tableName: 'b',
      joinPairs: [
        {
          leftValue: { tableName: 'aaaaaaaaaaa', columnName: 'aaa' },
          rightValue: { tableName: 'b', columnName: 'aaa' },
        },
        {
          leftValue: { columnName: 'bbb', tableName: 'aaaaaaaaaaa' },
          rightValue: { columnName: 'bbb', tableName: 'b' },
        },
      ],
      columns: [
        {
          schema: 'tag_engine',
          tableName: 'dl_user_main',
          columnName: 'aaa',
          columnType: 'INTEGER',
          columnComment: '消费额度',
          dimension: false,
          metric: true,
        },
        {
          schema: 'tag_engine',
          tableName: 'dl_user_main',
          columnName: 'bbb',
          columnType: 'varchar',
          columnComment: '消费等级',
          dimension: false,
          metric: true,
        },
      ],
    },
    {
      tableName: 'c',
      joinPairs: [
        {
          leftValue: { tableName: 'aaaaaaaaaaa', columnName: 'aaa' },
          rightValue: { tableName: 'c', columnName: 'aaa' },
        },
      ],
      columns: [
        {
          schema: 'tag_engine',
          tableName: 'dl_user_main',
          columnName: 'aaa',
          columnType: 'INTEGER',
          columnComment: '消费额度',
          dimension: false,
          metric: true,
        },
        {
          schema: 'tag_engine',
          tableName: 'dl_user_main',
          columnName: 'bbb',
          columnType: 'varchar',
          columnComment: '消费等级',
          dimension: false,
          metric: true,
        },
      ],
    },
  ],
};

const loop = (tree, cb?) => {
  const stack = [];
  stack.push(tree);
  while (stack.length > 0) {
    const parent = stack.pop();
    if (typeof cb === 'function') {
      cb(parent);
    }
    if (parent.children && Array.isArray(parent.children)) {
      parent.children.forEach((item) => {
        stack.push(item);
      });
    }
  }
};

const RelationView = () => {
  const graph = useRef(null);
  const rootCell = useRef(null);
  const refGraphEditor = useRef(null);

  const tableRender = (graph) => {
    return (
      data: { tableName: string; columnList: any[] },
      position: { x?: number; y?: number } = {}
    ) => {
      const { tableName, columnList } = data;
      const { x = 0, y = 0 } = position;
      const _tableCellList = [];
      const cellWidth = 180;
      const cellHeight = 32;
      const parent = graph.getDefaultParent();
      const height = (columnList.length + 1) * cellHeight;
      const tableNameCellColor = '#3F87FF';
      const tableNameFontColor = '#FFFFFF';
      const colNameCellColor = '#FFFFFF';

      const parentCell = graph.insertVertex(
        parent,
        null,
        '',
        x,
        y,
        cellWidth,
        height,
        `strokeColor=${tableNameCellColor};`
      );
      const tableNameCell = graph.insertVertex(
        parentCell,
        null,
        tableName,
        0,
        0,
        cellWidth,
        cellHeight,
        `fillColor=${tableNameCellColor};fontColor=${tableNameFontColor};strokeColor=${tableNameCellColor};align=left;`
      );
      tableNameCell.nodeType = EnumNodeType.TABLE_NAME;
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
          `fillColor=${colNameCellColor};strokeColor=#E8E8E8;align=left;`
        );
        cell.nodeType = EnumNodeType.COLUMN_NAME;
        cell.geometry.relative = true;
        _tableCellList.push(cell);
      });
      const wrapper = graph.insertVertex(
        parentCell,
        null,
        null,
        0,
        0,
        cellWidth,
        height,
        `strokeColor=${tableNameCellColor}`
      );
      wrapper.geometry.relative = true;

      return _tableCellList;
    };
  };

  const getLabel = (cell) => {
    // TODO: 逻辑待补充完善
    if (cell.edge === true) {
      return '<div style="background: #ffffff;">' + cell.value + '</div>';
    }
    switch (cell.nodeType) {
      case EnumNodeType.COLUMN_NAME:
        return '<div class="margin-left-12">' + cell.value + '</div>';
      case EnumNodeType.PARTITION_COLUMN:
        return '<div class="margin-left-12">' + cell.value + '</div>';
      case EnumNodeType.TABLE_NAME:
        return (
          '<div class="margin-left-12">' +
          '<span class="iconfont2 iconFilltianchong_biao"></span>' +
          '<span style="vertical-align: 2px; margin-left: 8px;">' +
          cell.value +
          '</span>' +
          '</div>'
        );
      default:
        return cell.value;
    }
  };

  const insertEdge = (parent: any, label: string, source: any, target: any) => {
    graph.current.insertEdge(parent, null, label, source, target);
  };

  const executeLayout = (parent: any, Layout: any, option: any = {}) => {
    const _layout = new Layout(graph.current);
    Object.keys(option).forEach((key) => {
      _layout[key] = option[key];
    });
    _layout.execute(parent);
  };

  const handleInit = (_graph, mx) => {
    graph.current = _graph;
    graph.current.getLabel = getLabel;
    const model = graph.current.getModel();
    model.beginUpdate();
    graph.current.labelsVisible = true;

    try {
      rootCell.current = graph.current.getDefaultParent();
      const render = tableRender(graph.current);

      const map = new Map();

      loop(tree, (item) => {
        const list = render({
          tableName: item.tableName,
          columnList: item.columns,
        });
        map.set(item.tableName, list);
        // render relation line
        if (item.joinPairs) {
          item.joinPairs.map((joinInfo) => {
            const leftTableColumnList = map.get(joinInfo.leftValue.tableName);
            const rightTableColumnList = map.get(joinInfo.rightValue.tableName);
            const cellLeftCol = leftTableColumnList.find(
              (item) => item.value === joinInfo.leftValue.columnName
            );
            const cellRightcol = rightTableColumnList.find(
              (item) => item.value === joinInfo.rightValue.columnName
            );
            insertEdge(rootCell.current, 'left', cellLeftCol, cellRightcol);
          });
        }
      });

      const { mxHierarchicalLayout } = mx;

      executeLayout(graph.current.getDefaultParent(), mxHierarchicalLayout, {
        orientation: 'west',
        disableEdgeStyle: false,
        interRankCellSpacing: 200,
        intraCellSpacing: 80,
      });
    } catch (err) {
      throw err;
    } finally {
      model.endUpdate();
    }
  };

  return (
    <div className="relation-view">
      <GraphEditor
        ref={(ref) => (refGraphEditor.current = ref)}
        rootCell={rootCell.current}
        loading={false}
        name="name"
        onInit={handleInit}
      />
    </div>
  );
};

export default RelationView;
