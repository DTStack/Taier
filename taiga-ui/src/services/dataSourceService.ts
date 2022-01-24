import { Component } from '@dtinsight/molecule/esm/react';

interface IDataSource {
  dataSource: any[];
  current: number;
  filters: any;
}

class FunctionManagerService extends Component<IDataSource> {
  protected state: IDataSource;
  constructor() {
    super();
    this.state = {
      dataSource: [],
      current: 1,
      filters: {},
    };
  }
}

export default new FunctionManagerService();
