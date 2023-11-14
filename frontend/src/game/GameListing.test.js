import { render, screen } from '../test-utils';
import {act} from '@testing-library/react'
import GameList from './index';


describe("Developer listing tests",()=>{
    test("Should show developers names", async ()=>{        
        act(() => {
            render(<GameList />);
        });
        // Here we show the current state of the screen.
        screen.debug();
        
        
        const rows=await screen.findByText(/Parejo/);
        // We show the status again!
        screen.debug();
        await expect(rows).toBeInTheDocument();
        
    });
});