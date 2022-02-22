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
            collapsed: false,
            items: [
                'guides/introduction',
                'guides/work-deployment',
                'guides/explain',
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
                            'quickstart/deploy/pre-operation',
                            'quickstart/deploy/backend',
                            'quickstart/deploy/web',
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
                'functions/datasource',
                'functions/task',
                'functions/maintenance',
                'functions/depend',
                'functions/task-param',
                'functions/environmental-parameters',
            ],
        },
        'contributing',
    ],

};

module.exports = sidebars;
