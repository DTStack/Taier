import { registerMicroApps, setDefaultMountApp, start } from 'qiankun';

export const AppContainer = 'AppContainer';
const container = `#${AppContainer}`;

let ENTRY_CONSOLE = '//local.dtstack.cn:8082/console/';
let ENTRY_OPERATION = '//local.dtstack.cn:8082/console/';
let ENTRY_DATABASE = '//local.dtstack.cn:8082/datasource/';

// For Production
if (process.env.NODE_ENV === 'production') {
    ENTRY_CONSOLE = '/console/';
    ENTRY_OPERATION = '/console/';
    ENTRY_DATABASE = '/datasource/';
}

registerMicroApps([
    {
        name: 'Operation',
        entry: ENTRY_OPERATION,
        container: container,
        activeRule: '#/operation-ui',
    },
    {
        name: 'DTConsoleApp',
        entry: ENTRY_CONSOLE,
        container: container,
        activeRule: '#/console-ui',
    },
    {
        name: 'Database',
        entry: ENTRY_DATABASE,
        container: container,
        activeRule: '#/data-source',
    },
]);

start({
    sandbox: {
        experimentalStyleIsolation: true,
    },
});

setDefaultMountApp('/');
