/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */

// @ts-check

/** @type {import('@docusaurus/plugin-content-docs').SidebarsConfig} */
const sidebars = {
    docs: [
        {
            type: 'category',
            label: '关于 Taier',
            collapsed: true,
            items: [
                'guides/introduction',
                'guides/explain',
                'guides/taier-architecture',
            ],
        },
        {
            type: 'category',
            label: '快速开始',
            collapsed: false,
            items: [
                'quickstart/rely',
                 {
                     type: 'category',
                     collapsed: false,
                     label: '快速部署',
                     items: [
                         'quickstart/deploy/deployment-quick',
                         'quickstart/deploy/docker',
                         'quickstart/deploy/cluster-deploy',
                     ],
                 },
                'quickstart/build',
                'quickstart/idea',
                'quickstart/start',
                'quickstart/precautions',
            ],
        },
        {
            type: 'category',
            label: '功能介绍',
            collapsed: false,
            items: [
                'functions/multi-cluster',
                {
                    type: 'category',
                    collapsed: true,
                    label: '组件配置',
                    items: [
                        'functions/component/sftp',
                        'functions/component/yarn',
                        'functions/component/hdfs',
                        'functions/component/flink-on-yarn',
                        'functions/component/flink-on-standalone',
                        'functions/component/script-on-yarn',
                        'functions/component/script-on-standalone',
                        'functions/component/spark',
                        'functions/component/datax',
                    ],
                 },
                'functions/datasource',
                {
                    type: 'category',
                    collapsed: true,
                    label: '任务类型',
                    items: [
                        'functions/task/sync',
                        'functions/task/data-acquisition',
                        'functions/task/python',
                        'functions/task/shell',
                        'functions/task/workflow',
                        'functions/task/virtual',
                        'functions/task/flink',
                        'functions/task/flink-sql',
                        'functions/task/spark-sql',
                        'functions/task/spark-jar',
                        'functions/task/hive-sql',
                        'functions/task/oceanbase-sql',
                        'functions/task/doris-sql',
                        'functions/task/clickhouse-sql',
                        'functions/task/tidb-sql',
                        'functions/task/gaussdb-sql',
                        'functions/task/mysql-sql',
                        'functions/task/vertica-sql',
                        'functions/task/greenplum-sql',
                        'functions/task/postgre-sql',
                        'functions/task/sqlserver-sql',
                        'functions/task/maxcompute-sql',
                        'functions/task/hadoop-mr',
                        'functions/task/datax',
                    ],
                 },
                'functions/maintenance',
                'functions/depend',
                'functions/task-param',
                'functions/environmental-parameters',
            ],
        },
        {
            type: 'category',
            label: '自定义扩展开发',
            collapsed: true,
            items: [
                'expand/task',
                'expand/component',
            ],
        },
        'contributing',
    ],

};

module.exports = sidebars;
