import { createContext } from 'react';

export interface IPersonLists {
	dtuicUserId: number;
	email: string;
	phoneNumber?: string;
	userName: string;
}

export interface IContext {
	personList: IPersonLists[];
	username?: string;
}

export default createContext<IContext>({
	personList: [],
	username: undefined,
});
