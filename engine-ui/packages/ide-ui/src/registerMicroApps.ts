import { registerMicroApps, setDefaultMountApp, start } from 'qiankun';

export const AppContainer = 'AppContainer';
const container = `#${AppContainer}`;

registerMicroApps([
  {
    name: 'Operation',
    entry: '//dev.insight.dtstack.cn/console',
    container: container,
    activeRule: '/operation',
  },
  {
    name: 'DTConsoleApp',
    entry: '//local.dtstack.cn:8080/console',
    container: container,
    activeRule: '#/console-ui',
  },{
    name: 'Database',
    entry: { scripts: ['//localhost:7100/main.js'] },
    container: container,
    activeRule: '/database',
  },
]);

start();

setDefaultMountApp('/');
