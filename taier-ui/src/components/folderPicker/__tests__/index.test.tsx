import api from '@/api';
import { CATALOGUE_TYPE } from '@/constant';
import { catalogueService } from '@/services';
import { toggleOpen } from '@/tests/utils';
import molecule from '@dtinsight/molecule';
import { cleanup, fireEvent, render } from '@testing-library/react';
import { act } from 'react-dom/test-utils';
import FolderPicker from '..';
import functionData from './fixtures/functionData';
import resourceData from './fixtures/resourceData';
import treeData from './fixtures/treeData';

jest.useFakeTimers();
jest.mock('@/api');
jest.mock('@/services', () => ({
    catalogueService: {
        loadTreeNode: jest.fn(),
    },
}));
jest.mock('@/services/resourceManagerService', () => {
    return {
        get: jest.fn(() => null),
        getState: jest.fn(() => ({
            folderTree: {
                data: [resourceData],
            },
        })),
    };
});

jest.mock('@/services/functionManagerService', () => {
    return {
        getState: jest.fn(() => ({
            folderTree: {
                data: [functionData],
            },
        })),
    };
});
jest.mock('@/utils/extensions', () => {
    return {
        fileIcon: () => <svg data-testid="mockFileIcon" />,
    };
});

describe('Test FolderPicker Component', () => {
    beforeEach(() => {
        cleanup();
        (molecule.folderTree.getState as jest.Mock).mockReset().mockImplementation(() => ({
            folderTree: {
                data: [treeData],
            },
        }));

        (catalogueService.loadTreeNode as jest.Mock).mockReset();
    });

    it('Should match snapshot', () => {
        const { asFragment } = render(<FolderPicker showFile dataType={CATALOGUE_TYPE.TASK} />);

        expect(asFragment()).toMatchSnapshot();
    });

    it("Should find current resource's location by backtrack", async () => {
        (api.getResourceLocation as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: [1, 2, 3],
        });
        await act(async () => {
            render(<FolderPicker showFile dataType={CATALOGUE_TYPE.RESOURCE} value={1} />);
        });

        expect(api.getResourceLocation).toBeCalled();
        expect(catalogueService.loadTreeNode).toBeCalled();
    });

    it('Should trigger loadData', async () => {
        const { container } = render(<FolderPicker showFile dataType={CATALOGUE_TYPE.FUNCTION} />);

        toggleOpen(container);

        await act(async () => {
            fireEvent.click(container.querySelector('.ant-select-tree-switcher')!);
        });
        expect(catalogueService.loadTreeNode).toBeCalledWith({ catalogueType: 'FunctionManager', id: 39 }, 'function');
    });
});
