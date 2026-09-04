import React, { Component } from 'react';
import { Button, ButtonGroup, Container, Table } from 'reactstrap';
// import AppNavbar from './AppNavbar';
import { Link } from 'react-router-dom';

class VetList extends Component {

    constructor(props) {
        super(props);
        this.state = { vets: [] };
        this.remove = this.remove.bind(this);
        this.jwt = JSON.parse(window.localStorage.getItem("jwt"));
    }

    componentDidMount() {
        fetch('/api/v1/vets', {
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
            },
        })
            .then(response => response.json())
            .then(data => this.setState({ vets: data }));
    }

    async remove(id) {
        await fetch(`/api/v1/vets/${id}`, {
            method: 'DELETE',
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        }).then(() => {
            let updatedVets = [...this.state.vets].filter(i => i.id !== id);
            this.setState({ vets: updatedVets });
        });
    }

    render() {
        const { vets, isLoading } = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        const vetList = vets.map(vet => {

            let specialtiesAux = vet.specialties.map(s => s.name).toString().replace(",", ", ");;


            return <tr key={vet.id}>
                <td style={{ whiteSpace: 'nowrap' }}>{vet.firstName} {vet.lastName}</td>
                <td >{vet.city}</td>
                <td style={{ whiteSpace: 'break-spaces' }}>{specialtiesAux}</td>
                <td >{vet.user.username}</td>
                <td>
                    <ButtonGroup>
                        <Button size="sm" color="primary" tag={Link} to={"/vets/" + vet.id}>Edit</Button>
                        <Button size="sm" color="danger" onClick={() => this.remove(vet.id)}>Delete</Button>
                    </ButtonGroup>
                </td>
            </tr>
        });

        return (
            <div>
                <Container style={{ marginTop: "15px" }} fluid>
                    <h1 className='text-center'>Vets</h1>
                    <div className="float-right">
                        <Button color="success" tag={Link} to="/vets/new">Add Vet</Button>{" "}
                        <Button color="info" tag={Link} to="/vets/specialties">Specialties</Button>
                    </div>
                    <Table className="mt-4">
                        <thead>
                            <tr>
                                <th width="20%">Name</th>
                                <th width="20%">City</th>
                                <th width="20%">Specialties</th>
                                <th width="20%">User</th>
                                <th width="20%">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {vetList}
                        </tbody>
                    </Table>
                </Container>
            </div>
        );
    }

}

export default VetList;

