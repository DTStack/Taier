const fs = require('fs');
const path = require('path');
const https = require('https');
const colors = require('colors');
const prettier = require('prettier');
const ProgressBar = require('progress');

const WORKBENCH_FILE = path.join(__dirname, '..', 'src', 'pages', 'workbench.tsx');

const fileAccess = (() => {
	try {
		fs.accessSync(WORKBENCH_FILE, fs.constants.F_OK);
		return true;
	} catch (error) {
		return false;
	}
})();

if (!fileAccess) {
	console.log(colors.cyan('There is no 「workbench.tsx」in project, sync it from molecule now'));

	https.get(
		'https://cdn.jsdelivr.net/gh/DTStack/molecule@1.0.2/src/workbench/workbench.tsx',
		(res) => {
			let content = '';

			const len = parseInt(res.headers['content-length'], 10);
			const bar = new ProgressBar('  downloading [:bar] :percent :etas', {
				complete: '=',
				incomplete: ' ',
				width: 20,
				total: len,
			});

			res.setEncoding('utf8');
			res.on('data', (chunk) => {
				content += chunk;
				bar.tick(chunk.length);
			});

			res.on('end', () => {
				console.log(colors.green('Sync done! Insert Rightbar now'));
				const lines = content.split('\n');
				let lastImportIdx = -1;
				let insertJSXIdx = -1;
				let layoutServiceIdx = -1;
				let layoutServiceTypeIdx = -1;

				const nextContent = lines.map((line, idx) => {
					if (line.startsWith('import')) {
						lastImportIdx = idx;
					}

					if (line.endsWith('</SplitPane>')) {
						insertJSXIdx = idx;
					}

					if (line.startsWith('const layoutService')) {
						layoutServiceIdx = idx;
					}

					if (line.includes('LayoutService') && line.startsWith('import')) {
						layoutServiceTypeIdx = idx;
					}

					if (line.includes('layoutService,')) {
						return line.replace('layoutService,', 'molecule.layout,');
					}

					if (line.includes('mo/')) {
						return line.replace('mo/', '@dtinsight/molecule/esm/');
					}

					return line;
				});

				// 插入 Rightbar 节点
				const indentLength =
					nextContent[insertJSXIdx - 1].length -
					nextContent[insertJSXIdx - 1].trimStart().length;
				nextContent.splice(
					insertJSXIdx + 1,
					0,
					`${new Array(indentLength).fill(' ').join('')}<RightBar />`,
				);

				// 删除 layoutService， 用 molecule.layout 代替
				nextContent.splice(layoutServiceIdx, 1);

				// 在 import 的最后一行导入侧边栏
				nextContent.splice(
					lastImportIdx + 1,
					0,
					"import molecule from '@dtinsight/molecule';\nimport RightBar from './rightBar';",
				);

				// 删除多余的类型导入
				nextContent.splice(layoutServiceTypeIdx, 1);

				nextContent.splice(
					0,
					0,
					'// 这是一个自动生成的文件，减少不必要的修改除了格式化以外',
				);

				prettier
					.resolveConfig(path.join(__dirname, '..', '.prettierrc.js'))
					.then((options) => {
						fs.writeFileSync(
							WORKBENCH_FILE,
							prettier.format(nextContent.join('\n'), {
								...options,
								useTabs: true,
								tabWidth: 4,
								parser: 'typescript',
							}),
						);
						console.log(colors.green('Sync workbench from molecule successfully!'));
					});
			});
		},
	);
}
