import React, { Component } from "react";
import "../../static/css/auth/authButton.css";
import {
  Form,
  Button,
  Container,
  FormGroup,
  Input,
  Label,
  Col,
  Alert,
} from "reactstrap";
import tokenService from "../../services/token.service";
import FormGenerator from "../../components/formGenerator/formGenerator";
import { loginFormInputs } from "./form/loginFormInputs";

class Login extends Component {
  constructor(props) {
    super(props);
    this.state = {
      navigation: props.navigation ? props.navigation : false,
      children: props.children ? props.children : null,
    };
    this.loginFormRef = React.createRef();
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    this.setState({
      [name]: value,
    });
  }

  async handleSubmit({ values }) {

    const reqBody = values;

    await fetch("/api/v1/auth/signin", {
      headers: { "Content-Type": "application/json" },
      method: "POST",
      body: JSON.stringify(reqBody),
    })
      .then(function (response) {
        if (response.status === 200) return response.json();
        else return Promise.reject("Invalid login attempt");
      })
      .then(function (data) {
        tokenService.setUser(data);
        tokenService.updateLocalAccessToken(data.token);
      })
      .catch((message) => {
        alert(message);
      });
    if (this.state.navigation === true) {
      return window.location.reload();
    } else window.location.href = "/dashboard";
  }

  render() {
    return (
      <div className="auth-page-container">
        {this.state.message ? (
          <Alert color="primary">{this.state.message}</Alert>
        ) : (
          <></>
        )}

        <h1>Login</h1>

        <div className="auth-form-container">
          <FormGenerator
            ref={this.loginFormRef}
            inputs={loginFormInputs}
            onSubmit={this.handleSubmit}
            numberOfColumns={1}
            listenEnterKey
            buttonText="Login"
            buttonClassName="auth-button"
          />
        </div>
      </div>
    );
  }
}

export default Login;
