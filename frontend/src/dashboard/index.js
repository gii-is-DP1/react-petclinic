import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from 'reactstrap';
import { useLocalState } from '../util/useLocalStorage';

const Dashboard = () => {
    const [jwt,] = useLocalState("jwt", "");

    function createPet() {
        fetch("/api/v1/pets", {
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${jwt}`,
            },
            method: "POST"
        }).then(response => {
            if (response.status === 200) return response.json();
        }).then((data) => {
            console.log(data);
        })
    }

    return (
        /*<AppNavbar/>  */
        <div style={{ margin: "1cm" }}>
            <Button tag={Link} to="/api/v1/pets" onClick={() => createPet()}>Create new Pet</Button>
        </div >
    );
};

export default Dashboard;