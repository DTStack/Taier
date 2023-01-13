import { container } from 'tsyringe';
import EditorActionBarService from './editorActionBarService';
import ExecuteService from './executeService';
import CatalogueService from './catalogueService';
import BreadcrumbService from './breadcrumbService';
import RightBarService from './rightBarService';
import TaskRenderService from './taskRenderService';
import DataSourceService from './dataSourceService';
import TaskParamsService from './taskParamsService';

const editorActionBarService = container.resolve(EditorActionBarService);
const executeService = container.resolve(ExecuteService);
const catalogueService = container.resolve(CatalogueService);
const breadcrumbService = container.resolve(BreadcrumbService);
const rightBarService = container.resolve(RightBarService);
const taskRenderService = container.resolve(TaskRenderService);
const dataSourceService = container.resolve(DataSourceService);
const taskParamsService = container.resolve(TaskParamsService);

export {
	editorActionBarService,
	catalogueService,
	executeService,
	breadcrumbService,
	rightBarService,
	taskRenderService,
	dataSourceService,
	taskParamsService,
};
