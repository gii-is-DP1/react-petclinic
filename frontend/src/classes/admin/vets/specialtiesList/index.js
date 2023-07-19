import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
// import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';

class SpecialtiesList extends Component {

    constructor(props) {
        super(props);
        this.state = { specialties: [] };
        this.remove = this.remove.bind(this);
        this.jwt = JSON.parse(window.localStorage.getItem("jwt"));
    }

    componentDidMount() {
        fetch('/api/v1/vets/specialties', {
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
            },
        })
            .then(response => response.json())
            .then(data => this.setState({ specialties: data }));
    }

    async remove(id) {
        await fetch(`/api/v1/vets/specialties/${id}`, {
            method: 'DELETE',
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(() => {
            let updatedSpecialties = [...this.state.specialties].filter(i => i.id !== id);
            this.setState({ specialties: updatedSpecialties });
        });
    }

    render() {
        const { specialties, isLoading } = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        const specialtiesList = specialties.map(s => {

            return <tr key={s.id}>
                <td style={{ whiteSpace: 'nowrap' }}>{s.name}</td>
                <td>
                    <ButtonGroup>
                        <Button size="sm" color="primary" tag={Link} to={"/vets/specialties/" + s.id}>Edit</Button>
                        <Button size="sm" color="danger" onClick={() => this.remove(s.id)}>Delete</Button>
                    </ButtonGroup>
                </td>
            </tr>
        });

        return (
            <div>
                <Container style={{ marginTop: "15px" }} fluid>
                    <h1 className='text-center'>Vets</h1>
                    <Button color="success" tag={Link} to="/vets/specialties/new">Add Specialty</Button>
                    {" "}
                    <Button color="info" tag={Link} to="/vets">Back</Button>
                    <Table className="mt-4">
                        <thead>
                            <tr>
                                <th width="20%">Name</th>
                                <th width="20%">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {specialtiesList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }

}

export default SpecialtiesList;

