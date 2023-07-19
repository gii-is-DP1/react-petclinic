import React, { Component } from "react";
import { Button, ButtonGroup, Container, Table } from "reactstrap";
// import AppNavbar from "../AppNavbar";
import { Link } from "react-router-dom";

class PetList extends Component {
    constructor(props) {
        super(props);
        this.state = { pets: [] };
        this.remove = this.remove.bind(this);
        this.jwt = JSON.parse(window.localStorage.getItem("jwt"));
    }

    componentDidMount() {
        fetch("/api/v1/pets", {
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
                "Content-Type": "application/json",
            },
        }).then((response) => response.json())
            .then((data) => this.setState({ pets: data }));
    }

    async remove(id) {
        await fetch(`/api/v1/pets/${id}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
                Accept: "application/json",
                "Content-Type": "application/json",
            },
        }).then((response) => {
            if (response.status === 200) {
                let updatedPets = [...this.state.pets].filter((i) => i.id !== id);
                this.setState({ pets: updatedPets });
            }
            return response.json();
        }).then(function (data) {
            alert(data.message);
        });
    }

    render() {
        const { pets, isLoading } = this.state;

        if (isLoading) {
            return <p>Loading...</p>;
        }

        const petList = pets.map((pet) => {
            return (
                <tr key={pet.id}>
                    <td>{pet.name}</td>
                    <td>{pet.birthDate}</td>
                    <td>{pet.type?.name}</td>
                    <td>{pet.owner.user.username}</td>
                    <td>
                        <Button size="sm" color="info" tag={Link}
                            to={`/pets/${pet.id}/visits`}>
                            Visits
                        </Button>
                    </td>
                    <td>
                        <ButtonGroup>
                            <Button size="sm" color="primary" tag={Link}
                                to={"/pets/" + pet.id}>
                                Edit
                            </Button>
                            <Button size="sm" color="danger" onClick={() => this.remove(pet.id)}>
                                Delete
                            </Button>
                        </ButtonGroup>
                    </td>
                </tr>
            );
        });

        return (
            <div>
                {/* <AppNavbar /> */}
                <Container style={{ marginTop: "15px" }} fluid>
                    <h1 className="text-center">Pets</h1>
                    <Button color="success" tag={Link} to="/pets/new">
                        Add Pet
                    </Button>
                    <Table className="mt-4">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Birth Date</th>
                                <th>Type</th>
                                <th>Owner</th>
                                <th>Visits</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>{petList}</tbody>
                    </Table>
                </Container>
            </div>
        );
    }
}

export default PetList;
