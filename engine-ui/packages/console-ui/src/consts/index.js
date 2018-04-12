export const defaultEditorOptions = { // 编辑器选项
    mode: 'text/x-sql',
    lint: true,
    indentWithTabs: true,
    smartIndent: true,
    lineNumbers: true,
    autofocus: false,
    // extraKeys: { 'Ctrl-Space': 'autocomplete' },
}

/** 
 * 所有应用的唯一ID
*/
export const MY_APPS = {
    MAIN: 'main',
    RDOS: 'rdos',
    DATA_QUALITY: 'dataQuality',
    API: 'dataApi',
    LABEL: 'label',
    DATA_MAP: 'map',
    META_DATA: 'metaData',
}

/**
 * 项目角色
 */
export const PROJECT_ROLE = { // 项目角色
    PROJECT_OWNER: 2, // 项目所有者
    TENANT_OWVER: 1, // 租户所有者
    VISITOR: 4, // 访客
}

export const formItemLayout = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
    },
}

export const tailFormItemLayout = { // 表单末尾布局
    wrapperCol: {
        xs: {
            span: 24,
            offset: 0,
        },
        sm: {
            span: 14,
            offset: 6,
        },
    },
}
