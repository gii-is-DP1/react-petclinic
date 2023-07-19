import { render, screen } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'

const customRender = (ui, options) =>
    render(ui, { wrapper: BrowserRouter, ...options })

const testRenderList = (title) => {
    const re = new RegExp(title, 'i');

    const heading = screen.getByRole('heading', { 'name': re });
    expect(heading).toBeInTheDocument();

    const table = screen.getByRole('table', { 'name': re });
    expect(table).toBeInTheDocument();

    const addLink = screen.getByRole('link', { 'name': /Add/ });
    expect(addLink).toBeInTheDocument();

    const rows = screen.getAllByRole('row');
    expect(rows).toHaveLength(1);

}

// re-export everything
export * from '@testing-library/react'

// override render method
export { customRender as render, testRenderList }