import { registerMicroApps, setDefaultMountApp, start } from 'qiankun';
import { AppContainer } from './App';

const container = `#${AppContainer}`;

registerMicroApps([
  {
    name: 'IDE', // app name registered
    entry: '//localhost:7100',
    container: container,
    activeRule: '/ide',
  },
  {
    name: 'Operation',
    entry: '//dev.insight.dtstack.cn/console',
    container: container,
    activeRule: '/operation',
  },
  {
    name: 'Console',
    entry: { scripts: ['//localhost:7100/main.js'] },
    container: container,
    activeRule: '/console',
  },{
    name: 'Database',
    entry: { scripts: ['//localhost:7100/main.js'] },
    container: container,
    activeRule: '/database',
  },
]);

start();

setDefaultMountApp('/ide');
