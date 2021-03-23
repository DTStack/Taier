import { JoinType } from '@/pages/DataModel/types';
export const joinTypeList = [
  { key: JoinType.LEFT_JOIN, label: 'left join' },
  { key: JoinType.RIGHT_JOIN, label: 'right join' },
  { key: JoinType.INNER_JOIN, label: 'inner join' },
]

export const updateTypeList = [
  { key: 1, label: '全量更新' },
  { key: 2, label: '增量更新' },
]
