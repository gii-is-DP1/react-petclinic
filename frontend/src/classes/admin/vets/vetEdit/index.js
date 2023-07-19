import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Button, Col, Container, Form, FormGroup, Input, Label, Row } from 'reactstrap';

class VetEdit extends Component {

    emptyVet = {
        id: '',
        firstName: '',
        lastName: '',
        specialties: [],
        user: { id: 1 },
    };

    constructor(props) {
        super(props);
        this.state = {
            vet: this.emptyVet,
            allSpecialties: [],
            // users: [],
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSpecialtyChange = this.handleSpecialtyChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.jwt = JSON.parse(window.localStorage.getItem("jwt"));
        let pathArray = window.location.pathname.split('/');
        this.id = pathArray[2];
    }

    async componentDidMount() {
        if (this.id !== 'new') {
            const vet = await (await fetch(`/api/v1/vets/${this.id}`, {
                headers: {
                    "Authorization": `Bearer ${this.jwt}`,
                },
            })).json();
            this.setState({ vet: vet });
        }

        const specialtiesList = await (await fetch(`/api/v1/vets/specialties`, {
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
            },
        })).json();
        this.setState({ allSpecialties: specialtiesList });

        const users = await (await fetch(`/api/v1/users?auth=VET`, {
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
            },
        })).json();
        this.setState({ users: users });
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let vet = { ...this.state.vet };
        if (name === "user") {
            vet.user.id = value;
        } else vet[name] = value;
        this.setState({ vet });
    }

    handleSpecialtyChange(event) {
        const target = event.target;
        const checked = target.checked;
        const name = target.name;
        const allSpecialties = { ...this.state.allSpecialties }
        let vet = { ...this.state.vet };
        let selectedSpecialties = vet.specialties;
        for (let i = 0; i < Object.keys(allSpecialties).length; i++) {
            if (allSpecialties[i].name === name) {
                if (!checked) selectedSpecialties = selectedSpecialties.filter(specialty => specialty.name !== name);
                else selectedSpecialties.push(allSpecialties[i]);
            }
        }
        vet.specialties = selectedSpecialties;
        this.setState({ vet });
    }

    async handleSubmit(event) {
        event.preventDefault();
        const { vet } = this.state;

        await fetch('/api/v1/vets' + (vet.id ? '/' + vet.id : ''), {
            method: (vet.id) ? 'PUT' : 'POST',
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(vet),
        });
        window.location.href = '/vets';
    }

    render() {
        const { vet, allSpecialties } = this.state;
        const title = <h2>{vet.id ? 'Edit Vet' : 'Add Vet'}</h2>;

        const selectedSpecialties = vet.specialties.map(specialty => specialty.name);
        const specialties = allSpecialties.map(specialty => {
            if (selectedSpecialties.includes(specialty.name)) {
                return (<FormGroup key={specialty.name}>
                    <Input type="checkbox" name={specialty.name} onChange={this.handleSpecialtyChange} checked />
                    <Label for={specialty.name}> {specialty.name}</Label>
                </FormGroup>);
            } else {
                return (<FormGroup key={specialty.name}>
                    <Input type="checkbox" key={specialty.name} name={specialty.name} onChange={this.handleSpecialtyChange} />
                    <Label for={specialty.name}> {specialty.name}</Label>
                </FormGroup>);
            }
        });

        // const userOptions = users.map(user => <option key={user.id} value={user.id}>{user.username}</option>);


        return (
            <div>
                <Container className="d-flex ">
                    <Col md={4}>
                        {title}
                        <Form onSubmit={this.handleSubmit}>
                            <FormGroup>
                                <Label for="firstName">First Name</Label>
                                <Input type="text" name="firstName" id="firstName" value={vet.firstName || ''}
                                    onChange={this.handleChange} autoComplete="firstName" />
                            </FormGroup>
                            <FormGroup>
                                <Label for="lastName">Last Name</Label>
                                <Input type="text" name="lastName" id="lastName" value={vet.lastName || ''}
                                    onChange={this.handleChange} autoComplete="lastName" />
                            </FormGroup>
                            <FormGroup>
                                <Label for="city">City</Label>
                                <Input type="text" name="city" id="city" value={vet.city || ''}
                                    onChange={this.handleChange} autoComplete="city" />
                            </FormGroup>
                            <Label for="specialties">Specialties</Label>
                            <Row className="row-cols-lg-auto g-3 align-items-center">
                                {specialties}
                            </Row>
                            {vet.id ?
                                <FormGroup>
                                    <Label for="user">User</Label>
                                    <p>{vet.user.username || ''}</p>
                                </FormGroup> : <></>
                            }
                            <br></br>
                            <FormGroup>
                                <Button color="primary" type="submit">Save</Button>{' '}
                                <Button color="secondary" tag={Link} to="/vets">Cancel</Button>
                            </FormGroup>
                        </Form>
                    </Col>
                </Container>
            </div >
        )
    }
}
export default VetEdit;