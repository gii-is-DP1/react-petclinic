import { rest } from "msw";
import { server } from "../../mocks/server";
import { render, screen, testRenderList } from "../../test-utils";
import OwnerListAdmin from "./OwnerListAdmin";
import userEvent from "@testing-library/user-event";

describe('OwnerListAdmin', () => {
    test('renders correctly', async () => {
        render(<OwnerListAdmin />);
        testRenderList('owners');
    });

    test('renders owners correctly', async () => {
        render(<OwnerListAdmin />);
        const owner1 = await screen.findByRole('cell', { 'name': 'owner1' });
        expect(owner1).toBeInTheDocument();

        const editButtons = await screen.findAllByRole('link', { 'name': /edit/ });
        expect(editButtons).toHaveLength(2);

        const deleteButtons = await screen.findAllByRole('button', { 'name': /delete/ });
        expect(deleteButtons).toHaveLength(2);

        const owner2 = await screen.findByRole('cell', { 'name': 'owner2' });
        expect(owner2).toBeInTheDocument();

        const owners = await screen.findAllByRole('row', {},);
        expect(owners).toHaveLength(3);
    });

    test('renders owners with exception', async () => {
        server.use(
            rest.get('*/owners', (req, res, ctx) => {
                return res(
                    ctx.status(200),
                    ctx.json(
                        {
                            message: 'Error fetching data'
                        }
                    )
                )
            })
        )
        render(<OwnerListAdmin />);

        const modal = await screen.findByRole('dialog');
        expect(modal).toBeInTheDocument();
    });

    test('renders owners with server error', async () => {
        server.use(
            rest.get('*/owners', (req, res, ctx) => {
                return res(
                    ctx.status(500),

                )
            })
        )
        render(<OwnerListAdmin />);

        const modal = await screen.findByRole('dialog');
        expect(modal).toBeInTheDocument();
    });

    test('delete owner correct', async () => {
        const user = userEvent.setup();
        const jsdomConfirm = window.confirm;
        window.confirm = () => { return true };
        render(<OwnerListAdmin />);

        const owner1Delete = await screen.findByRole('button', { 'name': 'delete-owner1' });
        await user.click(owner1Delete);
        const alert = await screen.findByRole('alert');
        expect(alert).toBeInTheDocument();

        window.confirm = jsdomConfirm;
    });
});