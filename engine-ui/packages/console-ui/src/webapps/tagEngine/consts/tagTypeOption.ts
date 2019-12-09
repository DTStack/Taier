const tagTypeOption = {
    CHARACTER: [{ label: '等于', value: 'OP_EQUAL' }, { label: '不等于', value: 'OP_NOT_EQUAL' }, { label: '包含', value: 'OP_CONTAIN' }, { label: '不包含', value: 'OP_NOT_CONTAIN' }, { label: '有值', value: 'OP_HAVE' }, { label: '无值', value: 'OP_NOT' }],
    TIME: [{ label: '绝对时间', value: 'OP_ABSOLUTE_TIME' }, { label: '相对当前时间点', value: 'OP_RELATIVE_TIME' }, { label: '相对当前时间区间', value: 'OP_WITH_IN_BETWEEN' }, { label: '有值', value: 'OP_HAVE' }, { label: '无值', value: 'OP_NOT' }],
    OP_ABSOLUTE_TIME: [{ label: '等于', value: 'OP_EQUAL' }, { label: '不等于', value: 'OP_NOT_EQUAL' }, { label: '小于', value: 'OP_LESS_THAN' }, { label: '小于等于', value: 'OP_LESS_THAN_EQUAL' }, { label: '大于', value: 'OP_GREATER_THAN' }, { label: '大于等于', value: 'OP_GREATER_THAN_EQUAL' }, { label: '区间', value: 'OP_BETWEEN' }],
    OP_RELATIVE_TIME: [{ label: '之内', value: 'OP_WITH_IN' }, { label: '之前', value: 'OP_WITH_BEFORE' }],
    NUMBER: [{ label: '等于', value: 'OP_EQUAL' }, { label: '不等于', value: 'OP_NOT_EQUAL' }, { label: '小于', value: 'OP_LESS_THAN' }, { label: '小于等于', value: 'OP_LESS_THAN_EQUAL' }, { label: '大于', value: 'OP_GREATER_THAN' }, { label: '大于等于', value: 'OP_GREATER_THAN_EQUAL' }, { label: '区间', value: 'OP_BETWEEN' }, { label: '有值', value: 'OP_HAVE' }, { label: '无值', value: 'OP_NOT' }]
}
export default tagTypeOption;
