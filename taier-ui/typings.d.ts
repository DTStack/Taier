declare module '*.css';
declare module '*.less';
declare module '*.png';
declare module '*.svg' {
	export function ReactComponent(props: React.SVGProps<SVGSVGElement>): React.ReactElement;
	const url: string;
	export default url;
}

/**
 * refer to: https://jgraph.github.io/mxgraph/docs/js-api/files/index-txt.html
 */
interface IMxGraphConfig {
	mxBasePath?: string;
	mxImageBasePath?: string;
	mxLanguage?: string;
	mxDefaultLanguage?: string;
	mxLoadResources?: boolean;
	mxLoadStylesheets?: boolean;
}

interface IMxPoint {
	x: number;
	y: number;
	equals: (obj: IMxPoint) => boolean;
	clone: () => IMxPoint;
}

interface IMxGraphView {
	getTranslate: () => IMxPoint;
	getScale: () => number;
	setScale: (scale: number) => void;
	setTranslate: (dx: number, dy: number) => void;
	[key: string]: any;
}

interface IMxRectangle {
	width: number;
	height: number;
	x: number;
	y: number;
}

interface IMxCellHighlight {
	destory: () => void;
	[key: string]: any;
}

interface IMxGraph {
	container: HTMLElement;
	/**
	 * Holds the mxGraphView that caches the mxCellStates for the cells.
	 */
	view: IMxGraphView;
	/**
	 * mxRectangle that caches the scales, translated bounds of the current view.
	 */
	getGraphBounds: () => IMxRectangle;
	getView: () => IMxGraphView;
	/**
	 * Destroys the graph and all its resources
	 */
	destroy: () => void;
	/**
	 * Returns defaultParent or mxGraphView.currentRoot or the first child child of mxGraphModel.root if both are null.  The value returned by this function should be used as the parent for new cells (aka default layer).
	 */
	getDefaultParent: () => IMxCell;
	convertValueToString: (cell: IMxCell) => string;
	/**
	 * Zooms out of the graph by zoomFactor.
	 */
	zoomOut: () => void;
	/**
	 * Zooms into the graph by zoomFactor.
	 */
	zoomIn: () => void;
	addListener: (
		type: string,
		handler: (
			sender: any,
			evt: { getProperty: (type: string) => any; [key: string]: any },
		) => void | Promise<void>,
	) => void;
	getSelectionCells: () => IMxCell[] | null;
	setSelectionCell: (cell: IMxCell) => void;
	insertVertex: (
		parent: IMxCell,
		id: null | string,
		value: any,
		x: number,
		y: number,
		width: number,
		height: number,
		style?: string,
		relative?: boolean,
	) => IMxCell;
	[key: string]: any;
}

interface IMxEventSource {
	addItem: (
		title: string,
		image: string | null,
		funct?: () => void,
		parent?: IMxCell | null,
		iconCls?: string | null,
		enabled?: boolean,
		active?: boolean,
	) => IMxCell;
}

interface IMxCell<T = any> {
	id: string;
	mxObjectId: string;
	/**
	 * mxGeometry
	 */
	geometry?: any;
	style?: string | null;
	value?: T | null;
	parent?: IMxCell<T>;
	/**
	 * Root cell has children
	 */
	children?: IMxCell<T>[];
	edge?: true;
	vertex?: true;
	target?: IMxCell<T> | null;
	source?: IMxCell<T> | null;
	/**
	 * Only vertex cells have edges
	 */
	edges?: IMxCell<T>[];
	/**
	 * Only vertex cells have this property
	 */
	connectable?: boolean;
	/**
	 * Specifies whether the cell is collapsed.  Default is false.
	 */
	collapsed?: boolean;
}

interface mxInstance {
	mxGraphView: any;
	mxText: any;
	mxEvent: any;
	mxGraph: new (dom: HTMLElement) => IMxGraph;
	mxConstants: any;
	mxPerimeter: any;
	mxEdgeStyle: any;
	mxCellHighlight: any;
	mxPopupMenu: any;
	mxClient: any;
	mxRubberband: any;
	mxEventObject: any;
	mxRectangle: any;
	mxPoint: any;
	mxHierarchicalLayout: any;
	mxUtils: any;
}

declare module 'mxgraph' {
	export default function (config: IMxGraphConfig): mxInstance;
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
