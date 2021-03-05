// Tabs枚举值
export const TABS_TITLE_KEY = {
    COMMON: 0,
    SOURCE: 1,
    STORE: 2,
    COMPUTE: 3
}

export const TABS_POP_VISIBLE = {
    [TABS_TITLE_KEY.COMMON]: false,
    [TABS_TITLE_KEY.SOURCE]: false,
    [TABS_TITLE_KEY.STORE]: false,
    [TABS_TITLE_KEY.COMPUTE]: false
}

export const COMPONENT_TYPE_VALUE = {
    FLINK: 0,
    SPARK: 1,
    LEARNING: 2,
    DTYARNSHELL: 3,
    HDFS: 4,
    YARN: 5,
    SPARK_THRIFT_SERVER: 6,
    CARBONDATA: 7,
    LIBRA_SQL: 8,
    HIVE_SERVER: 9,
    SFTP: 10,
    IMPALA_SQL: 11,
    TIDB_SQL: 12,
    ORACLE_SQL: 13,
    GREEN_PLUM_SQL: 14,
    KUBERNETES: 15,
    PRESTO_SQL: 16,
    NFS: 17
}

export const COMPONENT_CONFIG_NAME = {
    0: 'Flink',
    1: 'Spark',
    2: 'Learning',
    3: 'DtScript',
    4: 'HDFS',
    5: 'YARN',
    6: 'SparkThrift',
    7: 'CarbonData ThriftServer',
    8: 'LibrA SQL',
    9: 'HiveServer',
    10: 'SFTP',
    11: 'Impala SQL',
    12: 'TiDB SQL',
    13: 'Oracle SQL',
    14: 'Greenplum SQL',
    15: 'Kubernetes',
    16: 'Presto SQL',
    17: 'NFS'
}

export const TABS_TITLE = {
    [TABS_TITLE_KEY.COMMON]: { iconName: 'iconcunchuzujian', name: '公共组件' },
    [TABS_TITLE_KEY.SOURCE]: { iconName: 'icongonggongzujian', name: '资源调度组件' },
    [TABS_TITLE_KEY.STORE]: { iconName: 'iconjisuanzujian', name: '存储组件' },
    [TABS_TITLE_KEY.COMPUTE]: { iconName: 'iconziyuantiaodu', name: '计算组件' }
}

export const CONFIG_BUTTON_TYPE = {
    [TABS_TITLE_KEY.COMMON]: [
        { code: COMPONENT_TYPE_VALUE.SFTP, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.SFTP] }
    ],
    [TABS_TITLE_KEY.SOURCE]: [
        { code: COMPONENT_TYPE_VALUE.YARN, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.YARN] },
        { code: COMPONENT_TYPE_VALUE.KUBERNETES, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.KUBERNETES] }
    ],
    [TABS_TITLE_KEY.STORE]: [
        { code: COMPONENT_TYPE_VALUE.HDFS, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.HDFS] },
        { code: COMPONENT_TYPE_VALUE.NFS, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.NFS] }
    ],
    [TABS_TITLE_KEY.COMPUTE]: [
        { code: COMPONENT_TYPE_VALUE.SPARK, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.SPARK] },
        { code: COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER] },
        { code: COMPONENT_TYPE_VALUE.FLINK, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.FLINK] },
        { code: COMPONENT_TYPE_VALUE.HIVE_SERVER, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.HIVE_SERVER] },
        { code: COMPONENT_TYPE_VALUE.IMPALA_SQL, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.IMPALA_SQL] },
        { code: COMPONENT_TYPE_VALUE.DTYARNSHELL, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.DTYARNSHELL] },
        { code: COMPONENT_TYPE_VALUE.LEARNING, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.LEARNING] },
        { code: COMPONENT_TYPE_VALUE.PRESTO_SQL, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.PRESTO_SQL] },
        { code: COMPONENT_TYPE_VALUE.TIDB_SQL, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.TIDB_SQL] },
        { code: COMPONENT_TYPE_VALUE.LIBRA_SQL, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.LIBRA_SQL] },
        { code: COMPONENT_TYPE_VALUE.ORACLE_SQL, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.ORACLE_SQL] },
        { code: COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL, componentName: COMPONENT_CONFIG_NAME[COMPONENT_TYPE_VALUE.GREEN_PLUM_SQL] }
    ]
}

export const VERSION_TYPE = {
    0: 'Flink',
    9: 'HiveServer',
    1: 'Spark',
    6: 'SparkThrift'
}

export const FILE_TYPE = {
    KERNEROS: 0,
    CONFIGS: 1,
    PARAMES: 2
}

export const CONFIG_ITEM_TYPE = {
    RADIO: 'RADIO',
    INPUT: 'INPUT',
    SELECT: 'SELECT',
    CHECKBOX: 'CHECKBOX',
    PASSWORD: 'PASSWORD',
    GROUP: 'GROUP',
    RADIO_LINKAGE: 'RADIO_LINKAGE',
    CUSTOM_CONTROL: 'CUSTOM_CONTROL'
}

export const DEFAULT_COMP_VERSION = {
    [COMPONENT_TYPE_VALUE.FLINK]: '180',
    [COMPONENT_TYPE_VALUE.SPARK]: '210',
    [COMPONENT_TYPE_VALUE.SPARK_THRIFT_SERVER]: '2.x',
    [COMPONENT_TYPE_VALUE.HIVE_SERVER]: '2.x'
}

export const CONFIG_FILE_DESC = {
    [COMPONENT_TYPE_VALUE.YARN]: 'zip格式，至少包括yarn-site.xml和core-site.xml',
    [COMPONENT_TYPE_VALUE.HDFS]: 'zip格式，至少包括core-site.xml、hdfs-site.xml、hive-site.xml',
    [COMPONENT_TYPE_VALUE.KUBERNETES]: 'zip格式，至少包括kubernetes.config'
}
