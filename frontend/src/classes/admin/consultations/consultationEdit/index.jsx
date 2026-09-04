import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';

class ConsultationEdit extends Component {

    emptyItem = {
        id: null,
        title: '',
        status: null,
        owner: null,
    };

    constructor(props) {
        super(props);
        this.state = {
            consultation: this.emptyItem,
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
            const consultation = await (await fetch(`/api/v1/consultations/${this.id}`, {
                headers: {
                    "Authorization": `Bearer ${this.jwt}`,
                },
            })).json();
            if (consultation.mesagge) this.setState({ message: consultation.message });
            else this.setState({ consultation: consultation });
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
        let consultation = { ...this.state.consultation };
        if (name === "owner") {
            consultation.owner = this.state.owners.filter((owner) => owner.id === Number(value))[0];
        }
        else consultation[name] = value;
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
        const { consultation, owners } = this.state;
        const title = <h2>{consultation.id ? 'Edit Consultation' : 'Add Consultation'}</h2>;

        const ownerOptions = owners.map(owner => <option key={owner.id} value={owner.id}>{owner.user.username}</option>);

        if (this.state.message) return <h2 className="text-center">{this.state.message}</h2>

        return <div>
            {/* <AppNavbar /> */}
            <Container>
                {title}
                <Form onSubmit={this.handleSubmit}>
                    <FormGroup>
                        <Label for="title">Title</Label>
                        <Input type="text" required name="title" id="title" value={consultation.title || ''}
                            onChange={this.handleChange} />
                    </FormGroup>
                    <FormGroup>
                        <Label for="status">Status</Label>
                        <Input type="select" required name="status" id="status" value={consultation.status || ""}
                            onChange={this.handleChange}>
                            <option value="">None</option>
                            <option value="PENDING">PENDING</option>
                            <option value="ANSWERED">ANSWERED</option>
                            <option value="CLOSED">CLOSED</option>
                        </Input>
                    </FormGroup>
                    <FormGroup>
                        <Label for="owner">Owner</Label>
                        {consultation.id ?
                            <p>{consultation.owner.user?.username}</p> :
                            <Input type="select" required name="owner" id="owner" value={consultation.owner?.id || ""}
                                onChange={this.handleChange} >
                                <option value="">None</option>
                                {ownerOptions}
                            </Input>}
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
export default ConsultationEdit;