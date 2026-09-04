import { render, screen, testRenderList } from "../../test-utils";
import userEvent from "@testing-library/user-event";
import ConsultationListAdmin from "./ConsultationListAdmin";

describe('ConsultationListAdmin', () => {
    test('renders correctly', async () => {
        render(<ConsultationListAdmin />);
        testRenderList('consultations');

        const filterButtons = screen.getAllByRole('button', { 'name': /filter/ });
        expect(filterButtons).toHaveLength(4);

        const searchBar = screen.getByRole('searchbox', { 'name': 'search' });
        expect(searchBar).toBeInTheDocument();

        const clearButton = screen.getByRole('button', { 'name': 'clear-all' });
        expect(clearButton).toBeInTheDocument();
    });

    test('renders consultations correctly', async () => {
        render(<ConsultationListAdmin />);
        const consultation1 = await screen.findByRole('cell', { 'name': 'Mi gato no come' });
        expect(consultation1).toBeInTheDocument();

        const consultation1Status = await screen.findByRole('cell', { 'name': 'ANSWERED' });
        expect(consultation1Status).toBeInTheDocument();

        const consultation2Status = await screen.findByRole('cell', { 'name': 'PENDING' });
        expect(consultation2Status).toBeInTheDocument();

        const editButtons = await screen.findAllByRole('link', { 'name': /edit/ });
        expect(editButtons).toHaveLength(2);

        const deleteButtons = await screen.findAllByRole('button', { 'name': /delete/ });
        expect(deleteButtons).toHaveLength(2)

        const consultations = await screen.findAllByRole('row', {},);
        expect(consultations).toHaveLength(3);
    });

    test('filter consultation correct', async () => {
        const user = userEvent.setup();
        render(<ConsultationListAdmin />);

        const pendingFilter = await screen.findByRole('button', { 'name': 'pending-filter' });
        await user.click(pendingFilter);

        const consultation2Status = await screen.findByRole('cell', { 'name': 'PENDING' });
        expect(consultation2Status).toBeInTheDocument();

        const consultation1Status = screen.queryByRole('cell', { 'name': 'ANSWERED' });
        expect(consultation1Status).not.toBeInTheDocument();
    });

    test('search consultation correct', async () => {
        const user = userEvent.setup();
        render(<ConsultationListAdmin />);

        const searchbar = await screen.findByRole('searchbox', { 'name': 'search' });
        await user.type(searchbar, "owner1");

        const consultationOwner1 = await screen.findByRole('cell', { 'name': 'owner1' });
        expect(consultationOwner1).toBeInTheDocument();

        const consultationOwner2 = screen.queryByRole('cell', { 'name': 'owner2' });
        expect(consultationOwner2).not.toBeInTheDocument();
    });

    test('clear all correct', async () => {
        const user = userEvent.setup();
        render(<ConsultationListAdmin />);

        const pendingFilter = await screen.findByRole('button', { 'name': 'pending-filter' });
        await user.click(pendingFilter);
        const searchbar = await screen.findByRole('searchbox', { 'name': 'search' });
        await user.type(searchbar, "owner2");
        const clearAllButton = await screen.findByRole('button', { 'name': 'clear-all' });
        await user.click(clearAllButton);

        const consultationOwner1 = await screen.findByRole('cell', { 'name': 'owner1' });
        expect(consultationOwner1).toBeInTheDocument();

        const consultationOwner2 = await screen.findByRole('cell', { 'name': 'owner2' });
        expect(consultationOwner2).toBeInTheDocument();
    });

    test('filter and search not found', async () => {
        const user = userEvent.setup();
        render(<ConsultationListAdmin />);

        const pendingFilter = await screen.findByRole('button', { 'name': 'pending-filter' });
        await user.click(pendingFilter);
        const searchbar = await screen.findByRole('searchbox', { 'name': 'search' });
        await user.type(searchbar, "owner2");

        const consultationOwner1 = screen.queryByRole('cell', { 'name': 'owner1' });
        expect(consultationOwner1).not.toBeInTheDocument();

        const consultationOwner2 = screen.queryByRole('cell', { 'name': 'owner2' });
        expect(consultationOwner2).not.toBeInTheDocument();

        const cell = await screen.findByRole('cell', { 'name': /There are no consultations/ });
        expect(cell).toBeInTheDocument();
    });

    test('filter then search', async () => {
        const user = userEvent.setup();
        render(<ConsultationListAdmin />);

        const pendingFilter = await screen.findByRole('button', { 'name': 'pending-filter' });
        await user.click(pendingFilter);
        const searchbar = await screen.findByRole('searchbox', { 'name': 'search' });
        await user.type(searchbar, "owner1");

        const consultationOwner1 = await screen.findByRole('cell', { 'name': 'owner1' });
        expect(consultationOwner1).toBeInTheDocument();

        const consultationOwner2 = screen.queryByRole('cell', { 'name': 'owner2' });
        expect(consultationOwner2).not.toBeInTheDocument();
    });

    test('search then filter', async () => {
        const user = userEvent.setup();
        render(<ConsultationListAdmin />);

        const searchbar = await screen.findByRole('searchbox', { 'name': 'search' });
        await user.type(searchbar, "owner1");
        const pendingFilter = await screen.findByRole('button', { 'name': 'pending-filter' });
        await user.click(pendingFilter);

        const consultationOwner1 = await screen.findByRole('cell', { 'name': 'owner1' });
        expect(consultationOwner1).toBeInTheDocument();

        const consultationOwner2 = screen.queryByRole('cell', { 'name': 'owner2' });
        expect(consultationOwner2).not.toBeInTheDocument();
    });

    test('search and filter then remove search', async () => {
        const user = userEvent.setup();
        render(<ConsultationListAdmin />);

        const searchbar = await screen.findByRole('searchbox', { 'name': 'search' });
        await user.type(searchbar, "owner2");
        const pendingFilter = await screen.findByRole('button', { 'name': 'pending-filter' });
        await user.click(pendingFilter);
        await user.clear(searchbar);

        const consultationStatusPending = await screen.findByRole('cell', { 'name': 'PENDING' });
        expect(consultationStatusPending).toBeInTheDocument();
    });

    test('search and filter then remove filter', async () => {
        const user = userEvent.setup();
        render(<ConsultationListAdmin />);

        const searchbar = await screen.findByRole('searchbox', { 'name': 'search' });
        await user.type(searchbar, "owner2");
        const pendingFilter = await screen.findByRole('button', { 'name': 'pending-filter' });
        await user.click(pendingFilter);
        const allFilter = await screen.findByRole('button', { 'name': 'all-filter' });
        await user.click(allFilter);

        const consultationOwner2 = await screen.findByRole('cell', { 'name': 'owner2' });
        expect(consultationOwner2).toBeInTheDocument();

        const consultationOwner1 = screen.queryByRole('cell', { 'name': 'owner1' });
        expect(consultationOwner1).not.toBeInTheDocument();
    });

    test('search then remove search', async () => {
        const user = userEvent.setup();
        render(<ConsultationListAdmin />);

        const searchbar = await screen.findByRole('searchbox', { 'name': 'search' });
        await user.type(searchbar, "owner2");
        await user.clear(searchbar);

        const consultationOwner2 = await screen.findByRole('cell', { 'name': 'owner2' });
        expect(consultationOwner2).toBeInTheDocument();

        const consultationOwner1 = await screen.findByRole('cell', { 'name': 'owner1' });
        expect(consultationOwner1).toBeInTheDocument();
    });

    test('filter then remove filter', async () => {
        const user = userEvent.setup();
        render(<ConsultationListAdmin />);

        const pendingFilter = await screen.findByRole('button', { 'name': 'pending-filter' });
        await user.click(pendingFilter);
        const allFilter = await screen.findByRole('button', { 'name': 'all-filter' });
        await user.click(allFilter);

        const consultationOwner2 = await screen.findByRole('cell', { 'name': 'owner2' });
        expect(consultationOwner2).toBeInTheDocument();

        const consultationOwner1 = await screen.findByRole('cell', { 'name': 'owner1' });
        expect(consultationOwner1).toBeInTheDocument();
    });

    test('delete consultation correct', async () => {
        const user = userEvent.setup();
        const jsdomConfirm = window.confirm;
        window.confirm = () => { return true };
        render(<ConsultationListAdmin />);

        const consultation1Delete = await screen.findByRole('button', { 'name': 'delete-1' });
        await user.click(consultation1Delete);
        const alert = await screen.findByRole('alert');
        expect(alert).toBeInTheDocument();

        window.confirm = jsdomConfirm;
    });

    test('delete consultation with filters correct', async () => {
        const user = userEvent.setup();
        const jsdomConfirm = window.confirm;
        window.confirm = () => { return true };
        render(<ConsultationListAdmin />);

        const answeredFilter = await screen.findByRole('button', { 'name': 'answered-filter' });
        await user.click(answeredFilter);

        const consultation1Delete = await screen.findByRole('button', { 'name': 'delete-1' });
        await user.click(consultation1Delete);
        const alert = await screen.findByRole('alert');
        expect(alert).toBeInTheDocument();

        const consultations = await screen.findByRole('cell', { 'name': /There are no consultations/ });
        expect(consultations).toBeInTheDocument();

        window.confirm = jsdomConfirm;
    });
});