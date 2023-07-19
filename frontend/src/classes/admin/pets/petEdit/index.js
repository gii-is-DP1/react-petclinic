import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';

class PetEdit extends Component {

    emptyItem = {
        id: null,
        name: '',
        birthDate: '',
        type: null,
        owner: null,
    };

    constructor(props) {
        super(props);
        this.state = {
            pet: this.emptyItem,
            types: [],
            owners: [],
            message: null,
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.jwt = JSON.parse(window.localStorage.getItem("jwt"));
        let pathArray = window.location.pathname.split('/');
        this.id = pathArray[2];
    }

    async componentDidMount() {
        if (this.id !== "new") {
            const pet = await (await fetch(`/api/v1/pets/${this.id}`, {
                headers: {
                    "Authorization": `Bearer ${this.jwt}`,
                },
            })).json();
            if (pet.mesagge) this.setState({ message: pet.message });
            else this.setState({ pet: pet });
        }
        if (!this.state.message) {
            const types = await (await fetch(`/api/v1/pets/types`, {
                headers: {
                    "Authorization": `Bearer ${this.jwt}`,
                },
            })).json();
            if (types.mesagge) this.setState({ message: types.message });
            else this.setState({ types: types });
        }
        if (!this.state.message) {
            const owners = await (await fetch(`/api/v1/owners`, {
                headers: {
                    "Authorization": `Bearer ${this.jwt}`,
                },
            })).json();
            if (owners.mesagge) this.setState({ message: owners.message });
            else this.setState({ owners: owners });
        }
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let pet = { ...this.state.pet };
        if (name === "type") {
            pet.type = this.state.types.filter((type) => type.id === Number(value))[0];
        } else if (name === "owner") {
            pet.owner = this.state.owners.filter((owner) => owner.id === Number(value))[0];
        }
        else pet[name] = value;
        this.setState({ pet });
    }

    async handleSubmit(event) {
        event.preventDefault();
        const { pet, } = this.state;

        const response = await (await fetch('/api/v1/pets' + (pet.id ? '/' + this.id : ''), {
            method: pet.id ? 'PUT' : 'POST',
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(pet),
        })).json();
        if (response.message) this.setState({ message: response.message })
        else window.location.href = '/pets';
    }

    render() {
        const { pet, types, owners } = this.state;
        const title = <h2>{pet.id ? 'Edit Pet' : 'Add Pet'}</h2>;

        const typeOptions = types.map(type => <option key={type.id} value={type.id}>{type.name}</option>);
        const ownerOptions = owners.map(owner => <option key={owner.id} value={owner.id}>{owner.user.username}</option>);

        if (this.state.message) return <h2 className="text-center">{this.state.message}</h2>

        return <div>
            <Container>
                {title}
                <Form onSubmit={this.handleSubmit}>
                    <FormGroup>
                        <Label for="name">Name</Label>
                        <Input type="text" required name="name" id="name" value={pet.name || ''}
                            onChange={this.handleChange} />
                    </FormGroup>
                    <FormGroup>
                        <Label for="birthDate">Birth Date</Label>
                        <Input type="date" name="birthDate" id="birthDate" value={pet.birthDate || ''}
                            onChange={this.handleChange} />
                    </FormGroup>
                    <FormGroup>
                        <Label for="type">Type</Label>
                        <Input type="select" required name="type" id="type" value={pet.type?.id}
                            onChange={this.handleChange}>
                            <option value="">None</option>
                            {typeOptions}
                        </Input>
                    </FormGroup>
                    <FormGroup>
                        <Label for="owner">Owner</Label>
                        {pet.id ?
                            <Input type="select" disabled name="owner" id="owner" value={pet.owner?.id || ""}
                                onChange={this.handleChange} >
                                <option value="">None</option>
                                {ownerOptions}
                            </Input> :
                            <Input type="select" required name="owner" id="owner" value={pet.owner?.id || ""}
                                onChange={this.handleChange} >
                                <option value="">None</option>
                                {ownerOptions}
                            </Input>}
                    </FormGroup>
                    <FormGroup>
                        <Button color="primary" type="submit">Save</Button>{' '}
                        <Button color="secondary" tag={Link} to="/pets">Cancel</Button>
                    </FormGroup>
                </Form>
            </Container>
        </div>
    }
}
export default PetEdit;