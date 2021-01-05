let err = false

const LOCK_NODE_VERSION = false
const LOCK_YARN_VERSION = false
const FORCE_YARN_INSTALL = true

if (LOCK_NODE_VERSION) {
    const majorNodeVersion = parseInt(/^(\d+)\./.exec(process.versions.node)[1])

    if (majorNodeVersion < 10 || majorNodeVersion >= 13) {
        console.error('\033[1;31m*** Please use node >=10 and <=12.\033[0;0m')
        err = true
    }
}

if (LOCK_YARN_VERSION) {
    const cp = require('child_process')
    const yarnVersion = cp.execSync('yarn -v', { encoding: 'utf8' }).trim()
    const parsedYarnVersion = /^(\d+)\.(\d+)\./.exec(yarnVersion)
    const majorYarnVersion = parseInt(parsedYarnVersion[1])
    const minorYarnVersion = parseInt(parsedYarnVersion[2])

    if (majorYarnVersion < 1 || minorYarnVersion < 10) {
        console.error('\033[1;31m*** Please use yarn >=1.10.1.\033[0;0m')
        err = true
    }
}

if (FORCE_YARN_INSTALL) {
    if (!/yarn[\w-.]*\.js$|yarnpkg$/.test(process.env['npm_execpath'])) {
        console.error('\033[1;31m*** Please use yarn to install dependencies.\033[0;0m')
        err = true
    }
}

if (err) {
    console.error('')
    process.exit(1)
}
