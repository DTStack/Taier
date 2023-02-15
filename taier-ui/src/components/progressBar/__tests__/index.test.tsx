import progressBar from '..';
import '@testing-library/jest-dom';

jest.useFakeTimers();
describe('Test ProgressBar Component', () => {
    beforeEach(() => {
        document.body.innerHTML = '';
    });

    it('Should support show and prevent duplicated show', () => {
        progressBar.show();

        jest.runOnlyPendingTimers();

        expect(document.querySelector('.dtc-progress-progress-bar')).toBeInTheDocument();

        progressBar.show();
        jest.runOnlyPendingTimers();

        expect(document.querySelectorAll('.dtc-progress-progress-bar').length).toBe(1);

        // Because calling the show function in twice
        progressBar.hide();
        progressBar.hide();
        expect(document.querySelectorAll('.dtc-progress-progress-bar').length).toBe(0);
    });
});
