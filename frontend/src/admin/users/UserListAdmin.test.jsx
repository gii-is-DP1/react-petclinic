import { render, screen, testRenderList } from "../../test-utils";
import userEvent from "@testing-library/user-event";
import UserListAdmin from "./UserListAdmin";

describe('UserListAdmin', () => {
    test('renders correctly', async () => {
        render(<UserListAdmin />);
        testRenderList('users');
    });

    test('renders users correctly', async () => {
        render(<UserListAdmin />);
        const owner1 = await screen.findByRole('cell', { 'name': 'owner1' });
        expect(owner1).toBeInTheDocument();

        const editButtons = await screen.findAllByRole('link', { 'name': /edit/ });
        expect(editButtons).toHaveLength(2);

        const deleteButtons = await screen.findAllByRole('button', { 'name': /delete/ });
        expect(deleteButtons).toHaveLength(2);

        const admin1 = await screen.findByRole('cell', { 'name': 'admin1' });
        expect(admin1).toBeInTheDocument();

        const owners = await screen.findAllByRole('row', {},);
        expect(owners).toHaveLength(3);
    });

    test('delete user correct', async () => {
        const user = userEvent.setup();
        const jsdomConfirm = window.confirm;
        window.confirm = () => { return true };
        render(<UserListAdmin />);

        const user1Delete = await screen.findByRole('button', { 'name': 'delete-1' });
        await user.click(user1Delete);
        const alert = await screen.findByRole('alert');
        expect(alert).toBeInTheDocument();

        window.confirm = jsdomConfirm;
    });
});