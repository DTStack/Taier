import { defineConfig } from 'umi';
import MonacoWebpackPlugin from 'monaco-editor-webpack-plugin';

export default defineConfig({
    title: 'Taier | DTStack',
    favicon: 'images/favicon.png',
    hash: true,
    publicPath: './',
    base: './',
    ignoreMomentLocale: true,
    targets: {
        ios: false,
    },
    nodeModulesTransform: {
        type: 'none',
    },
    webpack5: {},
    dynamicImportSyntax: {},
    routes: [
        {
            path: '/',
            component: '@/layout/index',
            routes: [
                {
                    path: '/',
                    component: '@/pages/index',
                },
            ],
        },
    ],
    chainWebpack(memo, { env }) {
        memo.plugin('monaco-editor').use(MonacoWebpackPlugin, [
            {
                languages: ['json', 'python', 'shell', 'sql'],
                customLanguages: [
                    {
                        label: 'mysql',
                        entry: 'monaco-sql-languages/out/esm/mysql/mysql.contribution',
                        worker: {
                            id: 'monaco-sql-languages/out/esm/mysql/mySQLWorker',
                            entry: 'monaco-sql-languages/out/esm/mysql/mysql.worker',
                        },
                    },
                    {
                        label: 'flinksql',
                        entry: 'monaco-sql-languages/out/esm/flinksql/flinksql.contribution',
                        worker: {
                            id: 'monaco-sql-languages/out/esm/flinksql/flinkSQLWorker',
                            entry: 'monaco-sql-languages/out/esm/flinksql/flinksql.worker',
                        },
                    },
                    {
                        label: 'sparksql',
                        entry: 'monaco-sql-languages/out/esm/sparksql/sparksql.contribution',
                        worker: {
                            id: 'monaco-sql-languages/out/esm/sparksql/sparkSQLWorker',
                            entry: 'monaco-sql-languages/out/esm/sparksql/sparksql.worker',
                        },
                    },
                    {
                        label: 'hivesql',
                        entry: 'monaco-sql-languages/out/esm/hivesql/hivesql.contribution',
                        worker: {
                            id: 'monaco-sql-languages/out/esm/hivesql/hiveSQLWorker',
                            entry: 'monaco-sql-languages/out/esm/hivesql/hivesql.worker',
                        },
                    },
                    {
                        label: 'pgsql',
                        entry: 'monaco-sql-languages/out/esm/pgsql/pgsql.contribution',
                        worker: {
                            id: 'monaco-sql-languages/out/esm/pgsql/PgSQLWorker',
                            entry: 'monaco-sql-languages/out/esm/pgsql/pgsql.worker',
                        },
                    },
                ],
            },
        ]);

        return memo;
    },
    esbuild: {},
    theme: {
        'primary-color': '#3f87ff',
        'border-radius-base': '0px',
    },
    tailwindcss: {},
    proxy: {
        '/taier': {
            target: 'http://localhost:8090',
            changeOrigin: true,
            secure: false,
        },
    },
});
