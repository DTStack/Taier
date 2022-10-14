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
                            'quickstart/deploy/docker',
                            'quickstart/deploy/deployment-quick',
                            'quickstart/deploy/cluster-deploy',
                            'quickstart/deploy/build',
                        ],
                 },
                  'quickstart/start',
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
                        'functions/component/flink',
                        'functions/component/spark-thrift',
                        'functions/component/spark',
                        'functions/component/hive',
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
                        'functions/task/flink-sql',
                        'functions/task/spark-sql',
                        'functions/task/hive-sql',
                        'functions/task/oceanbase-sql',
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
        'faq',
    ],

};

module.exports = sidebars;
