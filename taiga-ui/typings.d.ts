declare module '*.css';
declare module '*.less';
declare module '*.png';
declare module '*.svg' {
	export function ReactComponent(props: React.SVGProps<SVGSVGElement>): React.ReactElement;
	const url: string;
	export default url;
}

declare module 'mirror-creator' {
	function mc(strs: string[], options?: { prefix?: string }): Record<string, string>;
	export default mc;
}

interface BrowserInter {
	chrome?: string;
	ie?: string;
	edge?: string;
	firefox?: string;
	safari?: string;
	opera?: string;
}

type numOrStr = number | string;

type Valueof<T> = T[keyof T];
