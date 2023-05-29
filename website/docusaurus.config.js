// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require("prism-react-renderer/themes/github");
const darkCodeTheme = require("prism-react-renderer/themes/dracula");

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: "Taier",
  tagline: "分布式可视化的DAG任务调度系统",
  url: "https://dtstack.github.io",
  baseUrl: "/Taier/",
  onBrokenLinks: "error",
  onBrokenMarkdownLinks: "error",
  favicon: "img/favicon.png",
  organizationName: "DTStack", // Usually your GitHub org/user name.
  projectName: "Taier", // Usually your repo name.
  presets: [
    [
      "classic",
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve("./sidebars.js"),
          // Please change this to your repo.
          editUrl:
            "https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/",
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          editUrl:
            "https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/",
        },
        theme: {
          customCss: require.resolve("./src/css/custom.css"),
        },
        gtag: {
          trackingID: "G-09MSEFN7VC",
          anonymizeIP: true,
        },
      }),
    ],
  ],
  plugins: ["docusaurus-plugin-sass"],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: "Taier",
        logo: {
          alt: "Taier Logo",
          src: "img/logo.svg",
        },
        items: [
          {
            type: "doc",
            docId: "guides/introduction",
            position: "left",
            label: "文档",
          },
          {
            position: "left",
            href: "https://github.com/DTStack/Taier/releases",
            label: "源码下载",
          },
          {
            position: "left",
            href: "/docs/quickstart/faq",
            label: "常见问题",
          },
          {
            href: "https://github.com/DTStack/Taier",
            position: "right",
            className: "header-github-link",
          },
        ],
      },
      footer: {
        links: [
          {
            title: "Docs",
            items: [
              {
                label: "Introduction",
                to: "/docs/guides/introduction",
              },
              {
                label: "Quick Start",
                to: "/docs/quickstart/start",
              },
              {
                label: "Contributing",
                to: "/docs/contributing",
              },
            ],
          },
          {
            title: "Community",
            items: [
              {
                label: "GitHub",
                href: "https://github.com/DTStack/Taier",
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} DTStack, Inc.`,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
        additionalLanguages: ["nginx", "java"],
      },
    }),
};

module.exports = config;
