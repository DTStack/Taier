import React from 'react';
import { TASK_TYPE } from '../../comm/const';

export function TaskType(props: any) {
    const value = props.value;
    switch (value) {
        case TASK_TYPE.HIVESQL:
            return <span>HiveSQL</span>;
        case TASK_TYPE.CUBE_KYLIN:
            return <span>Kylin</span>;
        case TASK_TYPE.VIRTUAL_NODE:
            return <span>虚节点</span>;
        case TASK_TYPE.MR:
            return <span>Spark</span>;
        case TASK_TYPE.SYNC:
            return <span>数据同步</span>;
        case TASK_TYPE.PYTHON:
            return <span>PySpark</span>;
        case TASK_TYPE.DEEP_LEARNING:
            return <span>深度学习</span>;
        case TASK_TYPE.PYTHON_23:
            return <span>Python</span>;
        case TASK_TYPE.SHELL:
            return <span>Shell</span>;
        case TASK_TYPE.ML:
            return <span>机器学习</span>;
        case TASK_TYPE.HAHDOOPMR:
            return <span>HadoopMR</span>;
        case TASK_TYPE.WORKFLOW:
            return <span>工作流</span>;
        case TASK_TYPE.SQL:
            return <span>SparkSQL</span>;
        case TASK_TYPE.CARBONSQL:
            return <span>CarbonSQL</span>;
        case TASK_TYPE.NOTEBOOK:
            return <span>Notebook</span>;
        case TASK_TYPE.EXPERIMENT:
            return <span>算法实验</span>;
        case TASK_TYPE.LIBRASQL:
            return <span>LibraSQL</span>;
        case TASK_TYPE.TI_DB_SQL:
            return <span>TiDBSQL</span>;
        case TASK_TYPE.ORACLE_SQL:
            return <span>OracleSQL</span>;
        case TASK_TYPE.IMPALA_SQL:
            return <span>ImpalaSQL</span>;
        case TASK_TYPE.GREEN_PLUM_SQL:
            return <span>GreenPlumSQL</span>;
        default:
            return <span>SparkSQL</span>;
    }
}
