import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';

class OwnerConsultationEdit extends Component {

    emptyItem = {
        id: null,
        title: '',
        status: 'PENDING'
    };

    constructor(props) {
        super(props);
        this.state = {
            consultation: this.emptyItem,
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
            const consultation = await (await fetch(`/api/v1/consultations/${this.id}`, {
                headers: {
                    "Authorization": `Bearer ${this.jwt}`,
                },
            })).json();
            if (consultation.message) this.setState({ message: consultation.message });
            else this.setState({ consultation: consultation });
        }
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let consultation = { ...this.state.consultation };
        consultation[name] = value;
        this.setState({ consultation });
    }

    async handleSubmit(event) {
        event.preventDefault();
        const { consultation, } = this.state;

        const response = await (await fetch('/api/v1/consultations' + (consultation.id ? '/' + this.id : ''), {
            method: consultation.id ? 'PUT' : 'POST',
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(consultation),
        })).json();
        if (response.message) this.setState({ message: response.message })
        else window.location.href = '/consultations';
    }

    render() {
        const { consultation } = this.state;
        const title = <h2 className="text-center">{consultation.id ? 'Edit Consultation' : 'Add Consultation'}</h2>;

        if (this.state.message) return <h2 className="text-center">{this.state.message}</h2>

        return <div>
            {/* <AppNavbar /> */}
            <Container style={{ marginTop: "15px" }}>
                {title}
                <Form onSubmit={this.handleSubmit}>
                    <FormGroup>
                        <Label for="title">Title</Label>
                        <Input type="text" required name="title" id="title" value={consultation.title || ''}
                            onChange={this.handleChange} />
                    </FormGroup>
                    <FormGroup>
                        <Button color="primary" type="submit">Save</Button>{' '}
                        <Button color="secondary" tag={Link} to="/consultations">Cancel</Button>
                    </FormGroup>
                </Form>
            </Container>
        </div>
    }
}
export default OwnerConsultationEdit;