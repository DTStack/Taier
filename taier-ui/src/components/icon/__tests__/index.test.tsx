import { render } from '@testing-library/react';
import {
    ClickhouseIcon,
    CommonComponentIcon,
    ComponentConfigIcon,
    ComputeComponentIcon,
    DataCollectionIcon,
    DataSourceLinkFailed,
    DataSourceLinkSuccess,
    DorisIcon,
    FlinkIcon,
    FlinkSQLIcon,
    GreenPlumIcon,
    HadoopMRIcon,
    HiveSQLIcon,
    Logo,
    MysqlIcon,
    OceanBaseIcon,
    PostgreSqlIcon,
    PythonIcon,
    ResourceIcon,
    SchedulingComponentIcon,
    ShellIcon,
    SparkIcon,
    SparkSQLIcon,
    SqlServerIcon,
    StoreComponentIcon,
    SyntaxIcon,
    TiDbIcon,
    VerticaIcon,
    VirtualIcon,
    WorkflowIcon,
} from '..';

describe('Test Icon Component', () => {
    it('Should match snapshots', () => {
        expect(render(<Logo />).asFragment()).toMatchSnapshot();
        expect(render(<SparkSQLIcon />).asFragment()).toMatchSnapshot();
        expect(render(<HiveSQLIcon />).asFragment()).toMatchSnapshot();
        expect(render(<FlinkSQLIcon />).asFragment()).toMatchSnapshot();
        expect(render(<DataCollectionIcon />).asFragment()).toMatchSnapshot();
        expect(render(<ComponentConfigIcon />).asFragment()).toMatchSnapshot();
        expect(render(<ResourceIcon />).asFragment()).toMatchSnapshot();
        expect(render(<CommonComponentIcon />).asFragment()).toMatchSnapshot();
        expect(render(<SchedulingComponentIcon />).asFragment()).toMatchSnapshot();
        expect(render(<StoreComponentIcon />).asFragment()).toMatchSnapshot();
        expect(render(<ComputeComponentIcon />).asFragment()).toMatchSnapshot();
        expect(render(<DataSourceLinkSuccess />).asFragment()).toMatchSnapshot();
        expect(render(<DataSourceLinkFailed />).asFragment()).toMatchSnapshot();
        expect(render(<SyntaxIcon />).asFragment()).toMatchSnapshot();
        expect(render(<FlinkIcon />).asFragment()).toMatchSnapshot();
        expect(render(<OceanBaseIcon />).asFragment()).toMatchSnapshot();
        expect(render(<VirtualIcon />).asFragment()).toMatchSnapshot();
        expect(render(<WorkflowIcon />).asFragment()).toMatchSnapshot();
        expect(render(<ShellIcon />).asFragment()).toMatchSnapshot();
        expect(render(<PythonIcon />).asFragment()).toMatchSnapshot();
        expect(render(<ClickhouseIcon />).asFragment()).toMatchSnapshot();
        expect(render(<DorisIcon />).asFragment()).toMatchSnapshot();
        expect(render(<SparkIcon />).asFragment()).toMatchSnapshot();
        expect(render(<MysqlIcon />).asFragment()).toMatchSnapshot();
        expect(render(<HadoopMRIcon />).asFragment()).toMatchSnapshot();
        expect(render(<GreenPlumIcon />).asFragment()).toMatchSnapshot();
        expect(render(<PostgreSqlIcon />).asFragment()).toMatchSnapshot();
        expect(render(<SqlServerIcon />).asFragment()).toMatchSnapshot();
        expect(render(<TiDbIcon />).asFragment()).toMatchSnapshot();
        expect(render(<VerticaIcon />).asFragment()).toMatchSnapshot();
    });
});
