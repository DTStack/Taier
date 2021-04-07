import { JoinType } from '../../types';
import { holder } from '../constants';

export const relationListColumns = [
  {
    title: '表别名',
    dataIndex: 'tableAlias',
    key: 'tableAlias',
    render: holder,
    width: 200,
  },
  {
    title: '数据库',
    dataIndex: 'schema',
    key: 'schema',
    render: holder,
    width: 200,
  },
  {
    title: '表名',
    dataIndex: 'table',
    key: 'table',
    render: holder,
    width: 200,
  },
  {
    title: '关联类型',
    dataIndex: 'joinType',
    key: 'joinType',
    width: 160,
    render: (type) => {
      switch (type) {
        case JoinType.LEFT_JOIN:
          return 'Left Join';
        case JoinType.RIGHT_JOIN:
          return 'Right Join';
        case JoinType.INNER_JOIN:
          return 'Inner Join';
        default:
          return 'error type';
      }
    },
  },
  {
    title: '关联条件',
    dataIndex: 'joinPairs',
    key: 'joinPairs',
    width: 300,
    render: (joinPairs) => {
      return joinPairs
        .reduce((temp, cur) => {
          // TODO: 逻辑重复，可优化
          const ltTable = cur.leftValue.tableName;
          const ltCol = cur.leftValue.columnName;
          const rtTable = cur.rightValue.tableName;
          const rtCol = cur.rightValue.columnName;
          return `${temp}${ltTable}.${ltCol} = ${rtTable}.${rtCol} and `;
        }, '')
        .replace(/ and $/, '');
    },
  },
];

export const columns = [
  {
    title: '表',
    dataIndex: 'tableName',
    key: 'tableName',
    render: holder,
    width: 200,
  },
  {
    title: '字段名称',
    dataIndex: 'columnName',
    key: 'columnName',
    render: holder,
    width: 200,
  },
  {
    title: '字段描述',
    dataIndex: 'columnComment',
    key: 'columnComment',
    render: holder,
    width: 200,
  },
  {
    title: '字段类型',
    dataIndex: 'columnType',
    key: 'columnType',
    render: holder,
    width: 200,
  },
];
