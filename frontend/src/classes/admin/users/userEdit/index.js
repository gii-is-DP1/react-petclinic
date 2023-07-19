import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label } from 'reactstrap';

class UserEdit extends Component {

    emptyUser = {
        id: null,
        username: '',
        password: '',
        authority: { id: 1 }
    };

    constructor(props) {
        super(props);
        this.state = {
            user: this.emptyUser,
            auths: [],
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.jwt = JSON.parse(window.localStorage.getItem("jwt"));
        let pathArray = window.location.pathname.split("/");
        this.id = pathArray[2];
    }

    async componentDidMount() {
        console.log(this.id)
        if (this.id !== "new") {
            const user = await (await fetch(`/api/v1/users/${this.id}`, {
                headers: {
                    "Authorization": `Bearer ${this.jwt}`,
                },
            })).json();
            this.setState({ user: user });
        }
        const auths = await (await fetch(`/api/v1/users/authorities`, {
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
            },
        })).json();
        this.setState({ auths: auths });
    }

    handleChange(event) {
        const target = event.target;
        const value = target.value;
        const name = target.name;
        let user = { ...this.state.user };
        if (name === "authority") {
            user.authority.id = value;
        } else user[name] = value;
        this.setState({ user });
    }

    async handleSubmit(event) {
        event.preventDefault();
        const { user } = this.state;

        await fetch('/api/v1/users' + (user.id ? '/' + this.id : ''), {
            method: user.id ? 'PUT' : 'POST',
            headers: {
                "Authorization": `Bearer ${this.jwt}`,
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(user),
        });
        window.location.href = '/users';
    }

    render() {
        const { user, auths } = this.state;
        const title = <h2>{user.id ? 'Edit User' : 'Add User'}</h2>;

        const authOptions = auths.map(auth => <option key={auth.id} value={auth.id}>{auth.authority}</option>);

        return <div>
            {/* <AppNavbar /> */}
            <Container>
                {title}
                <Form onSubmit={this.handleSubmit}>
                    <FormGroup>
                        <Label for="username">Username</Label>
                        <Input type="text" name="username" id="username" value={user.username || ''}
                            onChange={this.handleChange} autoComplete="username" />
                    </FormGroup>
                    <FormGroup>
                        <Label for="lastName">Password</Label>
                        <Input type="password" name="password" id="password" value={user.password || ''}
                            onChange={this.handleChange} autoComplete="password" />
                    </FormGroup>
                    <Label for="authority">Authority</Label>
                    {user.id ?
                        <p>{user.authority.authority || ''}</p> :
                        <Input type="select" name="authority" id="authority" value={user.authority.id || 1}
                            onChange={this.handleChange} autoComplete="authority">
                            {authOptions}
                        </Input>}
                    <FormGroup>
                        <Button color="primary" type="submit">Save</Button>{' '}
                        <Button color="secondary" tag={Link} to="/users">Cancel</Button>
                    </FormGroup>
                </Form>
            </Container>
        </div>
    }
}
export default UserEdit;