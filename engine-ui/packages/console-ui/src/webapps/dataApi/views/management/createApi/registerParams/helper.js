import React from 'react';

import { Select, Popconfirm } from 'antd';

import { PARAMS_POSITION, PARAMS_POSITION_TEXT, FIELD_TYPE_LIST } from '../../../../consts';

const Option = Select.Option;

/**
 * 获取类型的select组件
 */
export function getTypeSelect () {
    return (
        <Select style={{ width: '100%' }}>
            {FIELD_TYPE_LIST.map((type) => {
                return <Option key={type} value={type}>{type}</Option>
            })}
        </Select>
    )
}
/**
 * 获取参数位置的select组件
 */
export function getPositionSelect (hideBody) {
    return (
        <Select style={{ width: '100%' }}>
            <Option key={PARAMS_POSITION.QUERY} value={PARAMS_POSITION.QUERY}>{PARAMS_POSITION_TEXT[PARAMS_POSITION.QUERY]}</Option>
            {!hideBody && (
                <Option key={PARAMS_POSITION.BODY} value={PARAMS_POSITION.BODY}>{PARAMS_POSITION_TEXT[PARAMS_POSITION.BODY]}</Option>
            )}
            <Option key={PARAMS_POSITION.HEAD} value={PARAMS_POSITION.HEAD}>{PARAMS_POSITION_TEXT[PARAMS_POSITION.HEAD]}</Option>
        </Select>
    )
}
export function getCommonDelete (props, record) {
    return <Popconfirm
        title="确认删除吗？"
        onConfirm={() => {
            props.deleteColumn(record.id);
        }}
        okText="删除"
        cancelText="取消">
        <a>删除</a>
    </Popconfirm>
}
const DELIMITER = '~'
/**
 * 生成一个formItem的唯一key
 * @param {*} itemName formItem名字
 * @param {*} id id
 * @param {*} type 输入输出参数
 */
export function generateFormItemKey (itemName, id) {
    return `${itemName}${DELIMITER}${id}`
}
/**
 * 解构key
 * @param {*} key formItemKey
 */
export function resolveFormItemKey (key) {
    let item = key.split(DELIMITER);
    return {
        name: item[0],
        id: item[1]
    }
}
/**
 * 动态表单通用方法
 */
export function mapPropsToFields (props) {
    const { data } = props;
    let formValues = {};
    /**
     * 遍历每一个属性，加上form属性映射
     */
    data.forEach((column) => {
        let { id, ...others } = column;
        let keyAndValue = Object.entries(others);
        keyAndValue.forEach(([key, value]) => {
            formValues[generateFormItemKey(key, id)] = {
                value
            }
        })
    })
    return formValues;
}
/**
 * 动态表单通用方法
 */
export function onValuesChange (props, values) {
    props.updateColumnData(values);
}

export const formItemParamOption = {
    rules: [
        {
            required: true,
            message: '请输入参数名称'
        },
        {
            pattern: /[-\w]*/,
            message: '参数名称只能由字母，数字和下划线组成'
        }
    ]
}
