const spawn = require('child_process').spawn;
const path = require('path');
const fs = require('fs');

const rootPath = path.resolve(__dirname, '../../');
const packagesPath = path.resolve(rootPath, './packages');
const scriptsPath = path.resolve(rootPath, './scripts');
const pluginPath = path.join(
    scriptsPath,
    './deploy/plugin/insert-html-webpack-plugin.js'
);

/**
 * If you modify the micro front end, you need to maintain it here
 */
const packagePath = require(path.join(rootPath, './package.json'));
const appConfig = packagePath.microApp;
const publicURL = packagePath.microHost

const bundlePath = {};
const iconfontPath = {};
const sourceFile = new Map();

for (const project in appConfig) {
    if (appConfig.hasOwnProperty(project)) {
        const projectConfig = appConfig[project];
        projectConfig.bundlePath &&
            (bundlePath[project] = projectConfig.bundlePath);
        projectConfig.iconfontPath &&
            (iconfontPath[project] = projectConfig.iconfontPath);
    }
}

function isDirectory(filePath) {
    try {
        return fs.statSync(filePath).isDirectory();
    } catch {
        return false;
    }
}

function useSpawn(
    command,
    args = [],
    options = {
        cwd: rootPath,
    },
    log = false
) {
    if (typeof args === 'boolean') {
        log = args;
        args = [];
    }
    if (typeof options === 'boolean') {
        log = options;
        options = {
            cwd: rootPath,
        };
    }
    const process = spawn(command, args, options);

    return new Promise((resolve, reject) => {
        try {
            logger(`${command} start execution!`);
            process.stdout.on('data', function (data) {
                log && logger(`${data}`);
            });
            process.stderr.on('data', function (data) {
                /**
                 *  lerna correct information comes in through the wrong information stream,
                 *  which is kind of weird. But it works fine.
                 */
                log && logger(`${data}`);
            });
            process.on('exit', function () {
                logger(`${command} Mission Accomplished！`);
                resolve(true);
            });
        } catch (error) {
            reject(error);
        }
    });
}

function logger(message) {
    console.log('DAG Info:'.padEnd(6, ' '), message);
}

function iconfontInject(iconFilePath) {
    try {
        const content = fs.readFileSync(iconFilePath, { encoding: 'utf8' });
        /**
         * TODO: subsequently, url logic of micro applications such as console here will be extracted.
         */
        const finalContent = content.replace(
            /(?<=url\(')(iconfont)/g,
            `${publicURL}/console/public/iconfont/$1`
        );

        sourceFile.set(iconFilePath, content);
        fs.writeFileSync(iconFilePath, finalContent, { encoding: 'utf8' });
    } catch (e) {
        throw new Error('iconfont handling exception:' + e);
    }
}

function pluginInject(configFilePath) {
    try {
        const content = fs.readFileSync(configFilePath, { encoding: 'utf8' });
        const randomId = Math.random().toString().replace('.', '');
        const pluginName = `InsertHtmlPlugin${randomId}`;
        const pluginModule = `const ${pluginName} = require('${pluginPath}'); \n`;
        /**
         * TODO: a better way to do this is to check if there is a plugin module, instead of adding a plugin module by default
         */
        const contentBody = JSON.stringify(content)
            .replace(
                /(?<=plugins:\s\[)(.+)(?=\])/,
                `\n            new ${pluginName}({ addCode: "${publicURL}" }),$1`
            )
            .slice(1)
            .split('\\n')
            .reduce((total, item) => {
                if (item === '"') return total;
                item = item.replace(/(?<!\\)\\(?!\\)/g, '');
                return (total += `${item.replace(/\\\\/g, '\\')}\n`);
            }, '');
        const finalContent = pluginModule + contentBody;

        sourceFile.set(configFilePath, content);
        fs.writeFileSync(configFilePath, finalContent, { encoding: 'utf8' });
    } catch (error) {
        throw new Error(error);
    }
}

function microAdaptation(projects) {
    logger('start webpack and iconfont temporary processing');

    projects.forEach((item) => {
        if (bundlePath.hasOwnProperty(item)) {
            const completePath = path.join(rootPath, bundlePath[item]);
            pluginInject(completePath);
        }
        if (iconfontPath.hasOwnProperty(item)) {
            const completePath = path.join(rootPath, iconfontPath[item]);
            iconfontInject(completePath);
        }
    });

    logger('complete the temporary processing of webpack and iconfont');
}

function resetContent() {
    logger('start restoring file contents');

    sourceFile.forEach((content, path) => {
        fs.writeFileSync(path, content, { encoding: 'utf8' });
    });
    sourceFile.clear();

    logger('restore of file contents completed');
}

async function automata(projects) {
  for (const project of projects) {
      useSpawn('lerna', ['run', 'start', `--scope=${project}`], true);
  }
}

module.exports = {
    rootPath: rootPath,
    packagesPath: packagesPath,
    scriptsPath: scriptsPath,
    pluginPath: pluginPath,
    bundlePath: bundlePath,
    iconfontPath: iconfontPath,
    sourceFile: sourceFile,
    isDirectory: isDirectory,
    useSpawn: useSpawn,
    logger: logger,
    iconfontInject: iconfontInject,
    resetContent: resetContent,
    microAdaptation: microAdaptation,
    automata: automata
};
