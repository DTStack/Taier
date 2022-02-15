import { createContext } from 'react';

export interface IPersonLists {
	email: string;
	id: number;
	phoneNumber: string;
	status: number;
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
