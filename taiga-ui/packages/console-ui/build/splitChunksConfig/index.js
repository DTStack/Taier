let baseCommonList = ['react', 'react-router', 'react-dom', 'react-redux', 'redux', 'react-router-redux', 'lodash']

function createBaseCommonRegExp() {
	let result = baseCommonList.join("|");
	return new RegExp(`[\\\/]node_modules[\\\/](${result})`)
}

let baseCommonRegExp = createBaseCommonRegExp();

module.exports = {
	baseCommonRegExp: baseCommonRegExp
}