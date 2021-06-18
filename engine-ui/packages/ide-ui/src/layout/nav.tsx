import React from 'react';

import molecule from 'molecule';
import { IMenuBarItem } from 'molecule/esm/model';
import { MenuMode } from 'molecule/esm/components';
import './nav.css';

function Navbar() {
  const data: IMenuBarItem[] = [{
    id: 'ide',
    name: '任务开发',
  },{
    id: 'operation',
    name: '运维中心',
  },{
    id: 'console',
    name: '控制台',
  },{
    id: 'database',
    name: '数据源',
  }];

  const handleClick = (e: any, item: any) => {
    switch(item.id) {
      default: {
        window.location.href = `/${item.id}`;
      }
    }
  }

  return (
      <div className="navbar">
        <molecule.component.Menu 
          data={data} 
          onClick={handleClick}
          mode={MenuMode.Horizontal} 
        />
      </div>
  );
}

export default Navbar;
