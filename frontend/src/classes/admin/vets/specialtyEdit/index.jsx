import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Button, Col, Container, Form, FormGroup, Input, Label } from 'reactstrap';

class SpecialtyEdit extends Component {

    emptySpecialty = {
        id: '',
        name: '',
    };

    constructor(props) {
        super(props);
        this.state = {
            specialty: this.emptySpecialty,
            message: null
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.jwt = JSON.parse(window.localStorage.getItem("jwt"));
        let pathArray = window.location.pathname.split('/');
        this.id = pathArray[3];
    }

    async componentDidMount() {
        if (this.id !== 'new') {
            const specialty = await (await fetch(`/api/v1/vets/specialties/${this.id}`, {
                headers: {
                    "Authorization": `Bearer ${this.jwt}`,
                },
            })).json();
            specialty.message ? this.setState({ message: specialty.message }) :
                this.setState({ specialty: specialty });
        }
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let specialty = { ...this.state.specialty };
        specialty[name] = value;
        this.setState({ specialty });
    }

    async handleSubmit(event) {
        event.preventDefault();
        const { specialty } = this.state;

        await fetch('/api/v1/vets/specialties' + (specialty.id ? '/' + specialty.id : ''), {
            method: (specialty.id) ? 'PUT' : 'POST',
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(specialty),
        });
        window.location.href = '/vets/specialties';
    }

    render() {
        const { specialty } = this.state;
        const title = <h2>{specialty.id ? 'Edit Specialty' : 'Add Specialty'}</h2>;
        return (
            <div>
                <Container className="d-flex ">
                    <Col md={4}>
                        {title}
                        <Form onSubmit={this.handleSubmit}>
                            <FormGroup>
                                <Label for="name">Name</Label>
                                <Input type="text" name="name" id="name" value={specialty.name || ''}
                                    onChange={this.handleChange} />
                            </FormGroup>
                            <FormGroup style={{ marginTop: "10px" }}>
                                <Button color="primary" type="submit">Save</Button>{' '}
                                <Button color="secondary" tag={Link} to="/vets/specialties">Cancel</Button>
                            </FormGroup>
                        </Form>
                    </Col>
                </Container>
            </div >
        )
    }
}
export default SpecialtyEdit;