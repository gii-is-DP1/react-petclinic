import { render, screen, testRenderList } from "../../test-utils";
import userEvent from "@testing-library/user-event";
import SpecialtyListAdmin from "./SpecialtyListAdmin";

describe('SpecialtyListAdmin', () => {
    test('renders correctly', async () => {
        render(<SpecialtyListAdmin />);
        testRenderList('specialties');
    });

    test('renders specialties correctly', async () => {
        render(<SpecialtyListAdmin />);
        const specialty1 = await screen.findByRole('cell', { 'name': 'surgery' });
        expect(specialty1).toBeInTheDocument();

        const editButtons = await screen.findAllByRole('link', { 'name': /edit/ });
        expect(editButtons).toHaveLength(3);

        const deleteButtons = await screen.findAllByRole('button', { 'name': /delete/ });
        expect(deleteButtons).toHaveLength(3);

        const specialty2 = await screen.findByRole('cell', { 'name': 'dentistry' });
        expect(specialty2).toBeInTheDocument();

        const specialties = await screen.findAllByRole('row', {},);
        expect(specialties).toHaveLength(4);
    });

    test('delete specialty correct', async () => {
        const user = userEvent.setup();
        const jsdomConfirm = window.confirm;
        window.confirm = () => { return true };
        render(<SpecialtyListAdmin />);

        const specialty1Delete = await screen.findByRole('button', { 'name': 'delete-1' });
        await user.click(specialty1Delete);
        const alert = await screen.findByRole('alert');
        expect(alert).toBeInTheDocument();

        window.confirm = jsdomConfirm;
    });
});