/**
 * Note: Before starting, 
 * you need to global install pm2 and make sure that the node version is greater than v12.18.0
 */

const path = require('path');
const fs = require('fs');
const spawn = require('child_process').spawn;

/**
 * publicURL needs to be modified to your domain
 */
const publicURL = 'http://schedule.dtstack.cn';
const rootPath = path.resolve(__dirname, '../../');
const packagesPath = path.resolve(rootPath, './packages');
const scriptsPath = path.resolve(rootPath, './scripts');
const pluginPath = path.join(
    scriptsPath,
    './fastProvision/plugin/insert-html-webpack-plugin.js'
);

/**
 * If you modify the micro front end, you need to maintain it here
 */
const microProjects = {
    ide: 'ide-ui',
    dataSource: 'publicservice',
    console: 'console-ui',
};
const bundlePath = {
    [microProjects.dataSource]: path.join(
        packagesPath,
        `./${microProjects.dataSource}/ko.config.js`
    ),
    [microProjects.console]: path.join(
        packagesPath,
        `./${microProjects.console}/build/base.js`
    ),
};
const iconfontPath = {
    [microProjects.console]: path.join(
        packagesPath,
        `./${microProjects.console}/src/public/iconfont/iconfont.css`
    ),
};


const sourceFile = new Map();

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
                if (typeof callback === 'function') {
                    callback();
                }
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
        throw new Error('webpack handling exception');
    }
}

function microAdaptation(projects) {
    logger('start webpack and iconfont temporary processing');

    projects.forEach((item) => {
        switch (item) {
            case microProjects.dataSource: {
                pluginInject(bundlePath[microProjects.dataSource]);
                break;
            }
            case microProjects.console: {
                pluginInject(bundlePath[microProjects.console]);
                iconfontInject(iconfontPath[microProjects.console]);
                break;
            }
            default:
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

const fastProvision = async () => {
    try {
        const files = fs.readdirSync(packagesPath);
        const projects = files.filter((item) => {
            return isDirectory(path.join(packagesPath, item));
        });

        /**
         * git synchronizes the latest code
         */
        await useSpawn('yarn', ['pull'], true);

        /**
         * update project dependencies
         */
        await useSpawn('lerna', ['bootstrap'], true);

        /**
         * process webpack to add a common prefix and process iconfont
         */
        microAdaptation(projects);

        /**
         * lerna build
         */
        await useSpawn('sh', [`${scriptsPath}/build.sh`], true);

        /**
         * pm2 control
         */
        await useSpawn(
            'pm2',
            [`start`, `${path.join(scriptsPath, './start.sh')}`],
            true
        );

        /**
         * restore the scene
         */
        resetContent();
    } catch (error) {
        /**
         * restore the file contents and interrupt the process
         */
        resetContent();
        return logger(error);
    }
};

fastProvision();
