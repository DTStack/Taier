import { container } from 'tsyringe';

import BreadcrumbService from './breadcrumbService';
import CatalogueService from './catalogueService';
import DataSourceService from './dataSourceService';
import EditorActionBarService from './editorActionBarService';
import ExecuteService from './executeService';
import RightBarService from './rightBarService';
import TaskParamsService from './taskParamsService';
import TaskRenderService from './taskRenderService';

const editorActionBarService = container.resolve(EditorActionBarService);
const executeService = container.resolve(ExecuteService);
const catalogueService = container.resolve(CatalogueService);
const breadcrumbService = container.resolve(BreadcrumbService);
const rightBarService = container.resolve(RightBarService);
const taskRenderService = container.resolve(TaskRenderService);
const dataSourceService = container.resolve(DataSourceService);
const taskParamsService = container.resolve(TaskParamsService);

export {
    breadcrumbService,
    catalogueService,
    dataSourceService,
    editorActionBarService,
    executeService,
    rightBarService,
    taskParamsService,
    taskRenderService,
};
