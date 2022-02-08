// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
    title: 'Taiga',
    tagline: 'A Distributed dispatching system',
    url: 'https://taiga.vercel.app/',
    baseUrl: '/',
    onBrokenLinks: 'error',
    onBrokenMarkdownLinks: 'error',
    favicon: 'img/favicon.ico',
    organizationName: 'DTStack', // Usually your GitHub org/user name.
    projectName: 'Taiga', // Usually your repo name.
    presets: [
        [
            'classic',
            /** @type {import('@docusaurus/preset-classic').Options} */
            ({
                docs: {
                    sidebarPath: require.resolve('./sidebars.js'),
                    // Please change this to your repo.
                    editUrl: 'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
                },
                blog: {
                    showReadingTime: true,
                    // Please change this to your repo.
                    editUrl:
                        'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
                },
                theme: {
                    customCss: require.resolve('./src/css/custom.css'),
                },
            }),
        ],
    ],

    themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
        ({
            navbar: {
                title: 'Taiga',
                logo: {
                    alt: 'Taiga Logo',
                    src: 'img/logo.svg',
                },
                items: [
                    {
                        type: 'doc',
                        docId: 'overview',
                        position: 'left',
                        label: 'DOCS',
                    },
                    // {
                    //     to: 'case',
                    //     label: 'SHOWCASE',
                    //     position: 'left',
                    // },
                    {
                        href: 'https://github.com/DTStack/Taiga',
                        label: 'GITHUB',
                        position: 'left',
                    }
                ],
            },
            footer: {
                style: 'dark',
                links: [
                    {
                        title: 'Docs',
                        items: [
                            {
                                label: 'Quick Start',
                                to: '/docs/quickstart/start',
                            },
                        ],
                    },
                    {
                        title: 'Community',
                        items: [
                            {
                                label: 'GitHub',
                                href: 'https://github.com/DTStack/Taiga',
                            }
                        ],
                    },
                ],
                copyright: `Copyright © ${new Date().getFullYear()} DTStack, Inc. 平台开发团队.`,
            },
            prism: {
                theme: lightCodeTheme,
                darkTheme: darkCodeTheme,
            },
        }),
};

module.exports = config;
