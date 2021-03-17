export const relationListColumns = [
  {
    title: '表别名',
    dataIndex: 'tableAlias',
    key: 'tableAlias',
  },
  {
    title: '数据库',
    dataIndex: 'dsTypeName',
    key: 'dsTypeName',
  },
  {
    title: '表名',
    dataIndex: 'table',
    key: 'table',
  },
  {
    title: '关联类型',
    dataIndex: 'joinType',
    key: 'joinType',
  },
  {
    title: '关联条件',
    dataIndex: '',
    key: 'joinPairs',
    render: () => {
      return '--';
    }
  }
];

export const dimensionListColumns = [
  {
    title: '表',
    dataIndex: 'tableName',
    key: 'tableName',
  },
  {
    title: '字段名称',
    dataIndex: 'columnName',
    key: 'columnName',
  },
  {
    title: '字段别名',
    dataIndex: '',
    key: 'alias'
  },
  {
    title: '字段类型',
    dataIndex: 'columnType',
    key: 'columnType',
  }
];

export const metricListColumns = [
  {
    title: '表',
    dataIndex: 'tableName',
    key: 'tableName',
  },
  {
    title: '字段名称',
    dataIndex: 'columnName',
    key: 'columnName',
  },
  {
    title: '字段别名',
    dataInedx: '',
    key: 'alias',
  },
  {
    title: '字段类型',
    dataInedx: 'columnType',
    key: 'columnType',
  }
];
