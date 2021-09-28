import mc from 'mirror-creator';
export const projectAction = mc(
  ['GET_PROJECT', 'GET_PROJECTS', 'GET_ALL_PROJECTS', 'SET_PROJECT'],
  { prefix: 'project/' }
);
